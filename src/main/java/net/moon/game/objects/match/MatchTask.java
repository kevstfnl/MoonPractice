package net.moon.game.objects.match;

public record MatchTask(Match match) implements Runnable {

    @Override
    public void run() {
        switch (this.match.getState()) {
            case CREATING -> {
                if (this.match.getStandaloneArena() == null) {
                    this.match.getArenasManager().getAvailableArena(this.match.getKit());
                }
                if (this.match.getStandaloneArena() != null && this.match.isReady()) {
                    this.match.setState(MatchState.STARTING);
                    this.match.apply();
                }
            }
            case STARTING -> {
                if (System.currentTimeMillis() - this.match.getPending() <= 3000) {
                    this.match.setState(MatchState.INPROGRESS);
                    this.match.sendMessage("§aStarting match !");
                }
            }
            case INPROGRESS -> {

            }
            case ENDING -> {

            }
        }
    }
}
