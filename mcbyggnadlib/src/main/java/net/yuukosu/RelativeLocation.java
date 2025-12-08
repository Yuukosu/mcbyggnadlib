package net.yuukosu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

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
     * Converts this relative location to an absolute location based on a center point with default rotation (NORTH).
     *
     * @param center the center location to calculate from
     * @return the absolute location in the world
     */
    public Location toLocation(Location center) {
        return this.toLocation(center, Rotation.NORTH);
    }

    /**
     * Converts this relative location to an absolute location based on a center point with the given rotation.
     *
     * @param center the center location to calculate from
     * @param rotation the rotation to apply when converting to absolute location
     * @return the absolute location in the world
     */
    public Location toLocation(Location center, Rotation rotation) {
        World world = center.getWorld();
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        return switch (rotation) {
            case EAST -> new Location(world, centerX + this.relativeZ, centerY - this.relativeY, centerZ + this.relativeX);
            case WEST -> new Location(world, centerX - this.relativeZ, centerY - this.relativeY, centerZ - this.relativeX);
            case SOUTH -> new Location(world, centerX + this.relativeX, centerY - this.relativeY, centerZ + this.relativeZ);
            case NORTH -> new Location(world, centerX - this.relativeX, centerY - this.relativeY, centerZ - this.relativeZ);
        };
    }
}
