package com.smashingmods.reroll.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GraveBlock extends Block implements IWaterLoggable {

    protected final String name;
    protected final GraveModel model;
    public static final EnumProperty<Direction> FACING = HorizontalFaceBlock.FACING;

    public GraveBlock(GraveModel model) {
        super(Properties.of(Material.STONE)
                .strength(2.0f, Float.MAX_VALUE)
                .harvestLevel(2)
                .harvestTool(ToolType.PICKAXE)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .sound(SoundType.STONE)
        );

        this.model = model;
        this.name = model.getSerializedName();
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull IBlockReader pLevel, @Nonnull BlockPos pPos, @Nonnull ISelectionContext pContext) {
        VoxelShape northSouth = Block.box(1, 0, 5, 15, 15, 10);
        VoxelShape eastWest = Block.box(5, 0, 1, 10, 15, 15);
        Direction direction = pState.getValue(FACING);

        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            return northSouth;
        } else if (direction == Direction.EAST || direction == Direction.WEST) {
            return eastWest;
        }

        return northSouth;
    }

    @Nonnull
    @Override
    public String getDescriptionId() {
        return "reroll.grave." + this.name;
    }

    @SuppressWarnings("unused")
    public GraveModel getGraveModel() {
        return model;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean dropFromExplosion(@Nonnull Explosion pExplosion) {
        return false;
    }

    @Override
    public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
        // do nothing
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new GraveBlockTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
    }

    @SuppressWarnings("deprecation")
    public void onRemove(@Nonnull BlockState pState, @Nonnull World pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            TileEntity tileEntity = pLevel.getBlockEntity(pPos);
            if (tileEntity instanceof GraveBlockTileEntity) {
                IItemHandler itemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElseThrow(() -> new IllegalStateException("Reroll grave inventory handler not present."));
                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    ItemStack itemStack = itemHandler.getStackInSlot(i);
                    ItemEntity itemEntity = new ItemEntity(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), itemStack);
                    itemEntity.spawnAtLocation(itemStack);
                }
            }
        }
    }
}
