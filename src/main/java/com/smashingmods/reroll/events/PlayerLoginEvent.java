package com.smashingmods.reroll.events;

import com.smashingmods.reroll.capability.RerollCapability;
import com.smashingmods.reroll.capability.RerollCapabilityImplementation;
import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.handler.InventoryHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

public class PlayerLoginEvent {

    @SubscribeEvent
    public static void playerLogin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP entityPlayer = (EntityPlayerMP) event.getEntity();
            RerollCapabilityImplementation rerollCapability = entityPlayer.getCapability(RerollCapability.REROLL_CAPABILITY, null);
            boolean itemsReceived = Objects.requireNonNull(rerollCapability).getItemsReceived();

            if (!itemsReceived) {
                if (Config.initialInventory) {
                    InventoryHandler.setInventory(entityPlayer, Config.rerollItems);
                    rerollCapability.setItemsReceived(true);
                }
                if (Config.startLocked) {
                    rerollCapability.setLock(true);
                }
            }
        }
    }
}
