package com.smashingmods.reroll.capability;

import com.smashingmods.reroll.Reroll;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;

import javax.annotation.Nonnull;
import java.util.Objects;

public class WorldSavedData extends net.minecraft.world.storage.WorldSavedData {

    private static final String DATA_NAME = Reroll.MODID;
    private NBTTagCompound DATA = new NBTTagCompound();

    public WorldSavedData() {
        super(DATA_NAME);
    }

    @SuppressWarnings("unused")
    public WorldSavedData(String pName) {
        super(pName);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        DATA = tag.getCompoundTag("spiral");
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setTag("spiral", DATA);
        return tag;
    }

    @Nonnull
    public static WorldSavedData getDataForWorld(@Nonnull World world, String pName) {
        MapStorage storage = world.getPerWorldStorage();
        WorldSavedData instance = (WorldSavedData) Objects.requireNonNull(storage).getOrLoadData(WorldSavedData.class, DATA_NAME);

        if (instance == null) {
            instance = new WorldSavedData();
            storage.setData(pName, instance);
        }

        return instance;
    }

    public NBTTagCompound getDATA() {
        return DATA;
    }

    public void setDATA(NBTTagCompound pTag) {
        this.DATA = pTag;
    }
}
