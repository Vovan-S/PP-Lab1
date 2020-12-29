package ru.spbstu.telematics.java;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Число в формате number * 2 ^ (-exponent)
 */
public class BigFloat {
    int exponent;
    BigInteger number;

    static public boolean USE_THREADS = false;
    static public boolean PI_PRECOMPUTED = false;

    static final private int FOURIER_MULTIPLICATION_THRESHOLD = 10000;

    private void initFields(BigInteger digits, int exp) {
        exponent = exp;
        number = digits;
    }

    public BigFloat() {
        initFields(BigInteger.ZERO, 0);
    }

    public BigFloat(BigInteger number, int exponent) {
        initFields(number, exponent);
    }

    public BigFloat(BigInteger number) {
        initFields(number, 0);
    }

    public BigFloat(long val) {
        initFields(BigInteger.valueOf(val), 0);
    }

    public BigFloat(long val, int exponent) {
        initFields(BigInteger.valueOf(val), exponent);
    }

    public BigFloat(BigFloat other) {
        initFields(other.number, other.exponent);
    }

    public BigFloat add(BigFloat other) {
        if (other == null) {
            return new BigFloat(this);
        }
        if (exponent > other.exponent) {
            return new BigFloat(number.add(other.number.shiftLeft(exponent - other.exponent)), exponent);
        }
        else {
            return new BigFloat(number.shiftLeft(other.exponent - exponent).add(other.number), other.exponent);
        }
    }

    public BigFloat subtract(BigFloat other) {
        if (other == null) {
            return new BigFloat(this);
        }
        if (exponent > other.exponent) {
            return new BigFloat(number.subtract(other.number.shiftLeft(exponent - other.exponent)), exponent);
        }
        else {
            return new BigFloat(number.shiftLeft(other.exponent - exponent).subtract(other.number), other.exponent);
        }
    }

    public BigFloat multiplyBuiltIn(BigFloat other) throws NullPointerException {
        if (other == null)
            throw new NullPointerException();
        return new BigFloat(number.multiply(other.number), exponent + other.exponent);
    }

    public BigFloat multiplyFourier(BigFloat other) throws NullPointerException {
        if (other == null) {
            throw new NullPointerException();
        }
        // Необходимо подобрать такие значения:
        // 2n <= n*2^k < 4n
        // k >= 7
        // Желательно, чтобы l mod 8 = 0
        int n = Math.max(number.bitLength(), other.number.bitLength());
        int k = 7;
        int l = (n > 100000) ? 16 : 8;
        while (l * (1 << k) < 2 * n) {
            k++;
        }
        int precision = 4 * k + 2 * l;
        FFT fft = new FFT(k, precision);
        FFT.FFTInstance f1 = fft.getFFTInstance(number.abs().toByteArray(), l);
        FFT.FFTInstance f2 = fft.getFFTInstance(other.number.abs().toByteArray(), l);
        ArrayList<ComplexNumber> res1 = f1.transform(false);
        ArrayList<ComplexNumber> res2 = f2.transform(false);
        for (int i = 0; i < res1.size(); i++) {
            res1.set(i, res1.get(i).multiply(res2.get(i)));
        }
        f1.setElements(res1);
        res2 = f1.transform(true);
        BigInteger res = BigInteger.valueOf(0);
        int shift = 0;
        for (ComplexNumber complexNumber : res2) {
            BigInteger t = complexNumber.getReal().multiplyByPowerOfTwo(2 * k + 2 * l).round(0, 0).number;
            res = res.add(t.shiftLeft(shift));
            //System.out.println(res.toString(16));
            shift += l;
        }
        if (number.signum() * other.number.signum() < 0)
            res = res.negate();
        return new BigFloat(res, exponent + other.exponent);
    }

