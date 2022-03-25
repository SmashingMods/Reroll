package com.smashingmods.reroll.event;

import com.smashingmods.reroll.capability.RerollCapability;
import com.smashingmods.reroll.capability.RerollCapabilityImplementation;
import com.smashingmods.reroll.config.ConfigHandler;
import com.smashingmods.reroll.util.RerollUtilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerLoginEvent {

    @SubscribeEvent
    public static void onPlayerLoggedInEvent(@Nonnull PlayerEvent.PlayerLoggedInEvent event) {
        if (ConfigHandler.Common.initialInventory.get()) {
            PlayerEntity player = event.getPlayer();
            try {
                RerollCapabilityImplementation rerollCapability = player.getCapability(RerollCapability.REROLL_CAPABILITY, null).orElseThrow(() -> new IllegalAccessException("Reroll attempted to access capability on player."));

                if (!rerollCapability.getItemsReceived()) {
                    RerollUtilities.setInventory(player);
                    rerollCapability.setItemsReceived(true);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
