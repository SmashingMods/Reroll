package com.smashingmods.reroll.block;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class GraveBlockTileEntity extends TileEntity  {

    private final LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);

    public GraveBlockTileEntity() {
        super(BlockRegistry.GRAVE_TILE);
    }

    @Nonnull
    private IItemHandler createHandler() {
        return new ItemStackHandler(255);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        double renderExtension = 1.0D;
        return new AxisAlignedBB(
                this.worldPosition.getX() - renderExtension,
                this.worldPosition.getY() - renderExtension,
                this.worldPosition.getZ() - renderExtension,
                this.worldPosition.getX() + 1 + renderExtension,
                this.worldPosition.getY() + 1 + renderExtension,
                this.worldPosition.getZ() + 1 +renderExtension
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public CompoundNBT save(@Nonnull CompoundNBT pTag) {
        handler.ifPresent(itemHandler -> {
            CompoundNBT inventory = ((INBTSerializable<CompoundNBT>) itemHandler).serializeNBT();
            pTag.put("inventory", inventory);
        });
        return super.save(pTag);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load(@Nonnull BlockState pBlockState, @Nonnull CompoundNBT pTag) {
        CompoundNBT inventory = pTag.getCompound("inventory");
        handler.ifPresent(itemHandler -> ((INBTSerializable<CompoundNBT>) itemHandler).deserializeNBT(inventory));
        super.load(pBlockState, pTag);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        IItemHandler inventory = handler.orElse(handler.orElseThrow(() -> new IllegalStateException("Reroll grave inventory handler not present.")));
        if (Objects.requireNonNull(this.level).getBlockState(this.worldPosition).getBlock() instanceof GraveBlock) {
            return;
        }
        for (int i = 1; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                InventoryHelper.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), inventory.extractItem(i, stack.getCount(), false));
            }
        }
        super.invalidateCaps();
    }

    @Override
    @Nonnull
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = new CompoundNBT();
        super.save(tag);
        return tag;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        load(this.getBlockState(), pkt.getTag());
    }
}
