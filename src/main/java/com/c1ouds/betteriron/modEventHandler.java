package com.c1ouds.betteriron;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class modEventHandler {
    @SubscribeEvent
    public void onPigZombieSpawn(EntityJoinWorldEvent event) {
        if (event == null || event.entity == null || event.world == null) {
            return;
        }
        if (!event.world.isRemote && event.entity instanceof EntityPigZombie) {
            EntityPigZombie pigman = (EntityPigZombie) event.entity;
            double damageAddedByYourMod = Config.gold_swordDamage - 4f;
            IAttributeInstance attackAttribute = pigman.getEntityAttribute(SharedMonsterAttributes.attackDamage);
            if (attackAttribute != null) {
                var oldValue = attackAttribute.getBaseValue();
                if (oldValue == 5.0D) attackAttribute.setBaseValue(oldValue - damageAddedByYourMod);
                System.out.println("[BetterIron] Pigzombie attacked damage fixed with new golden sword damage in mind" +
                    " (old attackAttribute: "+oldValue+", new: " + attackAttribute.getBaseValue() + ")");
            }
        }
    }

}
