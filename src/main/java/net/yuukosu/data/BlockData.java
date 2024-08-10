package net.yuukosu.data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class BlockData {

    private final int blockId;
    private final byte data;

    @SuppressWarnings("deprecation")
    private BlockData(Block block) {
        this.blockId = block.getTypeId();
        this.data = block.getData();
    }

    @SuppressWarnings("deprecation")
    public void place(Location location) {
        Block block = location.getBlock();
        block.setTypeIdAndData(this.blockId, this.data, true);
    }

    public static BlockData create(Block block) {
        return new BlockData(block);
    }
}