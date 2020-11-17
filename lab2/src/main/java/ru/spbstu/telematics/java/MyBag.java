package ru.spbstu.telematics.java;

import javafx.util.Pair;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.AbstractMapBag;

import java.lang.reflect.Array;
import java.util.*;

import static java.lang.Math.abs;

/**
 * Контейнерный класс, реализующий интерфейс <code>Bag</code> из Apache
 * Collections 4. Для каждого элемента хранится его кратность, то есть
 * то, сколько раз он встречается в коллекции. Для хранения используется
 * хэш-таблица.
 * @param <T> тип элемента коллекции.
 */
public class MyBag<T> implements Bag<T> {
    /**
     * Хэш-таблица, элементами массива являются первые элементы односвязного списка.
     * @see MyNode
     */
    MyNode<T>[] table;
    /**
     * Общее количество элементов в таблице.
     */
    int count;
    /**
     * Изначальное количество списков в таблице.
     * Тут используется наивная реализация -- таблица не расширяется.
     */
    static final int INIT_TABLE_SIZE = 20;

    /**
     * Узел односвязного списка.
     * @param <T> тип хранимых в коллекции данных.
     */
    static private class MyNode<T> {

        /**
         * Узел хранит данные и их кратность.
         */
        Pair<T, Integer> data;

        /**
         * Указатель на следующий элемент списка.
         */
        MyNode<T> next;

        /**
         * Конструктор.
         * @param val данные для узла.
         * @param count кратность данных.
         */
        public MyNode(T val, Integer count) {
            data = new Pair<T, Integer>(val, count);
            next = null;
        }

        /**
         * Вставляет новый узел узел после этого узла
         * @param val данные для нового узла.
         * @param count кратность данных.
         */
        public void insert(T val, int count) {
            MyNode<T> p = next;
            next = new MyNode<T>(val, count);
            next.next = p;
        }

        /**
         * Добавляет новый узел в конец списка.
         * @param val данные для нового узла.
         * @param count кратность данных.
         * @return длина списка после вставки, начиная с этого узла.
         */

        public int pushBack(T val, int count) {
            int c = 2;
            MyNode<T> p = this;
            while (p.next != null) {
                p = p.next;
                c++;
            }
            p.next = new MyNode<T>(val, count);
            return c;
        }

        /**
         * Удаляет следующий узел.
         */
        public void removeNext() {
            if (next != null) {
                next = next.next;
            }
        }

        /**
         * Доступ на чтение для поля {@link MyNode#next}.
         * @return значение поля {@link MyNode#next}.
         */
        public MyNode<T> getNext() {
            return next;
        }

        /**
         * Доступ на чтение для поля {@link MyNode#data}.
         * @return значение поля {@link MyNode#data}.
         */
        public Pair<T, Integer> getData() {
            return data;
        }

        /**
         * Устанавливает новую кратность для данных в узле.
         * @param c новая кратность данных.
         */
        public void setCount(int c) {
            data = new Pair<T, Integer>(data.getKey(), c);
        }
    } //класс MyNode кончился

    /**
     * Итератор для коллекции.
     * @param <T> тип данных в коллекции.
     */
    static private class BagIterator<T> implements Iterator<T> {
        /**
         * Ссылка на таблицу, которую хранит экземпляр коллекции.
         * @see MyBag#table
         */
        MyNode<T>[] table;

        /**
         * Индекс текущего списка в таблице.
         */
        int index;

        /**
         * Предыдущий узел в списке. Имеет значение <code>null</code>, если
         * если итератор указывает на первые узел в списке.
         */
        MyNode<T> prev;

        /**
         * Текущая кратность в узел.
         */
        int card;


        /**
         * Конструктор.
         * @param tab таблица {@link MyBag#table}.
         */
        public BagIterator(MyNode<T>[] tab) {
            table = tab;
            prev = null;
            index = 0;
            card = 0;
        }

        /**
         * Проверка, имеется ли слудующий элемент в коллекции.
         * @return <code>false</code> если итератор дошел до последнего элемента в
         * коллекции, <code>true</code> иначе.
         */
        public boolean hasNext() {
            //мы в начале списка
            if (prev == null) {
                int i = index;
                //пропускаем все пустые списки
                while (i < table.length && table[i] == null)
                    i++;
                //есть непустой список до конца таблицы
                return i < table.length;
            }
            return true;
        }

