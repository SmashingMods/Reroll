package com.smashingmods.reroll.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.smashingmods.reroll.capability.RerollCapability;
import com.smashingmods.reroll.capability.RerollCapabilityImplementation;
import com.smashingmods.reroll.config.ConfigHandler;
import com.smashingmods.reroll.handler.RerollHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class RerollCommand {

    private static final RerollHandler HANDLER = new RerollHandler();

    public static void register(@Nonnull CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("reroll")
            .requires(commandSource -> commandSource.hasPermission(0))
                .executes(context -> self(context.getSource()))

            .then(Commands.literal("player")
                .requires(commandSource -> commandSource.hasPermission(4))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> player(context.getSource(), EntityArgument.getPlayer(context, "player"))
                    )
                )
            )

            .then(Commands.literal("lock")
                .executes(context -> lockSelf(context.getSource()))
                .then(Commands.argument("player", EntityArgument.players())
                    .requires(commandSource -> commandSource.hasPermission(4))
                    .executes(context -> lockPlayer(context.getSource(), EntityArgument.getPlayer(context, "player"))
                    )
                )
            )

            .then(Commands.literal("unlock")
                .executes(context -> unlockSelf(context.getSource()))
                .then(Commands.argument("player", EntityArgument.players())
                    .requires(commandSource -> commandSource.hasPermission(4))
                    .executes(context -> unlockPlayer(context.getSource(), EntityArgument.getPlayer(context, "player"))
                    )
                )
            )

            .then(Commands.literal("all").requires(commandSource -> commandSource.hasPermission(4))
                .executes(context -> all(context.getSource())
                )
            )
        );
    }

    private static int self(@Nonnull CommandSource pSource) throws CommandSyntaxException {
        try {
            ServerPlayerEntity player = pSource.getPlayerOrException();
            RerollCapabilityImplementation lockCapability = Objects.requireNonNull(pSource.getEntity()).getCapability(RerollCapability.REROLL_CAPABILITY, null).orElseThrow(() -> new IllegalAccessException("Reroll attempted to access capability on player."));

            if (ConfigHandler.Common.requireItem.get()) {
                player.sendMessage(new TranslationTextComponent("commands.reroll.item_required").withStyle(TextFormatting.RED), player.getUUID());
                return 1;
            } else if (!lockCapability.getLock()) {
                HANDLER.reroll(player, true);
                player.sendMessage(new TranslationTextComponent("commands.reroll.self").withStyle(TextFormatting.AQUA), player.getUUID());
            } else {
                player.sendMessage(new TranslationTextComponent("commands.reroll.self.locked").withStyle(TextFormatting.RED), player.getUUID());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int lockSelf(@Nonnull CommandSource pSource)  {
        try {
            ServerPlayerEntity player = pSource.getPlayerOrException();
            RerollCapabilityImplementation lockCapability = player.getCapability(RerollCapability.REROLL_CAPABILITY, null).orElseThrow(() -> new IllegalAccessException("Reroll attempted to access capability on player."));
            lockCapability.setLock(true);

            player.sendMessage(new TranslationTextComponent("commands.reroll.self.lock").withStyle(TextFormatting.YELLOW), player.getUUID());
        } catch (IllegalAccessException | CommandSyntaxException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int unlockSelf(@Nonnull CommandSource pSource) {
        try {
            ServerPlayerEntity player = pSource.getPlayerOrException();
            RerollCapabilityImplementation lockCapability = player.getCapability(RerollCapability.REROLL_CAPABILITY, null).orElseThrow(() -> new IllegalAccessException("Reroll attempted to access lock capability on player."));
            lockCapability.setLock(false);

            player.sendMessage(new TranslationTextComponent("commands.reroll.self.unlock").withStyle(TextFormatting.YELLOW), player.getUUID());
        } catch (IllegalAccessException | CommandSyntaxException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int player(@Nonnull CommandSource pSource, @Nonnull ServerPlayerEntity pPlayer) {
        try {
            ServerPlayerEntity sender = pSource.getPlayerOrException();
            RerollCapabilityImplementation lockCapability = pPlayer.getCapability(RerollCapability.REROLL_CAPABILITY, null).orElseThrow(() -> new IllegalAccessException("Reroll attempted to access lock capability on player."));

            if (!lockCapability.getLock()) {
                HANDLER.reroll(pPlayer, true);

                if (sender.getUUID() != pPlayer.getUUID()) {
                    sender.sendMessage(new TranslationTextComponent("commands.reroll.player", pPlayer.getName()).withStyle(TextFormatting.AQUA), sender.getUUID());
                }
                pPlayer.sendMessage(new TranslationTextComponent("commands.reroll.player.rerolled").withStyle(TextFormatting.AQUA), pPlayer.getUUID());
            } else {
                sender.sendMessage(new TranslationTextComponent("commands.reroll.player.locked").withStyle(TextFormatting.RED), sender.getUUID());
            }
        } catch (IllegalAccessException | CommandSyntaxException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int lockPlayer(CommandSource pSource, ServerPlayerEntity pPlayer) {
        try {
            ServerPlayerEntity sender = pSource.getPlayerOrException();
            RerollCapabilityImplementation lockCapability = pPlayer.getEntity().getCapability(RerollCapability.REROLL_CAPABILITY, null).orElseThrow(() -> new IllegalAccessException("Reroll attempted to access lock capability on player."));
            lockCapability.setLock(true);

            pPlayer.sendMessage(new TranslationTextComponent("commands.reroll.self.lock").withStyle(TextFormatting.YELLOW), pPlayer.getUUID());

            if (sender.getUUID() != pPlayer.getUUID()) {
                sender.sendMessage(new TranslationTextComponent("commands.reroll.player.lock", pPlayer.getName()).withStyle(TextFormatting.YELLOW), sender.getUUID());
            }
        } catch (IllegalAccessException | CommandSyntaxException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int unlockPlayer(CommandSource pSource, ServerPlayerEntity pPlayer) {
        try {
            ServerPlayerEntity sender = pSource.getPlayerOrException();
            RerollCapabilityImplementation lockCapability = pPlayer.getEntity().getCapability(RerollCapability.REROLL_CAPABILITY, null).orElseThrow(() -> new IllegalAccessException("Reroll attempted to access lock capability on player."));
            lockCapability.setLock(false);

            pPlayer.sendMessage(new TranslationTextComponent("commands.reroll.self.unlock").withStyle(TextFormatting.YELLOW), pPlayer.getUUID());
            if (sender.getUUID() != pPlayer.getUUID()) {
                sender.sendMessage(new TranslationTextComponent("commands.reroll.player.unlock", pPlayer.getName()).withStyle(TextFormatting.YELLOW), sender.getUUID());
            }
        } catch (IllegalAccessException | CommandSyntaxException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int all(CommandSource pSource) {
        try {
            ServerPlayerEntity sender = pSource.getPlayerOrException();
            List<ServerPlayerEntity> playerList = pSource.getServer().getPlayerList().getPlayers();
            for (ServerPlayerEntity player : playerList) {
                RerollCapabilityImplementation lockCapability = player.getEntity().getCapability(RerollCapability.REROLL_CAPABILITY, null).orElseThrow(() -> new IllegalAccessException("Reroll attempted to access lock capability on player."));
                if (!lockCapability.getLock()) {

                    if (ConfigHandler.Common.rerollAllTogether.get()) {
                        HANDLER.reroll(player, false);
                    } else {
                        HANDLER.reroll(sender, player, true);
                    }

                    if (sender.getUUID() != player.getUUID()) {
                        sender.sendMessage(new TranslationTextComponent("commands.reroll.player", player.getName()).withStyle(TextFormatting.YELLOW), sender.getUUID());
                    }
                    player.sendMessage(new TranslationTextComponent("commands.reroll.player.rerolled").withStyle(TextFormatting.AQUA), player.getUUID());
                } else {
                    sender.sendMessage(new TranslationTextComponent("commands.reroll.player.locked").withStyle(TextFormatting.RED), sender.getUUID());
                }
            }
            if (ConfigHandler.Common.rerollAllTogether.get()) {
                HANDLER.setNext(sender.getLevel());
            }
        } catch (IllegalAccessException | CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
