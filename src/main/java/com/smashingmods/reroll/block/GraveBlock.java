package com.smashingmods.reroll.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GraveBlock extends Block implements IWaterLoggable {

    protected final String name;
    protected final GraveModel model;
    private static final EnumProperty<Direction> FACING = HorizontalFaceBlock.FACING;
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

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
        this.registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Nonnull
    @Override
    public String getDescriptionId() {
        return "block.reroll.grave." + this.name;
    }

    @SuppressWarnings("unused")
    public GraveModel getGraveModel() {
        return model;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, WATERLOGGED);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull IBlockReader pLevel, @Nonnull BlockPos pPos, @Nonnull ISelectionContext pContext) {
        VoxelShape northSouth = Block.box(1, 0, 5, 15, 15, 10);
        VoxelShape eastWest = Block.box(5, 0, 1, 10, 15, 15);
        Direction direction = pState.getValue(FACING);

        switch (direction) {
            case NORTH:
            case SOUTH: return northSouth;
            case EAST:
            case WEST: return eastWest;
        }

        return northSouth;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext pContext) {
        BlockPos blockPos = pContext.getClickedPos();
        World world = pContext.getLevel();
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, world.getFluidState(blockPos).getType() == Fluids.WATER);
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
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
        return new GraveTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    public void onRemove(@Nonnull BlockState pState, @Nonnull World pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            TileEntity tileEntity = pLevel.getBlockEntity(pPos);
            if (tileEntity instanceof GraveTileEntity) {
                LazyOptional<IItemHandler> itemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                itemHandler.ifPresent(handler -> {
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack itemStack = handler.getStackInSlot(i);
                        ItemEntity itemEntity = new ItemEntity(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), itemStack);
                        itemEntity.spawnAtLocation(itemStack);
                    }
                });
            }
        }
        pLevel.removeBlockEntity(pPos);
    }
}
