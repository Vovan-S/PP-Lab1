package ru.spbstu.telematics.java;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    static String piFileName = "testing/pi.txt";
    static String sqrtFileName = "testing/sqrt2.txt";
    static String eFileName = "testing/e.txt";

    public static void main(String[] args) {
        TestCase test = new AppTest("deteminant") {
            public void runTest() {
                testApp();
            }
        };
        test.run();
    }

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        System.out.println("Testing pi");
        for (int i = 1; i < 200; i++) {
            System.out.println("pi, testing precision: " + (i * 5));
            testPi(i * 5);
        }
        System.out.println("Testing e");
        for (int i = 1; i < 400; i++) {
            System.out.println("e, testing precision: " + (i * 5));
            testExp(i * 5);
        }
        System.out.println("Testing sqrt2");
        for (int i = 1; i < 200; i++) {
            System.out.println("sqrt2, testing precision: " + (i * 5));
            testSqrt2(i * 5);
        }
        System.out.println("All tests are passed successfully!");
    }

    public void testPi(int precision) {
        BigFloat pi = BigMath.pi(precision, true);
        try {
            FileReader reader = new FileReader(piFileName);
            for (int i = 0; i < precision + 2; i++) {
                int b = reader.read();
                if ((b == 49) != getNthBit(pi, i)) {
                    System.out.println("Actual precision: " + (i - 2));
                    fail();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testExp(int precision) {
        BigFloat exp = BigMath.exp(BigMath.ONE, precision);
        try {
            FileReader reader = new FileReader(eFileName);
            for (int i = 0; i < precision + 2; i++) {
                int b = reader.read();
                if ((b == 49) != getNthBit(exp, i)) {
                    System.out.println("Actual precision: " + (i - 2));
                    fail();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testSqrt2(int precision) {
        BigFloat exp = BigMath.sqrt(BigMath.valueOf(2), precision);
        try {
            FileReader reader = new FileReader(sqrtFileName);
            for (int i = 0; i < precision + 1; i++) {
                int b = reader.read();
                if ((b == 49) != getNthBit(exp, i)) {
                    System.out.println("Actual precision: " + (i - 1));
                    fail();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getNthBit(BigFloat a, int n) {
        int bits = a.valuableBits();
        return (bits > n) && a.number.testBit(bits - n - 1);
    }


}
