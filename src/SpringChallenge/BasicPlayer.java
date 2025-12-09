package SpringChallenge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

class BasicPlayer {
    static final int MOD = 1073741824; // 2^30
    static int maxDepth;
    static final int[][][] neighbors = new int[9][][];

    static {
        neighbors[0] = new int[][]{{1, 3} };
        neighbors[1] = new int[][]{{0, 2}, {2, 4}, {0, 4}, {0, 2, 4} };
        neighbors[2] = new int[][]{{1, 5} };
        neighbors[3] = new int[][]{{0, 4}, {4, 6}, {0, 6}, {0, 4, 6} };
        neighbors[4] = new int[][]{{1, 3}, {1, 5}, {1, 7}, {3, 5}, {3, 7}, {5, 7}, {1, 3, 5}, {1, 3, 7}, {1, 5, 7}, {3, 5, 7}, {1, 3, 5, 7} };
        neighbors[5] = new int[][]{{2, 4}, {2, 8}, {4, 8}, {2, 4, 8} };
        neighbors[6] = new int[][]{{3, 7} };
        neighbors[7] = new int[][]{{4, 6}, {6, 8}, {4, 8}, {4, 6, 8} };
        neighbors[8] = new int[][]{{5, 7} };
    }

    public static void main (String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        maxDepth = Integer.parseInt(br.readLine());

        int[] state = new int[9];
        int index = 0;
        for (int i = 0; i < 3; i++) {
            String[] parts = br.readLine().split(" ");
            for (int j = 0; j < 3; j++) {
                int next = Integer.parseInt(parts[j]);
                state[index++] = next;
            }
        }

        System.out.println(compute(state, 0));
    }

    static int compute(int[] curState, int depth) {
        // Track the result
        int result = 0;

        // Verify if we reached max depth or a final state
        if (depth == maxDepth || isFinalState(curState)) {
            for (int digit : curState) {
                result = result * 10 + digit;
            }
            return result;
        }

        // Check for each position if we can branch out
        for (int position = 0; position < 9; position ++) {
            // If the current element is not 0 there is nothing we can do so continue
            if (curState[position] != 0) {
                continue;
            }

            // Attempt to capture
            int captureResult = applyCapture(curState, position, depth);
            if (captureResult != 0) {
                result = (result + captureResult) % MOD;
                continue;
            }

            // If we did not capture, attempt to place a 1 die
            int[] newState = Arrays.copyOf(curState, 9);
            newState[position] = 1;
            result = (result + compute(newState, depth + 1)) % MOD;
        }

        return result;
    }

    /**
     * Try to perform a capture move
     * @param curState current configuration of the board
     * @param position position on which we are trying to perform a capture move
     * @param depth current depth in the traversal, needed for recursion
     * @return the sum of all state configuration resulting from this capture module 2^30
     */
    static int applyCapture(int[] curState, int position, int depth) {
        int result = 0;
        for (int n = 0; n < neighbors[position].length; n++) {
            int [] captureOption = neighbors[position][n];
            // Compute the sum of all neighbors, will return -1 if we have an invalid configuration for capture
            int sum = computeSumOfNeighbors(curState, captureOption);
            if (sum != -1) {
                // We can capture! Create a new game state as if we have captured
                int[] newState = Arrays.copyOf(curState, 9);
                for (int neighborPosition : captureOption) {
                    newState[neighborPosition] = 0;
                }
                newState[position] = sum;

                // Compute result starting from this move
                result = (result + compute(newState, depth + 1)) % MOD;
            }
        }
        return result;
    }

    /**
     * Check if we have reached an end configuration, aka no more 0 positions
     * @param state the board state for which we want to evaluate
     * @return true if end state, false otherwise
     */
    static boolean isFinalState(int[] state) {
        for (int die: state) {
            if (die == 0) {
                return false;
            }
        }
        return true;
    }


    /**
     * Compute the sum of all the neighbors
     * @param state the state for which we want to compute the neighbor sum
     * @param neighbors the indices which we should consider as neighbors
     * @return -1 if we have an invalid configuration, otherwise return the sum
     */
    static int computeSumOfNeighbors(int[] state, int[] neighbors) {
        int sum = 0;
        for (int neighbor: neighbors) {
            if (state[neighbor] == 0) {
                return -1;
            }
            sum += state[neighbor];
        }
        return sum <= 6 ? sum : -1;
    }
}
