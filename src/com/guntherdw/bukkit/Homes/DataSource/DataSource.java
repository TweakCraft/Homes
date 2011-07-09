package com.guntherdw.bukkit.Homes.DataSource;

import com.guntherdw.bukkit.Homes.Home;
import com.guntherdw.bukkit.Homes.SaveHome;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * @author GuntherDW
 */
public abstract class DataSource {

    public abstract void saveHome(Object homeObject);

    public Home getHome(Player player)
    {
        return this.getHome(player.getName());
    }

    public abstract Home getHome(String player);

    public abstract Map<String, Home> getHomesMap();

    public abstract List<Home> getHomes();

    public abstract List<SaveHome> getSaveHomes();

    public List<SaveHome> getSaveHomes(Player player) {
        return this.getSaveHomes(player.getName());
    }

    public abstract SaveHome getSaveHome(String player, int savehomeid);

    public abstract List<SaveHome> getSaveHomes(String player);

    public abstract boolean deleteHome(String player, int savehomeid);

    public abstract boolean addSavehome(String player, SaveHome savehome);
    public abstract boolean addHome(String player, Home home);

}
