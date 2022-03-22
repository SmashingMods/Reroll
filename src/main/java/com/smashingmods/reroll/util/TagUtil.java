//package com.smashingmods.reroll.util;
//
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.nbt.NBTTagCompound;
//
//public class TagUtil {
//    public static NBTTagCompound getTag(NBTTagCompound entityData) {
//        if (entityData == null || !entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
//            return new NBTTagCompound();
//        } else {
//            return entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
//        }
//    }
//
//    public static NBTTagCompound getTag(EntityPlayer entityPlayer) {
//        return getTag(entityPlayer.getEntityData());
//    }
//}
