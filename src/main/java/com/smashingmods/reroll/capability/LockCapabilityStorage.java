package com.smashingmods.reroll.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LockCapabilityStorage implements Capability.IStorage<LockCapabilityInterface> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<LockCapabilityInterface> capability, @NotNull LockCapabilityInterface instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag.putBoolean(instance.getTagName(), instance.getLock());
        return tag;
    }

    @Override
    public void readNBT(Capability<LockCapabilityInterface> capability, LockCapabilityInterface instance, Direction side, @NotNull INBT nbt) {

        boolean lock = false;

        if (nbt.getType() == CompoundNBT.TYPE) {
            lock = ((CompoundNBT) nbt).getBoolean(instance.getTagName());
        }
        instance.setLock(lock);
    }
}
