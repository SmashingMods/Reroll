package com.smashingmods.reroll.capability;

import com.smashingmods.reroll.Reroll;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;

import javax.annotation.Nonnull;

public class WorldSavedData extends net.minecraft.world.storage.WorldSavedData {

    private static String tagName;
    private CompoundNBT data = new CompoundNBT();

    @SuppressWarnings("unused")
    public WorldSavedData() {
        super(Reroll.MODID);
    }

    public WorldSavedData(String pName) {
        super(Reroll.MODID);
        tagName = pName;
    }

    @Override
    public void load(CompoundNBT pTag) {
        data = pTag.getCompound(tagName);
    }

    @Override
    @Nonnull
    public  CompoundNBT save(@Nonnull CompoundNBT pTag) {
        pTag.put("spiral", data);
        return pTag;
    }

    @Nonnull
    public static WorldSavedData getDataForWorld(@Nonnull ServerWorld world, String pName) {
        DimensionSavedDataManager storage = world.getDataStorage();
        return storage.computeIfAbsent(() -> new WorldSavedData(pName), Reroll.MODID);
    }

    public CompoundNBT getData() {
        return data;
    }

    public void setData(CompoundNBT pTag) {
        this.data = pTag;
    }
}
