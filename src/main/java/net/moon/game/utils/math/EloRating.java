package net.moon.game.utils.math;

public class EloRating {
    public static int getEloRating(int winner, int loser) {
        return (int) Math.max(32 - Math.round(1 / (1 + Math.pow(10, (double) (loser - winner) / 300)) * 32), 1);
    }

}