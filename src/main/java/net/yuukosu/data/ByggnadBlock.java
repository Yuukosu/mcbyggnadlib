package net.yuukosu.data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.block.Block;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class ByggnadBlock {

    private final char blockId;
    private final byte data;

    @SuppressWarnings("deprecation")
    private ByggnadBlock(Block block) {
        this.blockId = (char) block.getTypeId();
        this.data = block.getData();
    }

    public static ByggnadBlock create(Block block) {
        return new ByggnadBlock(block);
    }

    public char pack() {
        return (char) (((this.blockId & 0xFF) << 4) | (this.data & 0xF));
    }

    public static ByggnadBlock unpack(char data) {
        int blockId = (data >> 4) & 0xFF;
        int metaData = data & 0xF;

        return new ByggnadBlock((char) blockId, (byte) metaData);
    }

    public static int getPackedSize() {
        return Character.SIZE / 8;
    }
}