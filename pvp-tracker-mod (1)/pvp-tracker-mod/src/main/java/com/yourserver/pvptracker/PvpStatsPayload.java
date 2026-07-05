package com.yourserver.pvptracker;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Sent from the server to each client once per HUD update tick. Carries
 * that player's own counts plus the nearest-enemy counts (if any enemy was
 * found within range).
 */
public record PvpStatsPayload(
        boolean hasEnemy,
        String enemyName,
        int distance,
        int selfPearls,
        int selfWind,
        int enemyPearls,
        int enemyWind,
        int enemyPotions,
        int enemyTotems,
        int enemyXpBottles,
        int helmetDurability,
        int chestDurability,
        int legsDurability,
        int bootsDurability,
        int maceDurability
) implements CustomPayload {

    public static final CustomPayload.Id<PvpStatsPayload> ID =
            new CustomPayload.Id<>(Identifier.of("pvptracker", "stats"));

    public static final PacketCodec<PacketByteBuf, PvpStatsPayload> CODEC =
            PacketCodec.of(PvpStatsPayload::write, PvpStatsPayload::read);

    private void write(PacketByteBuf buf) {
        buf.writeBoolean(hasEnemy);
        buf.writeString(enemyName);
        buf.writeVarInt(distance);
        buf.writeVarInt(selfPearls);
        buf.writeVarInt(selfWind);
        buf.writeVarInt(enemyPearls);
        buf.writeVarInt(enemyWind);
        buf.writeVarInt(enemyPotions);
        buf.writeVarInt(enemyTotems);
        buf.writeVarInt(enemyXpBottles);
        buf.writeVarInt(helmetDurability);
        buf.writeVarInt(chestDurability);
        buf.writeVarInt(legsDurability);
        buf.writeVarInt(bootsDurability);
        buf.writeVarInt(maceDurability);
    }

    private static PvpStatsPayload read(PacketByteBuf buf) {
        boolean hasEnemy = buf.readBoolean();
        String enemyName = buf.readString();
        int distance = buf.readVarInt();
        int selfPearls = buf.readVarInt();
        int selfWind = buf.readVarInt();
        int enemyPearls = buf.readVarInt();
        int enemyWind = buf.readVarInt();
        int enemyPotions = buf.readVarInt();
        int enemyTotems = buf.readVarInt();
        int enemyXpBottles = buf.readVarInt();
        int helmetDurability = buf.readVarInt();
        int chestDurability = buf.readVarInt();
        int legsDurability = buf.readVarInt();
        int bootsDurability = buf.readVarInt();
        int maceDurability = buf.readVarInt();
        return new PvpStatsPayload(hasEnemy, enemyName, distance, selfPearls, selfWind,
                enemyPearls, enemyWind, enemyPotions, enemyTotems, enemyXpBottles,
                helmetDurability, chestDurability, legsDurability, bootsDurability, maceDurability);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    /** Registers this payload type. Must be called from common (main) init, which runs on both sides. */
    public static void register() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
    }
}
