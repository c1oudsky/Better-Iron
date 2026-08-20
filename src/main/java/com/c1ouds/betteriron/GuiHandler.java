package com.c1ouds.betteriron;

import com.c1ouds.betteriron.IronFurnace.IronFurnaceContainer;
import com.c1ouds.betteriron.IronFurnace.IronFurnaceGUI;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) != null) {
            return new IronFurnaceContainer(player.inventory, world.getTileEntity(x, y, z));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) != null) {
            return new IronFurnaceGUI(player.inventory, world.getTileEntity(x, y, z));
        }
        return null;
    }
}
