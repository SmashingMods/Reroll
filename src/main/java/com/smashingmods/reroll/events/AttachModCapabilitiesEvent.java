package com.smashingmods.reroll.events;

import com.smashingmods.reroll.capability.RerollCapabilityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

public class AttachModCapabilitiesEvent {

    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(@Nonnull AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation("reroll:capability"), new RerollCapabilityProvider());
        }
    }
}
