package com.guntherdw.bukkit.Homes;

import com.guntherdw.bukkit.Homes.Commands.*;
import org.bukkit.command.SimpleCommandMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class CommandHandler {
    public Map<String, iCommand> commandMap = new HashMap<String, iCommand>();
    private Homes plugin;

    public CommandHandler(Homes instance) {
        this.plugin = instance;
        commandMap.clear();
        commandMap.put("home", new CommandHome());
        commandMap.put("homes", new CommandHomes());
        commandMap.put("sethome", new CommandSetHome());
        commandMap.put("reloadhomes", new CommandReloadHomes());
    }

    public void addCommand(String cmd, iCommand command) {
        this.addCommand(cmd, command, false);
    }

    public void addCommand(String cmd, iCommand command, boolean override) {
        if(override || !this.commandMap.containsKey(cmd)) {
            commandMap.put(cmd, command);
            /* if(this.plugin.getServer().getPluginCommand(cmd)==null) {
                this.plugin.getServer().
            } */
        }
    }

    public Homes getPlugin() {
        return plugin;
    }

    public Map<String, iCommand> getCommandMap() {
        return commandMap;
    }

    public iCommand getCommand(String command) {
        if (commandMap.containsKey(command)) {
            return commandMap.get(command);
        } else {
            return null;
        }
    }

}
