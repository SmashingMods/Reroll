package com.smashingmods.reroll.util;

import com.google.common.collect.AbstractIterator;
import com.smashingmods.reroll.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Predicate;

public class MalekUtil {
    public static EntityPlayer setPlayerToBlock(EntityPlayer player) {
        BlockPos currentPosition = player.getPosition();
        Optional<BlockPos> leafBlock = findClosest(currentPosition, 600, 100, blockPos -> player.getEntityWorld().getBlockState(blockPos).getBlock() == Config.spawnBlock);
        leafBlock.ifPresent(blockPos -> player.setPositionAndUpdate(blockPos.getX(), blockPos.getY()+4, blockPos.getZ()));
        return player;
    }
    public static BlockPos treePosOrNormal(World world, BlockPos pos) {
        System.out.println(Config.spawnBlock);
        Optional<BlockPos> treePos = MalekUtil.findClosest(pos, 800, 200, blockPos -> world.getBlockState(blockPos).getBlock().equals(Config.spawnBlock));
        return treePos.orElse(pos);
    }


    public static Optional<BlockPos> findClosest(BlockPos pos, int horizontalRange, int verticalRange, Predicate<BlockPos> condition) {
        for (BlockPos blockPos : iterateOutwards(pos, horizontalRange, verticalRange, horizontalRange)) {
            if (!condition.test(blockPos)) continue;
            return Optional.of(blockPos);
        }
        return Optional.empty();
    }
    public static Iterable<BlockPos> iterateOutwards(BlockPos center, final int rangeX, final int rangeY, final int rangeZ) {
        final int i = rangeX + rangeY + rangeZ;
        final int j = center.getX();
        final int k = center.getY();
        final int l = center.getZ();
        return () -> new AbstractIterator<BlockPos>(){
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
                    this.pos.setPos(this.pos.getX(), this.pos.getY(), l - (this.pos.getZ() - l));
                    return this.pos;
                }
                BlockPos.MutableBlockPos blockPos = null;
                while (blockPos == null) {
                    if (this.dy > this.limitY) {
                        ++this.dx;
                        if (this.dx > this.limitX) {
                            ++this.manhattanDistance;
                            if (this.manhattanDistance > i) {
                                return (BlockPos)this.endOfData();
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
                        blockPos = this.pos.setPos(j + i2, k + j2, l + k2);
                    }
                    ++this.dy;
                }
                return blockPos;
            }

        };
    }
}
