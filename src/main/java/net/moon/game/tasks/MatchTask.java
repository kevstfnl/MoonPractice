package net.moon.game.tasks;

import net.moon.game.objects.match.Match;
import net.moon.game.objects.match.MatchState;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchTask extends BukkitRunnable {

    private final Match match;

    public MatchTask(final Match match) {
        this.match = match;
    }

    @Override
    public void run() {
        switch (this.match.getState()) {
            case CREATING -> {
                if (this.match.getStandAloneArena() == null) {
                    this.match.setStandAloneArena(this.match.getArena().getAvailable(this.match.getKit().containBlock()));
                }
                if (this.match.getStandAloneArena() != null) {
                    this.match.setState(MatchState.STARTING);
                }
            }
            case STARTING -> {
                this.match.apply();
                if (System.currentTimeMillis() - this.match.getPending() <= 3000) {
                    this.match.setState(MatchState.INPROGRESS);
                }
            }
            case INPROGRESS -> {

            }
            case ENDING -> {

            }
        }
    }
}
