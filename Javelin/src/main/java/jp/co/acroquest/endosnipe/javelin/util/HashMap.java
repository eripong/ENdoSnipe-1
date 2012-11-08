/*******************************************************************************
 * ENdoSnipe 5.0 - (https://github.com/endosnipe)
 * 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2012 Acroquest Technology Co.,Ltd.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package jp.co.acroquest.endosnipe.javelin.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class HashMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Cloneable, Serializable
{

    /**  */
    private static final long serialVersionUID = 8761989745265438004L;

    static final int DEFAULT_INITIAL_CAPACITY = 16;

    static final int MAXIMUM_CAPACITY = 1 << 30;

    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    transient Entry[] table;

    transient int size;

    int threshold;

    final float loadFactor;

    transient volatile int modCount;

    public HashMap(int initialCapacity, float loadFactor)
    {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);

        int capacity = 1;
        while (capacity < initialCapacity)
            capacity <<= 1;

        this.loadFactor = loadFactor;
        threshold = (int)(capacity * loadFactor);
        table = new Entry[capacity];
        init();
    }

    public HashMap(int initialCapacity)
    {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public HashMap()
    {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        table = new Entry[DEFAULT_INITIAL_CAPACITY];
        init();
    }

    public HashMap(Map<? extends K, ? extends V> m)
    {
        this(Math.max((int)(m.size() / DEFAULT_LOAD_FACTOR) + 1, DEFAULT_INITIAL_CAPACITY),
                DEFAULT_LOAD_FACTOR);
        putAllForCreate(m);
    }

    void init()
    {
    }

    static final Object NULL_KEY = new Object();

    static <T> T maskNull(T key)
    {
        return key == null ? (T)NULL_KEY : key;
    }

    static <T> T unmaskNull(T key)
    {
        return (key == NULL_KEY ? null : key);
    }

    private static final boolean useNewHash;
    static
    {
        useNewHash = false;
    }

    private static int oldHash(int h)
    {
        h += ~(h << 9);
        h ^= (h >>> 14);
        h += (h << 4);
        h ^= (h >>> 10);
        return h;
    }

    private static int newHash(int h)
    {
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    static int hash(int h)
    {
        return useNewHash ? newHash(h) : oldHash(h);
    }

    static int hash(Object key)
    {
        return hash(key.hashCode());
    }

    static boolean eq(Object x, Object y)
    {
        return x == y || x.equals(y);
    }

    static int indexFor(int h, int length)
    {
        return h & (length - 1);
    }

    public int size()
    {
        return size;
    }

    public boolean isEmpty()
    {
        return size == 0;
    }

    public V get(Object key)
    {
        if (key == null)
            return getForNullKey();
        int hash = hash(key.hashCode());
        for (Entry<K, V> e = table[indexFor(hash, table.length)]; e != null; e = e.next)
        {
            Object k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k)))
                return e.value;
        }
        return null;
    }

    private V getForNullKey()
    {
        int hash = hash(NULL_KEY.hashCode());
        int i = indexFor(hash, table.length);
        Entry<K, V> e = table[i];
        while (true)
        {
            if (e == null)
                return null;
            if (e.key == NULL_KEY)
                return e.value;
            e = e.next;
        }
    }

    public boolean containsKey(Object key)
    {
        Object k = maskNull(key);
        int hash = hash(k.hashCode());
        int i = indexFor(hash, table.length);
        Entry e = table[i];
        while (e != null)
        {
            if (e.hash == hash && eq(k, e.key))
                return true;
            e = e.next;
        }
        return false;
    }

    Entry<K, V> getEntry(Object key)
    {
        Object k = maskNull(key);
        int hash = hash(k.hashCode());
        int i = indexFor(hash, table.length);
        Entry<K, V> e = table[i];
        while (e != null && !(e.hash == hash && eq(k, e.key)))
            e = e.next;
        return e;
    }

    public V put(K key, V value)
    {
        if (key == null)
            return putForNullKey(value);
        int hash = hash(key.hashCode());
        int i = indexFor(hash, table.length);
        for (Entry<K, V> e = table[i]; e != null; e = e.next)
        {
            Object k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k)))
            {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }

        modCount++;
        addEntry(hash, key, value, i);
        return null;
    }

    private V putForNullKey(V value)
    {
        int hash = hash(NULL_KEY.hashCode());
        int i = indexFor(hash, table.length);

        for (Entry<K, V> e = table[i]; e != null; e = e.next)
        {
            if (e.key == NULL_KEY)
            {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }

        modCount++;
        addEntry(hash, (K)NULL_KEY, value, i);
        return null;
    }

    private void putForCreate(K key, V value)
    {
        K k = maskNull(key);
        int hash = hash(k.hashCode());
        int i = indexFor(hash, table.length);

        for (Entry<K, V> e = table[i]; e != null; e = e.next)
        {
            if (e.hash == hash && eq(k, e.key))
            {
                e.value = value;
                return;
            }
        }

        createEntry(hash, k, value, i);
    }

    void putAllForCreate(Map<? extends K, ? extends V> m)
    {
        for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry<? extends K, ? extends V> e = i.next();
            putForCreate(e.getKey(), e.getValue());
        }
    }

    void resize(int newCapacity)
    {
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY)
        {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable);
        table = newTable;
        threshold = (int)(newCapacity * loadFactor);
    }

    void transfer(Entry[] newTable)
    {
        Entry[] src = table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++)
        {
            Entry<K, V> e = src[j];
            if (e != null)
            {
                src[j] = null;
                do
                {
                    Entry<K, V> next = e.next;
                    int i = indexFor(e.hash, newCapacity);
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                }
                while (e != null);
            }
        }
    }

    public void putAll(Map<? extends K, ? extends V> m)
    {
        int numKeysToBeAdded = m.size();
        if (numKeysToBeAdded == 0)
            return;

        if (numKeysToBeAdded > threshold)
        {
            int targetCapacity = (int)(numKeysToBeAdded / loadFactor + 1);
            if (targetCapacity > MAXIMUM_CAPACITY)
                targetCapacity = MAXIMUM_CAPACITY;
            int newCapacity = table.length;
            while (newCapacity < targetCapacity)
                newCapacity <<= 1;
            if (newCapacity > table.length)
                resize(newCapacity);
        }

        for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry<? extends K, ? extends V> e = i.next();
            put(e.getKey(), e.getValue());
        }
    }

    public V remove(Object key)
    {
        Entry<K, V> e = removeEntryForKey(key);
        return (e == null ? null : e.value);
    }

    Entry<K, V> removeEntryForKey(Object key)
    {
        Object k = maskNull(key);
        int hash = hash(k.hashCode());
        int i = indexFor(hash, table.length);
        Entry<K, V> prev = table[i];
        Entry<K, V> e = prev;

        while (e != null)
        {
            Entry<K, V> next = e.next;
            if (e.hash == hash && eq(k, e.key))
            {
                modCount++;
                size--;
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }

    Entry<K, V> removeMapping(Object o)
    {
        if (!(o instanceof Map.Entry))
            return null;

        Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
        Object k = maskNull(entry.getKey());
        int hash = hash(k.hashCode());
        int i = indexFor(hash, table.length);
        Entry<K, V> prev = table[i];
        Entry<K, V> e = prev;

        while (e != null)
        {
            Entry<K, V> next = e.next;
            if (e.hash == hash && e.equals(entry))
            {
                modCount++;
                size--;
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }

    public void clear()
    {
        modCount++;
        Entry[] tab = table;
        for (int i = 0; i < tab.length; i++)
            tab[i] = null;
        size = 0;
    }

    public boolean containsValue(Object value)
    {
        if (value == null)
            return containsNullValue();

        Entry[] tab = table;
        for (int i = 0; i < tab.length; i++)
            for (Entry e = tab[i]; e != null; e = e.next)
                if (value.equals(e.value))
                    return true;
        return false;
    }

    private boolean containsNullValue()
    {
        Entry[] tab = table;
        for (int i = 0; i < tab.length; i++)
            for (Entry e = tab[i]; e != null; e = e.next)
                if (e.value == null)
                    return true;
        return false;
    }

    public Object clone()
    {
        HashMap<K, V> result = null;
        try
        {
            result = (HashMap<K, V>)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            // assert false;
        }
        result.table = new Entry[table.length];
        result.entrySet = null;
        result.modCount = 0;
        result.size = 0;
        result.init();
        result.putAllForCreate(this);

        return result;
    }

    static class Entry<K, V> implements Map.Entry<K, V>
    {
        final K key;

        V value;

        final int hash;

        Entry<K, V> next;

        Entry(int h, K k, V v, Entry<K, V> n)
        {
            value = v;
            next = n;
            key = k;
            hash = h;
        }

        public K getKey()
        {
            return HashMap.<K> unmaskNull(key);
        }

        public V getValue()
        {
            return value;
        }

        public V setValue(V newValue)
        {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public boolean equals(Object o)
        {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry)o;
            Object k1 = getKey();
            Object k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2)))
            {
                Object v1 = getValue();
                Object v2 = e.getValue();
                if (v1 == v2 || (v1 != null && v1.equals(v2)))
                    return true;
            }
            return false;
        }

        public int hashCode()
        {
            return (key == NULL_KEY ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }

        public String toString()
        {
            return getKey() + "=" + getValue();
        }

        void recordAccess(HashMap<K, V> m)
        {
        }

        void recordRemoval(HashMap<K, V> m)
        {
        }
    }

    void addEntry(int hash, K key, V value, int bucketIndex)
    {
        Entry<K, V> e = table[bucketIndex];
        table[bucketIndex] = new Entry<K, V>(hash, key, value, e);
        if (size++ >= threshold)
            resize(2 * table.length);
    }

    void createEntry(int hash, K key, V value, int bucketIndex)
    {
        Entry<K, V> e = table[bucketIndex];
        table[bucketIndex] = new Entry<K, V>(hash, key, value, e);
        size++;
    }

    private abstract class HashIterator<E> implements Iterator<E>
    {
        Entry<K, V> next; // next entry to return

        int expectedModCount; // For fast-fail 

        int index; // current slot 

        Entry<K, V> current; // current entry

        HashIterator()
        {
            expectedModCount = modCount;
            Entry[] t = table;
            int i = t.length;
            Entry<K, V> n = null;
            if (size != 0)
            { // advance to first entry
                while (i > 0 && (n = t[--i]) == null)
                    ;
            }
            next = n;
            index = i;
        }

        public boolean hasNext()
        {
            return next != null;
        }

        Entry<K, V> nextEntry()
        {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            Entry<K, V> e = next;
            if (e == null)
                throw new NoSuchElementException();

            Entry<K, V> n = e.next;
            Entry[] t = table;
            int i = index;
            while (n == null && i > 0)
                n = t[--i];
            index = i;
            next = n;
            return current = e;
        }

        public void remove()
        {
            if (current == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            Object k = current.key;
            current = null;
            HashMap.this.removeEntryForKey(k);
            expectedModCount = modCount;
        }

    }

    private class ValueIterator extends HashIterator<V>
    {
        public V next()
        {
            return nextEntry().value;
        }
    }

    private class KeyIterator extends HashIterator<K>
    {
        public K next()
        {
            return nextEntry().getKey();
        }
    }

    private class EntryIterator extends HashIterator<Map.Entry<K, V>>
    {
        public Map.Entry<K, V> next()
        {
            return nextEntry();
        }
    }

    // Subclass overrides these to alter behavior of views' iterator() method
    Iterator<K> newKeyIterator()
    {
        return new KeyIterator();
    }

    Iterator<V> newValueIterator()
    {
        return new ValueIterator();
    }

    Iterator<Map.Entry<K, V>> newEntryIterator()
    {
        return new EntryIterator();
    }

    private transient Set<Map.Entry<K, V>> entrySet = null;

    public Set<K> keySet()
    {
        Set<K> ks = keySet;
        return (ks != null ? ks : (keySet = new KeySet()));
    }

    private class KeySet extends AbstractSet<K>
    {
        public Iterator<K> iterator()
        {
            return newKeyIterator();
        }

        public int size()
        {
            return size;
        }

        public boolean contains(Object o)
        {
            return containsKey(o);
        }

        public boolean remove(Object o)
        {
            return HashMap.this.removeEntryForKey(o) != null;
        }

        public void clear()
        {
            HashMap.this.clear();
        }
    }

    public Collection<V> values()
    {
        Collection<V> vs = values;
        return (vs != null ? vs : (values = new Values()));
    }

    private class Values extends AbstractCollection<V>
    {
        public Iterator<V> iterator()
        {
            return newValueIterator();
        }

        public int size()
        {
            return size;
        }

        public boolean contains(Object o)
        {
            return containsValue(o);
        }

        public void clear()
        {
            HashMap.this.clear();
        }
    }

    public Set<Map.Entry<K, V>> entrySet()
    {
        Set<Map.Entry<K, V>> es = entrySet;
        return (es != null ? es : (entrySet = (Set<Map.Entry<K, V>>)(Set)new EntrySet()));
    }

    private class EntrySet extends AbstractSet/*<Map.Entry<K,V>>*/
    {
        public Iterator/*<Map.Entry<K,V>>*/iterator()
        {
            return newEntryIterator();
        }

        public boolean contains(Object o)
        {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<K, V> e = (Map.Entry<K, V>)o;
            Entry<K, V> candidate = getEntry(e.getKey());
            return candidate != null && candidate.equals(e);
        }

        public boolean remove(Object o)
        {
            return removeMapping(o) != null;
        }

        public int size()
        {
            return size;
        }

        public void clear()
        {
            HashMap.this.clear();
        }
    }

    private void writeObject(java.io.ObjectOutputStream s)
        throws IOException
    {
        Iterator<Map.Entry<K, V>> i = entrySet().iterator();

        s.defaultWriteObject();

        s.writeInt(table.length);

        s.writeInt(size);

        while (i.hasNext())
        {
            Map.Entry<K, V> e = i.next();
            s.writeObject(e.getKey());
            s.writeObject(e.getValue());
        }
    }

    private void readObject(java.io.ObjectInputStream s)
        throws IOException,
            ClassNotFoundException
    {
        s.defaultReadObject();

        int numBuckets = s.readInt();
        table = new Entry[numBuckets];

        init();

        int size = s.readInt();

        for (int i = 0; i < size; i++)
        {
            K key = (K)s.readObject();
            V value = (V)s.readObject();
            putForCreate(key, value);
        }
    }

    int capacity()
    {
        return table.length;
    }

    float loadFactor()
    {
        return loadFactor;
    }
}
