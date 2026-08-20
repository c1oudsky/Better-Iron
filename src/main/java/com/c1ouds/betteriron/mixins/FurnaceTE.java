package com.c1ouds.betteriron.mixins;

import static com.c1ouds.betteriron.BetterIron.ironItems;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import com.c1ouds.betteriron.utility.ItemMetaKey;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityFurnace.class)
public abstract class FurnaceTE {
    private static final int LAVABUCKETTIME = 20000;
    @Shadow public int currentItemBurnTime;
    @Shadow public abstract ItemStack getStackInSlot(int slot);
    @Shadow public abstract boolean isBurning();

    @Inject(method = "canSmelt()Z", at = @At("HEAD"), cancellable = true, remap = true)
    public void onCanSmelt(CallbackInfoReturnable<Boolean> cir) {
        System.out.println("[BetterIron] I'm injected!");
        ItemStack input = this.getStackInSlot(0);
        if (input != null && ironItems.contains(new ItemMetaKey(input))) {
            if (this.isBurning()) {
                if (this.currentItemBurnTime != LAVABUCKETTIME) cir.setReturnValue(false);
            } else {
                ItemStack fuel = this.getStackInSlot(1);
                if (fuel == null || fuel.getItem() != Items.lava_bucket) cir.setReturnValue(false);
            }
        }
    }
}
