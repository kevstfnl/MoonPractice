package net.moon.game.objects.queues.impl;

import net.moon.game.objects.kits.Kit;
import net.moon.game.objects.parties.Party;
import net.moon.game.objects.queues.Queue;
import net.moon.game.objects.queues.QueueType;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;

public class PartyQueue extends Queue {

    public PartyQueue(final QueueType type, final Kit kit) {
        super(type, kit);
        this.queue = new ConcurrentLinkedDeque<>();
    }

    public void add(Party party) {
        this.queue.add(party);
    }

    public void remove(Party party) {
        this.queue.remove(party);
    }

    public void shuffle() {
        final Random random = new Random();
        ConcurrentLinkedDeque<Object> randomData = new ConcurrentLinkedDeque<>();
        for (Object o : this.queue) {
            if (o instanceof Party party) {
                if (random.nextBoolean()) {
                    randomData.addFirst(party);
                } else {
                    randomData.addLast(party);
                }
            }
        }
        this.queue = randomData;
    }

    @Override
    public void run() {
        for (Object o : this.queue) {
            if (o instanceof Party party) {

            }
        }
    }
}