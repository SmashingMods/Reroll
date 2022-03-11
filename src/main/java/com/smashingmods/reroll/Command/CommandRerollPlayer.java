package com.smashingmods.reroll.Command;

import com.smashingmods.reroll.Reroll;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.*;
import java.util.List;

import static com.smashingmods.reroll.Command.RerollHandler.reroll;

public class CommandRerollPlayer extends CommandBase implements ICommand  {

    private final List aliases;

    public CommandRerollPlayer() {
        aliases = new ArrayList();
        aliases.add("rerollother");
        aliases.add("remakeother");
    }

    @Override
    public String getName() {
        return "rerollplayer";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/rerollplayer <player>";
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        switch (args.length) {
            case 0: {
                sender.sendMessage(new TextComponentString("Must specify a player to reroll."));
                sender.sendMessage(new TextComponentString(getUsage(sender)));
                break;
            }
            case 1: {
                try {
                    sender.sendMessage(new TextComponentString("Rerolling for player " + args[0] + "."));
                    reroll(server, sender, server.getPlayerList().getPlayerByUsername(args[0]));
                } catch (Exception e) {
                    sender.sendMessage(new TextComponentString("Player " + args[0] + " doesn't exist."));
                    Reroll.LOGGER.error("Player " + args[0] + " doesn't exist.");
                }
                break;
            }
            default: {
                sender.sendMessage(new TextComponentString("Reroll command had too many arguments."));
                sender.sendMessage(new TextComponentString(getUsage(sender)));
                break;
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return super.checkPermission(server, sender);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.emptyList();
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
