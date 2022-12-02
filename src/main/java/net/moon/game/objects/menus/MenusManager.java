package net.moon.game.objects.menus;

import net.moon.game.objects.menus.impl.KitEditorMenu;
import net.moon.game.objects.menus.impl.RankedQueueMenu;
import net.moon.game.objects.menus.impl.UnrankedQueueMenu;
import net.moon.game.objects.players.PlayerData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MenusManager {

    private final Map<String, Menu> menus;
    private final Map<String, Menu> uniqueMenus;
    private final Map<UUID, Map<String, Menu>> playersMenus;

    public MenusManager() {
        this.menus = new ConcurrentHashMap<>();
        this.uniqueMenus = new ConcurrentHashMap<>();
        this.playersMenus = new ConcurrentHashMap<>();
        init();
    }

    public void init() {
        add("unranked", new UnrankedQueueMenu());
        add("kit-editor", new KitEditorMenu());
    }

    public void injectPlayerGui(final PlayerData playerData) {
        final UUID uuid = playerData.getUuid();
        this.playersMenus.put(uuid, this.uniqueMenus);
        for (Menu menu : this.playersMenus.get(uuid).values()) {
            menu.setPlayerData(playerData);
        }
    }
    public void uninjectPlayerGui(final UUID uuid) {
        this.playersMenus.remove(uuid);
    }

    public void add(final String name, final Menu menu) {
        if (menu.isUnique()) {
            this.uniqueMenus.put(name, menu);
        } else {
            this.menus.put(name, menu);
        }
    }

    public Menu get(final String name) {
        return this.menus.get(name);
    }

    public Menu getPlayerMenu(final PlayerData playerData, final String name) {
        return this.playersMenus.get(playerData.getUuid()).get(name);
    }

    public void stop() {
        this.menus.clear();
        this.uniqueMenus.clear();
    }
}
