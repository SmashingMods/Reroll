package com.smashingmods.reroll;

import com.smashingmods.reroll.block.BlockRegistry;
import com.smashingmods.reroll.capability.RerollCapability;
import com.smashingmods.reroll.config.ConfigHandler;
import com.smashingmods.reroll.handler.ModCompatibilityHandler;
import com.smashingmods.reroll.item.ItemRegistry;
import com.smashingmods.reroll.network.RerollPacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(Reroll.MODID)
public class Reroll {
    public static final String MODID = "reroll";
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogManager.getLogger();

    public Reroll() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_SPEC);
        ItemRegistry.register();
        BlockRegistry.register();
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        RerollCapability.register();
        RerollPacketHandler.register();
        ModCompatibilityHandler.register();
    }
}
