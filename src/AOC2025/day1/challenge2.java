package AOC2025.day1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static java.lang.Integer.parseInt;

public class challenge2 {
    // a dial with one arrow, 0 to 99
    // sequence of rotations
    //     L or R for direction
    //     Distance to rotate
    // Dial starts at 50

    public static void main(String[] args) throws IOException {
        int dial = 50;
        int result = 0;
        String fileName = args[0];
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ((line = reader.readLine()) != null) {
                int distance = parseInt(line.substring(1));
                result += distance / 100;
                distance = distance % 100;

                if (line.charAt(0) == 'L') {
                    if (dial > 0 && dial - distance < 0) {
                        result ++;
                    }
                    dial = (dial - distance) % 100;
                    if (dial < 0) {
                        dial += 100;
                    }
                } else {
                    if (dial + distance > 100) {
                        result ++;
                    }
                    dial = (dial + distance) % 100;
                }

                if (dial == 0) {
                    result ++;
                }
            }
        }
        System.out.printf("There are %s zeroes.", result);
    }
}
