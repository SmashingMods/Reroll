package com.smashingmods.reroll.handler;

import com.smashingmods.reroll.block.BlockRegistry;
import com.smashingmods.reroll.block.GraveBlockTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Iterator;
import java.util.Objects;

public class GraveHandler {

    public static void handleGrave(PlayerEntity pPlayer) {
        World world = pPlayer.level;
        BlockPos pos = pPlayer.blockPosition();
        BlockState state = BlockRegistry.GRAVE_NORMAL.get().defaultBlockState();
        world.setBlock(pos, getFacingState(state, pPlayer.getViewYRot(0.0F)), 2);
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
        Item dice = ForgeRegistries.ITEMS.getValue(new ResourceLocation("reroll:dice"));

        while (itemIterator.hasNext()) {
            ItemStack itemStack = itemIterator.next();
            if (!itemStack.getItem().equals(dice)) {
                toReturn.add(itemStack);
            }
        }
        while (armorIterator.hasNext()) {
            ItemStack itemStack = armorIterator.next();
            if (!itemStack.getItem().equals(dice)) {
                toReturn.add(itemStack);
            }
        }
        while (offhandIterator.hasNext()) {
            ItemStack itemStack = offhandIterator.next();
            if (!itemStack.getItem().equals(dice)) {
                toReturn.add(itemStack);
            }
        }

        return toReturn;
    }
}
