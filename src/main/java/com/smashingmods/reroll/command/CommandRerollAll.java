package com.smashingmods.reroll.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.List;

public class CommandRerollAll extends CommandBase implements ICommand  {

    private final List aliases;

    public CommandRerollAll() {
        aliases = new ArrayList();
        aliases.add("rerolleveryone");
    }

    @Override
    public String getName() {
        return "rerollall";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.rerollall.usage";
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            server.getPlayerList().getPlayers().forEach(player -> {
                server.getCommandManager().executeCommand(player, String.format("/reroll"));
            });
            sender.sendMessage(new TextComponentTranslation("commands.rerollall.successful"));
            server.sendMessage(new TextComponentTranslation("commands.rerollall.successful"));
        } else {
            throw new CommandException("commands.rerollall.usage");
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return super.checkPermission(server, sender);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
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
