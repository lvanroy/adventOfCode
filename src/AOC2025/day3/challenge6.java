package AOC2025.day3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import static java.lang.Character.getNumericValue;

public class challenge6 {

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        String line;
        long result = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ((line = reader.readLine()) != null) {
                int[] digits = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    int digit = getNumericValue(c);
                    int startingIndex = line.length() - i >= 12 ? 0 : 12 - line.length() + i;
                    for (int j = startingIndex; j < 12; j++) {
                        if (digits[j] < digit) {
                            digits[j] = digit;
                            Arrays.fill(digits, j + 1, 12, -1);
                            break;
                        }
                    }
                }
                long resultingNumber = 0;
                for (int digit: digits) {
                    resultingNumber = resultingNumber * 10 + digit;
                }
                result += resultingNumber;
            }
        }
        System.out.printf("The total joltage is %s.", result);
    }
}
