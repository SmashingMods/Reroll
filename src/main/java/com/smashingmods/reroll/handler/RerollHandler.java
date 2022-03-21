package com.smashingmods.reroll.handler;

import arekkuusu.enderskills.api.capability.AdvancementCapability;
import arekkuusu.enderskills.api.capability.Capabilities;
import baubles.api.BaublesApi;
import baubles.api.inv.BaublesInventoryWrapper;
import com.smashingmods.reroll.Reroll;
import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.item.DiceItem;
import com.smashingmods.reroll.model.RerollObject;
import com.smashingmods.reroll.model.SpiralObject;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.server.command.CommandSetDimension;
import timeisup.TimeIsUp;
import timeisup.capabilities.Timer;
import timeisup.capabilities.TimerCapability;
import timeisup.network.PacketHandler;
import timeisup.network.TimerPacket;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

public class RerollHandler {

    private final String SEPARATOR = System.getProperty("file.separator");
    private RerollObject CURRENT;
    private SpiralObject SPIRAL;

    public RerollHandler() {}

    public void reroll(MinecraftServer server, EntityPlayerMP entityPlayer, boolean next) {
        reroll(server, entityPlayer, next, null);
    }

    public void reroll(MinecraftServer server, EntityPlayerMP entityPlayer, boolean next, @Nullable Item usedItem) {

        String worldPath;
        if (server.isDedicatedServer()) {
            worldPath = server.getDataDirectory() + SEPARATOR + server.getFolderName();
        } else {
            worldPath = server.getDataDirectory() + SEPARATOR + "saves" + SEPARATOR + server.getWorldName();
        }

        Reroll.MAPPER.setFile(new File(worldPath, "reroll_position.json"));
        CURRENT = Reroll.MAPPER.readFile(RerollObject.class);
        if (CURRENT == null) CURRENT = new RerollObject();

        if (CURRENT.containsDimension(entityPlayer.dimension)) {
            SPIRAL = CURRENT.getDimensionObjectByID(entityPlayer.dimension).getSpiral();
        } else {
            CURRENT.addDimension(entityPlayer.dimension);
        }

        resetInventory(entityPlayer);
        server.getCommandManager().executeCommand(server, String.format("/advancement revoke %s everything", entityPlayer.getName()));
        resetLocation(server, entityPlayer, next);
        if (Config.setNewInventory) {
            InventoryHandler.setInventory(entityPlayer, Config.rerollItems);
        }
        resetData(entityPlayer);
        resetModData(server, entityPlayer);
        entityPlayer.sendMessage(new TextComponentTranslation("commands.reroll.successful").setStyle(new Style().setColor(TextFormatting.AQUA)));

        if (usedItem instanceof DiceItem) {
            CooldownTracker tracker = entityPlayer.getCooldownTracker();
            tracker.setCooldown(usedItem, Config.cooldown * 20);
        }
    }

