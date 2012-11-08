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

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

public class CopyOnWriteArraySet<E> extends AbstractSet<E> implements java.io.Serializable
{
    /**  */
    private static final long serialVersionUID = 8560577487834038908L;
    private final CopyOnWriteArrayList<E> al;

    public CopyOnWriteArraySet()
    {
        al = new CopyOnWriteArrayList<E>();
    }

    public CopyOnWriteArraySet(Collection<? extends E> c)
    {
        al = new CopyOnWriteArrayList<E>();
        al.addAllAbsent(c);
    }

    public int size()
    {
        return al.size();
    }

    public boolean isEmpty()
    {
        return al.isEmpty();
    }

    public boolean contains(Object o)
    {
        return al.contains(o);
    }

    public Object[] toArray()
    {
        return al.toArray();
    }

    public <T> T[] toArray(T[] a)
    {
        return al.toArray(a);
    }

    public void clear()
    {
        al.clear();
    }

    public Iterator<E> iterator()
    {
        return al.iterator();
    }

    public boolean remove(Object o)
    {
        return al.remove(o);
    }

    public boolean add(E o)
    {
        return al.addIfAbsent(o);
    }

    public boolean containsAll(Collection<?> c)
    {
        return al.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c)
    {
        return al.addAllAbsent(c) > 0;
    }

    public boolean removeAll(Collection<?> c)
    {
        return al.removeAll(c);
    }

    public boolean retainAll(Collection<?> c)
    {
        return al.retainAll(c);
    }

}
