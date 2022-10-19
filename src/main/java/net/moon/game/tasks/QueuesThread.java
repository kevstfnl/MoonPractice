package net.moon.game.tasks;

import net.moon.game.objects.queues.QueueManager;

public class QueuesThread extends Thread {

    private final QueueManager queueManager;

    public QueuesThread(final QueueManager queueManager) {
        this.queueManager = queueManager;
        this.setPriority(1);
        this.setName("Practice Queue");
        this.start();
    }

    @Override
    public void run() {
        this.queueManager.run();
    }
}
