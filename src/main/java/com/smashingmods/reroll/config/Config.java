package com.smashingmods.reroll.config;

import com.smashingmods.reroll.Reroll;
import net.minecraftforge.common.config.Configuration;

public class Config {
    private static final String CATEGORY_REROLL = "reroll";
    private static final String CATEGORY_COMPAT = "compat";

    public static boolean requireItem;
    public static String[] rerollItems;
    public static int minDistance;
    public static int maxTries;
    public static int horizontalRadius;
    public static int verticalRadius;
    public static boolean rerollAllTogether;
    public static boolean useCurrentDim;
    public static boolean useOverrideDim;
    public static int overrideDim;
    public static boolean initialInventory;
    public static boolean setNewInventory;
    public static boolean resetEnderChest;
    public static boolean sendInventoryToChest;
    public static boolean rerollOnDeath;
    public static boolean startLocked;
    public static int cooldown;
    public static String[] potentialSpawnBlocks;
    public static String[] additionalCommands;

    public static int timeisupTimer;

    public static void readConfig() {
        Configuration config = Reroll.CONFIG;
        try {
            config.load();
            initRerollConfig(config);
        } catch (Exception e) {
            com.smashingmods.reroll.Reroll.LOGGER.error("Problem loading config file:\n", e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    private static void initRerollConfig(Configuration config) {
        config.addCustomCategoryComment(CATEGORY_REROLL, "Reroll Command Configuration");
        requireItem = config.getBoolean("Require Dice", CATEGORY_REROLL, true, "Using Reroll requires Reroll Dice. Disables the /reroll command for non-OP players.");
        rerollItems = config.getStringList("Reroll Inventory", CATEGORY_REROLL, new String[]{"reroll:dice;1"}, "A list of items that will be added to a player's inventory after using the reroll command.\nYou can add any existing item per line like this: \"minecraft:torch;16\".\nNote that you can only have as many items as there are inventory slots.");
        minDistance = config.getInt("Reroll Minimum Distance", CATEGORY_REROLL, 512, 256, 10240, "Determines the minimum distance to teleport when you reroll.");
        maxTries = config.getInt("Max Tries", CATEGORY_REROLL, 5, 1, 10, "Maximum number of times to try to find a valid block position for reroll. Rerolling again will reset the count and start from the next reroll position.");
        horizontalRadius = config.getInt("Reroll Horizontal Radius", CATEGORY_REROLL, 256, 16, 2048, "Horizontal radius to search for a reroll position. Larger numbers might cause more lag.");
        verticalRadius = config.getInt("Reroll Vertical Radius", CATEGORY_REROLL, 64, 16, 256, "Vertical radius to search for a reroll position. Larger numbers might cause more lag.");
        rerollAllTogether = config.getBoolean("Reroll All Together", CATEGORY_REROLL, false, "Should '/reroll all' send all players to the same location?");
        useOverrideDim = config.getBoolean("Use Override Dimension", CATEGORY_REROLL, false, "Should reroll spawn location be set to the override dimension? If false, defaults to player spawn dimension.");
        useCurrentDim = config.getBoolean("Use Current Dimension", CATEGORY_REROLL, false, "Should reroll spawn location be set in the player's current dimension? If false, defaults to player spawn dimension.");
        overrideDim = config.getInt("Override Dimension", CATEGORY_REROLL, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, "Override the dimension used for the reroll spawn location");
        initialInventory = config.getBoolean("Initial Inventory", CATEGORY_REROLL, true, "Should players get an initial inventory when they first join a world?");
        setNewInventory = config.getBoolean("Initial Inventory per Reroll", CATEGORY_REROLL, true, "Should players get an initial inventory every time they reroll?");
        resetEnderChest = config.getBoolean("Reset Ender Chest", CATEGORY_REROLL, true, "Should players have their ender chest cleared too?");
        sendInventoryToChest = config.getBoolean("Send to Chest", CATEGORY_REROLL, false, "Should player inventory be dropped into a chest where they rerolled?");
        rerollOnDeath = config.getBoolean("Reroll On Death [Hardcore Mode]", CATEGORY_REROLL, false, "Reroll players if they die to simulate a hardcore experience.");
        startLocked = config.getBoolean("Lock Reroll", CATEGORY_REROLL, true, "This is a safety feature to lock the use of reroll at the start. Users are required to use /reroll unlock to use reroll just in case.");
        cooldown = config.getInt("Reroll Cooldown", CATEGORY_REROLL, 60, 30, Integer.MAX_VALUE, "Cooldown time to use reroll dice.");
        potentialSpawnBlocks = config.getStringList("Block Spawn", CATEGORY_REROLL, new String[] { "minecraft:grass", "minecraft:dirt", "minecraft:stone" }, "Sets the resource location for the block you can spawn on.");
        additionalCommands = config.getStringList("Additional Commands", CATEGORY_REROLL, new String[] {"advancement revoke @p everything"}, "You can use most valid commands. '@p' will replace wil the player's name. You can also use format strings. Examples: 'say hello world', 'advancement revoke @p everything', 'say hello %s, you are %s!;playername;amazing'");

        config.addCustomCategoryComment(CATEGORY_COMPAT, "Mod Compatibility Configuration");
        timeisupTimer = config.getInt("Time is up Timer", CATEGORY_COMPAT, 12000, 1200, Integer.MAX_VALUE, "Set the Timer value after a reroll.");
    }
}
