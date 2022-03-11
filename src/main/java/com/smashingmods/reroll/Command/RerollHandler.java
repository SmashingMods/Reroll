package com.smashingmods.reroll.Command;

import com.smashingmods.reroll.Config.Config;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RerollHandler {

    public static void reroll(MinecraftServer server, ICommandSender sender, EntityPlayer entityPlayer) {
        entityPlayer.sendMessage(new TextComponentString("Well, here we go again ... But hey, if you don't like it, you can always reroll!").setStyle(new Style().setColor(TextFormatting.DARK_AQUA)));
        setRerollInventory(entityPlayer);
        resetData(entityPlayer);
        resetLocation(server, sender, entityPlayer);
    }

    public static void setRerollInventory(EntityPlayer entityPlayer) {

        entityPlayer.inventory.mainInventory.clear();
        entityPlayer.inventory.armorInventory.clear();
        entityPlayer.inventory.offHandInventory.clear();
        entityPlayer.getInventoryEnderChest().clear();

        List<ItemStack> items = new ArrayList<>();

        for (String startingItem : Config.rerollItems) {

            String itemString = startingItem.split(";")[0];
            int count;

            try {
                count = Item.REGISTRY.getObject(new ResourceLocation(itemString)).getItemStackLimit() == 1 ? 1 :  Integer.parseInt(startingItem.split(";")[1]);

                if (Item.REGISTRY.containsKey(new ResourceLocation(itemString))) {
                    items.add(new ItemStack(Item.getByNameOrId(itemString), count));
                }
                else {
                    com.smashingmods.reroll.Reroll.LOGGER.error(itemString + " isn't a valid registered Item. Check the config file for errors.");
                }

            } catch (NumberFormatException e) {
                com.smashingmods.reroll.Reroll.LOGGER.error(itemString + "value isn't set to an integer: " + e);
            } catch (NullPointerException e) {
                com.smashingmods.reroll.Reroll.LOGGER.error("Invalid item entry in Reroll Inventory config: " + itemString);
            }
        }

        items.forEach(item -> {
            entityPlayer.inventory.setInventorySlotContents(items.indexOf(item), item);
        });
    }

    public static void resetData(EntityPlayer entityPlayer) {

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

        if (com.smashingmods.reroll.Reroll.ModCompat_TimeIsUp) {
            com.smashingmods.reroll.Reroll.LOGGER.info("Time Is Up is loaded.");
        }
    }

    public static void resetLocation(MinecraftServer server, ICommandSender sender, EntityPlayer entityPlayer) {
        CommandTeleport commandTeleport = new CommandTeleport();

        BlockPos blockPos = generateValidBlockPos(entityPlayer);
        try {
            commandTeleport.execute(server, sender, new String[] {entityPlayer.getName(), String.valueOf(blockPos.getX()), String.valueOf(blockPos.getY()), String.valueOf(blockPos.getZ())});
        } catch (CommandException e) {
            com.smashingmods.reroll.Reroll.LOGGER.error("Reroll command failed, unable to set player position: " + e);
            entityPlayer.sendMessage(new TextComponentString("Something went wrong, try again!").setStyle(new Style().setColor(TextFormatting.RED)));
        } finally {
            entityPlayer.setSpawnDimension(Config.useCurrentDim ? entityPlayer.dimension : Config.overrideDim);
            entityPlayer.setSpawnPoint(blockPos, true);
        }
        entityPlayer.bedLocation = blockPos;
    }

    public static BlockPos generateValidBlockPos(EntityPlayer entityPlayer) {

        BlockPos newPosition = new BlockPos(new Vec3d(getRandomNumber(entityPlayer.posX - Config.rerollRange, entityPlayer.posX + Config.rerollRange) + Config.minDistance, 75, getRandomNumber(entityPlayer.posZ - Config.rerollRange, entityPlayer.posZ + Config.rerollRange) + Config.minDistance));
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

    public static double getRandomNumber(double min, double max) {
        return ((Math.random() * (max - min)) + min);
    }
}
