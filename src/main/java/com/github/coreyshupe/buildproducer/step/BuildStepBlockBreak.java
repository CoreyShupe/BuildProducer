package com.github.coreyshupe.buildproducer.step;

import com.github.coreyshupe.buildproducer.script.BuildScriptTask;
import com.github.coreyshupe.buildproducer.util.LocationWrapper;
import com.google.common.collect.Maps;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Map;

@Data
@SerializableAs("BuildStepBlockBreak")
public class BuildStepBlockBreak implements BuildStep, Serializable, ConfigurationSerializable {
    private final static long serialVersionUID = 1L;

    private final LocationWrapper locationWrapper;

    @Override
    public void reproduce(BuildScriptTask task) {
        locationWrapper.buildLocation().ifPresent(location -> location.getBlock().setType(Material.AIR, true));
        task.next();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("location", locationWrapper);
        return map;
    }
    
    public static @NotNull BuildStepBlockBreak deserialize(Map<String, Object> args) {
        return new BuildStepBlockBreak((LocationWrapper) args.get("location"));
    }
}
