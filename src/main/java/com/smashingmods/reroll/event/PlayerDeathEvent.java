package com.smashingmods.reroll.event;

import com.smashingmods.reroll.capability.IRerollCapability;
import com.smashingmods.reroll.capability.RerollCapability;
import com.smashingmods.reroll.config.ConfigHandler;
import com.smashingmods.reroll.handler.GraveHandler;
import com.smashingmods.reroll.handler.RerollHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerDeathEvent {

    @SubscribeEvent
    public static void onPlayerCloneEvent(@Nonnull PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            PlayerEntity original = event.getOriginal();
            PlayerEntity cloned = event.getPlayer();

            AtomicBoolean lock = new AtomicBoolean(false);
            AtomicBoolean itemsReceived = new AtomicBoolean(false);

            LazyOptional<IRerollCapability> originalCapability = original.getCapability(RerollCapability.REROLL_CAPABILITY, null);
            LazyOptional<IRerollCapability> clonedCapability = cloned.getCapability(RerollCapability.REROLL_CAPABILITY, null);

            originalCapability.ifPresent(cap -> {
                lock.set(cap.getLock());
                itemsReceived.set(cap.getItemsReceived());
            });

            clonedCapability.ifPresent(cap -> {
                cap.setLock(lock.get());
                cap.setItemsReceived(itemsReceived.get());
            });

            RerollHandler handler = new RerollHandler();

            if (ConfigHandler.Common.rerollOnDeath.get()) {
                handler.reroll((ServerPlayerEntity) cloned, true);
                cloned.sendMessage(new TranslationTextComponent("reroll.death_event.player").withStyle(TextFormatting.RED), cloned.getUUID());

                MinecraftServer server = cloned.getServer();
                if (server != null && !server.isSingleplayer() && ConfigHandler.Common.broadcastDeath.get()) {
                    server.getPlayerList().broadcastMessage(new TranslationTextComponent("reroll.death_event.broadcast", cloned.getName()).withStyle(TextFormatting.RED), ChatType.SYSTEM, UUID.randomUUID());
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDeathEvent(@Nonnull LivingDamageEvent event) {

        if (event.getEntity() instanceof ServerPlayerEntity && ConfigHandler.Common.createGraveOnDeath.get()) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            float amount = event.getAmount();
            float health = player.getHealth();

            if (amount > health && ConfigHandler.Common.createGraveOnDeath.get()) {
                GraveHandler.handleGrave(player);
            }
        }
    }
}
