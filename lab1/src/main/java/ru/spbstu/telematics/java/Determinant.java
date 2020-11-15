package ru.spbstu.telematics.java;

import java.io.File;

/**
 * Выполнимый класс для вычисления определителя матрицы. Матрицу считывает из
 * файл.
 * @see MatrixFileReader
 * @see Matrix
 */
public class Determinant
{
    /**
     * Вычисление определителя матрицы из файла.
     * @param args относительный путь к файлу с матрицей.
     * @see MatrixFileReader
     * @see Matrix
     */
    public static void main( String[] args ) {
        if (args.length == 0) {
            System.out.println("Missing argument: expected path to file!");
            return;
        }
        if (args.length > 1) {
            System.out.println("Too much arguments!");
            return;
        }
        File f = new File(args[0]);
        if (!f.exists()) {
            System.out.println("Error: File '" + f.getAbsolutePath() + "' doesn't exist!");
            return;
        }
        if (!f.isFile()) {
            System.out.println("Error: '" + f.getAbsolutePath() + "' is supposed to be a file!");
            return;
        }
        MatrixFileReader fr = new MatrixFileReader(f);
        Matrix m;
        try {
            m = fr.read();
        }
        catch (Exception e) {
            System.out.println("Error: Problem with file reading: '" + e.getMessage() + "'.");
            return;
        }
        System.out.println("Matrix:");
        System.out.println(m);
        try {
            System.out.println("\nDeterminant:");
            System.out.println(m.determinant());
        }
        catch (Exception e) {
            System.out.println("Cannot evaluate determinant: Matrix is not square!");
        }
    }
}
