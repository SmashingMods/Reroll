package com.smashingmods.reroll.model;

import net.minecraft.nbt.NBTTagCompound;

public class Spiral {

    private NBTTagCompound spiral;

    public Spiral() {
        spiral = new NBTTagCompound();
        spiral.setInteger("posX", 0);
        spiral.setInteger("posZ", 0);
        spiral.setInteger("deltaX", 0);
        spiral.setInteger("deltaZ", -1);
    }

    public NBTTagCompound getSpiral() {
        return spiral;
    }

    public void setSpiral(NBTTagCompound pNbt) {
        this.spiral = pNbt;
    }

    public NBTTagCompound next() {
        int posX = getPosX();
        int posZ = getPosZ();
        int deltaX = getDeltaX();
        int deltaZ = getDeltaZ();

        if (posX == posZ || posX < 0 && posX == -posZ || posX > 0 && posX == 1 - posZ) {
            int rotate = deltaX;
            deltaX = -deltaZ;
            deltaZ = rotate;
        }
        posX += deltaX;
        posZ += deltaZ;

        NBTTagCompound newSpiral = new NBTTagCompound();

        newSpiral.setInteger("posX", posX);
        newSpiral.setInteger("posZ", posZ);
        newSpiral.setInteger("deltaX", deltaX);
        newSpiral.setInteger("deltaZ", deltaZ);

        return newSpiral;
    }

    public void setNext() {
        spiral = next();
    }

    public int getPosX() {
        return spiral.getInteger("posX");
    }

    public int getPosZ() {
        return spiral.getInteger("posZ");
    }

    public int getDeltaX() {
        return spiral.getInteger("deltaX");
    }

    public int getDeltaZ() {
        return spiral.getInteger("deltaZ");
    }
}
