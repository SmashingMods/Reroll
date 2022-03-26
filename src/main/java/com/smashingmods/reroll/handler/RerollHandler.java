package com.smashingmods.reroll.handler;

import com.smashingmods.reroll.capability.WorldSavedData;
import com.smashingmods.reroll.config.ConfigHandler;
import com.smashingmods.reroll.model.Spiral;
import com.smashingmods.reroll.util.RerollUtilities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class RerollHandler {

    private final Spiral HOLDER = new Spiral();

    public RerollHandler() {}

    public void reroll(ServerPlayerEntity pPlayer, boolean pNext) {
        reroll(null, pPlayer, pNext);
    }

    public void reroll(@Nullable ServerPlayerEntity pSender, ServerPlayerEntity pPlayer, boolean pNext) {

        resetInventory(pPlayer);
        if (pSender != null) {
            resetLocation(pSender, pPlayer, pNext);
        } else {
            resetLocation(pPlayer, pNext);
        }
        resetData(pPlayer);
    }

    public void resetInventory(ServerPlayerEntity pPlayer) {
        if (!ConfigHandler.Common.createGraveOnReroll.get()) {
            pPlayer.inventory.clearContent();
            if (ConfigHandler.Common.setNewInventory.get()) {
                RerollUtilities.setInventory(pPlayer);
            }
        }  else {
            // If reroll on death was set, the player death event would have already placed a grave.
            if (!ConfigHandler.Common.rerollOnDeath.get()) GraveHandler.handleGrave(pPlayer);
        }

        if (ConfigHandler.Common.resetEnderChest.get()) pPlayer.getEnderChestInventory().clearContent();
        pPlayer.doCloseContainer();
    }

    public void resetData(ServerPlayerEntity pPlayer) {

        pPlayer.clearFire();
        pPlayer.setHealth(pPlayer.getMaxHealth());
        pPlayer.setAirSupply(pPlayer.getMaxAirSupply());
        pPlayer.getFoodData().setFoodLevel(20);
        pPlayer.getFoodData().setSaturation(10);
        pPlayer.setExperiencePoints(0);
        pPlayer.setAbsorptionAmount(0.0f);
        pPlayer.removeAllEffects();
        pPlayer.setArrowCount(0);
        pPlayer.setScore(0);
        pPlayer.setGlowing(false);
        pPlayer.setInvisible(false);
        pPlayer.setJumping(false);
        pPlayer.stopRiding();
        pPlayer.stopFallFlying();
        pPlayer.stopSleeping();
        pPlayer.stopUsingItem();

        pPlayer.getServer().getCommands().performCommand(ServerLifecycleHooks.getCurrentServer().createCommandSourceStack(), String.format("/advancement revoke %s everything", pPlayer.getName().getContents()));
    }

    public void resetLocation(ServerPlayerEntity pPlayer, boolean pNext) {
        resetLocation(null, pPlayer, pNext);
    }

    public void resetLocation(@Nullable ServerPlayerEntity pSender, ServerPlayerEntity pPlayer, boolean pNext) {

        ServerWorld world;
        BlockPos newPosition;

        if (ConfigHandler.Common.useCurrentDim.get()) {
            if (pSender != null) {
                world = pSender.getLevel();
            } else {
                world = pPlayer.getLevel();
            }
        } else {
            if (ConfigHandler.Common.useSpawnDim.get()) {
                if (pSender != null) {
                    world = pSender.getServer().getLevel(pSender.getRespawnDimension());
                } else {
                    world = pPlayer.getServer().getLevel(pPlayer.getRespawnDimension());
                }
            } else {
                RegistryKey<World> worldRegistryKey = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(ConfigHandler.Common.overrideDim.get()));
                world = Objects.requireNonNull(pPlayer.getServer()).getLevel(worldRegistryKey);
            }
        }

        newPosition = generateValidBlockPos(Objects.requireNonNull(world), pPlayer, pNext);
        pPlayer.teleportTo(world, newPosition.getX(), newPosition.getY() + 1.5d, newPosition.getZ(), 0, 0);
        pPlayer.getLevel().setDefaultSpawnPos(newPosition, 0);
        if (pPlayer.getRespawnPosition() != null) {
            pPlayer.getLevel().setBlock(pPlayer.getRespawnPosition(), Blocks.AIR.defaultBlockState(), 2);
        }
    }

    public BlockPos generateValidBlockPos(@Nonnull ServerWorld pWorld, ServerPlayerEntity pPlayer, boolean pNext) {

        HOLDER.setSpiral(loadSpiral(pWorld));
        CompoundNBT spiral = HOLDER.getSpiral();

        double posX = spiral.getInt("posX") * ConfigHandler.Common.minDistance.get();
        double posZ = spiral.getInt("posZ") * ConfigHandler.Common.minDistance.get();
        int worldHeight = pWorld.getHeight();
        int seaLevel = pWorld.getSeaLevel();
        boolean ceiling = pWorld.dimensionType().hasCeiling();

        pWorld.getProfiler().push("Block Position Validator");
        Optional<BlockPos> toReturn = BlockPos.findClosestMatch(new BlockPos(posX, ceiling ? (double) worldHeight / 2 : seaLevel, posZ), ceiling ? 8 : 16, ceiling ? worldHeight / 4 : 32, blockStatePredicate(pWorld));
        pWorld.getProfiler().pop();

        if (pNext) HOLDER.setNext();
        saveSpiral(pWorld, HOLDER.getSpiral());

        return toReturn.orElseGet(() -> generateValidBlockPos(pWorld, pPlayer, pNext));
    }

    public Predicate<BlockPos> blockStatePredicate(ServerWorld pWorld) {
        return position -> {
            BlockState state = pWorld.getBlockState(position);
            return state.isValidSpawn(pWorld, position, EntityType.PLAYER) &&
                    (pWorld.canSeeSky(position.above()) || pWorld.dimensionType().hasCeiling()) &&
                    (pWorld.isEmptyBlock(position.above(1)) && pWorld.isEmptyBlock(position.above(2)));
        };
    }

    public CompoundNBT loadSpiral(ServerWorld world) {
        WorldSavedData savedData = WorldSavedData.getDataForWorld(world, "spiral");
        CompoundNBT data = savedData.getData();

        if (data.contains("posX") && data.contains("posZ") && data.contains("deltaX") && data.contains("deltaZ")) {
            return data;
        }
        return HOLDER.getSpiral();
    }

    public void saveSpiral(ServerWorld world, CompoundNBT pSpiral) {
        WorldSavedData savedData = WorldSavedData.getDataForWorld(world, "spiral");
        savedData.setData(pSpiral);
        savedData.setDirty();
    }

    public void setNext(ServerWorld pWorld) {
        HOLDER.setNext();
        saveSpiral(pWorld, HOLDER.getSpiral());
    }
}
