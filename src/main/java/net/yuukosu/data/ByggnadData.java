package net.yuukosu.data;

import com.google.common.collect.LinkedListMultimap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@EqualsAndHashCode
public class ByggnadData {

    private final Set<BlockData> blockPallet;
    private final LinkedListMultimap<RelLocationData, Integer> blockDataList;

    private int width = -1;
    private int height = -1;

    private ByggnadData() {
        this.blockPallet = new LinkedHashSet<>();
        this.blockDataList = LinkedListMultimap.create();
    }

    public ByggnadData(Set<BlockData> blockPallet, LinkedListMultimap<RelLocationData, Integer> blockDataList) {
        this.blockPallet = blockPallet;
        this.blockDataList = blockDataList;
    }

    public void byggnad(Location center) {
        this.blockDataList.entries().forEach(entry -> {
            BlockData blockData = this.blockPallet.toArray(BlockData[]::new)[entry.getValue()];
            Location location = entry.getKey().toLocation(center);
            blockData.place(location);
        });
    }

    public int getWidth() {
        return this.getWidth(false);
    }

    public int getHeight() {
        return this.getHeight(false);
    }

    @SuppressWarnings("deprecation")
    public int getWidth(boolean ignoreAir) {
        if (this.width == -1) {
            this.width = this.blockDataList.entries().parallelStream()
                    .filter(entry -> Material.AIR.getId() != this.blockPallet.toArray(BlockData[]::new)[entry.getValue()].getBlockId())
                    .mapToInt(entry -> Math.max(Math.abs(entry.getKey().getRelX()), Math.abs(entry.getKey().getRelZ())))
                    .max()
                    .orElse(-1);
        }

        return this.width;
    }

    @SuppressWarnings("deprecation")
    public int getHeight(boolean ignoreAir) {
        if (this.height == -1) {
            this.height = this.blockDataList.entries().parallelStream()
                    .filter(entry -> Material.AIR.getId() != this.blockPallet.toArray(BlockData[]::new)[entry.getValue()].getBlockId())
                    .mapToInt(entry -> entry.getKey().getRelY())
                    .max()
                    .orElse(-1);
        }

        return this.height;
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