package com.smashingmods.reroll.item;

import com.smashingmods.reroll.handler.RerollHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class DiceItem extends Item {

    public DiceItem(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> use(@Nonnull World pLevel, @Nonnull PlayerEntity pPlayer, @Nonnull Hand pHand) {
        RerollHandler handler = new RerollHandler();
        handler.reroll((ServerPlayerEntity) pPlayer, true);
        return super.use(pLevel, pPlayer, pHand);
    }

    @Override
    @Nonnull
    public UseAction getUseAnimation(@Nonnull ItemStack pStack) {
        return UseAction.BLOCK;
    }

    @Override
    @Nonnull
    public ITextComponent getDescription() {
        return new TranslationTextComponent("reroll.item.dice");
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack pStack) {
        return 0;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack pStack, @Nullable World pLevel, List<ITextComponent> pTooltip, @Nonnull ITooltipFlag pFlag) {
        pTooltip.add(new TranslationTextComponent("reroll.tooltip.dice.description"));
        pTooltip.add(new TranslationTextComponent("reroll.tooltip.dice.warning").withStyle(TextFormatting.RED));
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

    @Override
    @Nonnull
    public ITextComponent getName(@Nonnull ItemStack pStack) {
        return super.getName(pStack);
    }

    @Override
    public boolean isFoil(@Nonnull ItemStack pStack) {
        return true;
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack pStack) {
        return false;
    }

//    @Override
//    public void addInformation(ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, ITooltipFlag flagIn) {
//        tooltip.add("Use this item to reroll.");
//        tooltip.add(TextFormatting.RED + "This can't be undone!");
//        super.addInformation(stack, worldIn, tooltip, flagIn);
//    }
}