        /**
         * Следующий элемент коллекции.
         * @return значение следующего элемента коллекции.
         */
        public T next() {
            if (!hasNext())
                return null;
            //мы в начале списка
            if (prev == null) {
                //находим непустой список
                MyNode<T> p = table[index];
                while (p == null) {
                    index++;
                    p = table[index];
                }
                //еще не все копии перебрали
                if (card < p.getData().getValue()) {
                    card++;
                    //возвращаем копию
                    return p.getData().getKey();
                }
                //перебрали все копии
                else {
                    //переходим к следующему узлу
                    prev = p;
                    card = 0;
                    return next();
                }
            }
            //мы в конце списка
            else if (prev.next == null) {
                //переходим к следующему списку в таблице
                prev = null;
                card = 0;
                index++;
                return next();
            }
            //находимся где-то в середине списка
            else {
                //перебрали не все копии
                if (card < prev.next.getData().getValue()) {
                    card++;
                    //возвращаем очередную копию
                    return prev.getData().getKey();
                }
                else {
                    //переходим к следующему узлу
                    card = 0;
                    prev = prev.next;
                    return next();
                }
            }
        }

        /**
         * Метод-заглушка. Добавлен, чтобы компилировалось. <br>
         * Ничего не делает.
         */
        public void remove() {
            //METHOD STUB
        }
    } //класс BagIterator кончился

    /**
     * Находит узел, в котором находится переданный объект.
     * @param o объект, узел с которым нужно найти.
     * @return узел, в котором находится объект.
     */
    private MyNode<T> getNode(Object o) {
        int i = getIndex(o);
        for (MyNode<T> p = table[i]; p != null; p = p.getNext()) {
            if (safeEquals(p.getData().getKey(), o))
                return p;
        }
        return null;
    }

    /**
     * Добавляем новый узел в <code>i</code>-тую строку хэш-таблицы. <br>
     * <b>Важно:</b> предполагается, что в таблице нет узла, который
     * хранил бы такой объект.
     * @param i номер строки в хэш-таблице.
     * @param val пара (данные, кратность)
     */
    private void addNew(int i, Pair<T, Integer> val) {
        if (table[i] == null)
            table[i] = new MyNode<T>(val.getKey(), val.getValue());
        else
            table[i].pushBack(val.getKey(), val.getValue());
    }

    /**
     * Служебный метод, подсчитывает количество указанных объектов в указанной коллекции.
     * @param collection коллекция, в которой надо подсчитать количество объектов.
     * @param o объект, количество копий которого, надо подсчитать.
     * @return количество копий объекта в коллекции.
     */
    private int countInCollection(Collection collection, Object o) {
        if (collection == null)
            return 0;
        int colCount = 0;
        for(Object o1: collection) {
            if (safeEquals(o, o1))
                colCount ++;
        }
        return colCount;
    }

    /**
     * Безопасная проверка равества ссылок. Если первая из ссылок -- нулевая,
     * то проверяется ее равенство второй по адресу, иначе -- по значению.
     * @param o1 первая ссылка.
     * @param o2 вторая ссылка.
     * @return <code>true</code>, если две ссылки равны.
     */
    static private boolean safeEquals(Object o1, Object o2) {
        return o1 == null && o2 == null || o1 != null && o1.equals(o2);
    }

    /**
     * Индекс списка, к которому относится (или должен относиться) объект.
     * @param o объект.
     * @return индекс списка.
     */
    private int getIndex(Object o) {
        return (o == null) ? 0 : abs(o.hashCode() % table.length);
    }

    /**
     * Инициализация полей класса. Используется в конструкторах.
     */
    private void initFields() {
        //ругается, гад
        MyNode<T> p = new MyNode<T>(null, 0);
        table = (MyNode<T>[]) Array.newInstance(p.getClass(), INIT_TABLE_SIZE);
        //table = (MyNode<T>[]) new Object[INIT_TABLE_SIZE];
        count = 0;
    }

    /**
     * Конструктор по умолчанию. Создает пустую сумку.
     * @see MyBag#MyBag(Object, int)
     * @see MyBag#MyBag(Iterable)
     */
    public MyBag() {
        initFields();
    }

    /**
     * Создает сумку, в которой есть один элемент. <br>
     * Аналогично вызову <code>MyBag(val, 1)</code>.
     * @param val элемент, который будет хранится в сумке.
     * @see MyBag#MyBag(Object, int)
     */
    public MyBag(T val) {
        initFields();
        add(val);
    }

    /**
     * Создает сумку с несколькими копиями элемента.
     * @param val элемент, который будет хранится в сумке.
     * @param count количество копий этого элемента.
     * @see MyBag#MyBag(Object)
     * @see MyBag#MyBag(Iterable)
     */
    public MyBag(T val, int count) {
        initFields();
        add(val, count);
    }

