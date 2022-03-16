package com.smashingmods.reroll.handler;

import baubles.api.BaublesApi;
import baubles.api.inv.BaublesInventoryWrapper;
import com.smashingmods.reroll.Reroll;
import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.model.RerollObject;
import com.smashingmods.reroll.model.SpiralObject;
import com.smashingmods.reroll.util.JsonMapper;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import timeisup.TimeIsUp;
import timeisup.capabilities.Timer;
import timeisup.capabilities.TimerCapability;
import timeisup.network.PacketHandler;
import timeisup.network.TimerPacket;

import java.io.File;
import java.io.IOException;

public class RerollHandler {

    private static String PATH;
    private static final String SEPARATOR = System.getProperty("file.separator");
    private static RerollObject CURRENT;
    private static SpiralObject SPIRAL;

    public static void reroll(MinecraftServer server, EntityPlayerMP entityPlayer) throws CommandException {

        try {
            if (server.isDedicatedServer()) {
                PATH = server.getDataDirectory().getCanonicalPath() + SEPARATOR + server.getFolderName();
            } else {
                PATH = server.getDataDirectory().getCanonicalPath() + SEPARATOR + "saves" + SEPARATOR + server.getWorldName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Reroll.MAPPER.setFile(new File(PATH, "reroll_step.json"));
        CURRENT = Reroll.MAPPER.readFile(RerollObject.class);

        if (CURRENT == null) {
            CURRENT = new RerollObject();
        }

        if (CURRENT.containsDimension(entityPlayer.dimension)) {
            CURRENT.incrementStep(entityPlayer.dimension);
            SPIRAL = CURRENT.getDimensionObjectByID(entityPlayer.dimension).getSpiral();
        } else {
            CURRENT.addDimension(entityPlayer.dimension);
        }

        resetInventory(entityPlayer);
        InventoryHandler.setInventory(entityPlayer, Config.rerollItems);
        resetData(entityPlayer);
        server.getCommandManager().executeCommand(server, String.format("/advancement revoke %s everything", entityPlayer.getName()));
        resetModData(server, entityPlayer);
        resetLocation(server, entityPlayer);
        entityPlayer.sendMessage(new TextComponentTranslation("commands.reroll.successful").setStyle(new Style().setColor(TextFormatting.AQUA)));
    }

    public static void resetInventory(EntityPlayerMP entityPlayer) {

        entityPlayer.closeContainer();
        entityPlayer.inventory.mainInventory.clear();
        entityPlayer.inventory.armorInventory.clear();
        entityPlayer.inventory.offHandInventory.clear();
        entityPlayer.getInventoryEnderChest().clear();
        entityPlayer.inventory.dropAllItems();
    }

    public static void resetData(EntityPlayerMP entityPlayer) {

        entityPlayer.setHealth(20);
        entityPlayer.setAir(300);
        entityPlayer.getFoodStats().setFoodLevel(20);
        entityPlayer.addExperienceLevel(-Integer.MAX_VALUE);
        entityPlayer.setFire(0);
        entityPlayer.extinguish();
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

    public static void resetModData(MinecraftServer server, EntityPlayerMP entityPlayer) {
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
            server.getCommandManager().executeCommand(server, String.format("/es_advancement %s retries set 0", entityPlayer.getName()));
            server.getCommandManager().executeCommand(server, String.format("/es_advancement %s level set 1", entityPlayer.getName()));
            server.getCommandManager().executeCommand(server, String.format("/es_skill %s reset", entityPlayer.getName()));
        }
    }

    public static void resetLocation(MinecraftServer server, EntityPlayerMP entityPlayer) {

        BlockPos blockPos = generateValidBlockPos(entityPlayer);
        server.getCommandManager().executeCommand(server, String.format("/tp %s %d %d %d", entityPlayer.getName(), blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        entityPlayer.setSpawnDimension(Config.useCurrentDim ? entityPlayer.dimension : Config.overrideDim);
        entityPlayer.setSpawnPoint(blockPos, true);
        entityPlayer.bedLocation = blockPos;
    }

    public static BlockPos generateValidBlockPos(EntityPlayerMP entityPlayer) {

        SPIRAL = CURRENT.getDimensionObjectByID(entityPlayer.dimension).getSpiral();
        int[] data = SPIRAL.next();
        CURRENT.getDimensionObjectByID(entityPlayer.dimension).setSpiral(new SpiralObject(data[0], data[1], data[2], data[3]));
        Reroll.MAPPER.writeFile(CURRENT);

        BlockPos newPosition = new BlockPos(data[0] * Config.minDistance, 0, data[1] * Config.minDistance);
        double posX = newPosition.getX();
        double posZ = newPosition.getZ();
        World world = entityPlayer.getEntityWorld();

        BlockPos toReturn = new BlockPos(entityPlayer);
        for (int i = 0; i < entityPlayer.getEntityWorld().getActualHeight(); i++) {
            BlockPos topBlock = new BlockPos(posX, i + 1, posZ);
            BlockPos bottomBlock= new BlockPos(posX, i, posZ);
            BlockPos support = new BlockPos(posX, i - 1, posZ);
            if (
                    (world.canBlockSeeSky(topBlock) && world.canBlockSeeSky(bottomBlock)) &&
                    (world.isAirBlock(topBlock) && world.isAirBlock(bottomBlock)) &&
                    !world.getBiome(topBlock).getRegistryName().getResourcePath().contains("ocean") &&
                    world.getBlockState(support).getMaterial().isSolid()
            ) {
                toReturn = new BlockPos(posX, i, posZ);
                break;
            } else {
                if (i == 255) {
                    toReturn = generateValidBlockPos(entityPlayer);
                }
            }
        }
        return toReturn;
    }
}
