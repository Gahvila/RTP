package biz.donvi.evenDistribution;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import static biz.donvi.evenDistribution.TransitionMatrices.*;
import static java.lang.Math.*;

public class RandomCords {

    private static final Random r = new Random();

    /**
     * Generates a pseudo random number using the {@code nextGaussian()} method in {@link Math}, centers it on
     * {@code center}, divides it by {@code shrink}, and makes sure that it is between {@code 0} and {@code 1}.
     *
     * @param shrink Number to divide the initial value by (before being subject to constraints)
     * @param center Number to center the values on. Must be between 0 and 1.
     * @return A random number, as described above.
     */
    static double randomG(double shrink, double center) {
        if (center < 0 || center > 1) throw new IllegalArgumentException("Center must be between 0 and 1 inclusive.");
        double randG;
        do randG = (r.nextGaussian() / shrink + center);
        while (randG < 0 || randG >= 1);
        return randG;
    }


    /**
     * Returns an int array of length 2, representing a random coordinate pair that is both inside the square
     * <pre>{@code x = radiusMax * 2 and y = radiusMax * 2 centered on (0,0)}</pre>
     * and also NOT inside the square
     * <pre>{@code x = radiusMin * 2 and y = radiusMin * 2 centered on (0,0)}</pre>
     * The points generated by this method are evenly distributed throughout the area.
     *
     * @param radiusMax The maximum that {@code |x|} or {@code |y|} will be.
     * @param radiusMin The minimum that {@code |x|} or {@code |y|} will be.
     * @return A pair of numbers, each randomly generated, that satisfy the above conditions.
     */
    public static int[] getRandXySquare(int radiusMax, int radiusMin) {
        double[] randPairAtSize = new double[]{
                random() * (radiusMax - radiusMin) + radiusMin,
                random() * (radiusMax + radiusMin) - radiusMin};
        double[] result = multiplyMatrixVector(
                ROTATIONS_0_90_180_270[(int) (random() * 4)],
                randPairAtSize);
        return new int[]{
                (int) result[0],
                (int) result[1]};
    }


    /**
     * Returns an int array of length 2, representing a random coordinate pair that is both inside the square
     * <pre>{@code x = radiusMax * 2 and y = radiusMax * 2 centered on (0,0)}</pre>
     * and also NOT inside the square
     * <pre>{@code x = radiusMin * 2 and y = radiusMin * 2 centered on (0,0)}</pre>
     * The points generated by this method will <em>not</em> be random. Unlike the other method, which uses
     * evenly distributed pseudo random numbers, this receives random numbers with a Gaussian distribution,
     * keeping the points concentrated around the radius {@code r} where {@code r} is
     * {@code (radiusMax - radiusMin) * gCenter + radiusMin}. {@code gShrink} is a lazily named variable
     * that is inversely related to the size of the distribution in the valid range.
     *
     * @param radiusMax The maximum that {@code |x|} or {@code |y|} will be.
     * @param radiusMin The minimum that {@code |x|} or {@code |y|} will be.
     * @param gShrink   The number to divide the initial value by.
     *                  It will make the cluster of points more dense around the center radius.
     * @param gCenter   Where to center the points between {@code radiusMin} and {@code radiusMax},
     *                  using {@code 0} and {@code 1} respectively.
     * @return
     */
    public static int[] getRandXySquare(int radiusMax, int radiusMin, double gShrink, double gCenter) {
        double s0 = (double) radiusMin / (double) radiusMax;
        double s1 = 1 - s0;
        double r0 = random();
        double r1 = randomG(gShrink, gCenter);
        r1 = r1 * s0 + sqrt(r1) * s1;
        double[] xy = {
                s0 + s1 * r1,
                s0 * r0 + r0 * r1 * s1};
        if (random() * 2 > 1) xy[1] *= -1;
        xy = multiplyMatrixVector(ROTATIONS_0_90_180_270[(int) (random() * 4)], xy);
        return new int[]{
                (int) (xy[0] * radiusMax),
                (int) (xy[1] * radiusMax)};

    }


    /**
     * Returns an int array of length 2, representing a random coordinate pair that is both
     * in a circle with the given radius radiusMax, and not in the circle with the given
     * radius radiusMin.
     * The coordinates generated are evenly distributed throughout the available area.
     *
     * @param radiusMax The maximum distance from the center of the circle that a point can be.
     * @param radiusMin The minimum distance from the center of the circle that a point can be.
     * @return A pair of numbers representing a random point inside the specific area in the circle.
     */
    public static int[] getRandXyCircle(int radiusMax, int radiusMin) {
        return getRandXyCircle(radiusMax, radiusMin, 0, 0);
    }


    /**
     * Returns an int array of length 2, representing a random coordinate pair that is both
     * in a circle with the given radius radiusMax, and not in the circle with the given
     * radius radiusMin.
     * The points generated by this method will <em>not</em> be random. Unlike the other method, which uses
     * evenly distributed pseudo random numbers, this receives random numbers with a Gaussian distribution,
     * keeping the points concentrated around the radius {@code r} where {@code r} is
     * {@code (radiusMax - radiusMin) * gCenter + radiusMin}. {@code gShrink} is a lazily named variable
     * that is inversely related to the size of the distribution in the valid range.
     *
     * @param radiusMax The maximum distance from the center of the circle that a point can be.
     * @param radiusMin The minimum distance from the center of the circle that a point can be.
     *                  It will make the cluster of points more dense around the center radius.
     * @param gCenter   Where to center the points between {@code radiusMin} and {@code radiusMax},
     *                  using {@code 0} and {@code 1} respectively.
     * @return A pair of numbers representing a random point inside the specific area in the circle.
     */
    public static int[] getRandXyCircle(int radiusMax, int radiusMin, double gShrink, double gCenter) {
        double a = pow((double) radiusMin / (double) radiusMax, 2d);
        double r = sqrt((gShrink == 0 ? random() : randomG(gShrink, gCenter)) * (1 - a) + a);
        double theta = 2d * PI * random();
        int x = (int) (r * cos(theta) * radiusMax);
        int y = (int) (r * sin(theta) * radiusMax);
        return new int[]{x, y};
    }

}