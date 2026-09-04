package com.ceclientmod.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * One raw protocol frame carried by one of the existing bridge channels. The server writes the complete
 * generation/chunk header before sending this payload; the client validates it in {@link ChunkAssembler}.
 */
public record ChunkPayload(CustomPacketPayload.Type<ChunkPayload> payloadId, byte[] data) implements CustomPacketPayload {

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return payloadId;
    }

    public static StreamCodec<FriendlyByteBuf, ChunkPayload> codecFor(CustomPacketPayload.Type<ChunkPayload> id) {
        return StreamCodec.of(
                (buf, chunk) -> buf.writeByteArray(chunk.data()),
                buf -> new ChunkPayload(id, buf.readByteArray())
        );
    }
}
