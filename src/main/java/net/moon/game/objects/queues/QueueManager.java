package net.moon.game.objects.queues;

import lombok.Getter;
import net.moon.game.Practice;
import net.moon.game.objects.kits.Kit;
import net.moon.game.objects.queues.impl.ClassicQueue;
import net.moon.game.objects.queues.impl.PartyQueue;
import net.moon.game.tasks.QueuesThread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {

    private final Practice instance;
    @Getter private final Map<QueueType, Map<Kit, Queue>> queues;


    public QueueManager(final Practice instance) {
        this.instance = instance;
        this.queues = new ConcurrentHashMap<>();
        init();
    }

    private void init() {
        this.instance.execute(() -> {
            final Map<Kit, Queue> queueMap = new HashMap<>();
            for (Kit kit : this.instance.getKitsManager().getKits().values()) {
                if (kit.isEnabled()) {
                    for (QueueType type : QueueType.values()) {
                        //Create all kit queue on all type
                        Queue queue;
                        if (type.isParty()) {
                            queue = new PartyQueue(type, kit);
                        } else {
                            queue = new ClassicQueue(type, kit);
                        }
                        queueMap.put(kit, queue);
                    }
                }
            }
            queueMap.forEach((kit, queue) -> this.queues.put(queue.getType(), queueMap));
            new QueuesThread(this);
        });
    }
    public void stop() { this.queues.clear(); }

    public void run() { this.queues.values().forEach(kitQueueMap -> kitQueueMap.values().forEach(Queue::run)); }

    public List<Queue> getByType(final QueueType type) { return this.queues.get(type).values().stream().toList(); }
}
