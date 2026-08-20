package com.c1ouds.betteriron;

import static com.c1ouds.betteriron.BetterIron.instance;
import static com.c1ouds.betteriron.Config.ironItems;
import com.c1ouds.betteriron.utility.ItemMetaKey;

import com.c1ouds.betteriron.IronFurnace.IronFurnaceBlock;
import com.c1ouds.betteriron.IronFurnace.IronFurnaceTE;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.HashSet;

public class CommonProxy {

    final static int iron_toolDurability = 400;
    final static int iron_armorDurability = 20;
    private boolean TC4 = false;

    public static IronFurnaceBlock iron_furnace;

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());

        //BetterIron.LOG.info(Config.greeting);
        BetterIron.LOG.info("I am BetterIron at version " + Tags.VERSION);

        try {
            java.lang.reflect.Field refField = ReflectionHelper
                .findField(Item.ToolMaterial.class, "maxUses", "field_78002_g");
            refField.setAccessible(true);
            refField.setInt(Item.ToolMaterial.IRON, iron_toolDurability);

            refField = ReflectionHelper.findField(Item.ToolMaterial.class, "enchantability", "field_78008_j");
            refField.setAccessible(true);
            refField.setInt(Item.ToolMaterial.IRON, 12); // было 14

            refField = ReflectionHelper.findField(ItemArmor.ArmorMaterial.class, "maxDamageFactor", "field_78048_f");
            refField.setAccessible(true);
            refField.setInt(ItemArmor.ArmorMaterial.IRON, iron_armorDurability);

            System.out.println("[BetterIron] Iron material values set successfully.");
        } catch (Exception e) {
            System.out.println("[BetterIron] Couldn't set iron material values.");
            e.printStackTrace();
        }
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        try {
            java.lang.reflect.Field refField = ReflectionHelper.findField(Item.class, "maxDamage", "field_77699_b");
            refField.setAccessible(true);
            refField.setInt(net.minecraft.init.Items.iron_pickaxe, iron_toolDurability);
            refField.setInt(net.minecraft.init.Items.iron_sword, iron_toolDurability);
            refField.setInt(net.minecraft.init.Items.iron_axe, iron_toolDurability);
            refField.setInt(net.minecraft.init.Items.iron_shovel, iron_toolDurability);
            refField.setInt(net.minecraft.init.Items.iron_hoe, iron_toolDurability);
            refField.setInt(Items.shears, iron_toolDurability);

            Item[] armor = { Items.iron_helmet, Items.iron_chestplate, Items.iron_leggings, Items.iron_boots };
            var iron = ItemArmor.ArmorMaterial.IRON;
            for (int i = 0; i < 4; i++) {
                refField.setInt(armor[i], iron.getDurability(i));
            }

            System.out.println("[BetterIron] Vanilla items iron values rewritten successfully.");
        } catch (Exception e) {
            System.out.println("[BetterIron] Couldn't rewrite vanilla items iron values.");
            e.printStackTrace();
        }

        if (Loader.isModLoaded("Thaumcraft")) {
            System.out.println("[BetterIron] Thaumcraft detected"); TC4 = true;
            TC4integration.applyThaumiumValue();
        } else System.out.println("[BetterIron] Thaumcraft not detected");

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        iron_furnace = new IronFurnaceBlock();
        GameRegistry.registerBlock(iron_furnace, "iron_furnace");
        GameRegistry.registerTileEntity(IronFurnaceTE.class, "betteriron:iron_furnace_tileentity");
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(iron_furnace, 1),
    "BCB",
            "bFb",
            "BCB",
            'F', Blocks.furnace, 'B', "blockBronze", 'C', "blockCopper", 'b', Blocks.brick_block));
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        ironItems = new HashSet<ItemMetaKey>();
        ironItems.add(new ItemMetaKey(Item.getItemFromBlock(Blocks.iron_ore)));
        if(TC4) TC4integration.addIron();
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}
}
