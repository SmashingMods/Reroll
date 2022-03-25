package com.smashingmods.reroll.block;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public enum GraveModel implements IStringSerializable {

    GRAVE_NORMAL("grave_normal");

    private final String name;

    GraveModel(String name) {
        this.name = name;
    }

    public static GraveModel getModel(int id) {
        return id >= 0 && id < values().length ? values()[id] : getDefault();
    }

    public static GraveModel getDefault() {
        return GRAVE_NORMAL;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    @Nonnull
    public String getSerializedName() {
        return name;
    }
}
