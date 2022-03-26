package com.smashingmods.reroll.event;

import com.smashingmods.reroll.config.ConfigHandler;
import com.smashingmods.reroll.handler.GraveHandler;
import com.smashingmods.reroll.handler.RerollHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerDeathEvent {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDeathEvent(@Nonnull LivingDamageEvent event) {

        if ((event.getEntity() instanceof ServerPlayerEntity) && (ConfigHandler.Common.rerollOnDeath.get() || ConfigHandler.Common.createGraveOnDeath.get())) {

            RerollHandler handler = new RerollHandler();
            PlayerEntity player = (PlayerEntity) event.getEntity();
            float amount = event.getAmount();
            float health = player.getHealth();

            ItemStack selected = player.inventory.getSelected();
            ItemStack offhand = player.inventory.offhand.get(0);
            Item totem = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:totem_of_undying"));

            if (amount > health) {
                //noinspection StatementWithEmptyBody
                if (offhand.getItem().equals(totem) || selected.getItem().equals(totem)) {
                    /* If I flip the if or use !, it fails. Minecraft being Minecraft */
                } else {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);

                    if (ConfigHandler.Common.createGraveOnDeath.get()) GraveHandler.handleGrave(player);

                    if (ConfigHandler.Common.rerollOnDeath.get()) {
                        handler.reroll((ServerPlayerEntity) player, true);
                        player.sendMessage(new TranslationTextComponent("reroll.death_event.player").withStyle(TextFormatting.RED), player.getUUID());

                        MinecraftServer server = player.getServer();
                        if (server != null && !server.isSingleplayer() && ConfigHandler.Common.broadcastDeath.get()) {
                            server.getPlayerList().broadcastMessage(new TranslationTextComponent("reroll.death_event.broadcast", player.getName()).withStyle(TextFormatting.RED), ChatType.SYSTEM, UUID.randomUUID());
                        }
                    }
                }
            }
        }
    }
}
