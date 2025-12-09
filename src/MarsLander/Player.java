package MarsLander;

import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.asin;
import static java.lang.Math.hypot;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    static final double MAX_VERTICAL_SPEED = 40.0;
    static final double MAX_HORIZONTAL_SPEED = 20.0;
    static int startFlat = -1;
    static int endFlat = -1;
    static int landingHeight = -1;

    static int x;
    static int y;
    static int vx;
    static int vy;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int surfaceN = in.nextInt(); // the number of points used to draw the surface of Mars.
        int lastX = -1;
        int lastY = -1;
        for (int i = 0; i < surfaceN; i++) {
            int landX = in.nextInt(); // X coordinate of a surface point. (0 to 6999)
            int landY = in.nextInt(); // Y coordinate of a surface point. By linking all the points together in a sequential fashion, you form the surface of Mars.
            if (lastY == landY) {
                startFlat = lastX;
                endFlat = landX;
                landingHeight = landY;
            }
            lastX = landX;
            lastY = landY;
        }

        System.err.println(startFlat);
        System.err.println(endFlat);

        boolean overFlat = false;
        List<Integer> rotations = new ArrayList<>();

        // game loop
        while (true) {
            x = in.nextInt();
            y = in.nextInt();
            vx = in.nextInt(); // the horizontal speed (in m/s), can be negative.
            vy = in.nextInt(); // the vertical speed (in m/s), can be negative.
            int fuel = in.nextInt(); // the quantity of remaining fuel in liters.
            int rotate = in.nextInt(); // the rotation angle in degrees (-90 to 90).
            int power = in.nextInt(); // the thrust power (0 to 4).

            // For a thrust power of X, a push force equivalent to X m/s² is generated and X liters of fuel are consumed. Gravity on Mars is 3.711 m/s²
            // power delta max 1

            // land on flat ground
            // land in a vertical position (tilt angle = 0°)
            // vertical speed must be limited ( ≤ 40m/s in absolute value)
            // horizontal speed must be limited ( ≤ 20m/s in absolute value)
            update();
        }
    }

    public static void update() {
        boolean aboveLandingZone = x >= startFlat && x <= endFlat;
        double targetX = (endFlat + startFlat) / 2.0;
        double dx = targetX - x;

        int targetAngle;
        int power;

        // --- angle ---
        // If not over zone, steer horizontally
        if (!aboveLandingZone || abs(dx) > 300) {
            targetAngle = (int) Math.round(clamp(-dx * 0.03, -45, 45));
        } else {
            targetAngle = 0;
        }

        // --- power ---
        boolean braking = abs(vx) > MAX_HORIZONTAL_SPEED || vy < -MAX_VERTICAL_SPEED;
        power = braking ? 4 : (vy < -20 ? 3 : 2);
        System.out.printf("%d %d%n", targetAngle, power);
    }

    public static void updateOld() {
        boolean aboveLandingZone = x >= startFlat && x <= endFlat;

        int rotate = 0;
        if (!aboveLandingZone) {
            rotate = x < startFlat ? -30: 30;
        } else if (vx != 0) {
            rotate = vx > 0 ? 20 : -20;
        }

        int suggestedPower = 4;
        if (aboveLandingZone) {
            if (abs(vy) >= 39) {
                suggestedPower = 4;
            } else if (abs(vy) >= 30) {
                suggestedPower = 3;
            } else if (abs(vy) >= 20) {
                suggestedPower = 2;
            } else if (abs(vy) >= 10) {
                suggestedPower = 1;
            } else {
                suggestedPower = 0;
            }
        }

        // 2 integers: rotate power. rotate is the desired rotation angle (should be 0 for level 1), power is the desired thrust power (0 to 4).
        System.out.printf("%d %d%n", rotate, suggestedPower);
    }

    private static void updateNew() {
        boolean overLandingSpot = startFlat <= x && x <= endFlat;
        double speed = hypot(vx, vy);
        int angle = (int) toDegrees(asin(vx/speed));
    }

    private static double clamp(double value, double minVal, double maxVal) {
        return max(minVal, min(maxVal, value));
    }
}