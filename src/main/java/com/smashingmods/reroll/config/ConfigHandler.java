package com.smashingmods.reroll.config;

import com.smashingmods.reroll.Reroll;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConfigHandler {

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMON_SPEC = specPair.getRight();
    }

    public static class Common {

        public static ForgeConfigSpec.BooleanValue requireItem;
        public static ForgeConfigSpec.ConfigValue<List<? extends String>> rerollItems;
        public static ForgeConfigSpec.IntValue minDistance;
        public static ForgeConfigSpec.BooleanValue rerollAllTogether;
        public static ForgeConfigSpec.BooleanValue useCurrentDim;
        public static ForgeConfigSpec.BooleanValue useSpawnDim;
        public static ForgeConfigSpec.ConfigValue<String> overrideDim;
        public static ForgeConfigSpec.BooleanValue initialInventory;
        public static ForgeConfigSpec.BooleanValue setNewInventory;
        public static ForgeConfigSpec.BooleanValue resetEnderChest;
        public static ForgeConfigSpec.BooleanValue sendInventoryToChest;
        public static ForgeConfigSpec.BooleanValue rerollOnDeath;
        public static ForgeConfigSpec.BooleanValue startLocked;
        public static ForgeConfigSpec.IntValue cooldown;

        public Common(ForgeConfigSpec.Builder builder) {
            String seperator = System.getProperty("line.separator");
            builder.push("Reroll Command Configuration");
            requireItem = builder
                    .comment(seperator +
                            "  Require Item" + seperator +
                            "  Using Reroll requires Reroll Dice. Disables the /reroll command for non-OP players." +
                            seperator)
                    .define("requireItem", true);

            rerollItems = builder
                    .comment(seperator +
                            "  Reroll Items" + seperator +
                            "  A list of items that will be added to a player's inventory after using the reroll command." + seperator +
                            "  You can add any existing item per line like this: \"minecraft:torch;16\"." + seperator +
                            "  Note that you can only have as many items as there are inventory slots." +
                            seperator)
                    .defineList("rerollItems", Collections.emptyList(), configItem -> {
                        if (configItem instanceof String) {
                            String name = ((String) configItem).split(";")[0];
                            int count = Integer.parseInt(((String) configItem).split(";")[1]);
                            return ResourceLocation.tryParse(name) != null && count > 0 && count <= 64;
                        }
                        return false;
                    });

            minDistance = builder
                    .comment(seperator +
                            "  Minimum Distance" + seperator +
                            "  Determines the minimum distance to teleport when you reroll." +
                            seperator)
                    .defineInRange("minDistance", 512, 256, 10240);

            rerollAllTogether = builder
                    .comment(seperator +
                            "  Reroll All Together" + seperator +
                            "  Should '/reroll all' send all players to the same location?" +
                            seperator)
                    .define("rerollAllTogether", false);

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

            initialInventory = builder
                    .comment(seperator +
                            "  Initial Inventory" + seperator +
                            "  Should players get an initial inventory when they first join a world?" +
                            seperator)
                    .define("initialInventory", false);

            setNewInventory = builder
                    .comment(seperator +
                            "  Set New Inventory" + seperator +
                            "  Should players get an initial inventory every time they reroll?" +
                            seperator)
                    .define("setNewInventory", true);

            resetEnderChest = builder
                    .comment(seperator +
                            "  Reset Ender Chest" + seperator +
                            "  Should players have their ender chest cleared too?" +
                            seperator)
                    .define("resetEnderChest", true);

            sendInventoryToChest = builder
                    .comment(seperator +
                            "  Send Inventory to Chest" + seperator +
                            "  Should player inventory be dropped into a chest where they rerolled?" +
                            seperator)
                    .define("sendInventoryToChest", false);

            rerollOnDeath = builder
                    .comment(seperator +
                            "  Reroll On Death" + seperator +
                            "  Reroll players if they die to simulate a hardcore experience." +
                            seperator)
                    .define("rerollOnDeath", false);

            startLocked = builder
                    .comment(seperator +
                            "  Start Locked" + seperator +
                            "  This is a safety feature to lock the use of reroll at the start. Users are required to use /reroll unlock to use reroll just in case." +
                            seperator)
                    .define("startLocked", true);

            cooldown = builder
                    .comment(seperator +
                            "  Cooldown" + seperator +
                            "  Cooldown time in seconds to use reroll dice." +
                            seperator)
                    .defineInRange("cooldown", 1, 1, 60 * 60 * 24);

            builder.pop();
        }
    }
}
