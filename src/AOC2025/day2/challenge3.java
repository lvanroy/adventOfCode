package AOC2025.day2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static java.lang.Long.parseLong;

public class challenge3 {
    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        long result = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine(); // There always is one line of input of comma separated values
            String[] ranges = line.split(",");
            for (String range: ranges) {
                String[] bounds = range.split("-");
                for (long i = parseLong(bounds[0]); i <= parseLong(bounds[1]); i++) {
                    String str = Long.toString(i);
                    if (str.substring(0, str.length() / 2).equals(str.substring(str.length() / 2))) {
                        result += i;
                    }
                }
            }
        }
        System.out.printf("The total sum of invalid IDs is: %s.", result);
    }

}
