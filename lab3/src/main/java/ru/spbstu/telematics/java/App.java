package ru.spbstu.telematics.java;

import sun.awt.windows.ThemeReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException {
        int nUshers = 5;
        int width = 15;
        int height = 10;
        final Concert concert = new Concert(width, height, nUshers);
        List<Buyer> buyerList = new ArrayList<Buyer>();
        List<Thread> threadList = new ArrayList<Thread>();
        for (int i = 0; i < nUshers; i++) {
            Buyer b = new Buyer(concert.getUshers()[i]);
            Thread t = new Thread(b);
            t.start();
            threadList.add(t);
            buyerList.add(b);
        }
        new Thread(new Runnable() {
            public void run() {
                System.out.println(concert.soldOut());
                while (!concert.soldOut()) {
                    concert.checkHall();
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        for(Thread t: threadList)
            t.join();
        concert.checkHall();
        int bought = 0;
        for(Buyer b: buyerList) {
            bought += b.getTicketsBought();
        }
        System.out.println("Size: " + width*height + "; Sold: " + bought);
    }
}
