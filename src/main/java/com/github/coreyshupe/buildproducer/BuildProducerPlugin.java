package com.github.coreyshupe.buildproducer;

import com.github.coreyshupe.buildproducer.script.BuildScript;
import com.github.coreyshupe.buildproducer.step.BuildStepBlockBreak;
import com.github.coreyshupe.buildproducer.step.BuildStepBlockPlace;
import com.github.coreyshupe.buildproducer.step.BuildStepDelay;
import com.github.coreyshupe.buildproducer.util.LocationWrapper;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BuildProducerPlugin extends JavaPlugin {
    static {
        ConfigurationSerialization.registerClass(LocationWrapper.class);
        ConfigurationSerialization.registerClass(BuildStepBlockBreak.class);
        ConfigurationSerialization.registerClass(BuildStepBlockPlace.class);
        ConfigurationSerialization.registerClass(BuildStepDelay.class);
        ConfigurationSerialization.registerClass(BuildScript.class);
    }

    private final File scriptFolder;
    private final Map<UUID, BuildScript> inProgressScripts;
    private final Map<String, BuildScript> savedScripts;

    public BuildProducerPlugin() {
        this.scriptFolder = new File(getDataFolder(), "scripts");
        this.inProgressScripts = Maps.newHashMap();
        this.savedScripts = Maps.newHashMap();
    }

    @Override
    public void onEnable() {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            failEnable("Failed to create data folder.");
            return;
        }
        if (!scriptFolder.exists() && !scriptFolder.mkdirs()) {
            failEnable("Failed to create script folder.");
            return;
        }
        File[] scriptFiles = scriptFolder.listFiles();
        if (scriptFiles != null && scriptFiles.length > 0) {
            for (File scriptFile : scriptFiles) {
                if (scriptFile.getName().endsWith(".yml")) {
                    loadScript(scriptFile);
                }
            }
        }
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    private void failEnable(@NotNull String message) {
        getLogger().log(Level.SEVERE, message);
        getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console cannot use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (inProgressScripts.containsKey(player.getUniqueId())) {
            if (args.length == 0 || !args[0].equalsIgnoreCase("finish")) {
                sender.sendMessage(ChatColor.RED + "Please use \"" +
                        ChatColor.WHITE + "/buildproduce finish" +
                        ChatColor.RED + "\" when you're done with your build.");
            } else {
                getProgressScript(player).ifPresent(this::saveScript);
                inProgressScripts.remove(player.getUniqueId());
            }
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Invalid usage, please use: " + ChatColor.WHITE + "/buildproduce <script name> <create|execute>");
            return true;
        }
        String scriptName = args[0];
        String cmd = args[1];
        if (cmd.equalsIgnoreCase("create")) {
            if (savedScripts.containsKey(scriptName.toLowerCase())) {
                sender.sendMessage(ChatColor.RED + "That script already exists.");
                return true;
            }
            inProgressScripts.put(player.getUniqueId(), new BuildScript(scriptName));
            sender.sendMessage(ChatColor.GREEN + "You have started build script creation.\n" +
                    ChatColor.GREEN + "   Please break and place blocks to setup your build.\n" +
                    ChatColor.GREEN + "   When finished enter " + ChatColor.WHITE + "/buildproduce finish");
        } else if (cmd.equalsIgnoreCase("execute")) {
            if (savedScripts.containsKey(scriptName.toLowerCase())) {
                savedScripts.get(scriptName.toLowerCase()).execute(this);
                sender.sendMessage(ChatColor.GREEN + "The script has started executing.");
            } else {
                sender.sendMessage(ChatColor.RED + "That script does not exist.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid usage, please use: " + ChatColor.WHITE + "/buildproduce <script name> <create|execute>");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) return Collections.emptyList();
        String check = args[args.length - 1];
        if (args.length == 1) {
            if (inProgressScripts.containsKey(((Player) sender).getUniqueId())) {
                return Collections.singletonList("finish");
            }
            return savedScripts.keySet().stream().filter(str -> str.startsWith(check)).collect(Collectors.toList());
        } else if (args.length == 2) {
            if (inProgressScripts.containsKey(((Player) sender).getUniqueId())) {
                return Collections.emptyList();
            }
            if (check.startsWith("e")) {
                return Collections.singletonList("execute");
            } else if (check.startsWith("c")) {
                return Collections.singletonList("create");
            }
        }
        return Collections.emptyList();
    }

    public Optional<BuildScript> getProgressScript(Player player) {
        return Optional.ofNullable(inProgressScripts.getOrDefault(player.getUniqueId(), null));
    }

    public void saveScript(BuildScript script) {
        savedScripts.putIfAbsent(script.getName().toLowerCase(), script);
        BuildScriptYamlHandler handler = new BuildScriptYamlHandler();
        handler.setScript(script);
        try {
            handler.save(new File(scriptFolder, script.getName() + ".yml"));
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to save script to file: ", e);
        }
    }

    public void loadScript(File file) {
        try {
            BuildScriptYamlHandler handler = BuildScriptYamlHandler.loadFrom(file);
            BuildScript script;
            if ((script = handler.getScript()) != null) {
                savedScripts.putIfAbsent(script.getName().toLowerCase(), script);
            }
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().log(Level.SEVERE, "Failed to load script from file: " + file.getName() + ":", e);
        }
    }
}
