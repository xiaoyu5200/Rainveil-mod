package com.ceclientmod.net;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** C2S request for the CraftEngine furniture represented by the entity currently targeted by Jade. */
public record FurnitureProbePayload(int requestId, int entityId) implements CustomPayload {

    public static final CustomPayload.Id<FurnitureProbePayload> TYPE =
            new CustomPayload.Id<>(Identifier.of("ceclientbridge", "furniture_probe"));
    public static final PacketCodec<RegistryByteBuf, FurnitureProbePayload> CODEC = PacketCodec.ofStatic(
            (buf, payload) -> {
                buf.writeVarInt(payload.requestId());
                buf.writeVarInt(payload.entityId());
            },
            buf -> new FurnitureProbePayload(buf.readVarInt(), buf.readVarInt())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}
