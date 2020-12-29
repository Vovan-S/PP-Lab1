package ru.spbstu.telematics.java;

import java.io.*;

public class SpeedTest {

    public static void main(String[] args) {
        String directory = "speedTests/";
        int[] piFastPrecisions = new int[]{500, 1000, 10000, 50000, 100000};
        int[] piBPPPrecisions = new int[]{200, 500, 1000, 2000, 10000};
        int[] BPPThreads = new int[]{1, 2, 4, 5, 8};
        int[] ln2Precisions = new int[]{500, 1000, 2000, 5000, 10000, 50000};
        int[] expPrecisions = new int[]{500, 1000, 2000, 5000, 10000};

        boolean[] test = new boolean[]{false, false, false, false, true};

        try {
            OutputStreamWriter writer;

            //Алгоритм BB4
            if (test[0]) {
                writer = new FileWriter(directory + "BB4.txt");
                System.out.println("Writing BB4");
                writer.write("Алгоритм BB4\nТочность");
                for (int v: piFastPrecisions) {
                    writer.write('\t');
                    writer.write(Integer.toString(v));
                }
                writer.write("\nОдин поток");
                for (int v: piFastPrecisions) {
                    writer.write('\t');
                    writer.write(getMilliseconds(new BB4Test(v, false)).toString());
                }
                writer.write("\nМногопоточность");
                for (int v: piFastPrecisions) {
                    writer.write('\t');
                    writer.write(getMilliseconds(new BB4Test(v, true)).toString());
                }
                writer.write("\n");
                writer.close();
            }

            //Алгоритм GL
            if (test[1]) {
                System.out.println("Writing GL");
                writer = new FileWriter(directory + "GL.txt");
                writer.write("Алгоритм GL\nТочность");
                for (int v: piFastPrecisions) {
                    writer.write('\t');
                    writer.write(Integer.toString(v));
                }
                writer.write("\nОдин поток");
                for (int v: piFastPrecisions) {
                    writer.write('\t');
                    writer.write(getMilliseconds(new GLTest(v, false)).toString());
                }
                writer.write("\nМногопоточность");
                for (int v: piFastPrecisions) {
                    writer.write('\t');
                    writer.write(getMilliseconds(new GLTest(v, true)).toString());
                }
                writer.write("\n");
                writer.close();
            }

            //Алгоритм BPP
            if (test[2]) {
                System.out.println("Writing BPP");
                writer = new FileWriter(directory + "BPP.txt");
                writer.write("Алгоритм BPP\nТочность");
                for (int t: piBPPPrecisions) {
                    writer.write('\t');
                    writer.write(Integer.toString(t));
                }
                for (int t: BPPThreads) {
                    writer.write("\nПотоков: " + t);
                    for(int p: piBPPPrecisions) {
                        writer.write('\t');
                        writer.write(getMilliseconds(new BPPTest(p, t)).toString());
                    }
                }
                writer.write('\n');
                writer.close();
            }

            //Вычисление ln2
            if (test[3]) {
                System.out.println("Writing ln2");
                //BigMath.savePiInRam(true);
                writer = new FileWriter(directory + "ln2.txt");
                writer.write("Вычисление ln2\nТочность");
                for (int p: ln2Precisions) {
                    writer.write('\t');
                    writer.write(Integer.toString(p));
                }
                writer.write("\nОдин поток");
                for (int p: ln2Precisions) {
                    writer.write('\t');
                    writer.write(getMilliseconds(new Ln2Test(p, false)).toString());
                }
                writer.write("\nМногопоточность");
                for (int p: ln2Precisions) {
                    writer.write('\t');
                    writer.write(getMilliseconds(new Ln2Test(p, true)).toString());
                }
                writer.write('\n');
                writer.close();
            }

            //вычисление exp(1)
            if (test[4]) {
                System.out.println("Writing exp");
                //BigMath.savePiInRam(true);
                writer = new FileWriter(directory + "exp.txt");
                writer.write("Вычисление e\nТочность");
                for (int p: expPrecisions) {
                    writer.write('\t');
                    writer.write(Integer.toString(p));
                }
                writer.write("\nОдин поток");
                for (int p: expPrecisions) {
                    writer.write('\t');
                    writer.write(getMilliseconds(new ExpTest(p, false)).toString());
                }
                writer.write("\nМногопоточность");
                for (int p: expPrecisions) {
                    writer.write('\t');
                    writer.write(getMilliseconds(new ExpTest(p, true)).toString());
                }
                writer.write('\n');
                writer.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static Float getMilliseconds(Runnable runnable) {
        long sum = 0;
        int N = 20;
        for (int i = 0; i < N; i++) {
            long m = System.currentTimeMillis();
            runnable.run();
            sum += System.currentTimeMillis() - m;
        }
        return (float) sum / N;
    }

    static class BB4Test implements Runnable {
        int precision;
        boolean useThreads;

        BB4Test(int precision, boolean useThreads) {
            this.precision = precision;
            this.useThreads = useThreads;
        }

        @Override
        public void run() {
            boolean t = PiCalculation.USE_THREADS;
            PiCalculation.USE_THREADS = useThreads;
            PiCalculation.BB4(precision);
            PiCalculation.USE_THREADS = t;
        }
    }

    static class GLTest implements Runnable {
        int precision;
        boolean useThreads;

        GLTest(int precision, boolean useThreads) {
            this.precision = precision;
            this.useThreads = useThreads;
        }

        @Override
        public void run() {
            boolean t = PiCalculation.USE_THREADS;
            PiCalculation.USE_THREADS = useThreads;
            PiCalculation.GaussLegendrePi(precision);
            PiCalculation.USE_THREADS = t;
        }
    }

    static class BPPTest implements Runnable {
        int precision;
        int numberOfThreads;

        BPPTest(int precision, int numberOfThreads) {
            this.precision = precision;
            this.numberOfThreads = numberOfThreads;
        }

        @Override
        public void run() {
            PiCalculation.BPP(precision, numberOfThreads);
        }
    }

    static class Ln2Test implements Runnable {
        int precision;
        boolean useThreads;

        Ln2Test(int precision, boolean useThreads) {
            this.precision = precision;
            this.useThreads = useThreads;
        }

        @Override
        public void run() {
            boolean t = BigMath.isUsingThreads();
            BigMath.setUsingThreads(useThreads);
            BigMath.ln2(precision, true);
            BigMath.setUsingThreads(t);
        }
    }

    static class ExpTest implements Runnable {
        int precision;
        boolean useThreads;

        ExpTest(int precision, boolean useThreads) {
            this.precision = precision;
            this.useThreads = useThreads;
        }

        @Override
        public void run() {
            boolean t = BigMath.isUsingThreads();
            BigMath.setUsingThreads(useThreads);
            BigMath.exp(BigMath.ONE, precision);
            BigMath.setUsingThreads(t);
        }
    }
}
