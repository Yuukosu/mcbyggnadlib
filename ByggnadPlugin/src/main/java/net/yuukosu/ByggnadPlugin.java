package net.yuukosu;

import lombok.Getter;
import net.yuukosu.data.Byggnad;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    public void addByggnad(String name, Byggnad byggnad) {
        byggnadList.put(name, byggnad);
    }

    public void loadByggnads() {
        File dir = new File(String.format("plugins/%s", this.getName()));
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        this.addByggnad(file.getName(), ByggnadLib.load(file));
                    } catch (IOException e) {
                        e.printStackTrace(System.out);
                    }
                }
            }
        }
    }

    public void saveByggnads() {
        for (Map.Entry<String, Byggnad> entry : byggnadList.entrySet()) {
            File folder = new File(String.format("plugins/%s", this.getName()));
            File file = new File(String.format("plugins/%s/%s", this.getName(), entry.getKey()));

            try {
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                if (!file.exists()) {
                    Files.createFile(file.toPath());
                }

                ByggnadLib.save(entry.getValue(), file);
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }
    }

    @Override
    public void onEnable() {
        instance = this;

        ByggnadCommand byggnadCommand = new ByggnadCommand();
        PluginCommand byggnadPluginCommand = this.getCommand("byggnad");
        byggnadPluginCommand.setExecutor(byggnadCommand);
        byggnadPluginCommand.setTabCompleter(byggnadCommand);

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        Bukkit.getLogger().info("Loading Byggnads...");
        this.loadByggnads();

        Bukkit.getLogger().info("Byggnad plugin has been enabled!");
    }
}