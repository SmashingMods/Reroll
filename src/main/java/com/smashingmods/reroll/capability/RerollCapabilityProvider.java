package com.smashingmods.reroll.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RerollCapabilityProvider implements ICapabilitySerializable<INBT> {

    private final RerollCapabilityImplementation rerollCapability = new RerollCapabilityImplementation();

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return (LazyOptional<T>) LazyOptional.of(() -> rerollCapability);
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
