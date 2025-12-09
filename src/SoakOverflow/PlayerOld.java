package SoakOverflow;

import java.util.*;
import java.io.*;
import java.util.Objects;

import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.String.format;

/**
 * Win the water fight by controlling the most territory, or out-soak your opponent!
 **/
class PlayerOld {

    static int myId;
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    static int width;
    static int height;

    static final List<Integer> friends = new ArrayList<>();
    static final List<Integer> enemies = new ArrayList<>();
    static final Map<Integer, Map<Integer, Tile>> tiles = new HashMap<>();
    static final Map<Integer, Agent> agents = new HashMap<>();
    static final int[][] moves = new int[5][2];

    static {
        moves[0] = new int[]{0, 0};
        moves[1] = new int[]{1, 0};
        moves[2] = new int[]{0, 1};
        moves[3] = new int[]{-1, 0};
        moves[4] = new int[]{0, -1};
    }

    // ~~~~ PARSE ALL INPUT ~~~~
    private static void readInitialInput() throws IOException {
        String[] input;

        // Read out agent data
        myId = parseInt(br.readLine()); // Your player id (0 or 1)
        int agentDataCount = parseInt(br.readLine()); // Total number of agents in the game
        for (int i = 0; i < agentDataCount; i++) {
            input = br.readLine().split("\\s");
            int agentId = parseInt(input[0]); // Unique identifier for this agent
            int player = parseInt(input[1]); // Player id of this agent
            int shootCooldown = parseInt(input[2]); // Number of turns between each of this agent's shots
            int optimalRange = parseInt(input[3]); // Maximum manhattan distance for greatest damage output
            int soakingPower = parseInt(input[4]); // Damage output within optimal conditions
            int splashBombs = parseInt(input[5]); // Number of splash bombs this can throw this game
            agents.put(agentId, new Agent(player, shootCooldown, optimalRange, soakingPower, splashBombs));
            if (player == myId) {
                friends.add(agentId);
            } else {
                enemies.add(agentId);
            }
        }

        // Read out tile data
        input = br.readLine().split("\\s");
        width = parseInt(input[0]); // Width of the game map
        height = parseInt(input[1]); // Height of the game map

        // Initialize the tile storage
        for (int i = 0; i < width; i++) {
            tiles.put(i, new HashMap<>());
        }

        // Read out all tile information
        for (int i = 0; i < height; i++) {
            input = br.readLine().split("\\s");
            for (int j = 0; j < width; j++) {
                int x = parseInt(input[0 + j * 3]); // X coordinate, 0 is left edge
                int y = parseInt(input[1 + j * 3]); // Y coordinate, 0 is top edge
                int tileType = parseInt(input[2 + j * 3]);
                tiles.get(x).put(y, new Tile(x, y, tileType));
            }
        }
    }

    private static void parseAgentData() throws IOException {
        List<Integer> oldIds = new ArrayList<>(friends);
        oldIds.addAll(enemies);
        friends.clear();
        enemies.clear();
        int agentCount = parseInt(br.readLine());
        for (int i = 0; i < agentCount; i++) {
            String[] input = br.readLine().split("\\s");
            int id = parseInt(input[0]);
            Agent agent = agents.get(id);
            Tile tile = tiles.get(agent.x).get(agent.y);
            if (tile.agent == agent) {
                tile.agent = null;
            }
            if (agent.playerId == myId) {
                friends.add(id);
                oldIds.remove((Integer) id);
            } else {
                enemies.add(id);
                oldIds.remove((Integer) id);
            }
            agents.get(id).update(parseInt(input[1]), parseInt(input[2]), parseInt(input[3]), parseInt(input[4]), parseInt(input[5]));
            tiles.get(agent.x).get(agent.y).agent = agent;
        }
        for (int id: oldIds) {
            Agent agent = agents.remove(id);
            if (tiles.get(agent.x).get(agent.y).agent == agent) {
                tiles.get(agent.x).get(agent.y).agent = null;
            }
        }
    }


    private static void move() throws IOException {
        parseAgentData();
        br.readLine();
        for (int id: friends) {
            Agent playerAgent = agents.get(id);
            int[] bestMove = computeOptimalMove(playerAgent);
            String bestCombatAction = computeOptimalCombat(playerAgent, bestMove);
            if (bestCombatAction.isEmpty()) {
                System.out.printf("%s;MOVE %s %s;MESSAGE moving to %s %s%n", id, bestMove[0], bestMove[1], bestMove[0], bestMove[1]);
            } else if (bestCombatAction.contains("MOVE")) {
                System.out.printf("%s;%s;MESSAGE %s%n", id, bestCombatAction, bestCombatAction);
            } else {
                System.out.printf("%s;MOVE %s %s;%s;MESSAGE moving to %s %s then %s%n", id, bestMove[0], bestMove[1], bestCombatAction, bestMove[0], bestMove[1], bestCombatAction);
            }
        }
    }

