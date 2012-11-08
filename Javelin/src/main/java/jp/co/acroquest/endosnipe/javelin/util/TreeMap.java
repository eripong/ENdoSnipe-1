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

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public class TreeMap<K, V> extends AbstractMap<K, V> implements SortedMap<K, V>, Cloneable,
        java.io.Serializable
{

    /**  */
    private static final long serialVersionUID = -5489490343290508150L;

    private Comparator<? super K> comparator = null;

    private transient Entry<K, V> root = null;

    private transient int size = 0;

    private transient int modCount = 0;

    private void incrementSize()
    {
        modCount++;
        size++;
    }

    private void decrementSize()
    {
        modCount++;
        size--;
    }

    public TreeMap()
    {
    }

    public TreeMap(Comparator<? super K> c)
    {
        this.comparator = c;
    }

    public TreeMap(Map<? extends K, ? extends V> m)
    {
        putAll(m);
    }

    public TreeMap(SortedMap<K, ? extends V> m)
    {
        comparator = m.comparator();
        try
        {
            buildFromSorted(m.size(), m.entrySet().iterator(), null, null);
        }
        catch (java.io.IOException cannotHappen)
        {
        }
        catch (ClassNotFoundException cannotHappen)
        {
        }
    }

    public int size()
    {
        return size;
    }

    public boolean containsKey(Object key)
    {
        return getEntry(key) != null;
    }

    public boolean containsValue(Object value)
    {
        return (root == null ? false : (value == null ? valueSearchNull(root)
                : valueSearchNonNull(root, value)));
    }

    private boolean valueSearchNull(Entry n)
    {
        if (n.value == null)
            return true;

        return (n.left != null && valueSearchNull(n.left))
                || (n.right != null && valueSearchNull(n.right));
    }

    private boolean valueSearchNonNull(Entry n, Object value)
    {

        if (value.equals(n.value))
            return true;

        return (n.left != null && valueSearchNonNull(n.left, value))
                || (n.right != null && valueSearchNonNull(n.right, value));
    }

    public V get(Object key)
    {
        Entry<K, V> p = getEntry(key);
        return (p == null ? null : p.value);
    }

    public Comparator<? super K> comparator()
    {
        return comparator;
    }

    public K firstKey()
    {
        return key(firstEntry());
    }

    public K lastKey()
    {
        return key(lastEntry());
    }

    public void putAll(Map<? extends K, ? extends V> map)
    {
        int mapSize = map.size();
        if (size == 0 && mapSize != 0 && map instanceof SortedMap)
        {
            Comparator c = ((SortedMap)map).comparator();
            if (c == comparator || (c != null && c.equals(comparator)))
            {
                ++modCount;
                try
                {
                    buildFromSorted(mapSize, map.entrySet().iterator(), null, null);
                }
                catch (java.io.IOException cannotHappen)
                {
                }
                catch (ClassNotFoundException cannotHappen)
                {
                }
                return;
            }
        }
        super.putAll(map);
    }

    private Entry<K, V> getEntry(Object key)
    {
        Entry<K, V> p = root;
        K k = (K)key;
        while (p != null)
        {
            int cmp = compare(k, p.key);
            if (cmp == 0)
                return p;
            else if (cmp < 0)
                p = p.left;
            else
                p = p.right;
        }
        return null;
    }

    private Entry<K, V> getCeilEntry(K key)
    {
        Entry<K, V> p = root;
        if (p == null)
            return null;

        while (true)
        {
            int cmp = compare(key, p.key);
            if (cmp == 0)
            {
                return p;
            }
            else if (cmp < 0)
            {
                if (p.left != null)
                    p = p.left;
                else
                    return p;
            }
            else
            {
                if (p.right != null)
                {
                    p = p.right;
                }
                else
                {
                    Entry<K, V> parent = p.parent;
                    Entry<K, V> ch = p;
                    while (parent != null && ch == parent.right)
                    {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            }
        }
    }

    private Entry<K, V> getPrecedingEntry(K key)
    {
        Entry<K, V> p = root;
        if (p == null)
            return null;

        while (true)
        {
            int cmp = compare(key, p.key);
            if (cmp > 0)
            {
                if (p.right != null)
                    p = p.right;
                else
                    return p;
            }
            else
            {
                if (p.left != null)
                {
                    p = p.left;
                }
                else
                {
                    Entry<K, V> parent = p.parent;
                    Entry<K, V> ch = p;
                    while (parent != null && ch == parent.left)
                    {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            }
        }
    }

    private static <K> K key(Entry<K, ?> e)
    {
        if (e == null)
            throw new NoSuchElementException();
        return e.key;
    }

    public V put(K key, V value)
    {
        Entry<K, V> t = root;

        if (t == null)
        {
            incrementSize();
            root = new Entry<K, V>(key, value, null);
            return null;
        }

        while (true)
        {
            int cmp = compare(key, t.key);
            if (cmp == 0)
            {
                return t.setValue(value);
            }
            else if (cmp < 0)
            {
                if (t.left != null)
                {
                    t = t.left;
                }
                else
                {
                    incrementSize();
                    t.left = new Entry<K, V>(key, value, t);
                    fixAfterInsertion(t.left);
                    return null;
                }
            }
            else
            {
                if (t.right != null)
                {
                    t = t.right;
                }
                else
                {
                    incrementSize();
                    t.right = new Entry<K, V>(key, value, t);
                    fixAfterInsertion(t.right);
                    return null;
                }
            }
        }
    }

    public V remove(Object key)
    {
        Entry<K, V> p = getEntry(key);
        if (p == null)
            return null;

        V oldValue = p.value;
        deleteEntry(p);
        return oldValue;
    }

    public void clear()
    {
        modCount++;
        size = 0;
        root = null;
    }

    public Object clone()
    {
        TreeMap<K, V> clone = null;
        try
        {
            clone = (TreeMap<K, V>)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError();
        }

        clone.root = null;
        clone.size = 0;
        clone.modCount = 0;
        clone.entrySet = null;

        try
        {
            clone.buildFromSorted(size, entrySet().iterator(), null, null);
        }
        catch (java.io.IOException cannotHappen)
        {
        }
        catch (ClassNotFoundException cannotHappen)
        {
        }

        return clone;
    }

    private transient volatile Set<Map.Entry<K, V>> entrySet = null;

    public Set<K> keySet()
    {
        if (keySet == null)
        {
            keySet = new AbstractSet<K>() {
                public Iterator<K> iterator()
                {
                    return new KeyIterator();
                }

                public int size()
                {
                    return TreeMap.this.size();
                }

                public boolean contains(Object o)
                {
                    return containsKey(o);
                }

                public boolean remove(Object o)
                {
                    int oldSize = size;
                    TreeMap.this.remove(o);
                    return size != oldSize;
                }

                public void clear()
                {
                    TreeMap.this.clear();
                }
            };
        }
        return keySet;
    }

    public Collection<V> values()
    {
        if (values == null)
        {
            values = new AbstractCollection<V>() {
                public Iterator<V> iterator()
                {
                    return new ValueIterator();
                }

                public int size()
                {
                    return TreeMap.this.size();
                }

                public boolean contains(Object o)
                {
                    for (Entry<K, V> e = firstEntry(); e != null; e = successor(e))
                        if (valEquals(e.getValue(), o))
                            return true;
                    return false;
                }

                public boolean remove(Object o)
                {
                    for (Entry<K, V> e = firstEntry(); e != null; e = successor(e))
                    {
                        if (valEquals(e.getValue(), o))
                        {
                            deleteEntry(e);
                            return true;
                        }
                    }
                    return false;
                }

                public void clear()
                {
                    TreeMap.this.clear();
                }
            };
        }
        return values;
    }

    public Set<Map.Entry<K, V>> entrySet()
    {
        if (entrySet == null)
        {
            entrySet = new AbstractSet<Map.Entry<K, V>>() {
                public Iterator<Map.Entry<K, V>> iterator()
                {
                    return new EntryIterator();
                }

                public boolean contains(Object o)
                {
                    if (!(o instanceof Map.Entry))
                        return false;
                    Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
                    V value = entry.getValue();
                    Entry<K, V> p = getEntry(entry.getKey());
                    return p != null && valEquals(p.getValue(), value);
                }

                public boolean remove(Object o)
                {
                    if (!(o instanceof Map.Entry))
                        return false;
                    Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
                    V value = entry.getValue();
                    Entry<K, V> p = getEntry(entry.getKey());
                    if (p != null && valEquals(p.getValue(), value))
                    {
                        deleteEntry(p);
                        return true;
                    }
                    return false;
                }

                public int size()
                {
                    return TreeMap.this.size();
                }

                public void clear()
                {
                    TreeMap.this.clear();
                }
            };
        }
        return entrySet;
    }

    public SortedMap<K, V> subMap(K fromKey, K toKey)
    {
        return new SubMap(fromKey, toKey);
    }

    public SortedMap<K, V> headMap(K toKey)
    {
        return new SubMap(toKey, true);
    }

    public SortedMap<K, V> tailMap(K fromKey)
    {
        return new SubMap(fromKey, false);
    }

    private class SubMap extends AbstractMap<K, V> implements SortedMap<K, V>, java.io.Serializable
    {
        private static final long serialVersionUID = -6520786458950516097L;

        private boolean fromStart = false, toEnd = false;

        private K fromKey, toKey;

        SubMap(K fromKey, K toKey)
        {
            if (compare(fromKey, toKey) > 0)
                throw new IllegalArgumentException("fromKey > toKey");
            this.fromKey = fromKey;
            this.toKey = toKey;
        }

        SubMap(K key, boolean headMap)
        {
            compare(key, key);

            if (headMap)
            {
                fromStart = true;
                toKey = key;
            }
            else
            {
                toEnd = true;
                fromKey = key;
            }
        }

        SubMap(boolean fromStart, K fromKey, boolean toEnd, K toKey)
        {
            this.fromStart = fromStart;
            this.fromKey = fromKey;
            this.toEnd = toEnd;
            this.toKey = toKey;
        }

        public boolean isEmpty()
        {
            return entrySet.isEmpty();
        }

        public boolean containsKey(Object key)
        {
            return inRange((K)key) && TreeMap.this.containsKey(key);
        }

        public V get(Object key)
        {
            if (!inRange((K)key))
                return null;
            return TreeMap.this.get(key);
        }

        public V put(K key, V value)
        {
            if (!inRange(key))
                throw new IllegalArgumentException("key out of range");
            return TreeMap.this.put(key, value);
        }

        public Comparator<? super K> comparator()
        {
            return comparator;
        }

        public K firstKey()
        {
            TreeMap.Entry<K, V> e = fromStart ? firstEntry() : getCeilEntry(fromKey);
            K first = key(e);
            if (!toEnd && compare(first, toKey) >= 0)
                throw (new NoSuchElementException());
            return first;
        }

        public K lastKey()
        {
            TreeMap.Entry<K, V> e = toEnd ? lastEntry() : getPrecedingEntry(toKey);
            K last = key(e);
            if (!fromStart && compare(last, fromKey) < 0)
                throw (new NoSuchElementException());
            return last;
        }

        private transient Set<Map.Entry<K, V>> entrySet = new EntrySetView();

        public Set<Map.Entry<K, V>> entrySet()
        {
            return entrySet;
        }

        private class EntrySetView extends AbstractSet<Map.Entry<K, V>>
        {
            private transient int size = -1, sizeModCount;

            public int size()
            {
                if (size == -1 || sizeModCount != TreeMap.this.modCount)
                {
                    size = 0;
                    sizeModCount = TreeMap.this.modCount;
                    Iterator i = iterator();
                    while (i.hasNext())
                    {
                        size++;
                        i.next();
                    }
                }
                return size;
            }

            public boolean isEmpty()
            {
                return !iterator().hasNext();
            }

            public boolean contains(Object o)
            {
                if (!(o instanceof Map.Entry))
                    return false;
                Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
                K key = entry.getKey();
                if (!inRange(key))
                    return false;
                TreeMap.Entry node = getEntry(key);
                return node != null && valEquals(node.getValue(), entry.getValue());
            }

            public boolean remove(Object o)
            {
                if (!(o instanceof Map.Entry))
                    return false;
                Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
                K key = entry.getKey();
                if (!inRange(key))
                    return false;
                TreeMap.Entry<K, V> node = getEntry(key);
                if (node != null && valEquals(node.getValue(), entry.getValue()))
                {
                    deleteEntry(node);
                    return true;
                }
                return false;
            }

            public Iterator<Map.Entry<K, V>> iterator()
            {
                return new SubMapEntryIterator((fromStart ? firstEntry() : getCeilEntry(fromKey)),
                                               (toEnd ? null : getCeilEntry(toKey)));
            }
        }

        public SortedMap<K, V> subMap(K fromKey, K toKey)
        {
            if (!inRange2(fromKey))
                throw new IllegalArgumentException("fromKey out of range");
            if (!inRange2(toKey))
                throw new IllegalArgumentException("toKey out of range");
            return new SubMap(fromKey, toKey);
        }

        public SortedMap<K, V> headMap(K toKey)
        {
            if (!inRange2(toKey))
                throw new IllegalArgumentException("toKey out of range");
            return new SubMap(fromStart, fromKey, false, toKey);
        }

        public SortedMap<K, V> tailMap(K fromKey)
        {
            if (!inRange2(fromKey))
                throw new IllegalArgumentException("fromKey out of range");
            return new SubMap(false, fromKey, toEnd, toKey);
        }

        private boolean inRange(K key)
        {
            return (fromStart || compare(key, fromKey) >= 0) && (toEnd || compare(key, toKey) < 0);
        }

        private boolean inRange2(K key)
        {
            return (fromStart || compare(key, fromKey) >= 0) && (toEnd || compare(key, toKey) <= 0);
        }
    }

    private abstract class PrivateEntryIterator<T> implements Iterator<T>
    {
        private int expectedModCount = TreeMap.this.modCount;

        private Entry<K, V> lastReturned = null;

        Entry<K, V> next;

        PrivateEntryIterator()
        {
            next = firstEntry();
        }

        PrivateEntryIterator(Entry<K, V> first)
        {
            next = first;
        }

        public boolean hasNext()
        {
            return next != null;
        }

        final Entry<K, V> nextEntry()
        {
            if (next == null)
                throw new NoSuchElementException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            lastReturned = next;
            next = successor(next);
            return lastReturned;
        }

        public void remove()
        {
            if (lastReturned == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            if (lastReturned.left != null && lastReturned.right != null)
                next = lastReturned;
            deleteEntry(lastReturned);
            expectedModCount++;
            lastReturned = null;
        }
    }

    private class EntryIterator extends PrivateEntryIterator<Map.Entry<K, V>>
    {
        public Map.Entry<K, V> next()
        {
            return nextEntry();
        }
    }

    private class KeyIterator extends PrivateEntryIterator<K>
    {
        public K next()
        {
            return nextEntry().key;
        }
    }

    private class ValueIterator extends PrivateEntryIterator<V>
    {
        public V next()
        {
            return nextEntry().value;
        }
    }

    private class SubMapEntryIterator extends PrivateEntryIterator<Map.Entry<K, V>>
    {
        private final K firstExcludedKey;

        SubMapEntryIterator(Entry<K, V> first, Entry<K, V> firstExcluded)
        {
            super(first);
            firstExcludedKey = (firstExcluded == null ? null : firstExcluded.key);
        }

        public boolean hasNext()
        {
            return next != null && next.key != firstExcludedKey;
        }

        public Map.Entry<K, V> next()
        {
            if (next == null || next.key == firstExcludedKey)
                throw new NoSuchElementException();
            return nextEntry();
        }
    }

    private int compare(K k1, K k2)
    {
        return (comparator == null ? ((Comparable<K>)k1).compareTo(k2) : comparator.compare((K)k1,
                                                                                            (K)k2));
    }

    private static boolean valEquals(Object o1, Object o2)
    {
        return (o1 == null ? o2 == null : o1.equals(o2));
    }

    private static final boolean RED = false;

    private static final boolean BLACK = true;

    static class Entry<K, V> implements Map.Entry<K, V>
    {
        K key;

        V value;

        Entry<K, V> left = null;

        Entry<K, V> right = null;

        Entry<K, V> parent;

        boolean color = BLACK;

        Entry(K key, V value, Entry<K, V> parent)
        {
            this.key = key;
            this.value = value;
            this.parent = parent;
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

            return valEquals(key, e.getKey()) && valEquals(value, e.getValue());
        }

        public int hashCode()
        {
            int keyHash = (key == null ? 0 : key.hashCode());
            int valueHash = (value == null ? 0 : value.hashCode());
            return keyHash ^ valueHash;
        }

        public String toString()
        {
            return key + "=" + value;
        }
    }

    private Entry<K, V> firstEntry()
    {
        Entry<K, V> p = root;
        if (p != null)
            while (p.left != null)
                p = p.left;
        return p;
    }

    private Entry<K, V> lastEntry()
    {
        Entry<K, V> p = root;
        if (p != null)
            while (p.right != null)
                p = p.right;
        return p;
    }

    private Entry<K, V> successor(Entry<K, V> t)
    {
        if (t == null)
            return null;
        else if (t.right != null)
        {
            Entry<K, V> p = t.right;
            while (p.left != null)
                p = p.left;
            return p;
        }
        else
        {
            Entry<K, V> p = t.parent;
            Entry<K, V> ch = t;
            while (p != null && ch == p.right)
            {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    private static <K, V> boolean colorOf(Entry<K, V> p)
    {
        return (p == null ? BLACK : p.color);
    }

    private static <K, V> Entry<K, V> parentOf(Entry<K, V> p)
    {
        return (p == null ? null : p.parent);
    }

    private static <K, V> void setColor(Entry<K, V> p, boolean c)
    {
        if (p != null)
            p.color = c;
    }

    private static <K, V> Entry<K, V> leftOf(Entry<K, V> p)
    {
        return (p == null) ? null : p.left;
    }

    private static <K, V> Entry<K, V> rightOf(Entry<K, V> p)
    {
        return (p == null) ? null : p.right;
    }

    private void rotateLeft(Entry<K, V> p)
    {
        Entry<K, V> r = p.right;
        p.right = r.left;
        if (r.left != null)
            r.left.parent = p;
        r.parent = p.parent;
        if (p.parent == null)
            root = r;
        else if (p.parent.left == p)
            p.parent.left = r;
        else
            p.parent.right = r;
        r.left = p;
        p.parent = r;
    }

    private void rotateRight(Entry<K, V> p)
    {
        Entry<K, V> l = p.left;
        p.left = l.right;
        if (l.right != null)
            l.right.parent = p;
        l.parent = p.parent;
        if (p.parent == null)
            root = l;
        else if (p.parent.right == p)
            p.parent.right = l;
        else
            p.parent.left = l;
        l.right = p;
        p.parent = l;
    }

    private void fixAfterInsertion(Entry<K, V> x)
    {
        x.color = RED;

        while (x != null && x != root && x.parent.color == RED)
        {
            if (parentOf(x) == leftOf(parentOf(parentOf(x))))
            {
                Entry<K, V> y = rightOf(parentOf(parentOf(x)));
                if (colorOf(y) == RED)
                {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                }
                else
                {
                    if (x == rightOf(parentOf(x)))
                    {
                        x = parentOf(x);
                        rotateLeft(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    if (parentOf(parentOf(x)) != null)
                        rotateRight(parentOf(parentOf(x)));
                }
            }
            else
            {
                Entry<K, V> y = leftOf(parentOf(parentOf(x)));
                if (colorOf(y) == RED)
                {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                }
                else
                {
                    if (x == leftOf(parentOf(x)))
                    {
                        x = parentOf(x);
                        rotateRight(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    if (parentOf(parentOf(x)) != null)
                        rotateLeft(parentOf(parentOf(x)));
                }
            }
        }
        root.color = BLACK;
    }

    private void deleteEntry(Entry<K, V> p)
    {
        decrementSize();

        if (p.left != null && p.right != null)
        {
            Entry<K, V> s = successor(p);
            p.key = s.key;
            p.value = s.value;
            p = s;
        }

        Entry<K, V> replacement = (p.left != null ? p.left : p.right);

        if (replacement != null)
        {

            replacement.parent = p.parent;
            if (p.parent == null)
                root = replacement;
            else if (p == p.parent.left)
                p.parent.left = replacement;
            else
                p.parent.right = replacement;

            p.left = p.right = p.parent = null;

            if (p.color == BLACK)
                fixAfterDeletion(replacement);
        }
        else if (p.parent == null)
        {
            root = null;
        }
        else
        {
            if (p.color == BLACK)
                fixAfterDeletion(p);

            if (p.parent != null)
            {
                if (p == p.parent.left)
                    p.parent.left = null;
                else if (p == p.parent.right)
                    p.parent.right = null;
                p.parent = null;
            }
        }
    }

    private void fixAfterDeletion(Entry<K, V> x)
    {
        while (x != root && colorOf(x) == BLACK)
        {
            if (x == leftOf(parentOf(x)))
            {
                Entry<K, V> sib = rightOf(parentOf(x));

                if (colorOf(sib) == RED)
                {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateLeft(parentOf(x));
                    sib = rightOf(parentOf(x));
                }

                if (colorOf(leftOf(sib)) == BLACK && colorOf(rightOf(sib)) == BLACK)
                {
                    setColor(sib, RED);
                    x = parentOf(x);
                }
                else
                {
                    if (colorOf(rightOf(sib)) == BLACK)
                    {
                        setColor(leftOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateRight(sib);
                        sib = rightOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(rightOf(sib), BLACK);
                    rotateLeft(parentOf(x));
                    x = root;
                }
            }
            else
            {
                Entry<K, V> sib = leftOf(parentOf(x));

                if (colorOf(sib) == RED)
                {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateRight(parentOf(x));
                    sib = leftOf(parentOf(x));
                }

                if (colorOf(rightOf(sib)) == BLACK && colorOf(leftOf(sib)) == BLACK)
                {
                    setColor(sib, RED);
                    x = parentOf(x);
                }
                else
                {
                    if (colorOf(leftOf(sib)) == BLACK)
                    {
                        setColor(rightOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateLeft(sib);
                        sib = leftOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(leftOf(sib), BLACK);
                    rotateRight(parentOf(x));
                    x = root;
                }
            }
        }

        setColor(x, BLACK);
    }

    void readTreeSet(int size, java.io.ObjectInputStream s, V defaultVal)
        throws java.io.IOException,
            ClassNotFoundException
    {
        buildFromSorted(size, null, s, defaultVal);
    }

    void addAllForTreeSet(SortedSet<Map.Entry<K, V>> set, V defaultVal)
    {
        try
        {
            buildFromSorted(set.size(), set.iterator(), null, defaultVal);
        }
        catch (java.io.IOException cannotHappen)
        {
        }
        catch (ClassNotFoundException cannotHappen)
        {
        }
    }

    private void buildFromSorted(int size, Iterator it, java.io.ObjectInputStream str, V defaultVal)
        throws java.io.IOException,
            ClassNotFoundException
    {
        this.size = size;
        root = buildFromSorted(0, 0, size - 1, computeRedLevel(size), it, str, defaultVal);
    }

    private final Entry<K, V> buildFromSorted(int level, int lo, int hi, int redLevel, Iterator it,
            java.io.ObjectInputStream str, V defaultVal)
        throws java.io.IOException,
            ClassNotFoundException
    {

        if (hi < lo)
            return null;

        int mid = (lo + hi) / 2;

        Entry<K, V> left = null;
        if (lo < mid)
            left = buildFromSorted(level + 1, lo, mid - 1, redLevel, it, str, defaultVal);

        K key;
        V value;
        if (it != null)
        {
            if (defaultVal == null)
            {
                Map.Entry<K, V> entry = (Map.Entry<K, V>)it.next();
                key = entry.getKey();
                value = entry.getValue();
            }
            else
            {
                key = (K)it.next();
                value = defaultVal;
            }
        }
        else
        {
            key = (K)str.readObject();
            value = (defaultVal != null ? defaultVal : (V)str.readObject());
        }

        Entry<K, V> middle = new Entry<K, V>(key, value, null);

        if (level == redLevel)
            middle.color = RED;

        if (left != null)
        {
            middle.left = left;
            left.parent = middle;
        }

        if (mid < hi)
        {
            Entry<K, V> right =
                    buildFromSorted(level + 1, mid + 1, hi, redLevel, it, str, defaultVal);
            middle.right = right;
            right.parent = middle;
        }

        return middle;
    }

    private static int computeRedLevel(int sz)
    {
        int level = 0;
        for (int m = sz - 1; m >= 0; m = m / 2 - 1)
            level++;
        return level;
    }

    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException
    {
        s.defaultWriteObject();

        s.writeInt(size);

        for (Iterator<Map.Entry<K, V>> i = entrySet().iterator(); i.hasNext();)
        {
            Map.Entry<K, V> e = i.next();
            s.writeObject(e.getKey());
            s.writeObject(e.getValue());
        }
    }

    private void readObject(final java.io.ObjectInputStream s)
        throws java.io.IOException,
            ClassNotFoundException
    {
        s.defaultReadObject();

        // Read in size
        int size = s.readInt();

        buildFromSorted(size, null, s, null);
    }
}
