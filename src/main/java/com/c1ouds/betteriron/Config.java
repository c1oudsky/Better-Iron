package com.c1ouds.betteriron;

import java.io.File;
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
    static public int[] chain_armorProtection;
    static public int chain_armorEnchantability;
    static public Set<ItemMetaKey> ironItems;
    static public float coalOreHardness;
    static public int goldPickaxeLevel;
    static public boolean PicksForGlass;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        if (Loader.isModLoaded("Thaumcraft"))
        { System.out.println("[BetterIron] Thaumcraft detected"); BetterIron.TC4 = true; }
        else { System.out.println("[BetterIron] Thaumcraft not detected"); BetterIron.TC4 = false; }

        iron_toolDurability = configuration.getInt("iron_toolsDurability", Configuration.CATEGORY_GENERAL,
            400, 1, Short.MAX_VALUE, "vanilla: 250");
        iron_armorDurability = configuration.getInt("iron_armorDurability", Configuration.CATEGORY_GENERAL,
            20, 1, Short.MAX_VALUE, "vanilla: 15");
        iron_armorEnchantability = configuration.getInt("iron_armorEnchantability", Configuration.CATEGORY_GENERAL,
            14, 0, Short.MAX_VALUE, "Default value is vanilla");
        gold_swordDamage = configuration.getFloat("gold_swordDamage", Configuration.CATEGORY_GENERAL,
            BetterIron.TC4 ? 7 : 4, 4f, Short.MAX_VALUE,
            "Golden sword is set to diamond tier damage (7.0) if thaumcraft is present to balance gold tier. Vanilla value is 4.0.");
        chain_armorProtection = configuration.get(Configuration.CATEGORY_GENERAL, "chain_armorProtection", new int[] {2,5,4,1},
            "Default values are vanilla").getIntList();
        chain_armorEnchantability = configuration.getInt("chain_armorEnchantability", Configuration.CATEGORY_GENERAL,
            12, 0, Short.MAX_VALUE, "Default value is vanilla");
        if (BetterIron.TC4) thaumium_toolDurability = configuration.getInt("thaumium_toolDurability",
            "Thaumcraft", 700, 0, Short.MAX_VALUE,
            "Set to 700 by default comparing to iron 400. (for comparison: elemental and diamond tiers are ~1500) Vanilla value is 400.");

        coalOreHardness = configuration.getFloat("coalOreHardness", Configuration.CATEGORY_GENERAL,
            3.0F, 0.1F, 20.0F,"Default value is vanilla");
        goldPickaxeLevel = configuration.getInt("goldPickaxeLevel", Configuration.CATEGORY_GENERAL,
            BetterIron.TC4 ? 3 : 0, 0, 3,
            "Golden pickaxe is set to diamond harvest level (3) if thaumcraft is present to balance gold tier. Vanilla value is 0.");
        PicksForGlass = configuration.getBoolean("assignPickaxesForGlass", Configuration.CATEGORY_GENERAL, true,
            "Assigns pickaxe as effecient tool type for all blocks with glass material. Vanilla valus is false");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
