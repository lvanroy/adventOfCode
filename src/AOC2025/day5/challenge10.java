package AOC2025.day5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Long.parseLong;

public class challenge10 {
    private static List<Long> freshIdRanges = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        // All even indexes represent the start of an interval, all odd indexes the end of an interval
        // They will also be sorted in increasing manner
        // E.g. [0, 3, 6, 9] represents the intervals 0-3 and 6-9
        String line;
        long result = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                String[] tokens = line.split("-");
                long start = parseLong(tokens[0]);
                long end = parseLong(tokens[1]);
                boolean added = false;
                for (int i = 0; i < freshIdRanges.size(); i += 2) {
                    // Check if the start overlaps
                    if (start >= freshIdRanges.get(i) && start <= freshIdRanges.get(i + 1)) {
                        if (end > freshIdRanges.get(i + 1)) {
                            freshIdRanges.set(i + 1, end);
                        }
                        added = true;
                        mergeEntries(end, i);
                        break;
                    }
                    // Check if the end overlaps
                    if (end >= freshIdRanges.get(i) && end <= freshIdRanges.get(i + 1)) {
                        freshIdRanges.set(i, start);
                        added = true;
                        mergeEntries(end, i);
                        break;
                    }
                    // Check if it is entirely before the current element
                    if (start < freshIdRanges.get(i)) {
                        freshIdRanges.add(i, start);
                        freshIdRanges.add(i + 1, end);
                        added = true;
                        mergeEntries(end, i);
                        break;
                    }
                }
                if (!added) {
                    freshIdRanges.add(start);
                    freshIdRanges.add(end);
                }
            }
        }
        for (int i = 0; i < freshIdRanges.size(); i += 2) {
            result += freshIdRanges.get(i + 1) - freshIdRanges.get(i) + 1;
        }
        System.out.printf("The total number of fresh ids is %s.", result);
    }

    private static void mergeEntries(long end, int i) {
        // Double check that the new end does not overlap with the next interval
        boolean mergePotential = true;
        while (mergePotential && i < freshIdRanges.size() - 3) {
            mergePotential = false;
            if (end >= freshIdRanges.get(i + 2) && end <= freshIdRanges.get(i + 3)) {
                freshIdRanges.remove(i + 1);
                freshIdRanges.remove(i + 1);
            }

            else if (end >= freshIdRanges.get(i + 1) && end > freshIdRanges.get(i + 3)) {
                freshIdRanges.remove(i + 2);
                freshIdRanges.remove(i + 2);
                mergePotential = true;
            }
        }
    }
}