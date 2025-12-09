package CodeVsZombies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static java.lang.Math.ceil;
import static java.lang.Math.hypot;
import static java.lang.String.format;

class Player {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    // Track all humans and zombies
    static Human[] humans;
    static Zombie[] zombies;


    static int distance = Integer.MAX_VALUE;
    static int x;
    static int y;
    static int[] xOptions = new int[8];
    static int[] yOptions = new int[8];
    static int[] killCounter = new int[8];
    static int[] xDelta = new int[]{1000, -1000, 0, 0, 500, 500, -500, -500};
    static int[] yDelta = new int[]{0, 0, 1000, -1000, 500, -500, 500, -500};
    static int closestZombie;
    static int lowestTimeToLive = 46;
    static int bestZombieKill = -1;
    static double closestZombieDistance = 18358;

    public static void main(String args[]) throws IOException {
        int bestX = 0;
        int bestY = 0;

        readInitialInput();

        // game loop
        while (true) {
            System.out.print(simulate());
            readInput();
        }
    }

    static String simulate() {
        if (bestZombieKill != -1) {
            return format("%d %d%n", zombies[bestZombieKill].x, zombies[bestZombieKill].y);
        }
        int max = 0;
        int bestIndex = -1;
        for (int i = 0; i < 8; i++) {
            if (killCounter[i] > max) {
                max = killCounter[i];
                bestIndex = i;
            }
        }

        if (bestIndex != -1) {
            return format("%d %d%n",xOptions[bestIndex], yOptions[bestIndex]);
        } else {
            return format("%d %d%n", zombies[closestZombie].x, zombies[closestZombie].y);
        }
    }

    static void readInitialInput() throws IOException {
        // --- reset the environment ---
        resetEnv();

        // --- Initialize Ash ---
        String line = br.readLine();
        System.err.println(line);
        String[] input = line.split("\\s");
        x = Integer.parseInt(input[0]);
        y = Integer.parseInt(input[1]);
//        System.err.printf("Ash %d %d%n", x, y);

        for (int i = 0; i < 8; i++) {
            int newX = x + xDelta[i];
            int newY = y + yDelta[i];
            if (newX >= 0 && newX < 16000 && newY >= 0 && newY < 9000) {
                xOptions[i] = newX ;
                yOptions[i] = newY;
            } else {
                xOptions[i] = -1;
                yOptions[i] = -1;
            }
        }

        // --- Initialize humans ---
        line = br.readLine();
        System.err.println(line);
        int humanCount = Integer.parseInt(line);
        humans = new Human[humanCount];
        for (int i = 0; i < humanCount; i++) {
            line = br.readLine();
            System.err.println(line);
            input = line.split("\\s");
            int humanId = Integer.parseInt(input[0]);
            int humanX = Integer.parseInt(input[1]);
            int humanY = Integer.parseInt(input[2]);
//            System.err.printf("Human %d %d %d%n", humanId, humanX, humanY);

            humans[humanId] = new Human(humanX, humanY);
        }

        // --- Initialize zombies ---
        line = br.readLine();
        System.err.println(line);
        int zombieCount = Integer.parseInt(line);
        zombies = new Zombie[zombieCount];
        closestZombie = -1;
        for (int i = 0; i < zombieCount; i++) {
            line = br.readLine();
            System.err.println(line);
            input = line.split("\\s");
            int zombieId = Integer.parseInt(input[0]);
            int zombieX = Integer.parseInt(input[1]);
            int zombieY = Integer.parseInt(input[2]);
            int zombieXNext = Integer.parseInt(input[3]);
            int zombieYNext = Integer.parseInt(input[4]);
//            System.err.printf("zombie %d %d %d %d %d%n", zombieId, zombieX, zombieY, zombieXNext, zombieYNext);

            zombies[zombieId] = new Zombie(zombieX, zombieY, zombieXNext, zombieYNext);

            // --- Compute time to live for all humans ---
            analyzeHumanTtl(zombieId, zombieXNext, zombieYNext);

            // --- Compute proximity of zombie to new Ash option ---
            checkKillOptions(zombieXNext, zombieYNext);

            // --- Compute proximity of zombie to current Ash ---
            checkClosestZombie(zombieId, zombieXNext, zombieYNext);
        }
    }

