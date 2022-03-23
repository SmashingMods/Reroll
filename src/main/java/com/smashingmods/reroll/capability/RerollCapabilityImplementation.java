package com.smashingmods.reroll.capability;

import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.NotNull;

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
    public CompoundNBT serializeNBT() {
        final CompoundNBT tag = new CompoundNBT();
        tag.putBoolean("recieved", ITEMS_RECEIVED);
        tag.putBoolean("locked", LOCK);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundNBT nbt) {
        ITEMS_RECEIVED = nbt.getBoolean("received");
        LOCK = nbt.getBoolean("locked");
    }
}
