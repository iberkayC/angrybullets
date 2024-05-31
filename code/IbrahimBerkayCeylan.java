/**
 * For CMPE160 Object-Oriented Programming course in Bogazici University.
 * Simple game that simulates shooting bullets, like Angry Birds.
 * @author Ibrahim Berkay Ceylan, Student ID: 2023400327
 * @since Date: 15.03.2024
 */

import java.awt.event.KeyEvent;

public class IbrahimBerkayCeylan {
    /**
     * Main method that runs the Angry Birds like game.
     * @param args Main input arguments are not used.
     */
    public static void main(String[] args) {
        // Game Parameters
        int width = 1600; //screen width
        int height = 800; // screen height
        double gravity = 9.80665; // gravity
        double x0 = 120; // x and y coordinates of the bullet’s starting position on the platform
        double y0 = 120;
        double bulletVelocity = 180; // initial velocity
        double bulletAngle = 45.0; // initial angle
        // Box coordinates for obstacles and targets
        // Each row stores a box containing the following information:
        // x and y coordinates of the lower left rectangle corner, width, and height
        double[][] obstacleArray = {
                {1200, 0, 60, 220},
                {1000, 0, 60, 160},
                {600, 0, 60, 80},
                {600, 180, 60, 160},
                {220, 0, 120, 180}
                // {800, 600, 60, 200}
        };
        double[][] targetArray = {
                {1160, 0, 30, 30},
                {730, 0, 30, 30},
                {150, 0, 20, 20},
                {1480, 0, 60, 60},
                {340, 80, 60, 30},
                {1500, 600, 60, 60}
                // {860, 670, 60, 60}
        };

        // Game variables
        boolean shooting = false; // shooting state
        double bulletAngleRadians = Math.toRadians(bulletAngle); // angle in radians
        double time = 0; // time variable for bullet animation
        int pauseDuration = 20; // pause duration for the game loop
        boolean bulletMoving = false; // bullet moving state
        double[][] trajectory = {{120, 120}}; // trajectory array

        // setting up the canvas and coordinates
        StdDraw.setCanvasSize(width, height); // setting canvas size
        StdDraw.setXscale(0, width); // setting x and y scales
        StdDraw.setYscale(0, height);
        StdDraw.enableDoubleBuffering(); // enabling double buffering for smooth animations


        // game loop
        while (true) {

            StdDraw.clear();

            // drawing shooting platform
            StdDraw.setPenColor(StdDraw.BLACK);
            drawRectangle(0, 0, 120, 120);

            // drawing obstacles
            StdDraw.setPenColor(StdDraw.DARK_GRAY);
            for(int i = 0; i < obstacleArray.length; i ++) {
                drawRectangle(obstacleArray[i][0], obstacleArray[i][1], obstacleArray[i][2], obstacleArray[i][3]);
            }

            // drawing targets
            StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
            for(int i = 0; i < targetArray.length; i++) {
                drawRectangle(targetArray[i][0], targetArray[i][1], targetArray[i][2], targetArray[i][3]);
            }

            // drawing player and states
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.005);
            double x_component = 120 + (bulletVelocity * Math.cos(bulletAngleRadians)) / 3.0;
            double y_component = 120 + (bulletVelocity * Math.sin(bulletAngleRadians)) / 3.0;
            StdDraw.line(120, 120, x_component, y_component);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.textLeft(30, 60, String.format("a: %d", (int)bulletAngle));
            StdDraw.textLeft(30, 40, String.format("v: %d", (int)bulletVelocity));

            if(!shooting) {
                // taking user inputs and changing player states
                if (StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
                    bulletAngle += 1;
                    bulletAngleRadians = Math.toRadians(bulletAngle);
                }

                if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {
                    bulletAngle -= 1;
                    bulletAngleRadians = Math.toRadians(bulletAngle);
                }

                if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
                    bulletVelocity += 1;
                }

                if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {
                    if (bulletVelocity > 1) {
                        bulletVelocity -= 1;
                    }
                }

                if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                    time = 0;
                    shooting = true;
                    bulletMoving = true;
                }
            }

            // shooting mechanic and bullet animation
            if (shooting) {

                // checking if the bullet is in bounds or hit an obstacle or target
                if(!xInBounds(x0)) {
                    writeRetry("Max X reached. Press 'r' to shoot again.");
                    bulletMoving = false;
                }

                if(!yInBounds(y0)) {
                    writeRetry("Hit the ground. Press 'r' to shoot again.");
                    bulletMoving = false;
                }

                if(checkCollision(x0, y0, obstacleArray)) {
                    writeRetry("Hit an obstacle. Press 'r' to shoot again.");
                    bulletMoving = false;
                }

                if(checkCollision(x0, y0, targetArray)) {
                    writeRetry("Congratulations: You hit the target!");
                    bulletMoving = false;
                }

                // bullet animation and trajectory calculation
                if(bulletMoving) {
                    time += pauseDuration / 1000.0; // time increment
                    double scaledVelocity = bulletVelocity * 0.055; // scaling the velocity for better gameplay
                    x0 = x0 + scaledVelocity * time * Math.cos(bulletAngleRadians); // bullet x and y coordinates
                    y0 = y0 + scaledVelocity * time * Math.sin(bulletAngleRadians) - 0.5 * gravity * time * time;
                    trajectory = appendToArray(trajectory, new double[]{x0, y0}); // appending the trajectory array
                } else {
                    // bullet stopped, wait for input to reset the bullet position and trajectory
                    if (StdDraw.isKeyPressed(KeyEvent.VK_R)) {
                        shooting = false;
                        x0 = 120;
                        y0 = 120;
                        trajectory = new double[][]{{120, 120}};
                        bulletVelocity = 180;
                        bulletAngle = 45;
                        bulletAngleRadians = Math.toRadians(bulletAngle);
                    }
                }

                // trajectory drawing
                StdDraw.setPenColor(StdDraw.BLACK);
                for(int i = 0; i < trajectory.length; i++) {
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.filledCircle(trajectory[i][0], trajectory[i][1], 4);
                }
                for(int i=0; i < trajectory.length - 1; i++) {
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.setPenRadius(0.002);
                    StdDraw.line(trajectory[i][0], trajectory[i][1], trajectory[i+1][0], trajectory[i+1][1]);
                }
            }

            StdDraw.show();
            StdDraw.pause(pauseDuration);
        }
    }

    /**
     * Draws a rectangle with the given parameters.
     * @param lcx x coordinate of the lower left corner
     * @param lcy y coordinate of the lower left corner
     * @param width width of the rectangle
     * @param height height of the rectangle
     */
    private static void drawRectangle(double lcx, double lcy, double width, double height) {
        double adjusted_x = lcx + width/2.0;
        double adjusted_y = lcy + height/2.0;
        double adjusted_height = height/2.0;
        double adjusted_width = width/2.0;
        StdDraw.filledRectangle(adjusted_x, adjusted_y, adjusted_width, adjusted_height);
    }

    /**
     * Checks if the x coordinate is in bounds.
     * @param x x coordinate
     * @return true if x is in bounds, false otherwise
     */
    private static boolean xInBounds(double x) {
        return ((x > 0) & (x < 1600));
    }

    /**
     * Checks if the y coordinate is in bounds.
     * @param y y coordinate
     * @return true if y is in bounds, false otherwise
     */
    private static boolean yInBounds(double y) {
        return (y > 0);
    }

    /**
     * Checks if the given x and y coordinates are in collision with any of the boxes.
     * @param x x coordinate
     * @param y y coordinate
     * @param boxArray array of boxes
     * @return true if there is a collision, false otherwise
     */
    private static boolean checkCollision(double x, double y, double[][] boxArray) {
        for(int i = 0; i < boxArray.length; i++) {
            if((((boxArray[i][0] + boxArray[i][2]) > x) && (x > boxArray[i][0])) &&
                    (((boxArray[i][1] + boxArray[i][3]) > y) && (y > boxArray[i][1]))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Writes the given text to the screen.
     * @param text text to be written
     */
    private static void writeRetry(String text) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.textLeft(15, 775, text);
    }

    /**
     * Appends the given element to the given array.
     * Since Java does not support dynamic arrays, this method is needed.
     * @param arr array to be appended
     * @param element element to be appended
     * @return new array with the element appended
     */
    private static double[][] appendToArray(double[][] arr, double[] element) {
        double[][] temp = new double[arr.length + 1][2];
        for(int i = 0; i < arr.length; i++) {
            temp[i][0] = arr[i][0];
            temp[i][1] = arr[i][1];
        }
        temp[arr.length] = element;
        return temp;
    }
}
