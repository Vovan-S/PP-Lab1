package ru.spbstu.telematics.java;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Locale;

public class MatrixFileReader {
    File file;


    public MatrixFileReader(File f) {
        file = f;
    }

    public Matrix read() throws Exception {
        FileReader fr = new FileReader(file);
        Scanner sc = new Scanner(fr);
        //используем, чтобы считать double
        sc.useLocale(Locale.US);
        int m, n;
        if (sc.hasNextInt())
            m = sc.nextInt();
        else {
            sc.close();
            throw new Exception("Invalid format of file!");
        }
        if (sc.hasNextInt())
            n = sc.nextInt();
        else {
            sc.close();
            throw new Exception("Invalid format of file!");
        }
        double[][] array = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (sc.hasNextDouble())
                    array[i][j] = sc.nextDouble();
                else if(sc.hasNextInt())
                    array[i][j] = sc.nextInt();
                else {
                    sc.close();
                    throw new Exception("Invalid format of file! Probably, not enough numbers.");
                }
                //System.out.println(array[i][j]);
            }
        }
        sc.close();
        return new Matrix(array);
    }
}
