package com.c1ouds.betteriron.mixins;

import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static com.c1ouds.betteriron.Config.MinecartSpeed;

@Mixin(BlockRailBase.class)
public class MixinBlockRail {
    private static final boolean IsVanilla = Math.abs(MinecartSpeed - 0.4D) < 0.001D;
    private static final double SPEED_MULTIPLIER = MinecartSpeed / 0.4D;

    @Inject(method = "getRailMaxSpeed", at = @At("RETURN"), cancellable = true, remap = false)
    private void onGetRailMaxSpeed(CallbackInfoReturnable<Float> cir) {
        if (!IsVanilla) cir.setReturnValue((float)MinecartSpeed);
    }
}
