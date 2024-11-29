package net.yuukosu.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Byggnad {

    private final Map<ByggnadBlock, Integer> pallet;
    private final Multimap<Integer, RelativeLocation> blocks;

    private Byggnad(Location center, Location pos1, Location pos2, boolean skipAir) {
        int area = Math.abs(pos2.getBlockX() - pos1.getBlockX())
                * Math.abs(pos2.getBlockY() - pos1.getBlockY())
                * Math.abs(pos2.getBlockZ() - pos1.getBlockZ());
        this.pallet = new HashMap<>(area);
        this.blocks = ArrayListMultimap.create();
        this.create(center, pos1, pos2, skipAir);
    }

    public static Byggnad createInstance(Location center, Location pos1, Location pos2, boolean skipAir) {
        return new Byggnad(center, pos1, pos2, skipAir);
    }

    private void create(Location center, Location pos1, Location pos2, boolean skipAir) {
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

                    if (skipAir && block.isEmpty()) {
                        continue;
                    }

                    ByggnadBlock byggnadBlock = ByggnadBlock.create(block);
                    Integer index = this.pallet.get(byggnadBlock);

                    if (index == null) {
                        this.pallet.put(byggnadBlock, this.pallet.size());
                        index = this.pallet.size() - 1;
                    }

                    RelativeLocation relativeLocation = RelativeLocation.create(
                            center,
                            location
                    );
                    this.blocks.put(index, relativeLocation);
                }
            }
        }
    }

    public void byggnad(final Location center) {
        List<net.minecraft.server.v1_8_R3.Chunk> chunks = new ArrayList<>();

        this.pallet.forEach((key, value) -> {
            List<Location> locations = this.blocks.get(value).stream()
                    .map(relativeLocation -> relativeLocation.toLocation(center))
                    .toList();
            locations.forEach(location -> {
                Chunk chunk = location.getChunk();
                if (!chunk.isLoaded()) chunk.load(true);
                net.minecraft.server.v1_8_R3.Chunk nmsChunk = ((CraftChunk) chunk).getHandle();
                ChunkSection section = nmsChunk.getSections()[location.getBlockY() >> 4];

                if (section == null) {
                    section = nmsChunk.getSections()[location.getBlockY() >> 4] = new ChunkSection(location.getBlockY() >> 4, true, new char[4096]);
                }

                char[] blocks = section.getIdArray();
                blocks[(location.getBlockX() & 0xF) + ((location.getBlockY() & 0xF) << 4) + ((location.getBlockZ() & 0xF) << 8)] = key.pack();
                section.a(blocks);

                chunks.add(nmsChunk);
            });
        });

        chunks.forEach(chunk -> Bukkit.getOnlinePlayers().forEach(player -> ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, true, '\uFFFF'))));
    }
}