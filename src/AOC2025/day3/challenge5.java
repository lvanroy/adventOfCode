package AOC2025.day3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static java.lang.Character.getNumericValue;

public class challenge5 {

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        String line;
        int result = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                int firstMax = -1;
                int secondMax = -1;
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    int digit = getNumericValue(c);
                    if (digit > firstMax && i != line.length() - 1) {
                        firstMax = digit;
                        secondMax = -1;
                    } else if (digit > secondMax) {
                        secondMax = digit;
                    }
                }
                System.out.println(firstMax);
                System.out.println(secondMax);
                System.out.println(firstMax * 10 + secondMax);
                result = result + firstMax * 10 + secondMax;
            }
        }
        System.out.printf("The total joltage is %s.", result);
    }
}
