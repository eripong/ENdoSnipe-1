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

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class HashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, java.io.Serializable
{
    /**  */
    private static final long serialVersionUID = 7481282919838399045L;

    private transient HashMap<E, Object> map;

    private static final Object PRESENT = new Object();

    public HashSet()
    {
        map = new HashMap<E, Object>();
    }

    public HashSet(Collection<? extends E> c)
    {
        map = new HashMap<E, Object>(Math.max((int)(c.size() / .75f) + 1, 16));
        addAll(c);
    }

    public HashSet(int initialCapacity, float loadFactor)
    {
        map = new HashMap<E, Object>(initialCapacity, loadFactor);
    }

    public HashSet(int initialCapacity)
    {
        map = new HashMap<E, Object>(initialCapacity);
    }

    HashSet(int initialCapacity, float loadFactor, boolean dummy)
    {
        map = new LinkedHashMap<E, Object>(initialCapacity, loadFactor);
    }

    public Iterator<E> iterator()
    {
        return map.keySet().iterator();
    }

    public int size()
    {
        return map.size();
    }

    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public boolean contains(Object o)
    {
        return map.containsKey(o);
    }

    public boolean add(E o)
    {
        return map.put(o, PRESENT) == null;
    }

    public boolean remove(Object o)
    {
        return map.remove(o) == PRESENT;
    }

    public void clear()
    {
        map.clear();
    }

    public Object clone()
    {
        try
        {
            HashSet<E> newSet = (HashSet<E>)super.clone();
            newSet.map = (HashMap<E, Object>)map.clone();
            return newSet;
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError();
        }
    }

    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException
    {
        s.defaultWriteObject();

        s.writeInt(map.capacity());
        s.writeFloat(map.loadFactor());

        s.writeInt(map.size());

        for (Iterator i = map.keySet().iterator(); i.hasNext();)
            s.writeObject(i.next());
    }

    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException,
            ClassNotFoundException
    {
        s.defaultReadObject();

        int capacity = s.readInt();
        float loadFactor = s.readFloat();
        map =
                (((HashSet)this) instanceof LinkedHashSet ? new LinkedHashMap<E, Object>(capacity,
                                                                                         loadFactor)
                        : new HashMap<E, Object>(capacity, loadFactor));

        int size = s.readInt();

        for (int i = 0; i < size; i++)
        {
            E e = (E)s.readObject();
            map.put(e, PRESENT);
        }
    }
}
