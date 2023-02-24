package com.smashingmods.reroll.events;

import com.smashingmods.reroll.Reroll;
import com.smashingmods.reroll.capability.RerollCapability;
import com.smashingmods.reroll.capability.RerollCapabilityImplementation;
import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.handler.InventoryHandler;
import com.smashingmods.reroll.handler.RerollHandler;
import com.smashingmods.reroll.item.DiceItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.Objects;
import java.util.Optional;

public class PlayerLoginEvent {

    @SubscribeEvent
    public static void playerFirstLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player == null) return;

        // Player check: Missing the "joined" tag? Must be first login
        if (!event.player.getTags().contains("joined")) {
            Reroll.LOGGER.info("Player {} joined the world for the first time!", event.player.getName());

            // Set tag to prevent initial settings every login
            event.player.getTags().add("joined");

            if (event.player instanceof EntityPlayerMP) {
                EntityPlayerMP entityPlayer = (EntityPlayerMP) event.player;

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

                RerollHandler handler = new RerollHandler();
                handler.resetLocation(entityPlayer.getServer(), entityPlayer, true);
                handler.resetLocation(entityPlayer.getServer(), entityPlayer, true);
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
