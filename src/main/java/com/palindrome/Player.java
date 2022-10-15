package com.palindrome;

public class Player {
    String name;
    volatile double score;

    public Player(String name, double initialScore) {
        this.name = name;
        this.score = initialScore;
    }

    public void addScore(double score) {
        this.score += score;
    }

    public String toString() {
        return name + ": " + score;
    }
}
