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
public class CommandHome implements iCommand {
    public boolean executeCommand(CommandSender sender, String cmd, List<String> args, Homes plugin) {
        // try{
            if(sender instanceof Player)
            {
                Player player = (Player) sender;
                if(plugin.check(player, "home"))
                {
                    boolean bo=false;
                    String playername = "";
                    if(args.size()!=0 && plugin.check(player, "home.other"))
                    {
                        bo=true;
                        playername = args.get(0).toLowerCase();
                    } else {
                        playername = player.getName();
                    }
                    if(plugin.getHomesMap().containsKey(playername.toLowerCase())) {
                        Home h = plugin.getHomesMap().get(playername.toLowerCase());
                        Location loc = h.toLocation();
                        if(plugin.getServer().getWorld(h.getWorld())==null) {
                            player.sendMessage(ChatColor.DARK_AQUA + "That world doesn't exist anymore!");
                        }
                        loc.setWorld(plugin.getServer().getWorld(h.getWorld()));
                        /* new Location(plugin.getServer().getWorld(h.getWorld()),
                                h.getX(), h.getY(), h.getZ(), h.getYaw(), h.getPitch() ); */
                        if(plugin.getTweakcraftutils()!= null) {
                            if(!plugin.savehomesTCUtils.contains(player.getName())) {
                                plugin.getTweakcraftutils().getTelehistory().addHistory(player.getName(), player.getLocation());
                            }
                        }
                        player.teleport(loc);
                    } else {
                        if(bo)
                        {
                            player.sendMessage(ChatColor.DARK_AQUA + "Can't find that player's home!");
                            plugin.getLogger().info("[Homes] "+player.getName()+" tried to go to "+playername+"'s home!");
                        }
                        else{
                            plugin.getLogger().info("[Homes] "+player.getName()+" went home!");
                            player.sendMessage(ChatColor.DARK_AQUA + "Can't find your home!");
                        }
                    }
                } else {
                    player.sendMessage("You don't have permission to use /home!");
                }
            } else {
                sender.sendMessage("You need to be a player to go home!");
            }
            return true;
    }

    public String getPermissionSuffix() {
        return "home";
    }
}
