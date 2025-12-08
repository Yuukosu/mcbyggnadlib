package net.yuukosu;

import com.github.luben.zstd.Zstd;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Bytes;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ByggnadSerializerImpl implements ByggnadSerializer {

    @Getter
    private static final ByggnadSerializer instance;

    private static final byte[] MAGIC_HEADER;
    private static final int PALLET_BYTES;
    private static final int BLOCK_BYTES;

    static {
        instance = new ByggnadSerializerImpl();
        MAGIC_HEADER = "BYGGNAD".getBytes();
        PALLET_BYTES = (Character.SIZE + Short.SIZE) / 8;
        BLOCK_BYTES = Short.SIZE * 4 / 8;
    }

    private ByggnadSerializerImpl() {}

    private char toBytes(ByggnadBlock block) {
        return (char) ((block.getBlockId() << 4) | (block.getData() & 0xF));
    }

    private void writeRelativeLocation(ByteBuffer buffer, RelativeLocation location) {
        buffer.putShort(location.getRelativeX());
        buffer.putShort(location.getRelativeY());
        buffer.putShort(location.getRelativeZ());
    }

    @Override
    public byte[] toBytes(Byggnad byggnad) {
        Map<ByggnadBlock, Short> pallet = byggnad.getPallet();
        Multimap<Short, RelativeLocation> blocks = byggnad.getBlocks();

        // Magic Header + Pallet Size + Block Size + Width + Height
        ByteBuffer headers = ByteBuffer.allocate(MAGIC_HEADER.length + (Integer.SIZE * 2 + Short.SIZE * 2) / 8);

        // Pallet Size * (BlockData + Index)
        ByteBuffer palletBuffer = ByteBuffer.allocate(pallet.size() * PALLET_BYTES);

        // Block Size * (Index + RelativeX + RelativeY + RelativeZ)
        ByteBuffer blockBuffer = ByteBuffer.allocate(blocks.size() * BLOCK_BYTES);

        headers.put(MAGIC_HEADER);
        headers.putInt(pallet.size());
        headers.putInt(blocks.size());
        headers.putShort(byggnad.getWidth());
        headers.putShort(byggnad.getHeight());
        headers.flip();

        for (Map.Entry<ByggnadBlock, Short> entry : pallet.entrySet()) {
            palletBuffer.putChar(this.toBytes(entry.getKey()));
            palletBuffer.putShort(entry.getValue());
        }

        palletBuffer.flip();

        for (Map.Entry<Short, RelativeLocation> entry : blocks.entries()) {
            blockBuffer.putShort(entry.getKey());
            this.writeRelativeLocation(blockBuffer, entry.getValue());
        }

        blockBuffer.flip();

        byte[] compressed = Zstd.compress(Bytes.concat(palletBuffer.array(), blockBuffer.array()));

        return Bytes.concat(headers.array(), compressed);
    }

    private ByggnadBlock parseFrom(char data) {
        return new ByggnadBlock((char) (data >> 4), (byte) (data & 0xF));
    }

    @Override
    public Byggnad parseFrom(byte[] bytes) {
        Map<ByggnadBlock, Short> pallet = new HashMap<>();
        Multimap<Short, RelativeLocation> blocks = ArrayListMultimap.create();

        ByteBuffer payload = ByteBuffer.wrap(bytes);

        byte[] magic = new byte[MAGIC_HEADER.length];
        payload.get(magic);

        if (!Arrays.equals(magic, MAGIC_HEADER)) throw new ByggnadException("Invalid magic header");

        int palletSize = payload.getInt();
        int blocksSize = payload.getInt();
        short width = payload.getShort();
        short height = payload.getShort();

        byte[] body = new byte[payload.remaining()];
        payload.get(body);

        long code = Zstd.getFrameContentSize(body);

        if (0 <= code) {
            byte[] decompressed = new byte[palletSize * PALLET_BYTES + blocksSize * BLOCK_BYTES];
            code = Zstd.decompress(decompressed, body);

            if (Zstd.isError(code)) throw new ByggnadException("Failed to decompress data. Zstd Error Code: " + code);

            payload = ByteBuffer.wrap(decompressed);
        } else {
            payload = ByteBuffer.wrap(body);
        }

        for (int i = 0; i < palletSize; i++) {
            pallet.put(this.parseFrom(payload.getChar()), payload.getShort());
        }

        for (int i = 0; i < blocksSize; i++) {
            blocks.put(payload.getShort(), new RelativeLocation(payload.getShort(), payload.getShort(), payload.getShort()));
        }

        return new Byggnad(pallet, blocks, width, height);
    }
}
