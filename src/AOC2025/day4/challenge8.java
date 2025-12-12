package AOC2025.day4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class challenge8 {

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        String line;
        int result = 0;
        int[][] neighbors = null;
        boolean[][] paper = null;
        int row = 0;
        int size = -1;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ((line = reader.readLine()) != null) {
                if (size == -1) {
                    size = line.length();
                    neighbors = new int[size][size];
                    paper = new boolean[size][size];
                }
                for (int col = 0; col < size; col++) {
                    char c = line.charAt(col);
                    if (c == '@') {
                        paper[row][col] = true;
                    } else {
                        continue;
                    }
                    for (int drow = -1; drow <=1 ; drow++) {
                        for (int dcol = -1; dcol <=1 ; dcol++) {
                            if (row + drow >= line.length() || row + drow < 0 || col + dcol >= size || col + dcol < 0 || (dcol == 0 && drow == 0)) {
                                continue;
                            }
                            neighbors[row + drow][col + dcol]++;
                        }
                    }
                }
                row++;
            }
        }

        boolean canMakeMoves = true;
        while (canMakeMoves) {
            canMakeMoves = false;
            for (row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    if (paper[row][col] && neighbors[row][col] < 4) {
                        result ++;
                        canMakeMoves = true;
                        for (int drow = -1; drow <=1 ; drow++) {
                            for (int dcol = -1; dcol <=1 ; dcol++) {
                                if (row + drow >= size || row + drow < 0 || col + dcol >= size || col + dcol < 0 || (dcol == 0 && drow == 0)) {
                                    continue;
                                }
                                neighbors[row + drow][col + dcol]--;
                            }
                        }
                        paper[row][col] = false;
                    }
                }
            }
        }

        System.out.printf("The total number of movable stacks is %s.", result);
    }
}
