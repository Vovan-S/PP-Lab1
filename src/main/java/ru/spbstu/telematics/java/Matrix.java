package ru.spbstu.telematics.java;

import static java.lang.Math.abs;

/**
 * Класс, представляющий матрицу из действительных чисел double.
 * @author Вова
 * @version 1.0
 */

public class Matrix {
    /**
     * Двумерный массив, числа в матрице.
     * Первый индекс - номер строки,
     * Второй индекс - номер столбца.
     */
    double[][] matrix;

    /**
     * Количество знаков после десятичной точки при выводе матрицы на печать.
     * По умолчанию имеет значение 3.
     */
    static int outPrecision;

    /**
     * Инициализация поля outPrecision.
     * @see Matrix#outPrecision
     */
    static private void initOutPrecision() {
        if (outPrecision == 0)
            outPrecision = 3;
    }

    /**
     * Конструктор по умолчанию, создает матрицу [0]
     * @see Matrix#Matrix(double) 
     * @see Matrix#Matrix(double[][])
     */
    public Matrix() {
        matrix = new double[1][1];
        initOutPrecision();
    }

    /**
     * Конструктор, создающий матрица 1x1 [x]
     * @param x вещественное число, на основе которого создается матрица.
     * @see Matrix#Matrix() 
     * @see Matrix#Matrix(double[][]) 
     */
    public Matrix(double x) {
        matrix = new double[1][1];
        matrix[0][0] = x;
    }

    /**
     * Конструктор, создает матрицу на основе двумерного массива.
     * Если строки массива имееют разную длину, они дополняются
     * по самой длинной строке нулями при помощи метода {@link Matrix#normalise()}.
     * @param Matrix двумерный массив, на основе которого
     *               создается матрица.
     * @see Matrix#Matrix()
     * @see Matrix#Matrix(double) 
     * @see Matrix#normalise()
     */
    public Matrix(double[][] Matrix) {
        matrix = Matrix;
        this.normalise();
        initOutPrecision();
    }

    /**
     * Метод делает матрицу прямоугольной по самой длинной строке,
     * пустые поля заполняет нулями.
     */
    private void normalise() {
        int max = 0;
        for (double[] value : matrix) {
            if (value.length > max)
                max = value.length;
        }
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i].length < max) {
                double[] new_line = new double[max];
                System.arraycopy(matrix[i], 0, new_line, 0, matrix[i].length);
                matrix[i] = new_line;
            }
        }
    }

    /**
     * Возвращает матрицу, соответсвующую минору (i, j)
     * @param i номер строки элемента, для которого ищем минор
     * @param j номер столбца элемента, для которого ищем минор
     * @return матрица, соответсвующая минору (i, j)
     */
    private Matrix minorMatrix(int i, int j) {
        Matrix res = new Matrix(new double[matrix.length - 1][matrix.length - 1]);
        for (int k = 0; k < matrix.length - 1; k++) {
            for (int l = 0; l < matrix.length - 1; l++) {
                int i1, j1;
                if (k < i)
                    i1 = k;
                else
                    i1 = k + 1;
                if (l < j)
                    j1 = l;
                else
                    j1 = l + 1;
                res.matrix[k][l] = matrix[i1][j1];
            }
        }
        return res;
    }

    /**
     * Поиск определителя матрицы методом миноров.
     * Для ускорения работы ищется строка или столбец с наибольшим
     * количеством нулей.
     * @return опеределитель матрицы
     * @see Matrix#minorMatrix(int, int)
     */
    public double determinant() {
        if (matrix.length != matrix[0].length)
            return 0;
        if (matrix.length == 1)
            return matrix[0][0];
        double res = 0;
        int maxZeros = 0;
        int maxZerosIndex = 0;
        boolean maxZeroesVertical = false;
        for(int i = 0; i < matrix.length; i++) {
            int zeroesVertical = 0;
            int zeroesHorizontal = 0;
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] == 0)
                    zeroesHorizontal++;
                if (matrix[j][i] == 0)
                    zeroesVertical++;
            }
            if (zeroesVertical > maxZeros) {
                maxZeros = zeroesVertical;
                maxZeroesVertical = true;
                maxZerosIndex = i;
            }
            if (zeroesHorizontal > maxZeros) {
                maxZeros = zeroesHorizontal;
                maxZeroesVertical = false;
                maxZerosIndex = i;
            }
            if (maxZeros == matrix.length - 1)
                break;
        }
        for (int i = 0; i < matrix.length; i++) {
            int x = (maxZeroesVertical) ? i : maxZerosIndex;
            int y = (maxZeroesVertical) ? maxZerosIndex : i;
            if (matrix[x][y] != 0) {
                Matrix minor = minorMatrix(x, y);
                //System.out.println(minor);
                double d = matrix[x][y] * minor.determinant();
                if ((x + y) % 2 == 0)
                    res += d;
                else
                    res -= d;
            }
        }
        return res;
    }

    /**
     * Строковое представление матрицы для удобного вывода на экран.
     * @return строковое представлене матрицы
     */
    @Override
    public String toString() {
        long upperBound = 10;
        int wholeDigits = 1;
        boolean haveNegative = false;
        for (double[] line: matrix)
            for (double val: line) {
                while (abs(val) >= upperBound) {
                    upperBound *= 10;
                    wholeDigits++;
                }
                if (val < 0)
                    haveNegative = true;
            }
        if (haveNegative)
            wholeDigits++;
        StringBuilder res = new StringBuilder();
        for (double[] line: matrix) {
            res.append("[ ");
            for (double val: line) {
                String valStr = String.valueOf(val);
                int wholePart = valStr.indexOf('.');
                if (wholePart < 0)
                    wholePart = valStr.length();
                for (int i = 0; i < wholeDigits - wholePart; i++) {
                    res.append(" ");
                }
                int len = wholePart + 1 + outPrecision;
                if (len > valStr.length())
                    len = valStr.length();
                res.append(valStr, 0, len);
                for (int i = 0; i < wholePart + 1 + outPrecision - len; i++)
                    res.append(" ");
                res.append(" ");
            }
            res.append("]\n");
        }
        return res.toString();
    }
}
