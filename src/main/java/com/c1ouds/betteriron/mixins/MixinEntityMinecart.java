package com.c1ouds.betteriron.mixins;

import net.minecraft.entity.item.EntityMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static com.c1ouds.betteriron.Config.MinecartSpeed;

@Mixin(EntityMinecart.class)
public class MixinEntityMinecart {
    private static final boolean IsVanilla = Math.abs(MinecartSpeed - 0.4D) < 0.001D;
    @ModifyConstant(method = "func_145821_a", constant = @Constant(doubleValue = 0.06D), remap = true)
    private double modifyPoweredRailAcceleration(double original) {
        return !IsVanilla ? (MinecartSpeed * 0.15D) : original;
    }

    @ModifyConstant(method = "func_145821_a", constant = @Constant(doubleValue = 0.02D), remap = true)
    private double modifyPoweredRailStartSpeed(double original) {
        return !IsVanilla ? (MinecartSpeed * 0.05D) : original;
    }

    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 0.4D), remap = true)
    private double modifyOnUpdateMaxSpeed(double original) {
        return !IsVanilla ? MinecartSpeed : original;
    }
}
