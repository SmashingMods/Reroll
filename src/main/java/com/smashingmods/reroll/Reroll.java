package com.smashingmods.reroll;

import com.smashingmods.reroll.Command.CommandReroll;
import com.smashingmods.reroll.Command.CommandRerollPlayer;
import com.smashingmods.reroll.Config.Config;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = com.smashingmods.reroll.Reroll.MODID, name = com.smashingmods.reroll.Reroll.NAME, version = com.smashingmods.reroll.Reroll.VERSION)
public class Reroll {
    public static final String MODID = "reroll";
    public static final String NAME = "Reroll";
    public static final String VERSION = "0.0.1";
    public static Configuration CONFIG;
    public static Logger LOGGER;

    // Mod Compatibility
    public static boolean ModCompat_TimeIsUp;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        CONFIG = new Configuration(new File(event.getModConfigurationDirectory().getPath(), "reroll.cfg"));
        Config.readConfig();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
         ModCompat_TimeIsUp = Loader.isModLoaded("timeisup");

        if (CONFIG.hasChanged()) {
            CONFIG.save();
        }
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandReroll());
        event.registerServerCommand(new CommandRerollPlayer());
//        CapabilityManager.INSTANCE.register();
    }
}
