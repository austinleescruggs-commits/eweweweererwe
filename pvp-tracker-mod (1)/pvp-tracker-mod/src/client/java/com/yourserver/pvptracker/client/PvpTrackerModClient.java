package com.yourserver.pvptracker.client;

import com.yourserver.pvptracker.PvpStatsPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class PvpTrackerModClient implements ClientModInitializer {

    private static final int TEXT_COLOR = 0xFFFFFF;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(PvpStatsPayload.ID, (payload, context) ->
                context.client().execute(() -> PvpHudState.update(payload))
        );

        HudRenderCallback.EVENT.register(this::renderHud);
    }

    private void renderHud(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        PvpStatsPayload stats = PvpHudState.getLatest();
        if (stats == null) return;

        String text = buildText(stats);

        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();
        int textWidth = client.textRenderer.getWidth(text);

        // Just above the hotbar, centered
        int x = (screenWidth - textWidth) / 2;
        int y = screenHeight - 48;

        context.drawTextWithShadow(client.textRenderer, Text.literal(text), x, y, TEXT_COLOR);
    }

    private String buildText(PvpStatsPayload s) {
        if (!s.hasEnemy()) {
            return "You P:" + s.selfPearls() + " W:" + s.selfWind();
        }

        return "You P:" + s.selfPearls() + " W:" + s.selfWind()
                + "  |  " + s.enemyName() + " (" + s.distance() + "m)"
                + " P:" + s.enemyPearls()
                + " W:" + s.enemyWind()
                + " Pot:" + s.enemyPotions()
                + " Tot:" + s.enemyTotems()
                + " XP:" + s.enemyXpBottles()
                + " Armor[" + fmt(s.helmetDurability()) + "/" + fmt(s.chestDurability())
                + "/" + fmt(s.legsDurability()) + "/" + fmt(s.bootsDurability()) + "]"
                + " Mace:" + fmt(s.maceDurability());
    }

    private String fmt(int percent) {
        return percent < 0 ? "--" : (percent + "%");
    }
}
