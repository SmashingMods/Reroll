package com.smashingmods.reroll.handler;

import baubles.api.BaublesApi;
import baubles.api.inv.BaublesInventoryWrapper;
import com.smashingmods.reroll.Reroll;
import com.smashingmods.reroll.capability.WorldSavedData;
import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.item.DiceItem;
import com.smashingmods.reroll.model.Spiral;
import com.smashingmods.reroll.util.PositionUtil;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.server.command.CommandSetDimension;
import timeisup.TimeIsUp;
import timeisup.capabilities.Timer;
import timeisup.capabilities.TimerCapability;
import timeisup.network.PacketHandler;
import timeisup.network.TimerPacket;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.smashingmods.reroll.util.PositionUtil.blockStatePredicate;

public class RerollHandler {

    private final Spiral HOLDER = new Spiral();

    public RerollHandler() {}

    public void reroll(MinecraftServer server, EntityPlayerMP entityPlayer, boolean next) {
        reroll(server, entityPlayer, next, null);
    }

    public void reroll(MinecraftServer server, EntityPlayerMP entityPlayer, boolean next, @Nullable Item usedItem) {

        resetInventory(entityPlayer);
        resetLocation(server, entityPlayer, next);
        if (Config.setNewInventory) {
            InventoryHandler.setInventory(entityPlayer, Config.rerollItems);
        }
        resetData(entityPlayer);
        resetModData(entityPlayer);

        if (usedItem instanceof DiceItem) {
            CooldownTracker tracker = entityPlayer.getCooldownTracker();
            tracker.setCooldown(usedItem, Config.cooldown * 20);
        }

        executeAdditionalCommands(server, entityPlayer);
    }

    private List<BlockPos> generateValidChestPosition(World world, BlockPos position) {

        List<BlockPos> directions = new ArrayList<>();
        directions.add(position.north());
        directions.add(position.east());
        directions.add(position.south());
        directions.add(position.west());
        directions.add(position.up());
        directions.add(position.down());

        if (world.getBlockState(position).getMaterial().isReplaceable()) {
            List<BlockPos> positionList = new ArrayList<>();
            for (BlockPos direction : directions) {
                if (world.getBlockState(direction).getMaterial().isReplaceable()) {
                    positionList.add(position);
                    positionList.add(direction);
                    break;
                }
            }
            return positionList;
        } else {
            List<BlockPos> positionList = new ArrayList<>();
            directions.forEach(direction -> positionList.addAll(generateValidChestPosition(world, direction)));
            return positionList;
        }
    }

    private void resetInventory(EntityPlayerMP entityPlayer) {

        if (Config.sendInventoryToChest && !entityPlayer.inventory.isEmpty()) {
            World world = entityPlayer.getEntityWorld();
            BlockPos position = entityPlayer.getPosition();
            List<BlockPos> chestPositions = generateValidChestPosition(world, position);
            final int[] count = {0};
            chestPositions.forEach(pos -> {
                world.setBlockState(pos, Blocks.CHEST.getDefaultState());
                for (int slot = 0; slot < 27; slot++) {
                    IItemHandler capability = Objects.requireNonNull(world.getTileEntity(pos)).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
                    Objects.requireNonNull(capability).insertItem(slot, entityPlayer.inventory.getStackInSlot(count[0]), false);
                    entityPlayer.inventory.removeStackFromSlot(count[0]);
                    count[0]++;
                }
            });
        } else {
            entityPlayer.inventory.mainInventory.clear();
            entityPlayer.inventory.armorInventory.clear();
            entityPlayer.inventory.offHandInventory.clear();
        }

        if (Config.resetEnderChest) entityPlayer.getInventoryEnderChest().clear();
        entityPlayer.inventory.dropAllItems();
    }

    private void resetData(EntityPlayerMP entityPlayer) {

        entityPlayer.setHealth(entityPlayer.getMaxHealth());
        entityPlayer.setAir(300);
        entityPlayer.getFoodStats().setFoodLevel(20);
        entityPlayer.addExperienceLevel(-Integer.MAX_VALUE);
        entityPlayer.setAbsorptionAmount(0.0f);
        entityPlayer.clearActivePotions();
        entityPlayer.setArrowCountInEntity(0);
        entityPlayer.setScore(0);
        entityPlayer.setGlowing(false);
        entityPlayer.setInvisible(false);
        entityPlayer.setJumping(false);
        entityPlayer.stopActiveHand();
        entityPlayer.removePassengers();
        entityPlayer.dismountRidingEntity();
        entityPlayer.setFire(0);
        entityPlayer.extinguish();
    }

