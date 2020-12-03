package ru.spbstu.telematics.java;

import javafx.util.Pair;

import java.awt.*;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Concert {

    ReadWriteLock hallLock;
    Hall hall;
    int currentState;
    Usher[] ushers;
    Boolean[] ushersBusy;

    Integer[] referenceHall;


    public Concert(int hallWidth, int hallHeight, int ushersNum) {
        if (ushersNum < 1)
            ushersNum = 1;
        ushers = new Usher[ushersNum];
        for (int i = 0; i < ushers.length; i++) {
            ushers[i] = new Usher(this);
        }
        ushersBusy = new Boolean[ushersNum];
        Arrays.fill(ushersBusy, false);
        currentState = 0;
        hall = new Hall(hallWidth, hallHeight);
        hallLock = new ReentrantReadWriteLock();
        referenceHall = new Integer[hallHeight * hallWidth];
        Arrays.fill(referenceHall, 0);
    }

    boolean session(boolean start, Usher u) {
        int i = 0;
        while (i < ushers.length && ushers[i] != u) i++;
        if (i == ushers.length || start == ushersBusy[i]) {
            return false;
        }
        ushersBusy[i] = start;
        return true;
    }

    Pair<Hall, Integer> getHall() {
        Lock lock = hallLock.readLock();
        lock.lock();
        Hall res = new Hall(hall);
        Integer index = currentState;
        lock.unlock();
        return new Pair<Hall, Integer>(res, index);
    }

    boolean buy(Set<Pair<Integer, Integer>> seats, int stateIndex) {
        Lock lock = hallLock.writeLock();
        lock.lock();
        boolean canBuy = true;
        //после чтения что-то успели записать изменилось
        if (stateIndex != currentState) {
            //проверим, не успели ли купить наши места
            for (Pair<Integer, Integer> seat: seats) {
                if (!hall.isAvailable(seat.getKey(), seat.getValue())) {
                    canBuy = false;
                    break;
                }
            }
        }
        if (canBuy) {
            for (Pair<Integer, Integer> seat: seats) {
                hall.setSeat(seat.getKey(), seat.getValue());
                //System.out.println("size: " + referenceHall.length + " h: " + hall.getHeight() + " w: " + hall.getWidth());
                //System.out.println(seat);
                referenceHall[hall.getWidth() * seat.getKey() + seat.getValue()] ++;
            }
            currentState++;
            if (currentState < 0)
                currentState = 0;
        }
        lock.unlock();
        return canBuy;
    }

    public Usher[] getUshers() {
        return ushers;
    }

    public boolean checkHall() {
        Lock lock = hallLock.readLock();
        lock.lock();
        System.out.println("Current hall:\n" + hall);
        int errors = 0;
        int bought = 0;
        for (int i = 0; i < hall.getHeight(); i++) {
            for (int j = 0; j < hall.getWidth(); j++) {
                int times = referenceHall[i * hall.getWidth() + j];
                bought += times;
                if (times > 1) {
                    errors ++;
                    System.out.println("Seat (" + i + "; " + j + ") bought " + times + " times!");
                }
            }
        }
        System.out.println("Seat bought: " + bought + ".");
        if (errors > 0) {
            System.out.println("Errors: " + errors + ".");
        }
        else {
            System.out.println("No errors.");
        }
        lock.unlock();
        return errors > 0;
    }

    public boolean soldOut() {
        return hall.freeSeats() == 0;
    }
}
