package com.guntherdw.bukkit.Homes;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.Plugin;


/**
 * @author GuntherDW
 */
public class HomesListener extends PlayerListener {

    private Homes plugin;

    public HomesListener(Homes plugin) {
        this.plugin = plugin;
    }
}