    public BigFloat multiply(BigFloat other) throws NullPointerException {
        if (other == null) {
            throw new NullPointerException();
        }
        if (false && Math.max(number.bitLength(), other.number.bitLength())
                > FOURIER_MULTIPLICATION_THRESHOLD) {
            return multiplyFourier(other);
        }
        else {
            return multiplyBuiltIn(other);
        }
    }

    /**
     * Округление значения по модулю в лучшую сторону.
     * @param precision точность в битах после двоичной точки.
     * @return округленное значение.
     */
    public BigFloat round(int precision) {
        return round(precision, -1);
    }

    /**
     * Округление по модулю до нужного бита после точки в указанную сторону.
     * @param precision точность в битах после двоичной точки.
     * @param where 1 если округление вверх, -1 если вниз, 0 если в лучшую сторону.
     * @return округленное значение.
     */
    public BigFloat round(int precision, int where) {
        if (exponent < precision || (where <= 0 && exponent == precision)) {
            return new BigFloat(this);
        }
        if (where == 0) {
            int n = valuableBits();
            //число нулевое
            if (n == 0) {
                return new BigFloat(0);
            }
            int shift = exponent - precision;
            if (n < shift) {
                return new BigFloat(0);
            }
            boolean increment = number.testBit(shift - 1);
            BigInteger t = number.shiftRight(shift);
            if (increment) {
                t = t.add(BigInteger.valueOf(1));
            }
            return new BigFloat(t, precision);
        }
        BigInteger t = number.shiftRight(exponent - precision);
        if (where < 0) {
            return new BigFloat(t, precision);
        }
        else {
            return new BigFloat(t.add(BigInteger.ONE), precision);
        }
    }

    public BigFloat multiplyByPowerOfTwo(int power) {
        return new BigFloat(number, exponent - power);
    }

    /**
     * Вычисление 1/a используя итерацию по Ньютону: <br>
     * e[i] = a*x[i] - 1 <br>
     * x[i+1] = x[i] - x[i]*e[i]
     * @param precision точность числа в битах дробной части.
     * @return оценку 1/a с указанной точностью.
     * @throws ArithmeticException если a = 0.
     */
    public BigFloat inverse(int precision) throws ArithmeticException {
        int n = valuableBits();
        if (n == 0) {
            throw new ArithmeticException("Zero division");
        }
        // Число имеет вид 2^(-e)
        if (n == 1) {
            return new BigFloat(1, -exponent);
        }
        // рассчитаем изначальную оценку
        // x0 = 1/4 * floor(32 / (4*a0 + 2*a1 + a2))
        int initial = 0;
        for (int i = 3; i >= 1; i--) {
            if (n - i >= 0 && number.testBit(n - i))
                initial += 1 << (3 - i);
        }
        BigFloat x = new BigFloat(32 / initial, 2);
        BigFloat a = new BigFloat(number.abs(), n);
        int k = 0;
        BigFloat y;
        while (precision > (1 << k) + n - exponent) {
            y = x.multiply(x).multiply(a.round((1 << k + 1) + 3));
            x = x.multiplyByPowerOfTwo(1).subtract(y);
            x = x.round(1 << k + 1);
            k++;
        }
        x = x.multiplyByPowerOfTwo(exponent - n);
        if (number.signum() < 0) {
            return x.negate();
        }
        else {
            return x;
        }
    }

    public BigFloat negate() {
        return new BigFloat(number.negate(), exponent);
    }

    public BigFloat divide(BigFloat other, int precision) throws ArithmeticException {
        if (other == null) {
            throw new ArithmeticException("Zero division");
        }
        return multiply(other.inverse(precision)).round(precision);
    }

