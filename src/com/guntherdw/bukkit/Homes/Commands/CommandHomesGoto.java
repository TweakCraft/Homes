package com.guntherdw.bukkit.Homes.Commands;

import com.guntherdw.bukkit.Homes.Homes;
import com.guntherdw.bukkit.Homes.SaveHome;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandHomesGoto implements iCommand {
    public boolean gotoHomes(CommandSender sender, String cmd, List<String> args, Homes plugin, boolean skipfirst) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.YELLOW+"Why would you even TRY?");
            return true;
        }
        Player player = (Player) sender;
        String searchstr = "";
        for(int x = skipfirst?0:1; x<args.size(); x++) {
            searchstr+= args.get(x)+" ";
        }
        if(searchstr.length()>0) searchstr = searchstr.trim();
        SaveHome searchhome = plugin.matchHome(player.getName(), searchstr);

        if(searchhome!=null) {

            sender.sendMessage(ChatColor.GREEN+"Teleporting you to Home '"+searchhome.getDescription()+"'!");
            plugin.getLogger().info("[Homes] "+player.getName()+" teleported to home with name "+searchhome.getDescription());
            if(plugin.getTweakcraftutils()!= null) {
                if(!plugin.savehomesTCUtils.contains(player.getName())) {
                    plugin.getTweakcraftutils().getTelehistory().addHistory(player.getName(), player.getLocation());
                }
            }
            Location loc = searchhome.getLocation();
            player.teleport(loc);

            return true;
        } else {
            sender.sendMessage(ChatColor.GREEN + "No homes by that name found!");
            return false;
        }
    }

    public boolean executeCommand(CommandSender sender, String cmd, List<String> args, Homes plugin) {
        if(args.size()==0) { sender.sendMessage(ChatColor.YELLOW+"What?"); return true; }
        else gotoHomes(sender, cmd, args, plugin, true);

        return true;
    }

    public String getPermissionSuffix() {
        return "homes.homes";
    }
}
