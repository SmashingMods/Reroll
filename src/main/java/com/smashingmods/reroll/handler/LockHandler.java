package com.smashingmods.reroll.handler;

import com.smashingmods.reroll.Reroll;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import static com.smashingmods.reroll.util.TagUtil.getTag;

public class LockHandler {

    public static String REROLL_LOCKED = Reroll.MODID + "_reroll_locked";

    public static void lockReroll(EntityPlayer entityPlayer) throws PlayerNotFoundException {
        NBTTagCompound tag = getTag(entityPlayer);

        if (!tag.getBoolean(REROLL_LOCKED)) {
            tag.setBoolean(REROLL_LOCKED, true);
            entityPlayer.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, tag);
        }
    }

    public static void unlockReroll(EntityPlayer entityPlayer) throws PlayerNotFoundException {
        NBTTagCompound tag = getTag(entityPlayer);

        tag.setBoolean(REROLL_LOCKED, false);
        entityPlayer.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, tag);
    }
}
