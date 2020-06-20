package com.github.coreyshupe.buildproducer;

import com.github.coreyshupe.buildproducer.script.BuildScript;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;

public class BuildScriptYamlHandler extends YamlConfiguration {
    @NotNull private final DumperOptions yamlOptions;
    @NotNull private final Representer yamlRepresenter;
    @NotNull private final Yaml yaml;
    @Nullable private BuildScript script;

    public BuildScriptYamlHandler() {
        this.yamlOptions = new DumperOptions();
        this.yamlRepresenter = new YamlRepresenter();
        this.yaml = new Yaml(new YamlConstructor(), this.yamlRepresenter, this.yamlOptions);
    }

    public @Nullable BuildScript getScript() {
        return script;
    }

    public void setScript(@NotNull BuildScript script) {
        this.script = script;
    }

    @Override
    public @NotNull String saveToString() {
        yamlOptions.setIndent(options().indent());
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        String dump = yaml.dump(getScript());

        if (dump.equals("{}\n")) {
            return "";
        } else {
            return dump;
        }
    }

    @Override
    public void loadFromString(@NotNull String contents) throws InvalidConfigurationException {
        Validate.notNull(contents, "Contents cannot be null");
        try {
            setScript(yaml.load(contents));
        } catch (YAMLException e) {
            throw new InvalidConfigurationException(e);
        }
    }

    public static @NotNull BuildScriptYamlHandler loadFrom(@NotNull File file) throws IOException, InvalidConfigurationException {
        Validate.notNull(file, "File cannot be null");
        BuildScriptYamlHandler config = new BuildScriptYamlHandler();
        config.load(file);
        return config;
    }

    public static @NotNull BuildScriptYamlHandler loadFrom(@NotNull Reader reader) throws IOException, InvalidConfigurationException {
        Validate.notNull(reader, "Stream cannot be null");
        BuildScriptYamlHandler config = new BuildScriptYamlHandler();
        config.load(reader);
        return config;
    }
}