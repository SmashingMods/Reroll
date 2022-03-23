package com.smashingmods.reroll.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface RerollCapabilityInterface extends INBTSerializable<CompoundNBT> {
    boolean getLock();
    void setLock(boolean lock);
    boolean getItemsReceived();
    void setItemsReceived(boolean itemsReceived);
}
