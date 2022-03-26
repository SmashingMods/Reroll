package com.smashingmods.reroll.event;

import com.smashingmods.reroll.capability.RerollCapabilityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttachCapabilitiesEvent {

    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(@Nonnull net.minecraftforge.event.AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation("reroll:capability"), new RerollCapabilityProvider());
        }
    }
}
