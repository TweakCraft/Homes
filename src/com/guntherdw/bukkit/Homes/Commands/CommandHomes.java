package com.guntherdw.bukkit.Homes.Commands;

import com.guntherdw.bukkit.Homes.Home;
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
public class CommandHomes implements iCommand {
    // player.sendMessage(ChatColor.GREEN + "Usage: /homes add <alias>|del <alias>|use <alias>|list|tpback");

    private enum homesCommand {
        USE,
        ADD,
        DELETE,
        LIST,
        GOTO,
        TPBACK
    }

    public boolean executeCommand(CommandSender sender, String cmd, List<String> args, Homes plugin) {

        if(!(sender instanceof Player)) {
            sender.sendMessage("You need to be a player to use homes");
            return true;
        }

        Player player = (Player) sender;
        
        if(!plugin.check(player, "homes"))
        {
            player.sendMessage("You don't have permission to use homes!");
            return true;
        }
        String name = "";
        homesCommand hcmd = null;
        if (args.size() >= 2) {
            for (int i = 1; i < args.size(); i++)
                name += args.get(i) + " ";
            name = name.trim();
        }


        boolean skipfirst = false;

        if (args.size() >= 1) {
            String mode = args.get(0).toLowerCase();
            if(mode.equals("use")) hcmd = homesCommand.USE;
            else if(mode.equals("add")) hcmd = homesCommand.ADD;
            else if(mode.equals("delete")
                    || mode.equalsIgnoreCase("del")
                    || mode.equals("remove")) hcmd = homesCommand.DELETE;
            else if(mode.equals("list")) hcmd = homesCommand.LIST;
            else if(mode.equals("goto")) hcmd = homesCommand.GOTO;
            else if(mode.equals("tpback")) hcmd = homesCommand.TPBACK;
            else { hcmd = homesCommand.USE; skipfirst = true; }
        }

        if(hcmd==null) {
            player.sendMessage(ChatColor.GREEN + "Usage: /homes add <alias>|del <alias>|use <alias>|goto <alias>|list|tpback");
            return true;
        }

        switch(hcmd) {
            case USE:
                if(plugin.check(player, "use"))
                    this.useHomes(player, cmd, args, plugin, skipfirst);
                else
                    player.sendMessage(ChatColor.RED+"You don't have permission for that!");
                break;
            case ADD:
                if(plugin.check(player, "add"))
                    this.addHomes(player, cmd, args, plugin, skipfirst);
                else
                    player.sendMessage(ChatColor.RED+"You don't have permission for that!");
                break;
            case DELETE:
                if(plugin.check(player, "delete"))
                    this.delHomes(player, cmd, args, plugin, skipfirst);
                else
                    player.sendMessage(ChatColor.RED+"You don't have permission for that!");
                break;
            case LIST:
                if(plugin.check(player, "list"))
                    this.listHomes(player, cmd, args, plugin, skipfirst);
                else
                    player.sendMessage(ChatColor.RED+"You don't have permission for that!");
                break;
            case GOTO:
                if(plugin.check(player, "goto"))
                    this.gotoHomes(player, cmd, args, plugin, skipfirst);
                else
                    player.sendMessage(ChatColor.RED+"You don't have permission for that!");
                break;
            case TPBACK:
                //if(plugin.check(player, "use"))
                this.tpBackToggle(player, cmd, args, plugin, skipfirst);
                break;
        }

        return true;
    }

    public String getPermissionSuffix() {
        return "homes";
    }

    public boolean addHomes(Player player, String cmd, List<String> args, Homes plugin, boolean skipfirst){
        String playername = player.getName();
        String searchstr = "";
        for(int x = skipfirst?0:1; x<args.size(); x++) {
            searchstr+= args.get(x)+" ";
        }
        if(searchstr.length()>0) searchstr = searchstr.trim();
        
        Location loc = player.getLocation();
        SaveHome savehome = new SaveHome(player.getName(), searchstr, loc);

        plugin.getDataSource().addSavehome(player.getName(), savehome);
        plugin.reloadSavehomes();
        plugin.getLogger().info("[Homes] "+player.getName()+" added home with name "+savehome.getDescription()+" at location world:"+loc.getWorld()+" x:"+loc.getX()+" y:"+loc.getY()+" z:"+loc.getZ());;
        player.sendMessage(ChatColor.GREEN + "Home '" + savehome.getDescription() + "' saved.");
        // sender.sendMessage("It should be at "+savehome.getLocation().toString());
        return true;
    }

