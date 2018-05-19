package main.world;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Matrix {

    public Matrix() {

    }

    public double[] rotate(double[] v, double yaw, double pitch, double roll) {
        double[] w = rotateAroundX(v, pitch);
        w = rotateAroundY(w, yaw);
        w = rotateAroundZ(w, roll);
        return w;
    }

    public int[] project2d(int[] v) {
        return new int[]{v[0], v[1]};
    }

    private double[] rotateAroundX(double[] v, double theta) {
        double[][] a = new double[][]{

                {1,          0,             0},
                {0, cos(theta),   -sin(theta)},
                {0, sin(theta),    cos(theta)}};

        double[] w = new double[3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                w[row] += a[row][col] * v[col];
            }
        }

        return w;
    }

    private double[] rotateAroundZ(double[] v, double theta) {
        double[][] a = new double[][]{

                {cos(theta), -sin(theta), 0},
                {sin(theta), cos(theta),  0},
                {0,       0,              1}};

        double[] w = new double[3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                w[row] += a[row][col] * v[col];
            }
        }

        return w;
    }

    private double[] rotateAroundY(double[] v, double theta) {
        double[][] a = new double[][]{

                {cos(theta),  0,       sin(theta)},
                {0         ,  1,                0},
                {-sin(theta), 0,       cos(theta)}};

        double[] w = new double[3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                w[row] += a[row][col] * v[col];
            }
        }

        return w;
    }
}
