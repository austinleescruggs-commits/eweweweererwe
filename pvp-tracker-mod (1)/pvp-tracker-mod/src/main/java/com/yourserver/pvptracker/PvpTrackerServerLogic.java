package com.yourserver.pvptracker;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.util.List;

public class PvpTrackerServerLogic {

    private final PvpTrackerConfig config;
    private int tickCounter = 0;

    public PvpTrackerServerLogic(PvpTrackerConfig config) {
        this.config = config;
    }

    /** Call this every server tick; it internally throttles to config.updateIntervalTicks. */
    public void onServerTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter < config.updateIntervalTicks) return;
        tickCounter = 0;

        for (ServerPlayerEntity self : server.getPlayerManager().getPlayerList()) {
            PvpStatsPayload payload = buildPayload(self);
            ServerPlayNetworking.send(self, payload);
        }
    }

    private PvpStatsPayload buildPayload(ServerPlayerEntity self) {
        int selfPearls = StatsHelper.countItem(self, Items.ENDER_PEARL);
        int selfWind = StatsHelper.countItem(self, Items.WIND_CHARGE);

        ServerPlayerEntity nearest = findNearestEnemy(self);

        if (nearest == null) {
            return new PvpStatsPayload(false, "", 0, selfPearls, selfWind,
                    0, 0, 0, 0, 0, -1, -1, -1, -1, -1);
        }

        int distance = (int) self.getPos().distanceTo(nearest.getPos());

        int enemyPearls = StatsHelper.countItem(nearest, Items.ENDER_PEARL);
        int enemyWind = StatsHelper.countItem(nearest, Items.WIND_CHARGE);
        int enemyPotions = StatsHelper.countPotions(nearest);
        int enemyTotems = StatsHelper.countItem(nearest, Items.TOTEM_OF_UNDYING);
        int enemyXpBottles = StatsHelper.countItem(nearest, Items.EXPERIENCE_BOTTLE);

        var armor = nearest.getInventory().armor;
        // Armor slot order in the inventory list is boots, leggings, chestplate, helmet
        int boots = StatsHelper.durabilityPercent(armor.get(0));
        int legs = StatsHelper.durabilityPercent(armor.get(1));
        int chest = StatsHelper.durabilityPercent(armor.get(2));
        int helmet = StatsHelper.durabilityPercent(armor.get(3));
        int mace = StatsHelper.maceDurabilityPercent(nearest);

        return new PvpStatsPayload(true, nearest.getName().getString(), distance,
                selfPearls, selfWind, enemyPearls, enemyWind, enemyPotions, enemyTotems,
                enemyXpBottles, helmet, chest, legs, boots, mace);
    }

    private ServerPlayerEntity findNearestEnemy(ServerPlayerEntity self) {
        ServerPlayerEntity nearest = null;
        double nearestDistSq = config.range * config.range;

        List<ServerPlayerEntity> candidates = self.getServerWorld().getPlayers();
        for (ServerPlayerEntity other : candidates) {
            if (other == self) continue;
            if (other.interactionManager.getGameMode() == GameMode.SPECTATOR) continue;

            double distSq = self.getPos().squaredDistanceTo(other.getPos());
            if (distSq <= nearestDistSq) {
                nearestDistSq = distSq;
                nearest = other;
            }
        }
        return nearest;
    }
}
