package com.smashingmods.reroll.capability;

import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class RerollCapabilityImplementation implements RerollCapabilityInterface {

    private boolean itemsReceived = false;
    private boolean locked = false;

    @Override
    public boolean getItemsReceived() {
        return itemsReceived;
    }

    @Override
    public void setItemsReceived(boolean itemsReceived) {
        this.itemsReceived = itemsReceived;
    }

    @Override
    public boolean getLock() {
        return locked;
    }

    @Override
    public void setLock(boolean lock) {
        locked = lock;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("recieved", itemsReceived);
        tag.setBoolean("locked", locked);
        return tag;
    }

    @Override
    public void deserializeNBT(@Nonnull NBTTagCompound nbt) {
        itemsReceived = nbt.getBoolean("received");
        locked = nbt.getBoolean("locked");
    }
}
