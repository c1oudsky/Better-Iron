package com.c1ouds.betteriron;

import com.c1ouds.betteriron.utility.ItemMetaKey;
import com.c1ouds.betteriron.IronFurnace.IronFurnaceBlock;
import com.c1ouds.betteriron.IronFurnace.IronFurnaceTE;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;

public class CommonProxy {
    public static IronFurnaceBlock iron_furnace;

    private boolean chainPr=false;
    private boolean goldPr=false;

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        //BetterIron.LOG.info("I am BetterIron at version " + Tags.VERSION);

        // <editor-fold desc="Reflection | Iron material values">
        try {
            Field refField = ReflectionHelper
                .findField(Item.ToolMaterial.class, "maxUses", "field_78002_g");
            refField.setInt(Item.ToolMaterial.IRON, Config.iron_toolDurability);

            refField = ReflectionHelper.findField(Item.ToolMaterial.class, "enchantability", "field_78008_j");
            refField.setInt(Item.ToolMaterial.IRON, Config.iron_armorEnchantability);

            refField = ReflectionHelper.findField(ItemArmor.ArmorMaterial.class, "maxDamageFactor", "field_78048_f");
            refField.setInt(ItemArmor.ArmorMaterial.IRON, Config.iron_armorDurability);

            System.out.println("[BetterIron] Iron material values set successfully.");
        } catch (Exception e) {
            System.out.println("[BetterIron] Couldn't set iron material values.");
            e.printStackTrace();
        }
        // </editor-fold>
        // <editor-fold desc="Reflection | Gold material damage values">
        if(Config.gold_swordDamage != 4.0f) try {
            Field refField = ReflectionHelper.findField(Item.ToolMaterial.class, "damageVsEntity", "field_78011_i");
            refField.setFloat(Item.ToolMaterial.GOLD, Config.gold_swordDamage - 4);

            refField = ReflectionHelper.findField(net.minecraft.item.ItemSword.class, "field_150934_a");
            refField.setFloat(Items.golden_sword, Config.gold_swordDamage);

            if(Config.fixPigmenDmg) MinecraftForge.EVENT_BUS.register(new modEventHandler());

            System.out.println("[BetterIron] Gold material damage values set successfully.");
        } catch (Exception e) {
            System.out.println("[BetterIron] Couldn't set gold material damage values.");
            e.printStackTrace();
        }

