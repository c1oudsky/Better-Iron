package com.c1ouds.betteriron.utility;

import java.util.Objects;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemMetaKey {

    private final Item item;
    private final int meta;

    public ItemMetaKey(Item item) {
        this.item = item;
        this.meta = 0;
    }

    public ItemMetaKey(ItemStack stack) {
        this.item = stack.getItem();
        this.meta = stack.getItemDamage();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemMetaKey that = (ItemMetaKey) o;
        return this.meta == that.meta && this.item == that.item;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, meta);
    }
}
