package com.yourserver.pvptracker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class PvpTrackerMod implements ModInitializer {

    public static final String MOD_ID = "pvptracker";

    @Override
    public void onInitialize() {
        PvpStatsPayload.register();

        PvpTrackerConfig config = PvpTrackerConfig.load();
        PvpTrackerServerLogic serverLogic = new PvpTrackerServerLogic(config);

        ServerTickEvents.END_SERVER_TICK.register(serverLogic::onServerTick);
    }
}
