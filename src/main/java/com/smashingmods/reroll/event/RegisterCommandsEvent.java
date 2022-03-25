package com.smashingmods.reroll.event;

import com.mojang.brigadier.CommandDispatcher;
import com.smashingmods.reroll.command.RerollCommand;
import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegisterCommandsEvent {

    @SubscribeEvent
    public static void onRegisterCommandsEvent(@Nonnull net.minecraftforge.event.RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> commandDispatcher = event.getDispatcher();
        RerollCommand.register(commandDispatcher);
    }
}
