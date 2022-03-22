//package com.smashingmods.reroll.item;
//
//import com.smashingmods.reroll.Reroll;
//import net.minecraft.client.util.ITooltipFlag;
//import net.minecraft.creativetab.CreativeTabs;
//import net.minecraft.item.EnumAction;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.*;
//import net.minecraft.util.text.TextFormatting;
//import net.minecraft.world.World;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//import java.util.List;
//
//public class DiceItem extends Item {
//
//    public static final String name = "dice";
//    public static final ResourceLocation registryName = new ResourceLocation(Reroll.MODID, name);
//
//    public DiceItem() {
//        setRegistryName(registryName);
//        setUnlocalizedName(registryName.toString());
//        setCreativeTab(CreativeTabs.MISC);
//        setMaxStackSize(1);
//        setMaxDamage(0);
//    }
//
//    @Override
//    @Nonnull
//    public EnumAction getItemUseAction(ItemStack stack) {
//        return EnumAction.BOW;
//    }
//
//
//    @Override
//    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
//        tooltip.add("Use this item to reroll.");
//        tooltip.add(TextFormatting.RED + "This can't be undone!");
//        super.addInformation(stack, worldIn, tooltip, flagIn);
//    }
//}
