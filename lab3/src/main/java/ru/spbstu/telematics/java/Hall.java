package ru.spbstu.telematics.java;

import java.util.Arrays;

public class Hall {
    public enum Seat {
        Available,
        Selected,
        Bought
    }
    Seat[][] matrix;

    Hall(Hall other) {
        matrix = other.matrix.clone();
    }

    Hall(int width, int height) {
        matrix = new Seat[height][width];
        for (Seat[] seats : matrix) {
            Arrays.fill(seats, Seat.Available);
        }
    }

    boolean isAvailable(int i, int j) {
        Seat status;
        try {
            status = matrix[i][j];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        return status.equals(Seat.Available);
    }

    void setSeat(int i, int j) {
        try {
             matrix[i][j] = Seat.Bought;
        }
        catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    Seat[][] getMatrix() {
        return (matrix == null) ? null : matrix.clone();
    }

    public int getHeight() {
        return (matrix == null)? 0 : matrix.length;
    }

    public int getWidth() {
        return (getHeight() == 0)? 0 : matrix[0].length;
    }

    public int freeSeats() {
        int count = 0;
        for(Seat[] row: matrix)
            for(Seat s: row)
                if(s.equals(Seat.Available))
                    count ++;
        return count;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        boolean first = true;
        for (Seat[] row: matrix) {
            if (first) {
                res.append("[");
                first = false;
            }
            else {
                res.append("\n[");
            }
            for (Seat s: row)
                switch (s) {
                    case Bought:
                        res.append('B');
                        break;
                    case Selected:
                        res.append('R');
                        break;
                    case Available:
                        res.append('A');
                        break;
                }
            res.append("]");
        }
        return res.toString();
    }
}
