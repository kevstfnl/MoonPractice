package net.moon.game;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import net.moon.game.commands.admins.ArenaCommand;
import net.moon.game.commands.admins.KitCommand;
import net.moon.game.commands.admins.LobbyCommand;
import net.moon.game.commands.admins.PracticeCommand;
import net.moon.game.configurations.DatabaseConfiguration;
import net.moon.game.listeners.ConnexionListener;
import net.moon.game.listeners.InventoryListener;
import net.moon.game.objects.arenas.ArenasManager;
import net.moon.game.objects.databases.mongodb.MongoManager;
import net.moon.game.objects.databases.redis.RedisManager;
import net.moon.game.objects.kits.KitsManager;
import net.moon.game.objects.leaderboards.LeaderboardsManager;
import net.moon.game.objects.menus.MenusManager;
import net.moon.game.objects.parties.PartyManager;
import net.moon.game.objects.players.PlayersManager;
import net.moon.game.objects.practice.PracticeManager;
import net.moon.game.objects.queues.QueueManager;
import net.moon.game.tasks.PracticeTask;
import net.moon.game.utils.commons.TaskUtils;
import net.moon.game.utils.world.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.moon.game.constants.PracticeLogger.log;

public class Practice extends JavaPlugin {

    //Instance
    private static Practice instance; //Main plugin instance.
    public static Practice get() {
        return instance;
    } //Getter of plugin instance.
    public static boolean isStarted = false; //To get if plugin is running.
    public static boolean isUseMoon = false; //Get if MoonSpigot is used to run plugin.

    //Data executor
    private ExecutorService threadData; //Thread to players data, for more performances.

    //Configuration files
    @Getter private DatabaseConfiguration databaseConfiguration; //Database configuration file.

    //Managers
    @Getter private RedisManager redisManager; //Redis manager, used to save temporary data into database.
    @Getter private MongoManager mongoManager; //MongoDB manager, used to save data into database.
    @Getter private PlayersManager playersManager; //Players manager, used to control all player's data.
    @Getter private KitsManager kitsManager; //Kits manager, used to control all kit's data.
    @Getter private ArenasManager arenasManager; //Arenas manager, used to control all arena's data.
    @Getter private PartyManager partyManager; //Party manager, used to control all parties.
    @Getter private QueueManager queueManager; //Queues manager, used to control all queues
    @Getter private LeaderboardsManager leaderboardsManager; //Leaderboard manager, used to generate le board of bests players.
    @Getter private MenusManager menusManager; //Menus manager, used to create inventory gui.
    @Getter private PracticeManager practiceManager; //Other contents of practice manager.

    @Override
    public void onEnable() {
        instance = this; //Initialize main instance.
        this.threadData = Executors.newSingleThreadExecutor(
                new ThreadFactoryBuilder().setPriority(3).setNameFormat("Data Executor").build()); //Initialise thread

        log("Init plugin contents...");
        registerConfigurationFile(); //Register configuration file.
        registerManagers(); //Register managers.
        registerListeners(); //Register listeners.
        registerCommands(); //Register commands.
        registerTasks(); //Register taks.



        try {
            log("Enabling private moon contents...");
            Class.forName("net.moon.spigot.Moon");
            isUseMoon = true;
        } catch (ClassNotFoundException ignored) {
            isUseMoon = false;
        } //Check if MoonSpigot is used.
        isStarted = true;
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("§cServer stopped.");
        }
        WorldUtils.deleteWorld("arenas"); //Delete arena world.
        this.menusManager.stop(); //Stop menus manager.
        this.leaderboardsManager.stop(); //Stop leaderboard manager.
        this.queueManager.stop(); //Stop queues manager.
        this.partyManager.stop(); //Stop party manager.
        this.arenasManager.stop(); //Stop arena manager
        this.kitsManager.stop(); //Stop kits manager.
        this.playersManager.stop(); //Stop player manager.
        this.practiceManager.stop(); //Stop practice manager.
        this.redisManager.stop(); //Stop Redis.
        this.mongoManager.stop(); //Stop MongoDB.

    }

    private void registerConfigurationFile() {
        log("Register configuration files...");
        this.databaseConfiguration = new DatabaseConfiguration(this.getDataFolder().toPath()); //Register database access configuration.
    }

    private void registerManagers() {
        log("Register managers...");
        this.redisManager = new RedisManager(this); //Init Redis.
        this.mongoManager = new MongoManager(this); //Init MongoDB.
        this.arenasManager = new ArenasManager(this); //Init arenas manager
        this.kitsManager = new KitsManager(this); //Init kits manager.
        this.playersManager = new PlayersManager(this); //Init players manager.
        this.partyManager = new PartyManager(this); //Init party manager.
        this.queueManager = new QueueManager(this); //Int queue manager.
        this.leaderboardsManager = new LeaderboardsManager(this); //Init leaderboards manager.
        this.menusManager = new MenusManager(); //Init menus manager.
        this.practiceManager = new PracticeManager(this); //Init practice manager.
    }

    private void registerListeners() {
        log("Register listeners...");
        final PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new ConnexionListener(this), this); //Register join and quit listeners.
        pm.registerEvents(new InventoryListener(this), this); //Register inventory listeners.
    }

    private void registerCommands() {
        log("Register commands...");
        this.getCommand("practice").setExecutor(new PracticeCommand()); //Register practice command.
        this.getCommand("lobby").setExecutor(new LobbyCommand(this)); //Register lobby command.
        this.getCommand("kit").setExecutor(new KitCommand(this)); //Register kit command.
        this.getCommand("arena").setExecutor(new ArenaCommand()); //Register arena command.
    }

    private void registerTasks() {
        log("Register tasks...");
        TaskUtils.runAsync(new PracticeTask(this)); //Init main practice task
    }

    public void execute(final Runnable runnable) {
        this.threadData.execute(runnable);
    }
}
