package net.moon.game.objects.players;

import lombok.Getter;
import net.moon.game.objects.queues.Queue;
import net.moon.game.objects.queues.impl.ClassicQueue;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerQueue {

    private final PlayerData playerData;
    @Getter private final Map<ClassicQueue, Long> currentQueues;

    
    public PlayerQueue(final PlayerData playerData) {
        this.playerData = playerData;
        this.currentQueues = new LinkedHashMap<>();
    }

    public void clear() {
        this.currentQueues.keySet().forEach(this::remove);
    }


    public void add(final Queue queue) {
        if (queue instanceof ClassicQueue classicQueue) {
            this.currentQueues.put(classicQueue, System.currentTimeMillis());
            classicQueue.add(this.playerData);
            if (this.currentQueues.size() == 1) {
                this.playerData.setState(PlayerState.QUEUE);
                this.playerData.applyHotbar();
            }
        }
    }
    public void remove(final Queue queue) {
        if (queue instanceof ClassicQueue classicQueue) {
            this.currentQueues.remove(classicQueue);
            classicQueue.remove(this.playerData);
            if (this.currentQueues.isEmpty()) {
                this.playerData.setState(PlayerState.LOBBY);
                this.playerData.applyHotbar();
            }
        }

    }
}
