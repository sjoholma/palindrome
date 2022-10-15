package com.palindrome;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

class GameTest {
    static final int LOOPS = 3000000;
    static final int QUEUE = 3 * LOOPS;
    static final int THREADS = 20;

    @Test
    void getHighScoresTop5() throws InterruptedException {
        Game game = new Game();
        long start = System.currentTimeMillis();

        ThreadPoolExecutor pool = new ThreadPoolExecutor(THREADS, THREADS, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(QUEUE));
        IntStream.range(0, LOOPS).forEach(i -> pool.execute(() -> {
            game.enterScore("Mike", "1Jack kcaJ1");
            game.enterScore("Silvia", "A man, a plan, a canal - Panama!");
            game.enterScore("Doris", "Not a palindrome");
        }));

        IntStream.range(0, LOOPS).forEach(i -> pool.execute(() -> {
            game.enterScore("Mike", "Pop");
            game.enterScore("Dorothy", "1AB ab faf ba BA1");
            game.enterScore("Bertha", "Lol");
        }));

        IntStream.range(0, LOOPS).forEach(i -> pool.execute(() -> {
            game.enterScore("John", "Madam, I'm Adam");
            game.enterScore("Bruno", "Dammit I'm mad");
        }));

        pool.shutdown();

        if (pool.awaitTermination(20, TimeUnit.SECONDS)) {
            long stop = System.currentTimeMillis() - start;
            List<Player> top5 = game.getHighScoresTop5();
            System.out.println("Top 5");
            top5.forEach(System.out::println);

            Assertions.assertEquals("Silvia", top5.get(0).name);
            Assertions.assertEquals(31500000, top5.get(0).score);
            Assertions.assertEquals("Dorothy", top5.get(1).name);
            Assertions.assertEquals(18000000, top5.get(1).score);
            Assertions.assertEquals("Mike", top5.get(2).name);
            Assertions.assertEquals(16500000, top5.get(2).score);
            Assertions.assertEquals("John", top5.get(3).name);
            Assertions.assertEquals(15000000, top5.get(3).score);
            Assertions.assertEquals("Bruno", top5.get(4).name);
            Assertions.assertEquals(15000000, top5.get(4).score);

            System.out.println();
            System.out.println("Loops: " + LOOPS);
            System.out.println("Threads: " + THREADS);
            System.out.println("Took: " + stop + " ms");
        } else {
            pool.shutdownNow();
            System.out.println("Timeout");
        }
    }
}