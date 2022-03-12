package com.smashingmods.reroll.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.*;
import java.util.List;

import static com.smashingmods.reroll.command.RerollHandler.reroll;

public class CommandReroll extends CommandBase implements ICommand  {

    private final List aliases;

    public CommandReroll() {
        aliases = new ArrayList();
        aliases.add("rerollself");
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
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            reroll(server, sender, getCommandSenderAsPlayer(sender));
        } else {
            sender.sendMessage(new TextComponentString("commands.reroll.failure").setStyle(new Style().setColor(TextFormatting.RED)));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
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
