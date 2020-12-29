package ru.spbstu.telematics.java;


import java.io.*;

/**
 * Длинная арифметика с заданной точностью.
 * Методы аналогичны методам класса {@link BigFloat},
 * но они удобнее и используется сохранение вычисленных
 * констант пи и ln2.
 */
public class BigMath {
    /**
     * Нулевое число в формате BigFloat.
     */
    static public final BigFloat ZERO = new BigFloat(0);
    /**
     * Единица в формате BigFloat.
     */
    static public final BigFloat ONE = new BigFloat(1);

    /**
     * Число, соответствующее переданному значению.
     * @param val представление числа в формате long.
     * @return представление числа в формате BigFloat.
     */
    public static BigFloat valueOf(long val) {
        return new BigFloat(val);
    }

    /**
     * Точность по умолчанию, если при вызове функции
     * не указывается точность, операция будет выполенена
     * с такой точностью.
     */
    private static int defaultPrecision = 64;

    public static int getDefaultPrecision() {
        return defaultPrecision;
    }

    public static void setDefaultPrecision(int defaultPrecision) {
        BigMath.defaultPrecision = defaultPrecision;
    }

    /**
     * Путь к файлу, в котором хранится число пи.
     */
    final private static String piFilePath = "_Big_math_pi.bf";
    /**
     * Точность последнего сохраненного значение пи.
     */
    private static int lastPiPrecision = -1;

    /**
     * Флаг, который позволяет сохранить пи в памяти.
     */
    static private boolean SAVE_PI_IN_RAM = false;
    /**
     * Сохраненное значение пи в памяти.
     * Не null только если SAVE_PI_IN_RAM = true.
     */
    static private BigFloat PI = null;

    static public void savePiInRam(boolean save) {
        if (save) {
            SAVE_PI_IN_RAM = save;
        }
        else {
            SAVE_PI_IN_RAM = save;
            PI = null;
        }
    }

    static public boolean isSavingPiInRam() {
        return SAVE_PI_IN_RAM;
    }

    /**
     * Путь к файлу, в котором хранится ln2.
     */
    final private static String ln2FilePath = "_Big_math_ln2.bf";
    /**
     * Точность последнего сохраненного значения ln2.
     */
    private static int lastLn2Precision = -1;

    /**
     * True, если используется многопоточность.
     */
    private static boolean usingThreads = true;

    public static boolean isUsingThreads() {
        return usingThreads;
    }

    public static void setUsingThreads(boolean usingThreads) {
        BigMath.usingThreads = usingThreads;
    }

    /**
     * Сумма чисел a и b без округления.
     * @param a левый операнд.
     * @param b правый операнд.
     * @return a + b без округления.
     */
    static public BigFloat addExact(BigFloat a, BigFloat b) {
        if (a != null) {
            return a.add(b);
        }
        if (b != null)
            return b.add(a);
        return ZERO;
    }

    /**
     * Сумма чисел a и b с округлением до точности precision.
     * @param a левый операнд.
     * @param b правый операнд.
     * @param precision точность суммы.
     * @return a + b с точностью precision.
     */
    static public BigFloat add(BigFloat a, BigFloat b, int precision) {
        return addExact(a, b).round(precision);
    }

    /**
     * Сумма чисел a и b с окргулением до точности defaultPrecision.
     * @param a левый операнд.
     * @param b правый операнд.
     * @return a + b с точностью defaultPrecision.
     * @see BigMath#defaultPrecision
     */
    static public BigFloat add(BigFloat a, BigFloat b) {
        return add(a, b, defaultPrecision);
    }

    /**
     * Разность чисел a и b без округления.
     * @param a левый операнд.
     * @param b правый операнд.
     * @return a - b без округления.
     */
    static public BigFloat subtractExact(BigFloat a, BigFloat b) {
        if (a != null) {
            return a.subtract(b);
        }
        if (b != null)
            return b.negate();
        return ZERO;
    }

    /**
     * Разность чисел a и b с округлением до точности precision.
     * @param a левый операнд.
     * @param b правый операнд.
     * @param precision точность разности.
     * @return a - b с точностью precision.
     */
    static public BigFloat subtract(BigFloat a, BigFloat b, int precision) {
        return subtractExact(a, b).round(precision);
    }

