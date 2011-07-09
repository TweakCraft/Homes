package com.guntherdw.bukkit.Homes.DataSource.Sources;

import com.guntherdw.bukkit.Homes.DataSource.DataSource;
import com.guntherdw.bukkit.Homes.Home;
import com.guntherdw.bukkit.Homes.Homes;
import com.guntherdw.bukkit.Homes.SaveHome;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

/**
 * @author GuntherDW
 */
public class MySQL extends DataSource {

    private Homes plugin;
    private String db, user, pass, dbhost;

    public MySQL(Homes instance) {
        this.plugin = instance;
        this.loadDriver();
        this.setupConnection();
    }

    public void initConfig()
    {
        try{
            plugin.getConfiguration().setProperty("database", "databasename");
            plugin.getConfiguration().setProperty("username", "database-username");
            plugin.getConfiguration().setProperty("password", "database-password");
        } catch (Throwable e)
        {
            plugin.getLogger().severe("[Homes] There was an exception while we were saving the config, be sure to doublecheck!");
        }
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
            String url = "jdbc:mysql://"+dbhost+":3306/" + db;
            return DriverManager.getConnection(url + "?autoReconnect=true&user=" + user + "&password=" + pass);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }

    public void setupConnection() {
        this.dbhost = plugin.getConfiguration().getString("dbhost");
        this.db =  plugin.getConfiguration().getString("database");
        this.user = plugin.getConfiguration().getString("username");
        this.pass = plugin.getConfiguration().getString("password");
    }

    @Override
    public void saveHome(Object homeObject) {
        if(homeObject instanceof SaveHome)
            this.addSavehome(((SaveHome)homeObject).getName(), ((SaveHome) homeObject));
        else if(homeObject instanceof Home)
            this.addHome(((Home) homeObject).getName(), ((Home) homeObject));
        else
            plugin.getLogger().warning("[Homes] saveHome tried to do something that was not supported!");
    }

