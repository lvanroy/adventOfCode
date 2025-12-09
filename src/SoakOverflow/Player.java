package SoakOverflow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.String.format;

class Player {
    // Use a buffered reader instead of the scanner, much more performant!
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    static int myId;
    static int width;
    static int height;

    static Tile[][] tiles;
    static double[][] threats;
    static Map<Integer, Agent> agents = new HashMap<>();
    static List<Agent> friends = new ArrayList<>();
    static List<Agent> foes = new ArrayList<>();

    static String decideMove(Agent agent) {
        int bestX = agent.x;
        int bestY = agent.y;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int newX = agent.x + dx;
                int newY = agent.y + dy;
                if (outOfBounds(newX, newY)) continue;
                if (tiles[newX][newY].type > 0) continue;
                if (tiles[newX][newY].agent != null) continue;

                double score = 0;

                // We want to avoid threats so inverse the threat (potential damage) value
                score -= 0.05 * threats[newX][newY];

                // We want to reward approaching enemies
                score -= tiles[newX][newY].enemyProximity;

                // We want to maximize gained tiles
                int gain = 0;
                for (int x = 0; x < width; x ++) {
                    for (int y = 0; y < height; y++) {
                        int dist = abs(x - newX) + abs(y - newY);
                        if (agent.wetness > 50) dist *= 2;
                        if (tiles[x][y].enemyProximity < tiles[x][y].playerProximity && tiles[x][y].enemyProximity > dist) {
                            gain += 1;
                        }
                    }
                }
                score += gain;

                // Keep track of the highest score and base move of of this
                if (score > bestScore) {
                    bestScore = score;
                    bestX = newX;
                    bestY = newY;
                }

                System.err.println(format("ag: %s, cor: (%s %s) threat: -%s, prox: -%s, gain: +%s", agent.id, newX, newY, threats[newX][newY], tiles[newX][newY].enemyProximity, gain));
            }
        }
        tiles[agent.x][agent.y].agent = null;
        tiles[bestX][bestY].agent = agent;
        return format("MOVE %s %s", bestX, bestY);
    }

    static String decideCombat(Agent agent) {
        // Check if we can shoot at all
        if (agent.curCooldown > 0 && agent.curBombs == 0) return "";

        double bestScore = -1;
        int target = -1;
        int bestX = -1;
        int bestY = -1;
        String bestAction = "";
        // try shooting
        if (agent.curCooldown == 0) {

            for (Agent foe: foes) {
                // Check if we can reach the foe
                int dist = abs(agent.x - foe.x) + abs(agent.y - foe.y);
                if (dist > agent.range) continue;

                // Apply cover
                int cover = getCoverType(agent.x, agent.y, foe.x, foe.y);
                double dmg = agent.power;
                if (cover == 1) dmg *= 0.5;
                if (cover == 2) dmg *= 0.25;

                // Store best target
                if (dmg > bestScore) {
                    bestScore = dmg;
                    target = foe.id;
                    bestAction = "SHOOT";
                }
            }
        }

        // Try bombing
        if (agent.curBombs > 0) {

            for (int dx = -4; dx < 4; dx ++) {
                for (int dy = -4 + abs(dx); dy <= 4 - abs(dx); dy++) {
                    int x = agent.x + dx;
                    int y = agent.y + dy;
                    if (outOfBounds(x, y)) continue;
                    int dmg = 0;
                    int targets = 0;
                    for (Agent foe: foes) {
                        int dist = abs(foe.x - x) + abs(foe.y - y);
                        if (dist <= 1) {
                            dmg += 30;
                            targets += 1;
                        }
                    }
                    for (Agent friend: friends) {
                        int dist = abs(friend.x - x) + abs(friend.y - y);
                        if (dist <= 1) {
                            dmg -= 50;
                        }
                    }

                    // Store best target
                    if (dmg > bestScore && targets >= 1) {
                        bestScore = dmg;
                        bestX = agent.x + dx;
                        bestY = agent.y + dy;
                        bestAction = "THROW";
                    }
                }
            }
        }

        // Return optimal action
        return switch (bestAction) {
            case "SHOOT" -> format(";SHOOT %s", target);
            case "THROW" -> format(";THROW %s %s", bestX, bestY);
            default -> "";
        };
    }

    static void updateThreatMap() {
        for (Agent foe: foes) {
            int range = 2 * foe.range; // TODO should this be tuned or is the 2 correct?

            for (int dx = -range; dx <= range; dx++) {
                for (int dy = -range; dy <= range; dy++) {
                    int newX = foe.x + dx;
                    int newY = foe.y + dy;

                    if (outOfBounds(newX, newY)) continue;
                    int distance = abs(dx) + abs(dy);
                    if (distance > foe.range) continue;

                    int cover = getCoverType(newX, newY, foe.x, foe.y);
                    double dmg = (distance <= foe.range ? foe.power : foe.power * 0.5);
                    if (cover == 1) dmg *= 0.5;
                    if (cover == 2) dmg *= 0.25;

                    threats[newX][newY] += dmg;
                }
            }

            tiles[foe.x][foe.y].agent = foe;
        }

        for (Agent friend: friends) {
            tiles[friend.x][friend.y].agent = friend;
        }
    }

    static void updateProximity() {
        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y].enemyProximity = Integer.MAX_VALUE;
                tiles[x][y].agent = null;
                for (Agent foe: foes) {
                    int dist = abs(foe.x - x) + abs(foe.y - y);
                    if (dist < tiles[x][y].enemyProximity) {
                        tiles[x][y].enemyProximity = dist;
                    }
                }
                for (Agent friend: friends) {
                    int dist = abs(friend.x - x) + abs(friend.y - y);
                    if (dist < tiles[x][y].enemyProximity) {
                        tiles[x][y].playerProximity = dist;
                    }
                }
            }
        }
    }

    private static boolean outOfBounds(int x, int y) {
        return x < 0 || x >= width || y < 0 || y >= height;
    }

    private static int getCoverType(int x, int y, int x2, int y2) {
        int coverType = 0;
        int dx = x - x2; // > 0 => enemy agent is to the left
        int dy = y - y2; // > 0 => enemy agent is above you

        if (dx > 1 && x > 2) {
            // Enemy to the left
            boolean enemyAdjacent = x2 == x - 2 && abs(dy) <= 1;
            if (!enemyAdjacent) {
                int potentialCover = tiles[x-1][y].type;
                coverType = max(coverType, potentialCover);
            }
        } else if (dx < -1 && x < width - 2) {
            // Enemy to the right
            boolean enemyAdjacent = x2 == x + 2 && abs(dy) <= 1;
            if (!enemyAdjacent) {
                int potentialCover = tiles[x+1][y].type;
                coverType = max(coverType, potentialCover);
            }
        }
        if (dy > 1 && y > 2) {
            // Enemy is above
            boolean enemyAdjacent = y2 == y - 2 && abs(dx) <= 1;
            if (!enemyAdjacent) {
                int potentialCover = tiles[x][y-1].type;
                coverType = max(coverType, potentialCover);
            }
        } else if (dy < -1 && y < height - 2) {
            // Enemy is below
            boolean enemyAdjacent = y2 == y + 2 && abs(dx) <= 1;
            if (!enemyAdjacent) {
                int potentialCover = tiles[x][y+1].type;
                coverType = max(coverType, potentialCover);
            }
        }

        return coverType;
    }

    private static void readInitialInput() throws Exception {
        // Read out agent data
        myId = parseInt(br.readLine()); // Your player id (0 or 1)
        int agentDataCount = parseInt(br.readLine()); // Total number of agents in the game
        for (int i = 0; i < agentDataCount; i++) {
            Agent agent = Agent.initAgent();
            agents.put(agent.id, agent);
        }

        // Read out grid size
        String[] input = br.readLine().split("\\s");
        width = parseInt(input[0]); // Width of the game map
        height = parseInt(input[1]); // Height of the game map
        tiles = new Tile[width][height];
        threats = new double[width][height];

        // Read out tile data
        for (int i = 0; i < height; i++) {
            input = br.readLine().split("\\s");
            for (int j = 0; j < width; j++) {
                int x = parseInt(input[j * 3]); // X coordinate, 0 is left edge
                int y = parseInt(input[1 + j * 3]); // Y coordinate, 0 is top edge
                int tileType = parseInt(input[2 + j * 3]); // obstacle height: O, 1 or 2
                tiles[x][y] = new Tile(x, y, tileType);
            }
        }
    }

    private static void readGameLoopInput() throws Exception {
        // Reset the game state to prevent counting agents that died
        friends.clear();
        foes.clear();
        for (double[] col: threats) {
            Arrays.fill(col, 0);
        }

        int agentCount = parseInt(br.readLine());
        for (int i = 0; i < agentCount; i++) {
            String[] input = br.readLine().split("\\s");
            int id = parseInt(input[0]);
            int x = parseInt(input[1]);
            int y = parseInt(input[2]);
            int curCooldown = parseInt(input[3]);
            int curBombs = parseInt(input[4]);
            int wetness = parseInt(input[5]);

            Agent agent = agents.get(id);
            agent.update(x, y, curCooldown, curBombs, wetness);

            if (agent.playerId == myId) {
                friends.add(agent);
            } else {
                foes.add(agent);
            }
        }
        int myAgents = parseInt(br.readLine());
    }

    public static void main(String[] args) throws Exception {
        readInitialInput();

        // Game loop
        while (true) {
            readGameLoopInput();
            updateProximity();
            updateThreatMap();

            for (Agent a : friends) {
                System.out.printf("%s;%s%s%n", a.id, decideMove(a), decideCombat(a));
            }
        }
    }

    static class Agent {
        int id, playerId;
        int cooldown, range, power, bombs;
        int x, y;
        int curCooldown, curBombs, wetness;

        Agent(int id, int playerId, int cooldown, int range, int power, int bombs) {
            this.id = id;
            this.playerId = playerId;
            this.cooldown = cooldown;
            this.range = range;
            this.power = power;
            this.bombs = bombs;
        }

        public static Agent initAgent() throws IOException {
            String[] input = br.readLine().split("\\s");
            int agentId = parseInt(input[0]); // Unique identifier for this agent
            int player = parseInt(input[1]); // Player id of this agent
            int cooldown = parseInt(input[2]); // Number of turns between each of this agent's shots
            int range = parseInt(input[3]); // Maximum manhattan distance for greatest damage output
            int power = parseInt(input[4]); // Damage output within optimal conditions
            int bombs = parseInt(input[5]); // Number of splash bombs this can throw this game
            return new Agent(agentId, player, cooldown, range, power, bombs);
        }

        public void update(int x, int y, int curCooldown, int curBombs, int wetness) {
            this.x = x;
            this.y = y;
            this.curCooldown = curCooldown;
            this.curBombs = curBombs;
            this.wetness = wetness;
        }
    }

    static class Tile {
        int x, y;
        int type;
        int enemyProximity;
        int playerProximity;
        Agent agent;

        public Tile(int x, int y, int type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }
    }
}
