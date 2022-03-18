package com.smashingmods.reroll.events;

import com.smashingmods.reroll.Reroll;
import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.handler.InventoryHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import static com.smashingmods.reroll.handler.LockHandler.REROLL_LOCKED;
import static com.smashingmods.reroll.handler.LockHandler.USER_UNLOCKED;
import static com.smashingmods.reroll.util.TagUtil.getTag;

public class PlayerLoginEvent {

    public static String REROLL_ITEMS = Reroll.MODID + "_starting_items";

    @SubscribeEvent
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer entityPlayer = event.player;
        NBTTagCompound entityData = event.player.getEntityData();
        NBTTagCompound tag = getTag(entityData);

        if (!tag.getBoolean(REROLL_ITEMS)) {
            InventoryHandler.setInventory(entityPlayer, Config.rerollItems);
        }

        if (Config.startLocked) {
            if (!tag.getBoolean(USER_UNLOCKED)) tag.setBoolean(REROLL_LOCKED, true);
        }

        tag.setBoolean(REROLL_ITEMS, true);
        entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, tag);
    }
}
