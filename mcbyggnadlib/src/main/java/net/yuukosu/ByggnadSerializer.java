package net.yuukosu;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Serializer interface for converting {@link Byggnad} objects to and from byte arrays.
 * <p>
 * This interface provides methods for serializing building structures into a compact
 * binary format and deserializing them back. The serialization format uses Zstd compression
 * to reduce the data size.
 * </p>
 *
 * @author Yuukosu
 * @see ByggnadSerializerImpl
 */
public interface ByggnadSerializer {

    /**
     * Serializes a {@link Byggnad} object into a byte array.
     *
     * @param byggnad the building structure to serialize
     * @return the serialized byte array
     */
    byte[] toBytes(Byggnad byggnad);

    /**
     * Deserializes a byte array into a {@link Byggnad} object.
     *
     * @param bytes the byte array to deserialize
     * @return the deserialized building structure, or null if the data is invalid
     * @throws ByggnadException if decompression fails
     */
    Byggnad parseFrom(byte[] bytes);

    /**
     * Deserializes a {@link Byggnad} object from an {@link InputStream}.
     *
     * @param input the input stream to read from
     * @return the deserialized building structure
     * @throws IOException if an I/O error occurs
     */
    default Byggnad parseFrom(InputStream input) throws IOException {
        return parseFrom(input.readAllBytes());
    }

    /**
     * Serializes a {@link Byggnad} object and writes it to an {@link OutputStream}.
     *
     * @param byggnad the building structure to serialize
     * @param out the output stream to write to
     * @throws IOException if an I/O error occurs
     */
    default void write(Byggnad byggnad, OutputStream out) throws IOException {
        out.write(toBytes(byggnad));
        out.flush();
    }
}
