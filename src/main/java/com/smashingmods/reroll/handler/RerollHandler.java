package com.smashingmods.reroll.handler;

import com.smashingmods.reroll.Reroll;
import com.smashingmods.reroll.capability.WorldSavedData;
import com.smashingmods.reroll.config.ConfigHandler;
import com.smashingmods.reroll.model.Spiral;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public class RerollHandler {

    private final Spiral HOLDER = new Spiral();

    public RerollHandler() {}

    public void reroll(ServerWorld world, ServerPlayerEntity pPlayer, boolean pNext) {

        resetLocation(pPlayer, pNext);
        resetInventory(pPlayer);
        resetData(pPlayer);

//        server.getCommandManager().executeCommand(server, String.format("/advancement revoke %s everything", pPlayer.getName()));
//        if (Config.setNewInventory) {
//            InventoryHandler.setInventory(pPlayer, Config.rerollItems);
//        }
//        pPlayer.sendMessage(new TextComponentTranslation("commands.reroll.successful").setStyle(new Style().setColor(TextFormatting.AQUA)));
    }

    public void resetInventory(ServerPlayerEntity pPlayer) {
        pPlayer.inventory.clearContent();
        pPlayer.doCloseContainer();
        pPlayer.inventory.dropAll();
    }

//
//    public void resetInventory(ServerPlayerEntity pPlayer) {
//
//        if (Config.sendInventoryToChest && !pPlayer.inventory.isEmpty()) {
//            World world = pPlayer.getEntityWorld();
//            BlockPos position = pPlayer.getPosition();
//            List<BlockPos> chestPositions = generateValidChestPosition(world, position);
//            final int[] count = {0};
//            chestPositions.forEach(pos -> {
//                world.setBlockState(pos, Blocks.CHEST.getDefaultState());
//                for (int slot = 0; slot < 27; slot++) {
//                    IItemHandler capability = world.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
//                    capability.insertItem(slot, pPlayer.inventory.getStackInSlot(count[0]), false);
//                    pPlayer.inventory.removeStackFromSlot(count[0]);
//                    count[0]++;
//                }
//            });
//        } else {
//            pPlayer.inventory.mainInventory.clear();
//            pPlayer.inventory.armorInventory.clear();
//            pPlayer.inventory.offHandInventory.clear();
//        }
//
//        pPlayer.closeScreen();
//        if (Config.resetEnderChest) pPlayer.getInventoryEnderChest().clear();
//        pPlayer.inventory.dropAllItems();
//    }
//
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
    }

    public void resetLocation(ServerPlayerEntity pPlayer, boolean pNext) {

        ServerWorld world;
        BlockPos newPosition;

        if (ConfigHandler.Common.useCurrentDim.get()) {
            world = pPlayer.getLevel();
        } else {
            if (ConfigHandler.Common.useSpawnDim.get()) {
                world = pPlayer.getServer().getLevel(pPlayer.getRespawnDimension());
            } else {
                RegistryKey<World> worldRegistryKey = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(ConfigHandler.Common.overrideDim.get()));
                world = pPlayer.getServer().getLevel(worldRegistryKey);
            }
        }

        newPosition = generateValidBlockPos(world, pPlayer, pNext);
        pPlayer.teleportTo(world, newPosition.getX(), newPosition.getY() + 1.5d, newPosition.getZ(), 0, 0);
        pPlayer.sendMessage(new StringTextComponent("New reroll position found."), pPlayer.getUUID());
    }

    public BlockPos generateValidBlockPos(@NotNull ServerWorld pWorld, ServerPlayerEntity pPlayer, @Nullable boolean pNext) {

        pPlayer.sendMessage(new StringTextComponent("Searching for valid reroll position . . ."), pPlayer.getUUID());

        HOLDER.setSpiral(loadSpiral(pWorld));
        CompoundNBT spiral = HOLDER.getSpiral();

        double posX = spiral.getInt("posX") * ConfigHandler.Common.minDistance.get();
        double posZ = spiral.getInt("posZ") * ConfigHandler.Common.minDistance.get();
        int worldHeight = pWorld.getHeight();
        int seaLevel = pWorld.getSeaLevel();
        boolean ceiling = pWorld.dimensionType().hasCeiling();

        Optional<BlockPos> toReturn = BlockPos.findClosestMatch(new BlockPos(posX, ceiling ? (double) worldHeight / 2 : seaLevel, posZ), ceiling ? 8 : 32, ceiling ? worldHeight / 4 : 16, blockStatePredicate(pWorld));

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
}
