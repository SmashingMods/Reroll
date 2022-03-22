package com.smashingmods.reroll.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface LockCapabilityInterface extends INBTSerializable<CompoundNBT> {
    boolean getLock();
    void setLock(boolean lock);
    String getTagName();
}
