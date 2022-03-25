package com.smashingmods.reroll.event;

import com.smashingmods.reroll.block.BlockRegistry;
import com.smashingmods.reroll.block.GraveBlockTileEntity;
import com.smashingmods.reroll.config.ConfigHandler;
import com.smashingmods.reroll.handler.RerollHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LivingDeathEvent {

    @SubscribeEvent
    public static void onLivingDeathEvent(@Nonnull LivingDamageEvent event) {
        if (event.getEntity() instanceof PlayerEntity && ConfigHandler.Common.rerollOnDeath.get()) {
            RerollHandler handler = new RerollHandler();
            PlayerEntity player = (PlayerEntity) event.getEntity();
            float amount = event.getAmount();
            float health = player.getHealth();

            if (amount > health) {
                event.setCanceled(true);
                if (ConfigHandler.Common.sendInventoryToChest.get()) handleGrave(player);
                handler.reroll((ServerPlayerEntity) player, true);
                player.sendMessage(new TranslationTextComponent("reroll.death_event.player").withStyle(TextFormatting.RED), player.getUUID());

                MinecraftServer server = player.getServer();
                if (server != null && !server.isSingleplayer() && ConfigHandler.Common.broadcastDeath.get()) {
                    server.getPlayerList().broadcastMessage(new TranslationTextComponent("reroll.death_event.broadcast", player.getName()).withStyle(TextFormatting.RED), ChatType.SYSTEM, UUID.randomUUID());
                }
            }
        }
    }

    public static void handleGrave(PlayerEntity pPlayer) {
        World world = pPlayer.level;
        BlockPos pos = pPlayer.blockPosition();
        BlockState state = BlockRegistry.GRAVE_NORMAL.defaultBlockState();
        world.setBlock(pos, getFacingState(state, pPlayer.getYHeadRot()), 2);
        GraveBlockTileEntity graveBlockTileEntity = (GraveBlockTileEntity) world.getBlockEntity(pos);

        IItemHandler itemHandler = Objects.requireNonNull(graveBlockTileEntity).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElseThrow(() -> new IllegalStateException("Reroll grave inventory handler not present."));
        for (ItemStack itemStack : getItemList(pPlayer)) {
            ItemHandlerHelper.insertItemStacked(itemHandler, itemStack, false);
        }
        pPlayer.inventory.clearContent();
    }

    public static BlockState getFacingState(BlockState pBlockState, float rotation) {
        BlockState toReturn;

        if (rotation > 315 || rotation <= 45) {
            toReturn = pBlockState.setValue(HorizontalFaceBlock.FACING, Direction.SOUTH);
        } else if (rotation > 45 && rotation <= 135) {
            toReturn = pBlockState.setValue(HorizontalFaceBlock.FACING, Direction.WEST);
        } else if (rotation > 135 && rotation <= 225) {
            toReturn = pBlockState.setValue(HorizontalFaceBlock.FACING, Direction.NORTH);
        } else {
            toReturn = pBlockState.setValue(HorizontalFaceBlock.FACING, Direction.EAST);
        }
        return toReturn;
    }

    public static NonNullList<ItemStack> getItemList(PlayerEntity pPlayer) {
        NonNullList<ItemStack> toReturn = NonNullList.create();
        Iterator<ItemStack> itemIterator = pPlayer.inventory.items.iterator();
        Iterator<ItemStack> armorIterator = pPlayer.inventory.armor.iterator();
        Iterator<ItemStack> offhandIterator = pPlayer.inventory.offhand.iterator();

        while (itemIterator.hasNext()) {
            ItemStack itemStack = itemIterator.next();
            toReturn.add(itemStack);
        }
        while (armorIterator.hasNext()) {
            ItemStack itemStack = armorIterator.next();
            toReturn.add(itemStack);
        }
        while (offhandIterator.hasNext()) {
            ItemStack itemStack = offhandIterator.next();
            toReturn.add(itemStack);
        }

        return toReturn;
    }
}
