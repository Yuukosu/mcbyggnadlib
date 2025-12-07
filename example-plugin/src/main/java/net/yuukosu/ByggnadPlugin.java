package net.yuukosu;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    public void register(String name, Byggnad byggnad) {
        byggnadList.put(name, byggnad);
    }

    public void load() {
        File dir = new File(String.format("plugins/%s", this.getName()));
        File[] files = dir.listFiles();

        ByggnadSerializer serializer = ByggnadSerializerImpl.getInstance();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try (FileInputStream fs = new FileInputStream(file)) {
                        this.register(file.getName(), serializer.parseFrom(fs));
                    } catch (IOException e) {
                        e.printStackTrace(System.out);
                    }
                }
            }
        }
    }

    public void save() {
        for (Map.Entry<String, Byggnad> entry : byggnadList.entrySet()) {
            File folder = new File(String.format("plugins/%s", this.getName()));
            File file = new File(String.format("plugins/%s/%s", this.getName(), entry.getKey()));

            ByggnadSerializer serializer = ByggnadSerializerImpl.getInstance();

            try {
                if (!folder.exists()) {
                    Files.createDirectory(folder.toPath());
                }

                if (!file.exists()) {
                    Files.createFile(file.toPath());
                }

                try (FileOutputStream fo = new FileOutputStream(file)) {
                    serializer.write(entry.getValue(), fo);
                }
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
        this.load();

        Bukkit.getLogger().info("Byggnad plugin has been enabled!");
    }
}
