package com.smashingmods.reroll.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.smashingmods.reroll.capability.RerollCapability;
import com.smashingmods.reroll.capability.RerollCapabilityImplementation;
import com.smashingmods.reroll.handler.RerollHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class RerollCommand {

    private static final RerollHandler HANDLER = new RerollHandler();

    public static void register(@NotNull CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("reroll")
            .requires(commandSource -> commandSource.hasPermission(0))
                .executes(context -> self(context.getSource()))

            .then(Commands.literal("player")
                .requires(commandSource -> commandSource.hasPermission(4))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> player(context.getSource().getEntityOrException())
                    )
                )
            )

            .then(Commands.literal("lock")
                .executes(context -> lockSelf(context.getSource()))
                .then(Commands.argument("player", EntityArgument.players())
                    .requires(commandSource -> commandSource.hasPermission(4))
                    .executes(context -> lockPlayer(context.getSource().getEntityOrException())
                    )
                )
            )

            .then(Commands.literal("unlock")
                .executes(context -> unlockSelf(context.getSource()))
                .then(Commands.argument("player", EntityArgument.players())
                    .requires(commandSource -> commandSource.hasPermission(4))
                    .executes(context -> unlockPlayer(context.getSource().getEntityOrException())
                    )
                )
            )

            .then(Commands.literal("all").requires(commandSource -> commandSource.hasPermission(4))
                .executes(context -> all(context.getSource())
                )
            )
        );
    }

    private static int self(@NotNull CommandSource pSource) throws CommandSyntaxException {
        try {
            RerollCapabilityImplementation lockCapability = Objects.requireNonNull(pSource.getEntity()).getCapability(RerollCapability.REROLL_CAPABILITY, null).orElseThrow(() -> new IllegalAccessException("Reroll attempted to access capability on player."));
            if (!lockCapability.getLock()) {
                HANDLER.reroll(pSource.getPlayerOrException(), true);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int lockSelf(@NotNull CommandSource pSource)  {
        try {
            RerollCapabilityImplementation lockCapability = Objects.requireNonNull(pSource.getEntity()).getCapability(RerollCapability.REROLL_CAPABILITY, null).orElseThrow(() -> new IllegalAccessException("Reroll attempted to access capability on player."));
            lockCapability.setLock(true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int unlockSelf(@NotNull CommandSource pSource) {
        try {
            RerollCapabilityImplementation lockCapability = Objects.requireNonNull(pSource.getEntity()).getCapability(RerollCapability.REROLL_CAPABILITY, null).orElseThrow(() -> new IllegalAccessException("Reroll attempted to access lock capability on player."));
            lockCapability.setLock(false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int player(@NotNull Entity pPlayer) {
        try {
            RerollCapabilityImplementation lockCapability = pPlayer.getEntity().getCapability(RerollCapability.REROLL_CAPABILITY, null).orElseThrow(() -> new IllegalAccessException("Reroll attempted to access lock capability on player."));
            if (!lockCapability.getLock()) {
                HANDLER.reroll((ServerPlayerEntity) pPlayer, true);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int lockPlayer(Entity pPlayer) {
        try {
            RerollCapabilityImplementation lockCapability = pPlayer.getEntity().getCapability(RerollCapability.REROLL_CAPABILITY, null).orElseThrow(() -> new IllegalAccessException("Reroll attempted to access lock capability on player."));
            lockCapability.setLock(true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int unlockPlayer(Entity pPlayer) {
        try {
            RerollCapabilityImplementation lockCapability = pPlayer.getEntity().getCapability(RerollCapability.REROLL_CAPABILITY, null).orElseThrow(() -> new IllegalAccessException("Reroll attempted to access lock capability on player."));
            lockCapability.setLock(false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int all(@NotNull CommandSource pSource) {
        List<ServerPlayerEntity> list = pSource.getServer().getPlayerList().getPlayers();
        for (ServerPlayerEntity player : list) {
            try {
                RerollCapabilityImplementation lockCapability = player.getEntity().getCapability(RerollCapability.REROLL_CAPABILITY, null).orElseThrow(() -> new IllegalAccessException("Reroll attempted to access lock capability on player."));
                if (!lockCapability.getLock()) {
                    HANDLER.reroll(player, true);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }
}