    public List<BlockPos> generateValidChestPosition(World world, BlockPos position) {

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
            directions.forEach(direction -> {
                positionList.addAll(generateValidChestPosition(world, direction));
            });
            return positionList;
        }
    }

    public void resetInventory(EntityPlayerMP entityPlayer) {

        if (Config.sendInventoryToChest && !entityPlayer.inventory.isEmpty()) {
            World world = entityPlayer.getEntityWorld();
            BlockPos position = entityPlayer.getPosition();
            List<BlockPos> chestPositions = generateValidChestPosition(world, position);
            final int[] count = {0};
            chestPositions.forEach(pos -> {
                world.setBlockState(pos, Blocks.CHEST.getDefaultState());
                for (int slot = 0; slot < 27; slot++) {
                    IItemHandler capability = world.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
                    capability.insertItem(slot, entityPlayer.inventory.getStackInSlot(count[0]), false);
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

    public void resetData(EntityPlayerMP entityPlayer) {

        entityPlayer.setFire(0);
        entityPlayer.extinguish();
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
    }

    public void resetModData(MinecraftServer server, EntityPlayerMP entityPlayer) {
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

        if (Reroll.MODCOMPAT_GAMESSTAGES) {
            server.getCommandManager().executeCommand(server, String.format("/gamestage clear %s", entityPlayer.getName()));
        }

        if (Reroll.MODCOMPAT_ENDERSKILLS) {
            AdvancementCapability esadvancements = entityPlayer.getCapability(Capabilities.ADVANCEMENT, null);
            esadvancements.consumeExperienceFromTotal(entityPlayer, Integer.MAX_VALUE);

            server.getCommandManager().executeCommand(server, String.format("/es_advancement %s retries set 0", entityPlayer.getName()));
            server.getCommandManager().executeCommand(server, String.format("/es_advancement %s level set 1", entityPlayer.getName()));
            server.getCommandManager().executeCommand(server, String.format("/es_skill %s reset", entityPlayer.getName()));
            server.getCommandManager().executeCommand(server, String.format("/es_cooldown %s reset", entityPlayer.getName()));
            server.getCommandManager().executeCommand(server, String.format("/es_endurance %s reset", entityPlayer.getName()));
        }
    }

    public void resetLocation(MinecraftServer server, EntityPlayerMP entityPlayer, boolean next) {
        if (!Config.useCurrentDim) {
            CommandSetDimension setDimension = new CommandSetDimension();
            try {
                if (entityPlayer.dimension != Config.overrideDim) {
                    setDimension.execute(server, entityPlayer, new String[] { entityPlayer.getName(), String.valueOf(Config.overrideDim), String.valueOf(0), String.valueOf(75), String.valueOf(0)});
                }
                executeTeleport(server, entityPlayer, generateValidBlockPos(entityPlayer, next));
                entityPlayer.setSpawnDimension(Config.overrideDim);
            } catch (CommandException e) {
                e.printStackTrace();
            }
        } else {
            executeTeleport(server, entityPlayer, generateValidBlockPos(entityPlayer, next));
            entityPlayer.setSpawnDimension(entityPlayer.dimension);
        }
    }

    public void executeTeleport(MinecraftServer server, EntityPlayerMP entityPlayer, BlockPos blockPos) {
        server.getCommandManager().executeCommand(server, String.format("/tp %s %d %d %d", entityPlayer.getName(), blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        entityPlayer.setSpawnPoint(blockPos, true);
    }

    public BlockPos generateValidBlockPos(EntityPlayerMP entityPlayer, boolean next) {

        SPIRAL = CURRENT.getDimensionObjectByID(entityPlayer.dimension).getSpiral();

        if (next) SPIRAL.setSpiral(SPIRAL.next());

        double posX = SPIRAL.getPosX() * Config.minDistance;
        double posZ = SPIRAL.getPosZ() * Config.minDistance;
        World world = entityPlayer.getEntityWorld();
        int height = world.getActualHeight();
        BlockPos toReturn = new BlockPos(entityPlayer);

        boolean found = false;

        for (int i = 2; i < height; i++) {
            BlockPos topBlock = new BlockPos(posX, i, posZ);
            if (
                (world.canBlockSeeSky(topBlock) && world.canBlockSeeSky(topBlock.down(1))) &&
                (world.isAirBlock(topBlock) && world.isAirBlock(topBlock.down(1))) &&
                !world.getBiome(topBlock).getRegistryName().getResourcePath().contains("ocean") &&
                world.getBlockState(topBlock.down(2)).getMaterial().isSolid() &&
                world.getBlockState(topBlock.down(2)).isFullCube() &&
                !world.getBlockState(topBlock.down(2)).getMaterial().isLiquid()
            ) {
                found = true;
                toReturn = new BlockPos(posX, i + 0.5f, posZ);
                break;
            } else {
                if (i == height) {
                    SPIRAL.setSpiral(SPIRAL.next());
                }
            }
        }
        Reroll.MAPPER.writeFile(CURRENT);

        if (found) {
            return toReturn;
        } else {
            return generateValidBlockPos(entityPlayer, next);
        }
    }

    public void next() {
        SPIRAL.setSpiral(SPIRAL.next());
        Reroll.MAPPER.writeFile(CURRENT);
    }
}
