package war;

import War.Solution;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolutionTest {
    @Test
    void testBase() {
        Solution.p1 = new ArrayList<>(Arrays.asList(14, 13, 12));
        Solution.p2 = new ArrayList<>(Arrays.asList(13, 12, 11));
        assertEquals("1 3\r\n", Solution.solve());
    }

    @Test
    void testWar() {
        Solution.p1 = new ArrayList<>(Arrays.asList(8, 13, 14, 12, 2));
        Solution.p2 = new ArrayList<>(Arrays.asList(8, 2, 3, 4, 3));
        assertEquals("2 1\r\n", Solution.solve());
    }
}
