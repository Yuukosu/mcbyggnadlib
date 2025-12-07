package net.yuukosu;

import org.bukkit.Location;

public interface ByggnadFactory {
    Byggnad create(Location center, Location pos1, Location pos2, boolean excludeAir);

    default Byggnad create(Location center, Location pos1, Location pos2) {
        return create(center, pos1, pos2);
    }
}
