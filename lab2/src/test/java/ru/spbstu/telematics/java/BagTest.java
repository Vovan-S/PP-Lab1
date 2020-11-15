package ru.spbstu.telematics.java;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import java.util.Arrays;
import java.util.Collection;

/**
 * Тесты для установления соответсвия поведения класса {@link MyBag} и
 * {@link org.apache.commons.collections4.bag.HashBag}.
 */
public class BagTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public BagTest(String testName )
    {
        super( testName );
    }

    public static void main(String[] args) {
        TestCase test = new BagTest("My Bag :)");
        test.run();
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( BagTest.class );
    }

    public void runTest() {
        testAdd();
        testRemove();
        System.out.println("correct");
    }

    /**
     * Функция для проверки того, что две сумки равны.
     * @param bag1 первая сумка.
     * @param bag2 вторая сумка.
     * @param <T> тип данных в сумках.
     * @return <code>true</code>, если для любого элемента из <code>uniqueSet</code>
     *         сумок количество элементов совпадает, иначе <code>false</code>.
     */
    private <T> boolean bagsAreEqual(Bag<T> bag1, Bag<T> bag2) {
        if (bag1 == null || bag2 == null)
            return bag1 == bag2;
        for(T t: bag1.uniqueSet()) {
            if (bag1.getCount(t) != bag2.getCount(t))
                return false;
        }
        for(T t: bag2.uniqueSet()) {
            if (bag1.getCount(t) != bag2.getCount(t))
                return false;
        }
        return true;
    }

    /**
     * Тест для функции добавления нового элемента. <br>
     * Проверяет методы:
     * <ol>
     *     <li>{@link MyBag#add(Object)}</li>
     *     <li>{@link MyBag#add(Object, int)}</li>
     *     <li>{@link MyBag#clear()}</li>
     * </ol>
     */
    public void testAdd()
    {
        HashBag<Integer> bag1 = new HashBag<Integer>();
        MyBag<Integer> bag2 = new MyBag<Integer>();
        Integer[] toAdd = {1, 3, 3, 1, 3, 4, 6, 3, null, 1, -1, 4, 5, 2, 1};
        for (Integer val: toAdd) {
            bag1.add(val);
            bag2.add(val);
            assertTrue(bagsAreEqual(bag1, bag2));
        }
        bag1.clear();
        bag2.clear();
        assertTrue(bagsAreEqual(bag1, bag2));
        int[] cards = {1, 2, 4, 5, -1, 0, 1, -3, 5, 1};
        for (int i = 0; i < toAdd.length; i++) {
            bag1.add(toAdd[i], cards[i % cards.length]);
            bag2.add(toAdd[i], cards[i % cards.length]);
            assertTrue(bagsAreEqual(bag1, bag2));
        }
    }
    /**
     * Тесты для метода удаления объекта и прочих. <br>
     * Проверяет методы:
     * <ol>
     *     <li>{@link MyBag#remove(Object)}</li>
     *     <li>{@link MyBag#remove(Object, int)}</li>
     *     <li>{@link MyBag#addAll(Collection)}</li>
     *     <li>{@link MyBag#clear()}</li>
     * </ol>
     */
    public void testRemove() {
        Integer[] toAdd = {1, 3, 3, 1, 3, 4, 6, 3, null, 1, -1, 4, 5, 2, 1};
        HashBag<Integer> bag1 = new HashBag<Integer>();
        MyBag<Integer> bag2 = new MyBag<Integer>();
        bag1.addAll(Arrays.asList(toAdd));
        bag2.addAll(Arrays.asList(toAdd));
        assertTrue(bagsAreEqual(bag1, bag2));
        Integer[] toRemove = {3, 1, 4, 7, null, null, -1, -1, -1, 5, 5};
        for(Integer val: toRemove) {
            bag1.remove(val);
            bag2.remove(val);
            assertTrue(bagsAreEqual(bag1, bag2));
        }
        bag1.clear();
        bag2.clear();
        bag1.addAll(Arrays.asList(toAdd));
        bag2.addAll(Arrays.asList(toAdd));
        assertTrue(bagsAreEqual(bag1, bag2));
        int[] cards = {3, 0, 4, -4, 14341, 1, 4, 5};
        for (int i = 0; i < toRemove.length; i++) {
            bag1.remove(toRemove[i], cards[i % cards.length]);
            bag2.remove(toRemove[i], cards[i % cards.length]);
            assertTrue(bagsAreEqual(bag1, bag2));
        }
    }

    //TODO: дописать юнит тесты для метода uniqueSet()
    //TODO: дописать юнит тесты для метода removeAll()
    //TODO: дописать юнит тесты для метода retainAll()
    //TODO: дописать юнит тесты для метода containsAll()

}
