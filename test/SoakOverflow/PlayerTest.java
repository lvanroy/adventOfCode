package SoakOverflow;

import SoakOverflow.PlayerOld.Agent;
import SoakOverflow.PlayerOld.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static SoakOverflow.PlayerOld.agents;
import static SoakOverflow.PlayerOld.computeExpectedDamage;
import static SoakOverflow.PlayerOld.enemies;
import static SoakOverflow.PlayerOld.getCoverType;
import static SoakOverflow.PlayerOld.height;
import static SoakOverflow.PlayerOld.tiles;
import static SoakOverflow.PlayerOld.width;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {
    @BeforeEach
    void setup() {
        width = 13;
        height = 5;
        tiles.clear();
        agents.clear();
        enemies.clear();
        for (int i = 0; i < width; i++) {
            tiles.put(i, new HashMap<>());
        }
    }

    @Test
    void canGetCoverType() {
        // Enemy to the right of player
        tiles.get(1).put(0, new Tile(1, 0, 0));
        assertEquals(0, getCoverType(0, 0, 3, 0));

        tiles.get(1).put(0, new Tile(1, 0, 1));
        assertEquals(1, getCoverType(0, 0, 3, 0));

        // Enemy to the left of player
        tiles.get(2).put(0, new Tile(2, 0, 2));
        assertEquals(2, getCoverType(3, 0, 0, 0));

        // Enemy below player
        tiles.get(0).put(1, new Tile(0, 1, 1));
        assertEquals(1, getCoverType(0, 0, 0, 3));

        // Enemy above player
        tiles.get(0).put(2, new Tile(0, 2, 2));
        assertEquals(2, getCoverType(0, 3, 0, 0));
    }

    @Test
    void canDetectEnemyProximity() {
        // Enemy to the right of player
        tiles.get(1).put(0, new Tile(1, 0, 1));
        assertEquals(0, getCoverType(0, 0, 2, 0));

        // Enemy to the left of player
        tiles.get(2).put(0, new Tile(1, 0, 2));
        assertEquals(0, getCoverType(2, 0, 0, 0));

        // Enemy below player
        tiles.get(0).put(1, new Tile(0, 1, 1));
        assertEquals(0, getCoverType(0, 0, 0, 2));

        // Enemy above player
        tiles.get(0).put(2, new Tile(0, 1, 2));
        assertEquals(0, getCoverType(0, 2, 0, 0));
    }

    @Test
    void canComputeIncomingDamage() {
        // no cover, in range
        PlayerOld.enemies.add(1);
        agents.put(1, new Agent(0, 0, 2000, 10, 0));
        agents.get(1).update(4, 3, 0, 0, 0);
        tiles.get(1).put(3, new Tile(1, 3, 0));
        assertEquals(10, computeExpectedDamage(0, 3));

        // cover 1, in range
        agents.put(1, new Agent(0, 0, 2000, 10, 0));
        agents.get(1).update(4, 3, 0, 0, 0);
        tiles.get(1).put(3, new Tile(1, 3, 1));
        assertEquals(5, computeExpectedDamage(0, 3));

        // cover 2, in range
        agents.put(1, new Agent(0, 0, 2000, 10, 0));
        agents.get(1).update(4, 3, 0, 0, 0);
        tiles.get(1).put(3, new Tile(1, 3, 2));
        assertEquals(2.5, computeExpectedDamage(0, 3));

        // cover 0, out of range
        agents.put(1, new Agent(0, 0, 1, 10, 0));
        agents.get(1).update(4, 3, 0, 0, 0);
        tiles.get(1).put(3, new Tile(1, 3, 0));
        assertEquals(5, computeExpectedDamage(0, 3));

        // cover 1, out of range
        agents.put(1, new Agent(0, 0, 1, 10, 0));
        agents.get(1).update(4, 3, 0, 0, 0);
        tiles.get(1).put(3, new Tile(1, 3, 1));
        assertEquals(2.5, computeExpectedDamage(0, 3));

        // cover 2, out of range
        agents.put(1, new Agent(0, 0, 1, 10, 0));
        agents.get(1).update(4, 3, 0, 0, 0);
        tiles.get(1).put(3, new Tile(1, 3, 2));
        assertEquals(1.25, computeExpectedDamage(0, 3));
    }
}
