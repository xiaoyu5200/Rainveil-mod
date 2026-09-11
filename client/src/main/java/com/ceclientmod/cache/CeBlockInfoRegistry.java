package com.ceclientmod.cache;

import com.ceclientbridge.protocol.JadeIconProtocol;
import com.ceclientmod.net.BlockProbePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * On-demand, authoritative CraftEngine block identity for a given position. The disguise blockstate the
 * client sees is ambiguous when two custom blocks share it, so instead of trusting the state -&gt; id map
 * we ask the server which CraftEngine block is actually at the position. Requests and responses are
 * correlated so stale replies are dropped, and negative results are remembered so we don't re-ask.
 */
public final class CeBlockInfoRegistry {
    private static final long REQUEST_TIMEOUT_NANOS = 2_000_000_000L;

    /** A resolved CraftEngine block: its id and the client-bound item that represents it. */
    public record Info(String ceId, ItemStack stack) {
    }

    private final Map<Long, Info> byPos = new HashMap<>();
    private final Map<Integer, Long> posByRequest = new HashMap<>();
    private final Map<Long, Long> pending = new HashMap<>();
    private final Set<Long> negative = new HashSet<>();
    private int nextRequestId;

    public Optional<Info> infoFor(BlockPos pos) {
        long key = pos.asLong();
        Info info = byPos.get(key);
        if (info != null) return Optional.of(info);
        if (negative.contains(key)) return Optional.empty();
        long now = System.nanoTime();
        Long requestedAt = pending.get(key);
        if (requestedAt != null && now - requestedAt < REQUEST_TIMEOUT_NANOS) return Optional.empty();
        if (requestedAt != null) {
            pending.remove(key);
            posByRequest.entrySet().removeIf(entry -> entry.getValue() == key);
        }
        if (!ClientPlayNetworking.canSend(BlockProbePayload.TYPE)) return Optional.empty();

        int requestId = nextRequestId++ & Integer.MAX_VALUE;
        posByRequest.put(requestId, key);
        pending.put(key, now);
        ClientPlayNetworking.send(new BlockProbePayload(requestId, key));
        return Optional.empty();
    }

    public void accept(byte[] payload) throws IOException {
        JadeIconProtocol.BlockInfo response = JadeIconProtocol.decodeBlockInfo(payload);
        Long expectedKey = posByRequest.remove(response.requestId());
        if (expectedKey == null || expectedKey != response.blockPos()) return;
        pending.remove(expectedKey);
        if (response.appearance().length == 0) {
            negative.add(response.blockPos());
            return;
        }
        ItemStack stack = CeItemRegistry.readAppearance(
                new DataInputStream(new ByteArrayInputStream(response.appearance())));
        byPos.put(response.blockPos(), new Info(response.ceId(), stack));
    }

    public void clear() {
        byPos.clear();
        posByRequest.clear();
        pending.clear();
        negative.clear();
    }
}
