package com.smashingmods.reroll.handler;

import net.minecraftforge.fml.ModList;

public class ModCompatibilityHandler {

    public static boolean CURIOS_LOADED;

    public static void register() {
        ModList modList = ModList.get();
        CURIOS_LOADED = modList.isLoaded("curios");
    }
}
