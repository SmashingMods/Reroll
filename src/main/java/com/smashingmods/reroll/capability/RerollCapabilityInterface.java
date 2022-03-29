package com.smashingmods.reroll.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface RerollCapabilityInterface extends INBTSerializable<NBTTagCompound> {
    boolean getLock();
    void setLock(boolean lock);
    boolean getItemsReceived();
    void setItemsReceived(boolean itemsReceived);
}
