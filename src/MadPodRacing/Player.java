package MadPodRacing;

import java.util.*;
import java.io.*;
import java.math.*;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.clamp;
import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    static boolean boostAvailable = true;
    static int prevX = 0;
    static int prevY = 0;
    static int turn = 0;
    static int adjustment = 0; // TODO check what this does
    static int xAim = 0;
    static int yAim = 0;

    public static void main(String args[]) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] input = br.readLine().split("\\s");
        int x = Integer.parseInt(input[0]);
        int y = Integer.parseInt(input[1]);
        int nextCheckpointX = Integer.parseInt(input[2]);
        int nextCheckpointY = Integer.parseInt(input[3]);
        int nextCheckpointDist = Integer.parseInt(input[4]);
        int nextCheckpointAngle =Integer.parseInt(input[5]);
        input = br.readLine().split("\\s");
        int opponentX = Integer.parseInt(input[0]);
        int opponentY = Integer.parseInt(input[1]);
        prevX = x;
        prevY = y;
        while (true) {

            // --- compute velocity ---
            int vx = x - prevX;
            int vy = y - prevY;
            double speed = hypot(vx, vy);
            System.err.println("Speed is " + speed);

            // --- compute aim ---
            double leadFactor = 0.0;
            if (nextCheckpointDist > 4000) {
                double angleFactor = max(0.4, cos(toRadians(nextCheckpointAngle)));
                leadFactor = min(2, nextCheckpointDist / 3000.0 * angleFactor);
            }
            xAim = (int)(nextCheckpointX + vx * leadFactor);
            yAim = (int)(nextCheckpointY + vy * leadFactor);

            // --- Move state ---
            prevX = x;
            prevY = y;

            // --- Compute thrust ---
            double thrustFactor = cos(toRadians(nextCheckpointAngle));
            if (nextCheckpointDist < 1000) {
                thrustFactor *= nextCheckpointDist / 1000.0;
            }
            int thrust = (int) (100 * thrustFactor);
            thrust = clamp(thrust, 0, 100);

            // Account for the case where the angle is very bad and we are on the target
            if (nextCheckpointDist < 600 && Math.abs(nextCheckpointAngle) > 20) {
                thrust = 0;
            }

            // --- Check if we want to boost ---
            if (boostAvailable && abs(nextCheckpointAngle) <= 5 && nextCheckpointDist >= 6000) {
                boostAvailable = false;
                System.out.printf("%d %d BOOST%n", xAim, yAim);
            } else {
                System.out.printf("%d %d %d %d%n", xAim, yAim, thrust, thrust);
            }
            input = br.readLine().split("\\s");
            x = Integer.parseInt(input[0]);
            y = Integer.parseInt(input[1]);
            nextCheckpointX = Integer.parseInt(input[2]);
            nextCheckpointY = Integer.parseInt(input[3]);
            nextCheckpointDist = Integer.parseInt(input[4]);
            nextCheckpointAngle =Integer.parseInt(input[5]);
            input = br.readLine().split("\\s");
            opponentX = Integer.parseInt(input[0]);
            opponentY = Integer.parseInt(input[1]);
        }
    }
}