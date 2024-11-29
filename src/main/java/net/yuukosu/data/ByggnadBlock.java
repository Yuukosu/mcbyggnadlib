package net.yuukosu.data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;
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

    @SuppressWarnings("deprecation")
    public void place(Location location) {
        Block block = location.getBlock();
        block.setTypeIdAndData(this.blockId, this.data, true);
    }

    public static ByggnadBlock create(Block block) {
        return new ByggnadBlock(block);
    }

    public char pack() {
        return (char) (((this.blockId & 0xFF) << 4) | (this.data & 0xF));
    }

    public static ByggnadBlock unpack(char packedData) {
        int blockId = (packedData >> 4) & 0xFF;
        int data = packedData & 0xF;

        return new ByggnadBlock((char) blockId, (byte) data);
    }
}