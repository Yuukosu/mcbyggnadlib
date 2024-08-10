package net.yuukosu.data.serialization;

import com.google.common.collect.*;
import com.google.common.primitives.Bytes;
import lombok.Getter;
import net.yuukosu.data.BlockData;
import net.yuukosu.data.ByggnadData;
import net.yuukosu.data.RelLocationData;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ByggnadSerializer implements Serializable, ByggnadSerializable {

    private static final Logger LOGGER;

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private static final ByggnadSerializer instance;

    private static final int BYTE_SIZE = 1;
    private static final int INT_SIZE = Integer.SIZE / Byte.SIZE;

    private static final int HEADER_SIZE = BYTE_SIZE + INT_SIZE + INT_SIZE;
    private static final int PALLET_SECTION_SIZE = INT_SIZE + BYTE_SIZE;
    private static final int PAYLOAD_SECTION_SIZE = BYTE_SIZE + INT_SIZE + INT_SIZE + INT_SIZE;

    static {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);

        LOGGER = Logger.getLogger(ByggnadSerializer.class.getName());
        LOGGER.setLevel(Level.ALL);
        LOGGER.addHandler(consoleHandler);
        LOGGER.setUseParentHandlers(false);

        instance = new ByggnadSerializer();
    }

    @Override
    public byte[] serialize(ByggnadData byggnadData) {
        Set<BlockData> blockPallet = byggnadData.getBlockPallet();
        LinkedListMultimap<RelLocationData, Integer> blockDataList = byggnadData.getBlockDataList();

        ByteBuffer pallet = ByteBuffer.allocate(PALLET_SECTION_SIZE * blockPallet.size());
        ByteBuffer payload = ByteBuffer.allocate(PAYLOAD_SECTION_SIZE * blockDataList.size());

        int palletOffset = 0;
        for (BlockData blockData : blockPallet) {
            pallet.putInt(palletOffset, blockData.getBlockId());
            pallet.put(palletOffset + INT_SIZE, blockData.getData());
            palletOffset += PALLET_SECTION_SIZE;
        }

        for (int i = 0; i < blockDataList.size(); i++) {
            Map.Entry<RelLocationData, Integer> entry = blockDataList.entries().get(i);
            RelLocationData locDat = entry.getKey();

            int offset = PAYLOAD_SECTION_SIZE * i;
            payload.put(offset, entry.getValue().byteValue());
            payload.putInt(offset + BYTE_SIZE, locDat.getRelX());
            payload.putInt(offset + BYTE_SIZE + INT_SIZE, locDat.getRelY());
            payload.putInt(offset + BYTE_SIZE + INT_SIZE + INT_SIZE, locDat.getRelZ());
        }

        ByteBuffer header = ByteBuffer.allocate(HEADER_SIZE);
        header.put(0, (byte) serialVersionUID);
        header.putInt(BYTE_SIZE, pallet.capacity() / PALLET_SECTION_SIZE);
        header.putInt(BYTE_SIZE + INT_SIZE, payload.capacity() / PAYLOAD_SECTION_SIZE);

        return Bytes.concat(header.array(), pallet.array(), payload.array());
    }

    private byte[] readHeader(InputStream is) throws IOException {
        byte[] header = new byte[HEADER_SIZE];

        if (is.read(header) == HEADER_SIZE) {
            return header;
        }

        throw new IOException("Failed to read header.");
    }

    private byte[] readPallet(InputStream is, int palletSize) throws IOException {
        int size = PALLET_SECTION_SIZE * palletSize;
        byte[] pallet = new byte[size];

        if (is.read(pallet) == size) {
            return pallet;
        }

        throw new IOException("Failed to read pallet.");
    }

    private byte[] readPayload(InputStream is, int payloadSize) throws IOException {
        int size = PAYLOAD_SECTION_SIZE * payloadSize;
        byte[] payload = new byte[size];

        if (is.read(payload) == size) {
            return payload;
        }

        throw new IOException("Failed to read payload.");
    }

    @Override
    public ByggnadData deserialize(InputStream is) {
        ByggnadData byggnadData = null;

        try {
            ByteBuffer headerBuffer = ByteBuffer.wrap(this.readHeader(is));
            byte version = headerBuffer.get(0);
            int palletLength = headerBuffer.getInt(BYTE_SIZE);
            int payloadLength = headerBuffer.getInt(BYTE_SIZE + INT_SIZE);

            if (serialVersionUID != (long) version) {
                throw new UnsupportedClassVersionError("Serial version do not match.");
            }

            ByteBuffer pallet = ByteBuffer.wrap(this.readPallet(is, palletLength));
            ByteBuffer payload = ByteBuffer.wrap(this.readPayload(is, payloadLength));

            Set<BlockData> blockPallet = new LinkedHashSet<>();
            LinkedListMultimap<RelLocationData, Integer> blockDataList = LinkedListMultimap.create();

            for (int i = 0; i < PALLET_SECTION_SIZE * palletLength; i += PALLET_SECTION_SIZE) {
                BlockData blockData = new BlockData(pallet.getInt(i), pallet.get(i + INT_SIZE));
                blockPallet.add(blockData);
            }

            for (int i = 0; i < PAYLOAD_SECTION_SIZE * payloadLength; i += PAYLOAD_SECTION_SIZE) {
                int index = payload.get(i);
                int relX = payload.getInt(i + BYTE_SIZE);
                int relY = payload.getInt(i + BYTE_SIZE + INT_SIZE);
                int relZ = payload.getInt(i + BYTE_SIZE + INT_SIZE + INT_SIZE);
                RelLocationData relLocationData = new RelLocationData(relX, relY, relZ);
                blockDataList.put(relLocationData, index);
            }

            byggnadData = new ByggnadData(blockPallet, blockDataList);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read data.", e);
        }

        return byggnadData;
    }
}