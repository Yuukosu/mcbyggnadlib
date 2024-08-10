package net.yuukosu.data;

import com.google.common.collect.LinkedListMultimap;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class ByggnadData {

    private final Set<BlockData> blockPallet;
    private final LinkedListMultimap<RelLocationData, Integer> blockDataList;

    private ByggnadData() {
        this.blockPallet = new LinkedHashSet<>();
        this.blockDataList = LinkedListMultimap.create();
    }

    public void byggnad(Location center) {
        this.blockDataList.entries().forEach(entry -> {
            BlockData blockData = this.blockPallet.toArray(BlockData[]::new)[entry.getValue()];
            Location location = entry.getKey().toLocation(center);
            blockData.place(location);
        });
    }

    public static ByggnadData create(Location center, Location pos1, Location pos2, boolean skipAir) {
        ByggnadData byggnadData = new ByggnadData();

        for (int x = (int) Math.min(pos1.getX(), pos2.getX()); x <= Math.max(pos1.getX(), pos2.getX()); x++) {
            for (int y = (int) Math.min(pos1.getY(), pos2.getY()); y <= Math.max(pos1.getY(), pos2.getY()); y++) {
                for (int z = (int) Math.min(pos1.getZ(), pos2.getZ()); z <= Math.max(pos1.getZ(), pos2.getZ()); z++) {
                    Location location = new Location(center.getWorld(), x, y, z);
                    Block block = location.getBlock();

                    if (skipAir && block.getType() == Material.AIR) {
                        continue;
                    }

                    BlockData blockData = BlockData.create(block);
                    byggnadData.blockPallet.add(blockData);

                    int index = -1;

                    for (int i = 0; i < byggnadData.blockPallet.size(); i++) {
                        if (blockData.equals(byggnadData.blockPallet.toArray(BlockData[]::new)[i])) {
                            index = i;
                            break;
                        }
                    }

                    byggnadData.blockDataList.put(RelLocationData.create(center, location), index);
                }
            }
        }

        return byggnadData;
    }
}