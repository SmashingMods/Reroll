package com.smashingmods.reroll.command;

import com.smashingmods.reroll.capability.RerollCapability;
import com.smashingmods.reroll.capability.RerollCapabilityImplementation;
import com.smashingmods.reroll.config.Config;
import com.smashingmods.reroll.handler.RerollHandler;
import net.minecraft.command.*;
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

public class CommandReroll extends CommandBase implements ICommand  {

    private static final RerollHandler HANDLER = new RerollHandler();

    public CommandReroll() {}

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
                    } else {
                        sender.sendMessage(new TextComponentTranslation("commands.reroll.item_required"));
                    }
                } else {
                    rerollSelf(server, sender);
                }
                break;
            }
            case 1: {
                switch (args[0]) {
                    case "lock": {
                        lockSelf(sender);
                        break;
                    }
                    case "unlock": {
                        unlockSelf(sender);
                        break;
                    }
                    case "all": {
                        rerollAll(server, sender);
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
                            lockPlayer(server, sender, args[1]);
                            break;
                        }
                        case "unlock": {
                            unlockPlayer(server, sender, args[1]);
                            break;
                        }
                        case "player": {
                            rerollPlayer(server, sender, args[1]);
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

    private static void rerollSelf(MinecraftServer server, ICommandSender sender) {
        if (sender instanceof EntityPlayerMP) {
            RerollCapabilityImplementation rerollCapability = ((EntityPlayerMP) sender).getCapability(RerollCapability.REROLL_CAPABILITY, null);
            if (Objects.requireNonNull(rerollCapability).getLock()) {
                sender.sendMessage(new TextComponentTranslation("commands.reroll.self.locked").setStyle(new Style().setColor(TextFormatting.RED)));
            } else {
                HANDLER.reroll(server, (EntityPlayerMP) sender, true);
                sender.sendMessage(new TextComponentTranslation("commands.reroll.self"));
            }
        } else {
            sender.sendMessage(new TextComponentTranslation("commands.reroll.server"));
        }
    }

    private static void lockSelf(ICommandSender sender) throws PlayerNotFoundException {
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP entityplayer = getCommandSenderAsPlayer(sender);
            RerollCapabilityImplementation rerollCapability = entityplayer.getCapability(RerollCapability.REROLL_CAPABILITY, null);
            Objects.requireNonNull(rerollCapability).setLock(true);
            sender.sendMessage(new TextComponentTranslation("commands.reroll.self.lock", entityplayer.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
        } else {
            sender.sendMessage(new TextComponentTranslation("commands.reroll.self.lock"));
        }
    }

    private static void unlockSelf(ICommandSender sender) throws PlayerNotFoundException {
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP entityplayer = getCommandSenderAsPlayer(sender);
            RerollCapabilityImplementation rerollCapability = entityplayer.getCapability(RerollCapability.REROLL_CAPABILITY, null);
            Objects.requireNonNull(rerollCapability).setLock(false);
            sender.sendMessage(new TextComponentTranslation("commands.reroll.self.unlock", entityplayer.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
        } else {
            sender.sendMessage(new TextComponentTranslation("commands.reroll.server"));
        }
    }

    private static void rerollPlayer(MinecraftServer server, ICommandSender sender, String playerName) {
        EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(playerName);
        if (player != null) {
            RerollCapabilityImplementation rerollCapability = player.getCapability(RerollCapability.REROLL_CAPABILITY, null);
            if (Objects.requireNonNull(rerollCapability).getLock()) {
                sender.sendMessage(new TextComponentTranslation("commands.reroll.player.locked", player.getName()).setStyle(new Style().setColor(TextFormatting.RED)));
            } else {
                HANDLER.reroll(server, player, true);
                if (player != sender) {
                    player.sendMessage(new TextComponentTranslation("commands.reroll.player.rerolled"));
                }
                sender.sendMessage(new TextComponentTranslation("commands.reroll.player", player.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
            }
        } else {
            sender.sendMessage(new TextComponentTranslation("commands.reroll.playernotfound", playerName).setStyle(new Style().setColor(TextFormatting.RED)));
        }
    }

    private static void lockPlayer(MinecraftServer server, ICommandSender sender, String playerName) {
        EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(playerName);
        if (player != null) {
            RerollCapabilityImplementation rerollCapability = player.getCapability(RerollCapability.REROLL_CAPABILITY, null);
            Objects.requireNonNull(rerollCapability).setLock(true);
            if (player != sender) {
                player.sendMessage(new TextComponentTranslation("commands.reroll.self.lock", player.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
            }
            sender.sendMessage(new TextComponentTranslation("commands.reroll.player.lock", player.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
        } else {
            sender.sendMessage(new TextComponentTranslation("commands.reroll.playernotfound", playerName).setStyle(new Style().setColor(TextFormatting.RED)));
        }
    }

    private static void unlockPlayer(MinecraftServer server, ICommandSender sender, String playerName) {
        EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(playerName);
        if (player != null) {
            RerollCapabilityImplementation rerollCapability = player.getCapability(RerollCapability.REROLL_CAPABILITY, null);
            Objects.requireNonNull(rerollCapability).setLock(false);
            if (player != sender) {
                player.sendMessage(new TextComponentTranslation("commands.reroll.self.unlock", player.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
            }
            sender.sendMessage(new TextComponentTranslation("commands.reroll.player.unlock", player.getName()).setStyle(new Style().setColor(TextFormatting.AQUA)));
        } else {
            sender.sendMessage(new TextComponentTranslation("commands.reroll.playernotfound", playerName).setStyle(new Style().setColor(TextFormatting.RED)));
        }
    }

    private static void rerollAll(MinecraftServer server, ICommandSender sender) {
        server.getPlayerList().getPlayers().forEach(player -> {
            RerollCapabilityImplementation rerollCapability = player.getCapability(RerollCapability.REROLL_CAPABILITY, null);
            if (Objects.requireNonNull(rerollCapability).getLock()) {
                sender.sendMessage(new TextComponentTranslation("commands.reroll.player.locked", player.getName()).setStyle(new Style().setColor(TextFormatting.RED)));
            } else {
                HANDLER.reroll(server, player, !Config.rerollAllTogether);
                if (player != sender) {
                    player.sendMessage(new TextComponentTranslation("commands.reroll.player"));
                } else {
                    sender.sendMessage(new TextComponentTranslation("commands.reroll.player.rerolled"));
                }
            }
        });
        if (Config.rerollAllTogether) HANDLER.setNext(sender.getEntityWorld());
        sender.sendMessage(new TextComponentTranslation("commands.reroll.all").setStyle(new Style().setColor(TextFormatting.AQUA)));
    }
}
