package ru.spbstu.telematics.java;

public class PrecisionChecker {
    int precision;
    BigFloat previousValue;
    int iteration;

    public PrecisionChecker(int precision, BigFloat first) {
        this.precision = precision;
        previousValue = first;
        iteration = 0;
    }

    public boolean check(BigFloat nextValue) {
        int currentPrecision = -nextValue.subtract(previousValue).floorOfLog2();
        previousValue = nextValue;
        //System.out.println("Current precision: " + currentPrecision);
        iteration ++;
        return currentPrecision > precision || currentPrecision == Integer.MIN_VALUE;
    }

    public BigFloat getPreviousValue() {
        return previousValue;
    }

    public int getIteration() {
        return iteration;
    }
}
