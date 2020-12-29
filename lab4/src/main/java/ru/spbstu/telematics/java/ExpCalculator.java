package ru.spbstu.telematics.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

class ExpCalculator {
    final BigFloat[] results = new BigFloat[16];
    int precision;
    static BigFloat[] factorialTable = new BigFloat[16];
    boolean useThreads;

    final static int precisionMultiplier = 2;
    final static int precisionAdd = 20;

    ExpCalculator(int precision, boolean useThreads) {
        this.useThreads = useThreads;
        this.precision = precision;
        if (factorialTable[0] == null || factorialTable[0].precision() < 4 * precision) {
            int factorial = 1;
            for (int i = 0; i < 16; i++) {
                factorialTable[i] = BigMath.inverse(BigMath.valueOf(factorial),
                        precisionMultiplier * precision + precisionAdd);
                factorial *= (i + 1);
            }
        }
    }

    BigFloat calculate(BigFloat a) {
        if (a == null) {
            return BigMath.ONE;
        }
        boolean tempSavePi = BigMath.isSavingPiInRam();
        BigMath.savePiInRam(true);
        BigFloat x = BigMath.ONE;
        PrecisionChecker pc = new PrecisionChecker(precision, x);
        int[][] programs = {{0x11, 0x22, 0x44, 0x84, 0xc2},
                {0x21, 0x41, 0x81, 0xc1},
                {0x42, 0x82, 0x92},
                {0x43, 0x87}
        };
        ArrayList<Thread> powerThreads = new ArrayList<>(4);
        Thread sumThread = null;
        do {
            Arrays.fill(results, null);
            results[0] = BigMath.ONE;
            BigFloat dy = BigMath.subtractExact(a, BigMath.ln(x,
                    precisionMultiplier * precision + precisionAdd));
            results[1] = dy;
            if (useThreads) {
                sumThread = new Thread(new SumTread());
                sumThread.start();
                powerThreads.clear();
                for (int i = 0; i < 4; i++) {
                    Thread t = new Thread(new PowerThread(programs[i]));
                    powerThreads.add(t);
                    t.start();
                }
                try {
                    for (Thread t: powerThreads) {
                        t.join();
                    }
                    sumThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                for (int i = 2; i < 16; i++) {
                    results[i] = BigMath.multiply(results[i - 1], dy,
                            precisionMultiplier * precision);
                    results[0] = BigMath.addExact(results[0],
                            BigMath.multiply(results[i], factorialTable[i],
                                    precisionMultiplier * precision + precisionAdd));
                }
                results[0] = BigMath.addExact(results[0], results[1]);
            }
            x = BigMath.multiply(x, results[0],
                    precisionMultiplier * precision + precisionAdd);
        } while (!pc.check(x));
        BigMath.savePiInRam(tempSavePi);
        return x.round(precision);
    }

    private class PowerThread implements Runnable {
        int[] program;
        PowerThread(int[] program) {
            this.program = program;
        }
        @Override
        public void run() {
            //String name = Thread.currentThread().getName();
            //System.out.println(name + " started!");
            for (int command: program) {
                int a = (command & 0xf0) >> 4;
                int b = command & 0x0f;
                //System.out.println(name + " executing (" + a + "; " + b + "), " + Integer.toHexString(command));
                BigFloat x, y;
                synchronized (results) {
                    while (results[a] == null) {
                        try {
                            //System.out.println(name + " waiting for " + a);
                            results.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    x = results[a];
                    y = results[b];
                }
                x = BigMath.multiply(x, y,
                        precisionMultiplier * precision + precisionAdd);
                synchronized (results) {
                    //System.out.println(name + " uploaded " + (a + b));
                    results[a + b] = x;
                    results.notifyAll();
                }
            }
        }
    }

    private class SumTread implements Runnable {
        boolean[] summed;
        int count;
        SumTread() {
            summed = new boolean[16];
            Arrays.fill(summed, false);
            count = 0;
        }

        @Override
        public void run() {
            //String name = Thread.currentThread().getName() + "_sum";
            //System.out.println(name + " started!");
            Stack<Integer> toAdd = new Stack<>();
            while (count < 15) {
                while (!toAdd.empty()) {
                    int i = toAdd.pop();
                    results[0] = BigMath.addExact(results[0],
                            BigMath.multiply(results[i], factorialTable[i],
                                    precisionMultiplier * precision + precisionAdd));
                    count ++;
                    summed[i] = true;
                }
                if (count < 15) {
                    int k = 0;
                    do {
                        synchronized (results) {
                            for (int i = 1; i < 16; i++) {
                                if (results[i] != null && !summed[i]) {
                                    k ++;
                                    toAdd.push(i);
                                    //System.out.println(name + " picked " + i);
                                }
                            }
                            if (k == 0) {
                                try {
                                    //System.out.println(name + " picked nothing :(");
                                    results.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } while (k == 0);
                }
            }
        }
    }

}
