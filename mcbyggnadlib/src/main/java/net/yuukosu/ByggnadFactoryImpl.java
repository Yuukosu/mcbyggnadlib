package net.yuukosu;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class ByggnadFactoryImpl implements ByggnadFactory {

    @Getter
    private static final ByggnadFactory instance;

    static {
        instance = new ByggnadFactoryImpl();
    }

    @Override
    public Byggnad create(Location center, Location pos1, Location pos2, boolean excludeAir) {
        int area = Math.abs(pos2.getBlockX() - pos1.getBlockX())
                * Math.abs(pos2.getBlockY() - pos1.getBlockY())
                * Math.abs(pos2.getBlockZ() - pos1.getBlockZ());
        Map<ByggnadBlock, Short> pallet = new HashMap<>(area);
        Multimap<Short, RelativeLocation> blocks = ArrayListMultimap.create();
        short width = (short) (Math.max(Math.abs(pos1.getBlockX() - pos2.getBlockX()), Math.abs(pos1.getBlockZ() - pos2.getBlockZ())) + 1);
        short height = (short) (Math.abs(pos1.getBlockY() - pos2.getBlockY()) + 1);

        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        for (int x = Math.min(pos1.getBlockX(), pos2.getBlockX()); x <= maxX; x++) {
            for (int y = Math.min(pos1.getBlockY(), pos2.getBlockY()); y <= maxY; y++) {
                for (int z = Math.min(pos1.getBlockZ(), pos2.getBlockZ()); z <= maxZ; z++) {
                    Location location = new Location(
                            center.getWorld(),
                            x, y, z
                    );
                    Block block = location.getBlock();

                    if (excludeAir && block.isEmpty()) continue;

                    @SuppressWarnings("deprecation")
                    ByggnadBlock byggnadBlock = new ByggnadBlock((char) block.getTypeId(), block.getData());
                    Short index = pallet.putIfAbsent(byggnadBlock, (short) pallet.size());

                    if (index == null) index = (short) (pallet.size() - 1);

                    blocks.put(index, new RelativeLocation(center, location));
                }
            }
        }

        return new Byggnad(pallet, blocks, width, height);
    }
}
