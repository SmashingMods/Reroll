package com.smashingmods.reroll.events;

import com.smashingmods.reroll.handler.RerollHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerDeathEvent {

    @SubscribeEvent
    public void playerDeath(LivingDamageEvent event) {
        RerollHandler handler = new RerollHandler();
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) entity;
            if (true) {
                if (event.getAmount() > entityPlayer.getHealth()) {
                    event.setCanceled(true);
                    handler.reroll(entityPlayer.getServer(), entityPlayer, true);
                }
            }
        }
    }
}
