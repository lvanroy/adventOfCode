package AOC2025.day4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class challenge7 {

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        String line;
        int result = 0;
        int[][] neighbors = new int[137][137];
        boolean[][] paper = new boolean[137][137];
        int row = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ((line = reader.readLine()) != null) {
                for (int col = 0; col < line.length(); col++) {
                    char c = line.charAt(col);
                    if (c == '@') {
                        paper[row][col] = true;
                    } else {
                        continue;
                    }
                    for (int drow = -1; drow <=1 ; drow++) {
                        for (int dcol = -1; dcol <=1 ; dcol++) {
                            if (row + drow >= line.length() || row + drow < 0 || col + dcol >= line.length() || col + dcol < 0 || (dcol == 0 && drow == 0)) {
                                continue;
                            }
                            neighbors[row + drow][col + dcol]++;
                        }
                    }
                }
                row++;
            }
        }

        for (row = 0; row < neighbors.length; row++) {
            for (int col = 0; col < neighbors[row].length; col++) {
                if (paper[row][col] && neighbors[row][col] < 4) {
                    result ++;
                }
            }
        }
        System.out.printf("The total number of movable stacks is %s.", result);
    }
}
