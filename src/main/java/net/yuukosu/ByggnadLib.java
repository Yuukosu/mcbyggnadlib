package net.yuukosu;

import net.yuukosu.data.Byggnad;
import net.yuukosu.data.ByggnadSerializer;

import java.io.*;
import java.nio.file.Files;

public class ByggnadLib {

//    public static Byggnad load(File file) throws IOException {
//        try (FileInputStream fis = new FileInputStream(file)) {
//            return ByggnadSerializer.getInstance().deserialize(fis);
//        }
//    }
//
//    public static void save(Byggnad byggnad, File file) throws IOException {
//        if (!file.exists() || !file.isFile()) {
//            Files.createFile(file.toPath());
//        }
//
//        try (FileOutputStream fos = new FileOutputStream(file)) {
//            byte[] serialized = ByggnadSerializer.getInstance().serialize(byggnad);
//            fos.write(serialized);
//            fos.flush();
//        }
//    }
}