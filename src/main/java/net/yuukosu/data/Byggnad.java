package net.yuukosu.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Bytes;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@SuppressWarnings("unused")
public class Byggnad {

    private final Map<ByggnadBlock, Short> pallet;
    private final Multimap<Short, RelativeLocation> blocks;

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
                    Short index = this.pallet.get(byggnadBlock);

                    if (index == null) {
                        this.pallet.put(byggnadBlock, (short) this.pallet.size());
                        index = (short) (this.pallet.size() - 1);
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

    public void byggnad(final Location center, boolean updatePacket) {
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
                nmsChunk.initLighting();
            });
        });

        if (updatePacket) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                World world = player.getWorld();

                for (Chunk chunk : world.getLoadedChunks()) {
                    PacketPlayOutMapChunk packet = new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), true, '\uFFFF');
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                }
            });
        }
    }

    public byte[] pack() {
        ByteBuffer headerBuf = ByteBuffer.allocate((Short.SIZE * 2) / 8);
        headerBuf.putShort((short) this.pallet.size());
        headerBuf.putShort((short) this.blocks.size());
        headerBuf.flip();

        ByteBuffer palletBuf = ByteBuffer.allocate((ByggnadBlock.getPackedSize() + Short.SIZE / 8) * this.pallet.size());

        for (Map.Entry<ByggnadBlock, Short> entry : this.pallet.entrySet()) {
            palletBuf.putChar(entry.getKey().pack());
            palletBuf.putShort(entry.getValue());
        }
        palletBuf.flip();

        ByteBuffer blocksBuf = ByteBuffer.allocate((RelativeLocation.getPackedSize() + Short.SIZE / 8) * this.blocks.size());

        for (Map.Entry<Short, RelativeLocation> entry : this.blocks.entries()) {
            blocksBuf.putShort(entry.getKey());
            blocksBuf.put(entry.getValue().pack());
        }
        blocksBuf.flip();

        return Bytes.concat(headerBuf.array(), palletBuf.array(), blocksBuf.array());
    }

    public static Byggnad unpack(byte[] data) {
        Map<ByggnadBlock, Short> pallet = new HashMap<>();
        Multimap<Short, RelativeLocation> blocks = ArrayListMultimap.create();
        ByteBuffer payload = ByteBuffer.wrap(data);
        int palletSize = payload.getShort();
        int blocksSize = payload.getShort();

        for (int i = 0; i < palletSize; i++) {
            pallet.put(ByggnadBlock.unpack(payload.getChar()), payload.getShort());
        }

        for (int i = 0; i < blocksSize; i++) {
            short id = payload.getShort();
            byte[] relativeLocationBuffer = new byte[RelativeLocation.getPackedSize()];
            payload.get(relativeLocationBuffer);
            blocks.put(id, RelativeLocation.unpack(relativeLocationBuffer));
        }

        return new Byggnad(pallet, blocks);
    }
}