package com.c1ouds.betteriron;

import com.c1ouds.betteriron.utility.ItemMetaKey;
import static com.c1ouds.betteriron.Config.thaumium_toolDurability;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.ReflectionHelper;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.config.ConfigItems;

public class TC4integration {

    public static void applyThaumiumValue() {
        try {
            Item.ToolMaterial Thaumium = ThaumcraftApi.toolMatThaumium;
            ReflectionHelper.setPrivateValue(Item.ToolMaterial.class, Thaumium, 700, "maxUses", "field_78002_g");

            Item[] tools = {ConfigItems.itemPickThaumium, ConfigItems.itemSwordThaumium, ConfigItems.itemAxeThaumium, ConfigItems.itemShovelThaumium, ConfigItems.itemHoeThaumium};
            for(Item tool : tools) tool.setMaxDamage(thaumium_toolDurability);

            System.out.println("[BetterIron] Thaumium material values set successfully.");
        } catch (Exception e) {
            System.out.println("[BetterIron] Couldn't set thaumium material values.");
            e.printStackTrace();
        }
    }

    public static void addIron() {
        Config.ironItems.add(new ItemMetaKey(new ItemStack(ConfigItems.itemNugget, 1, 16)));
        System.out.println("[BetterIron] Added ironCluster to iron list");
    }
}
