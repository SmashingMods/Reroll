package com.smashingmods.reroll.command;

import com.smashingmods.reroll.Reroll;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.List;

import static com.smashingmods.reroll.command.RerollHandler.reroll;

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
        return "commands.rerollplayer.usage";
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (sender.getCommandSenderEntity() instanceof EntityPlayerMP) {
            if (args.length == 1) {
                EntityPlayerMP toReroll = server.getPlayerList().getPlayerByUsername(args[0]);

                if (toReroll != null) {
                    sender.sendMessage(new TextComponentTranslation("commands.rerollplayer.successful", new Object[]{getCommandSenderAsPlayer(sender).getName()}));
                    reroll(server, sender, toReroll);
                } else {
                    sender.sendMessage(new TextComponentTranslation("commands.rerollplayer.failure", new Object[]{args[0]}));
                }
            } else {
                throw new WrongUsageException("commands.rerollplayer.usage");
            }
        } else {
            Reroll.LOGGER.info("/rerollplayer can only be used in game.");
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return super.checkPermission(server, sender);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, sender.getServer().getOnlinePlayerNames()) : Collections.emptyList();
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
