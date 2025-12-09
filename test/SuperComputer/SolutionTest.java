package SuperComputer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolutionTest {

    @Test
    void basicTest() {
        Solution.jobs = new int[][]{new int[]{2, 6}, new int[]{9, 15}, new int[]{15, 20}, new int[]{9, 11}};
        Solution.n = 4;
        assertEquals(3, Solution.solve());
    }

    @Test
    void basicTest2() {
        Solution.jobs = new int[][]{new int[]{3, 7}, new int[]{9, 10}, new int[]{24, 28}, new int[]{16, 24}, new int[]{11, 16}};
        Solution.n = 5;
        assertEquals(3, Solution.solve());
    }
}
