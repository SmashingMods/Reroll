package com.smashingmods.reroll;

import com.smashingmods.reroll.command.CommandReroll;
import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.events.PlayerDeathEvent;
import com.smashingmods.reroll.events.PlayerLoginEvent;
import com.smashingmods.reroll.events.UseItemEvent;
import com.smashingmods.reroll.item.DiceItem;
import com.smashingmods.reroll.util.JsonMapper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = Reroll.MODID, version = "1.12.2-1.4.0", useMetadata = true)
@Mod.EventBusSubscriber
public class Reroll {
    public static final String MODID = "reroll";
    public static Configuration CONFIG;
    public static Logger LOGGER;
    public static JsonMapper MAPPER = new JsonMapper();

    // Mod Compatibility
    public static boolean MODCOMPAT_TIMEISUP;
    public static boolean MODCOMPAT_BAUBLES;
    public static boolean MODCOMPAT_GAMESSTAGES;
    public static boolean MODCOMPAT_ENDERSKILLS;

    @GameRegistry.ObjectHolder(DiceItem.name)
    public static final Item diceItem = Items.AIR;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        String path = event.getModConfigurationDirectory().getPath();
        LOGGER = event.getModLog();
        CONFIG = new Configuration(new File(path, "reroll.cfg"));
        Config.readConfig();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new PlayerLoginEvent());
        MinecraftForge.EVENT_BUS.register(new PlayerDeathEvent());
        MinecraftForge.EVENT_BUS.register(new UseItemEvent());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        MODCOMPAT_TIMEISUP = Loader.isModLoaded("timeisup");
        MODCOMPAT_BAUBLES = Loader.isModLoaded("baubles");
        MODCOMPAT_GAMESSTAGES = Loader.isModLoaded("gamestages");
        MODCOMPAT_ENDERSKILLS = Loader.isModLoaded("enderskills");

        if (CONFIG.hasChanged()) {
            CONFIG.save();
        }
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandReroll());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(new DiceItem());
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Reroll.diceItem, 0, new ModelResourceLocation(DiceItem.registryName, "inventory"));
    }
}
