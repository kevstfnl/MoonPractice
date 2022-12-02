package net.moon.game.objects.match;

public record MatchTask(Match match) implements Runnable {

    @Override
    public void run() {
        switch (this.match.getState()) {
            case CREATING -> {
                /*
                if (this.match.getStandAloneArena() == null) {
                    this.match.setStandAloneArena(this.match.getArena().getAvailable(this.match.getKit().containBlock()));
                }
                if (this.match.getStandAloneArena() != null) {
                    this.match.setState(MatchState.STARTING);
                }*/
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
