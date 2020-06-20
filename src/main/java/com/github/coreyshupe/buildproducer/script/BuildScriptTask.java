package com.github.coreyshupe.buildproducer.script;

import org.bukkit.plugin.java.JavaPlugin;

public interface BuildScriptTask {
    void next();

    JavaPlugin getPlugin();
}
