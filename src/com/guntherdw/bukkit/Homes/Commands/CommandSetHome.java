package com.guntherdw.bukkit.Homes.Commands;

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
                Home home = plugin.getDatabase().find(Home.class).where().ieq("name", player.getName()).findUnique();
                if(home==null) { home = new Home(); home.setName(player.getName()); }
                Location loc = player.getLocation();
                home.setX(loc.getX());
                home.setY(loc.getY()+0.5D);
                home.setZ(loc.getZ());
                home.setWorld(loc.getWorld().getName());
                home.setPitch(loc.getPitch());
                home.setYaw(loc.getYaw());

                plugin.getDatabase().save(home);

                // player.sendMessage("INTO homes (x,y,z,rotX,rotY,world,name) VALUES (?,?,?,?,?,?,?)");
                player.sendMessage(ChatColor.GREEN + "Successfully set your home!");
                plugin.getHomesMap().put(player.getName(), home);
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
