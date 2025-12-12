package AOC2025.day5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class challenge9 {

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        List<List<Long>> freshIdRanges = new ArrayList<>();
        String line;
        int result = 0;
        boolean freshList = true;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    freshList = false;
                    continue;
                }
                if (freshList) {
                    String[] tokens = line.split("-");
                    freshIdRanges.add(List.of(parseLong(tokens[0]), parseLong(tokens[1])));
                } else {
                    long id = parseLong(line);
                    for (List<Long> range: freshIdRanges) {
                        if (id >= range.get(0) && id <= range.get(1)) {
                            result ++;
                            break;
                        }
                    }
                }
            }
        }
        System.out.printf("The total number of fresh ids is %s.", result);
    }
}
