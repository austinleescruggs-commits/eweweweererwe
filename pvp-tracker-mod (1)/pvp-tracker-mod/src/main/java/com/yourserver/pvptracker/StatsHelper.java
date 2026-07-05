package com.yourserver.pvptracker;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class StatsHelper {

    public static int countItem(ServerPlayerEntity player, Item item) {
        int total = 0;
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() == item) total += stack.getCount();
        }
        for (ItemStack stack : player.getInventory().offHand) {
            if (stack.getItem() == item) total += stack.getCount();
        }
        return total;
    }

    public static int countPotions(ServerPlayerEntity player) {
        int total = 0;
        List<ItemStack> all = player.getInventory().main;
        for (ItemStack stack : all) {
            Item item = stack.getItem();
            if (item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION) {
                total += stack.getCount();
            }
        }
        return total;
    }

    /** Returns 0-100 durability percent, or -1 if the slot is empty or the item has no durability. */
    public static int durabilityPercent(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return -1;
        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0) return -1;
        if (stack.contains(DataComponentTypes.UNBREAKABLE)) return 100;

        int damage = stack.getDamage();
        double remaining = (double) (maxDamage - damage) / maxDamage * 100.0;
        return (int) Math.round(remaining);
    }

    /** Finds a Mace in main hand or offhand, if any, and returns its durability percent (or -1). */
    public static int maceDurabilityPercent(ServerPlayerEntity player) {
        ItemStack main = player.getMainHandStack();
        if (main.getItem() == Items.MACE) return durabilityPercent(main);
        ItemStack off = player.getOffHandStack();
        if (off.getItem() == Items.MACE) return durabilityPercent(off);
        return -1;
    }
}
