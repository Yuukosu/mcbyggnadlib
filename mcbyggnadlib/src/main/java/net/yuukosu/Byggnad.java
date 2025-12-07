package net.yuukosu;

import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a building structure in Minecraft.
 * <p>
 * This domain class holds block palette information and relative coordinates,
 * and generates actual block placements from a specified center location.
 * </p>
 *
 * @author Yuukosu
 */
@AllArgsConstructor
public class Byggnad {

    /**
     * Block palette mapping block types to their identifier.
     */
    @Getter(AccessLevel.PROTECTED)
    private final Map<ByggnadBlock, Short> pallet;

    /**
     * Multimap of block identifiers to their relative locations.
     */
    @Getter(AccessLevel.PROTECTED)
    private final Multimap<Short, RelativeLocation> blocks;

    /**
     * Width of the building structure.
     */
    @Getter
    private short width;

    /**
     * Height of the building structure.
     */
    @Getter
    private short height;

    /**
     * Retrieves the absolute locations and block information for this building.
     *
     * @param center the center location of the building
     * @return an immutable map with absolute locations as keys and block information as values
     */
    public Map<Location, ByggnadBlock> getBlocks(Location center) {
        return this.pallet.entrySet().stream()
                .flatMap(entry -> this.blocks.get(entry.getValue()).stream()
                        .map(relativeLocation -> relativeLocation.toLocation(center))
                        .map(location -> Map.entry(location, entry.getKey())))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
