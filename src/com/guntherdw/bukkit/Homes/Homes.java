package com.guntherdw.bukkit.Homes;
import com.guntherdw.bukkit.Homes.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Homes extends JavaPlugin {
    private final static Logger log = Logger.getLogger("Minecraft");
    public static Permissions perm = null;
    private CommandHandler chandler = new CommandHandler(this);
    public Map<String, Home> homes;
    public List<String> savehomesTCUtils;
    public TweakcraftUtils tweakcraftutils;
    public PluginDescriptionFile pdfFile = null;

    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info("["+pdfFile.getName() + "] Homes version " + pdfFile.getVersion() + " is disabled!");
    }

    public Logger getLogger() {
        return log;
    }

    public void onEnable() {
        savehomesTCUtils = new ArrayList<String>();
        pdfFile = this.getDescription();
        reloadHomes();
        this.setupPermissions();
        this.setupTCUtils();
        log.info("["+pdfFile.getName() + "] "+pdfFile.getName()+" version " + pdfFile.getVersion() + " is enabled!");
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(Home.class);
        list.add(SaveHome.class);
        return list;
    }

    public Map<String, Home> getHomesMap() {
        return homes;
    }

    public void setupDatabase() {
         try {
             getDatabase().find(SaveHome.class).findRowCount();
             getDatabase().find(Homes.class).findRowCount();

         } catch (PersistenceException ex) {
             log.info("["+pdfFile.getName()+"] Installing database for " +pdfFile.getName()+ " due to first time usage");
             installDDL();
         }
    }

    public void setupPermissions() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Permissions");

        if (perm == null) {
            if (plugin != null) {
                perm = (Permissions) plugin;
            }
        }
    }

    public void setupTCUtils() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("TweakcraftUtils");

        if (tweakcraftutils == null) {
            if (plugin != null) {
                tweakcraftutils = (TweakcraftUtils) plugin;
            }
        }
    }


    public void reloadHomes() {
        homes = new HashMap<String, Home>();

        List<Home> homeslist = this.getDatabase().find(Home.class).findList();
        for(Home h : homeslist) {
            homes.put(h.getName(), h);
        }
        log.info("["+pdfFile.getName() + "] Loaded " + homes.size() + " homes!");
    }

    public void reloadHomes(Player p) {
        homes = new HashMap<String, Home>();

        List<Home> homeslist = this.getDatabase().find(Home.class).where().ieq("name", p.getName()).findList();
        for(Home h : homeslist) {
            homes.put(h.getName(), h);
        }
        log.info("["+pdfFile.getName() + "] Loaded " + p.getName() +"'s new home!");

    }


    public boolean check(Player player, String permNode) {
        if (perm == null) {
            return true;
        } else {
            return player.isOp() ||
                    perm.getHandler().has(player, "homes."+permNode);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] unfilteredargs) {

        List<String> argsa = new ArrayList<String>();

        for(String a : unfilteredargs) {
            if(a!=null&& !a.isEmpty() && !a.trim().equals("")) {
                argsa.add(a);
            }
        }

        iCommand IC = chandler.getCommand(command.getName());
        if(IC!=null) {
            /* public abstract boolean executeCommand(CommandSender sender, String cmd, List<String> args, Homes plugin); */
            if(IC.executeCommand(sender, command.getName(), argsa, this)) {
                // success

            } else {
                // failure
            }
            return true;
        }

        return false;
    }
}