package net.yuukosu;

import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for generating building structures in the Minecraft world.
 * <p>
 * This class provides methods to place {@link Byggnad} structures at specified locations
 * and optionally update the chunks for connected players.
 * </p>
 *
 * @author Yuukosu
 */
public class ByggnadUtils {

    /**
     * Generates a building structure at the specified center location with chunk updates enabled.
     *
     * @param byggnad the building structure to generate
     * @param center the center location where the building will be placed
     */
    public static void generate(Byggnad byggnad, Location center) {
        generate(byggnad, center, true);
    }

    /**
     * Generates a building structure at the specified center location.
     *
     * @param byggnad the building structure to generate
     * @param center the center location where the building will be placed
     * @param updateChunks whether to send chunk update packets to connected players
     */
    public static void generate(Byggnad byggnad, Location center, boolean updateChunks) {
        Set<ChunkSection> changedSections = new HashSet<>();
        Set<net.minecraft.server.v1_8_R3.Chunk> changedChunks = new HashSet<>();

        byggnad.getBlocks(center)
                .forEach((key, value) -> {
                    Chunk chunk = key.getChunk();

                    if (!chunk.isLoaded()) chunk.load(true);

                    net.minecraft.server.v1_8_R3.Chunk nmsChunk = ((CraftChunk) chunk).getHandle();
                    int y = key.getBlockY();
                    ChunkSection section = nmsChunk.getSections()[y >> 4];

                    if (section == null) section = nmsChunk.getSections()[y >> 4] = new ChunkSection(y >> 4 << 4, true);

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
    }
}