    /**
     * Вычисление a^(-1/2) используя итерацию по Ньютону третьего порядка. <br>
     * e[i] = a*x[i]^2 - 1 <br>
     * x[i+1] = x[i] - 1/2*x[i](e[i] - 3/4*e[i]^2)
     * @param precision точность в битах после двоичной точки.
     * @return a^(-1/2) с заданной точностью
     * @throws ArithmeticException если a <= 0.
     */
    public BigFloat inverseSqrt(int precision) throws ArithmeticException {
        int sign = number.signum();
        if (sign < 0) {
            throw new ArithmeticException("Root of negative number");
        }
        if (sign == 0) {
            throw new ArithmeticException("Zero division");
        }
        int n = valuableBits();
        //нормализуем a, чтобы a < 1, при этом умножая на четную степень 2
        int newExponent = ((n + exponent) % 2 == 0) ? n : n + 1;
        int postExponent = (newExponent - exponent) / 2;
        BigFloat a = new BigFloat(number, newExponent);
        // Используем следующую начальную оценку
        // a = 0.a1a2a3a4...
        // a1 a2 a3 a4 | x
        //  1  1  *  * | 1.01
        //  1  0  1  * | 1.011
        //  1  0  0  * | 1.1
        //  0  1  1  * | 1.1011
        //  0  1  0  1 | 1.1111
        //  0  1  0  0 | 10.001
        //  0  0  *  * - не может быть, так как a нормализовано
        // Такая оценка дает 1 верную цифру после запятой для
        // e[0] = a*x^2 - 1
        BigInteger firstFour = null;
        if (n < 4)
            firstFour = number.shiftLeft(4 - n);
        else
            firstFour = number.shiftRight(n - 4);
        int initial = 0;
        if (firstFour.testBit(3)) {
            if (firstFour.testBit(2)) {
                initial = 20;
            }
            else {
                if (firstFour.testBit(1)) {
                    initial = 22;
                }
                else {
                    initial = 24;
                }
            }
        }
        else {
            if (firstFour.testBit(1)) {
                initial = 27;
            }
            else {
                if (firstFour.testBit(0)) {
                    initial = 30;
                }
                else {
                    initial = 34;
                }
            }
        }
        if (n == 1) {
            // У нас число 2^(-2k), его обратный квадратный корень
            // тривиален и равен 2^k
            if (exponent % 2 == 0) {
                return new BigFloat(1, -(exponent / 2));
            }
            // иначе мы получим при нормализации 0.10,
            // значит  x = 1.1, a  initial = 3
        }
        BigFloat x = new BigFloat(initial, 4);
        BigFloat e;
        BigFloat y;
        BigFloat one = new BigFloat(1);
        BigFloat threeFourth = new BigFloat(3, 2);
        int currentPrecision = 1;
        while (precision << 1 > currentPrecision + postExponent) {
            e = x.multiply(x).multiply(a.round(precision + 1)).subtract(one);
            y = e.multiply(e).multiply(threeFourth);
            y = one.subtract(e.multiplyByPowerOfTwo(-1)).add(y);
            currentPrecision *= 2;
            x = x.multiply(y).round(currentPrecision);
        }
        return x.multiplyByPowerOfTwo(-postExponent).round(precision);
    }

    int valuableBits() {
        int n = number.bitLength();
        while (n >= 1 && !number.testBit(n - 1)) {
            n--;
        }
        return n;
    }

    public int wholeBits() {
        return valuableBits() - exponent;
    }

    public int precision() {
        return 2 * exponent - valuableBits();
    }

    public int floorOfLog2() {
        int n = valuableBits();
        if (n == 0) {
            return Integer.MIN_VALUE;
        }
        return n - exponent - 1;
    }

    public BigInteger floor() {
        return number.shiftRight(exponent);
    }

    public BigFloat sqrt(int precision) {
        int sign = number.signum();
        if (sign < 0) {
            throw new ArithmeticException("Root of negative number");
        }
        if (sign == 0) {
            return new BigFloat();
        }
        return multiply(inverseSqrt(precision)).round(precision);
    }

    static public BigFloat pi(int precision) {
        return PiCalculation.GaussLegendrePi(precision);
    }

