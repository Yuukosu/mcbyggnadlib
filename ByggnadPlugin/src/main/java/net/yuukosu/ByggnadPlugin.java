package net.yuukosu;

import lombok.Getter;
import net.yuukosu.data.Byggnad;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class ByggnadPlugin extends JavaPlugin {

    @Getter
    private static ByggnadPlugin instance;

    @Getter
    private static final Map<String, Byggnad> byggnadList;

    static {
        byggnadList = new HashMap<>();
    }

    public void addByggnadData(String name, Byggnad byggnad) {
        byggnadList.put(name, byggnad);
    }

    @Override
    public void onEnable() {
        instance = this;

        ByggnadCommand byggnadCommand = new ByggnadCommand();
        PluginCommand byggnadPluginCommand = this.getCommand("byggnad");
        byggnadPluginCommand.setExecutor(byggnadCommand);
        byggnadPluginCommand.setTabCompleter(byggnadCommand);

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        Bukkit.getLogger().info("Byggnad plugin has been enabled!");
    }
}