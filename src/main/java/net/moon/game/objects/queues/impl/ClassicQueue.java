package net.moon.game.objects.queues.impl;

import net.moon.game.objects.kits.Kit;
import net.moon.game.objects.players.PlayerData;
import net.moon.game.objects.queues.Queue;
import net.moon.game.objects.queues.QueueType;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ClassicQueue extends Queue {

    public ClassicQueue(final QueueType type, final Kit kit) {
        super(type, kit);
    }

    public void add(PlayerData playerData) {
        this.queue.add(playerData);
    }

    public void remove(PlayerData playerData) {
        this.queue.remove(playerData);
    }

    public void shuffle() {
        final Random random = new Random();
        ConcurrentLinkedDeque<Object> randomData = new ConcurrentLinkedDeque<>();
        for (Object o : this.queue) {
            if (o instanceof PlayerData playerData) {
                if (random.nextBoolean()) {
                    randomData.addFirst(playerData);
                } else {
                    randomData.addLast(playerData);
                }
            }
        }
        this.queue = randomData;
    }

    @Override
    public void run() {
        for (Object o : this.queue) {
            if (o instanceof PlayerData playerData) {

            }
        }
    }
}
