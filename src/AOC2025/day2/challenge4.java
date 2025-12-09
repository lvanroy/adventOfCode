package AOC2025.day2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Long.parseLong;

public class challenge4 {
    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        long result = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine(); // There always is one line of input of comma separated values
            String[] ranges = line.split(",");
            for (String range: ranges) {
                String[] bounds = range.split("-");
                for (long id = parseLong(bounds[0]); id <= parseLong(bounds[1]); id++) {
                    String str = Long.toString(id);
                    for (int seqSize = 1; seqSize <= str.length() / 2; seqSize++) {
                        if (str.length() % seqSize != 0) {
                            continue;
                        }
                        Set<String> sequences = new HashSet<>();
                        for (int index = 0; index < str.length(); index += seqSize) {
                            sequences.add(str.substring(index, index + seqSize));
                        }
                        if (sequences.size() == 1) {
                            result += id;
                            break;
                        }
                    }
                }
            }
        }
        System.out.printf("The total sum of invalid IDs is: %s.", result);
    }

}
