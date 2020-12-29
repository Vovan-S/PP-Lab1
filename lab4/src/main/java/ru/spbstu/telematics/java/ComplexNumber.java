package ru.spbstu.telematics.java;

import java.math.BigInteger;

public class ComplexNumber {
    private BigFloat real;
    private BigFloat imaginary;

    private void initFields(BigFloat r, BigFloat i) {
        real = r;
        imaginary = i;
    }

    public ComplexNumber() {
        initFields(new BigFloat(0), new BigFloat(0));
    }

    public ComplexNumber(BigFloat Re, BigFloat Im) {
        initFields(Re, Im);
    }

    public ComplexNumber(BigFloat Re) {
        initFields(Re, new BigFloat(0));
    }

    public ComplexNumber(ComplexNumber other) {
        if (other == null) {
            initFields(new BigFloat(0), new BigFloat(0));
        }
        else {
            initFields(other.real, other.imaginary);
        }
    }

    public ComplexNumber multiply(ComplexNumber other) throws NullPointerException {
        if (other == null)
            throw new NullPointerException();
        BigFloat r = real.multiply(other.real).subtract(imaginary.multiply(other.imaginary));
        BigFloat i = real.multiply(other.imaginary).add(imaginary.multiply(other.real));
        return new ComplexNumber(r, i);
    }

    public ComplexNumber multiply(BigFloat other) throws NullPointerException {
        if (other == null)
            throw new NullPointerException();
        return new ComplexNumber(real.multiply(other), imaginary.multiply(other));
    }

    public ComplexNumber add(ComplexNumber other) {
        if (other == null) {
            return new ComplexNumber(this);
        }
        return new ComplexNumber(real.add(other.real), imaginary.add(other.imaginary));
    }

    public ComplexNumber subtract(ComplexNumber other) {
        if (other == null) {
            return new ComplexNumber(this);
        }
        return new ComplexNumber(real.subtract(other.real), imaginary.subtract(other.imaginary));
    }

    public BigFloat absSquared() {
        return real.multiply(real).add(imaginary.multiply(imaginary));
    }

    public BigFloat abs(int precision) {
        return absSquared().sqrt(precision);
    }

    public ComplexNumber inverse(int precision) {
        BigFloat absSqr = absSquared();
        BigFloat r = real.divide(absSqr, precision);
        BigFloat i = imaginary.divide(absSqr, precision).negate();
        return new ComplexNumber(r, i);
    }

    public ComplexNumber divide(ComplexNumber other, int precision) {
        if (other == null) {
            throw new NullPointerException();
        }
        return multiply(other.inverse(precision)).round(precision);
    }

    public ComplexNumber divide(BigFloat other, int precision) {
        if (other == null) {
            throw new NullPointerException();
        }
        return new ComplexNumber(real.divide(other, precision),
                imaginary.divide(other, precision));
    }

    public ComplexNumber round(int precision) {
        return new ComplexNumber(real.round(precision), imaginary.round(precision));
    }

    /**
     * Если число равно  w[k] = exp(2*pi*i*2^(-k)), то есть
     * является первообразным корнем единицы 2^k степени,
     * вернет w[k+1].
     * @param precision точность в битах после двоичной запятой.
     * @return первообразный корень из единицы порядком вдвое меньше.
     */
    public ComplexNumber nextOneRoot(int precision) {
        BigFloat r = new BigFloat(1).add(real).multiplyByPowerOfTwo(-1).sqrt(precision);
        BigFloat i = imaginary.multiplyByPowerOfTwo(-1).divide(r, precision);
        return new ComplexNumber(r, i);
    }

    public ComplexNumber conjugate() {
        return new ComplexNumber(real, imaginary.negate());
    }

    public ComplexNumber multiplyByPowerOfTwo(int power) {
        return new ComplexNumber(real.multiplyByPowerOfTwo(power), imaginary.multiplyByPowerOfTwo(power));
    }

    public BigFloat getReal() {
        return real;
    }

    public BigFloat getImaginary() {
        return imaginary;
    }

    static ComplexNumber valueOf(long r, long i) {
        return new ComplexNumber(new BigFloat(r), new BigFloat(i));
    }

    @Override
    public String toString() {
        return "(" + real + " + i*" + imaginary + ')';
    }
}