    static void readInput() throws IOException {
        // --- reset the environment ---
        resetEnv();

        // --- Initialize Ash ---
        String line = br.readLine();
        System.err.println(line);
        String[] input = line.split("\\s");
        x = Integer.parseInt(input[0]);
        y = Integer.parseInt(input[1]);
//        System.err.printf("Ash %d %d%n", x, y);

        for (int i = 0; i < 8; i++) {
            int newX = x + xDelta[i];
            int newY = y + yDelta[i];
            if (newX >= 0 && newX < 16000 && newY >= 0 && newY < 9000) {
                xOptions[i] = newX ;
                yOptions[i] = newY;
            } else {
                xOptions[i] = -1;
                yOptions[i] = -1;
            }
        }

        // --- Initialize humans ---
        line = br.readLine();
        System.err.println(line);
        int humanCount = Integer.parseInt(line);
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < humanCount; i++) {
            line = br.readLine();
            System.err.println(line);
            input = line.split("\\s");
            int humanId = Integer.parseInt(input[0]);
            ids.add(humanId);
            humans[humanId].turnsToLive = 46;
        }
        for (int i = 0; i < humans.length; i++) {
            if (!ids.contains(i)) {
                humans[i] = null;
            }
        }

        // --- Initialize zombies ---
        line = br.readLine();
        System.err.println(line);
        int zombieCount = Integer.parseInt(line);
        closestZombie = -1;
        ids.clear();
        for (int i = 0; i < zombieCount; i++) {
            line = br.readLine();
            System.err.println(line);
            input = line.split("\\s");
            int zombieId = Integer.parseInt(input[0]);
            int zombieX = Integer.parseInt(input[1]);
            int zombieY = Integer.parseInt(input[2]);
            int zombieXNext = Integer.parseInt(input[3]);
            int zombieYNext = Integer.parseInt(input[4]);
//            System.err.printf("zombie %d %d %d %d %d%n", zombieId, zombieX, zombieY, zombieXNext, zombieYNext);

            zombies[zombieId].update(zombieX, zombieY, zombieXNext, zombieYNext);
            ids.add(zombieId);

            // --- Compute time to live for all humans ---
            analyzeHumanTtl(zombieId, zombieXNext, zombieYNext);

            // --- Compute proximity of zombie to Ash ---
            checkKillOptions(zombieXNext, zombieYNext);

            // --- Compute proximity of zombie to current Ash ---
            checkClosestZombie(zombieId, zombieXNext, zombieYNext);
        }
        for (int i = 0; i < zombies.length; i++) {
            if (!ids.contains(i)) {
                zombies[i] = null;
            }
        }
    }

    static void analyzeHumanTtl(int zombieId, int zombieXNext, int zombieYNext) {
        for (Human human: humans) {
            if (human == null) continue;
            int maxTurnsToLive = (int) ceil(Math.hypot(human.x - zombieXNext, human.y - zombieYNext) / 400);
            if (maxTurnsToLive < human.turnsToLive) {
                human.turnsToLive = maxTurnsToLive;
                human.killerId = zombieId;
                if (maxTurnsToLive < lowestTimeToLive && (hypot(zombieXNext - x, zombieYNext - y) - 2000)/1000  < maxTurnsToLive + 1) {
                    lowestTimeToLive = maxTurnsToLive;
                    bestZombieKill = zombieId;
                }
            }
        }
    }

    static void checkKillOptions(int zombieXNext, int zombieYNext) {
        for (int j = 0; j < 8; j++) {
            if (xOptions[j] != -1) {
                if (hypot(xOptions[j] - zombieXNext, yOptions[j] - zombieYNext) < 2000) {
                    killCounter[j] ++;
                }
            }
        }
    }

    static void checkClosestZombie(int zombieId, int zombieXNext, int zombieYNext) {
        double zombieDistance = hypot(zombieXNext - x, zombieYNext - y);
        if (zombieDistance < closestZombieDistance) {
            closestZombieDistance = zombieDistance;
            closestZombie = zombieId;
        }
    }

    static void resetEnv() {
        killCounter = new int[8];
        lowestTimeToLive = 46;
        bestZombieKill = -1;
        closestZombieDistance = 18358; // max distance is 18357.55975068582
    }

    static class Human {
        int x;
        int y;
        int turnsToLive = 46; // = ceil(hypot(16000, 9000)/400)
        int killerId;

        Human(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Zombie {
        int x;
        int y;
        int nextX;
        int nextY;

        Zombie(int x, int y, int nextX, int nextY) {
            this.x = x;
            this.y = y;
            this.nextX = nextX;
            this.nextY = nextY;
        }

        void update(int x, int y, int nextX, int nextY) {
            this.x = x;
            this.y = y;
            this.nextX = nextX;
            this.nextY = nextY;
        }
    }
}
