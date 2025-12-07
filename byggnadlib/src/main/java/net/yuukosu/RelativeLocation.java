package net.yuukosu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

@Getter
@AllArgsConstructor
public class RelativeLocation {

    private final short relativeX;
    private final short relativeY;
    private final short relativeZ;

    protected RelativeLocation(Location center, Location location) {
        this.relativeX = (short) (center.getBlockX() - location.getBlockX());
        this.relativeY = (short) (center.getBlockY() - location.getBlockY());
        this.relativeZ = (short) (center.getBlockZ() - location.getBlockZ());
    }

    public Location toLocation(Location center) {
        return new Location(
                center.getWorld(),
                center.getBlockX() - this.relativeX,
                center.getBlockY() - this.relativeY,
                center.getBlockZ() - this.relativeZ
        );
    }
}