    static int[] computeOptimalMove(Agent playerAgent) {
        double damageReceived = Double.MAX_VALUE;
        int bestX = -1;
        int bestY = -1;
        for (int[] move: moves) {
            int newX = playerAgent.x + move[0];
            int newY = playerAgent.y + move[1];
            if (newX < 0 || newY < 0) {
                continue;
            }
            double newDamage = computeExpectedDamage(playerAgent.x + move[0], playerAgent.y + move[1]);
            if (newDamage < damageReceived) {
                damageReceived = newDamage;
                bestX = newX;
                bestY = newY;
            }
        }
        return new int[] {bestX, bestY};
    }

    static String computeOptimalCombat(Agent playerAgent, int[] bestMove) {
        // TODO compute bomb locations, walk to bomb locations, blow shit up
        double[] shootingResult = computeOptimalShootingDamage(playerAgent, bestMove);
//        if (playerAgent.curBombs > 0) {
//            int[] target = findOptimalBombSpot();
//            if (manhattanDistance(bestMove[0], bestMove[1], target[0], target[1]) > 4) {
//                return format("MOVE %s %s", target[0], target[1]);
//            } else {
//                return format("THROW %s %s", target[0], target[1]);
//            }
//        }
        if (shootingResult[0] > 0) {
            return format("SHOOT %s", (int) shootingResult[1]);
        }
        return "";
    }

    static double[] computeOptimalShootingDamage(Agent playerAgent, int[] newPosition) {
        double maxDamageDealt = 0;
        int bestShootId = -1;
        for (int enemyId: enemies) {
            Agent enemyAgent = agents.get(enemyId);
            // Apply damage loss due to cover
            int cover = getCoverType(enemyAgent.x, enemyAgent.y, newPosition[0], newPosition[1]);
            double damage = applyCover(cover, playerAgent.power);

            // Apply damage loss due to distance
            int distance = manhattanDistance(playerAgent.x, playerAgent.y, enemyAgent.x, enemyAgent.y);
            if (distance > playerAgent.optRange) {
//                damage *= 0.005;
                continue;
            }

            // See if we can damage this enemy most
            if (damage > maxDamageDealt) {
                maxDamageDealt = damage;
                bestShootId = enemyId;
            }
        }
        return new double[]{maxDamageDealt, bestShootId};
    }

    static int[] findOptimalBombSpot() {
        int maxTargets = -1;
        int bestX = -1;
        int bestY = -1;
        for (int x = 1; x < width; x ++) {
            for (int y = 1; y < height - 1; y ++) {
                int targets = 0;
                if (tiles.get(x - 1).get(y - 1).agent != null) {
                    if (tiles.get(x - 1).get(y - 1).agent.playerId == myId) {
                        targets -= 5;
                    }
                    targets++;
                }
                if (tiles.get(x - 1).get(y).agent != null) {
                    if (tiles.get(x - 1).get(y).agent.playerId == myId) {
                        targets -= 5;
                    }
                    targets++;
                }
                if (tiles.get(x - 1).get(y + 1).agent != null) {
                    if (tiles.get(x - 1).get(y + 1).agent.playerId == myId) {
                        targets -= 5;
                    }
                    targets++;
                }
                if (tiles.get(x).get(y - 1).agent != null) {
                    if (tiles.get(x).get(y - 1).agent.playerId == myId) {
                        targets -= 5;
                    }
                    targets++;
                }
                if (tiles.get(x).get(y).agent != null) {
                    if (tiles.get(x).get(y).agent.playerId == myId) {
                        targets -= 5;
                    }
                    targets++;
                }
                if (tiles.get(x).get(y + 1).agent != null) {
                    if (tiles.get(x).get(y + 1).agent.playerId == myId) {
                        targets -= 5;
                    }
                    targets++;
                }
                if (tiles.get(x + 1).get(y - 1).agent != null) {
                    if (tiles.get(x + 1).get(y - 1).agent.playerId == myId) {
                        targets -= 5;
                    }
                    targets++;
                }
                if (tiles.get(x + 1).get(y).agent != null) {
                    if (tiles.get(x + 1).get(y).agent.playerId == myId) {
                        targets -= 5;
                    }
                    targets++;
                }
                if (tiles.get(x + 1).get(y + 1).agent != null) {
                    if (tiles.get(x + 1).get(y + 1).agent.playerId == myId) {
                        targets -= 5;
                    }
                    targets++;
                }
                if (targets > maxTargets) {
                    maxTargets = targets;
                    bestX = x;
                    bestY = y;
                }
            }
        }
        return new int[]{bestX, bestY};
    }

