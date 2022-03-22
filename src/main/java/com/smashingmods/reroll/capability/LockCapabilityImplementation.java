package com.smashingmods.reroll.capability;

import com.smashingmods.reroll.Reroll;
import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.NotNull;

public class LockCapabilityImplementation implements LockCapabilityInterface {

    private static final String TAG_NAME = Reroll.MODID + "_locked";
    private static boolean LOCK = false;

    @Override
    public boolean getLock() {
        return LOCK;
    }

    @Override
    public void setLock(boolean lock) {
        LOCK = lock;
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    public CompoundNBT serializeNBT() {
        final CompoundNBT tag = new CompoundNBT();
        tag.putBoolean(TAG_NAME, LOCK);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundNBT nbt) {
        LOCK = nbt.getBoolean(TAG_NAME);
    }
}
