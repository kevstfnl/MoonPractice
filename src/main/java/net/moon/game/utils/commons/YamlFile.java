package net.moon.game.utils.commons;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Getter
public class YamlFile {

    private final Path path;
    private final Path configFile;
    private YamlConfiguration yamlConfiguration;

    public YamlFile(final String name, final Path file, final String header) {
        this.path = file;
        this.configFile = Paths.get(file + File.separator + name + ".yml");
        try {
            if (!path.toFile().exists()) {
                Files.createDirectories(path);
            }
            if (!configFile.toFile().exists()) {
                Files.createFile(configFile);
            }
            this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.configFile.toFile());
            if (header != null && !header.isEmpty() && !header.isBlank()) {
                this.yamlConfiguration.options().header(header);
            }
            this.yamlConfiguration.options().copyDefaults(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void set(final String path, final Object object) {
        this.yamlConfiguration.set(path, object);
    }
    public void set(String path, Object object, String comment) {
        this.yamlConfiguration.addDefault(path, comment);
        this.yamlConfiguration.set(path, object);
    }
    public void save() {
        try {
            this.yamlConfiguration.save(this.configFile.toFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Set<String> getKeys(final String path) {
        if (!this.yamlConfiguration.isConfigurationSection(path)) {
            this.yamlConfiguration.createSection(path);
            return new HashSet<>();
        }
        return this.yamlConfiguration.getConfigurationSection(path).getKeys(false);
    }
    public boolean getBoolean(final String path, final boolean def) {
        this.yamlConfiguration.addDefault(path, def);
        return this.yamlConfiguration.getBoolean(path, this.yamlConfiguration.getBoolean(path));
    }
    public double getDouble(final String path, final double def) {
        this.yamlConfiguration.addDefault(path, def);
        return this.yamlConfiguration.getDouble(path, this.yamlConfiguration.getDouble(path));
    }
    public float getFloat(final String path, final float def) {
        return (float) this.getDouble(path, def);
    }
    public int getInt(final String path, final int def) {
        this.yamlConfiguration.addDefault(path, def);
        return yamlConfiguration.getInt(path, this.yamlConfiguration.getInt(path));
    }
    public <T> List<?> getList(final String path, final T def) {
        this.yamlConfiguration.addDefault(path, def);
        return this.yamlConfiguration.getList(path, this.yamlConfiguration.getList(path));
    }
    public String getString(final String path, final String def) {
        this.yamlConfiguration.addDefault(path, def);
        return this.yamlConfiguration.getString(path, this.yamlConfiguration.getString(path));
    }
}

