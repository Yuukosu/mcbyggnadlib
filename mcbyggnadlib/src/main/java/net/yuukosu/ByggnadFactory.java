package net.yuukosu;

import org.bukkit.Location;

/**
 * Factory interface for creating {@link Byggnad} building structures.
 * <p>
 * This interface provides methods to create building structures by capturing
 * blocks within a specified region between two positions.
 * </p>
 *
 * @author Yuukosu
 * @see ByggnadFactoryImpl
 */
public interface ByggnadFactory {

    /**
     * Creates a building structure from a region defined by two positions.
     *
     * @param center the center location used as the reference point for relative coordinates
     * @param pos1 the first corner of the region
     * @param pos2 the second corner of the region
     * @param excludeAir whether to exclude air blocks from the structure
     * @return a new building structure containing the blocks in the specified region
     */
    Byggnad create(Location center, Location pos1, Location pos2, boolean excludeAir);

    /**
     * Creates a building structure from a region defined by two positions, including air blocks.
     *
     * @param center the center location used as the reference point for relative coordinates
     * @param pos1 the first corner of the region
     * @param pos2 the second corner of the region
     * @return a new building structure containing all blocks in the specified region
     */
    default Byggnad create(Location center, Location pos1, Location pos2) {
        return create(center, pos1, pos2, false);
    }
}