    /**
     * Создает сумку, в которой будут хранится ссылки на элементы
     * переданной коллекции.
     * @param col коллекция, ссылки на элементы которой будут храниться в коллекции.
     * @see MyBag#MyBag(Object, int)
     */
    public MyBag(Iterable<T> col) {
        initFields();
        if (col != null)
            for (T val: col) {
                add(val);
            }
    }


    /**
     * Возвращает количество объектов в сумке.
     * @param o объект, количество которого считаем.
     * @return количество объектов в сумке.
     */
    public int getCount(Object o) {
        MyNode<T> p = getNode(o);
        if (p == null)
            return 0;
        else
            return p.getData().getValue();
    }

    /**
     * Добавляет объект с кратностью 1. Аналогично вызову
     * <code>add(o, 1)</code>.
     * @param o добавляемый объект.
     * @return <code>true</code>, если объект добавлен. Всегда возвращается <code>true</code>.
     * @see MyBag#add(Object, int)
     */
    public boolean add(Object o) {
        return add(o, 1);
    }

    /**
     * Добавляет несколько копий объекта.
     * @param o добавляемый объект.
     * @param i количество копий, если <code>i < 1</code>, объект не добавляется.
     * @return <code>true</code>, если объект был добавлен, иначе <code>false</code>.
     * @see MyBag#add(Object)
     */
    public boolean add(Object o, int i) {
        if (i < 1)
            return false;
        MyNode<T> p = getNode(o);
        if (p == null) {
            int k = getIndex(o);
            addNew(k, new Pair<T, Integer>((T) o, i));
        }
        else
            p.setCount(p.getData().getValue() + i);
        count++;
        return true;
    }

    /**
     * Удаляет все копии объекта из сумки.
     * @param o удаляемый объект.
     * @return <code>true</code>, если объект был ранее в сумке, иначе <code>false</code>.
     * @see MyBag#remove(Object, int)
     */
    public boolean remove(Object o) {
        int i = getIndex(o);
        MyNode<T> p = table[i];
        if (p != null && safeEquals(p.getData().getKey(), o)) {
            count -= p.getData().getValue();
            table[i] = p.next;
            return true;
        }
        while (p != null && p.next != null) {
            if (safeEquals(p.next.getData().getKey(), o)) {
                count -= p.next.getData().getValue();
                p.removeNext();
                return true;
            }
            p = p.next;
        }
        return false;
    }

    /**
     * Добавляет все элементы коллекции, учитывая кратность.
     * @param c коллекция.
     * @return всегда <code>true</code>.
     */
    public boolean addAll(Collection c) {
        for (Object o: c)
            add(o);
        return true;
    }

    /**
     * Очищает сумку.
     */
    public void clear() {
        Arrays.fill(table, null);
        count = 0;
    }

    /**
     * Удаляет <code>i</code> копий объекта из сумки.
     * @param o удаляемый объект.
     * @param i количество удаляемых копий. Если <code>i < 1</code>,
     *          удаление не происходит.
     * @return <code>true</code>, если что-то было удалено, иначе <code>false</code>.
     */
    public boolean remove(Object o, int i) {
        if (i < 1)
            return false;
        int k = getIndex(o);
        MyNode<T> p = table[k];
        if (p != null && safeEquals(p.getData().getKey(), o)) {
            if (p.getData().getValue() <= i) {
                count -= p.getData().getValue();
                table[k] = p.next;
            }
            else {
                count -= i;
                p.setCount(p.getData().getValue() - i);
            }
            return true;
        }
        while (p != null && p.next != null) {
            if (safeEquals(p.next.getData().getKey(), o)) {
                if (p.next.getData().getValue() <= i) {
                    count -= p.getData().getValue();
                    p.removeNext();
                }
                else {
                    count -= i;
                    p.setCount(p.getData().getValue() - i);
                }
                return true;
            }
            p = p.next;
        }
        return false;
    }

    /**
     * @return множество со всеми различными объектами сумки.
     */
    public Set uniqueSet() {
        Set<T> res = new HashSet<T>();
        for (MyNode<T> n: table) {
            if (n == null)
                continue;
            res.add(n.data.getKey());
            MyNode<T> p = n;
            while (p.next != null) {
                res.add(p.next.data.getKey());
                p = p.next;
            }
        }
        return res;
    }

