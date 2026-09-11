package com.ceclientmod.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Channel identities shared with the server-side CraftEngineClientBridge plugin (see its BridgeChannels). */
public final class BridgeChannels {

    public static final CustomPacketPayload.Type<ChunkPayload> ITEMS = id("items");
    public static final CustomPacketPayload.Type<ChunkPayload> BLOCKS = id("blocks");
    public static final CustomPacketPayload.Type<ChunkPayload> BREWING = id("brewing");
    public static final CustomPacketPayload.Type<ChunkPayload> CRAFTING_DISPLAY = id("crafting_display");
    public static final CustomPacketPayload.Type<ChunkPayload> SMITHING_DISPLAY = id("smithing_display");
    public static final CustomPacketPayload.Type<ChunkPayload> BLOCK_ICONS = id("block_icons");
    public static final CustomPacketPayload.Type<ChunkPayload> FURNITURE_ICON = id("furniture_icon");
    public static final CustomPacketPayload.Type<ChunkPayload> BLOCK_INFO = id("block_info");

    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> id(String path) {
        return new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("ceclientbridge", path));
    }

    private BridgeChannels() {
    }
}
