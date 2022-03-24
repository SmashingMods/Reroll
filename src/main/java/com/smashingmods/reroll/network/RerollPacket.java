package com.smashingmods.reroll.network;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class RerollPacket {

    private final ItemStack itemStack;

    public RerollPacket(ItemStack item) {
        this.itemStack = item;
    }

    public RerollPacket(PacketBuffer buffer) {
        this.itemStack = buffer.readItem();
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeItem(itemStack);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
