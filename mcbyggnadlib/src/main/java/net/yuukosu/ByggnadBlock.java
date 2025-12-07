package net.yuukosu;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents a block type in a building structure.
 * <p>
 * This class encapsulates block identification information including
 * the block ID and its data value (metadata).
 * </p>
 *
 * @author Yuukosu
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class ByggnadBlock {

    /**
     * The character identifier for this block type.
     */
    private final char blockId;

    /**
     * The data value (metadata) for this block.
     */
    private final byte data;
}
