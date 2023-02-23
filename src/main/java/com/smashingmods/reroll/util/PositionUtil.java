package com.smashingmods.reroll.util;

import com.google.common.collect.AbstractIterator;
import com.smashingmods.reroll.config.Config;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class PositionUtil {

    private static BlockPos oldPos;

    public static List<Block> getSpawnBlocks() {
        List<Block> blocks = new ArrayList<>();

        if (Config.potentialSpawnBlocks != null) {
            for (String blockName : Config.potentialSpawnBlocks) {
                blocks.add(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName)));
            }
        }
        return blocks;
    }

    public static Predicate<BlockPos> blockStatePredicate(World world) {
        return position ->
            position != oldPos &&
            getSpawnBlocks().contains(world.getBlockState(position).getBlock()) &&
            world.canBlockSeeSky(position) &&
            world.isAirBlock(position.up(1)) &&
            world.isAirBlock(position.up(2)) &&
            world.getBlockState(position).getMaterial().isSolid() &&
            !Objects.requireNonNull(world.getBiome(position).getRegistryName()).getPath().contains("ocean");
    }

    public static Optional<BlockPos> findClosest(BlockPos pos, int horizontalRange, int verticalRange, Predicate<BlockPos> condition) {
        for (BlockPos blockPos : iterateOutwards(pos, horizontalRange, verticalRange, horizontalRange)) {
            if (condition.test(blockPos)) {
                oldPos = blockPos;
                return Optional.of(blockPos);
            }
        }
        return Optional.empty();
    }

    public static Iterable<BlockPos> iterateOutwards(BlockPos center, final int rangeX, final int rangeY, final int rangeZ) {

        final int range = rangeX + rangeY + rangeZ;
        final int centerX = center.getX();
        final int centerY = center.getY();
        final int centerZ = center.getZ();

        return () -> new AbstractIterator<BlockPos>() {

            private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            private int manhattanDistance;
            private int limitX;
            private int limitY;
            private int dx;
            private int dy;
            private boolean swapZ;

            protected BlockPos computeNext() {

                if (this.swapZ) {
                    this.swapZ = false;
                    this.pos.setPos(this.pos.getX(), this.pos.getY(), centerZ - (this.pos.getZ() - centerZ));
                    return this.pos;
                }

                BlockPos.MutableBlockPos blockPos = null;

                while (blockPos == null) {
                    if (this.dy > this.limitY) {
                        ++this.dx;
                        if (this.dx > this.limitX) {
                            ++this.manhattanDistance;
                            if (this.manhattanDistance > range) {
                                return this.endOfData();
                            }
                            this.limitX = Math.min(rangeX, this.manhattanDistance);
                            this.dx = -this.limitX;
                        }
                        this.limitY = Math.min(rangeY, this.manhattanDistance - Math.abs(this.dx));
                        this.dy = -this.limitY;
                    }

                    int i2 = this.dx;
                    int j2 = this.dy;
                    int k2 = this.manhattanDistance - Math.abs(i2) - Math.abs(j2);

                    if (k2 <= rangeZ) {
                        this.swapZ = k2 != 0;
                        blockPos = this.pos.setPos(centerX + i2, centerY + j2, centerZ + k2);
                    }
                    ++this.dy;
                }
                return blockPos;
            }
        };
    }
}
