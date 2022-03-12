package com.smashingmods.reroll.events;

import com.smashingmods.reroll.Reroll;
import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.util.InventoryHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class PlayerLoginEvent {

    public static String givenItems = Reroll.MODID + "_given_items";

    @SubscribeEvent
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer entityPlayer = event.player;
        NBTTagCompound entityData = event.player.getEntityData();
        NBTTagCompound tag;

        if (entityData == null || !entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            tag = new NBTTagCompound();
        } else {
            tag = entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        }

        if (!tag.getBoolean(givenItems)) {
            InventoryHandler.setInventory(entityPlayer, Config.rerollItems);
        }

        tag.setBoolean(givenItems, true);
        entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, tag);
    }
}
