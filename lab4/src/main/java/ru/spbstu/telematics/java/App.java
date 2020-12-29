package ru.spbstu.telematics.java;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.math.BigInteger;
import java.util.Random;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        int n = 10000;
        BigMath.exp(BigMath.ONE, n);
        BigMath.setUsingThreads(true);
        long m = System.currentTimeMillis();
        BigMath.exp(BigMath.ONE, n);
        m = System.currentTimeMillis() - m;
        System.out.println(m);
        BigMath.setUsingThreads(false);
        m = System.currentTimeMillis();
        BigMath.exp(BigMath.ONE, n);
        m = System.currentTimeMillis() - m;
        System.out.println(m);
    }

    private static void bigMathTest() {
        BigMath.setUsingThreads(true);
        BigFloat a = BigMath.valueOf(10);
        BigFloat b = BigMath.valueOf(4);
        System.out.println(BigMath.add(a, b));
        System.out.println(BigMath.subtract(a, b));
        System.out.println(BigMath.multiply(a, b));
        System.out.println(BigMath.divide(a, b));
        System.out.println(BigMath.inverse(a));
        System.out.println(BigMath.inverseSqrt(a));
        System.out.println(BigMath.sqrt(a).multiply(BigMath.sqrt(a)));
        System.out.println(BigMath.pi(150, false));
        System.out.println(BigMath.ln2());
        System.out.println(BigMath.ln(BigMath.valueOf(3)));
        System.out.println(BigMath.log(a, b));
        //System.out.println(BigMath.exp(BigMath.ONE, 1000));
        System.out.println(BigMath.pow(BigMath.valueOf(3), b));
    }

    private static void readWriteTest() {
        BigFloat pi = PiCalculation.BB4(100);
        System.out.println(pi.toString(10));
        String filePath = "pi.bf";
        try {
            OutputStream outputStream;
            outputStream = new FileOutputStream(filePath);
            //outputStream = System.out;
            BigFloatWriter writer = new BigFloatWriter(outputStream);
            writer.writeBinary(pi);
            outputStream.close();
            InputStream inputStream = new FileInputStream(filePath);
            BigFloatReader reader = new BigFloatReader(inputStream);
            System.out.println(reader.readPrecision());
            pi = reader.read(100);
            System.out.println(pi.toString(10));
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void expTest() {
        BigFloat.USE_THREADS = false;
        //System.out.println(new BigFloat(1).add(new BigFloat(1, 6)).ln(50).toString(10));
        //System.out.println(new BigFloat(1, 1).inverseSqrt(100).toString(10));
        //System.out.println(PiCalculation.BB4(100).toString(10));
        System.out.println(new BigFloat(81).ln(100).toString(10));
        long m = System.currentTimeMillis();
        new BigFloat(1).exp(1000).toString(10);
        m = System.currentTimeMillis() - m;
        System.out.println("e: " + m + " ms");
        m = System.currentTimeMillis();
        PiCalculation.BB4(1000);
        m = System.currentTimeMillis() - m;
        System.out.println("pi: " + m + " ms");
    }

    private static void piTest() {
        long m;
        BigFloat pi;
        int precision = 900;
        System.out.println("Precision: " + precision);
        System.out.println("Without threads:");
        PiCalculation.USE_THREADS = false;
        m = System.currentTimeMillis();
        pi = PiCalculation.GaussLegendrePi(precision);
        m = System.currentTimeMillis() - m;
        System.out.println("GL: " + m + " ms");
        m = System.currentTimeMillis();
        pi = PiCalculation.BB4(precision);
        m = System.currentTimeMillis() - m;
        System.out.println("BB4: " + m + " ms");
        System.out.println("Using threads:");
        PiCalculation.USE_THREADS = true;
        m = System.currentTimeMillis();
        pi = PiCalculation.GaussLegendrePi(precision);
        m = System.currentTimeMillis() - m;
        System.out.println("GL: " + m + " ms");
        //System.out.println(pi);
        m = System.currentTimeMillis();
        pi = PiCalculation.BB4(precision);
        m = System.currentTimeMillis() - m;
        System.out.println("BB4: " + m + " ms");
        //System.out.println(pi);
    }

    private static void testRound() {
        BigFloat f = new BigFloat(6, 2);
        System.out.println(f.round(1, 0));
        System.out.println(f.round(1, -1));
        System.out.println(f.round(1, 1));
    }

    private static void fftTest() {
        int n = 5;
        ArrayList<ComplexNumber> a = new ArrayList<>(1 << 5);
        for (int i = 0; i < 1 << n; i++) {
            a.add(ComplexNumber.valueOf(i, 0));
        }
        FFT fft = new FFT(n, 6*n);
        FFT.FFTInstance f1 = fft.getFFTInstance(a);
        a = f1.transform(false);
        f1.setElements(a);
        a = f1.transform(true);
        for (ComplexNumber complexNumber : a) {
            System.out.println(complexNumber.round(0));
        }
        System.out.println(a);
    }

    private static void multiplyTest() {
        BigInteger a = new BigInteger(1000000, new Random(30));
        BigInteger b = new BigInteger(1000000, new Random(35));
        BigFloat f = new BigFloat(a, 0);
        BigFloat g = new BigFloat(b, 0);
        //System.out.println(f);
        //System.out.println(g);
        long m1 = System.currentTimeMillis();
        BigFloat prod1 = f.multiply(g);
        m1 = System.currentTimeMillis() - m1;
        long m2 = System.currentTimeMillis();
        BigFloat prod2 = f.multiplyFourier(g);
        m2 = System.currentTimeMillis() - m2;
        //System.out.println(prod1);
        //System.out.println(prod2);
        BigInteger xor = prod1.number.xor(prod2.number);
        //System.out.println(xor.toString(16));
        //System.out.println(xor.bitCount());
        System.out.println("Built-in: " + m1 + " ms, Fourier: " + m2 + " ms");

        BigFloat.USE_THREADS = false;
        long m = System.currentTimeMillis();
        //BigFloat g = BigFloat.pi(100);
        //System.out.println("Time elapsed: " + (System.currentTimeMillis() - m) + " ms");
    }

    private static void tryInverse(BigInteger a, int precision) {
        System.out.println(a);
        int n = a.bitLength();
        while (n >= 1 && !a.testBit(n - 1))
            n--;
        System.out.println(n);
        int initial = 0;
        for (int i = 3; i >= 1; i--) {
            if (a.testBit(n - i))
                initial += 1 << (3 - i);
        }
        System.out.println(initial);
        BigInteger x = BigInteger.valueOf(32 / initial);
        BigInteger y;
        System.out.println(x);
        int exponent = 2;
        int k = 0;
        while (precision > (1 << k)) {
            int old_exponent = exponent;
            y = x.multiply(x);
            exponent *= 2;
            int shift = n - (1 << k + 1) - 3;
            if (shift > 0) {
                y = y.multiply(a.shiftRight(shift));
                exponent += (1 << k + 1) + 3;
            }
            else {
                y = y.multiply(a);
                exponent += n;
            }
            x = x.shiftLeft(1 + exponent - old_exponent).subtract(y).shiftRight(exponent - (1 << k + 1));
            exponent = (1 << k + 1);
            System.out.println(x.multiply(a).toString(16) + "\n\t" + exponent);
            k ++;
        }
    }
}
