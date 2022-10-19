package net.moon.game.objects.queues;

import lombok.Getter;
import net.moon.game.Practice;
import net.moon.game.objects.kits.Kit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ConcurrentLinkedDeque;

@Getter
public abstract class Queue {

    private final Practice instance;
    private final QueueType type;
    private final Kit kit;

    public ConcurrentLinkedDeque<Object> queue;

    public Queue(final QueueType type, final Kit kit) {
        this.instance = Practice.get();

        this.type = type;
        this.kit = kit;

        this.queue = new ConcurrentLinkedDeque<>();
    }

    public abstract void run();

    public boolean compareTo(ItemStack item) {
        String name = kit.getDisplayName();
        if (item.getType().equals(Material.REDSTONE)) {
            return name.equals(item.getItemMeta().getDisplayName());
        }
        return kit.getIcon().getType().equals(item.getType()) && name.equals(item.getItemMeta().getDisplayName());
    }
}
