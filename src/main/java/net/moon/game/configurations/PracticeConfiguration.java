package net.moon.game.configurations;

import net.moon.game.Practice;
import net.mooncore.core.commons.YamlFile;

public class PracticeConfiguration {

    private boolean logs, debug;

    public PracticeConfiguration(final Practice instance) {
        final YamlFile yml = new YamlFile("config", instance.getDataFolder().toPath(), null);

        yml.save();
    }
}
