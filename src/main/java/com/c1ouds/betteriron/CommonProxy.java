package com.c1ouds.betteriron;

import com.c1ouds.betteriron.utility.ItemMetaKey;
import com.c1ouds.betteriron.IronFurnace.IronFurnaceBlock;
import com.c1ouds.betteriron.IronFurnace.IronFurnaceTE;
import static com.c1ouds.betteriron.Config.iron_armorDurability;
import static com.c1ouds.betteriron.Config.iron_toolDurability;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
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

import java.util.HashSet;

public class CommonProxy {
    public static IronFurnaceBlock iron_furnace;

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        //BetterIron.LOG.info("I am BetterIron at version " + Tags.VERSION);

        try {
            java.lang.reflect.Field refField = ReflectionHelper
                .findField(Item.ToolMaterial.class, "maxUses", "field_78002_g");
            refField.setInt(Item.ToolMaterial.IRON, iron_toolDurability);

            refField = ReflectionHelper.findField(Item.ToolMaterial.class, "enchantability", "field_78008_j");
            refField.setInt(Item.ToolMaterial.IRON, Config.iron_armorEnchantability);

            refField = ReflectionHelper.findField(ItemArmor.ArmorMaterial.class, "maxDamageFactor", "field_78048_f");
            refField.setInt(ItemArmor.ArmorMaterial.IRON, iron_armorDurability);

            System.out.println("[BetterIron] Iron material values set successfully.");
        } catch (Exception e) {
            System.out.println("[BetterIron] Couldn't set iron material values.");
            e.printStackTrace();
        }
        if(Config.gold_swordDamage != 4.0f) try {
            java.lang.reflect.Field refField = ReflectionHelper.findField(Item.ToolMaterial.class, "damageVsEntity", "field_78011_i");
            refField.setFloat(Item.ToolMaterial.GOLD, Config.gold_swordDamage - 4);

            refField = ReflectionHelper.findField(net.minecraft.item.ItemSword.class, "field_150934_a");
            refField.setFloat(Items.golden_sword, Config.gold_swordDamage);

            MinecraftForge.EVENT_BUS.register(new modEventHandler());

            System.out.println("[BetterIron] Gold material damage values set successfully.");
        } catch (Exception e) {
            System.out.println("[BetterIron] Couldn't set gold material damage values.");
            e.printStackTrace();
        }
    }

    public void init(FMLInitializationEvent event) {
        Item[] items = { Items.iron_pickaxe, Items.iron_sword, Items.iron_axe, Items.iron_shovel, Items.iron_hoe, Items.shears };
        for (Item tool : items) tool.setMaxDamage(iron_toolDurability);
        Item[] armor = { Items.iron_helmet, Items.iron_chestplate, Items.iron_leggings, Items.iron_boots };
        var iron = ItemArmor.ArmorMaterial.IRON;
        for (int i = 0; i < armor.length; i++) armor[i].setMaxDamage(iron.getDurability(i));

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
        if (Config.PicksForGlass) {
            for (Object obj : GameData.getBlockRegistry()) {
                Block block = (Block) obj;
                if (block.getMaterial() == net.minecraft.block.material.Material.glass)
                    block.setHarvestLevel("pickaxe", 0);
            }
        }
    }

    public void serverStarting(FMLServerStartingEvent event) {}
}
