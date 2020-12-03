package ru.spbstu.telematics.java;

import java.util.Random;

public class Buyer implements Runnable {
    Usher usher;
    int ticketsBought = 0;

    public Buyer(Usher u) {
        usher = u;
    }

    public void run() {
        Hall h = usher.getHall();
        Random r = new Random();
        if (h == null)
            return;
        int freeSeats = h.freeSeats();
        while (freeSeats > 0) {
            StringBuilder str = new StringBuilder(Thread.currentThread().getName() + ": want to buy ");
            usher.startSession();
            freeSeats = usher.getHall().freeSeats();
            if (freeSeats == 0)
                break;
            int count = r.nextInt(Math.min(freeSeats, 10)) + 1;
            str.append(count).append(" tickets: [");
            int i = r.nextInt(h.getHeight());
            int j = r.nextInt(h.getWidth());
            boolean bought = false;
            int c = count;
            while (c --> 0) {
                while(bought || !usher.checkSeat(i, j, false)) {
                    j++;
                    if (j == h.getWidth()) {
                        i = (i + 1) % h.getHeight();
                        j = 0;
                    }
                    bought = false;
                }
                usher.select(i, j);
                bought = true;
                str.append("(").append(i).append(";").append(j).append(")");
            }
            str.append("]");
            boolean res = usher.tryBuy();
            if (res) {
                System.out.println(str.append(" - succeeded").toString());
                ticketsBought += count;
                try {
                    Thread.sleep(r.nextInt(100) + 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println(str.append(" - failed").toString());
            }
        }
    }

    public int getTicketsBought() {
        return ticketsBought;
    }
}