    public boolean useHomes(Player player, String cmd, List<String> args, Homes plugin, boolean skipfirst){
        String searchstr = "";
        for(int x = skipfirst?0:1; x<args.size(); x++) {
            searchstr+= args.get(x)+" ";
        }
        if(searchstr.length()>0) searchstr = searchstr.trim();
        SaveHome searchhome = plugin.matchHome(player.getName(), searchstr);
        if(searchhome!=null){
            Home home = new Home();
            home.setSaveHome(searchhome);
            plugin.getHomesMap().put(player.getName().toLowerCase(), home);

            player.sendMessage(ChatColor.GREEN + "Home '" + searchhome.getDescription() + "' loaded. It's your /home now!");
            plugin.getLogger().info("[Homes] "+player.getName()+" loaded home with name "+searchhome.getDescription());
            plugin.getDataSource().saveHome(home);

            return true;
        } else {
            player.sendMessage(ChatColor.GREEN + "No homes by that name found!");
            return false;
        }
    }

    public boolean gotoHomes(Player player, String cmd, List<String> args, Homes plugin, boolean skipfirst){
        String searchstr = "";
        for(int x = skipfirst?0:1; x<args.size(); x++) {
            searchstr+= args.get(x)+" ";
        }
        if(searchstr.length()>0) searchstr = searchstr.trim();
        SaveHome searchhome = plugin.matchHome(player.getName(), searchstr);
        if(searchhome!=null) {

            player.sendMessage(ChatColor.GREEN+"Teleporting you to Home '"+searchhome.getDescription()+"'!");
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
            player.sendMessage(ChatColor.GREEN + "No homes by that name found!");
            return false;
        }
    }

    public boolean delHomes(Player player, String cmd, List<String> args, Homes plugin, boolean skipfirst){
        String searchstr = "";
        for(int x = skipfirst?0:1; x<args.size(); x++) {
            searchstr+= args.get(x)+" ";
        }
        if(searchstr.length()>0) searchstr = searchstr.trim();
        SaveHome searchhome = plugin.matchHome(player.getName(), searchstr);
        if(searchhome!=null) {
            if(plugin.getDataSource().deleteHome(player.getName(), searchhome.getId())) {
                player.sendMessage(ChatColor.GREEN+"Deleted SaveHome with description '"+searchhome.getDescription()+"'!");
                plugin.getLogger().info("[Homes] "+player.getName()+" deleted home with name "+searchhome.getDescription());
                plugin.reloadSavehomes();
            } else {
                player.sendMessage(ChatColor.GREEN+"Failure during deletion!");
            }
            return true;
        } else {
            player.sendMessage(ChatColor.GREEN + "No homes by that name found!");
            return false;
        }
    }

    public boolean listHomes(Player player, String cmd, List<String> args, Homes plugin, boolean skipfirst){
        String msg = ChatColor.GREEN.toString();
        List<SaveHome> searchhomelist = plugin.getDataSource().getSaveHomes(player.getName()); // plugin.getDatabase().find(SaveHome.class).where().ieq("name", ((Player) sender).getName()).findList();
        if (searchhomelist!=null && searchhomelist.size()>0) {
            msg += "Your homes: " + ChatColor.WHITE;

            for(SaveHome svh : searchhomelist)
                msg += svh.getDescription()+", ";
            msg=msg.substring(0, msg.length()-2);

        } else {
            msg += "No homes found";
        }
        plugin.getLogger().info("[Homes] "+player.getName()+" listed his homes");
        player.sendMessage(msg);
        return true;
    }
    
    public boolean tpBackToggle(Player player, String cmd, List<String> args, Homes plugin, boolean skipfirst){
        if(plugin.checkFull(player, "tweakcraftutils.tpback")) {
            if(plugin.savehomesTCUtils.contains(player.getName())) {
                plugin.savehomesTCUtils.remove(player.getName());
                player.sendMessage(ChatColor.GOLD+"Going home will set a tpback entry!");
            } else {
                plugin.savehomesTCUtils.add(player.getName());
                player.sendMessage(ChatColor.GOLD+"Going home will no longer set a tpback entry!");
            }
        } else {
            player.sendMessage("You don't have permission to tpback, so this would be useless!");
        }
        return true;
    }

}
