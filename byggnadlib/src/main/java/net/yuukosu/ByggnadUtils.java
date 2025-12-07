package net.yuukosu;

import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;

import java.util.ArrayList;
import java.util.List;

public class ByggnadUtils {

    public static List<Chunk> generate(Byggnad byggnad, Location center) {
        return generate(byggnad, center, true);
    }

    public static List<Chunk> generate(Byggnad byggnad, Location center, boolean updateChunks) {
        List<ChunkSection> changedSections = new ArrayList<>();
        List<net.minecraft.server.v1_8_R3.Chunk> changedChunks = new ArrayList<>();

        byggnad.getBlocks(center)
                .forEach((key, value) -> {
                    Chunk chunk = key.getChunk();

                    if (!chunk.isLoaded()) chunk.load(true);

                    net.minecraft.server.v1_8_R3.Chunk nmsChunk = ((CraftChunk) chunk).getHandle();
                    int y = key.getBlockY();
                    ChunkSection section = nmsChunk.getSections()[y >> 4];

                    if (null == section) section = nmsChunk.getSections()[y >> 4] = new ChunkSection(y >> 4 << 4, true);

                    section.getIdArray()[(key.getBlockX() & 0xF) + ((key.getBlockZ() & 0xF) << 4) + ((key.getBlockY() & 0xF) << 8)]
                            = (char) (value.getBlockId() << 4 | value.getData() & 0xF);

                    changedSections.add(section);
                    changedChunks.add(nmsChunk);
                });

        changedSections.forEach(ChunkSection::recalcBlockCounts);
        changedChunks.forEach(net.minecraft.server.v1_8_R3.Chunk::initLighting);

        if (updateChunks) {
            for (net.minecraft.server.v1_8_R3.Chunk chunk : changedChunks) {
                PacketPlayOutMapChunk packet = new PacketPlayOutMapChunk(chunk, true, '\uFFFF');

                chunk.getWorld().players.forEach(player -> {
                    if (player instanceof EntityPlayer entityPlayer) entityPlayer.playerConnection.sendPacket(packet);
                });
            }
        }

        return changedChunks.stream()
                .map(chunk -> chunk.bukkitChunk)
                .toList();
    }
}
