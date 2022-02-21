package net.moon.game.configurations;

import net.moon.game.Practice;
import net.mooncore.core.commons.YamlFile;

public class DatabaseConfiguration {

    public DatabaseConfiguration(final Practice instance) {
        final YamlFile yml = new YamlFile("databases", instance.getDataFolder().toPath(), null);

        yml.save();
    }
}
