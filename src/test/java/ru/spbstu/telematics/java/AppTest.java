package ru.spbstu.telematics.java;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    private final Map<double[][], Double> determinantTestData = new HashMap<double[][], Double>();
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    static public void main(String[] args)
    {
        TestCase test = new AppTest("deteminant") {
            public void runTest() {
                testDeterminant();
            }
        };
        test.run();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        determinantTestData.put(new double[][] {{}}, 0.0);
        determinantTestData.put(new double[][] {{1}}, 1.0);
        determinantTestData.put(new double[][] {{1, 2}, {4, 3}}, -3.0);
        determinantTestData.put(new double[][] {{1, 0}, {0, 1}}, 1.0);
        determinantTestData.put(new double[][] {{0, 1}, {1, 0}}, -1.0);
        determinantTestData.put(new double[][] {{2, 0}, {1, 0}}, 0.0);
        determinantTestData.put(new double[][] {{1, 1, 1},{0, 2, 2}, {0, 0, 3}}, 6.0);
        determinantTestData.put(new double[][] {{1, 1, 1},{1, 1, 1}, {1, 1, 1}}, 0.0);
        determinantTestData.put(new double[][] {{3, 4, 3}, {1, -1, 5}, {5, 6, 1}}, 26.0);
        determinantTestData.put(new double[][] {{1, 0}, {0, 1}, {1, 0}}, 0.0);
        determinantTestData.put(new double[][] {{1, 0, 1}, {0, 1, 0}}, 0.0);
        determinantTestData.put(new double[][] {{1}, {2, 2}, {3, 3, 3}}, 6.0);
    }

    @Override
    protected void tearDown() throws Exception {
        //super.tearDown();
        determinantTestData.clear();
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
    public void testDeterminant()
    {
        for (double[][] array : determinantTestData.keySet()) {
            final Double expected = (Double) determinantTestData.get(array);
            final double actual = new Matrix(array).determinantSafe();
            System.out.println(1);
            assertEquals(expected, actual);
        }
    }
}
