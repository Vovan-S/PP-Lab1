package ru.spbstu.telematics.java;

import org.apache.commons.collections4.bag.HashBag;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Debug purposes....
 */
public class App {

    public static void main( String[] args ) {
        Integer[] ar1 = {1, 1, 1};
        HashBag<Integer> bag = new HashBag<Integer>();
        bag.addAll(Arrays.asList(ar1));
        System.out.println("Initial bag: " + bag + " //has 3 copies of '1'");
        Integer[] ar2 = {1, 1, 1, 1};
        System.out.println("To retain: " + Arrays.asList(ar2) + " //retaining collections with 4 copies of '1'");
        bag.retainAll(Arrays.asList(ar2));
        System.out.println("After 'retain all': " + bag + " //??? Should have left all of '1's!!!");
    }
}
