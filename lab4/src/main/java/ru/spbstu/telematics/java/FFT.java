package ru.spbstu.telematics.java;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Реализация быстрого преобразования Фурье.
 */
public class FFT {
    /**
     * Класс, реализующий код Грея для быстрого
     * умножения корней из единицы
     */
    static public class GrayCode {
        int bits;
        int lastInverted;
        boolean newValue;
        int index;
        int i;

        private int Q(int j) {
            int q = 0;
            while ((j & (1 << q)) == 0) {
                q ++;
            }
            return q;
        }

        public GrayCode(int bits) {
            this.bits = bits;
            lastInverted = -1;
            newValue = false;
            index = 0;
            i = -1;
        }

        public int nextIndex() {
            i++;
            if (i == 0) {
                return index;
            }
            if (i < (1 << bits)) {
                lastInverted = Q(i);
                index ^= (1 << lastInverted);
                newValue = (index & 1 << lastInverted) > 0;
                return index;
            }
            i = -1;
            index = -1;
            lastInverted = -1;
            return index;
        }

        public int getLastInverted() {
            return lastInverted;
        }

        public boolean getNewValue() {
            return newValue;
        }

    }

    private final int precision;
    private final int log2Size;

    /**
     * Таблица корней из 1.
     */
    private class WTable {
        private ArrayList<ComplexNumber> ws;

        public WTable() {
            initTable();
        }


        public WTable(int depth) {
            initTable();
            fillTable(depth);
        }

        private void initTable() {
            ws = new ArrayList<>();
            ws.add(ComplexNumber.valueOf(-1, 0));
            ws.add(ComplexNumber.valueOf(0, 1));
        }

        public int getDepth() {
            return ws.size();
        }

        public void fillTable(int depth) {
            synchronized (this) {
            if (ws.size() >= depth)
                return;
            ComplexNumber e = ws.get(ws.size() - 1);
            while (ws.size() < depth) {
                e = e.nextOneRoot(FFT.this.precision);
                    ws.add(e);
            }
            }
        }

        public ComplexNumber newRoot(ComplexNumber oldRoot, int bitSwitch, boolean newState) {
            if (newState)
                return ws.get(bitSwitch).multiply(oldRoot).round(precision);
            else
                return ws.get(bitSwitch).conjugate().multiply(oldRoot).round(precision);
        }

        public void calculateNextRoot(int depth) {
            synchronized (this) {
                if (ws.size() == depth) {
                    ComplexNumber e = ws.get(ws.size() - 1);
                    ws.add(e.nextOneRoot(precision));
                }
            }
        }
    }

    private final WTable table;

    public class FFTInstance {
        ArrayList<ComplexNumber> elements;
        int iteration;

        FFTInstance(byte[] array, int bitsPerNumber) {
            elements = new ArrayList<>(1 << log2Size);
            iteration = 1;
            if (bitsPerNumber == Byte.SIZE) {
                int i = 0;
                for (i = 0; i < (1 << log2Size); i++) {
                    if (i < array.length)
                        elements.add(new ComplexNumber(
                                new BigFloat(0xff & array[array.length - i - 1],
                                        log2Size + bitsPerNumber)));
                    else
                        elements.add(ComplexNumber.valueOf(0,0));
                }
            }
            else if (bitsPerNumber % Byte.SIZE == 0) {
                int bytes = bitsPerNumber / Byte.SIZE;
                boolean arrayEnded = false;
                for (int i = 0; i < (1 << log2Size); i++) {
                    if (arrayEnded) {
                        elements.add(ComplexNumber.valueOf(0, 0));
                    }
                    else {
                        byte[] buff = new byte[bytes];
                        for (int j = 0; j < bytes; j++) {
                            if (i * bytes + j < array.length) {
                                buff[bytes - j - 1] = array[array.length - (i * bytes + j) - 1];
                            }
                            else {
                                arrayEnded = true;
                                break;
                            }
                        }
                        elements.add(new ComplexNumber(
                                new BigFloat(
                                        new BigInteger(1, buff),
                                        bitsPerNumber + log2Size)));
                    }
                }
            }
            else {
                BigInteger bi = new BigInteger(array);
                byte[] mask = new byte[(bitsPerNumber - 1) / Byte.SIZE + 1];
                for (int i = 1; i < mask.length; i++) {
                    mask[mask.length - i] = -1;
                }
                for (int i = 0; i < bitsPerNumber % Byte.SIZE; i++) {
                    mask[0] |= (1 << i);
                }
                BigInteger maskNumber = new BigInteger(mask);
                for (int i = 0; i < (1 << log2Size); i++) {
                    elements.add(new ComplexNumber(new BigFloat(bi.and(maskNumber), bitsPerNumber + log2Size)));
                    bi = bi.shiftRight(bitsPerNumber);
                }
            }
        }

        FFTInstance(ArrayList<ComplexNumber> array) {
            elements = array;
            iteration = 1;
        }

        public void iterate() {
            if (iteration > log2Size)
                return;
            int n = log2Size - iteration;
            table.calculateNextRoot(iteration - 1);
            GrayCode grayCode = new GrayCode(iteration);
            ArrayList<ComplexNumber> buff = new ArrayList<>(1 << log2Size);
            for (int i = 0; i < 1 << log2Size; i++) {
                buff.add(null);
            }
            int index;
            ComplexNumber w = new ComplexNumber();
            while ((index = grayCode.nextIndex()) != -1) {
                if (index == 0) {
                    w = ComplexNumber.valueOf(1,0);
                }
                else {
                    w = table.newRoot(w, grayCode.getLastInverted(), grayCode.getNewValue());
                }
                for (int t = 0; t < (1 << n); t++) {
                    int k = t + (index << n);
                    buff.set(k, elements.get(k & ~(1 << n)).add(
                            elements.get(k | (1 << n)).multiply(w)
                    ).round(precision));
                }
            }
            iteration ++;
            elements = buff;
        }

        public ArrayList<ComplexNumber> transform(boolean inverse) {
            if (inverse) {
                for (int i = 0; i < elements.size(); i++) {
                    elements.set(i, elements.get(i).conjugate());
                }
            }
            iteration = 1;
            while (iteration <= log2Size)
                iterate();
            ArrayList<ComplexNumber> res = new ArrayList<>(elements.size());
            for (int i = 0; i < elements.size(); i++) {
                int k = Integer.reverse(i << (Integer.SIZE - log2Size));
                if (inverse)
                    res.add(elements.get(k).multiplyByPowerOfTwo(-log2Size).conjugate());
                else
                    res.add(elements.get(k));
            }
            return res;
        }

        public void setElements(ArrayList<ComplexNumber> elements) {
            this.elements = elements;
        }
    }

    public FFT(int log2Size, int precision) {
        this.precision = precision;
        this.log2Size = log2Size;
        table = new WTable();
    }

    public FFTInstance getFFTInstance(byte[] digits, int bitsPerElement) {
        return new FFTInstance(digits, bitsPerElement);
    }

    public FFTInstance getFFTInstance(ArrayList<ComplexNumber> elements) {
        return new FFTInstance(elements);
    }
}
