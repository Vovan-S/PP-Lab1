package ru.spbstu.telematics.java;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class BigFloatWriter {
    OutputStream outputStream;

    BigFloatWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeBinary(BigFloat number) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(BigFloatReader.Header.Binary);
        objectOutputStream.writeInt(number.precision());
        objectOutputStream.writeInt(number.wholeBits());
        objectOutputStream.writeBoolean(number.number.signum() < 0);
        objectOutputStream.write(number.number.abs().toByteArray());
        objectOutputStream.flush();
    }

    public void writeBinary(BigFloat number, int precision) throws IOException {
        writeBinary(number.round(precision));
    }
}
