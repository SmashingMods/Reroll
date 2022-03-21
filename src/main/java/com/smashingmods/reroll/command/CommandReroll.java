package com.smashingmods.reroll.command;

import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.handler.RerollHandler;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.List;

import static com.smashingmods.reroll.handler.LockHandler.*;
import static com.smashingmods.reroll.util.TagUtil.getTag;

public class CommandReroll extends CommandBase implements ICommand  {

    private final RerollHandler handler;

    public CommandReroll() {
        this.handler = new RerollHandler();
    }

    @Override
    @Nonnull
    public String getName() {
        return "reroll";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "commands.reroll.usage";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException {
        switch (args.length) {
            case 0: {
                if (Config.requireItem) {
                    if (!super.checkPermission(server, sender)) {
                        sender.sendMessage(new TextComponentTranslation("commands.reroll.permission").setStyle(new Style().setColor(TextFormatting.RED)));
                        break;
                    }
                }
                if (sender instanceof EntityPlayer) {
                    if (getTag(getCommandSenderAsPlayer(sender)).getBoolean(REROLL_LOCKED)) {
                        sender.sendMessage(new TextComponentTranslation("commands.reroll.locked").setStyle(new Style().setColor(TextFormatting.RED)));
                    } else {
                        handler.reroll(server, getCommandSenderAsPlayer(sender), true);
                    }
                } else {
                    sender.sendMessage(new TextComponentTranslation("commands.reroll.server"));
                }
                break;
            }
            case 1: {
                switch (args[0]) {
                    case "lock": {
                        if (sender instanceof EntityPlayer) {
                            EntityPlayerMP entityplayer = getCommandSenderAsPlayer(sender);
                            lockReroll(entityplayer);
                            sender.sendMessage(new TextComponentTranslation("commands.lockreroll.successful", entityplayer.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
                        } else {
                            sender.sendMessage(new TextComponentTranslation("commands.reroll.server"));
                        }
                        break;
                    }
                    case "unlock": {
                        if (sender instanceof EntityPlayer) {
                            EntityPlayerMP entityplayer = getCommandSenderAsPlayer(sender);
                            unlockReroll(entityplayer);
                            sender.sendMessage(new TextComponentTranslation("commands.unlockreroll.successful", entityplayer.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
                        } else {
                            sender.sendMessage(new TextComponentTranslation("commands.reroll.server"));
                        }
                        break;
                    }
                    case "all": {
                        server.getPlayerList().getPlayers().forEach(player -> {
                            if (getTag(player).getBoolean(REROLL_LOCKED)) {
                                sender.sendMessage(new TextComponentTranslation("commands.reroll.server.locked", player.getName()).setStyle(new Style().setColor(TextFormatting.RED)));
                            } else {
                                handler.reroll(server, player, !Config.rerollAllTogether);
                            }
                        });
                        if (Config.rerollAllTogether) handler.next();
                        sender.sendMessage(new TextComponentTranslation("commands.rerollall.successful").setStyle(new Style().setColor(TextFormatting.AQUA)));
                        break;
                    }
                    default: {
                        sender.sendMessage(new TextComponentTranslation("commands.reroll.usage").setStyle(new Style().setColor(TextFormatting.RED)));
                        break;
                    }
                }
                break;
            }
            case 2: {
                if (super.checkPermission(server, sender)) {
                    switch (args[0]) {
                        case "lock": {
                            EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(args[1]);
                            if (player != null) {
                                lockReroll(player);
                                sender.sendMessage(new TextComponentTranslation("commands.lockreroll.server.successful", player.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
                                player.sendMessage(new TextComponentTranslation("commands.lockreroll.successful", player.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
                            } else {
                                sender.sendMessage(new TextComponentTranslation("commands.reroll.playernotfound", args[1]).setStyle(new Style().setColor(TextFormatting.RED)));
                            }
                            break;
                        }
                        case "unlock": {
                            EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(args[1]);
                            if (player != null) {
                                unlockReroll(player);
                                sender.sendMessage(new TextComponentTranslation("commands.unlockreroll.server.successful", player.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
                                player.sendMessage(new TextComponentTranslation("commands.unlockreroll.successful", player.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
                            } else {
                                sender.sendMessage(new TextComponentTranslation("commands.reroll.playernotfound", args[1]).setStyle(new Style().setColor(TextFormatting.RED)));
                            }
                            break;
                        }
                        case "player": {
                            EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(args[1]);
                            if (player != null) {
                                if (getTag(player).getBoolean(REROLL_LOCKED)) {
                                    sender.sendMessage(new TextComponentTranslation("commands.reroll.locked", player.getName()).setStyle(new Style().setColor(TextFormatting.RED)));
                                } else {
                                    handler.reroll(server, player, true);
                                    sender.sendMessage(new TextComponentTranslation("commands.rerollplayer.successful", player.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
                                }
                            } else {
                                sender.sendMessage(new TextComponentTranslation("commands.reroll.playernotfound", args[1]).setStyle(new Style().setColor(TextFormatting.RED)));
                            }
                            break;
                        }
                        default: {
                            sender.sendMessage(new TextComponentTranslation("commands.reroll.usage").setStyle(new Style().setColor(TextFormatting.RED)));
                            break;
                        }
                    }
                } else {
                    sender.sendMessage(new TextComponentTranslation("commands.reroll.permission").setStyle(new Style().setColor(TextFormatting.RED)));
                }
                break;
            }
            default: {
                sender.sendMessage(new TextComponentTranslation("commands.reroll.usage").setStyle(new Style().setColor(TextFormatting.RED)));
                break;
            }
        }
    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender) {
        return true;
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        switch (args.length) {
            case 0: return Collections.emptyList();
            case 1: {
                return getListOfStringsMatchingLastWord(args, "lock", "unlock", "player", "all");
            }
            case 2: {
                return getListOfStringsMatchingLastWord(args, Objects.requireNonNull(sender.getServer()).getOnlinePlayerNames());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(@Nonnull String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(@Nonnull ICommand o) {
        return 0;
    }
}
