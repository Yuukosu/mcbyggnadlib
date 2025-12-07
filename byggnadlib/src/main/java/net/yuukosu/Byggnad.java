package net.yuukosu;

import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Byggnad {

    @Getter(AccessLevel.PROTECTED)
    private final Map<ByggnadBlock, Short> pallet;

    @Getter(AccessLevel.PROTECTED)
    private final Multimap<Short, RelativeLocation> blocks;

    @Getter
    private short width;

    @Getter
    private short height;

    public Map<Location, ByggnadBlock> getBlocks(Location center) {
        return this.pallet.entrySet().stream()
                .flatMap(entry -> this.blocks.get(entry.getValue()).stream()
                        .map(relativeLocation -> relativeLocation.toLocation(center))
                        .map(location -> Map.entry(location, entry.getKey())))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