    /**
     * @return количество объектов в сумке, учитывая кратность.
     */
    public int size() {
        return count;
    }

    /**
     * @return <code>true</code>, если в сумке ничего не содержится, иначе <code>false</code>.
     */
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * Проверяет наличие объекта в сумке.
     * @param o проверяемый объект.
     * @return <code>true</code>, если объект есть в сумке, иначе <code>false</code>.
     */
    public boolean contains(Object o) {
        return getCount(o) > 0;
    }

    /**
     * @return итератор по сумке.
     * @see BagIterator
     */
    public Iterator<T> iterator() {
        return new BagIterator<T>(table);
    }

    /**
     * @return массив с объектами сумки, учитывая кратность.
     */
    public Object[] toArray() {
        Object[] res = new Object[size()];
        int i = 0;
        for (Object o: this) {
            res[i++] = o;
        }
        return res;
    }

    /**
     * Массив указанного типа из объектов этой сумки.
     * @param a экземляр класса, тип которого будет присвоен массиву.
     * @param <S> тип элементов возвращаемого массива.
     * @return массив, тип элементов которого соответсвует типу параметра.
     */
    public <S> S[] toArray(S[] a) {
        S[] res;
        if (size() <= a.length)
            res = a;
        else
            res = (S[]) new Object[size()];
        int i = 0;
        for (Object o : this) {
            res[i++] = (S) o;
        }
        return res;
    }

    /**
     * Оставляет в сумке только элементы из коллекции, учитывая кратность.
     * @param collection коллекция.
     * @return <code>true</code>, если сумка была изменена, иначе <code>false</code>.
     */
    public boolean retainAll(Collection collection) {
        if (collection == null) {
            clear();
            return true;
        }
        boolean changed = false;
        for (Object o: uniqueSet()) {
            int colCount = countInCollection(collection, o);
            int bagCount = getCount(o);
            if (bagCount > colCount) {
                remove(o, bagCount - colCount);
                changed = true;
            }
        }
        return changed;
    }

    /**
     * Удаляет из сумки все элементы коллекции, учитывая кратность.
     * @param collection коллекция.
     * @return <code>true</code>, если сумка была изменена, иначе <code>false</code>.
     */
    public boolean removeAll(Collection collection) {
        if (collection == null) {
            return false;
        }
        boolean changed = false;
        Set<Object> removed = new HashSet<Object>();
        for (Object o: collection) {
            if (removed.contains(o)  || !contains(o))
                continue;
            int colCount = countInCollection(collection, o);
            remove(o, colCount);
            removed.add(o);
            changed = true;
        }
        return changed;
    }

    /**
     * Проверяет, содержатся ли в сумке все элементы коллекции,
     * учитывая кратность.
     * @param collection коллекция.
     * @return <code>true</code>, если содержатся, иначе <code>false</code>.
     */
    public boolean containsAll(Collection collection) {
        if (collection == null)
            return true;
        Set<Object> removed = new HashSet<Object>();
        for(Object o: collection) {
            if (removed.contains(o))
                continue;
            if (getCount(o) < countInCollection(collection, o))
                return false;
            removed.add(o);
        }
        return true;
    }

    /**
     * Сравнение сумок происходит так же, как в классе {@link AbstractMapBag}.
     * @param o другая сумка.
     * @return <code>true</code>, если сумки одинаковые.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bag)) return false;
        Bag<?> other = (Bag<?>) o;
        for(Object val: other.uniqueSet()) {
            if (getCount(val) != other.getCount(val))
                return false;
        }
        for(Object val: uniqueSet()) {
            if (getCount(val) != other.getCount(val))
                return false;
        }
        return true;
    }

    /**
     * Хэш-код вычисляется так же, как в классе {@link org.apache.commons.collections4.bag.AbstractMapBag}.
     * @return хэш-код сумки.
     */
    @Override
    public int hashCode() {
        int result = 0;
        int i = 0;
        while (i < table.length) {
            MyNode<T> p = table[i];
            while (p != null) {
                result += (p.data.getKey() == null) ? 0 :
                        p.data.getKey().hashCode() ^ p.data.getValue();
                p = p.next;
            }
            i++;
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("[");
        int i = 0;
        boolean first = true;
        while (i < table.length) {
            MyNode<T> p = table[i];
            while(p != null) {
                if (first) {
                    first = false;
                }
                else {
                    res.append(",");
                }
                res.append(p.data.getValue()).append(":").append(p.data.getKey());
                p = p.next;
            }
            i++;
        }
        res.append(']');
        return res.toString();
    }
}
