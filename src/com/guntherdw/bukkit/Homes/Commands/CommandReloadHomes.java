package com.guntherdw.bukkit.Homes.Commands;

import com.guntherdw.bukkit.Homes.Homes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandReloadHomes implements iCommand {
    public boolean executeCommand(CommandSender sender, String cmd, List<String> args, Homes plugin) {
        if(sender instanceof Player)
            if(!plugin.check((Player)sender, getPermissionSuffix())) {
                sender.sendMessage(ChatColor.RED + "What do you think you are doing?");
                return true;
            }

        if(args.size()>0) {
            List<Player> ps = plugin.getServer().matchPlayer(args.get(0));
            Player play = null;
            if(ps.size()==1) {
                play = ps.get(0);
            } else {
                sender.sendMessage(ChatColor.RED+"Can't find player!");
            }
            if(play!=null) {
                sender.sendMessage(ChatColor.GREEN + "Reloading "+play.getName()+"'s home!");
                plugin.reloadHomes(play);
            }

        } else {
            sender.sendMessage(ChatColor.GREEN + "Reloading all homes!");
            plugin.reloadHomes();
        }

        return true;
    }

    public String getPermissionSuffix() {
        return "reloadhomes";
    }
}
