package com.smashingmods.reroll.config;

import com.smashingmods.reroll.Reroll;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class ConfigHandler {

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMON_SPEC = specPair.getRight();
    }

    public static class Common {

        // General Configuration
        public static ForgeConfigSpec.IntValue minDistance;
        public static ForgeConfigSpec.BooleanValue useCurrentDim;
        public static ForgeConfigSpec.BooleanValue useSpawnDim;
        public static ForgeConfigSpec.ConfigValue<String> overrideDim;
        public static ForgeConfigSpec.BooleanValue rerollOnDeath;
        public static ForgeConfigSpec.BooleanValue broadcastDeath;
        public static ForgeConfigSpec.BooleanValue resetTimeAndWeather;

        // Inventory Configuration
        public static ForgeConfigSpec.BooleanValue wipeCurrentInventory;
        public static ForgeConfigSpec.ConfigValue<List<? extends String>> rerollItems;
        public static ForgeConfigSpec.BooleanValue initialInventory;
        public static ForgeConfigSpec.BooleanValue setNewInventory;
        public static ForgeConfigSpec.BooleanValue resetEnderChest;
        public static ForgeConfigSpec.BooleanValue createGraveOnDeath;

        // Data Configuration
        public static ForgeConfigSpec.BooleanValue resetExperience;
        public static ForgeConfigSpec.BooleanValue resetAdvancements;

        // Dice Configuration
        public static ForgeConfigSpec.BooleanValue requireItem;
        public static ForgeConfigSpec.IntValue cooldown;

        // Command Configuration
        public static ForgeConfigSpec.BooleanValue startLocked;
        public static ForgeConfigSpec.BooleanValue rerollAllTogether;
        public static ForgeConfigSpec.BooleanValue createGraveOnReroll;

        public Common(ForgeConfigSpec.Builder builder) {

            String seperator = System.getProperty("line.separator");

            builder.push("General Configuration");

            minDistance = builder
                    .comment(seperator +
                            "  Minimum Distance" + seperator +
                            "  Determines the minimum distance to teleport when you reroll." +
                            seperator)
                    .defineInRange("minDistance", 512, 256, 10240);

            useCurrentDim = builder
                    .comment(seperator +
                            "  Use Current Dimension" + seperator +
                            "  Should reroll spawn location be set in the player's current dimension?" + seperator +
                            "  If set to false, it will default to the player's original spawn dimension" +
                            seperator)
                    .define("useCurrentDim", false);

            useSpawnDim = builder
                    .comment(seperator +
                            "  Use Spawn Dimension" + seperator +
                            "  Should reroll spawn location be set to the player's original respawn dimension?" + seperator +
                            "  If set to false, it will default to the override dimension." +
                            seperator)
                    .define("useSpawnDim", true);

            overrideDim = builder
                    .comment(seperator +
                            "  Override Dimension" + seperator +
                            "  Override the dimension used for the reroll spawn location." + seperator +
                            "  This can't be left blank!" +
                            seperator)
                    .define("overrideDim", "minecraft:overworld");

            rerollOnDeath = builder
                    .comment(seperator +
                            "  Reroll On Death" + seperator +
                            "  Reroll players if they die to simulate a hardcore experience." +
                            seperator)
                    .define("rerollOnDeath", true);

            broadcastDeath = builder
                    .comment(seperator +
                            "  Broadcast Death to Server" + seperator +
                            "  Should the server broadcast when a player died and was rerolled?" + seperator +
                            "  Reroll On Death must be true." +
                            seperator)
                    .define("broadcastDeath", true);

            resetTimeAndWeather = builder
                    .comment(seperator +
                            "  Reset Time and Weather on Reroll" + seperator +
                            "  This option is only available in single player." + seperator +
                            "  Wouldn't want to reset this for all players!" +
                            seperator)
                        .define("resetTimeAndWeather", true);

            builder.pop();
            builder.push("Reroll Inventory Configuration");

            wipeCurrentInventory = builder
                    .comment(seperator +
                            "  Wipe Current Inventory" + seperator +
                            "  Enable to wipe the player's inventory (or send to grave) on reroll or death." + seperator +
                            "  If send to grave is enabled, inventory will go to a grave." + seperator +
                            "  If disabled, the player will keep their inventory." +
                            seperator)
                    .define("wipeCurrentInventory", true);

            Vector<String> itemsVector = new Vector<>();
            itemsVector.add("reroll:dice;1");

            initialInventory = builder
                    .comment(seperator +
                            "  Initial Inventory" + seperator +
                            "  Should players get an initial inventory when they first join a world?" +
                            seperator)
                    .define("initialInventory", true);

            setNewInventory = builder
                    .comment(seperator +
                            "  Set New Inventory" + seperator +
                            "  Should players get an initial inventory every time they reroll?" +
                            seperator)
                    .define("setNewInventory", true);

            rerollItems = builder
                    .comment(seperator +
                            "  Reroll Items" + seperator +
                            "  A list of items that will be added to a player's inventory after using the reroll command." + seperator +
                            "  You can add any existing item per line like this: \"minecraft:torch;16\"." + seperator +
                            "  Note that you can only have as many items as there are inventory slots." +
                            seperator)
                    .defineList("rerollItems", itemsVector, configItem -> {
                        if (configItem instanceof String) {
                            String[] split = ((String) configItem).split(";");

                            if (split.length == 2) {
                                String name = split[0];
                                int count = Integer.parseInt(split[1]);
                                return ResourceLocation.tryParse(name) != null && count > 0 && count <= 64;
                            } else {
                                Reroll.LOGGER.warn(String.format("%s isn't a valid item entry. Check the Reroll config.", configItem));
                            }
                        }
                        return false;
                    });

            resetEnderChest = builder
                    .comment(seperator +
                            "  Reset Ender Chest" + seperator +
                            "  Should players have their ender chest cleared too?" +
                            seperator)
                    .define("resetEnderChest", true);

            createGraveOnDeath = builder
                    .comment(seperator +
                            "  Send Inventory to Chest" + seperator +
                            "  Set this to create a grave when you die." + seperator +
                            "  Also works with reroll on death." +
                            seperator)
                    .define("createGraveOnDeath", true);

            builder.pop();
            builder.push("Data Configuration");

            resetExperience = builder
                    .comment(seperator +
                            "  Reset Experience on Reroll" + seperator +
                            "  Should player experience reset when they are rerolled?" +
                            seperator)
                    .define("resetExperience", true);

            resetAdvancements = builder
                    .comment(seperator +
                            "  Reset Advancements on Reroll" + seperator +
                            "  Should player advancements be reset when they are rerolled?" +
                            seperator)
                    .define("resetAdvancements", true);

            builder.pop();
            builder.push("Dice Configuration");

            requireItem = builder
                    .comment(seperator +
                            "  Require Item" + seperator +
                            "  Using Reroll requires Reroll Dice. Disables the /reroll command for non-OP players." +
                            seperator)
                    .define("requireItem", true);

            cooldown = builder
                    .comment(seperator +
                            "  Cooldown" + seperator +
                            "  Cooldown time in seconds to use reroll dice." +
                            seperator)
                    .defineInRange("cooldown", 60, 30, 60 * 60 * 24);

            builder.pop();
            builder.push("Reroll Command Configuration");

            startLocked = builder
                    .comment(seperator +
                            "  Start Locked" + seperator +
                            "  This is a safety feature to lock the use of reroll at the start. Users are required to use /reroll unlock to use reroll just in case." +
                            seperator)
                    .define("startLocked", true);

            createGraveOnReroll = builder
                    .comment(seperator +
                            "  Send Inventory to Chest" + seperator +
                            "  Set this to create a grave when you reroll." +
                            seperator)
                    .define("createGraveOnReroll", true);

            rerollAllTogether = builder
                    .comment(seperator +
                            "  Reroll All Together" + seperator +
                            "  Should '/reroll all' send all players to the same location?" +
                            seperator)
                    .define("rerollAllTogether", true);

            builder.pop();
        }
    }
}
