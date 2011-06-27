package com.guntherdw.bukkit.Homes.Commands;

import com.guntherdw.bukkit.Homes.Homes;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandHomes implements iCommand {
    // player.sendMessage(ChatColor.GREEN + "Usage: /homes add <alias>|del <alias>|use <alias>|list|tpback");
    public boolean executeCommand(CommandSender sender, String cmd, List<String> args, Homes plugin) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getPermissionSuffix() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
