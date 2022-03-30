package com.smashingmods.reroll.handler;

import com.smashingmods.reroll.block.BlockRegistry;
import com.smashingmods.reroll.block.GraveTileEntity;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.util.ICuriosHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.smashingmods.reroll.handler.ModCompatibilityHandler.CURIOS_LOADED;

public class GraveHandler {

    public static void handleGrave(PlayerEntity pPlayer) {
        World world = pPlayer.level;
        BlockPos pos = pPlayer.blockPosition();
        BlockState state = BlockRegistry.GRAVE_NORMAL.get().defaultBlockState();
        world.setBlock(pos, getFacingState(state, pPlayer.getViewYRot(0.0F)), 2);
        GraveTileEntity graveBlockTileEntity = (GraveTileEntity) world.getBlockEntity(pos);

        LazyOptional<IItemHandler> itemHandler = Objects.requireNonNull(graveBlockTileEntity).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        itemHandler.ifPresent(handler -> {
            for (ItemStack itemStack : getItemList(pPlayer)) {
                ItemHandlerHelper.insertItemStacked(handler, itemStack, false);
            }
        });
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
        if (CURIOS_LOADED) {
            for (ItemStack itemStack : getCuriosIfPresent(pPlayer)) {
                //noinspection UseBulkOperation
                toReturn.add(itemStack);
            }
        }
        return toReturn;
    }

    public static List<ItemStack> getCuriosIfPresent(PlayerEntity pPlayer) {
        List<ItemStack> curios = new ArrayList<>();
        ICuriosHelper helper = CuriosApi.getCuriosHelper();
        LazyOptional<IItemHandlerModifiable> equipped = helper.getEquippedCurios(pPlayer);

        equipped.ifPresent(handler -> {
            for (int i = 0; i < handler.getSlots(); i++) {
                curios.add(handler.getStackInSlot(i));
                handler.setStackInSlot(i, ItemStack.EMPTY);
            }
        });
        return curios;
    }
}
