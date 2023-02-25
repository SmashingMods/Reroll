package com.smashingmods.reroll.handler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;

public class ScalingHealthHandler {

    public static void setScalingHealth(EntityPlayerMP entityPlayer) {
        SHPlayerDataHandler.PlayerData playerData = SHPlayerDataHandler.get(entityPlayer);
        if (playerData != null) {
            entityPlayer.setHealth(Config.Player.Health.startingHealth);
            playerData.setMaxHealth(Config.Player.Health.startingHealth);
        }
    }
}