    public BigFloat ln(int precision) {
        int sign = number.signum();
        if (sign <= 0) {
            throw new ArithmeticException("Ln domain error!");
        }
        int n = floorOfLog2();
        if (n * 2 > precision) {
            return lnCaseBig(precision, null);
        }
        if (n >= 0) {
            int logN = 0;
            while (1 << logN + 1 < precision) logN++;
            BigFloat delta = subtract(new BigFloat(1));
            // y = 1 + delta, а delta мало, тогда
            // используем степенной ряд
            int logD = delta.floorOfLog2();
            if (logD == Integer.MIN_VALUE) {
                return new BigFloat(0);
            }
            if (logD * logN < -precision) {
                BigFloat res = delta;
                BigFloat deltaPower = delta;
                for (int i = 0; i <= logN; i++) {
                    deltaPower = deltaPower.multiply(delta);
                    if (i % 2 == 0) {
                        res = res.subtract(deltaPower.divide(new BigFloat(i + 2), precision));
                    }
                    else {
                        res = res.add(deltaPower.divide(new BigFloat(i + 2), precision));
                    }
                }
                return res;
            }
            int m = precision / 2 - n + 1;
            BigFloat y = multiplyByPowerOfTwo(m);
            BigFloat[] logs = new BigFloat[2];
            Thread ln2Calculator = null;
            if (USE_THREADS) {
                int finalLogN = logN;
                ln2Calculator = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        logs[1] = ln2(precision + finalLogN);
                    }
                });
                ln2Calculator.start();
            }
            logs[0] = y.ln(precision);
            if (USE_THREADS) {
                try {
                    assert ln2Calculator != null;
                    ln2Calculator.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                logs[1] = ln2(precision + logN);
            }
            logs[1] = logs[1].multiply(new BigFloat(m));
            //System.out.println("log0 = " + logs[0].toString(10));
            //System.out.println("log1 = " + logs[1].toString(10));
            return logs[0].subtract(logs[1]);
        }
        return inverse(precision).ln(precision).negate();
    }

    /**
     * Логарифм числа в случае, если число больше 2^(n/2),
     * где n -- желаемая точность. Для вычисления используем
     * A-G итерацию. <br>
     * log(y) = pi/2a(1 + O(y^(-2))), a[i] -> a.
     * @param precision точность вычисления.
     * @param Pi значение числа пи, если оно было вычислено ранее. Если нет, то нужно передать null.
     * @return натуральный логарифм числа.
     */
    BigFloat lnCaseBig(int precision, BigFloat Pi) {
        //System.out.println("Big log");
        BigFloat[] pi = new BigFloat[]{null};
        Thread piCalculator = null, bCalculator = null;
        boolean piPrecalculated = false;
        if (Pi != null) {
            pi[0] = Pi;
            piPrecalculated = true;
        }
        else if(USE_THREADS) {
            piCalculator = new Thread(new Runnable() {
                @Override
                public void run() {
                    pi[0] = BigMath.pi(precision, false);
                }
            });
            piCalculator.start();
        }
        else {
            pi[0] = BigMath.pi(precision, false);
        }
        // a = 1
        // b = 4/y
        BigFloat[] ab = new BigFloat[]{new BigFloat(1), inverse(precision).multiplyByPowerOfTwo(2)};
        BigFloat prevValue = ab[0].inverse(precision);
        BigFloat currentValue = null;
        int currentPrecision = 0;
        while (currentPrecision < precision && currentPrecision != Integer.MIN_VALUE) {
            BigFloat tempB = ab[1];
            BigFloat tempA = ab[0];
            if (USE_THREADS) {
                bCalculator = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ab[1] = ab[0].multiply(ab[1]).sqrt(precision);
                    }
                });
                bCalculator.start();
            }
            else {
                ab[1] = ab[0].multiply(ab[1]).sqrt(precision);
            }
            tempA = tempA.add(tempB).multiplyByPowerOfTwo(-1);
            currentValue = tempA.inverse(precision);
            currentPrecision = -currentValue.subtract(prevValue).floorOfLog2();
            prevValue = currentValue;
            if (USE_THREADS) {
                try {
                    assert bCalculator != null;
                    bCalculator.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ab[0] = tempA;
            //System.out.println(currentValue.toString(10));
        }
        if (USE_THREADS && !piPrecalculated) {
            try {
                assert piCalculator != null;
                piCalculator.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return pi[0].multiply(currentValue).multiplyByPowerOfTwo(-1).round(precision);
    }

    static public BigFloat ln2(int precision) {
        int n = precision + 1;
        BigFloat y = new BigFloat(1, -n);
        y = y.lnCaseBig(precision, null);
        return y.divide(new BigFloat(n), precision);
    }

    public String toString(int base) {
        if (base == 2) {
            return toString();
        }
        StringBuilder sb = new StringBuilder();
        if (number.signum() < 0) {
            sb.append('-');
            sb.append(negate().toString(base));
            return sb.toString();
        }
        BigInteger wholePart = number.shiftRight(exponent);
        sb.append(wholePart.toString(base)).append('.');
        BigFloat fraction = new BigFloat(number.xor(wholePart.shiftLeft(exponent)), exponent);
        BigFloat Base = new BigFloat(base);
        BigFloat multiplier = new BigFloat(1);
        int max = (int) Math.ceil(exponent * Math.log(2) / Math.log(base)) + 1;
        for (int i = 0; i < max; i++) {
            multiplier = multiplier.multiply(Base);
        }
        fraction = fraction.multiply(multiplier);
        String fractionString = fraction.floor().toString(base);
        int zeros = max - fractionString.length();
        for (int i = 0; i < zeros; i++) {
            sb.append('0');
        }
        sb.append(fractionString);
        return sb.toString();
    }

    public BigFloat exp(int precision) {
        return expNewton(precision);
    }

    /**
     * Вычисления exp(x) с помощью поиска корня
     * функции  f(y) = log(y) - x.
     * @param precision точность результата в битах после двоичной точки.
     * @return значение показательной функции в этой точке.
     */
    private BigFloat expNewton(int precision) {
        BigFloat one = new BigFloat(1);
        // изначальная оценка y = 1 + x
        BigFloat y = one;
        BigFloat prevValue = y;
        int currentPrecision = 0;
        int k = 8;
        while (currentPrecision < precision && currentPrecision != Integer.MIN_VALUE) {
            BigFloat log = y.ln(precision * 2);
            //System.out.println("Log = " + log.toString(10));
            BigFloat dy = this.subtract(log);
            //System.out.println(dy.toString(10));
            BigFloat power = one;
            BigFloat sum = one;
            BigFloat factorial = new BigFloat(1);
            for (int i = 0; i < k; i++) {
                factorial = factorial.multiply(new BigFloat(i + 1));
                power = power.multiply(dy);
                sum = sum.add(power.divide(factorial, precision * 2));
            }
            y = y.multiply(sum).round(precision * 2);
            System.out.println(y.toString(10));
            currentPrecision = -y.subtract(prevValue).floorOfLog2();
            prevValue = y;
            System.out.println(currentPrecision);
        }
        return y;
    }

    private BigFloat expTaylor(int precision) {
        BigFloat sum = new BigFloat(1);
        BigFloat factorial = new BigFloat(1);
        BigFloat power = new BigFloat(1);
        int iteration = 1;
        PrecisionChecker pc = new PrecisionChecker(precision, sum);
        do {
            factorial = factorial.multiply(new BigFloat(iteration));
            power = power.multiply(this).round(precision);
            sum = sum.add(power.divide(factorial, precision));
            iteration ++;
        } while (!pc.check(sum));
        return sum;
    }

    @Override
    public String toString() {
        return toString(10);
        //return number.toString(16) + "^" + (-exponent);
    }

}
