package ru.spbstu.telematics.java;

import com.sun.javaws.exceptions.InvalidArgumentException;
import com.sun.media.sound.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

public class BigFloatReader {
    enum Header {
        Binary,
        UTF
    };

    InputStream inputStream;
    ObjectInputStream objectInputStream;
    Header header;
    int precision;

    public BigFloatReader(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        objectInputStream = new ObjectInputStream(inputStream);
        header = null;
        precision = -1;
    }

    public int readPrecision() throws IOException {
        if (header == null) {
            try {
                header = (Header) objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                System.out.println("Read BigFloat: cannot read header!");
                throw new InvalidFormatException("Read BigFloat: cannot read header!");
            }
        }
        else {
            throw new InvalidFormatException("Read BigFloat: cannot read precision from the stream!");
        }
        precision = objectInputStream.readInt();
        return precision;
    }

    public BigFloat read(int digits) throws IOException {
        if (digits < 0) {
            digits = Integer.MAX_VALUE;
        }
        boolean ignorePrecision = false;
        if (header == null) {
            ignorePrecision = true;
            try {
                header = (Header) objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                System.out.println("Read BigFloat: cannot read header!");
                throw new InvalidFormatException("Read BigFloat: cannot read header!");
            }
        }
        if (header == Header.UTF) {
            int base, wholeDigits;
            base = objectInputStream.readInt();
            wholeDigits = objectInputStream.readInt();
            String numberString = objectInputStream.readUTF();
            if (numberString.length() > digits) {
                numberString = numberString.substring(0, digits);
            }
            BigFloat res = new BigFloat(new BigInteger(numberString, base));
            int baseExponent = numberString.length() - wholeDigits;
            BigFloat multiplier = new BigFloat(1);
            for (int i = 0; i < Math.abs(baseExponent); i++) {
                multiplier = multiplier.multiply(new BigFloat(base));
            }
            if (baseExponent > 0) {
                multiplier = multiplier.inverse(res.valuableBits() + 5);
            }
            res = res.multiply(multiplier);
            header = null;
            return res;
        }
        else {
            // если до этого мы не считали точность
            if (ignorePrecision) {
                // Первым интом является точность значения, она нам не нужна
                precision = objectInputStream.readInt();
            }
            int wholeBits = objectInputStream.readInt();
            if (precision + wholeBits < digits) {
                digits = precision + wholeBits;
            }
            int size = (digits - 1) / Byte.SIZE + 1;
            boolean sign = objectInputStream.readBoolean();
            byte[] buff = new byte[size];
            int bytes = size;
            objectInputStream.readFully(buff, 0, size);
            //int exponent = digits - wholeBits - 1;
            int exponent = bytes * Byte.SIZE
                    - Integer.numberOfLeadingZeros(buff[0]) + (Integer.SIZE - Byte.SIZE) -
                    wholeBits;
            BigFloat res = new BigFloat(new BigInteger(buff), exponent);
            if (sign)
                res = res.negate();
            header = null;
            return res;
        }
    }
}
