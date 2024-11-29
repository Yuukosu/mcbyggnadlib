package net.yuukosu;

import net.yuukosu.data.Byggnad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@SuppressWarnings("unused")
public class ByggnadLib {

    public static Byggnad load(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return Byggnad.unpack(fis.readAllBytes());
        }
    }

    public static Byggnad load(byte[] data) {
        return Byggnad.unpack(data);
    }

    public static void save(Byggnad byggnad, File output) throws IOException {
        if (!output.exists() || !output.isFile()) {
            Files.createFile(output.toPath());
        }

        try (FileOutputStream fos = new FileOutputStream(output)) {
            fos.write(byggnad.pack());
            fos.flush();
        }
    }
}