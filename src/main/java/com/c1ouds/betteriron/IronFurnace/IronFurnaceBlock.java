package com.c1ouds.betteriron.IronFurnace;

import com.c1ouds.betteriron.BetterIron;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class IronFurnaceBlock extends BlockContainer {
    @SideOnly(Side.CLIENT) public IIcon[] textures = new IIcon[4];

    public IronFurnaceBlock() {
        super(Material.rock);
        setBlockName("iron_furnace");
        setHardness(3.5F);
        setLightLevel(0.001F);
        setStepSound(soundTypeMetal);
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new IronFurnaceTE();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int l = MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        //(2 - север, 3 - юг, 4 - запад, 5 - восток)
        if (l == 0) world.setBlockMetadataWithNotify(x, y, z, 2, 2);
        if (l == 1) world.setBlockMetadataWithNotify(x, y, z, 5, 2);
        if (l == 2) world.setBlockMetadataWithNotify(x, y, z, 3, 2);
        if (l == 3) world.setBlockMetadataWithNotify(x, y, z, 4, 2);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        String prefix = "betteriron:iron_furnace_";
        this.textures[0] = reg.registerIcon(prefix + "top");
        this.textures[1] = reg.registerIcon(prefix + "side");
        this.textures[2] = reg.registerIcon(prefix + "front_unlit");
        this.textures[3] = reg.registerIcon(prefix + "front_lit");
    }

    // Hand render
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 3) return this.textures[2];
        if (side == 0 || side == 1) return this.textures[0];
        return this.textures[1];
    }

    // World render!
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        if (side == 0 || side == 1) return this.textures[0]; // top/bottom
        if (side == meta) {
            if (world.getTileEntity(x, y, z) instanceof IronFurnaceTE furnace && furnace.isHeated) return this.textures[3];
            return this.textures[2];
        }
        return this.textures[1]; // sides
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) instanceof IronFurnaceTE furnace && furnace.isHeated)
            return 13;
        else return 0;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        if (world.getTileEntity(x, y, z) instanceof IronFurnaceTE)
            // Forge сам откроет Контейнер на сервере, Gui на клиенте,
            // и автоматически нарисует инвентарь игрока снизу!
            // ID интерфейса ставим 0 (так как он у нас один)
            player.openGui(BetterIron.instance, 0, world, x, y, z);

        return true;
    }
}
