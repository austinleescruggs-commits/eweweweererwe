package com.yourserver.pvptracker.client;

import com.yourserver.pvptracker.PvpStatsPayload;

/** Simple static holder for the most recent stats packet, read by the HUD renderer. */
public class PvpHudState {

    private static volatile PvpStatsPayload latest = null;

    public static void update(PvpStatsPayload payload) {
        latest = payload;
    }

    public static PvpStatsPayload getLatest() {
        return latest;
    }

    public static void clear() {
        latest = null;
    }
}
