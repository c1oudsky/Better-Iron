package com.c1ouds.betteriron.IronFurnace;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import static net.minecraft.item.ItemStack.loadItemStackFromNBT;

public class IronFurnaceTE extends TileEntity implements IInventory {
    public int cookProgress = 0;
    public final int maxCookTime = 200;
    public int heat = 0;
    public final int maxHeat = maxCookTime;
    public boolean isHeated = false;

    public final ItemStack[] furnaceItemStacks = new ItemStack[2];
    @Override
    public int getSizeInventory() {
        return this.furnaceItemStacks.length;
    }
    @Override
    public ItemStack getStackInSlot(int index) {
        return this.furnaceItemStacks[index];
    }
    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.furnaceItemStacks[index] != null) {
            ItemStack itemstack;
            if (this.furnaceItemStacks[index].stackSize <= count) {
                itemstack = this.furnaceItemStacks[index];
                this.furnaceItemStacks[index] = null;
                this.markDirty();
                return itemstack;
            } else {
                itemstack = this.furnaceItemStacks[index].splitStack(count);
                if (this.furnaceItemStacks[index].stackSize == 0) {
                    this.furnaceItemStacks[index] = null;
                }
                this.markDirty();
                return itemstack;
            }
        }
        return null;
    }
    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (this.furnaceItemStacks[index] != null) {
            ItemStack itemstack = this.furnaceItemStacks[index];
            this.furnaceItemStacks[index] = null;
            return itemstack;
        }
        return null;
    }
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.furnaceItemStacks[index] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }
    @Override
    public String getInventoryName() { return "container.iron_furnace"; }
    @Override
    public boolean hasCustomInventoryName() { return false; }
    @Override
    public int getInventoryStackLimit() { return 64; }
    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        // Проверка: не ушел ли игрок слишком далеко от печки
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
            && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }
    @Override public void openInventory() {}
    @Override public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index < 1;
    }

    @Override
    public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote) return;
        boolean shouldMarkDirty = false;

        Block belowBlock = this.worldObj.getBlock(this.xCoord, this.yCoord - 1, this.zCoord);
        int belowBlockMeta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord - 1, this.zCoord);
        boolean hasLava = (belowBlock == Blocks.lava && belowBlockMeta == 0);
        if (hasLava && this.cookProgress == 0 && this.heat < this.maxHeat) {
            this.heat++;
            shouldMarkDirty = true;
        }
        else if (!hasLava && this.cookProgress == 0 && this.heat > 0) {
            this.heat--;
            shouldMarkDirty = true;
        }
        if (this.canSmelt()) {
            if (this.cookProgress == 0 && this.heat >= this.maxHeat) {
                this.cookProgress = 1;
                shouldMarkDirty = true;
            }
            if (this.cookProgress > 0) {
                this.cookProgress++;
                this.heat--;
                if (this.cookProgress >= this.maxCookTime) {    /* smelting finished */
                    this.cookProgress = 0;
                    this.transformSmeltItem();
                }
                shouldMarkDirty = true;
            }
        }
        else if (this.cookProgress > 0) {
            this.cookProgress = 0;
            shouldMarkDirty = true;
        }

        boolean currentlyHeated = this.heat > 0;
        if (this.isHeated != currentlyHeated) { // updating block state for clients
            this.isHeated = currentlyHeated;
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
        if (shouldMarkDirty) {  // saving nbt data if anything changed
            this.markDirty();
        }
    }
    private boolean canSmelt() {
        if (this.furnaceItemStacks[0] == null) return false;
        ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0]);
        if (result == null) return false;
        if (this.furnaceItemStacks[1] == null) return true;
        if (!this.furnaceItemStacks[1].isItemEqual(result)) return false;

        int count = this.furnaceItemStacks[1].stackSize + result.stackSize;
        return count <= getInventoryStackLimit() && count <= this.furnaceItemStacks[1].getMaxStackSize();
    }
    protected void transformSmeltItem() {
        if (this.furnaceItemStacks[0] == null) return;

        ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0]);
        if (this.furnaceItemStacks[1] == null)
            this.furnaceItemStacks[1] = result.copy();
        else if (this.furnaceItemStacks[1].getItem() == result.getItem())
            this.furnaceItemStacks[1].stackSize += result.stackSize;
        this.furnaceItemStacks[0].stackSize--;
        if (this.furnaceItemStacks[0].stackSize <= 0) this.furnaceItemStacks[0] = null;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("FurnaceHeat", this.heat);
        nbt.setInteger("FurnaceCookProgress", this.cookProgress);
        nbt.setBoolean("IsHeated", this.isHeated);

        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < this.furnaceItemStacks.length; i++) {
            if (this.furnaceItemStacks[i] != null) {
                NBTTagCompound slotNbt = new NBTTagCompound();
                slotNbt.setByte("SlotSlot", (byte) i);
                this.furnaceItemStacks[i].writeToNBT(slotNbt);
                itemList.appendTag(slotNbt);
            }
        }
        nbt.setTag("Items", itemList);
    }
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.heat = nbt.getInteger("FurnaceHeat");
        this.cookProgress = nbt.getInteger("FurnaceCookProgress");
        this.isHeated = nbt.getBoolean("IsHeated");

        java.util.Arrays.fill(this.furnaceItemStacks, null);
        NBTTagList itemList = nbt.getTagList("Items", 10); // 10 — ID for NBTTagCompound
        for (int i = 0; i < itemList.tagCount(); i++) {
            NBTTagCompound slotNbt = itemList.getCompoundTagAt(i);
            int slotIndex = slotNbt.getByte("SlotSlot");
            if (slotIndex >= 0 && slotIndex < this.furnaceItemStacks.length)
                this.furnaceItemStacks[slotIndex] = loadItemStackFromNBT(slotNbt);
        }
    }

    @Override
    public net.minecraft.network.Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("IsHeated", this.isHeated);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbt);
    }
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound nbt = pkt.func_148857_g();
        boolean receivedValue = nbt.getBoolean("IsHeated");
        if(this.isHeated != receivedValue && this.worldObj != null) {
            this.isHeated = receivedValue;
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }
}
