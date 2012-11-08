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

import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Queue<E>,
        Cloneable, java.io.Serializable
{
    /**  */
    private static final long serialVersionUID = 6287226297445011619L;

    private transient Entry<E> header = new Entry<E>(null, null, null);

    private transient int size = 0;

    public LinkedList()
    {
        header.next = header.previous = header;
    }

    public LinkedList(Collection<? extends E> c)
    {
        this();
        addAll(c);
    }

    public E getFirst()
    {
        if (size == 0)
            throw new NoSuchElementException();

        return header.next.element;
    }

    public E getLast()
    {
        if (size == 0)
            throw new NoSuchElementException();

        return header.previous.element;
    }

    public E removeFirst()
    {
        return remove(header.next);
    }

    public E removeLast()
    {
        return remove(header.previous);
    }

    public void addFirst(E o)
    {
        addBefore(o, header.next);
    }

    public void addLast(E o)
    {
        addBefore(o, header);
    }

    public boolean contains(Object o)
    {
        return indexOf(o) != -1;
    }

    public int size()
    {
        return size;
    }

    public boolean add(E o)
    {
        addBefore(o, header);
        return true;
    }

    public boolean remove(Object o)
    {
        if (o == null)
        {
            for (Entry<E> e = header.next; e != header; e = e.next)
            {
                if (e.element == null)
                {
                    remove(e);
                    return true;
                }
            }
        }
        else
        {
            for (Entry<E> e = header.next; e != header; e = e.next)
            {
                if (o.equals(e.element))
                {
                    remove(e);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addAll(Collection<? extends E> c)
    {
        return addAll(size, c);
    }

    public boolean addAll(int index, Collection<? extends E> c)
    {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        Object[] a = c.toArray();
        int numNew = a.length;
        if (numNew == 0)
            return false;
        modCount++;

        Entry<E> successor = (index == size ? header : entry(index));
        Entry<E> predecessor = successor.previous;
        for (int i = 0; i < numNew; i++)
        {
            Entry<E> e = new Entry<E>((E)a[i], successor, predecessor);
            predecessor.next = e;
            predecessor = e;
        }
        successor.previous = predecessor;

        size += numNew;
        return true;
    }

    public void clear()
    {
        Entry<E> e = header.next;
        while (e != header)
        {
            Entry<E> next = e.next;
            e.next = e.previous = null;
            e.element = null;
            e = next;
        }
        header.next = header.previous = header;
        size = 0;
        modCount++;
    }

    public E get(int index)
    {
        return entry(index).element;
    }

    public E set(int index, E element)
    {
        Entry<E> e = entry(index);
        E oldVal = e.element;
        e.element = element;
        return oldVal;
    }

    public void add(int index, E element)
    {
        addBefore(element, (index == size ? header : entry(index)));
    }

    public E remove(int index)
    {
        return remove(entry(index));
    }

    private Entry<E> entry(int index)
    {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        Entry<E> e = header;
        if (index < (size >> 1))
        {
            for (int i = 0; i <= index; i++)
                e = e.next;
        }
        else
        {
            for (int i = size; i > index; i--)
                e = e.previous;
        }
        return e;
    }

    public int indexOf(Object o)
    {
        int index = 0;
        if (o == null)
        {
            for (Entry e = header.next; e != header; e = e.next)
            {
                if (e.element == null)
                    return index;
                index++;
            }
        }
        else
        {
            for (Entry e = header.next; e != header; e = e.next)
            {
                if (o.equals(e.element))
                    return index;
                index++;
            }
        }
        return -1;
    }

    public int lastIndexOf(Object o)
    {
        int index = size;
        if (o == null)
        {
            for (Entry e = header.previous; e != header; e = e.previous)
            {
                index--;
                if (e.element == null)
                    return index;
            }
        }
        else
        {
            for (Entry e = header.previous; e != header; e = e.previous)
            {
                index--;
                if (o.equals(e.element))
                    return index;
            }
        }
        return -1;
    }

    public E peek()
    {
        if (size == 0)
            return null;
        return getFirst();
    }

    public E element()
    {
        return getFirst();
    }

    public E poll()
    {
        if (size == 0)
            return null;
        return removeFirst();
    }

    public E remove()
    {
        return removeFirst();
    }

    public boolean offer(E o)
    {
        return add(o);
    }

    public ListIterator<E> listIterator(int index)
    {
        return new ListItr(index);
    }

    private class ListItr implements ListIterator<E>
    {
        private Entry<E> lastReturned = header;

        private Entry<E> next;

        private int nextIndex;

        private int expectedModCount = modCount;

        ListItr(int index)
        {
            if (index < 0 || index > size)
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            if (index < (size >> 1))
            {
                next = header.next;
                for (nextIndex = 0; nextIndex < index; nextIndex++)
                    next = next.next;
            }
            else
            {
                next = header;
                for (nextIndex = size; nextIndex > index; nextIndex--)
                    next = next.previous;
            }
        }

        public boolean hasNext()
        {
            return nextIndex != size;
        }

        public E next()
        {
            checkForComodification();
            if (nextIndex == size)
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.element;
        }

        public boolean hasPrevious()
        {
            return nextIndex != 0;
        }

        public E previous()
        {
            if (nextIndex == 0)
                throw new NoSuchElementException();

            lastReturned = next = next.previous;
            nextIndex--;
            checkForComodification();
            return lastReturned.element;
        }

        public int nextIndex()
        {
            return nextIndex;
        }

        public int previousIndex()
        {
            return nextIndex - 1;
        }

        public void remove()
        {
            checkForComodification();
            Entry<E> lastNext = lastReturned.next;
            try
            {
                LinkedList.this.remove(lastReturned);
            }
            catch (NoSuchElementException e)
            {
                throw new IllegalStateException();
            }
            if (next == lastReturned)
                next = lastNext;
            else
                nextIndex--;
            lastReturned = header;
            expectedModCount++;
        }

        public void set(E o)
        {
            if (lastReturned == header)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.element = o;
        }

        public void add(E o)
        {
            checkForComodification();
            lastReturned = header;
            addBefore(o, next);
            nextIndex++;
            expectedModCount++;
        }

        final void checkForComodification()
        {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private static class Entry<E>
    {
        E element;

        Entry<E> next;

        Entry<E> previous;

        Entry(E element, Entry<E> next, Entry<E> previous)
        {
            this.element = element;
            this.next = next;
            this.previous = previous;
        }
    }

    private Entry<E> addBefore(E o, Entry<E> e)
    {
        Entry<E> newEntry = new Entry<E>(o, e, e.previous);
        newEntry.previous.next = newEntry;
        newEntry.next.previous = newEntry;
        size++;
        modCount++;
        return newEntry;
    }

    private E remove(Entry<E> e)
    {
        if (e == header)
            throw new NoSuchElementException();

        E result = e.element;
        e.previous.next = e.next;
        e.next.previous = e.previous;
        e.next = e.previous = null;
        e.element = null;
        size--;
        modCount++;
        return result;
    }

    public Object clone()
    {
        LinkedList<E> clone = null;
        try
        {
            clone = (LinkedList<E>)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError();
        }

        clone.header = new Entry<E>(null, null, null);
        clone.header.next = clone.header.previous = clone.header;
        clone.size = 0;
        clone.modCount = 0;

        for (Entry<E> e = header.next; e != header; e = e.next)
            clone.add(e.element);

        return clone;
    }

    public Object[] toArray()
    {
        Object[] result = new Object[size];
        int i = 0;
        for (Entry<E> e = header.next; e != header; e = e.next)
            result[i++] = e.element;
        return result;
    }

    public <T> T[] toArray(T[] a)
    {
        if (a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        int i = 0;
        Object[] result = a;
        for (Entry<E> e = header.next; e != header; e = e.next)
            result[i++] = e.element;

        if (a.length > size)
            a[size] = null;

        return a;
    }

    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException
    {
        s.defaultWriteObject();

        s.writeInt(size);

        for (Entry e = header.next; e != header; e = e.next)
            s.writeObject(e.element);
    }

    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException,
            ClassNotFoundException
    {
        s.defaultReadObject();

        int size = s.readInt();

        header = new Entry<E>(null, null, null);
        header.next = header.previous = header;

        for (int i = 0; i < size; i++)
            addBefore((E)s.readObject(), header);
    }
}
