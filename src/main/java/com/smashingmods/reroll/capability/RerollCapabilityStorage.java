package com.smashingmods.reroll.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RerollCapabilityStorage implements Capability.IStorage<RerollCapabilityInterface> {

    private final String received = "recieved";
    private final String locked = "locked";

    @Nullable
    @Override
    public INBT writeNBT(Capability<RerollCapabilityInterface> capability, @NotNull RerollCapabilityInterface instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag.putBoolean(received, instance.getItemsReceived());
        tag.putBoolean(locked, instance.getLock());
        return tag;
    }

    @Override
    public void readNBT(Capability<RerollCapabilityInterface> capability, RerollCapabilityInterface instance, Direction side, @NotNull INBT nbt) {

        boolean itemsReceived = false;
        boolean lock = false;

        if (nbt.getType() == CompoundNBT.TYPE) {
            itemsReceived = ((CompoundNBT) nbt).getBoolean(received);
            lock = ((CompoundNBT) nbt).getBoolean(locked);
        }
        instance.setItemsReceived(itemsReceived);
        instance.setLock(lock);
    }
}
