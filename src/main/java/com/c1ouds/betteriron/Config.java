package com.c1ouds.betteriron;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

import com.c1ouds.betteriron.utility.ItemMetaKey;
import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.config.Configuration;

public class Config {

    static public int iron_toolDurability;
    static public int iron_armorDurability;
    static public int iron_armorEnchantability;
    static public int thaumium_toolDurability;
    static public float gold_swordDamage;
    static public boolean fixPigmenDmg;
    static public int[] gold_armorProtection;
    static public int gold_armorDurability;
    static public int[] chain_armorProtection;
    static public int chain_armorEnchantability;
    static public int chain_armorDurability;
    static public Set<ItemMetaKey> ironItems;
    static public int goldPickaxeLevel;
    static public boolean ReenchantGold;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        if (Loader.isModLoaded("Thaumcraft")) BetterIron.TC4 = true; else BetterIron.TC4 = false;
        if (Loader.isModLoaded("c1oudstweaks")) BetterIron.C1ST = true; else BetterIron.C1ST = false;

        iron_toolDurability = configuration.getInt("iron_toolsDurability", Configuration.CATEGORY_GENERAL,
            400, 1, Short.MAX_VALUE, "vanilla: 250");
        iron_armorDurability = configuration.getInt("iron_armorDurability", Configuration.CATEGORY_GENERAL,
            20, 1, Short.MAX_VALUE, "Iron armor durability multiplier. vanilla: 15");
        iron_armorEnchantability = configuration.getInt("iron_armorEnchantability", Configuration.CATEGORY_GENERAL,
            14, 0, Short.MAX_VALUE, "Default value is vanilla");
        gold_swordDamage = configuration.getFloat("gold_swordDamage", Configuration.CATEGORY_GENERAL,
            BetterIron.TC4 ? 7 : 4, 4f, Short.MAX_VALUE,
            "Golden sword is set to diamond tier damage (7.0) if thaumcraft is present to balance gold tier. Vanilla value is 4.0.");
        fixPigmenDmg = configuration.getBoolean("ZombiePigmenDamageStaysTheSame", Configuration.CATEGORY_GENERAL,
            false, "Tweak Zombie Pigmen's damage so it stays the same even if you change golden swords' damage." +
                " If set to false, Zombie Pigmen's damage will depend on golden sword set damage value. (Note: with golden sword damage changed " +
                "and this set to true, already spawned pigmen will retain their altered damage even if you disable the features or delete the mod.)");
        gold_armorProtection = configuration.get(Configuration.CATEGORY_GENERAL, "gold_armorProtection",
            BetterIron.TC4 ? new int[] {4,7,6,3} : new int[] {2,5,3,1},"Golden armor is set to diamond tier protection if thaumcraft is present " +
                "to balance gold tier. Vanilla values are 2,5,3,1.").getIntList();
        gold_armorDurability = configuration.getInt("gold_armorDurability", Configuration.CATEGORY_GENERAL,
            BetterIron.TC4 ? 3 : 7, 1, Short.MAX_VALUE, "Golden armor durability is set to about be as low as golden tools if " +
                "thaumcraft is present to balance gold tier. Vanilla value is 7.");

        chain_armorProtection = configuration.get(Configuration.CATEGORY_GENERAL, "chain_armorProtection", new int[] {2,5,4,1},
            "Default values are vanilla").getIntList();
        if (chain_armorProtection.length != 4) {
            System.out.println("[BetterIron] Chainmail material protection values in config are incorrect (needs to be 4 integers).");
            chain_armorProtection = new int[]{2, 5, 4, 1};
        }
        chain_armorDurability = configuration.getInt("chain_armorDurability", Configuration.CATEGORY_GENERAL,
            15, 1, Short.MAX_VALUE,"chainmail armor durability multiplier. vanilla: 15");
        chain_armorEnchantability = configuration.getInt("chain_armorEnchantability", Configuration.CATEGORY_GENERAL,
            12, 0, Short.MAX_VALUE, "Default value is vanilla");
        if (BetterIron.TC4) thaumium_toolDurability = configuration.getInt("thaumium_toolDurability",
            "Thaumcraft", 700, 0, Short.MAX_VALUE,
            "Set to 700 by default comparing to iron 400. (for comparison: elemental and diamond tiers are ~1500) Vanilla value is 400.");
        goldPickaxeLevel = configuration.getInt("goldPickaxeLevel", Configuration.CATEGORY_GENERAL,
            BetterIron.TC4 ? 3 : 0, 0, 3,
            "Golden pickaxe is set to diamond harvest level (3) if thaumcraft is present to balance gold tier. Vanilla value is 0.");
        ReenchantGold = configuration.getBoolean("ReenchantableGoldGear", Configuration.CATEGORY_GENERAL, BetterIron.C1ST,
            "Allows golden gear to be re-enchanted in enchanting table. Requires C1oudS Tweaks.");
        if(ReenchantGold && !BetterIron.C1ST) {
            System.out.println("[BetterIron]: C1oudS Tweaks isn't detected! Resetting ReenchantableGoldTools to false.");
        }

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
