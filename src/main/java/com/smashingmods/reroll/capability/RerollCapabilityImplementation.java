package com.smashingmods.reroll.capability;

import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class RerollCapabilityImplementation implements RerollCapabilityInterface {

    private static boolean ITEMS_RECEIVED = false;
    private static boolean LOCK = false;

    @Override
    public boolean getItemsReceived() {
        return ITEMS_RECEIVED;
    }

    @Override
    public void setItemsReceived(boolean itemsReceived) {
        ITEMS_RECEIVED = itemsReceived;
    }

    @Override
    public boolean getLock() {
        return LOCK;
    }

    @Override
    public void setLock(boolean lock) {
        LOCK = lock;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("recieved", ITEMS_RECEIVED);
        tag.setBoolean("locked", LOCK);
        return tag;
    }

    @Override
    public void deserializeNBT(@Nonnull NBTTagCompound nbt) {
        ITEMS_RECEIVED = nbt.getBoolean("received");
        LOCK = nbt.getBoolean("locked");
    }
}
