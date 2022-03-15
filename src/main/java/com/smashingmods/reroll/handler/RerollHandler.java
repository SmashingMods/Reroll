package com.smashingmods.reroll.handler;

import baubles.api.BaublesApi;
import baubles.api.inv.BaublesInventoryWrapper;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smashingmods.reroll.Reroll;
import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.util.JsonWriter;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RerollHandler {

    public static Spiral SPIRAL;

    public static void reroll(MinecraftServer server, ICommandSender sender, EntityPlayerMP entityPlayer) throws CommandException {

        if (SPIRAL == null) {
            SPIRAL = new Spiral();

            JsonWriter writer = null;
            try {
                writer = new JsonWriter(server.getDataDirectory().getCanonicalPath() + System.getProperty("file.separator") + "saves" + System.getProperty("file.separator") + server.getWorldName());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Rerolls current = writer.readJson("reroll_step.json", Rerolls.class);

            if (current == null) {
                current = new Rerolls();
            }

            if (current.containsDimension(entityPlayer.dimension)) {
                current.incrementStep(entityPlayer.dimension);
            } else {
                current.addDimension(entityPlayer.dimension);
            }
            writer.writeJson("reroll_step.json", current);

            for (int i = 0; i < current.getDimensionByID(entityPlayer.dimension).getStep(); i++) {
                SPIRAL.next();
            }
        }

        resetInventory(entityPlayer);
        InventoryHandler.setInventory(entityPlayer, Config.rerollItems);
        resetData(entityPlayer);
        server.getCommandManager().executeCommand(server, String.format("/advancement revoke %s everything", entityPlayer.getName()));
        resetModData(server, entityPlayer);
        resetLocation(server, sender, entityPlayer);
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

    public static void resetLocation(MinecraftServer server, ICommandSender sender, EntityPlayerMP entityPlayer) {
        BlockPos blockPos = generateValidBlockPos(entityPlayer);
        server.getCommandManager().executeCommand(server, String.format("/tp %s %d %d %d", sender.getName(), blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        entityPlayer.setSpawnDimension(Config.useCurrentDim ? entityPlayer.dimension : Config.overrideDim);
        entityPlayer.setSpawnPoint(blockPos, true);
        entityPlayer.bedLocation = blockPos;
    }

    public static BlockPos generateValidBlockPos(EntityPlayerMP entityPlayer) {

        int[] spiralPos = SPIRAL.next();

        BlockPos newPosition = new BlockPos(spiralPos[0] * Config.minDistance, 0, spiralPos[1] * Config.minDistance);

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

    private static class Spiral {
        private int posX = 0;
        private int posZ = 0;
        private int deltaX = 0;
        private int deltaZ = -1;

        int[] next() {
            if (posX == posZ || posX < 0 && posX == -posZ || posX > 0 && posX == 1 - posZ) {
                int t = deltaX;
                deltaX = -deltaZ;
                deltaZ = t;
            }
            posX += deltaX;
            posZ += deltaZ;
            return new int[] {posX, posZ};
        }
    }

    public static class Rerolls {
        public List<Dimension> dimensionList;

        public Rerolls() {
            dimensionList = new ArrayList<>();
            dimensionList.add(new Dimension(1));
            dimensionList.add(new Dimension(-1));
        }

        public void addDimension(int id) {
            dimensionList.add(new Dimension(id));
            incrementStep(id);
        }

        public void incrementStep(int id) {
            getDimensionByID(id).incrementStep();
        }

        public Dimension getDimensionByID(int id) {
            for (Dimension dimension : dimensionList) {
                if (dimension.getId() == id) {
                    return dimension;
                }
            }
            return null;
        }

        public boolean containsDimension(int id) {
            if (getDimensionByID(id) != null) {
                return true;
            }
            return false;
        }
    }

    public static class Dimension {
        public final int id;
        public int step = 0;

        @JsonCreator
        public Dimension(@JsonProperty("id") int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public void incrementStep() {
            step++;
        }
    }
}
