package main.world;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Transformations {

    public static double[] rotate(double[] v, double yaw, double pitch, double roll) {
        double[] w = rotateAroundX(v, pitch);
        w = rotateAroundY(w, yaw);
        w = rotateAroundZ(w, roll);
        return w;
    }

    public static int[] project2d(int[] v) {
        return new int[]{v[0], v[1]};
    }

    private static double[] rotateAroundX(double[] v, double pitch) {
        double[][] a = new double[][]{

                {1,          0,             0},
                {0, cos(pitch),   -sin(pitch)},
                {0, sin(pitch),    cos(pitch)}};

        double[] w = new double[3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                w[row] += a[row][col] * v[col];
            }
        }

        return w;
    }

    private static double[] rotateAroundY(double[] v, double yaw) {
        double[][] a = new double[][]{

                {cos(yaw),  0,       sin(yaw)},
                {0         ,  1,                0},
                {-sin(yaw), 0,       cos(yaw)}};

        double[] w = new double[3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                w[row] += a[row][col] * v[col];
            }
        }

        return w;
    }

    private static double[] rotateAroundZ(double[] v, double roll) {
        double[][] a = new double[][]{

                {cos(roll), -sin(roll), 0},
                {sin(roll), cos(roll),  0},
                {0,       0,              1}};

        double[] w = new double[3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                w[row] += a[row][col] * v[col];
            }
        }

        return w;
    }
}
