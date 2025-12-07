package net.yuukosu;

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
    protected ByggnadBlock(Block block) {
        this.blockId = (char) block.getTypeId();
        this.data = block.getData();
    }
}
