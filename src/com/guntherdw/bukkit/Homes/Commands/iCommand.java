package com.guntherdw.bukkit.Homes.Commands;

import com.guntherdw.bukkit.Homes.Homes;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author GuntherDW
 */
public interface iCommand {
    public abstract boolean executeCommand(CommandSender sender, String cmd, List<String> args, Homes plugin);

    /**
     * Get the command's main permission node, or null if there isn't one
     * This is without the homes. prefix!
     *
     * @return The node
     */
    public abstract String getPermissionSuffix();
}
