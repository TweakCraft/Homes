package com.guntherdw.bukkit.Homes;
import com.avaje.ebean.LogLevel;
import com.avaje.ebean.config.ServerConfig;
import com.guntherdw.bukkit.Homes.Commands.iCommand;
import com.guntherdw.bukkit.Homes.DataSource.DataSource;
import com.guntherdw.bukkit.Homes.DataSource.Sources.MySQL;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.ChatColor;
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
    public List<SaveHome> savehomes;
    public List<String> savehomesTCUtils;
    public TweakcraftUtils tweakcraftutils;
    public PluginDescriptionFile pdfFile = null;
    public DataSource ds;

    public SaveHome matchHome(String playername, String homename) {
		SaveHome rt = getSavehome(playername, homename);
		if(rt == null) {
			int delta = Integer.MAX_VALUE;
			for(SaveHome sh : savehomes) {
				if(sh.getName().equalsIgnoreCase(playername) && sh.getDescription().toLowerCase().contains(homename) && Math.abs(sh.getDescription().length() - homename.length()) < delta) {
					rt = sh;
					delta = Math.abs(sh.getDescription().length() - homename.length());
					if(delta == 0) break;
				}
			}
		}

		return rt;
	}

    public SaveHome getSavehome(String player, String description) {
        for(SaveHome sh : ds.getSaveHomes(player)) {
            if(sh.getDescription().toLowerCase().equals(description.toLowerCase())) {
                return sh;
            }
        }
        return null;
    }
    
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info("["+pdfFile.getName() + "] Homes version " + pdfFile.getVersion() + " is disabled!");
    }

    public DataSource getDataSource() {
        return ds;
    }

    public Logger getLogger() {
        return log;
    }

    public TweakcraftUtils getTweakcraftutils() {
        return tweakcraftutils;
    }

    public void onEnable() {
        savehomesTCUtils = new ArrayList<String>();
        pdfFile = this.getDescription();

        // this.setupDatabase();
        this.ds = new MySQL(this);
        this.reloadHomes();
        this.reloadSavehomes();
        this.setupPermissions();
        this.setupTCUtils();
        log.info("["+pdfFile.getName() + "] "+pdfFile.getName()+" version " + pdfFile.getVersion() + " is enabled!");
    }

    /* @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(SaveHome.class);
        list.add(Home.class);
        return list;
    } */

    public Map<String, Home> getHomesMap() {
        return homes;
    }

    /* public void setupDatabase() {
         try {
             getDatabase().find(Home.class).findRowCount();
             getDatabase().find(SaveHome.class).findRowCount();
         } catch (PersistenceException ex) {
             log.info("["+pdfFile.getName()+"] Installing database for " +pdfFile.getName()+ " due to first time usage");
             installDDL();
         }
    } */

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

        homes = this.ds.getHomesMap(); // this.getDatabase().find(Home.class).findList();
        /* for(Home h : homeslist) {
            homes.put(h.getName().toLowerCase(), h);
        } */
        log.info("["+pdfFile.getName() + "] Loaded " + homes.size() + " homes!");
    }

    public void reloadHomes(Player p) {
        // homes = new HashMap<String, Home>();

        /* List<Home> homeslist = this.getDatabase().find(Home.class).where().ieq("name", p.getName()).findList(); */
        Home h = this.ds.getHome(p.getName()); // )) {
        if(h!=null) {
            homes.put(h.getName().toLowerCase(), h);
        }
        log.info("["+pdfFile.getName() + "] Loaded " + p.getName() +"'s new home!");

    }

    public void reloadSavehomes() {
        savehomes = this.ds.getSaveHomes();
        log.info("["+pdfFile.getName() + "] Loaded " + (savehomes!=null?savehomes.size():"0") + " savehomes!");
    }


    public boolean check(Player player, String permNode) {
        return this.checkFull(player, "homes."+permNode);
    }

    public boolean checkFull(Player player, String permNode) {
        if (perm == null) {
            return true;
        } else {
            return player.isOp() ||
                    perm.getHandler().has(player, permNode);
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
            if(!IC.executeCommand(sender, command.getName(), argsa, this)) {
                sender.sendMessage(ChatColor.RED+"Something went wrong, contact an admin!");
            }
            return true;
        }

        return false;
    }
}