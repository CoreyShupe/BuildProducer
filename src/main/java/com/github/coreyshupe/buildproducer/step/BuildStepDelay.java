package com.github.coreyshupe.buildproducer.step;

import com.github.coreyshupe.buildproducer.script.BuildScriptTask;
import com.google.common.collect.Maps;
import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Map;

@Data
@SerializableAs("BuildStepDelay")
public class BuildStepDelay implements BuildStep, Serializable, ConfigurationSerializable {
    private final static long serialVersionUID = 1L;

    private final long delay;

    @Override
    public void reproduce(BuildScriptTask task) {
        task.getPlugin().getServer().getScheduler().runTaskLater(task.getPlugin(), task::next, delay);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("delay", delay);
        return map;
    }

    public static @NotNull BuildStepDelay deserialize(Map<String, Object> args) {
        return new BuildStepDelay((Long) args.getOrDefault("delay", 20L));
    }
}
