package com.smashingmods.reroll.events;

import com.smashingmods.reroll.handler.RerollHandler;
import com.smashingmods.reroll.item.DiceItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.smashingmods.reroll.handler.LockHandler.REROLL_LOCKED;
import static com.smashingmods.reroll.util.TagUtil.getTag;

public class UseItemEvent {

    @SubscribeEvent
    public void useItemServer(PlayerInteractEvent.RightClickItem event) {

        if (!event.getWorld().isRemote) {
            MinecraftServer server = event.getWorld().getMinecraftServer();
            EntityPlayer entityPlayer = event.getEntityPlayer();
            Item usedItem = event.getItemStack().getItem();
            RerollHandler handler = new RerollHandler();
            boolean locked = getTag(entityPlayer).getBoolean(REROLL_LOCKED);

            if (!locked && usedItem instanceof DiceItem) {
                handler.reroll(server, (EntityPlayerMP) entityPlayer, true, usedItem);
            } else if (locked && usedItem instanceof DiceItem) {
                entityPlayer.sendMessage(new TextComponentTranslation("commands.reroll.locked", entityPlayer.getName()).setStyle(new Style().setColor(TextFormatting.RED)));
            }
        }
    }
}
