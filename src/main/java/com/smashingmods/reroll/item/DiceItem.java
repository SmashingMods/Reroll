package com.smashingmods.reroll.item;

import com.smashingmods.reroll.Reroll;
import com.smashingmods.reroll.capability.RerollCapability;
import com.smashingmods.reroll.capability.RerollCapabilityImplementation;
import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.handler.RerollHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class DiceItem extends Item {

    public static final String name = "dice";
    public static final ResourceLocation registryName = new ResourceLocation(Reroll.MODID, name);

    public DiceItem() {
        setRegistryName(registryName);
        setUnlocalizedName(registryName.toString());
        setCreativeTab(CreativeTabs.MISC);
        setMaxStackSize(1);
        setMaxDamage(0);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {

        if (!worldIn.isRemote) {
            RerollCapabilityImplementation rerollCapability = player.getCapability(RerollCapability.REROLL_CAPABILITY, null);
            if (!Objects.requireNonNull(rerollCapability).getLock()) {
                CooldownTracker tracker = player.getCooldownTracker();

                RerollHandler handler = new RerollHandler();
                handler.reroll(player.getServer(), (EntityPlayerMP) player, true);
                tracker.setCooldown(this, Config.cooldown * 20);
                player.sendMessage(new TextComponentTranslation("commands.reroll.self").setStyle(new Style().setColor(TextFormatting.AQUA)));
            } else {
                player.sendMessage(new TextComponentTranslation("commands.reroll.self.locked").setStyle(new Style().setColor(TextFormatting.RED)));
                return EnumActionResult.FAIL;
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUseFirst(@Nonnull EntityPlayer player, World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ, @Nonnull EnumHand hand) {
        if (world.isRemote) {
            CooldownTracker tracker = player.getCooldownTracker();
            if (tracker.hasCooldown(this)) {
                player.sendMessage(new TextComponentTranslation("reroll.dice.cooldown").setStyle(new Style().setColor(TextFormatting.RED)));
                return EnumActionResult.FAIL;
            } else {
                return EnumActionResult.SUCCESS;
            }
        } else {
            return EnumActionResult.PASS;
        }
    }

    @Override
    @Nonnull
    public EnumAction getItemUseAction(@Nonnull ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public boolean hasEffect(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add("Use this item to reroll.");
        tooltip.add(TextFormatting.RED + "This can't be undone!");
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
