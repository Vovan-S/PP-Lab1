package ru.spbstu.telematics.java;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        System.out.println( "Hello World!" );
        double[][] ar = {{1, 1, 1}, {0, 2, 2}, {0, 0, 3}};
        Matrix m = new Matrix(ar);
        System.out.println(m);
        System.out.println(m.determinant());
    }
}
