package com.c1ouds.betteriron.mixins;

import com.c1ouds.betteriron.Config;
import com.c1ouds.betteriron.utility.EarlyConfig;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class CoalOreHardness {

    @Inject(method = "registerBlocks", at = @At("RETURN"))
    private static void onBlockOreConstructor(CallbackInfo ci) {
        float coal_hardness = EarlyConfig.getFloat("coalOreHardness", 3.0f);
        if(Blocks.coal_ore != null) Blocks.coal_ore.setHardness(coal_hardness);
    }
}
