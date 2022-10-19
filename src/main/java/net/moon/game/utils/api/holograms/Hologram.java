package net.moon.game.utils.api.holograms;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Data
public class Hologram implements Runnable {

    private Location location;
    private final List<Player> viewers;
    private int viewDistance;
    private final List<String> lines;

    public Hologram() {
        this.location = new Location(Bukkit.getWorld("world"),0,0,0);
        this.viewDistance = 16;
        this.viewers = new ArrayList<>();
        this.lines = new ArrayList<>();
    }

    public void addViewer(final Player player) {
        this.viewers.add(player);
    }
    public void removeViewer(final Player player) {
        this.viewers.remove(player);
    }
    public void clearViewer() {
        this.viewers.clear();
    }

    public void addLine(final String text) {
        this.lines.add(text);
    }
    public void setLine(final int index, final String text) {
        this.lines.set(index, text);
    }
    public void removeLine(final int index) {
        this.lines.remove(index);
    }
    public void clearLine() {
        this.lines.clear();
    }

    public void create() {

    }
    public void delete() {

    }
    public void update() {

    }
    public void move() {

    }

    @Override
    public void run() {
        this.viewers.forEach(player -> {
            if (player.getLocation().distance(this.location) < this.viewDistance) {

            }
        });
    }
}
