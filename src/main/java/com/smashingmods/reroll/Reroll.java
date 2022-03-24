package com.smashingmods.reroll;

import com.mojang.brigadier.CommandDispatcher;
import com.smashingmods.reroll.capability.RerollCapability;
import com.smashingmods.reroll.capability.RerollCapabilityImplementation;
import com.smashingmods.reroll.capability.RerollCapabilityProvider;
import com.smashingmods.reroll.command.RerollCommand;
import com.smashingmods.reroll.config.ConfigHandler;
import com.smashingmods.reroll.handler.RerollHandler;
import com.smashingmods.reroll.item.ItemRegistry;
import com.smashingmods.reroll.network.RerollPacketHandler;
import com.smashingmods.reroll.util.RerollUtilities;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;


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
        ItemRegistry.register(MOD_EVENT_BUS);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        RerollCapability.register();
        RerollPacketHandler.register();
        MinecraftForge.EVENT_BUS.register(RegisterEvents.class);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class RegisterEvents {

        @SubscribeEvent
        public static void onRegisterCommandsEvent(@Nonnull RegisterCommandsEvent event) {
            CommandDispatcher<CommandSource> commandDispatcher = event.getDispatcher();
            RerollCommand.register(commandDispatcher);
        }

        @SubscribeEvent
        public static void onAttachCapabilitiesEvent(@Nonnull AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof PlayerEntity) {
                event.addCapability(new ResourceLocation("reroll:capability"), new RerollCapabilityProvider());
            }
        }

        @SubscribeEvent
        public static void onLivingDeathEvent(@Nonnull LivingDamageEvent event) {
            if (ConfigHandler.Common.rerollOnDeath.get()) {
                if (event.getEntity() instanceof PlayerEntity) {

                    RerollHandler handler = new RerollHandler();
                    PlayerEntity player = (PlayerEntity) event.getEntity();
                    float amount = event.getAmount();
                    float health = player.getHealth();

                    if (amount > health) {
                        event.setCanceled(true);
                        handler.reroll((ServerPlayerEntity) player, true);
                    }
                }
            }
        }

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
}
