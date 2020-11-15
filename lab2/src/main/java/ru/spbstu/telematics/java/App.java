package ru.spbstu.telematics.java;

import org.apache.commons.collections4.bag.HashBag;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Debug purposes....
 */
public class App {

    public static void main( String[] args ) {
        Integer[] ar = {1, 2, 4, 5, 1, 0, 1, 3, 5, 4, 2, 0, null, null};
        HashBag<Integer> bag = new HashBag<Integer>();
        MyBag<Integer> myBag = new MyBag<Integer>();
        bag.addAll(Arrays.asList(ar));
        myBag.addAll(Arrays.asList(ar));
        Iterator<Integer> it1 = bag.iterator();
        Iterator<Integer> it2 = myBag.iterator();
        while(it1.hasNext() && it2.hasNext()) {
            System.out.println("1: " + it1.next() + " 2: " + it2.next());
        }
        for (Integer v: ar) {
            System.out.println(bag + " my: " + myBag);
            System.out.println(myBag.equals(bag));
            System.out.println(myBag.hashCode() == bag.hashCode());
            bag.add(v);
            myBag.add(v);
        }
    }
}
