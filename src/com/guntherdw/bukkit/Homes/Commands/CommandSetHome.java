package com.guntherdw.bukkit.Homes.Commands;

import com.avaje.ebean.SqlUpdate;
import com.guntherdw.bukkit.Homes.Home;
import com.guntherdw.bukkit.Homes.Homes;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandSetHome implements iCommand {
    public boolean executeCommand(CommandSender sender, String cmd, List<String> args, Homes plugin) {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;

            if(plugin.check(player, "sethome"))
            {
                Home home = new Home(player.getName(), player.getLocation());

                plugin.getDataSource().saveHome(home);

                // player.sendMessage("INTO homes (x,y,z,rotX,rotY,world,name) VALUES (?,?,?,?,?,?,?)");
                player.sendMessage(ChatColor.GREEN + "Successfully set your home!");
                plugin.getHomesMap().put(player.getName().toLowerCase(), home);
                plugin.getLogger().info("[Homes] "+player.getName()+" set his home!");
            } else {
                player.sendMessage("You don't have permission to use /sethome!");
            }
        } else {
            sender.sendMessage("You need to be a player to set your home!");
        }
        return true;
    }

    public String getPermissionSuffix() {
        return "sethome";
    }
}
