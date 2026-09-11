package com.ceclientmod.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * C2S request for the CraftEngine block at a specific position. The client only sees the disguise
 * blockstate (which is ambiguous when two custom blocks share it), so the server answers from its own
 * positional block data instead.
 */
public record BlockProbePayload(int requestId, long blockPos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BlockProbePayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("ceclientbridge", "block_probe"));
    public static final StreamCodec<FriendlyByteBuf, BlockProbePayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeVarInt(payload.requestId());
                buf.writeLong(payload.blockPos());
            },
            buf -> new BlockProbePayload(buf.readVarInt(), buf.readLong())
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
