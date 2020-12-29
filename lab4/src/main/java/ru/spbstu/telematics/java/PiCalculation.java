package ru.spbstu.telematics.java;

import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.List;

public class PiCalculation {

    static public boolean USE_THREADS = true;

    /**
     * Вычисление числа пи с указанной точностью.
     * Используется A-G итерация: <br>
     * A[0] = 1, B[0] = 2^(-1/2), T[0] = 1/4, X = 1. <br>
     * A[i+1] = (A[i] + B[i]) /2 <br>
     * B[i+1] = sqrt(A[i]*B[i]) <br>
     * T[i+1] = T[i] - X[i]*(A[i+1] - A[i])^2 <br>
     * X[i+1] = 2*X[i] <br>
     * Тогда pi ~= (A + B)^2 / 4T
     * @param precision точность в битах после двоичной запятой.
     * @return число пи с указанной точностью.
     */
    static public BigFloat GaussLegendrePi (int precision) {
        //инициализация
        BigFloat a = new BigFloat(1);
        final BigFloat[] b = {new BigFloat(2).inverseSqrt(precision), null};
        BigFloat t = new BigFloat(1, 2);
        int x = 0;
        Thread bCalculator = null;
        while (true) {
            BigFloat finalA = a;
            if (USE_THREADS) {
                bCalculator = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        b[1] = b[0].multiply(finalA).sqrt(precision + 5);
                    }
                });
                bCalculator.start();
            }
            BigFloat e = a.subtract(b[0]);
            int currentPrecision = -e.floorOfLog2();
            if (currentPrecision > precision) {
                break;
            }
            a = a.add(b[0]).multiplyByPowerOfTwo(-1);
            BigFloat dA = e.multiplyByPowerOfTwo(-1);
            t = t.subtract(dA.multiply(dA).multiplyByPowerOfTwo(x)).round(precision + 5);
            x ++;
            if (USE_THREADS) {
                try {
                    if (bCalculator != null) {
                        bCalculator.join();
                        b[0] = b[1];
                    }
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            else {
                b[0] = b[0].multiply(finalA).sqrt(precision + 5);
            }
        }
        a = a.add(b[0]);
        a = a.multiply(a);
        t = t.inverse(precision);
        return a.multiply(t).multiplyByPowerOfTwo(-2).round(precision);
    }

    static public BigFloat BPP(int precision, int threads) {
        List<Thread> threadList = new ArrayList<>(threads);
        int n = precision / 4 + 1;
        int steps = n / threads + 1;
        BigFloat[] results = new BigFloat[threads];
        for (int i = 0; i < threads; i++) {
            results[i] = new BigFloat(0);
            Thread t = new Thread(new BBPCalculator(
                    i, results, i, threads, steps, precision));
            threadList.add(t);
            t.start();
        }
        try {
            for (Thread t: threadList) {
                t.join();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        BigFloat res = new BigFloat(0);
        for (BigFloat x : results) {
            res = res.add(x);
        }
        return res.round(precision);
    }

    /**
     * Алгоритм Borwein Brothers 4 порядка. <br>
     * y[0] = 2^(1/2) - 1 <br>
     * z[0] = 2*y[0]^2 <br>
     * y[n+1] = (1 - (1 - y[n]^4)^(1/4)) / (1 + (1 - y[n]^4)^(1/4)) <br>
     * z[n+1] = z[n]*(1 + y[n+1])^4 - 2^(2n+3)*y[n+1]*(1+y[n+1]+y[n+1]^2) <br>
     *  pi = 1 / z <br>
     * С каждой итерацией точность увеличивается примерно в 4 раза.
     * @param precision точность в битах после двоичной точки.
     * @return значение числа пи с указанной точностью.
     */
    static public BigFloat BB4(int precision) {
        if (USE_THREADS) {
            return new BB4Calculator().calculate(precision);
        }
        //precision += 10;
        BigFloat one = new BigFloat(1);
        int oldPrecision = precision;
        //precision += precision / 3;
        // последовательная реализация
        BigFloat y = new BigFloat(1, 1).inverseSqrt(precision).subtract(one);
        //System.out.println(y.multiply(y).add(y.multiplyByPowerOfTwo(1)).subtract(one).floorOfLog2());
        //y = y.multiply(y).round(precision);
        BigFloat z = y.multiply(y).multiplyByPowerOfTwo(1).round(precision);
        int currentPrecision = 1;
        int iteration = 0;
        BigFloat prev = z.inverse(precision);
        BigFloat pi = new BigFloat();
        while (currentPrecision < precision) {
            y = y.multiply(y);
            y = y.multiply(y);
            y = one.subtract(y).inverseSqrt(precision);
            y = y.inverseSqrt(precision);
            y = one.subtract(y).divide(y.add(one), precision);
            BigFloat a = y.add(one);
            a = a.multiply(a);
            BigFloat t = a.subtract(y).multiply(y).
                    multiplyByPowerOfTwo(2 * iteration + 3);
            a = a.multiply(a).multiply(z);
            z = a.subtract(t);
            iteration ++;
            pi = z.inverse(precision);
            currentPrecision = -pi.subtract(prev).floorOfLog2();
            prev = pi;
            if (currentPrecision == Integer.MIN_VALUE)
                break;
        }
        return pi.round(oldPrecision, 0);
    }

    static public BigFloat lameButThready(int precision, int threads) {
        if (precision > 62) {
            throw new ArithmeticException("Too precise for this algorithm!!!!");
        }
        int k = precision + 1;
        long n = (1 << k);
        long di = n / threads;
        List<Thread> threadList = new ArrayList<>(threads);
        BigFloat[] results = new BigFloat[threads];
        for (int i = 0; i < threads; i++) {
            results[i] = new BigFloat(0);
            Thread t = new Thread(new LamePiCalculator(i, i * di,
                    (i == threads - 1) ? n : (i + 1)* di,
                    k, results));
            threadList.add(t);
            t.start();
        }
        try {
            for (Thread t: threadList) {
                t.join();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        BigFloat res = new BigFloat(0);
        for (BigFloat x : results) {
            res = res.add(x);
        }
        return res.multiplyByPowerOfTwo(2 - k).round(precision);
    }

    private static class MyRendezvous<G> {
        boolean completed;
        G res;

        public MyRendezvous() {
            completed = false;
            res = null;
        }

        public synchronized void set(G val) {
            if (completed)
                return;
            res = val;
            completed = true;
            notify();
        }

        public synchronized void waitForCompletion() {
            long m = System.currentTimeMillis();
            if (!completed) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Waited: " + (System.currentTimeMillis() - m));
        }

        public G get() {
            waitForCompletion();
            return res;
        }
    }

    private static class BBPCalculator implements Runnable {
        int numberOfThread;
        BigFloat[] resultsStorage;
        int startStep;
        int stepSize;
        int numberOfSteps;
        int precision;

        public BBPCalculator(int numberOfThread,
                             BigFloat[] resultStorage,
                             int startStep,
                             int stepSize,
                             int numberOfSteps,
                             int precision) {
            this.numberOfThread = numberOfThread;
            this.numberOfSteps = numberOfSteps;
            this.startStep = startStep;
            this.stepSize = stepSize;
            this.resultsStorage = resultStorage;
            this.precision = precision;
        }

        @Override
        public void run() {
            BigFloat[] constants = {new BigFloat(1), new BigFloat(4),
                new BigFloat(5), new BigFloat(6)};
            BigFloat bigK;
            BigFloat res;
            for (int i = 0; i < numberOfSteps; i++) {
                int k = startStep + i * stepSize;
                bigK = new BigFloat(k, -3);
                res = bigK.add(constants[0]).inverse(precision).multiplyByPowerOfTwo(2);
                res = res.subtract(bigK.add(constants[1]).inverse(precision).multiplyByPowerOfTwo(1));
                res = res.subtract(bigK.add(constants[2]).inverse(precision));
                res = res.subtract(bigK.add(constants[3]).inverse(precision));
                resultsStorage[numberOfThread] =
                        resultsStorage[numberOfThread].add(res.multiplyByPowerOfTwo(-4 * k));
            }
        }
    }

    private static class LamePiCalculator implements Runnable {
        int i;
        long lowerBound;
        long upperBound;
        int k;
        BigFloat[] resultStorage;
        public LamePiCalculator(int i, long lowerBound, long upperBound, int k, BigFloat[] resultStorage) {
            this.i = i;
            this.upperBound = upperBound;
            this.lowerBound = lowerBound;
            this.k = k;
            this.resultStorage = resultStorage;
        }

        @Override
        public void run() {
            BigFloat x;
            BigFloat one = new BigFloat(1);
            for (long j = lowerBound; j < upperBound; j++) {
                x = new BigFloat(j, k);
                resultStorage[i] = resultStorage[i].add(one.add(x.multiply(x)).inverse(k));
            }
        }
    }

    /**
     * Используем два дополнительных потока для ускорения
     * вычисления по алгоритму BB4.
    */
    private static class BB4Calculator {
        // Перед каждой итерацией должно быть вычислено 3 значения:
        // alpha = (1 + y^4)^(1/4)
        // beta = 1 / (1 + alpha)
        // z = z
        BigFloat[] results;

        // Основной поток вычисляет следующие значения alpha и beta
        // {y', A, B}, где y' = (1 - alpha)*beta; A, B - временные переменные
        BigFloat[] mainThreadVars;

        // Положим B = (y' + 1) = 2*beta, тогда z' = z*B^4 - 2^(2n+3)*y'*(B^2 - y')
        // Второй поток вычислит B^2, потом B^4 и z*B^4,
        // третий поток вычислит 2^(2n+3)*y'*(B^2 - y'), после чего результаты
        // нужно вычесть.
        BigFloat[] secondThreadVars;
        BigFloat[] thirdThreadVars;

        // Переменная для синхронизации результата вычисления
        // значиения y'
        final boolean[] yLock;

        int precision;
        int iteration;

        BB4Calculator() {
            results = new BigFloat[]{new BigFloat(), new BigFloat(), new BigFloat()};
            mainThreadVars = new BigFloat[]{new BigFloat(), new BigFloat(), new BigFloat()};
            secondThreadVars = new BigFloat[]{new BigFloat(), new BigFloat()};
            thirdThreadVars = new BigFloat[]{new BigFloat()};
            yLock = new boolean[]{false};
            iteration = 0;
            precision = 0;
        }

        BigFloat calculate(int Precision) {
            this.precision = Precision;
            BigFloat one = new BigFloat(1);
            // изначальная инициализация
            BigFloat sqrtTwo = new BigFloat(1, 1).inverseSqrt(precision);
            // y = 2^(1/2) - 1
            BigFloat y = sqrtTwo.subtract(one);
            BigFloat ySquared  = y.multiply(y);
            // z = 2*y*y
            results[2] = ySquared.multiplyByPowerOfTwo(1).round(precision);
            //y = one.subtract(ySquared.multiply(ySquared)).inverseSqrt(precision).
            //        inverseSqrt(precision);
            //y = new BigFloat(2).subtract(sqrtTwo).multiplyByPowerOfTwo(1);
            y = results[2].multiply(sqrtTwo).multiplyByPowerOfTwo(1);
            y = y.inverseSqrt(precision).inverseSqrt(precision);
            results[1] = one.add(y).inverse(precision);

            // pi - 1/z[0] ~= 0.23 < 0.5, значит изначальная
            // оценка имеет точность 1 бит после двоичной точки
            int currentPrecision = 1;
            iteration = 0;
            BigFloat prev = results[2].inverse(precision);
            BigFloat pi = new BigFloat();
            while (currentPrecision < precision) {
                //System.out.println("Iteration: " + iteration);
                //System.out.println("pi = " + results[2].inverse(precision).round(precision));
                yLock[0] = false;
                Thread second = new Thread(new SecondThread());
                second.start();
                // y' = (1 - alpha) * beta
                mainThreadVars[0] = results[1].multiplyByPowerOfTwo(1).subtract(one);
                synchronized (yLock) {
                    //System.out.println("T1: Y calculated, Y = " + mainThreadVars[0]);
                    yLock[0] = true;
                    yLock.notify();
                }
                // A = y'^2
                mainThreadVars[1] = mainThreadVars[0].multiply(mainThreadVars[0]).round(precision);
                // A = 1 - y'^4
                mainThreadVars[1] = one.subtract(mainThreadVars[1].multiply(mainThreadVars[1])).
                        round(precision);
                // A = (1 - y'^4)^(1/4)
                mainThreadVars[1] = mainThreadVars[1].inverseSqrt(precision).inverseSqrt(precision);
                // B = 1 / (1 + (1 - y'^4)^(1/4))
                mainThreadVars[2] = one.add(mainThreadVars[1]).inverse(precision);
                try {
                    //System.out.println("T1: Joining thread 2");
                    second.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                results[0] = mainThreadVars[1];
                results[1] = mainThreadVars[2];
                iteration ++;
                pi = results[2].inverse(precision);
                currentPrecision = -pi.subtract(prev).floorOfLog2();
                if (currentPrecision == Integer.MIN_VALUE)
                    break;
                //System.out.println(currentPrecision);
                prev = pi;
            }
            return pi.round(Precision);
        }

        private class SecondThread implements Runnable {
            @Override
            public void run() {
                //System.out.println("T2: Thread 2 started");
                // Вычисляем (1 + y')^2 = 4*beta^2
                secondThreadVars[0] = results[1].multiply(results[1])
                        .multiplyByPowerOfTwo(2).round(precision);
                // Тут же запускаем третий поток
                Thread third = new Thread(new ThirdThread());
                third.start();
                // Вычисляем (1 + y')^4
                secondThreadVars[1] = secondThreadVars[0].multiply(secondThreadVars[0]).
                        round(precision);
                // Вычисляем z*(1 + y')^4
                secondThreadVars[1] = secondThreadVars[1].multiply(results[2]).round(precision);
                // ждем, пока третий поток досчитает
                try {
                    //System.out.println("T2: Joining thread 3");
                    third.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Вычисляем новое значение z
                results[2] = secondThreadVars[1].subtract(thirdThreadVars[0]);
            }
        }

        private class ThirdThread implements Runnable {
            @Override
            public void run() {
                //System.out.println("T3: Thread 3 started");
                synchronized (yLock) {
                    if (!yLock[0]) {
                        //System.out.println("T3: Waiting for Y");
                        try {
                            yLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //System.out.println("T3: Received Y, Y = " + mainThreadVars[0]);
                // Вычисляем (1 + y')^2 - y'
                thirdThreadVars[0] = secondThreadVars[0].subtract(mainThreadVars[0]);
                thirdThreadVars[0] = thirdThreadVars[0].multiply(mainThreadVars[0]).
                        multiplyByPowerOfTwo(2 * iteration + 3).round(precision);
            }
        }
    }
}
