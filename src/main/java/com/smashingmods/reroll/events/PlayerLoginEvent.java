package com.smashingmods.reroll.events;

import com.smashingmods.reroll.capability.RerollCapability;
import com.smashingmods.reroll.capability.RerollCapabilityImplementation;
import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.handler.InventoryHandler;
import com.smashingmods.reroll.item.DiceItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;
import java.util.Optional;

public class PlayerLoginEvent {

    @SubscribeEvent
    public static void playerLogin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP entityPlayer = (EntityPlayerMP) event.getEntity();
            setInitialCooldown(entityPlayer);

            RerollCapabilityImplementation rerollCapability = entityPlayer.getCapability(RerollCapability.REROLL_CAPABILITY, null);
            Objects.requireNonNull(rerollCapability);
            boolean itemsReceived = rerollCapability.getItemsReceived();

            if (!itemsReceived) {
                if (Config.initialInventory) {
                    InventoryHandler.setInventory(entityPlayer, Config.rerollItems);
                    rerollCapability.setItemsReceived(true);
                }
                if (Config.startLocked) {
                    rerollCapability.setLock(true);
                }
            }
        }
    }

    private static void setInitialCooldown(EntityPlayerMP entityPlayer) {
        CooldownTracker tracker = entityPlayer.getCooldownTracker();
        NonNullList<ItemStack> inventory = NonNullList.create();
        inventory.addAll(entityPlayer.inventory.mainInventory);
        inventory.addAll(entityPlayer.inventory.offHandInventory);
        inventory.addAll(entityPlayer.inventory.armorInventory);
        Optional<Item> dice = inventory.stream().map(ItemStack::getItem).filter(item -> item instanceof DiceItem).findFirst();
        dice.ifPresent(item -> tracker.setCooldown(item, Config.cooldown * 20));
    }
}
