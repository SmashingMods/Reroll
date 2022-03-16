package com.smashingmods.reroll.model;

public class SpiralObject {
    private int posX = 0;
    private int posZ = 0;
    private int deltaX = 0;
    private int deltaZ = -1;

    public SpiralObject() {}

    public SpiralObject(int posX, int posZ, int deltaX, int deltaZ) {
        this.posX = posX;
        this.posZ = posZ;
        this.deltaX = deltaX;
        this.deltaZ = deltaZ;
    }

    public int[] next() {
        if (posX == posZ || posX < 0 && posX == -posZ || posX > 0 && posX == 1 - posZ) {
            int t = deltaX;
            deltaX = -deltaZ;
            deltaZ = t;
        }
        posX += deltaX;
        posZ += deltaZ;
        return new int[] { posX, posZ, deltaX, deltaZ };
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosZ() {
        return posZ;
    }

    public void setPosZ(int posZ) {
        this.posZ = posZ;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(int deltaX) {
        this.deltaX = deltaX;
    }

    public int getDeltaZ() {
        return deltaZ;
    }

    public void setDeltaZ(int deltaZ) {
        this.deltaZ = deltaZ;
    }

    public void setSpiral(SpiralObject spiral) {
        this.posX = spiral.getPosX();
        this.posZ = spiral.getPosZ();
        this.deltaX = spiral.getDeltaX();
        this.deltaZ = spiral.getDeltaZ();
    }
}
