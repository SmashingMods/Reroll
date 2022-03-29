package com.smashingmods.reroll.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RerollCapabilityStorage implements Capability.IStorage<RerollCapabilityInterface> {

    private final String received = "recieved";
    private final String locked = "locked";

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<RerollCapabilityInterface> capability, @Nonnull RerollCapabilityInterface instance, EnumFacing side) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean(received, instance.getItemsReceived());
        tag.setBoolean(locked, instance.getLock());
        return tag;
    }

    @Override
    public void readNBT(Capability<RerollCapabilityInterface> capability, RerollCapabilityInterface instance, EnumFacing side, @Nonnull NBTBase nbt) {

        boolean itemsReceived = false;
        boolean lock = false;

        if (nbt instanceof NBTTagCompound) {
            itemsReceived = ((NBTTagCompound) nbt).getBoolean(received);
            lock = ((NBTTagCompound) nbt).getBoolean(locked);
        }
        instance.setItemsReceived(itemsReceived);
        instance.setLock(lock);
    }
}
