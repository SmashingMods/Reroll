package com.smashingmods.reroll.handler;

import com.smashingmods.reroll.Reroll;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class InventoryHandler {
    public static void setInventory(EntityPlayer entityPlayer, String[] itemList) {
        List<ItemStack> items = new ArrayList<>();
        for (String startingItem : itemList) {
            String itemString = startingItem.split(";")[0];
            int count;
            try {
                count = Item.REGISTRY.getObject(new ResourceLocation(itemString)).getItemStackLimit() == 1 ? 1 :  Integer.parseInt(startingItem.split(";")[1]);
                if (Item.REGISTRY.containsKey(new ResourceLocation(itemString))) {
                    items.add(new ItemStack(Item.getByNameOrId(itemString), count));
                }
                else {
                    Reroll.LOGGER.error(itemString + " isn't a valid registered Item. Check the config file for errors.");
                }
            } catch (NumberFormatException e) {
                Reroll.LOGGER.error(itemString + "value isn't set to an integer: " + e);
            } catch (NullPointerException e) {
                Reroll.LOGGER.error("Invalid item entry in Reroll Inventory config: " + itemString);
            }
        }

        items.forEach(item -> {
            ItemHandlerHelper.giveItemToPlayer(entityPlayer, item);
        });
    }
}
