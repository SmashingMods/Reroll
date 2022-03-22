package com.smashingmods.reroll.capability;

import com.smashingmods.reroll.Reroll;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    public  CompoundNBT save(@NotNull CompoundNBT pTag) {
        pTag.put("spiral", data);
        return pTag;
    }

    @NotNull
    public static WorldSavedData getDataForWorld(@NotNull ServerWorld world, String pName) {
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
