package net.yuukosu;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ByggnadSerializer {
    byte[] toBytes(Byggnad byggnad);
    Byggnad parseFrom(byte[] bytes);

    default Byggnad parseFrom(InputStream input) throws IOException {
        return parseFrom(input.readAllBytes());
    }

    default void write(Byggnad byggnad, OutputStream out) throws IOException {
        out.write(toBytes(byggnad));
        out.flush();
    }
}
