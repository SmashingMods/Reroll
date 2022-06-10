package com.smashingmods.reroll.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "onEntityAdded", at = @At("RETURN"))
    public void onEntityAddedFunc(Entity e, CallbackInfo ci) {
        if(e instanceof EntityPlayer) {
            setPlayerToBlock((EntityPlayer) e);
        }
    }

    private void setPlayerToBlock(EntityPlayer player) {
        BlockPos currentPosition = player.getPosition();
        World world = player.getEntityWorld();
        Optional<BlockPos> spawnBlock = PositionUtil.findClosest(currentPosition, 64, world.getActualHeight() / 4, PositionUtil.blockStatePredicate(world));
        spawnBlock.ifPresent(blockPos -> player.setPositionAndUpdate(blockPos.getX(), blockPos.getY() + 0.5f, blockPos.getZ()));
    }
}
