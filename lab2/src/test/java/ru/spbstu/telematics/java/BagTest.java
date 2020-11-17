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
        System.out.println("Starting tests.");
        testAdd();
        System.out.println("Add tests are passed correctly.");
        testRemove();
        System.out.println("Remove tests are passed correctly.");
        testCollections();
        System.out.println("Collections tests are passed correctly.");
        System.out.println("All tests are passed!");
    }

    /**
     * Функция для проверки того, что две сумки равны. Для проверки используются методы
     * {@link MyBag#uniqueSet()} и {@link MyBag#getCount(Object)}.
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
     * Заполняем две сумки значениями из массива с помощью метода {@link MyBag#addAll(Collection)}.
     * @param bag1 первая сумка.
     * @param bag2 вторая сумка.
     * @param initList массив, значениями из которого заполняем обе сумке.
     * @param <T> тип элементов, которыми заполняются сумки.
     */
    private <T> void initBags(Bag<T> bag1, Bag<T> bag2, T[] initList) {
        bag1.clear();
        bag2.clear();
        bag1.addAll(Arrays.asList(initList));
        bag2.addAll(Arrays.asList(initList));
        assertTrue(bagsAreEqual(bag1, bag2));
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
        initBags(bag1, bag2, toAdd);
        Integer[] toRemove = {3, 1, 4, 7, null, null, -1, -1, -1, 5, 5};
        for(Integer val: toRemove) {
            bag1.remove(val);
            bag2.remove(val);
            assertTrue(bagsAreEqual(bag1, bag2));
        }
        initBags(bag1, bag2, toAdd);
        int[] cards = {3, 0, 4, -4, 14341, 1, 4, 5};
        for (int i = 0; i < toRemove.length; i++) {
            bag1.remove(toRemove[i], cards[i % cards.length]);
            bag2.remove(toRemove[i], cards[i % cards.length]);
            assertTrue(bagsAreEqual(bag1, bag2));
        }
    }

    /**
     * Тесты для работы с коллекциями. Проверяет работу методов:
     * <ol>
     *     <li>{@link MyBag#addAll(Collection)}</li>
     *     <li>{@link MyBag#removeAll(Collection)}</li>
     *     <li>{@link MyBag#retainAll(Collection)}</li>
     *     <li>{@link MyBag#containsAll(Collection)}</li>
     * </ol>
     */
    public void testCollections() {
        Integer[] initList = {1, 1, 1, 2, 2, 3, 4, null};
        Integer[][] toRemove = {initList, //все из коллекции
                {}, //пустой массив
                {1, 1, 1, 1}, //больше, чем в коллекции
                {-1, -1, 5, 5, 5}, //элементы, которых нет в коллекции
                {1, 1, 2, 3}, //частично убираем элементы
                {null, null, null}, //нуль-ссылки
                {1, 1, 2, 2, 2, 3, 5, 6, null, null}}; //сборная солянка
        Integer[][] toRetain = {initList, //все из коллекции
                {}, //пустой массив
                {1, 1, 2}, //частично оставляем
                //{1, 1, 1, 1, 2, 2, 2}, //оставляем больше, чем есть
                {5, -1, 5, -1}, //элементы, которых нет в коллекции
                {null}, //нуль-ссылки
                {1, 1, 2, 2, 11, -1, null}}; //сборная солянка
        Integer[][] toContains = {initList, //вся коллекция
                {}, //пустой массив
                {1, 1, 5}, //элемент, которого нет вообще
                {1, 1, 1, 2, 2, 2}, //элемент, которого не достаточно
                {null, null}, //нуль-ссылки
                {1, 1, 2, 2, 5, null}, //сборная солянка 1
                {1, 1, 1, 2, 2, 5, 5, 6}}; //сборная солянка 2
        Bag<Integer> bag2 = new MyBag<Integer>();
        Bag<Integer> bag1 = new HashBag<Integer>();
        for (Integer[] ar: toRemove) {
            initBags(bag1, bag2, initList);
            bag1.removeAll(Arrays.asList(ar));
            bag2.removeAll(Arrays.asList(ar));
            assertTrue(bagsAreEqual(bag1, bag2));
        }
        for (Integer[] ar: toRetain) {
            initBags(bag1, bag2, initList);
            bag1.retainAll(Arrays.asList(ar));
            bag2.retainAll(Arrays.asList(ar));
            assertTrue(bagsAreEqual(bag1, bag2));
        }
        for (Integer[] ar: toContains) {
            initBags(bag1, bag2, initList);
            assertEquals(bag1.containsAll(Arrays.asList(ar)),
                         bag2.containsAll(Arrays.asList(ar)));
        }
    }
}
