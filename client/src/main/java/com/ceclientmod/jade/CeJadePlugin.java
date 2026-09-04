package com.ceclientmod.jade;

import com.ceclientmod.CraftEngineClientModInit;
import com.ceclientmod.cache.CeItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.JadeUI;

/**
 * Instantiated by Jade via the "jade" fabric.mod.json entrypoint (Fabric's own entrypoint mechanism -
 * explicit class name declared there, no classpath scanning) - never referenced from our own client
 * entrypoint, so the game still launches fine without Jade installed.
 *
 * CraftEngine blocks and furniture arrive as vanilla block states/entities. The bridge replaces Jade's
 * icon with the exact client-bound source item and also replaces disguised blocks' titles with that
 * item's client-resolved hover name.
 */
public final class CeJadePlugin implements IWailaPlugin {

    private static final Identifier UID = Identifier.fromNamespaceAndPath("ceclientmod", "jade_plugin");

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        CeBlockComponentProvider blocks = new CeBlockComponentProvider();
        registration.registerBlockComponent(blocks, Block.class);
        registration.registerBlockIcon(blocks, Block.class);
        registration.registerEntityIcon(new CeFurnitureIconProvider(), Entity.class);
        registration.addTooltipCollectedCallback(Integer.MAX_VALUE, (box, accessor) -> {
            if (!(accessor instanceof BlockAccessor blockAccessor)) return;
            BlockState state = blockAccessor.getBlockState();
            // Prefer the full item cache (carries the real display name on the JEI/items channel),
            // falling back to the block-icon item, whose appearance may only carry the model and not
            // the custom name. This guarantees the CraftEngine name replaces the vanilla disguise name.
            Component name = CraftEngineClientModInit.blocks().ceIdFor(state)
                    .map(CraftEngineClientModInit.items()::byId)
                    .flatMap(opt -> opt.map(CeItem::stack))
                    .map(ItemStack::getHoverName)
                    .orElseGet(() -> CraftEngineClientModInit.blockIcons().iconFor(state)
                            .map(ItemStack::getHoverName)
                            .orElse(null));
            if (name != null) {
                ITooltip tooltip = box.getTooltip();
                Component title = IThemeHelper.get().title(name);
                // Jade 19.0.3's ObjectNameProvider adds the block name under CORE_OBJECT_NAME. Replace it
                // with the CraftEngine name, then re-layout: BoxElementImpl builds its layout/renderables
                // at CONSTRUCTION time (before this collected callback runs), so without updateSize() the
                // screen keeps showing the old vanilla name.
                boolean ok = tooltip.replace(JadeIds.CORE_OBJECT_NAME, title);
                if (!ok) {
                    tooltip.replace(null, title);
                }
                box.updateSize();
            }
        });
    }

    private static final class CeBlockComponentProvider implements IBlockComponentProvider {

        @Override
        public Element getIcon(BlockAccessor accessor, IPluginConfig config, Element currentIcon) {
            return CraftEngineClientModInit.blockIcons().iconFor(accessor.getBlockState())
                    .<Element>map(JadeUI::item)
                    .orElse(currentIcon);
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            // Intentionally empty: the CraftEngine display name is set via the tooltip-collected
            // callback (replace CORE_OBJECT_NAME), so we don't append a redundant "CraftEngine: <id>"
            // identity line.
        }

        @Override
        public Identifier getUid() {
            return UID;
        }
    }

    private static final class CeFurnitureIconProvider implements IEntityComponentProvider {

        @Override
        public Element getIcon(EntityAccessor accessor, IPluginConfig config, Element currentIcon) {
            return CraftEngineClientModInit.furnitureIcons().iconFor(accessor.getEntity())
                    .<Element>map(JadeUI::item)
                    .orElse(currentIcon);
        }

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        }

        @Override
        public Identifier getUid() {
            return UID;
        }
    }
}