    static double[] computeOptimalBombDamage(int[] newPosition) {
        int optimalDamage = 0;
        int optimalX = -1;
        int optimalY = -1;
        for (int x = 0; x < 4; x ++) {
            for (int y = 0; y <= 4 - x; y ++) {
                int damage = 0;
                for (int[] move: moves) {
                    Agent agent = tiles.get(newPosition[0] + move[0]).get(newPosition[1] + move[1]).agent;
                    if (agent != null) {
                        if (agent.playerId == myId) {
                            damage -= 100;
                        } else {
                            damage += 30;
                        }
                    }
                }
                if (damage > optimalDamage) {
                    optimalDamage = damage;
                    optimalX = x;
                    optimalY = y;
                }
            }
        }
        return new double[]{optimalDamage, optimalX, optimalY};
    }

    public static int manhattanDistance(int x1, int y1, int x2, int y2) {
        return abs(x1 - x2) + abs(y1 - y2);
    }

    static double computeExpectedDamage(int targetX, int targetY) {
        double expectedDamage = 0;
        for (int enemyId: enemies) {
            Agent enemy = agents.get(enemyId);

            // Apply damage loss due to cover
            int cover = getCoverType(targetX, targetY, enemy.x, enemy.y);
            double damage = applyCover(cover, enemy.power);

            // Apply damage loss due to distance
            int distance = manhattanDistance(targetX, targetY, enemy.x, enemy.y);
            if (distance > enemy.optRange) {
                damage *= 0.5;
            }

            expectedDamage += damage;
        }
        return expectedDamage;
    }

    static double applyCover(int cover, int damage) {
        if (cover == 0) {
            return damage;
        } else if (cover == 1) {
            return damage * 0.5;
        } else {
            return damage * 0.25;
        }
    }

    static int getCoverType(int x, int y, int ex, int ey) {
        int coverType = 0;
        int dx = x - ex; // > 0 => enemy agent is to the left
        int dy = y - ey; // > 0 => enemy agent is above you

        if (dx > 1 && x > 2) {
            // Enemy to the left
            boolean enemyAdjacent = ex == x - 2 && abs(dy) <= 1;
            if (!enemyAdjacent) {
                int potentialCover = tiles.get(x - 1).get(y).tileType;
                coverType = max(coverType, potentialCover);
            }
        } else if (dx < -1 && x < width - 2) {
            // Enemy to the right
            boolean enemyAdjacent = ex == x + 2 && abs(dy) <= 1;
            if (!enemyAdjacent) {
                int potentialCover = tiles.get(x + 1).get(y).tileType;
                coverType = max(coverType, potentialCover);
            }
        }
        if (dy > 1 && y > 2) {
            // Enemy is above
            boolean enemyAdjacent = ey == y - 2 && abs(dx) <= 1;
            if (!enemyAdjacent) {
                int potentialCover = tiles.get(x).get(y - 1).tileType;
                coverType = max(coverType, potentialCover);
            }
        } else if (dy < -1 && y < height - 2) {
            // Enemy is below
            boolean enemyAdjacent = ey == y + 2 && abs(dx) <= 1;
            if (!enemyAdjacent) {
                int potentialCover = tiles.get(x).get(y + 1).tileType;
                coverType = max(coverType, potentialCover);
            }
        }

        System.err.printf("P: (%s, %s) E: (%s, %s) D: (%s, %s) Cover: %s%n", x, y, ex, ey, dx, dy, coverType);
        return coverType;
    }

    static class Tile {
        int x;
        int y;
        int tileType;
        Agent agent;

        public Tile(int x, int y, int tileType) {
            this.x = x;
            this.y = y;
            this.tileType = tileType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tile tile = (Tile) o;
            return x == tile.x && y == tile.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, tileType);
        }
    }

    static class Agent {
        // static
        int playerId;
        int cooldown;
        int optRange;
        int power;
        int bombs;

        // dynamic
        int x;
        int y;
        int curCooldown;
        int curBombs;
        int curWetness;

        Agent(int playerId, int cooldown, int optRange, int power, int bombs) {
            this.playerId = playerId;
            this.cooldown = cooldown;
            this.optRange = optRange;
            this.power = power;
            this.bombs = bombs;
        }

        public void update(int x, int y, int curCooldown, int curBombs, int curWetness) {
            this.x = x;
            this.y = y;
            this.curCooldown = curCooldown;
            this.curBombs = curBombs;
            this.curWetness = curWetness;
        }

        @Override
        public int hashCode() {
            return Objects.hash(cooldown, optRange, power, bombs, x, y, curCooldown, curBombs, curWetness);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Agent agent = (Agent) o;
            return x == agent.x && y == agent.y;
        }
    }

    public static void main(String args[]) throws IOException {
        readInitialInput();

        // game loop
        while (true) {
            move();
        }
    }
}
