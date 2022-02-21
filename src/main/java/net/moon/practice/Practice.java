package net.moon.practice;

import lombok.Getter;
import net.mooncore.api.commons.generators.VoidGenerator;
import net.mooncore.api.commons.utils.WorldUtils;
import net.mooncore.core.Core;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public class Practice extends JavaPlugin {

    //Instances
    private static Practice instance; //Main plugin instance.
    public static Practice get() {
        return instance;
    } //Getter of plugin instance.
    @Getter private Core core; //Server core instance.

    //Configuration files

    //Managers


    @Override
    public void onLoad() {
        WorldUtils.deleteWorld("arenas"); //Delete arena world if server are stopped caused by crash or kill.
        WorldCreator wc = new WorldCreator("arenas"); //Create arena world.
        wc.generator(new VoidGenerator()); //Set custom void generator.
        wc.createWorld(); //Generate arena world.
    }

    @Override
    public void onEnable() {
        instance = this; //Initialize main instance.
        this.core = Core.get(); //Initialize core instance.
    }

    @Override
    public void onDisable() {
        WorldUtils.deleteWorld("arenas"); //Delete arena world.
    }

    private void registerConfigurationFile() {

    }
    private void registerManagers() {

    }
    private void registerListeners() {

    }
    private void registerCommands() {

    }
    private void registerTasks() {

    }
}
