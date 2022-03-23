package com.smashingmods.reroll.util;

import com.smashingmods.reroll.config.ConfigHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class RerollUtilities {
    public static void setInventory(PlayerEntity pPlayer) {
        for (String configString : ConfigHandler.Common.rerollItems.get()) {
            String itemName = configString.split(";")[0];
            int count = Integer.parseInt(configString.split(";")[1]);
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
            ItemStack itemStack = new ItemStack(item, count);
            pPlayer.inventory.add(itemStack);
        }
    }
}
