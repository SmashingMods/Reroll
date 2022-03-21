package com.smashingmods.reroll.events;

import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.handler.LockHandler;
import com.smashingmods.reroll.util.TagUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerDeathEvent {

    @SubscribeEvent
    public void playerDeath(LivingDamageEvent event) {
        Entity entity = event.getEntity();
        MinecraftServer server = event.getEntity().getServer();
        ICommandManager manager = server.getCommandManager();
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) entity;
            String playerName = entityPlayer.getName();
            if (Config.rerollOnDeath) {
                if (event.getAmount() > entityPlayer.getHealth()) {
                    event.setCanceled(true);
                    boolean wasLocked = TagUtil.getTag(entityPlayer).getBoolean(LockHandler.REROLL_LOCKED);
                    if (wasLocked) manager.executeCommand(server, String.format("/reroll unlock %s", playerName));
                    manager.executeCommand(server, String.format("/reroll player %s", entityPlayer.getName()));
                    if (wasLocked) manager.executeCommand(server, String.format("/reroll lock %s", playerName));
                }
            }
        }
    }
}
