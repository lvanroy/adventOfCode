package AOC2025.day1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class challenge1 {
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
                if (line.charAt(0) == 'L') {
                    dial = (dial - Integer.parseInt(line.substring(1))) % 100;
                } else {
                    dial = (dial + Integer.parseInt(line.substring(1))) % 100;
                }
                if (dial == 0) {
                    result ++;
                }
            }
        }
        System.out.printf("There are %s zeroes.", result);
    }
}
