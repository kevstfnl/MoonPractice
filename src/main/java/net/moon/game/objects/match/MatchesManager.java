package net.moon.game.objects.match;

import lombok.Getter;
import net.moon.game.Practice;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MatchesManager {

    private final List<Match> matches;
    public final List<Match> cachedMatches;

    public MatchesManager(final Practice instance) {
        this.matches = new ArrayList<>();
        this.cachedMatches = new ArrayList<>();
    }

    public void create(final Match match) {
        this.matches.add(match);
    }
}
