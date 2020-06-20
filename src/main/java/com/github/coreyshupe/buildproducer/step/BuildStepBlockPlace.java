package com.github.coreyshupe.buildproducer.step;

import com.github.coreyshupe.buildproducer.script.BuildScriptTask;
import com.github.coreyshupe.buildproducer.util.LocationWrapper;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@SerializableAs("BuildStepBlockPlace")
public class BuildStepBlockPlace implements BuildStep, Serializable, ConfigurationSerializable {
    private final static long serialVersionUID = 1L;

    // all effectively final
    private LocationWrapper locationWrapper;
    private Material type;
    private BlockData blockData;

    public BuildStepBlockPlace(Block block) {
        this.locationWrapper = new LocationWrapper(block.getLocation().clone());
        this.type = block.getType();
        this.blockData = block.getBlockData().clone();
    }

    @Override
    public void reproduce(BuildScriptTask task) {
        locationWrapper.buildLocation().ifPresent(location -> {
            Block block = location.getBlock();
            block.setType(type, false);
            block.setBlockData(blockData, true);
        });
        task.next();
    }

    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
        locationWrapper = (LocationWrapper) aInputStream.readObject();
        type = Material.matchMaterial(aInputStream.readUTF());
        blockData = Bukkit.createBlockData(aInputStream.readUTF());
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
        aOutputStream.writeObject(locationWrapper);
        aOutputStream.writeUTF(type.name());
        aOutputStream.writeUTF(blockData.getAsString());
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("location", locationWrapper);
        map.put("type", type.name());
        map.put("data", blockData.getAsString());
        return map;
    }

    public static @NotNull BuildStepBlockPlace deserialize(Map<String, Object> args) {
        return new BuildStepBlockPlace(
                (LocationWrapper) args.get("location"),
                Material.matchMaterial((String) args.get("type")),
                Bukkit.createBlockData((String) args.get("data"))
        );
    }
}