    private void resetModData(EntityPlayerMP entityPlayer) {
        if (Reroll.MODCOMPAT_TIMEISUP) {
            TimerCapability timer = entityPlayer.getCapability(TimeIsUp.TIMER, null);

            if (timer != null) {
                Timer dimTimer = timer.getOrCreate(entityPlayer.getEntityWorld());
                if (dimTimer != null) {
                    dimTimer.setDuration(Config.timeisupTimer);
                    PacketHandler.INSTANCE.sendTo(new TimerPacket(Config.timeisupTimer), entityPlayer);
                }
            }
        }

        if (Reroll.MODCOMPAT_BAUBLES) {
            BaublesInventoryWrapper wrapper = new BaublesInventoryWrapper(BaublesApi.getBaublesHandler(entityPlayer));
            wrapper.clear();
        }
    }

    private void resetLocation(MinecraftServer server, EntityPlayerMP entityPlayer, boolean next) {

        WorldServer world;
        BlockPos newPosition;

        if (Config.useOverrideDim) {
            world = server.getWorld(Config.overrideDim);
        } else if (Config.useCurrentDim) {
            world = server.getWorld(entityPlayer.dimension);
        } else {
            world = server.getWorld(entityPlayer.getSpawnDimension());
        }

        newPosition = generateValidBlockPos(world, next);

        if (!Config.useCurrentDim) {
            CommandSetDimension setDimension = new CommandSetDimension();
            try {
                if (entityPlayer.dimension != Config.overrideDim) {
                    setDimension.execute(server, entityPlayer, new String[] { entityPlayer.getName(), String.valueOf(Config.overrideDim), String.valueOf(0), String.valueOf(75), String.valueOf(0)});
                }
                entityPlayer.setSpawnDimension(Config.overrideDim);
            } catch (CommandException e) {
                e.printStackTrace();
            }
        } else {
            entityPlayer.setSpawnDimension(entityPlayer.dimension);
        }

        if (newPosition != null) {
            entityPlayer.setPositionAndUpdate(newPosition.getX(), newPosition.getY() + 2.5f, newPosition.getZ());
        } else {
            entityPlayer.sendMessage(new TextComponentTranslation("commands.reroll.max_tries"));
        }
    }

    private BlockPos generateValidBlockPos(WorldServer world, boolean next) {

        HOLDER.setSpiral(loadSpiral(world));
        NBTTagCompound spiral = HOLDER.getSpiral();

        double posX = spiral.getInteger("posX") * Config.minDistance;
        double posZ = spiral.getInteger("posZ") * Config.minDistance;
        int seaLevel = world.getSeaLevel();

        Optional<BlockPos> toReturn = PositionUtil.findClosest(new BlockPos(posX, seaLevel, posZ), Config.horizontalRadius, Config.verticalRadius, blockStatePredicate(world));

        if (next) HOLDER.setNext();
        saveSpiral(world, HOLDER.getSpiral());

        AtomicReference<Integer> count = new AtomicReference<>(0);
        return toReturn.orElseGet(() -> {
            if (count.get() > Config.maxTries) {
                count.set(count.get() + 1);
                return generateValidBlockPos(world, true);
            } else {
                return null;
            }
        });
    }

    public NBTTagCompound loadSpiral(World world) {
        WorldSavedData savedData = WorldSavedData.getDataForWorld(world, "spiral");
        NBTTagCompound data = savedData.getDATA();

        if (data.hasKey("posX") && data.hasKey("posZ") && data.hasKey("deltaX") &&data.hasKey("deltaZ")) {
            return data;
        }
        return HOLDER.getSpiral();
    }

    public void saveSpiral(World world, NBTTagCompound spiral) {
        WorldSavedData savedData = WorldSavedData.getDataForWorld(world, "spiral");
        savedData.setDATA(spiral);
        savedData.markDirty();
    }

    public void setNext(World world) {
        HOLDER.setNext();
        saveSpiral(world, HOLDER.getSpiral());
    }

    private void executeAdditionalCommands(MinecraftServer server, EntityPlayerMP entityPlayer) {

        for (String configString : Config.additionalCommands) {
            String commandToExecute = "";

            if (configString.contains(";")) {
                String[] commandArray = configString.split(";");
                if (commandArray.length > 1 || !commandArray[0].contains("%s")) {
                    String[] formatObject = Arrays.copyOfRange(commandArray, 1, commandArray.length);
                    commandToExecute = String.format(commandArray[0], (Object[]) formatObject);
                } else {
                    Reroll.LOGGER.error("Incorrectly formatted additional command list. Example: 'say hello world' or 'say hello %s;player'");
                    Reroll.LOGGER.error(configString);
                }
            } else {
                commandToExecute = configString;
            }

            if (!commandToExecute.isEmpty()) {
                if (commandToExecute.contains("@p")) {
                    commandToExecute = commandToExecute.replace("@p", entityPlayer.getName());
                }
                server.getCommandManager().executeCommand(server, commandToExecute);
            }
        }
    }
}