    /**
     * Разность чисел a и b с окргулением до точности defaultPrecision.
     * @param a левый операнд.
     * @param b правый операнд.
     * @return a - b с точностью defaultPrecision.
     * @see BigMath#defaultPrecision
     */
    static public BigFloat subtract(BigFloat a, BigFloat b) {
        return subtract(a, b, defaultPrecision);
    }

    /**
     * Произведение чисел a и b без округления.
     * @param a левый операнд.
     * @param b правый операнд.
     * @return a * b без округления.
     */
    static public BigFloat multiplyExact(BigFloat a, BigFloat b) {
        if (a == null || b == null)
            return ZERO;
        return a.multiply(b);
    }

    /**
     * Произведение чисел a и b с округлением до точности precision.
     * @param a левый операнд.
     * @param b правый операнд.
     * @param precision точность произведения.
     * @return a * b с точностью precision.
     */
    static public BigFloat multiply(BigFloat a, BigFloat b, int precision) {
        return multiplyExact(a, b).round(precision);
    }

    /**
     * Произведение чисел a и b с окргулением до точности defaultPrecision.
     * @param a левый операнд.
     * @param b правый операнд.
     * @return a * b с точностью defaultPrecision.
     * @see BigMath#defaultPrecision
     */
    static public BigFloat multiply(BigFloat a, BigFloat b) {
        return multiply(a, b, defaultPrecision);
    }

    static public BigFloat multiplyByPowerOfTwo(BigFloat a, int power) {
        if (a == null) {
            return ZERO;
        }
        return a.multiplyByPowerOfTwo(power);
    }

    /**
     * Обратное число для a, то есть 1/a.
     * @param a число, для которого ищем обратное.
     * @param precision точность результата.
     * @return 1 / a с точностью precision.
     */
    static public BigFloat inverse(BigFloat a, int precision) {
        if (a == null) {
            throw new ArithmeticException("Zero division");
        }
        return a.inverse(precision);
    }

    /**
     * Обратное число для a, то есть 1/a.
     * @param a число, для которого ищем обратное.
     * @return 1 / a с точностью defaultPrecision.
     * @see BigMath#defaultPrecision
     */
    static public BigFloat inverse(BigFloat a) {
        return inverse(a, defaultPrecision);
    }

    /**
     * Частное a и b с точностью precision.
     * @param a делимое.
     * @param b делитель.
     * @param precision точность результата.
     * @return a/b с указанной точностью.
     */
    static public BigFloat divide(BigFloat a, BigFloat b, int precision) {
        if (a == null) {
            return ZERO;
        }
        return a.divide(b, precision);
    }

    /**
     * Частное a и b с точностью defaultPrecision.
     * @param a делимое.
     * @param b делитель.
     * @return a/b с точностью defaultPrecision.
     * @see BigMath#defaultPrecision
     */
    static public BigFloat divide(BigFloat a, BigFloat b) {
        return divide(a, b, defaultPrecision);
    }

    /**
     * Округление числа a до точности precision.
     * @param a число, которое округляем.
     * @param precision точность.
     * @return результат округления.
     */
    static public BigFloat round(BigFloat a, int precision) {
        if (a == null)
            return ZERO;
        return a.round(precision);
    }

    /**
     * Модуль числа a. Результат не округляется.
     * @param a число, модуль которого вычисляется.
     * @return модуль числа a.
     */
    static public BigFloat abs(BigFloat a) {
        if (a == null)
            return ZERO;
        return new BigFloat(a.number.abs(), a.exponent);
    }

    /**
     * Противоположное число, то есть -a. Результат не округляется.
     * @param a число, для которого ищется противоположное.
     * @return противоположное для числа а.
     */
    static public BigFloat negate(BigFloat a) {
        if (a == null) {
            return ZERO;
        }
        return a.negate();
    }

    /**
     * Обратное число квадратному корню из а, то есть a^(-1/2).
     * Результат округлен до точности precision.
     * Вычисляется быстрее, чем квадратный корень.
     * @param a число, из которого извлекаем обратный квадратный корень.
     * @param precision точность результата.
     * @return a^(-1/2) с точностью precision.
     */
    static public BigFloat inverseSqrt(BigFloat a, int precision) {
        if (a == null) {
            throw new ArithmeticException("Zero division");
        }
        return a.inverseSqrt(precision);
    }

