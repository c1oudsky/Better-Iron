package com.c1ouds.betteriron;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.c1ouds.betteriron.utility.ItemMetaKey;

import cpw.mods.fml.relauncher.ReflectionHelper;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.config.ConfigItems;

public class TC4integration {

    final static public int toolDurability = 700;

    public static void applyThaumiumValue() {
        try {
            Item.ToolMaterial Thaumium = ThaumcraftApi.toolMatThaumium;

            ReflectionHelper.setPrivateValue(Item.ToolMaterial.class, Thaumium, 700, "maxUses", "field_78002_g");
            java.lang.reflect.Field refField = ReflectionHelper.findField(Item.class, "maxDamage", "field_77699_b");
            refField.setAccessible(true);
            refField.setInt(ConfigItems.itemPickThaumium, toolDurability);
            refField.setInt(ConfigItems.itemSwordThaumium, toolDurability);
            refField.setInt(ConfigItems.itemAxeThaumium, toolDurability);
            refField.setInt(ConfigItems.itemShovelThaumium, toolDurability);
            refField.setInt(ConfigItems.itemHoeThaumium, toolDurability);

            System.out.println("[BetterIron] Thaumium material values set successfully.");
        } catch (Exception e) {
            System.out.println("[BetterIron] Couldn't set thaumium material values.");
            e.printStackTrace();
        }
    }

    public static void addIron() {
        BetterIron.ironItems.add(new ItemMetaKey(new ItemStack(ConfigItems.itemNugget, 1, 16)));
        System.out.println("[BetterIron] Added ironCluster to iron list");
    }
}
