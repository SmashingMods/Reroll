package com.smashingmods.reroll.model;

import net.minecraft.nbt.CompoundNBT;

public class Spiral {

    private CompoundNBT spiral;

    public Spiral() {
        spiral = new CompoundNBT();
        spiral.putInt("posX", 0);
        spiral.putInt("posZ", 0);
        spiral.putInt("deltaX", 0);
        spiral.putInt("deltaZ", -1);
    }

    public CompoundNBT getSpiral() {
        return spiral;
    }

    public void setSpiral(CompoundNBT pNbt) {
        this.spiral = pNbt;
    }

    public CompoundNBT next() {
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

        CompoundNBT newSpiral = new CompoundNBT();

        newSpiral.putInt("posX", posX);
        newSpiral.putInt("posZ", posZ);
        newSpiral.putInt("deltaX", deltaX);
        newSpiral.putInt("deltaZ", deltaZ);

        return newSpiral;
    }

    public void setNext() {
        spiral = next();
    }

    public int getPosX() {
        return spiral.getInt("posX");
    }

    public int getPosZ() {
        return spiral.getInt("posZ");
    }

    public int getDeltaX() {
        return spiral.getInt("deltaX");
    }

    public int getDeltaZ() {
        return spiral.getInt("deltaZ");
    }
}
