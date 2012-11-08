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

import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class CopyOnWriteArrayList<E> implements List<E>, RandomAccess, Cloneable,
        java.io.Serializable
{
    /**  */
    private static final long serialVersionUID = -7056280058773009144L;

    private volatile transient E[] array;

    private E[] array()
    {
        return array;
    }

    public CopyOnWriteArrayList()
    {
        array = (E[])new Object[0];
    }

    public CopyOnWriteArrayList(Collection<? extends E> c)
    {
        array = (E[])new Object[c.size()];
        Iterator<? extends E> i = c.iterator();
        int size = 0;
        while (i.hasNext())
            array[size++] = i.next();
    }

    public CopyOnWriteArrayList(E[] toCopyIn)
    {
        copyIn(toCopyIn, 0, toCopyIn.length);
    }

    private synchronized void copyIn(E[] toCopyIn, int first, int n)
    {
        array = (E[])new Object[n];
        System.arraycopy(toCopyIn, first, array, 0, n);
    }

    public int size()
    {
        return array().length;
    }

    public boolean isEmpty()
    {
        return size() == 0;
    }

    public boolean contains(Object elem)
    {
        E[] elementData = array();
        int len = elementData.length;
        return indexOf(elem, elementData, len) >= 0;
    }

    public int indexOf(Object elem)
    {
        E[] elementData = array();
        int len = elementData.length;
        return indexOf(elem, elementData, len);
    }

    private static int indexOf(Object elem, Object[] elementData, int len)
    {
        if (elem == null)
        {
            for (int i = 0; i < len; i++)
                if (elementData[i] == null)
                    return i;
        }
        else
        {
            for (int i = 0; i < len; i++)
                if (elem.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    public int indexOf(E elem, int index)
    {
        E[] elementData = array();
        int elementCount = elementData.length;

        if (elem == null)
        {
            for (int i = index; i < elementCount; i++)
                if (elementData[i] == null)
                    return i;
        }
        else
        {
            for (int i = index; i < elementCount; i++)
                if (elem.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    public int lastIndexOf(Object elem)
    {
        E[] elementData = array();
        int len = elementData.length;
        return lastIndexOf(elem, elementData, len);
    }

    private static int lastIndexOf(Object elem, Object[] elementData, int len)
    {
        if (elem == null)
        {
            for (int i = len - 1; i >= 0; i--)
                if (elementData[i] == null)
                    return i;
        }
        else
        {
            for (int i = len - 1; i >= 0; i--)
                if (elem.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    public int lastIndexOf(E elem, int index)
    {

        E[] elementData = array();
        if (elem == null)
        {
            for (int i = index; i >= 0; i--)
                if (elementData[i] == null)
                    return i;
        }
        else
        {
            for (int i = index; i >= 0; i--)
                if (elem.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    public Object clone()
    {
        try
        {
            E[] elementData = array();
            CopyOnWriteArrayList<E> v = (CopyOnWriteArrayList<E>)super.clone();
            v.array = (E[])new Object[elementData.length];
            System.arraycopy(elementData, 0, v.array, 0, elementData.length);
            return v;
        }
        catch (CloneNotSupportedException e)
        {

            throw new InternalError();
        }
    }

    public Object[] toArray()
    {
        Object[] elementData = array();
        Object[] result = new Object[elementData.length];
        System.arraycopy(elementData, 0, result, 0, elementData.length);
        return result;
    }

    public <T> T[] toArray(T a[])
    {
        E[] elementData = array();

        if (a.length < elementData.length)
            a =
                    (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(),
                                                             elementData.length);

        System.arraycopy(elementData, 0, a, 0, elementData.length);

        if (a.length > elementData.length)
            a[elementData.length] = null;

        return a;
    }

    public E get(int index)
    {
        E[] elementData = array();
        rangeCheck(index, elementData.length);
        return elementData[index];
    }

    public synchronized E set(int index, E element)
    {
        int len = array.length;
        rangeCheck(index, len);
        E oldValue = array[index];

        boolean same = (oldValue == element || (element != null && element.equals(oldValue)));
        if (!same)
        {
            E[] newArray = (E[])new Object[len];
            System.arraycopy(array, 0, newArray, 0, len);
            newArray[index] = element;
            array = newArray;
        }
        return oldValue;
    }

    public synchronized boolean add(E element)
    {
        int len = array.length;
        E[] newArray = (E[])new Object[len + 1];
        System.arraycopy(array, 0, newArray, 0, len);
        newArray[len] = element;
        array = newArray;
        return true;
    }

    public synchronized void add(int index, E element)
    {
        int len = array.length;
        if (index > len || index < 0)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + len);

        E[] newArray = (E[])new Object[len + 1];
        System.arraycopy(array, 0, newArray, 0, index);
        newArray[index] = element;
        System.arraycopy(array, index, newArray, index + 1, len - index);
        array = newArray;
    }

    public synchronized E remove(int index)
    {
        int len = array.length;
        rangeCheck(index, len);
        E oldValue = array[index];
        E[] newArray = (E[])new Object[len - 1];
        System.arraycopy(array, 0, newArray, 0, index);
        int numMoved = len - index - 1;
        if (numMoved > 0)
            System.arraycopy(array, index + 1, newArray, index, numMoved);
        array = newArray;
        return oldValue;
    }

    public synchronized boolean remove(Object o)
    {
        int len = array.length;
        if (len == 0)
            return false;

        int newlen = len - 1;
        E[] newArray = (E[])new Object[newlen];

        for (int i = 0; i < newlen; ++i)
        {
            if (o == array[i] || (o != null && o.equals(array[i])))
            {

                for (int k = i + 1; k < len; ++k)
                    newArray[k - 1] = array[k];
                array = newArray;
                return true;
            }
            else
                newArray[i] = array[i];
        }

        if (o == array[newlen] || (o != null && o.equals(array[newlen])))
        {
            array = newArray;
            return true;
        }
        else
            return false;
    }

    private synchronized void removeRange(int fromIndex, int toIndex)
    {
        int len = array.length;

        if (fromIndex < 0 || fromIndex >= len || toIndex > len || toIndex < fromIndex)
            throw new IndexOutOfBoundsException();

        int numMoved = len - toIndex;
        int newlen = len - (toIndex - fromIndex);
        E[] newArray = (E[])new Object[newlen];
        System.arraycopy(array, 0, newArray, 0, fromIndex);
        System.arraycopy(array, toIndex, newArray, fromIndex, numMoved);
        array = newArray;
    }

    public synchronized boolean addIfAbsent(E element)
    {

        int len = array.length;
        E[] newArray = (E[])new Object[len + 1];
        for (int i = 0; i < len; ++i)
        {
            if (element == array[i] || (element != null && element.equals(array[i])))
                return false;
            else
                newArray[i] = array[i];
        }
        newArray[len] = element;
        array = newArray;
        return true;
    }

    public boolean containsAll(Collection<?> c)
    {
        E[] elementData = array();
        int len = elementData.length;
        Iterator e = c.iterator();
        while (e.hasNext())
            if (indexOf(e.next(), elementData, len) < 0)
                return false;

        return true;
    }

    public synchronized boolean removeAll(Collection<?> c)
    {
        E[] elementData = array;
        int len = elementData.length;
        if (len == 0)
            return false;

        E[] temp = (E[])new Object[len];
        int newlen = 0;
        for (int i = 0; i < len; ++i)
        {
            E element = elementData[i];
            if (!c.contains(element))
            {
                temp[newlen++] = element;
            }
        }

        if (newlen == len)
            return false;

        E[] newArray = (E[])new Object[newlen];
        System.arraycopy(temp, 0, newArray, 0, newlen);
        array = newArray;
        return true;
    }

    public synchronized boolean retainAll(Collection<?> c)
    {
        E[] elementData = array;
        int len = elementData.length;
        if (len == 0)
            return false;

        E[] temp = (E[])new Object[len];
        int newlen = 0;
        for (int i = 0; i < len; ++i)
        {
            E element = elementData[i];
            if (c.contains(element))
            {
                temp[newlen++] = element;
            }
        }

        if (newlen == len)
            return false;

        E[] newArray = (E[])new Object[newlen];
        System.arraycopy(temp, 0, newArray, 0, newlen);
        array = newArray;
        return true;
    }

    public synchronized int addAllAbsent(Collection<? extends E> c)
    {
        int numNew = c.size();
        if (numNew == 0)
            return 0;

        E[] elementData = array;
        int len = elementData.length;

        E[] temp = (E[])new Object[numNew];
        int added = 0;
        Iterator<? extends E> e = c.iterator();
        while (e.hasNext())
        {
            E element = e.next();
            if (indexOf(element, elementData, len) < 0)
            {
                if (indexOf(element, temp, added) < 0)
                {
                    temp[added++] = element;
                }
            }
        }

        if (added == 0)
            return 0;

        E[] newArray = (E[])new Object[len + added];
        System.arraycopy(elementData, 0, newArray, 0, len);
        System.arraycopy(temp, 0, newArray, len, added);
        array = newArray;
        return added;
    }

    public synchronized void clear()
    {
        array = (E[])new Object[0];
    }

    public synchronized boolean addAll(Collection<? extends E> c)
    {
        int numNew = c.size();
        if (numNew == 0)
            return false;

        int len = array.length;
        E[] newArray = (E[])new Object[len + numNew];
        System.arraycopy(array, 0, newArray, 0, len);
        Iterator<? extends E> e = c.iterator();
        for (int i = 0; i < numNew; i++)
            newArray[len++] = e.next();
        array = newArray;

        return true;
    }

    public synchronized boolean addAll(int index, Collection<? extends E> c)
    {
        int len = array.length;
        if (index > len || index < 0)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + len);

        int numNew = c.size();
        if (numNew == 0)
            return false;

        E[] newArray = (E[])new Object[len + numNew];
        System.arraycopy(array, 0, newArray, 0, len);
        int numMoved = len - index;
        if (numMoved > 0)
            System.arraycopy(array, index, newArray, index + numNew, numMoved);
        Iterator<? extends E> e = c.iterator();
        for (int i = 0; i < numNew; i++)
            newArray[index++] = e.next();
        array = newArray;

        return true;
    }

    private void rangeCheck(int index, int length)
    {
        if (index >= length || index < 0)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + length);
    }

    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException
    {

        s.defaultWriteObject();

        E[] elementData = array();

        s.writeInt(elementData.length);

        for (int i = 0; i < elementData.length; i++)
            s.writeObject(elementData[i]);
    }

    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException,
            ClassNotFoundException
    {

        s.defaultReadObject();

        int arrayLength = s.readInt();
        E[] elementData = (E[])new Object[arrayLength];

        for (int i = 0; i < elementData.length; i++)
            elementData[i] = (E)s.readObject();
        array = elementData;
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        Iterator e = iterator();
        buf.append("[");
        int maxIndex = size() - 1;
        for (int i = 0; i <= maxIndex; i++)
        {
            buf.append(String.valueOf(e.next()));
            if (i < maxIndex)
                buf.append(", ");
        }
        buf.append("]");
        return buf.toString();
    }

    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof List))
            return false;

        List<E> l2 = (List<E>)(o);
        if (size() != l2.size())
            return false;

        ListIterator<E> e1 = listIterator();
        ListIterator<E> e2 = l2.listIterator();
        while (e1.hasNext())
        {
            E o1 = e1.next();
            E o2 = e2.next();
            if (!(o1 == null ? o2 == null : o1.equals(o2)))
                return false;
        }
        return true;
    }

    public int hashCode()
    {
        int hashCode = 1;
        Iterator<E> i = iterator();
        while (i.hasNext())
        {
            E obj = i.next();
            hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
        }
        return hashCode;
    }

    public Iterator<E> iterator()
    {
        return new COWIterator<E>(array(), 0);
    }

    public ListIterator<E> listIterator()
    {
        return new COWIterator<E>(array(), 0);
    }

    public ListIterator<E> listIterator(final int index)
    {
        E[] elementData = array();
        int len = elementData.length;
        if (index < 0 || index > len)
            throw new IndexOutOfBoundsException("Index: " + index);

        return new COWIterator<E>(array(), index);
    }

    private static class COWIterator<E> implements ListIterator<E>
    {

        private final E[] array;

        private int cursor;

        private COWIterator(E[] elementArray, int initialCursor)
        {
            array = elementArray;
            cursor = initialCursor;
        }

        public boolean hasNext()
        {
            return cursor < array.length;
        }

        public boolean hasPrevious()
        {
            return cursor > 0;
        }

        public E next()
        {
            try
            {
                return array[cursor++];
            }
            catch (IndexOutOfBoundsException ex)
            {
                throw new NoSuchElementException();
            }
        }

        public E previous()
        {
            try
            {
                return array[--cursor];
            }
            catch (IndexOutOfBoundsException e)
            {
                throw new NoSuchElementException();
            }
        }

        public int nextIndex()
        {
            return cursor;
        }

        public int previousIndex()
        {
            return cursor - 1;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        public void set(E o)
        {
            throw new UnsupportedOperationException();
        }

        public void add(E o)
        {
            throw new UnsupportedOperationException();
        }
    }

    public synchronized List<E> subList(int fromIndex, int toIndex)
    {

        int len = array.length;
        if (fromIndex < 0 || toIndex > len || fromIndex > toIndex)
            throw new IndexOutOfBoundsException();
        return new COWSubList<E>(this, fromIndex, toIndex);
    }

    private static class COWSubList<E> extends AbstractList<E>
    {

        private final CopyOnWriteArrayList<E> l;

        private final int offset;

        private int size;

        private E[] expectedArray;

        private COWSubList(CopyOnWriteArrayList<E> list, int fromIndex, int toIndex)
        {
            l = list;
            expectedArray = l.array();
            offset = fromIndex;
            size = toIndex - fromIndex;
        }

        private void checkForComodification()
        {
            if (l.array != expectedArray)
                throw new ConcurrentModificationException();
        }

        private void rangeCheck(int index)
        {
            if (index < 0 || index >= size)
                throw new IndexOutOfBoundsException("Index: " + index + ",Size: " + size);
        }

        public E set(int index, E element)
        {
            synchronized (l)
            {
                rangeCheck(index);
                checkForComodification();
                E x = l.set(index + offset, element);
                expectedArray = l.array;
                return x;
            }
        }

        public E get(int index)
        {
            synchronized (l)
            {
                rangeCheck(index);
                checkForComodification();
                return l.get(index + offset);
            }
        }

        public int size()
        {
            synchronized (l)
            {
                checkForComodification();
                return size;
            }
        }

        public void add(int index, E element)
        {
            synchronized (l)
            {
                checkForComodification();
                if (index < 0 || index > size)
                    throw new IndexOutOfBoundsException();
                l.add(index + offset, element);
                expectedArray = l.array;
                size++;
            }
        }

        public void clear()
        {
            synchronized (l)
            {
                checkForComodification();
                l.removeRange(offset, offset + size);
                expectedArray = l.array;
                size = 0;
            }
        }

        public E remove(int index)
        {
            synchronized (l)
            {
                rangeCheck(index);
                checkForComodification();
                E result = l.remove(index + offset);
                expectedArray = l.array;
                size--;
                return result;
            }
        }

        public Iterator<E> iterator()
        {
            synchronized (l)
            {
                checkForComodification();
                return new COWSubListIterator<E>(l, 0, offset, size);
            }
        }

        public ListIterator<E> listIterator(final int index)
        {
            synchronized (l)
            {
                checkForComodification();
                if (index < 0 || index > size)
                    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
                return new COWSubListIterator<E>(l, index, offset, size);
            }
        }

        public List<E> subList(int fromIndex, int toIndex)
        {
            synchronized (l)
            {
                checkForComodification();
                if (fromIndex < 0 || toIndex > size)
                    throw new IndexOutOfBoundsException();
                return new COWSubList<E>(l, fromIndex + offset, toIndex + offset);
            }
        }

    }

    private static class COWSubListIterator<E> implements ListIterator<E>
    {
        private final ListIterator<E> i;

        private final int index;

        private final int offset;

        private final int size;

        private COWSubListIterator(List<E> l, int index, int offset, int size)
        {
            this.index = index;
            this.offset = offset;
            this.size = size;
            i = l.listIterator(index + offset);
        }

        public boolean hasNext()
        {
            return nextIndex() < size;
        }

        public E next()
        {
            if (hasNext())
                return i.next();
            else
                throw new NoSuchElementException();
        }

        public boolean hasPrevious()
        {
            return previousIndex() >= 0;
        }

        public E previous()
        {
            if (hasPrevious())
                return i.previous();
            else
                throw new NoSuchElementException();
        }

        public int nextIndex()
        {
            return i.nextIndex() - offset;
        }

        public int previousIndex()
        {
            return i.previousIndex() - offset;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        public void set(E o)
        {
            throw new UnsupportedOperationException();
        }

        public void add(E o)
        {
            throw new UnsupportedOperationException();
        }
    }
}
