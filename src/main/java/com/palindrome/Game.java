package com.palindrome;

import java.util.*;
import java.util.concurrent.*;

public class Game {
    static final ConcurrentMap<String, Player> players = new ConcurrentHashMap<>();
    static final List<Player> top5 = Collections.synchronizedList(new ArrayList<>());
    volatile double scoreToBeat = -1;

    public void enterScore(String username, String palindrome) {
        double score = evaluate(palindrome);
        saveScore(username, score);
    }

    public List<Player> getHighScoresTop5() {
        return top5;
    }

    private static double evaluate(String palindrome) {
        int score = 0;
        int countedChars = 0;
        int uncountedFromRight = 0;
        for (int i = 0; i < palindrome.length() - 1; i++) {
            char a = palindrome.charAt(i);
            if (isCountable(a)) {
                countedChars++;
                MatchResult result = hasMatchingChar(palindrome, countedChars + uncountedFromRight, a);
                if (result.hasMatch) {
                    score++;
                    uncountedFromRight += result.uncounted;
                } else {
                    return 0d;
                }
            }
        }

        return score / 2d;
    }

    private static MatchResult hasMatchingChar(String palindrome, int countedChars, char a) {
        int uncounted = 0;
        for (int j = palindrome.length() - countedChars; j >= countedChars; j--) {
            char b = palindrome.charAt(j);
            if (isCountable(b)) {
                return new MatchResult(Character.toLowerCase(a) == Character.toLowerCase(b), uncounted);
            }
            uncounted++;
        }
        return new MatchResult(true, uncounted);
    }

    private static boolean isCountable(char c) {
        return Character.isDigit(c) || Character.isAlphabetic(c);
    }

    private void saveScore(String username, Double score) {
        Player player = updatePlayers(username, score);

        if (player.score > scoreToBeat || top5.size() < 5) {
            updateTop5(player);
        }
    }

    private Player updatePlayers(String username, Double score) {
        return players.compute(username, (k, v) -> v == null ? new Player(username, score) : updatePlayer(v, score));
    }

    private Player updatePlayer(Player player, Double score) {
        player.addScore(score);
        return player;
    }

    private synchronized void updateTop5(Player player) {
        if (!top5.contains(player)) {
            top5.add(player);
        }

        top5.sort(Comparator.comparingDouble(p -> -p.score));

        while (top5.size() > 5) {
            top5.remove(top5.size() - 1);
        }

        scoreToBeat = top5.get(top5.size() - 1).score;
    }

    private static class MatchResult {
        boolean hasMatch;
        int uncounted;

        MatchResult(boolean hasMatch, int uncounted) {
            this.hasMatch = hasMatch;
            this.uncounted = uncounted;
        }
    }
}
