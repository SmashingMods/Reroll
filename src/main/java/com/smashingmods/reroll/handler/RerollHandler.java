package com.smashingmods.reroll.handler;

import arekkuusu.enderskills.api.capability.AdvancementCapability;
import arekkuusu.enderskills.api.capability.Capabilities;
import baubles.api.BaublesApi;
import baubles.api.inv.BaublesInventoryWrapper;
import com.smashingmods.reroll.Reroll;
import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.model.RerollObject;
import com.smashingmods.reroll.model.SpiralObject;
import net.minecraft.command.CommandException;
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

public class RerollHandler {

    private final String SEPARATOR = System.getProperty("file.separator");
    private RerollObject CURRENT;
    private SpiralObject SPIRAL;

    public RerollHandler() {}

    public void reroll(MinecraftServer server, EntityPlayerMP entityPlayer, boolean next) throws CommandException {
        String PATH;

        if (server.isDedicatedServer()) {
            PATH = server.getDataDirectory() + SEPARATOR + server.getFolderName();
        } else {
            PATH = server.getDataDirectory() + SEPARATOR + "saves" + SEPARATOR + server.getWorldName();
        }

        Reroll.MAPPER.setFile(new File(PATH, "reroll_position.json"));
        CURRENT = Reroll.MAPPER.readFile(RerollObject.class);
        if (CURRENT == null) CURRENT = new RerollObject();

        if (CURRENT.containsDimension(entityPlayer.dimension)) {
            SPIRAL = CURRENT.getDimensionObjectByID(entityPlayer.dimension).getSpiral();
        } else {
            CURRENT.addDimension(entityPlayer.dimension);
        }

        resetInventory(entityPlayer);
        InventoryHandler.setInventory(entityPlayer, Config.rerollItems);
        resetData(entityPlayer);
        server.getCommandManager().executeCommand(server, String.format("/advancement revoke %s everything", entityPlayer.getName()));
        resetModData(server, entityPlayer);
        resetLocation(server, entityPlayer, next);
        entityPlayer.sendMessage(new TextComponentTranslation("commands.reroll.successful").setStyle(new Style().setColor(TextFormatting.AQUA)));
    }

    public void resetInventory(EntityPlayerMP entityPlayer) {

        entityPlayer.closeContainer();
        entityPlayer.inventory.mainInventory.clear();
        entityPlayer.inventory.armorInventory.clear();
        entityPlayer.inventory.offHandInventory.clear();
        entityPlayer.getInventoryEnderChest().clear();
        entityPlayer.inventory.dropAllItems();
    }

    public void resetData(EntityPlayerMP entityPlayer) {

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

        BlockPos blockPos = generateValidBlockPos(entityPlayer, next);
        server.getCommandManager().executeCommand(server, String.format("/tp %s %d %d %d", entityPlayer.getName(), blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        entityPlayer.setSpawnDimension(Config.useCurrentDim ? entityPlayer.dimension : Config.overrideDim);
        entityPlayer.setSpawnPoint(blockPos, true);
        entityPlayer.bedLocation = blockPos;
    }

    public BlockPos generateValidBlockPos(EntityPlayerMP entityPlayer, boolean next) {

        SPIRAL = CURRENT.getDimensionObjectByID(entityPlayer.dimension).getSpiral();

        if (next) SPIRAL.setSpiral(SPIRAL.next());

        double posX = SPIRAL.getPosX() * Config.minDistance;
        double posZ = SPIRAL.getPosZ() * Config.minDistance;
        World world = entityPlayer.getEntityWorld();
        BlockPos toReturn = new BlockPos(entityPlayer);

        for (int i = 2; i < entityPlayer.getEntityWorld().getActualHeight(); i++) {
            BlockPos topBlock = new BlockPos(posX, i, posZ);
            if (
                (world.canBlockSeeSky(topBlock) && world.canBlockSeeSky(topBlock.down(1))) &&
                (world.isAirBlock(topBlock) && world.isAirBlock(topBlock.down(1))) &&
                !world.getBiome(topBlock).getRegistryName().getResourcePath().contains("ocean") &&
                world.getBlockState(topBlock.down(2)).getMaterial().isSolid() &&
                !world.getBlockState(topBlock.down(2)).getMaterial().isLiquid()
            ) {
                toReturn = new BlockPos(posX, i, posZ);
                break;
            } else {
                if (i == 255) {
                    SPIRAL.setSpiral(SPIRAL.next());
                    toReturn = generateValidBlockPos(entityPlayer, next);
                }
            }
        }
        Reroll.MAPPER.writeFile(CURRENT);
        return toReturn;
    }

    public void next() {
        SPIRAL.setSpiral(SPIRAL.next());
        Reroll.MAPPER.writeFile(CURRENT);
    }
}
