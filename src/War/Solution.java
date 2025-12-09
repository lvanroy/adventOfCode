package War;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class Solution {
    public static List<Integer> p1 = new ArrayList<>();
    public static List<Integer> p2 = new ArrayList<>();
    public static List<Integer> p1War = new ArrayList<>();
    public static List<Integer> p2War = new ArrayList<>();

    public static void main(String args[]) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine()); // the number of cards for player 1
        for (int i = 0; i < n; i++) {
            p1.add(cardToInt(br.readLine())); // the n cards of player 1
        }
        int m = Integer.parseInt(br.readLine()); // the number of cards for player 2
        for (int i = 0; i < m; i++) {
            p2.add(cardToInt(br.readLine())); // the n cards of player 2
        }
        System.out.println(solve());
    }

    public static String solve() {
        int round = 0;
        while (!p1.isEmpty() && !p2.isEmpty()) {
            System.err.println(p1);
            System.err.println(p2);
            int c1 = p1.removeFirst();
            int c2 = p2.removeFirst();
            if (c1 > c2) {
                round ++;
                p1.addAll(p1War);
                p1.add(c1);
                p1War.clear();
                p1.addAll(p2War);
                p1.add(c2);
                p2War.clear();
            } else if (c1 < c2) {
                round ++;
                p2.addAll(p1War);
                p2.add(c1);
                p1War.clear();
                p2.addAll(p2War);
                p2.add(c2);
                p2War.clear();
            } else {
                if (p1.size() < 4 || p2.size() < 4) {
                    return "PAT";
                } else {
                    p1War.add(c1);
                    p1War.add(p1.removeFirst());
                    p1War.add(p1.removeFirst());
                    p1War.add(p1.removeFirst());
                    p2War.add(c2);
                    p2War.add(p2.removeFirst());
                    p2War.add(p2.removeFirst());
                    p2War.add(p2.removeFirst());
                }
            }
        }

        if (!p1War.isEmpty()) {
            return "PAT";
        } else if (p1.isEmpty()) {
            return format("2 %d%n", round);
        } else {
            return format("1 %d%n", round);
        }
    }

    private static int cardToInt(String card) {
        card = card.substring(0, card.length() - 1);
        if (isNumeric(card)) {
            return Integer.parseInt(card);
        } else {
            return switch (card) {
                case "J": yield 11;
                case "Q": yield 12;
                case "K": yield 13;
                default: yield 14;
            };
        }
    }

    private static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}
