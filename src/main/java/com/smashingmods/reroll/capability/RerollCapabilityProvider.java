package com.smashingmods.reroll.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RerollCapabilityProvider implements ICapabilitySerializable<INBT> {

    private final RerollCapabilityImpl rerollCapability = new RerollCapabilityImpl();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == RerollCapability.REROLL_CAPABILITY) {
            return LazyOptional.of(() -> rerollCapability).cast();
        } else {
            return LazyOptional.empty();
        }
    }

    @Override
    public INBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        INBT rerollNBT = RerollCapability.REROLL_CAPABILITY.writeNBT(rerollCapability, null);
        if (rerollNBT != null) {
            tag.put("reroll", rerollNBT);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        INBT rerollNBT = tag.get("reroll");
        RerollCapability.REROLL_CAPABILITY.readNBT(rerollCapability, null, rerollNBT);
    }
}
