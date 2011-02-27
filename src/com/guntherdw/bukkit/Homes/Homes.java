package com.guntherdw.bukkit.Homes;

import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Homes extends JavaPlugin {
    private final static Logger log = Logger.getLogger("Minecraft");
    private String db, user, pass;
    // private static Connection conn;
    public static Permissions perm = null;
    public Map<String, Home> homes;

    public void onDisable() {
        //To change body of implemented methods use File | Settings | File Templates.
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("["+pdfFile.getName() + "] version " + pdfFile.getVersion() + " is disabled!");
    }

    public void setupConnection() {
        this.db =  getConfiguration().getString("database");
        this.user = getConfiguration().getString("username");
        this.pass = getConfiguration().getString("password");

        /* try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://localhost:3306/" + db;
            conn = DriverManager.getConnection(url, user, pass);
            this.reloadHomes();
        } catch (ClassNotFoundException e) {
            log.severe(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            log.severe(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            log.severe(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
        } catch (SQLException e) {
            log.severe(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
        } */
    }

    private void loadDriver() {
        final String driverName = "com.mysql.jdbc.Driver";
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            //return null;
        }
    }

    private Connection getConnection()
    {
        try {
            String url = "jdbc:mysql://localhost:3306/" + db;
            return DriverManager.getConnection(url + "?autoReconnect=true&user=" + user + "&password=" + pass);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }

    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        loadDriver();
        setupConnection();
        reloadHomes();
        this.setupPermissions();
        log.info("["+pdfFile.getName() + "] version " + pdfFile.getVersion() + " is enabled!");
    }

    public void setupPermissions() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Permissions");

        if (perm == null) {
            if (plugin != null) {
                perm = (Permissions) plugin;
            }
        }
    }

    /* public String searchHome(String homename)
    {
        for(String s : homes.keySet())
        {
            if(s.equalsIgnoreCase(warpname));
            return s;
        }
        return null;
    } */

    public void reloadHomes() {
        try {
            int count = 0;
            homes = new HashMap<String, Home>();
            Connection conn = getConnection();
            PreparedStatement st = null;
            ResultSet rs = null;
            st = conn.prepareStatement("SELECT name, x, y, z, rotX, rotY, world FROM homes");
            rs = st.executeQuery();

            while (rs.next()) {
                homes.put(rs.getString(1), new Home(rs.getDouble(2), rs.getDouble(3),
                        rs.getDouble(4), rs.getFloat(5), rs.getFloat(6), rs.getString(7)));
                count++;
            }
            log.info("[Homes] Loaded " + count + " homes!");

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public boolean check(Player player, String permNode) {
        if (perm == null) {
            return true;
        } else {
            return perm.Security.permission(player, permNode);
        }
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (command.getName().equalsIgnoreCase("homes")) {
            String name = "";
            if (strings.length >= 2) {
                for (int i = 1; i < strings.length; i++)
                    name += strings[i] + " ";
                    name = name.trim();
            }

            String nameEscaped = name.replaceAll("%", "\\%").replaceAll("_", "\\_");

            if (strings.length >= 2 && name.length() > 0) {
                Connection conn = getConnection();
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
                        log.info("[Homes] "+player.getName()+" saved a home! '"+name+"'");
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
                            log.info("[Homes] "+player.getName()+" deleted his '"+name+"' home!");
                        }
                    } catch (Exception e) {
                        log.info("homes: Error deleting '" + name + "' of player '" + player.getName() + "' error: ");
                        e.printStackTrace();
                    } finally {
                        try {
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
                                Home home = new Home(rs.getDouble(2), rs.getDouble(3) + 1, rs.getDouble(4), rs.getFloat(5), rs.getFloat(6), rs.getString(7));
                                st = conn.prepareStatement("UPDATE homes SET x = ?, y = ?, z = ?, rotX = ?, rotY = ?, world = ? WHERE name = ?");
                                st.setDouble(1, home.getX());
                                st.setDouble(2, home.getY());
                                st.setDouble(3, home.getZ());
                                st.setFloat(4, home.getYaw());
                                st.setFloat(5, home.getPitch());
                                st.setString(6, home.getWorld());
                                st.setString(7, player.getName());

                                if(st.executeUpdate()==0)
                                {
                                    player.sendMessage(ChatColor.RED + "Something went wrong, contact an admin!");
                                } else {
                                    player.sendMessage(ChatColor.GREEN + "Home '" + rs.getString(1) + "' loaded. It's your /home now!");
                                    log.info("[Homes] "+player.getName()+" set his home to '"+name+"'!");
                                    reloadHomes();
                                }
                            } else {
                                String msg = ChatColor.LIGHT_PURPLE + "Found multiple results: " + rs.getString(1);
                                while (rs.next())
                                    msg += ", " + rs.getString(1);

                                player.sendMessage(msg);
                                log.info("[Homes] "+player.getName()+" tried to set his home to '"+name+"' and got a list!");
                            }
                        } else {
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "No home found for: " + name);
                            log.info("[Homes] "+player.getName()+" tried to set his home to '"+name+"' but failed!");
                        }
                    } catch (Exception e) {
                        log.info("homes: Error using '" + name + "' of player '" + player.getName() + "' error: ");
                        e.printStackTrace();
                    } finally {
                        try {
                            if (st != null)
                                st.close();
                            if (rs != null)
                                rs.close();
                        } catch (Exception e) {
                        }
                    }
                }
            } else if (strings.length == 1 && strings[0].equalsIgnoreCase("list")) {
                Connection conn = getConnection();
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
                    log.info("[Homes] "+player.getName()+" listed his homes!");
                } catch (Exception e) {
                    log.info("homes: Error listing homes of player '" + player.getName() + "' error: ");
                    e.printStackTrace();
                } finally {
                    try {
                        if (st != null)
                            st.close();
                        if (rs != null)
                            rs.close();
                    } catch (Exception e) {
                    }
                }
            } else {
                player.sendMessage(ChatColor.GREEN + "Usage: /homes add <alias> | del <alias> | use <alias> | list");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("sethome")) {
            try {
                Connection conn = getConnection();
                PreparedStatement st = null;
                ResultSet rs = null;
                Home home = new Home(player.getLocation().getX(),
                                     player.getLocation().getY(),
                                     player.getLocation().getZ(),
                                     player.getLocation().getPitch(),
                                     player.getLocation().getYaw(),
                                     player.getLocation().getWorld().getName()
                );
                st = conn.prepareStatement("SELECT name FROM homes WHERE name = ?");
                st.setString(1, player.getName());
                rs = st.executeQuery();
                if(rs.next())
                {
                    st = conn.prepareStatement("UPDATE homes SET x = ?, y = ?, z = ?, rotX = ?, rotY = ?, world = ? WHERE name = ?");
                } else {
                    st = conn.prepareStatement("INSERT INTO homes (x,y,z,rotX,rotY,world,name) VALUES (?,?,?,?,?,?,?)");
                }
                st.setDouble(1, home.getX());
                st.setDouble(2, home.getY());
                st.setDouble(3, home.getZ());
                st.setFloat(5, home.getPitch());
                st.setFloat(4, home.getYaw());
                st.setString(6, home.getWorld());
                st.setString(7, player.getName());
                st.executeUpdate();
                // player.sendMessage("INTO homes (x,y,z,rotX,rotY,world,name) VALUES (?,?,?,?,?,?,?)");
                player.sendMessage(ChatColor.GREEN + "Successfully set your home!");
                log.info("[Homes] "+player.getName()+" set his home!");
                /* rs = st.executeUpdate();
                if(!=1) {
                    player.sendMessage(ChatColor.GREEN + "Successfully set your home!");
                } else {
                    log.severe ("[TweakHomes] sethome warning : "+st.getWarnings());
                    player.sendMessage(ChatColor.RED + "There was an error setting your home!");
                    player.sendMessage(st.getWarnings());
                } */
            } catch (SQLException e) {
                player.sendMessage(ChatColor.RED + "Something went wrong, contact an admin!");
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("home")) {
            // try{
                boolean bo=false;
                String playername = "";
                if(strings.length!=0 && check(player, "homes.homesother"))
                {
                    bo=true;
                    playername = strings[0];
                } else {
                    playername = player.getName();
                }
                if(homes.containsKey(playername)) {
                    Home h = homes.get(playername);
                    Location loc = new Location(getServer().getWorld(h.getWorld()),
                            h.getX(), h.getY(), h.getZ(), h.getYaw(), h.getPitch());
                    player.teleportTo(loc);
                } else {
                    if(bo)
                    {
                        player.sendMessage(ChatColor.DARK_AQUA + "Can't find that player's home!");
                        log.info("[Homes] "+player.getName()+" tried to go to "+playername+"'s home!");
                    }
                    else{
                        log.info("[Homes] "+player.getName()+" went home!");
                        player.sendMessage(ChatColor.DARK_AQUA + "Can't find your home!");
                    }
                }
                return true;
                /* PreparedStatement st = null;
                ResultSet rs = null;
                // st = conn.prepareStatement("SELECT x, y, z, rotX, rotY, world FROM homes WHERE name LIKE ?");
                if(strings.length!=0 && check(player, "tweakcraft.homesother"))
                {
                    bo=true;
                    st.setString(1, "%"+strings[0]+"%");
                } else {
                    st.setString(1, player.getName());
                }
                rs = st.executeQuery();
                if(rs.next())
                {
                    log.info("Teleporting "+player.getName() + " to " + rs.getDouble(1));
                    if(bo) log.info("Teleporting to player " + strings[0]);
                    Home home = new Home(rs.getDouble(1), rs.getDouble(2) + 1, rs.getDouble(3), rs.getFloat(4), rs.getFloat(5), rs.getString(6));
                    Location loc = new Location(this.getServer().getWorld(home.getWorld()), home.getX(), home.getY(), home.getZ(), home.getPitch(), home.getYaw());
                    player.teleportTo(loc);
                } else {
                    if(!bo)
                        player.sendMessage(ChatColor.GOLD + "Can't find your home!");
                    else
                        player.sendMessage(ChatColor.GOLD + "Can't find that player's home!");
                }
                return true;
            } catch (SQLException e) {
                player.sendMessage(ChatColor.RED + "Something went wrong, contact an admin!");
                log.warning ("[TweakHomes] home warning : "+e.getMessage());
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } */
            // }
        } else if(command.getName().equalsIgnoreCase("reloadhomes") &&
                    check(player, "homes.homesother")) {
                player.sendMessage(ChatColor.GREEN + "Reloading homes map!");
            log.info("[Homes] "+player.getName()+" issued /reloadhomes!");
                setupConnection();
                reloadHomes();
                return true;
        }
        return false;

    }

}
