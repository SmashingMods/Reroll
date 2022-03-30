package com.smashingmods.reroll.event;

import com.smashingmods.reroll.capability.RerollCapability;
import com.smashingmods.reroll.capability.IRerollCapability;
import com.smashingmods.reroll.config.ConfigHandler;
import com.smashingmods.reroll.util.RerollUtilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.util.LazyOptional;
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
            LazyOptional<IRerollCapability> rerollCapability = player.getCapability(RerollCapability.REROLL_CAPABILITY, null);

            rerollCapability.ifPresent(cap -> {
                if (!cap.getItemsReceived()) {
                    RerollUtilities.setInventory(player);
                    cap.setItemsReceived(true);
                }
            });
        }
    }
}
