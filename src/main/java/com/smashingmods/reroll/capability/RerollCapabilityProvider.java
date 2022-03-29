package com.smashingmods.reroll.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RerollCapabilityProvider implements ICapabilitySerializable<NBTBase> {

    private final RerollCapabilityImplementation rerollCapability = new RerollCapabilityImplementation();

    @Override
    public NBTBase serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTBase rerollNBT = RerollCapability.REROLL_CAPABILITY.writeNBT(rerollCapability, null);
        if (rerollNBT != null) {
            tag.setTag("reroll", rerollNBT);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        NBTTagCompound tag = (NBTTagCompound) nbt;
        NBTBase rerollNBT = tag.getCompoundTag("reroll");
        RerollCapability.REROLL_CAPABILITY.readNBT(rerollCapability, null, rerollNBT);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == RerollCapability.REROLL_CAPABILITY;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == RerollCapability.REROLL_CAPABILITY) {
            return (T) rerollCapability;
        }
        return null;
    }
}
