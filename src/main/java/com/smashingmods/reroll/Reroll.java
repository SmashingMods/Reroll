package com.smashingmods.reroll;

import com.mojang.brigadier.CommandDispatcher;
import com.smashingmods.reroll.capability.LockCapability;
import com.smashingmods.reroll.capability.LockCapabilityImplementation;
import com.smashingmods.reroll.capability.LockCapabilityInterface;
import com.smashingmods.reroll.capability.LockCapabilityProvider;
import com.smashingmods.reroll.command.RerollCommand;
import com.smashingmods.reroll.config.ConfigHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod(Reroll.MODID)
@Mod.EventBusSubscriber
public class Reroll {
    public static final String MODID = "reroll";
    public static IEventBus MOD_EVENT_BUS;
    public static final Logger LOGGER = LogManager.getLogger();

    public Reroll() {

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_SPEC);
        MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_EVENT_BUS.addListener(this::commonSetup);
        MOD_EVENT_BUS.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        LockCapability.register();
        MinecraftForge.EVENT_BUS.register(RegisterEvents.class);

//        MinecraftForge.EVENT_BUS.register(new PlayerLoginEvent());
//        MinecraftForge.EVENT_BUS.register(new PlayerDeathEvent());
//        MinecraftForge.EVENT_BUS.register(new UseItemEvent());
    }

    private void clientSetup(final FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public void serverSetup(FMLServerStartingEvent event) {
//        event.registerServerCommand(new CommandReroll());
    }

    public static class RegisterEvents {

//        @SubscribeEvent
//        public static void onItemRegistry(final RegistryEvent.Register<Item> event) {
////            event.getRegistry().register(new DiceItem());
//        }

        @SubscribeEvent
        public static void onRegisterCommandsEvent(@NotNull RegisterCommandsEvent event) {
            CommandDispatcher<CommandSource> commandDispatcher = event.getDispatcher();
            RerollCommand.register(commandDispatcher);
        }

        @SubscribeEvent
        public static void onAttachCapabilitiesEvent(@NotNull AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof PlayerEntity) {
                event.addCapability(new ResourceLocation("reroll:lock_capability"), new LockCapabilityProvider());
            }
        }
    }
}