        if (!Arrays.equals(Config.gold_armorProtection, new int[]{2, 5, 4, 1})) goldPr = true;
        if(goldPr || Config.gold_armorDurability != 7) try {
            String debug = "[BetterIron] Gold armor ";
            if(goldPr) {
                Field refField = ReflectionHelper.findField(ItemArmor.ArmorMaterial.class, "damageReductionAmountArray", "field_78049_g");
                refField.set(ItemArmor.ArmorMaterial.GOLD, Config.gold_armorProtection);

                debug += "material protection (1/2)";
            }
            if(Config.gold_armorDurability != 7) {
                Field refField = ReflectionHelper.findField(ItemArmor.ArmorMaterial.class, "maxDamageFactor", "field_78048_f");
                refField.setInt(ItemArmor.ArmorMaterial.GOLD, Config.gold_armorDurability);

                debug += (goldPr ? " and " : "") + "durability";
            }
            System.out.println(debug+" values set successfully.");
        } catch (Exception e) {
            System.out.println("[BetterIron] Couldn't set golden armor values.");
            e.printStackTrace();
        }
        // </editor-fold>
        // <editor-fold desc="Reflection | Chainmail material values">
        if(!Arrays.equals(Config.chain_armorProtection, new int[]{2, 5, 3, 1})) chainPr = true;
        if(chainPr || Config.chain_armorEnchantability != 12 || Config.chain_armorDurability != 15) try {
            String debug = "[BetterIron] Chainmail ";
            boolean strBegan=false;
            if(chainPr) {
                Field refField = ReflectionHelper.findField(ItemArmor.ArmorMaterial.class, "damageReductionAmountArray", "field_78049_g");
                refField.set(ItemArmor.ArmorMaterial.CHAIN, Config.chain_armorProtection);

                debug += "material protection (1/2)"; strBegan=true;
            }
            if(Config.chain_armorEnchantability != 12) {
                Field refField = ReflectionHelper.findField(ItemArmor.ArmorMaterial.class, "enchantability", "field_78055_h");
                refField.setInt(ItemArmor.ArmorMaterial.CHAIN, Config.chain_armorEnchantability);

                debug += (strBegan ? ", " : "") + "enchantability"; strBegan=true;
            }
            if(Config.chain_armorDurability != 15) {
                Field refField = ReflectionHelper.findField(ItemArmor.ArmorMaterial.class, "maxDamageFactor", "field_78048_f");
                refField.setInt(ItemArmor.ArmorMaterial.CHAIN, Config.chain_armorDurability);

                debug += (strBegan ? " and " : "") + "durability";
            }
            System.out.println(debug+" values set successfully.");
        } catch (Exception e) {
            System.out.println("[BetterIron] Couldn't set chainmail armor values.");
            e.printStackTrace();
        }
        // </editor-fold>
    }

    public void init(FMLInitializationEvent event) {
        // <editor-fold desc="Gear durability recalculation">

        //Iron gear
        Item[] items = { Items.iron_pickaxe, Items.iron_sword, Items.iron_axe, Items.iron_shovel, Items.iron_hoe, Items.shears };
        for (Item tool : items) tool.setMaxDamage(Config.iron_toolDurability);
        Item[] armor = { Items.iron_helmet, Items.iron_chestplate, Items.iron_leggings, Items.iron_boots };
        var material = ItemArmor.ArmorMaterial.IRON;
        for (int i = 0; i < armor.length; i++) armor[i].setMaxDamage(material.getDurability(i));

        //Chain and gold armor durability
        Item[] chainarmor = { Items.chainmail_helmet, Items.chainmail_chestplate, Items.chainmail_leggings, Items.chainmail_boots };
        Item[] goldarmor = new Item[] { Items.golden_helmet, Items.golden_chestplate, Items.golden_leggings, Items.golden_boots };
        if(Config.chain_armorDurability != 15 || !Config.chain_armorDurabilityMultiplier) {
            material = ItemArmor.ArmorMaterial.CHAIN;
            for (int i = 0; i < chainarmor.length; i++)
                chainarmor[i].setMaxDamage(Config.chain_armorDurabilityMultiplier ? material.getDurability(i) : Config.chain_armorDurability);
        }
        if(Config.gold_armorDurability != 7) {
            material = ItemArmor.ArmorMaterial.GOLD;
            for (int i = 0; i < goldarmor.length; i++)
                goldarmor[i].setMaxDamage(material.getDurability(i));
        }
        // </editor-fold>

        // <editor-fold desc="Reflection | Chainmail and golden armor protection values">
        if(chainPr || goldPr) try {
            Field refField = ReflectionHelper.findField(ItemArmor.class, "damageReduceAmount", "field_77879_b");
            if(chainPr) {
                material = ItemArmor.ArmorMaterial.CHAIN;
                for (int i = 0; i < chainarmor.length; i++) refField.setInt(chainarmor[i], material.getDamageReductionAmount(i));
                System.out.println("[BetterIron] Chainmail armor protection values (2/2) set successfully.");
            }
            if(goldPr) {
                material = ItemArmor.ArmorMaterial.GOLD;
                for (int i = 0; i < goldarmor.length; i++) refField.setInt(goldarmor[i], material.getDamageReductionAmount(i));
                System.out.println("[BetterIron] Golden armor protection values (2/2) set successfully.");
            }
        } catch (Exception e) {
            String debug = "[BetterIron] Couldn't set ";
            if(chainPr) debug += "chainmail"; if(goldPr) debug += (chainPr ? " and " : "") + "golden";
            System.out.println(debug + " armor protection values (2/2).");
            e.printStackTrace();
        }
        // </editor-fold>

        Blocks.coal_ore.setHardness(Config.coalOreHardness);
        Items.golden_pickaxe.setHarvestLevel("pickaxe", Config.goldPickaxeLevel);

        Config.ironItems = new HashSet<ItemMetaKey>();
        Config.ironItems.add(new ItemMetaKey(Item.getItemFromBlock(Blocks.iron_ore)));
        if (BetterIron.TC4) {
            TC4integration.applyThaumiumValue();
            TC4integration.addIron();
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(BetterIron.instance, new GuiHandler());
        iron_furnace = new IronFurnaceBlock();
        GameRegistry.registerBlock(iron_furnace, "iron_furnace");
        GameRegistry.registerTileEntity(IronFurnaceTE.class, "betteriron:iron_furnace_tileentity");
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(iron_furnace, 1),
   "BCB",
            "bFb",
            "BCB",
            'F', Blocks.furnace, 'B', "blockBronze", 'C', "blockCopper", 'b', Blocks.brick_block));
    }

    public void postInit(FMLPostInitializationEvent event) {
        if (Config.PicksForGlass || Config.ForgeAxeForWood) {
            for (Object obj : GameData.getBlockRegistry()) {
                Block block = (Block) obj;
                if (Config.PicksForGlass && block.getMaterial() == Material.glass)
                    block.setHarvestLevel("pickaxe", 0);
                else if (Config.ForgeAxeForWood && block.getMaterial() == Material.wood)
                    block.setHarvestLevel("axe", 0);
            }
        }
    }

    public void serverStarting(FMLServerStartingEvent event) {}
}
