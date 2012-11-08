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
package jp.co.acroquest.endosnipe.javelin.util.concurrent;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import jp.co.acroquest.endosnipe.javelin.util.AbstractMap;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;

public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>,
        Serializable
{

    /**  */
    private static final long serialVersionUID = 7986208325563360065L;

    static int DEFAULT_INITIAL_CAPACITY = 16;

    static final int MAXIMUM_CAPACITY = 1 << 30;

    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    static final int DEFAULT_SEGMENTS = 16;

    static final int MAX_SEGMENTS = 1 << 16;

    static final int RETRIES_BEFORE_LOCK = 2;

    final int segmentMask;

    final int segmentShift;

    final Segment[] segments;

    transient Set<K> keySet;

    transient Set<Map.Entry<K, V>> entrySet;

    transient Collection<V> values;

    static int hash(Object x)
    {
        int h = x.hashCode();
        h += ~(h << 9);
        h ^= (h >>> 14);
        h += (h << 4);
        h ^= (h >>> 10);
        return h;
    }

    final Segment<K, V> segmentFor(int hash)
    {
        return (Segment<K, V>)segments[(hash >>> segmentShift) & segmentMask];
    }

    static final class HashEntry<K, V>
    {
        final K key;

        final int hash;

        volatile V value;

        final HashEntry<K, V> next;

        HashEntry(K key, int hash, HashEntry<K, V> next, V value)
        {
            this.key = key;
            this.hash = hash;
            this.next = next;
            this.value = value;
        }
    }

    static final class Segment<K, V> extends ReentrantLock implements Serializable
    {

        private static final long serialVersionUID = 2249069246763182397L;

        transient volatile int count;

        transient int modCount;

        transient int threshold;

        transient volatile HashEntry[] table;

        final float loadFactor;

        Segment(int initialCapacity, float lf)
        {
            loadFactor = lf;
            setTable(new HashEntry[initialCapacity]);
        }

        void setTable(HashEntry[] newTable)
        {
            threshold = (int)(newTable.length * loadFactor);
            table = newTable;
        }

        HashEntry<K, V> getFirst(int hash)
        {
            HashEntry[] tab = table;
            return (HashEntry<K, V>)tab[hash & (tab.length - 1)];
        }

        V readValueUnderLock(HashEntry<K, V> e)
        {
            lock();
            try
            {
                return e.value;
            }
            finally
            {
                unlock();
            }
        }

        V get(Object key, int hash)
        {
            if (count != 0)
            {
                HashEntry<K, V> e = getFirst(hash);
                while (e != null)
                {
                    if (e.hash == hash && key.equals(e.key))
                    {
                        V v = e.value;
                        if (v != null)
                            return v;
                        return readValueUnderLock(e);
                    }
                    e = e.next;
                }
            }
            return null;
        }

        boolean containsKey(Object key, int hash)
        {
            if (count != 0)
            {
                HashEntry<K, V> e = getFirst(hash);
                while (e != null)
                {
                    if (e.hash == hash && key.equals(e.key))
                        return true;
                    e = e.next;
                }
            }
            return false;
        }

        boolean containsValue(Object value)
        {
            if (count != 0)
            {
                HashEntry[] tab = table;
                int len = tab.length;
                for (int i = 0; i < len; i++)
                {
                    for (HashEntry<K, V> e = (HashEntry<K, V>)tab[i]; e != null; e = e.next)
                    {
                        V v = e.value;
                        if (v == null)
                            v = readValueUnderLock(e);
                        if (value.equals(v))
                            return true;
                    }
                }
            }
            return false;
        }

        boolean replace(K key, int hash, V oldValue, V newValue)
        {
            lock();
            try
            {
                HashEntry<K, V> e = getFirst(hash);
                while (e != null && (e.hash != hash || !key.equals(e.key)))
                    e = e.next;

                boolean replaced = false;
                if (e != null && oldValue.equals(e.value))
                {
                    replaced = true;
                    e.value = newValue;
                }
                return replaced;
            }
            finally
            {
                unlock();
            }
        }

        V replace(K key, int hash, V newValue)
        {
            lock();
            try
            {
                HashEntry<K, V> e = getFirst(hash);
                while (e != null && (e.hash != hash || !key.equals(e.key)))
                    e = e.next;

                V oldValue = null;
                if (e != null)
                {
                    oldValue = e.value;
                    e.value = newValue;
                }
                return oldValue;
            }
            finally
            {
                unlock();
            }
        }

        V put(K key, int hash, V value, boolean onlyIfAbsent)
        {
            lock();
            try
            {
                int c = count;
                if (c++ > threshold)
                    rehash();
                HashEntry[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> first = (HashEntry<K, V>)tab[index];
                HashEntry<K, V> e = first;
                while (e != null && (e.hash != hash || !key.equals(e.key)))
                    e = e.next;

                V oldValue;
                if (e != null)
                {
                    oldValue = e.value;
                    if (!onlyIfAbsent)
                        e.value = value;
                }
                else
                {
                    oldValue = null;
                    ++modCount;
                    tab[index] = new HashEntry<K, V>(key, hash, first, value);
                    count = c;
                }
                return oldValue;
            }
            finally
            {
                unlock();
            }
        }

        void rehash()
        {
            HashEntry[] oldTable = table;
            int oldCapacity = oldTable.length;
            if (oldCapacity >= MAXIMUM_CAPACITY)
                return;

            HashEntry[] newTable = new HashEntry[oldCapacity << 1];
            threshold = (int)(newTable.length * loadFactor);
            int sizeMask = newTable.length - 1;
            for (int i = 0; i < oldCapacity; i++)
            {

                HashEntry<K, V> e = (HashEntry<K, V>)oldTable[i];

                if (e != null)
                {
                    HashEntry<K, V> next = e.next;
                    int idx = e.hash & sizeMask;

                    if (next == null)
                        newTable[idx] = e;

                    else
                    {

                        HashEntry<K, V> lastRun = e;
                        int lastIdx = idx;
                        for (HashEntry<K, V> last = next; last != null; last = last.next)
                        {
                            int k = last.hash & sizeMask;
                            if (k != lastIdx)
                            {
                                lastIdx = k;
                                lastRun = last;
                            }
                        }
                        newTable[lastIdx] = lastRun;

                        for (HashEntry<K, V> p = e; p != lastRun; p = p.next)
                        {
                            int k = p.hash & sizeMask;
                            HashEntry<K, V> n = (HashEntry<K, V>)newTable[k];
                            newTable[k] = new HashEntry<K, V>(p.key, p.hash, n, p.value);
                        }
                    }
                }
            }
            table = newTable;
        }

        V remove(Object key, int hash, Object value)
        {
            lock();
            try
            {
                int c = count - 1;
                HashEntry[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> first = (HashEntry<K, V>)tab[index];
                HashEntry<K, V> e = first;
                while (e != null && (e.hash != hash || !key.equals(e.key)))
                    e = e.next;

                V oldValue = null;
                if (e != null)
                {
                    V v = e.value;
                    if (value == null || value.equals(v))
                    {
                        oldValue = v;

                        ++modCount;
                        HashEntry<K, V> newFirst = e.next;
                        for (HashEntry<K, V> p = first; p != e; p = p.next)
                            newFirst = new HashEntry<K, V>(p.key, p.hash, newFirst, p.value);
                        tab[index] = newFirst;
                        count = c;
                    }
                }
                return oldValue;
            }
            finally
            {
                unlock();
            }
        }

        void clear()
        {
            if (count != 0)
            {
                lock();
                try
                {
                    HashEntry[] tab = table;
                    for (int i = 0; i < tab.length; i++)
                        tab[i] = null;
                    ++modCount;
                    count = 0;
                }
                finally
                {
                    unlock();
                }
            }
        }
    }

    public ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel)
    {
        if (!(loadFactor > 0) || initialCapacity < 0 || concurrencyLevel <= 0)
            throw new IllegalArgumentException();

        if (concurrencyLevel > MAX_SEGMENTS)
            concurrencyLevel = MAX_SEGMENTS;

        int sshift = 0;
        int ssize = 1;
        while (ssize < concurrencyLevel)
        {
            ++sshift;
            ssize <<= 1;
        }
        segmentShift = 32 - sshift;
        segmentMask = ssize - 1;
        this.segments = new Segment[ssize];

        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        int c = initialCapacity / ssize;
        if (c * ssize < initialCapacity)
            ++c;
        int cap = 1;
        while (cap < c)
            cap <<= 1;

        for (int i = 0; i < this.segments.length; ++i)
            this.segments[i] = new Segment<K, V>(cap, loadFactor);
    }

    public ConcurrentHashMap(int initialCapacity)
    {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, DEFAULT_SEGMENTS);
    }

    public ConcurrentHashMap()
    {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_SEGMENTS);
    }

    public ConcurrentHashMap(Map<? extends K, ? extends V> t)
    {
        this(Math.max((int)(t.size() / DEFAULT_LOAD_FACTOR) + 1, 11), DEFAULT_LOAD_FACTOR,
                DEFAULT_SEGMENTS);
        putAll(t);
    }

    public boolean isEmpty()
    {
        final Segment[] segments = this.segments;

        int[] mc = new int[segments.length];
        int mcsum = 0;
        for (int i = 0; i < segments.length; ++i)
        {
            if (segments[i].count != 0)
                return false;
            else
                mcsum += mc[i] = segments[i].modCount;
        }

        if (mcsum != 0)
        {
            for (int i = 0; i < segments.length; ++i)
            {
                if (segments[i].count != 0 || mc[i] != segments[i].modCount)
                    return false;
            }
        }
        return true;
    }

    public int size()
    {
        final Segment[] segments = this.segments;
        long sum = 0;
        long check = 0;
        int[] mc = new int[segments.length];

        for (int k = 0; k < RETRIES_BEFORE_LOCK; ++k)
        {
            check = 0;
            sum = 0;
            int mcsum = 0;
            for (int i = 0; i < segments.length; ++i)
            {
                sum += segments[i].count;
                mcsum += mc[i] = segments[i].modCount;
            }
            if (mcsum != 0)
            {
                for (int i = 0; i < segments.length; ++i)
                {
                    check += segments[i].count;
                    if (mc[i] != segments[i].modCount)
                    {
                        check = -1;
                        break;
                    }
                }
            }
            if (check == sum)
                break;
        }
        if (check != sum)
        {
            sum = 0;
            for (int i = 0; i < segments.length; ++i)
                segments[i].lock();
            for (int i = 0; i < segments.length; ++i)
                sum += segments[i].count;
            for (int i = 0; i < segments.length; ++i)
                segments[i].unlock();
        }
        if (sum > Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        else
            return (int)sum;
    }

    public V get(Object key)
    {
        int hash = hash(key);
        return segmentFor(hash).get(key, hash);
    }

    public boolean containsKey(Object key)
    {
        int hash = hash(key);
        return segmentFor(hash).containsKey(key, hash);
    }

    public boolean containsValue(Object value)
    {
        if (value == null)
            throw new NullPointerException();

        final Segment[] segments = this.segments;
        int[] mc = new int[segments.length];

        for (int k = 0; k < RETRIES_BEFORE_LOCK; ++k)
        {
            int sum = 0;
            int mcsum = 0;
            for (int i = 0; i < segments.length; ++i)
            {
                int c = segments[i].count;
                mcsum += mc[i] = segments[i].modCount;
                if (segments[i].containsValue(value))
                    return true;
            }
            boolean cleanSweep = true;
            if (mcsum != 0)
            {
                for (int i = 0; i < segments.length; ++i)
                {
                    int c = segments[i].count;
                    if (mc[i] != segments[i].modCount)
                    {
                        cleanSweep = false;
                        break;
                    }
                }
            }
            if (cleanSweep)
                return false;
        }

        for (int i = 0; i < segments.length; ++i)
            segments[i].lock();
        boolean found = false;
        try
        {
            for (int i = 0; i < segments.length; ++i)
            {
                if (segments[i].containsValue(value))
                {
                    found = true;
                    break;
                }
            }
        }
        finally
        {
            for (int i = 0; i < segments.length; ++i)
                segments[i].unlock();
        }
        return found;
    }

    public boolean contains(Object value)
    {
        return containsValue(value);
    }

    public V put(K key, V value)
    {
        if (value == null)
            throw new NullPointerException();
        int hash = hash(key);
        return segmentFor(hash).put(key, hash, value, false);
    }

    public V putIfAbsent(K key, V value)
    {
        if (value == null)
            throw new NullPointerException();
        int hash = hash(key);
        return segmentFor(hash).put(key, hash, value, true);
    }

    public void putAll(Map<? extends K, ? extends V> t)
    {
        for (Iterator<? extends Map.Entry<? extends K, ? extends V>> it =
                (Iterator<? extends Map.Entry<? extends K, ? extends V>>)t.entrySet().iterator(); it.hasNext();)
        {
            Entry<? extends K, ? extends V> e = it.next();
            put(e.getKey(), e.getValue());
        }
    }

    public V remove(Object key)
    {
        int hash = hash(key);
        return segmentFor(hash).remove(key, hash, null);
    }

    public boolean remove(Object key, Object value)
    {
        int hash = hash(key);
        return segmentFor(hash).remove(key, hash, value) != null;
    }

    public boolean replace(K key, V oldValue, V newValue)
    {
        if (oldValue == null || newValue == null)
            throw new NullPointerException();
        int hash = hash(key);
        return segmentFor(hash).replace(key, hash, oldValue, newValue);
    }

    public V replace(K key, V value)
    {
        if (value == null)
            throw new NullPointerException();
        int hash = hash(key);
        return segmentFor(hash).replace(key, hash, value);
    }

    public void clear()
    {
        for (int i = 0; i < segments.length; ++i)
            segments[i].clear();
    }

    public Set<K> keySet()
    {
        Set<K> ks = keySet;
        return (ks != null) ? ks : (keySet = new KeySet());
    }

    public Collection<V> values()
    {
        Collection<V> vs = values;
        return (vs != null) ? vs : (values = new Values());
    }

    public Set<Map.Entry<K, V>> entrySet()
    {
        Set<Map.Entry<K, V>> es = entrySet;
        return (es != null) ? es : (entrySet = (Set<Map.Entry<K, V>>)(Set)new EntrySet());
    }

    public Enumeration<K> keys()
    {
        return new KeyIterator();
    }

    public Enumeration<V> elements()
    {
        return new ValueIterator();
    }

    abstract class HashIterator
    {
        int nextSegmentIndex;

        int nextTableIndex;

        HashEntry[] currentTable;

        HashEntry<K, V> nextEntry;

        HashEntry<K, V> lastReturned;

        HashIterator()
        {
            nextSegmentIndex = segments.length - 1;
            nextTableIndex = -1;
            advance();
        }

        public boolean hasMoreElements()
        {
            return hasNext();
        }

        final void advance()
        {
            if (nextEntry != null && (nextEntry = nextEntry.next) != null)
                return;

            while (nextTableIndex >= 0)
            {
                if ((nextEntry = (HashEntry<K, V>)currentTable[nextTableIndex--]) != null)
                    return;
            }

            while (nextSegmentIndex >= 0)
            {
                Segment<K, V> seg = (Segment<K, V>)segments[nextSegmentIndex--];
                if (seg.count != 0)
                {
                    currentTable = seg.table;
                    for (int j = currentTable.length - 1; j >= 0; --j)
                    {
                        if ((nextEntry = (HashEntry<K, V>)currentTable[j]) != null)
                        {
                            nextTableIndex = j - 1;
                            return;
                        }
                    }
                }
            }
        }

        public boolean hasNext()
        {
            return nextEntry != null;
        }

        HashEntry<K, V> nextEntry()
        {
            if (nextEntry == null)
                throw new NoSuchElementException();
            lastReturned = nextEntry;
            advance();
            return lastReturned;
        }

        public void remove()
        {
            if (lastReturned == null)
                throw new IllegalStateException();
            ConcurrentHashMap.this.remove(lastReturned.key);
            lastReturned = null;
        }
    }

    final class KeyIterator extends HashIterator implements Iterator<K>, Enumeration<K>
    {
        public K next()
        {
            return super.nextEntry().key;
        }

        public K nextElement()
        {
            return super.nextEntry().key;
        }
    }

    final class ValueIterator extends HashIterator implements Iterator<V>, Enumeration<V>
    {
        public V next()
        {
            return super.nextEntry().value;
        }

        public V nextElement()
        {
            return super.nextEntry().value;
        }
    }

    final class EntryIterator extends HashIterator implements Map.Entry<K, V>,
            Iterator<Entry<K, V>>
    {
        public Map.Entry<K, V> next()
        {
            nextEntry();
            return this;
        }

        public K getKey()
        {
            if (lastReturned == null)
                throw new IllegalStateException("Entry was removed");
            return lastReturned.key;
        }

        public V getValue()
        {
            if (lastReturned == null)
                throw new IllegalStateException("Entry was removed");
            return ConcurrentHashMap.this.get(lastReturned.key);
        }

        public V setValue(V value)
        {
            if (lastReturned == null)
                throw new IllegalStateException("Entry was removed");
            return ConcurrentHashMap.this.put(lastReturned.key, value);
        }

        public boolean equals(Object o)
        {

            if (lastReturned == null)
                return super.equals(o);
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry)o;
            return eq(getKey(), e.getKey()) && eq(getValue(), e.getValue());
        }

        public int hashCode()
        {

            if (lastReturned == null)
                return super.hashCode();

            Object k = getKey();
            Object v = getValue();
            return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
        }

        public String toString()
        {

            if (lastReturned == null)
                return super.toString();
            else
                return getKey() + "=" + getValue();
        }

        boolean eq(Object o1, Object o2)
        {
            return (o1 == null ? o2 == null : o1.equals(o2));
        }

    }

    final class KeySet extends AbstractSet<K>
    {
        public Iterator<K> iterator()
        {
            return new KeyIterator();
        }

        public int size()
        {
            return ConcurrentHashMap.this.size();
        }

        public boolean contains(Object o)
        {
            return ConcurrentHashMap.this.containsKey(o);
        }

        public boolean remove(Object o)
        {
            return ConcurrentHashMap.this.remove(o) != null;
        }

        public void clear()
        {
            ConcurrentHashMap.this.clear();
        }

        public Object[] toArray()
        {
            Collection<K> c = new ArrayList<K>();
            for (Iterator<K> i = iterator(); i.hasNext();)
                c.add(i.next());
            return c.toArray();
        }

        public <T> T[] toArray(T[] a)
        {
            Collection<K> c = new ArrayList<K>();
            for (Iterator<K> i = iterator(); i.hasNext();)
                c.add(i.next());
            return c.toArray(a);
        }
    }

    final class Values extends AbstractCollection<V>
    {
        public Iterator<V> iterator()
        {
            return new ValueIterator();
        }

        public int size()
        {
            return ConcurrentHashMap.this.size();
        }

        public boolean contains(Object o)
        {
            return ConcurrentHashMap.this.containsValue(o);
        }

        public void clear()
        {
            ConcurrentHashMap.this.clear();
        }

        public Object[] toArray()
        {
            Collection<V> c = new ArrayList<V>();
            for (Iterator<V> i = iterator(); i.hasNext();)
                c.add(i.next());
            return c.toArray();
        }

        public <T> T[] toArray(T[] a)
        {
            Collection<V> c = new ArrayList<V>();
            for (Iterator<V> i = iterator(); i.hasNext();)
                c.add(i.next());
            return c.toArray(a);
        }
    }

    final class EntrySet extends AbstractSet<Map.Entry<K, V>>
    {
        public Iterator<Map.Entry<K, V>> iterator()
        {
            return new EntryIterator();
        }

        public boolean contains(Object o)
        {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<K, V> e = (Map.Entry<K, V>)o;
            V v = ConcurrentHashMap.this.get(e.getKey());
            return v != null && v.equals(e.getValue());
        }

        public boolean remove(Object o)
        {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<K, V> e = (Map.Entry<K, V>)o;
            return ConcurrentHashMap.this.remove(e.getKey(), e.getValue());
        }

        public int size()
        {
            return ConcurrentHashMap.this.size();
        }

        public void clear()
        {
            ConcurrentHashMap.this.clear();
        }

        public Object[] toArray()
        {

            Collection<Map.Entry<K, V>> c = new ArrayList<Map.Entry<K, V>>(size());
            for (Iterator<Map.Entry<K, V>> i = iterator(); i.hasNext();)
                c.add(new SimpleEntry<K, V>(i.next()));
            return c.toArray();
        }

        public <T> T[] toArray(T[] a)
        {
            Collection<Map.Entry<K, V>> c = new ArrayList<Map.Entry<K, V>>(size());
            for (Iterator<Map.Entry<K, V>> i = iterator(); i.hasNext();)
                c.add(new SimpleEntry<K, V>(i.next()));
            return c.toArray(a);
        }

    }

    static final class SimpleEntry<K, V> implements Entry<K, V>
    {
        K key;

        V value;

        public SimpleEntry(K key, V value)
        {
            this.key = key;
            this.value = value;
        }

        public SimpleEntry(Entry<K, V> e)
        {
            this.key = e.getKey();
            this.value = e.getValue();
        }

        public K getKey()
        {
            return key;
        }

        public V getValue()
        {
            return value;
        }

        public V setValue(V value)
        {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o)
        {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry)o;
            return eq(key, e.getKey()) && eq(value, e.getValue());
        }

        public int hashCode()
        {
            return ((key == null) ? 0 : key.hashCode()) ^ ((value == null) ? 0 : value.hashCode());
        }

        public String toString()
        {
            return key + "=" + value;
        }

        static boolean eq(Object o1, Object o2)
        {
            return (o1 == null ? o2 == null : o1.equals(o2));
        }
    }

    private void writeObject(java.io.ObjectOutputStream s)
        throws IOException
    {
        s.defaultWriteObject();

        for (int k = 0; k < segments.length; ++k)
        {
            Segment<K, V> seg = (Segment<K, V>)segments[k];
            seg.lock();
            try
            {
                HashEntry[] tab = seg.table;
                for (int i = 0; i < tab.length; ++i)
                {
                    for (HashEntry<K, V> e = (HashEntry<K, V>)tab[i]; e != null; e = e.next)
                    {
                        s.writeObject(e.key);
                        s.writeObject(e.value);
                    }
                }
            }
            finally
            {
                seg.unlock();
            }
        }
        s.writeObject(null);
        s.writeObject(null);
    }

    private void readObject(java.io.ObjectInputStream s)
        throws IOException,
            ClassNotFoundException
    {
        s.defaultReadObject();

        for (int i = 0; i < segments.length; ++i)
        {
            segments[i].setTable(new HashEntry[1]);
        }

        for (;;)
        {
            K key = (K)s.readObject();
            V value = (V)s.readObject();
            if (key == null)
                break;
            put(key, value);
        }
    }

}
