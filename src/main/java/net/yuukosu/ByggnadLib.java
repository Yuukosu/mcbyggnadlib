package net.yuukosu;

import net.yuukosu.data.ByggnadData;
import net.yuukosu.data.serialization.ByggnadSerializer;

import java.io.*;
import java.nio.file.Files;

public class ByggnadLib {

    public static ByggnadData load(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return ByggnadSerializer.getInstance().deserialize(fis);
        }
    }

    public static void save(ByggnadData byggnadData, File file) throws IOException {
        if (!file.exists() || !file.isFile()) {
            Files.createFile(file.toPath());
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] serialized = ByggnadSerializer.getInstance().serialize(byggnadData);
            fos.write(serialized);
            fos.flush();
        }
    }
}