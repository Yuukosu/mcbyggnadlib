package net.yuukosu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

/**
 * Represents a relative position in 3D space.
 * <p>
 * This class stores coordinates relative to a center point and provides
 * methods to convert between relative and absolute locations.
 * </p>
 *
 * @author Yuukosu
 */
@Getter
@AllArgsConstructor
public class RelativeLocation {

    /**
     * The relative X coordinate.
     */
    private final short relativeX;

    /**
     * The relative Y coordinate.
     */
    private final short relativeY;

    /**
     * The relative Z coordinate.
     */
    private final short relativeZ;

    /**
     * Constructs a RelativeLocation by calculating the relative offset between two locations.
     *
     * @param center the center (reference) location
     * @param location the target location
     */
    protected RelativeLocation(Location center, Location location) {
        this.relativeX = (short) (center.getBlockX() - location.getBlockX());
        this.relativeY = (short) (center.getBlockY() - location.getBlockY());
        this.relativeZ = (short) (center.getBlockZ() - location.getBlockZ());
    }

    /**
     * Converts this relative location to an absolute location based on a center point.
     *
     * @param center the center location to calculate from
     * @return the absolute location in the world
     */
    public Location toLocation(Location center) {
        return new Location(
                center.getWorld(),
                center.getBlockX() - this.relativeX,
                center.getBlockY() - this.relativeY,
                center.getBlockZ() - this.relativeZ
        );
    }
}
