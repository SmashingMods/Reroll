package com.smashingmods.reroll.mixin;

import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.util.PositionUtil;
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
    public void onEntityAddedFunc(Entity entity, CallbackInfo callbackInfo) {
        if (entity instanceof EntityPlayer) {
            setPlayerToBlock((EntityPlayer) entity);
        }
    }

    private void setPlayerToBlock(EntityPlayer player) {
        BlockPos currentPosition = player.getPosition();
        World world = player.getEntityWorld();
        Optional<BlockPos> spawnBlock = PositionUtil.findClosest(currentPosition, Config.horizontalRange, Config.verticalRange, PositionUtil.blockStatePredicate(world));
        spawnBlock.ifPresent(blockPos -> player.setPositionAndUpdate(blockPos.getX() + 0.5d, blockPos.getY() + 1.5d, blockPos.getZ() + 0.5d));
    }
}