    /**
     * Обратное число квадратному корню из а, то есть a^(-1/2).
     * Результат округлен до точности defaultPrecision.
     * Вычисляется быстрее, чем квадратный корень.
     * @param a число, из которого извлекаем обратный квадратный корень.
     * @return a^(-1/2) с точностью defaultPrecision.
     * @see BigMath#defaultPrecision
     */
    static public BigFloat inverseSqrt(BigFloat a) {
        return inverseSqrt(a, defaultPrecision);
    }

    /**
     * Квадратный корень из a с точностью precision.
     * Если нужно вычислить обратный квадратный корень,
     * быстрее будет использовать метод {@link BigMath#inverseSqrt(BigFloat, int)}.
     * @param a число, из которого извлекаем квадратный корень.
     * @param precision точность результата.
     * @return квадратный корень из a с точностью precision.
     */
    static public BigFloat sqrt(BigFloat a, int precision) {
        if (a == null) {
            return ZERO;
        }
        return a.sqrt(precision + 2);
    }

    /**
     * Квадратный корень из a с точностью defaultPrecision.
     * Если нужно вычислить обратный квадратный корень,
     * быстрее будет использовать метод {@link BigMath#inverseSqrt(BigFloat)}.
     * @param a число, из которого извлекаем квадратный корень.
     * @return квадратный корень из a с точностью defaultPrecision.
     * @see BigMath#defaultPrecision
     */
    static public BigFloat sqrt(BigFloat a) {
        return sqrt(a, defaultPrecision);
    }

    /**
     * Возвращает знак числа a.
     * @param a число, знак которого вычисляется.
     * @return 1, если a > 0, возвращает 0, если a = 0, иначе -1
     */
    static public int sign(BigFloat a) {
        if (a == null) {
            return 0;
        }
        return a.number.signum();
    }

    /**
     * Возвращает целую часть логарифма модуля a по основанию 2.
     * Функция очень быстрая, так как вычисляется количество значащих битов.
     * @param a число, для которого ищем целую часть логарифма модуля.
     * @return логарифм модуля числа a по основанию 2, если a = 0, возвращается
     * {@link Integer#MIN_VALUE}.
     */
    static public int fLg(BigFloat a) {
        if (a == null) {
            return Integer.MIN_VALUE;
        }
        return a.floorOfLog2();
    }

    /**
     * Значение числа пи с указанной точностью.
     * @param precision точность числа пи.
     * @param forceCalculation если false, то будет произвдена попытка
     *                         загрузить число пи из файла.
     * @return значения числа пи с указанной точностью.
     */
    static public BigFloat pi(int precision, boolean forceCalculation) {
        if (!forceCalculation) {
            if (SAVE_PI_IN_RAM && PI != null && PI.precision() >= precision) {
                return PI;
            }
            else if (getCalculatedPrecision(true) >= precision) {
                BigFloat pi;
                try {
                    pi = loadConstant(precision, true);
                } catch (IOException e) {
                    pi = piCalculation(precision);
                }
                return pi;
            }
        }
        return piCalculation(precision);
    }

    /**
     * @return число пи с точностью {@link BigMath#defaultPrecision}.
     */
    static public BigFloat pi() {
        return pi(defaultPrecision, false);
    }

