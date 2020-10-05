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
        }
        
    }
}
