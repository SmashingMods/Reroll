package com.smashingmods.reroll.events;

import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.handler.RerollHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerDeathEvent {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void playerDeath(LivingDamageEvent event) {
        Entity entity = event.getEntity();
        MinecraftServer server = event.getEntity().getServer();

        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP entityPlayer = (EntityPlayerMP) entity;
            RerollHandler handler = new RerollHandler();

            if (Config.rerollOnDeath) {
                if (event.getAmount() > entityPlayer.getHealth()) {
                    event.setCanceled(true);
                    handler.reroll(server, entityPlayer, true);
                    entityPlayer.sendMessage(new TextComponentTranslation("reroll.death_event.player"));
                }
            }
        }
    }
}
