package com.github.coreyshupe.buildproducer.script;

import com.github.coreyshupe.buildproducer.step.BuildStep;
import com.github.coreyshupe.buildproducer.step.BuildStepDelay;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SerializableAs("BuildScript")
public class BuildScript implements Serializable, ConfigurationSerializable {
    private final static long serialVersionUID = 1L;

    @Getter private final String name;
    private final LinkedList<BuildStep> buildSteps;

    public BuildScript(String name) {
        this.name = name;
        this.buildSteps = new LinkedList<>();
    }

    public void execute(JavaPlugin plugin) {
        new BuildScriptTaskExecutor(buildSteps, plugin).next();
    }

    public void addBuildStep(BuildStep buildStep) {
        this.addBuildStep(buildStep, 20);
    }

    public void addBuildStep(BuildStep buildStep, long delayTicks) {
        this.buildSteps.addLast(buildStep);
        if (delayTicks > 0) {
            this.buildSteps.addLast(new BuildStepDelay(delayTicks));
        }
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", name);
        map.put("steps", buildSteps);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static @NotNull BuildScript deserialize(Map<String, Object> args) {
        BuildScript script = new BuildScript((String) args.getOrDefault("name", "nil"));
        // this isn't a big deal because of type erasure, it will be a List<Object> being written though
        ((List<BuildStep>) args.get("steps")).forEach(step -> script.addBuildStep(step, -1));
        return script;
    }
}
