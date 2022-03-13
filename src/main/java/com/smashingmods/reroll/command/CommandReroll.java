package com.smashingmods.reroll.command;

import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.*;
import java.util.List;

import static com.smashingmods.reroll.handler.LockHandler.*;
import static com.smashingmods.reroll.handler.RerollHandler.reroll;
import static com.smashingmods.reroll.util.TagUtil.getTag;

public class CommandReroll extends CommandBase implements ICommand  {

    public CommandReroll() {
    }

    @Override
    public String getName() {
        return "reroll";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.reroll.usage";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        boolean locked = getTag(getCommandSenderAsPlayer(sender)).getBoolean(REROLL_LOCKED);

        switch (args.length) {
            case 0: {
                if (locked) {
                    sender.sendMessage(new TextComponentTranslation("commands.reroll.locked", getCommandSenderAsPlayer(sender).getName()).setStyle(new Style().setColor(TextFormatting.RED)));
                } else {
                    reroll(server, sender, getCommandSenderAsPlayer(sender));
                }
                break;
            }
            case 1:
            case 2: {
                if (super.checkPermission(server, sender)) {
                    switch (args[0]) {
                        case "lock": {
                            if (args.length == 2) {
                                try {
                                    EntityPlayer player = server.getPlayerList().getPlayerByUsername(args[1]);
                                    lockReroll(player);
                                    sender.sendMessage(new TextComponentTranslation("commands.lockreroll.successful", player.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
                                } catch (PlayerNotFoundException e) {
                                    sender.sendMessage(new TextComponentTranslation("commands.reroll.playernotfound", args[1]).setStyle(new Style().setColor(TextFormatting.RED)));
                                }
                            } else {
                                lockReroll(getCommandSenderAsPlayer(sender));
                                sender.sendMessage(new TextComponentTranslation("commands.lockreroll.successful", getCommandSenderAsPlayer(sender).getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
                            }
                            break;
                        }
                        case "unlock": {
                            if (args.length == 2) {
                                try {
                                    EntityPlayer player = server.getPlayerList().getPlayerByUsername(args[1]);
                                    unlockReroll(player);
                                    sender.sendMessage(new TextComponentTranslation("commands.unlockreroll.successful", player.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
                                } catch (PlayerNotFoundException e) {
                                    sender.sendMessage(new TextComponentTranslation("commands.reroll.playernotfound", args[1]).setStyle(new Style().setColor(TextFormatting.RED)));
                                }
                            } else {
                                unlockReroll(getCommandSenderAsPlayer(sender));
                                sender.sendMessage(new TextComponentTranslation("commands.unlockreroll.successful", getCommandSenderAsPlayer(sender).getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
                            }
                            break;
                        }
                        case "player": {
                            if (args.length == 2) {
                                try {
                                    EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(args[1]);
                                    if (locked) {
                                        sender.sendMessage(new TextComponentTranslation("commands.reroll.locked", player.getName()).setStyle(new Style().setColor(TextFormatting.RED)));
                                    } else {
                                        reroll(server, sender, player);
                                        sender.sendMessage(new TextComponentTranslation("commands.rerollplayer.successful", player.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
                                    }
                                } catch (PlayerNotFoundException e) {
                                    sender.sendMessage(new TextComponentTranslation("commands.reroll.playernotfound", args[1]).setStyle(new Style().setColor(TextFormatting.RED)));
                                }
                            } else {
                                sender.sendMessage(new TextComponentTranslation("commands.rerollplayer.usage").setStyle(new Style().setColor(TextFormatting.RED)));
                            }
                            break;
                        }
                        case "all": {
                            if (args.length == 1) {
                                server.getPlayerList().getPlayers().forEach(player -> {
                                    try {
                                        if (locked) {
                                            sender.sendMessage(new TextComponentTranslation("commands.reroll.locked", player.getName()).setStyle(new Style().setColor(TextFormatting.RED)));
                                        } else {
                                            reroll(server, sender, player);
                                        }
                                    } catch (CommandException e) {
                                        sender.sendMessage(new TextComponentTranslation("commands.rerollall.failure").setStyle(new Style().setColor(TextFormatting.RED)));
                                    }
                                });
                                sender.sendMessage(new TextComponentTranslation("commands.rerollall.successful").setStyle(new Style().setColor(TextFormatting.AQUA)));
                            } else {
                                sender.sendMessage(new TextComponentTranslation("commands.reroll.usage").setStyle(new Style().setColor(TextFormatting.RED)));
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
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        switch (args.length) {
            case 0: return Collections.emptyList();
            case 1: {
                return getListOfStringsMatchingLastWord(args, new String[]{"lock", "unlock", "player", "all"});
            }
            case 2: {
                return getListOfStringsMatchingLastWord(args, sender.getServer().getOnlinePlayerNames());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
