package ru.spbstu.telematics.java;

import java.io.File;

/**
 * Hello world!
 *
 */
public class Determinant
{
    public static void main( String[] args ) {
        if (args.length == 0) {
            System.out.println("Missing argument: expected path to file!");
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
