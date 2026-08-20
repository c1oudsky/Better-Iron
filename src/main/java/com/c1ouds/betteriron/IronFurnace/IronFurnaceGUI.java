package com.c1ouds.betteriron.IronFurnace;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class IronFurnaceGUI extends GuiContainer {
    public static final ResourceLocation furnaceGuiTextures = new ResourceLocation("betteriron:textures/furnacegui.png");

    public IronFurnaceGUI(InventoryPlayer playerInventory, TileEntity tileEntity) {
        super(new IronFurnaceContainer(playerInventory, tileEntity));
    }
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(furnaceGuiTextures);
        int centerX = (this.width - this.xSize) / 2;
        int centerY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(centerX, centerY, 0, 0, this.xSize, this.ySize);
        int heatGuiFullHeight = 22;
        var furnace = ((IronFurnaceContainer)this.inventorySlots).furnaceTE;
        if (furnace.heat > 0) {
            int heatGuiHeight = furnace.heat * heatGuiFullHeight / furnace.maxHeat;
            if (furnace.cookProgress == 0) {
                if (heatGuiHeight > 0) {
                    this.drawTexturedModalRect(
                        centerX + 56,
                        centerY + 48 + (heatGuiFullHeight - heatGuiHeight),
                        176,
                        31 + (heatGuiFullHeight - heatGuiHeight),
                        16,
                        heatGuiHeight
                    );
                }
            }
            else {
                if (heatGuiHeight > 0) {
                    this.drawTexturedModalRect(
                        centerX + 56,
                        centerY + 48,
                        176,
                        31,
                        16,
                        heatGuiHeight
                    );
                    this.drawTexturedModalRect(
                        centerX + 83,
                        centerY + 27,
                        176,
                        14,
                        (heatGuiFullHeight - heatGuiHeight)+2,
                        16
                    );
                }
            }
        }
    }
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        var tileFurnace = ((IronFurnaceContainer)this.inventorySlots).furnaceTE;
        String s = StatCollector.translateToLocal(tileFurnace.getInventoryName());
        this.fontRendererObj.drawString(s, (this.xSize - this.fontRendererObj.getStringWidth(s)) / 2, 6, 4210752);
        this.fontRendererObj.drawString(
            net.minecraft.util.StatCollector.translateToLocal("container.inventory"),
            8,
            this.ySize - 96 + 2,
            4210752
        );
    }
}
