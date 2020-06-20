package com.github.coreyshupe.buildproducer.script;

import com.github.coreyshupe.buildproducer.step.BuildStep;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;

public class BuildScriptTaskExecutor implements BuildScriptTask {
    private final LinkedList<BuildStep> steps;
    private final JavaPlugin plugin;
    private int index;

    public BuildScriptTaskExecutor(LinkedList<BuildStep> steps, JavaPlugin plugin) {
        this.steps = steps;
        this.plugin = plugin;
        this.index = -1;
    }

    @Override
    public void next() {
        index++;
        if (index >= steps.size()) return;
        steps.get(index).reproduce(this);
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }
}
