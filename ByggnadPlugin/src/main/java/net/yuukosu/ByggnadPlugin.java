package net.yuukosu;

import lombok.Getter;
import net.yuukosu.data.ByggnadData;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ByggnadPlugin extends JavaPlugin {

    @Getter
    private static ByggnadPlugin instance;

    @Getter
    private static final Map<String, ByggnadData> byggnadList;

    private final File byggnadDirectory = new File("plugins/" + this.getName());

    static {
        byggnadList = new HashMap<>();
    }

    public boolean save(String name) {
        if (!byggnadList.containsKey(name)) return false;

        Path byggnadPath = Paths.get(this.byggnadDirectory.getPath(), name);
        File byggnadFile = byggnadPath.toFile();

        try {
            ByggnadLib.save(byggnadList.get(name), byggnadFile);
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to save byggnad data.", e);
        }

        return false;
    }

    public void addByggnadData(String name, ByggnadData byggnadData) {
        byggnadList.put(name, byggnadData);
    }

    @Override
    public void onEnable() {
        instance = this;

        if (!this.byggnadDirectory.exists() || !this.byggnadDirectory.isDirectory()) {
            try {
                Files.createDirectories(this.byggnadDirectory.toPath());
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to create byggnad directory.", e);
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }

        File[] files = this.byggnadDirectory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (!file.isFile()) {
                    continue;
                }

                try {
                    byggnadList.put(file.getName(), ByggnadLib.load(file));
                } catch (IOException e) {
                    Bukkit.getLogger().log(Level.SEVERE, "Failed to load byggnad data.", e);
                }
            }
        }

        ByggnadCommand byggnadCommand = new ByggnadCommand();
        PluginCommand byggnadPluginCommand = this.getCommand("byggnad");
        byggnadPluginCommand.setExecutor(byggnadCommand);
        byggnadPluginCommand.setTabCompleter(byggnadCommand);

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        Bukkit.getLogger().info("Byggnad plugin has been enabled!");
    }
}