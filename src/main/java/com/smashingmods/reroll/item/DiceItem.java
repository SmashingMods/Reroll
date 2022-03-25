package com.smashingmods.reroll.item;

import com.smashingmods.reroll.capability.RerollCapability;
import com.smashingmods.reroll.capability.RerollCapabilityImplementation;
import com.smashingmods.reroll.network.RerollPacket;
import com.smashingmods.reroll.network.RerollPacketHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class DiceItem extends Item {

    public DiceItem() {
        super(new Item.Properties().stacksTo(1).setNoRepair().durability(1).rarity(Rarity.RARE).fireResistant().tab(ItemGroup.TAB_MISC));
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> use(@Nonnull World pLevel, @Nonnull PlayerEntity pPlayer, @Nonnull Hand pHand) {
        boolean clientSide = pLevel.isClientSide;
        if (clientSide) {
            try {
                RerollCapabilityImplementation rerollCapability = pPlayer.getCapability(RerollCapability.REROLL_CAPABILITY, null).orElseThrow(() -> new IllegalAccessException("Reroll attempted to access capability on player."));
                if (!rerollCapability.getLock()) {
                    RerollPacketHandler.INSTANCE.sendToServer(new RerollPacket(getItemStack()));
                    pPlayer.sendMessage(new TranslationTextComponent("commands.reroll.self").withStyle(TextFormatting.AQUA), pPlayer.getUUID());
                    return ActionResult.success(getItemStack());
                } else {
                    pPlayer.sendMessage(new TranslationTextComponent("commands.reroll.self.locked").withStyle(TextFormatting.RED), pPlayer.getUUID());
                    return ActionResult.fail(getItemStack());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            return ActionResult.pass(getItemStack());
        }
        return ActionResult.fail(getItemStack());
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        boolean clientSide = context.getLevel().isClientSide;
        if (clientSide) {
            PlayerEntity player = context.getPlayer();
            if (Objects.requireNonNull(player).getCooldowns().isOnCooldown(this)) {
                player.sendMessage(new TranslationTextComponent("reroll.dice.cooldown").withStyle(TextFormatting.RED), player.getUUID());
                return ActionResultType.FAIL;
            } else {
                return ActionResultType.SUCCESS;
            }
        } else {
            return ActionResultType.PASS;
        }
    }

    @Override
    @Nonnull
    public UseAction getUseAnimation(@Nonnull ItemStack pStack) {
        return UseAction.BOW;
    }

    @Override
    @Nonnull
    public ITextComponent getDescription() {
        return new TranslationTextComponent("reroll.dice.name");
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack pStack) {
        return 0;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack pStack, @Nullable World pLevel, List<ITextComponent> pTooltip, @Nonnull ITooltipFlag pFlag) {
        pTooltip.add(new TranslationTextComponent("reroll.dice.tooltip.description"));
        pTooltip.add(new TranslationTextComponent("reroll.dice.tooltip.warning").withStyle(TextFormatting.RED));
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

    @Override
    public boolean isFoil(@Nonnull ItemStack pStack) {
        return true;
    }

    public ItemStack getItemStack() {
        return new ItemStack(this);
    }
}
