package com.guntherdw.bukkit.Homes;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.logging.Logger;

public class Homes extends JavaPlugin {

    private final HomesListener homeslistener = new HomesListener(this);
    private final static Logger log = Logger.getLogger("Minecraft");
    private static String name = "homes";
    private String db, user, pass;
    // private final Server ser;
    PluginDescriptionFile pdfFile;
    Connection conn;
    public static PermissionHandler Permissions = null;
    private Listener Listener = new Listener();


    /* public Homes(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);

    } */

    /* public void initialize() {
        // PluginListener listener = new HomesListener();
        // etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
        log.info(name + " initialized");
    } */

    /* public void disable() {
        etc.getInstance().removeCommand("/homes");
        log.info(name + " disabled");
    }

    public void enable() {
        etc.getInstance().addCommand("/homes", "- multihome support.");
        log.info(name + " enabled");
    } */

    public void onDisable() {
        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!");
    }

    public void onEnable() {
        //To change body of implemented methods use File | Settings | File Templates.
        this.db = getConfiguration().getString("database");
        this.user = getConfiguration().getString("username");
        this.pass = getConfiguration().getString("password");
        PluginManager pm = getServer().getPluginManager();
        // pm.registerEvent(Event.Type.PLAYER_COMMAND, homeslistener, Event.Priority.Normal, this);
        pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
        this.registerEvents();
        // ser = instance;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://localhost:3306/" + db;
            conn = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }



    private class Listener extends ServerListener {

        public Listener() {
        }