    @Override
    public Home getHome(String player) {
        Connection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        Home h = null;
        try {
            int count = 0;
            st = conn.prepareStatement("SELECT name, x, y, z, yaw, pitch, world, id FROM homes WHERE name = ?");
            st.setString(1, player);
            rs = st.executeQuery();

            while (rs.next()) {
                h = new Home(rs.getInt(8), rs.getString(1), rs.getDouble(2), rs.getDouble(3),
                        rs.getDouble(4), rs.getFloat(5), rs.getFloat(6), rs.getString(7));
                plugin.getHomesMap().put(rs.getString(1).toLowerCase(), h);
                count++;
            }
            // plugin.getLogger().info("[Homes] Loaded " + count + " homes!");

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return h;
    }

    @Override
    public Map<String, Home> getHomesMap() {
        Map<String, Home> homes = new HashMap<String, Home>();
        Connection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            int count = 0;
            st = conn.prepareStatement("SELECT name, x, y, z, yaw, pitch, world, id  FROM homes");
            rs = st.executeQuery();

            while (rs.next()) {
                homes.put(rs.getString(1).toLowerCase(), new Home(rs.getInt(8), rs.getString(1), rs.getDouble(2), rs.getDouble(3),
                        rs.getDouble(4), rs.getFloat(5), rs.getFloat(6), rs.getString(7)));
                count++;
            }
            // plugin.getLogger().info("[Homes] Loaded " + count + " homes!");

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return homes;
    }

    @Override
    public List<Home> getHomes() {
        return new ArrayList<Home>(this.getHomesMap().values());
    }

    @Override
    public List<SaveHome> getSaveHomes() {
        List<SaveHome> shomes = null;
        Connection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            int count = 0;
            st = conn.prepareStatement("SELECT name, x, y, z, yaw, pitch, world, description, id  FROM savehomes");
            rs = st.executeQuery();

            while (rs.next()) {
                if(shomes==null) shomes = new ArrayList<SaveHome>();
                shomes.add(new SaveHome(rs.getInt(9), rs.getString(8), rs.getString(1), rs.getDouble(2), rs.getDouble(3),
                        rs.getDouble(4), rs.getFloat(5), rs.getFloat(6), rs.getString(7)));
                count++;
            }
            // plugin.getLogger().info("[Homes] Loaded " + count + " homes!");
        } catch(SQLException ex) {
            plugin.getLogger().info("[Homes] Error getting savehomes!");
            ex.printStackTrace();
        } finally {
            try {
                if (st != null)
                    st.close();
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
        }
        return shomes;
    }

    @Override
    public SaveHome getSaveHome(String player, int savehomeid) {
        Connection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        SaveHome sh = null;
        try {
            st = conn.prepareStatement("SELECT description, x, y, z, yaw, pitch, world, id, name FROM savehomes WHERE name = ?");
            st.setString(1, player);
            st.setInt(2, savehomeid);
            rs = st.executeQuery();

            if (rs.next()) {
                sh = new SaveHome(rs.getInt(8), rs.getString(1), rs.getString(9), rs.getDouble(2), rs.getDouble(3),
                        rs.getDouble(4), rs.getFloat(5), rs.getFloat(6), rs.getString(7));
            } else {
                plugin.getLogger().info("[Homes] No home found by that ID!");
            }

        } catch (Exception e) {
            plugin.getLogger().info("[Homes] Error using id:'" + savehomeid + "' of player '" + player + "' error: ");
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
        return sh;
    }

    @Override
    public List<SaveHome> getSaveHomes(String player) {
        Connection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        List<SaveHome> savehomes = null;

        boolean rancorrect = false;
        try {
            st = conn.prepareStatement("SELECT description, x, y, z, yaw, pitch, world, id, name FROM savehomes WHERE name = ?");
            st.setString(1, player);
            rs = st.executeQuery();
            SaveHome sh = null;


            while (rs.next()) {
                if(savehomes==null) savehomes = new ArrayList<SaveHome>();
                sh = new SaveHome(rs.getInt(8), rs.getString(1), rs.getString(9), rs.getDouble(2), rs.getDouble(3),
                        rs.getDouble(4), rs.getFloat(5), rs.getFloat(6), rs.getString(7));
                savehomes.add(sh);
            }
        } catch (Exception e) {
            plugin.getLogger().info("[Homes] Error listing homes of player "+player+"!");
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
        return savehomes;
    }



    @Override
    public boolean deleteHome(String player, int savehomeid) {
        Connection conn = getConnection();
        PreparedStatement st = null;
        boolean rancorrect = false;
        try{
            st = conn.prepareStatement("DELETE FROM savehomes WHERE name = ? AND id = ?");
            st.setString(1, player);
            st.setInt(2, savehomeid);
            rancorrect = st.executeUpdate() > 0;
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                if (st != null)
                    st.close();
            } catch (Exception e) {
            }
        }
        return rancorrect;
    }

    @Override
    public boolean addSavehome(String player, SaveHome savehome) {
        Connection conn = getConnection();
        PreparedStatement st = null;
        try {
            // Delete before inserting
            st = conn.prepareStatement("DELETE FROM savehomes WHERE name = ? AND description = ?");
            st.setString(1, player);
            st.setString(2, savehome.getDescription());
            st.executeUpdate();
            st.close();

            // Insert
            st = conn.prepareStatement("INSERT INTO savehomes (name, world, x, y, z, yaw, pitch, description) VALUES (?,?,?,?,?,?,?,?)");
            Location loc = savehome.getLocation();
            st.setString(1, savehome.getName());
            st.setString(2, loc.getWorld().getName());
            st.setDouble(3, loc.getX());
            st.setDouble(4, loc.getY());
            st.setDouble(5, loc.getZ());

            st.setFloat(6, loc.getYaw());
            st.setFloat(7, loc.getPitch());

            st.setString(8, savehome.getDescription());
            st.executeUpdate();

            plugin.getLogger().info("[Homes] "+player+" saved a home! '"+savehome.getDescription()+"'");
        } catch (Exception e) {
            plugin.getLogger().info("homes: Error saving '" + savehome.getDescription() + "' of player '" + player + "' error: ");
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
        return true;
    }

    @Override
    public boolean addHome(String player, Home home) {
        Connection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement("SELECT name FROM homes WHERE name = ?");
            st.setString(1, player);
            rs = st.executeQuery();
            if(rs.next())
            {
                st = conn.prepareStatement("UPDATE homes SET x = ?, y = ?, z = ?, yaw = ?, pitch = ?, world = ? WHERE name = ?");
            } else {
                st = conn.prepareStatement("INSERT INTO homes (x,y,z,yaw,pitch,world,name) VALUES (?,?,?,?,?,?,?)");
            }
            st.setDouble(1, home.getX());
            st.setDouble(2, home.getY());
            st.setDouble(3, home.getZ());

            st.setFloat(4, home.getYaw());
            st.setFloat(5, home.getPitch());

            st.setString(6, home.getWorld());
            st.setString(7, player);
            st.executeUpdate();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }  finally {
            try {
                if (st != null)
                    st.close();
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
        }
        return true;
    }
}
