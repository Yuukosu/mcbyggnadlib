package net.yuukosu.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

import java.nio.ByteBuffer;

@Getter
@AllArgsConstructor
public class RelativeLocation {

    private final short relativeX;
    private final short relativeY;
    private final short relativeZ;

    private RelativeLocation(Location center, Location location) {
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

    public static RelativeLocation create(Location center, Location location) {
        return new RelativeLocation(center, location);
    }

    public byte[] pack() {
        ByteBuffer buf = ByteBuffer.allocate(getPackedSize());
        buf.putShort(this.relativeX);
        buf.putShort(this.relativeY);
        buf.putShort(this.relativeZ);

        return buf.array();
    }

    public static RelativeLocation unpack(byte[] packedData) {
        ByteBuffer buf = ByteBuffer.wrap(packedData);

        return new RelativeLocation(
                buf.getShort(), buf.getShort(), buf.getShort()
        );
    }

    public static int getPackedSize() {
        return (Short.SIZE * 3) / 8;
    }
}