        @Override
        public void onPluginEnabled(PluginEvent event) {
            if(event.getPlugin().getDescription().getName().equals("Permissions")) {
                Homes.Permissions = ((Permissions)event.getPlugin()).Security;
                log.info("[Plugin Name] Attached plugin to Permissions. Enjoy~");
            }
        }
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, Listener, Event.Priority.Monitor, this);
    }

    public boolean check(Player player, String permNode) {
        if (Permissions == null) {
            return true;
        } else {
            return Permissions.permission(player, permNode);
        }
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (command.getName().equalsIgnoreCase("homes")) {
            String name = "";
            if (strings.length >= 3) {
                for (int i = 2; i < strings.length; i++)
                    name += strings[i] + " ";
                    name = name.trim();
            }

            String nameEscaped = name.replaceAll("%", "\\%").replaceAll("_", "\\_");

            if (strings.length >= 3 && name.length() > 0) {
                if (strings[0].equalsIgnoreCase("add")) {
                    PreparedStatement st = null;
                    try {
                        // Delete before inserting
                        st = conn.prepareStatement("DELETE FROM savehomes WHERE name = ? AND description = ?");
                        st.setString(1, player.getName());
                        st.setString(2, name);
                        st.executeUpdate();
                        st.close();

                        // Insert
                        st = conn.prepareStatement("INSERT INTO savehomes (name, world, x, y, z, rotX, rotY, description) VALUES (?,?,?,?,?,?,?,?)");
                        st.setString(1, player.getName());
                        st.setString(2, player.getLocation().getWorld().getName());
                        st.setDouble(3, player.getLocation().getX());
                        st.setDouble(4, player.getLocation().getY());
                        st.setDouble(5, player.getLocation().getZ());
                        st.setFloat(6, player.getLocation().getYaw());
                        st.setFloat(7, player.getLocation().getPitch());
                        st.setString(8, name);
                        st.executeUpdate();

                        player.sendMessage(ChatColor.GREEN + "Home '" + name + "' saved.");
                    } catch (Exception e) {
                        log.info("homes: Error saving '" + name + "' of player '" + player.getName() + "' error: ");
                        e.printStackTrace();
                    } finally {
                        try {
                            if (conn != null)
                                conn.close();
                            if (st != null)
                                st.close();
                        } catch (Exception e) {
                        }
                    }
                } else if (strings[0].equalsIgnoreCase("del")) {
                    PreparedStatement st = null;
                    ResultSet rs = null;
                    try {
                        st = conn.prepareStatement("SELECT description FROM savehomes WHERE name = ? AND description LIKE ? ORDER BY description");
                        st.setString(1, player.getName());
                        st.setString(2, nameEscaped + "%");
                        rs = st.executeQuery();

                        if (rs.next()) {
                            if (rs.isLast() || rs.getString(1).equalsIgnoreCase(name)) {
                                name = rs.getString(1);
                                rs.close();
                                st.close();

                                st = conn.prepareStatement("DELETE FROM savehomes WHERE name = ? AND description = ?");
                                st.setString(1, player.getName());
                                st.setString(2, name);
                                st.executeUpdate();

                                player.sendMessage(ChatColor.GREEN + "Home '" + name + "' deleted.");
                            } else {

                                String msg = ChatColor.LIGHT_PURPLE + "Found multiple results: " + rs.getString(1);
                                while (rs.next())
                                    msg += ", " + rs.getString(1);

                                player.sendMessage(msg);
                            }
                        } else {
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "No home found for: " + name);
                        }
                    } catch (Exception e) {
                        log.info("homes: Error deleting '" + name + "' of player '" + player.getName() + "' error: ");
                        e.printStackTrace();
                    } finally {
                        try {
                            /* if (conn != null)
                           conn.close(); */
                            if (st != null)
                                st.close();
                            if (rs != null)
                                rs.close();
                        } catch (Exception e) {
                        }
                    }
                } else if (strings[0].equalsIgnoreCase("use")) {
                    PreparedStatement st = null;
                    ResultSet rs = null;
                    try {
                        st = conn.prepareStatement("SELECT description, x, y, z, rotX, rotY, world FROM savehomes WHERE name = ? AND description LIKE ? ORDER BY description");
                        st.setString(1, player.getName());
                        st.setString(2, nameEscaped + "%");
                        rs = st.executeQuery();

                        if (rs.next()) {
                            if (rs.isLast() || rs.getString(1).equalsIgnoreCase(name)) {
                                /* Warp home = new Warp();
                               home.Location = new Location(rs.getDouble(2), rs.getDouble(3) + 1, rs.getDouble(4), rs.getFloat(5), rs.getFloat(6));
                               home.Group = "";
                               home.Name = player.getName();
                               etc.getInstance().changeHome(home); */
                                Home home = new Home(rs.getDouble(2), rs.getDouble(3) + 1, rs.getDouble(4), rs.getFloat(5), rs.getFloat(6), rs.getString(7));
                                st = conn.prepareStatement("UPDATE homes SET x = ?, y = ?, z = ?, rotX = ?, rotY = ?, world = ? WHERE name = ?");
                                st.setDouble(1, home.getX());
                                st.setDouble(2, home.getY());
                                st.setDouble(3, home.getZ());
                                st.setFloat(4, home.getYaw());
                                st.setFloat(5, home.getPitch());
                                st.setString(6, player.getName());
                                st.setString(7, home.getWorld());
                                st.executeUpdate();

                                player.sendMessage(ChatColor.GREEN + "Home '" + rs.getString(1) + "' loaded. It's your /home now!");
                            } else {
                                String msg = ChatColor.LIGHT_PURPLE + "Found multiple results: " + rs.getString(1);
                                while (rs.next())
                                    msg += ", " + rs.getString(1);

                                player.sendMessage(msg);
                            }
                        } else {
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "No home found for: " + name);
                        }
                    } catch (Exception e) {
                        log.info("homes: Error using '" + name + "' of player '" + player.getName() + "' error: ");
                        e.printStackTrace();
                    } finally {
                        try {
                            /* if (conn != null)
                           conn.close(); */
                            if (st != null)
                                st.close();
                            if (rs != null)
                                rs.close();
                        } catch (Exception e) {
                        }
                    }
                }
            } else if (strings[0].equalsIgnoreCase("list")) {
                PreparedStatement st = null;
                ResultSet rs = null;
                try {
                    st = conn.prepareStatement("SELECT description FROM savehomes WHERE name = ? ORDER BY description");
                    st.setString(1, player.getName());
                    rs = st.executeQuery();

                    String msg = ChatColor.GREEN.toString();
                    if (rs.next()) {
                        msg += "Your homes: " + ChatColor.WHITE + rs.getString(1);

                        while (rs.next())
                            msg += ", " + rs.getString(1);

                    } else {
                        msg += "No homes found";
                    }
                    player.sendMessage(msg);
                } catch (Exception e) {
                    log.info("homes: Error listing homes of player '" + player.getName() + "' error: ");
                    e.printStackTrace();
                } finally {
                    try {
                        /* if (conn != null)
                       conn.close(); */
                        if (st != null)
                            st.close();
                        if (rs != null)
                            rs.close();
                    } catch (Exception e) {
                    }
                }
            }
            return true;
        } else if (command.getName().equals("sethome")) {
            try {
                PreparedStatement st = null;
                // ResultSet rs = null;
                Home home = new Home(player.getLocation().getX(), player.getLocation().getY(),
                                     player.getLocation().getZ(), player.getLocation().getPitch(),
                                     player.getLocation().getYaw(), player.getLocation().getWorld().getName());
                st = conn.prepareStatement("UPDATE homes SET x = ?, y = ?, z = ?, rotX = ?, rotY = ?, world = ? WHERE name = ?");

                st.setDouble(1, home.getX());
                st.setDouble(2, home.getY());
                st.setDouble(3, home.getZ());
                st.setFloat(4, home.getYaw());
                st.setFloat(5, home.getPitch());
                st.setString(6, player.getName());
                st.setString(7, home.getWorld());
                st.executeUpdate();
                player.sendMessage(ChatColor.GREEN + "Successfully set your home!");
            } catch (SQLException e) {
                player.sendMessage(ChatColor.RED + "Something went wrong, contact an admin!");
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else if (command.getName().equals("home")) {
            try{
                boolean bo=false;
                PreparedStatement st = null;
                ResultSet rs = null;
                st = conn.prepareStatement("SELECT x, y, z, rotX, rotY, world FROM homes WHERE name = ?");
                if(strings.length!=0 && check(player, "tweakcraft.homesother"))
                {
                    bo=true;
                    st.setString(1, "%"+strings[1]+"%");
                } else {
                    st.setString(1, player.getName());
                }
                rs = st.executeQuery();
                if(rs.next())
                {
                    Home home = new Home(rs.getDouble(2), rs.getDouble(3) + 1, rs.getDouble(4), rs.getFloat(5), rs.getFloat(6), rs.getString(7));
                    Location loc = new Location(this.getServer().getWorld(home.getWorld()), home.getX(), home.getY(), home.getZ(), home.getYaw(), home.getPitch());
                    player.teleportTo(loc);
                } else {
                    if(!bo)
                        player.sendMessage(ChatColor.GOLD + "Can't find your home!");
                    else
                        player.sendMessage(ChatColor.GOLD + "Can't find that player's home!");
                }
            } catch (SQLException e) {
                player.sendMessage(ChatColor.RED + "Something went wrong, contact an admin!");
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }


        return false;

    }

}
