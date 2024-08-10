package net.yuukosu.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

@Getter
@AllArgsConstructor
public class RelLocationData {

    private final int relX;
    private final int relY;
    private final int relZ;

    private RelLocationData(Location center, Location location) {
        this.relX = center.getBlockX() - location.getBlockX();
        this.relY = center.getBlockY() - location.getBlockY();
        this.relZ = center.getBlockZ() - location.getBlockZ();
    }

    public Location toLocation(Location center) {
        return new Location(center.getWorld(), center.getBlockX() - this.relX, center.getBlockY() - this.relY, center.getBlockZ() - this.relZ);
    }

    public static RelLocationData create(Location center, Location location) {
        return new RelLocationData(center, location);
    }
}