package com.c1ouds.betteriron.compat;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class C1STintegration {
    static List<Item> goldengear = new ArrayList<>(Arrays.asList(Items.golden_helmet, Items.golden_chestplate, Items.golden_leggings, Items.golden_boots,
        Items.golden_pickaxe, Items.golden_axe, Items.golden_shovel, Items.golden_hoe, Items.golden_sword));
    static public void addGoldTools() {
        c1oudstweaks.Config.ReenchantableItems.addAll(goldengear);
    }
}
