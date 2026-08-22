package com.c1ouds.betteriron.IronFurnace;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class IronFurnaceContainer extends Container {
    public final IronFurnaceTE furnaceTE;
    int lastCookProgress;
    int lastHeat;

    public IronFurnaceContainer(InventoryPlayer playerInventory, TileEntity tileEntity) {
        this.furnaceTE = (IronFurnaceTE) tileEntity;

        this.addSlotToContainer(new Slot(this.furnaceTE, 0, 56, 27));
        this.addSlotToContainer(new SlotFurnace(playerInventory.player, this.furnaceTE, 1, 116, 27));

        // Slots
        for (int col = 0; col < 9; ++col) {
            for (int row = 0; row < 3; ++row)
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.furnaceTE.isUseableByPlayer(player);
    }

    // Shift-clicking items
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack_copy = null;
        Slot slot = this.inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack_in_slot = slot.getStack();
            itemstack_copy = itemstack_in_slot.copy();
            if (slotIndex == 0 || slotIndex == 1) {
                if (!this.mergeItemStack(itemstack_in_slot, 2, 38, true))
                    return null;
                slot.onSlotChange(itemstack_in_slot, itemstack_copy);
            }
            else { //not furnace
                if (!this.mergeItemStack(itemstack_in_slot, 0, 1, false)) {
                    return null;
                }
            }
            if (itemstack_in_slot.stackSize == 0) slot.putStack((ItemStack) null);
            else slot.onSlotChanged();
            if (itemstack_in_slot.stackSize == itemstack_copy.stackSize) return null;
            slot.onPickupFromSlot(player, itemstack_in_slot);
        }
        return itemstack_copy;
    }

    @Override
    public void addCraftingToCrafters(net.minecraft.inventory.ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);
        icrafting.sendProgressBarUpdate(this, 0, this.furnaceTE.cookProgress);
        icrafting.sendProgressBarUpdate(this, 1, this.furnaceTE.heat);
    }
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (ICrafting crafter : this.crafters) {
            if (this.lastCookProgress != this.furnaceTE.cookProgress)
                // ID 0: cookProgress
                crafter.sendProgressBarUpdate(this, 0, this.furnaceTE.cookProgress);
            if (this.lastHeat != this.furnaceTE.heat)
                // ID 1: heat
                crafter.sendProgressBarUpdate(this, 1, this.furnaceTE.heat);
        }
        this.lastCookProgress = this.furnaceTE.cookProgress;
        this.lastHeat = this.furnaceTE.heat;
    }
    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id == 0) this.furnaceTE.cookProgress = data;
        if (id == 1) this.furnaceTE.heat = data;
    }
}
