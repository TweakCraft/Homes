package com.guntherdw.bukkit.Homes;

import com.guntherdw.bukkit.Homes.Commands.CommandHome;
import com.guntherdw.bukkit.Homes.Commands.CommandHomes;
import com.guntherdw.bukkit.Homes.Commands.CommandSetHome;
import com.guntherdw.bukkit.Homes.Commands.iCommand;

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
