package SuperComputer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;

class Solution {

    static int[][] jobs;
    static int n;

    // Can never be better to have a fully overlapping job
    public static void main(String args[]) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(br.readLine());
        jobs = new int[n][2];
        for (int i = 0; i < n; i++) {
            String[] input = br.readLine().split("\\s");
            int start = Integer.parseInt(input[0]);
            jobs[i][0] = start;
            jobs[i][1] = start + Integer.parseInt(input[1]) - 1;
        }
        System.out.println(solve());
    }

    public static int solve() {
        Arrays.sort(jobs, Comparator.comparingInt(a -> a[1]));
        int end = 0;
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (end < jobs[i][0]){
                end = jobs[i][1];
                count ++;
            }
        }
        return count;
    }
}
