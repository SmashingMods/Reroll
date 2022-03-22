package com.smashingmods.reroll.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LockCapabilityProvider implements ICapabilitySerializable<INBT> {

    private final LockCapabilityImplementation lockCapability = new LockCapabilityImplementation();

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return (LazyOptional<T>) LazyOptional.of(() -> lockCapability);
    }

    @Override
    public INBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        INBT lockNBT = LockCapability.CAPABILITY_LOCK.writeNBT(lockCapability, null);
        tag.put("reroll_lock", lockNBT);
        return tag;
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        INBT lockNBT = tag.get("reroll_lock");
        LockCapability.CAPABILITY_LOCK.readNBT(lockCapability, null, lockNBT);
    }
}
