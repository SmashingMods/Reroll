package com.smashingmods.reroll.mixin;

import com.smashingmods.reroll.util.MalekUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class WorldMixin {

    @Inject(method = "onEntityAdded", at = @At("RETURN"))
    public void onEntityAddedFunc(Entity e, CallbackInfo ci) {
        if(e instanceof EntityPlayer) {
            System.out.println("AJSDFKASLFDAKSJFLKDSLFSJ");
            MalekUtil.setPlayerToBlock((EntityPlayer) e);
        }
    }
}
