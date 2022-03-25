package com.smashingmods.reroll;

import com.smashingmods.reroll.capability.RerollCapability;
import com.smashingmods.reroll.config.ConfigHandler;
import com.smashingmods.reroll.network.RerollPacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


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
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        RerollCapability.register();
        RerollPacketHandler.register();
    }
}
