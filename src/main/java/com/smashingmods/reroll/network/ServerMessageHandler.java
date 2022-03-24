package com.smashingmods.reroll.network;

import com.smashingmods.reroll.handler.RerollHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class ServerMessageHandler {

    public static void handle(RerollPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity sender = context.get().getSender();
            RerollHandler handler = new RerollHandler();
            CooldownTracker tracker = sender.getCooldowns();
            Item item = message.getItemStack().getItem();

            if (tracker.isOnCooldown(item)) {
                Objects.requireNonNull(sender.getServer()).sendMessage(new TranslationTextComponent("reroll.dice.cooldown"), sender.getUUID());
            } else {
                handler.reroll(sender, true);
                tracker.addCooldown(item, 10 * 20);
            }
        });
        context.get().setPacketHandled(true);
    }
}
