package ru.spbstu.telematics.java;

public class Matrix {
    double[][] matrix;

    public Matrix(double[][] Matrix) {
        matrix = Matrix;
        this.normalise();
    }

    private void normalise() {
        int max = 0;
        for (double[] value : matrix) {
            if (value.length > max)
                max = value.length;
        }
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i].length < max) {
                double[] new_line = new double[max];
                for (int j = 0; j < matrix[i].length; j++) {
                    new_line[j] = matrix[i][j];
                }
                matrix[i] = new_line;
            }
        }
    }

    private Matrix minorMatrix(int i, int j) {
        Matrix res = new Matrix(new double[matrix.length - 1][matrix.length - 1]);
        for (int k = 0; i < matrix.length; i++) {
            for (int l = 0; j < matrix.length; j++) {
                int i1, j1;
                if (k < i)
                    i1 = k;
                else
                    i1 = k + 1;
                if (l < j)
                    j1 = l;
                else
                    j1 = l + 1;
                res.matrix[i1][j1] = matrix[k][l];
            }
        }
        return res;
    }

    public double determinant() {
        if (matrix.length != matrix[0].length)
            return 0;
        if (matrix.length == 1)
            return matrix[0][0];
        double res = 0;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[0][i] != 0)
                res += matrix[0][i] * minorMatrix(0, i).determinant();
        }
        return res;
    }

    @Override
    public String toString() {
        return matrix.toString();
    }
}
