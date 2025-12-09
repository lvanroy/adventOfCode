package SpringChallenge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

class Player {
    static final int MOD = 1073741824;
    static final int CACHE_SIZE = 1 << 25;
    static int[] stateCache = new int[CACHE_SIZE];
    static byte[] stateUsed = new byte[CACHE_SIZE];
    static long[] keyCache = new long[CACHE_SIZE];
    static int maxDepth = 0;
    static int stateMask;
    static long key;
    static long fullKey;

    static final int[][][] neighbors = new int[9][][];
    static final int[][] neighborMasks = new int[9][];
    static final long[][] keyUpdates = new long[9][7];
    static final int[] POW10 = {100000000, 10000000, 1000000, 100000, 10000, 1000, 100, 10, 1};
    static final long[] POW7 = {5764801, 823543, 117649, 16807, 2401, 343, 49, 7, 1};
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

        for (int pos = 0; pos < 9; pos ++) {
            keyUpdates[pos] = new long[7];
            for (int val = 0; val < 7; val ++) {
                keyUpdates[pos][val] = POW7[pos] * val;
            }
        }

        for (int pos = 0; pos < 9; pos++) {
            int[][] sets = neighbors[pos];
            neighborMasks[pos] = new int[sets.length];
            for (int i = 0; i < sets.length; i++) {
                int mask = 0;
                for (int idx : sets[i]) {
                    mask |= 1 << idx;
                }
                neighborMasks[pos][i] = mask;
            }
        }
    }

    public static void reset() {
        stateCache = new int[CACHE_SIZE];
        stateUsed = new byte[CACHE_SIZE];
        keyCache = new long[CACHE_SIZE];
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        maxDepth = Integer.parseInt(br.readLine());
        System.err.println(maxDepth);

        int[] state = new int[9];
        int openCount = 0;
        int index = 0;
        for (int i = 0; i < 3; i++) {
            String[] parts = br.readLine().split(" ");
            for (int j = 0; j < 3; j++) {
                int next = Integer.parseInt(parts[j]);
                state[index++] = next;
                if (next == 0) openCount++;
            }
        }
        System.err.println(Arrays.toString(state));

        // Generate a state mask to speed up the capture checking
        computeMask(state);

        // Generate an encoded state to do matching
        encodeState(state, 0);

        System.out.println(compute(state, 0, openCount) % MOD);
    }

    // curState is an array of length 9 with digits ranging from 0 to 6
    // 0 < depth < 40
    static int compute(int[] curState, int depth, int openCount) {
        // Verify if we reached maxed depth or a terminal state
        if (depth == maxDepth || openCount == 0) {
            int result = 0;
            for (int digit : curState) {
                result = result * 10 + digit;
            }
            return result;
        }

        // Encode the key used for state matching
        int index = (int) (fullKey & (CACHE_SIZE - 1));
        if (stateUsed[index] == 1 && keyCache[index] == fullKey) return stateCache[index];

        // Track the partial sum starting from this state
        int modSum = 0;

        // create a temp storage to revert changes after computation
        int[] backup = new int[4];

        // Check for each position if we can branch out
        for (int pos = 0; pos < 9; pos ++) {
            // If the current element is not 0 there is nothing we can do so continue
            if (curState[pos] != 0) {
                continue;
            }

            // Attempt capturing
            boolean captured = false;
            for (int n = 0; n < neighbors[pos].length; n++) {
                if ((stateMask & neighborMasks[pos][n]) != neighborMasks[pos][n]) {
                    continue;
                }
                int [] option = neighbors[pos][n];
                int sum = 0;
                for (int i: option) {
                    sum += curState[i];
                }
                if (sum <= 6) {
                    captured = true;
                    curState[pos] = sum;
                    stateMask ^= 1 << pos;
                    for (int i = 0; i < option.length; i++) {
                        backup[i] = curState[option[i]];
                        key -= keyUpdates[option[i]][backup[i]];
                        curState[option[i]] = 0;
                        stateMask ^= 1 << option[i];
                    }
                    key = key + keyUpdates[pos][sum] - keyUpdates[pos][0];
                    fullKey = (key << 6) | (depth + 1);
                    modSum += compute(curState, depth + 1, openCount - 1 + option.length);
                    if (modSum > MOD) modSum -= MOD;
                    curState[pos] = 0;
                    stateMask ^= 1 << pos;
                    for (int i = 0; i < option.length; i++) {
                        key += keyUpdates[option[i]][backup[i]];
                        curState[option[i]] = backup[i];
                        stateMask ^= 1 << option[i];
                    }
                    key = key - keyUpdates[pos][sum] + keyUpdates[pos][0];
                    fullKey = (key << 6) | depth;
                }
            }

            if (captured) {
                continue;
            }

            curState[pos] = 1;
            key = key + keyUpdates[pos][1] - keyUpdates[pos][0];
            fullKey = (key << 6) | (depth + 1);
            if (openCount == 1) {

                int newStateInt = 0;
                for (int i = 0; i < 9; i++) {
                    newStateInt += curState[i] * POW10[i];
                }
                modSum += newStateInt;
                if (modSum > MOD) modSum -= MOD;
                curState[pos] = 0;
                int index2 = (int) (fullKey & (CACHE_SIZE - 1));
                stateUsed[index2] = 1;
                keyCache[index2] = fullKey;
                stateCache[index2] = modSum;
                key = key - keyUpdates[pos][1] + keyUpdates[pos][0];
                fullKey = (key << 6) | depth;
                return modSum;
            } else {
                stateMask ^= 1 << pos;
                modSum +=  compute(curState, depth + 1, openCount - 1);
                if (modSum > MOD) modSum -= MOD;
                curState[pos] = 0;
                stateMask ^= 1 << pos;
                key = key - keyUpdates[pos][1] + keyUpdates[pos][0];
                fullKey = (key << 6) | depth;
            }
        }
        stateUsed[index] = 1;
        keyCache[index] = fullKey;
        stateCache[index] = modSum;
        return modSum;
    }

    static void encodeState(int[] state, int depth) {
        key = 0;
        for (int i = 0; i < 9; i++) key = key * 7 + state[i];
        fullKey = (key << 6) | depth;
    }

    static long encodeKey(int[] state, int depth) {
        long key = 0;
        for (int i = 0; i < 9; i++) key = key * 7 + state[i];
        return key;
    }

    static long encodeFullKey(int [] state, int depth) {
        long key = 0;
        for (int i = 0; i < 9; i++) key = key * 7 + state[i];
        return (key << 6) | depth;
    }

    static void computeMask(int[] state) {
        stateMask = 0;
        for (int i = 0; i < 9; i++) {
            if (state[i] != 0) {
                stateMask |= 1 << i;
            }
        }
    }
}
