package net.yuukosu.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

@Getter
@AllArgsConstructor
public class RelativeLocation {

    private final int relativeX;
    private final int relativeY;
    private final int relativeZ;

    private RelativeLocation(Location center, Location location) {
        this.relativeX = center.getBlockX() - location.getBlockX();
        this.relativeY = center.getBlockY() - location.getBlockY();
        this.relativeZ = center.getBlockZ() - location.getBlockZ();
    }

    public Location toLocation(Location center) {
        return new Location(center.getWorld(), center.getBlockX() - this.relativeX, center.getBlockY() - this.relativeY, center.getBlockZ() - this.relativeZ);
    }

    public static RelativeLocation create(Location center, Location location) {
        return new RelativeLocation(center, location);
    }
}