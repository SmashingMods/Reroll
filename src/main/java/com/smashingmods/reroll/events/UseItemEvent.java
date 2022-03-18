package com.smashingmods.reroll.events;

import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.item.DiceItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.CooldownTracker;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.smashingmods.reroll.handler.LockHandler.REROLL_LOCKED;
import static com.smashingmods.reroll.util.TagUtil.getTag;

public class UseItemEvent {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void useItem(PlayerInteractEvent.RightClickItem event) {

        if (event.getWorld().isRemote) {
            EntityPlayer entityPlayer = event.getEntityPlayer();
            Item usedItem = event.getItemStack().getItem();
            boolean locked = getTag(entityPlayer).getBoolean(REROLL_LOCKED);
            if (!locked && usedItem instanceof DiceItem) {
                CooldownTracker tracker = entityPlayer.getCooldownTracker();
                Minecraft.getMinecraft().player.sendChatMessage("/reroll dice tPQi3mO5$!EX5m7Tn@0#&tfMSv#ZcG$c");
                tracker.setCooldown(usedItem, Config.cooldown * 20);
            }
        }
    }
}
