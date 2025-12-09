package SpringChallenge;

import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest {

    @ParameterizedTest
    @MethodSource("gameConfigurationsWithResults")
    @Timeout(value = 5, unit = SECONDS)
    void testBasicPlayer(int[] startState, int maxDepth, int result) throws ExecutionException, InterruptedException {
        BasicPlayer.maxDepth = maxDepth;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<Integer> future = executor.submit(() -> BasicPlayer.compute(startState, 0));
            int actualResult = future.get();
            assertEquals(result, actualResult);
        } finally {
            executor.shutdownNow();
        }
    }

    @ParameterizedTest
    @MethodSource("gameConfigurationsWithResults")
    void testAdvancedPlayer(int[] startState, int maxDepth, int result) {
        AdvancedPlayer.reset();
        AdvancedPlayer.maxDepth = maxDepth;
        assertEquals(result, AdvancedPlayer.compute(startState, 0));
    }

    @ParameterizedTest
    @MethodSource("gameConfigurationsWithResults")
    void testPlayer(int[] startState, int maxDepth, int result) {
        Player.reset();
        Player.maxDepth = maxDepth;
        Player.computeMask(startState);
        Player.encodeState(startState, 0);
        assertEquals(result, Player.compute(startState, 0, countOccurrences(startState, 0)) % 1073741824);
    }

    static Stream<Arguments> gameConfigurationsWithResults() {
        return Stream.of(
                Arguments.of(new int[]{0, 6, 0, 2, 2, 2, 1, 6, 1}, 20, 322444322),
                Arguments.of(new int[]{5, 0, 6, 4, 5, 0, 0, 6, 4}, 20, 951223336),
                Arguments.of(new int[]{5, 5, 5, 0, 0, 5, 5, 5, 5}, 1, 36379286),
                Arguments.of(new int[]{6, 1, 6, 1, 0, 1, 6, 1, 6}, 1, 264239762),
                Arguments.of(new int[]{6, 0, 6, 0, 0, 0, 6, 1, 5}, 8, 76092874),
                Arguments.of(new int[]{3, 0, 0, 3, 6, 2, 1, 0, 2}, 24, 661168294),
                Arguments.of(new int[]{6, 0, 4, 2, 0, 2, 4, 0, 0}, 36, 350917228),
                Arguments.of(new int[]{0, 0, 0, 0, 5, 4, 1, 0, 5}, 32, 999653138),
                Arguments.of(new int[]{0, 0, 4, 0, 2, 4, 1, 3, 4}, 40, 521112022),
                Arguments.of(new int[]{0, 5, 4, 0, 3, 0, 0, 3, 0}, 40, 667094338),
                Arguments.of(new int[]{0, 5, 1, 0, 0, 0, 4, 0, 1}, 20, 738691369),
                Arguments.of(new int[]{1, 0, 0, 3, 5, 2, 1, 0, 0}, 20, 808014757)
        );
    }

    public static int countOccurrences(int[] array, int target) {
        int count = 0;
        for (int num : array) {
            if (num == target) {
                count++;
            }
        }
        return count;
    }
}
