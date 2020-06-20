package com.github.coreyshupe.buildproducer.util;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@SerializableAs("LocationWrapper")
public class LocationWrapper implements Serializable, ConfigurationSerializable {
    private final static long serialVersionUID = 1L;

    private final UUID uuid;
    private final int x;
    private final int y;
    private final int z;
    private transient World world;

    public LocationWrapper(Location location) {
        this(Objects.requireNonNull(location.getWorld()).getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public LocationWrapper(@NotNull UUID uuid, int x, int y, int z) {
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Optional<Location> buildLocation() {
        if (world == null) {
            world = Bukkit.getWorld(uuid);
        }
        return world == null ? Optional.empty() : Optional.of(new Location(world, x, y, z));
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("uid", uuid.toString());
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        return map;
    }

    public static @NotNull LocationWrapper deserialize(Map<String, Object> args) {
        return new LocationWrapper(UUID.fromString((String) args.get("uid")), (int) args.get("x"), (int) args.get("y"), (int) args.get("z"));
    }
}
