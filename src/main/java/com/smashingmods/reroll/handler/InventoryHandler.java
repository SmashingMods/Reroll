package com.smashingmods.reroll.handler;

import com.smashingmods.reroll.Reroll;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InventoryHandler {

    @SuppressWarnings("deprecation")
    public static void setInventory(EntityPlayerMP entityPlayer, String[] itemList) {
        List<ItemStack> items = new ArrayList<>();
        for (String startingItem : itemList) {
            String itemString = startingItem.split(";")[0];
            int count;
            try {
                count = Objects.requireNonNull(Item.REGISTRY.getObject(new ResourceLocation(itemString))).getItemStackLimit() == 1 ? 1 :  Integer.parseInt(startingItem.split(";")[1]);
                if (Item.REGISTRY.containsKey(new ResourceLocation(itemString))) {
                    items.add(new ItemStack(Objects.requireNonNull(Item.getByNameOrId(itemString)), count));
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

        if (items.size() > 0) {
            items.forEach(item -> ItemHandlerHelper.giveItemToPlayer(entityPlayer, item));
        }
    }
}
