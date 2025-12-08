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
     * Retrieves the absolute locations and block information for this building with default rotation (NORTH).
     *
     * @param center the center location of the building
     * @return an immutable map with absolute locations as keys and block information as values
     */
    public Map<Location, ByggnadBlock> getBlocks(Location center) {
        return this.getBlocks(center, Rotation.NORTH);
    }

    /**
     * Retrieves the absolute locations and block information for this building with the given rotation.
     *
     * @param center the center location of the building
     * @param rotation the rotation to apply to the building structure
     * @return an immutable map with absolute locations as keys and block information as values
     */
    public Map<Location, ByggnadBlock> getBlocks(Location center, Rotation rotation) {
        return this.pallet.entrySet().stream()
                .flatMap(entry -> this.blocks.get(entry.getValue()).stream()
                        .map(relativeLocation -> relativeLocation.toLocation(center, rotation))
                        .map(location -> Map.entry(location, entry.getKey())))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
