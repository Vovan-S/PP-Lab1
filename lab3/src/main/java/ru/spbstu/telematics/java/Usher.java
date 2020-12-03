package ru.spbstu.telematics.java;

import javafx.util.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * Билетер -- продает билеты.
 */
public class Usher {
    Concert concert;
    int stateIndex;
    Hall hall;
    Set<Pair<Integer, Integer>> toBuy;

    Usher(Concert c) {
        concert = c;
        stateIndex = -1;
        hall = null;
        toBuy = new HashSet<Pair<Integer, Integer>>();
    }

    private boolean loadHall() {
        if (concert != null) {
            Pair<Hall, Integer> p = concert.getHall();
            hall = p.getKey();
            stateIndex = p.getValue();
            toBuy.clear();
            concert.session(true, this);
            return false;
        }
        else {
            return true;
        }
    }

    public String showHall() {
        if (hall == null) {
            if (loadHall())
                return "There is no concert.";
        }
        return hall.toString();
    }

    public boolean checkSeat(int i, int j, boolean forcedLoadHall) {
        if (hall == null || forcedLoadHall) {
            if (loadHall())
                return false;
        }
        return hall.isAvailable(i, j);
    }

    public boolean select(int i, int j) {
        if (hall == null)
            if (loadHall())
                return false;
        if (hall.isAvailable(i, j)) {
            toBuy.add(new Pair<Integer, Integer>(i, j));
            return true;
        }
        return false;
    }

    public boolean tryBuy() {
        if (concert == null)
            return false;
        boolean res = concert.buy(toBuy, stateIndex);
        if (res) {
            concert.session(false, this);
        }
        return res;
    }

    public boolean startSession() {
        if (concert == null)
            return false;
        loadHall();
        return true;
    }

    public boolean stopSession() {
        if (concert == null)
            return false;
        concert.session(false, this);
        return true;
    }

    public Hall getHall() {
        if (hall == null) {
            loadHall();
        }
        return hall;
    }
}