    /**
     * Непосредственное вычисление числа пи.
     * @param precision точность вычисления.
     * @return значение числа пи.
     */
    private static BigFloat piCalculation(int precision) {
        boolean t = PiCalculation.USE_THREADS;
        PiCalculation.USE_THREADS = usingThreads;
        BigFloat pi = PiCalculation.BB4(precision * 3 / 2 + 10);
        PiCalculation.USE_THREADS = t;
        if (SAVE_PI_IN_RAM) {
            if (PI == null || precision > PI.precision()) {
                PI = pi;
            }
        }
        if (precision > lastPiPrecision) {
            try {
                OutputStream outputStream = new FileOutputStream(piFilePath);
                BigFloatWriter writer = new BigFloatWriter(outputStream);
                writer.writeBinary(pi);
                lastPiPrecision = precision;
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pi;
    }

    /**
     * Загружает число пи из файла и возвращает результат.
     * @param precision точность загружаемого числа.
     * @return число пи с указанной точностью.
     * @throws IOException если возникают проблемы с чтением файла.
     */
    private static BigFloat loadConstant(int precision, boolean pi) throws IOException {
        InputStream inputStream;
        inputStream = new FileInputStream((pi) ? piFilePath : ln2FilePath);
        BigFloatReader reader = new BigFloatReader(inputStream);
        BigFloat x = reader.read(precision + 3);
        inputStream.close();
        if (pi && SAVE_PI_IN_RAM && (PI == null || precision > PI.precision())) {
            PI = x;
        }
        return x;
    }

    /**
     * @return возвращает точность сохраненного значения числа пи.
     * Если число нельзя прочитать будет возвращено -1.
     */
    private static int getCalculatedPrecision(boolean pi) {
        if (pi) {
            if (lastPiPrecision > 0)
                return lastPiPrecision;
        }
        else if (lastLn2Precision > 0) {
            return lastLn2Precision;
        }
        InputStream inputStream;
        int precision;
        try {
            inputStream = new FileInputStream((pi) ? piFilePath : ln2FilePath);
            BigFloatReader reader = new BigFloatReader(inputStream);
            precision = reader.readPrecision();
            inputStream.close();
        } catch (IOException e) {
            return -1;
        }
        if (pi)
            lastPiPrecision = precision;
        else
            lastLn2Precision = precision;
        return precision;
    }

    /**
     * Вычисление логарифма числа 2 по основанию e с точностью precision.
     * @param precision точность результата.
     * @param forceCalculation если true, результат будет точно вычислен заново,
     *                         иначе результат может быть загружен из файла.
     * @return логарифм числа 2 по основанию e с точностью precision.
     */
    public static BigFloat ln2(int precision, boolean forceCalculation) {
        if (!forceCalculation && getCalculatedPrecision(false) >= precision) {
            BigFloat ln2;
            try {
                ln2 = loadConstant(precision, false);
            } catch (IOException e) {
                ln2 = calculateLn2(precision);
            }
            return ln2;
        }
        return calculateLn2(precision);
    }

    /**
     * Вычисление логарифма числа 2 по основанию e с точностью {@link BigMath#defaultPrecision}.
     * @return логарифм числа 2 по основанию e с точностью {@link BigMath#defaultPrecision}.
     */
    public static BigFloat ln2() {
        return ln2(defaultPrecision, false);
    }

    /**
     * Вычисление логарифма 2 по основанию e с точностью precision.
     * Метод записывает в файл более точное значение.
     * @param precision точность результата.
     * @return логарифм 2 по основанию е с точностью precision.
     */
    private static BigFloat calculateLn2(int precision) {
        BigFloat.USE_THREADS = usingThreads;
        BigFloat pi = null;
        if (getCalculatedPrecision(true) >= precision) {
            pi = pi(precision, false);
        }
        int n = precision + 1;
        BigFloat y = new BigFloat(1, -n);
        y = y.lnCaseBig(precision, pi);
        BigFloat res = divide(y, valueOf(n), precision);
        if (getCalculatedPrecision(false) < precision) {
            try {
                OutputStream outputStream = new FileOutputStream(ln2FilePath);
                BigFloatWriter writer = new BigFloatWriter(outputStream);
                writer.writeBinary(res);
                lastPiPrecision = precision;
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * Вычисление логарифма числа a по основанию e с точностью precision.
     * @param a число, логарифм которого вычисляется.
     * @param precision точность логарифма.
     * @return логарифм числа а по основанию е с точностью precision.
     */
    public static BigFloat ln(BigFloat a, int precision) {
        assertLogDomain(a, false, precision);
        int n = fLg(a);
        if (n * 2 > precision) {
            return a.lnCaseBig(precision, null);
        }
        if (n >= 0) {
            int logPrecision = 0;
            while (1 << logPrecision + 1 < precision) logPrecision ++;
            BigFloat delta = subtractExact(a, ONE);
            int logD = fLg(delta);
            if (logD == Integer.MIN_VALUE) return ZERO;
            if (logD * logPrecision < -precision) {
                BigFloat sum = delta;
                BigFloat deltaPower = delta;
                PrecisionChecker pc = new PrecisionChecker(precision, delta);
                do {
                    deltaPower = multiply(deltaPower, delta, precision);
                    if (pc.getIteration() % 2 == 0) {
                        sum = subtractExact(sum, divide(deltaPower, valueOf(pc.getIteration() + 2), precision));
                    }
                    else {
                        sum = add(sum, divide(deltaPower, valueOf(pc.getIteration() + 2), precision));
                    }
                } while (!pc.check(sum));
                return sum;
            }
            int m = precision / 2 - n + 1;
            BigFloat y = multiplyByPowerOfTwo(a, m);
            return subtract(y.lnCaseBig(precision, null),
                    ln2(precision + logPrecision, false).
                            multiply(valueOf(m)), precision);
        }
        return negate(ln(inverse(a, precision), precision));
    }

    /**
     * Вычисление логарифма числа а по основанию е с точностью {@link BigMath#defaultPrecision}.
     * @param a число, логарифм которого вычисляем.
     * @return логарифм числа а по основанию е с точностью {@link BigMath#defaultPrecision}.
     */
    static public BigFloat ln(BigFloat a) {
        return ln(a, defaultPrecision);
    }

    /**
     * Вычисление логарифма а по основанию base с точностью precision.
     * @param a аргумент логарифма.
     * @param base основание логарифма.
     * @param precision точность результата.
     * @return логарифм а по основанию base с точностью precision.
     */
    static public BigFloat log(BigFloat a, BigFloat base, int precision) {
        assertLogDomain(a, false, precision);
        assertLogDomain(base, true, precision);
        BigFloat[] logs = new BigFloat[]{null, null};
        Thread baseCalculator = null;
        if (usingThreads) {
            baseCalculator = new Thread(new Runnable() {
                @Override
                public void run() {
                    logs[1] = ln(base, precision);
                }
            });
            baseCalculator.start();
        }
        else {
            logs[1] = ln(base, precision);
        }
        logs[0] = ln(a, precision);
        if (usingThreads) {
            try {
                assert baseCalculator != null;
                baseCalculator.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return divide(logs[0], logs[1], precision);
    }

    /**
     * Вычисление логарифма а по основанию base с точностью {@link BigMath#defaultPrecision}.
     * @param a аргумент логарифма.
     * @param base основание логарифма.
     * @return логарифм а по основанию base с точностью {@link BigMath#defaultPrecision}.
     */
    static public BigFloat log(BigFloat a, BigFloat base) {
        return log(a, base, defaultPrecision);
    }

    /**
     * Проверка области определения логарифма.
     * Выкидывает {@link ArithmeticException}, если
     * аргумент не подходит под определения логарифма.
     * @param a аргумент, который подлежит проверке.
     * @param base является ли этот аргумент основанием.
     * @param precision точность, с которой работаем.
     */
    static private void assertLogDomain(BigFloat a, boolean base, int precision) {
        if (sign(a) <= 0 || base && fLg(subtract(a, ONE, precision)) < -precision) {
            throw new ArithmeticException("Log domain error!");
        }
    }

    /**
     * Вычисление экспоненты числа a с точностью precision.
     * @param a показатель экспоненты.
     * @param precision точность результата.
     * @return экспонента числа а с точностью precision.
     */
    static public BigFloat exp(BigFloat a, int precision) {
        ExpCalculator ec = new ExpCalculator(precision, usingThreads);
        return ec.calculate(a).round(precision);
    }

    /**
     * Вычисление экспоненты числа a с точностью {@link BigMath#defaultPrecision}.
     * @param a показатель экспоненты.
     * @return экспонента числа а с точностью {@link BigMath#defaultPrecision}.
     */
    static public BigFloat exp(BigFloat a) {
        return exp(a, defaultPrecision);
    }

    /**
     * Вычисление base в степени exponent с точностью precision.
     * @param base основание степени.
     * @param exponent показатель степени.
     * @param precision точность результата.
     * @return base в степени exponent с точностью precision.
     */
    static public BigFloat pow(BigFloat base, BigFloat exponent, int precision) {
        BigFloat y = BigMath.multiply(BigMath.ln(base, precision), exponent, precision);
        return BigMath.exp(y, precision);
    }

    /**
     * Вычисление base в степени exponent с точностью {@link BigMath#defaultPrecision}.
     * @param base основание степени.
     * @param exponent показатель степени.
     * @return base в степени exponent с точностью {@link BigMath#defaultPrecision}.
     */
    static public BigFloat pow(BigFloat base, BigFloat exponent) {
        return pow(base, exponent, defaultPrecision);
    }
}
