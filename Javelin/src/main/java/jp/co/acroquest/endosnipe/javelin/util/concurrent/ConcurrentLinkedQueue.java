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

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import jp.co.acroquest.endosnipe.javelin.util.ArrayList;

public class ConcurrentLinkedQueue<E> extends AbstractQueue<E> implements Queue<E>,
        java.io.Serializable
{

    /**  */
    private static final long serialVersionUID = -3429890063062468928L;

    private static class Node<E>
    {
        private volatile E item;

        private volatile Node<E> next;

        private static final AtomicReferenceFieldUpdater<Node, Node> nextUpdater =
                AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "next");

        private static final AtomicReferenceFieldUpdater<Node, Object> itemUpdater =
                AtomicReferenceFieldUpdater.newUpdater(Node.class, Object.class, "item");

        Node(E x)
        {
            item = x;
        }

        Node(E x, Node<E> n)
        {
            item = x;
            next = n;
        }

        E getItem()
        {
            return item;
        }

        boolean casItem(E cmp, E val)
        {
            return itemUpdater.compareAndSet(this, cmp, val);
        }

        void setItem(E val)
        {
            itemUpdater.set(this, val);
        }

        Node<E> getNext()
        {
            return next;
        }

        boolean casNext(Node<E> cmp, Node<E> val)
        {
            return nextUpdater.compareAndSet(this, cmp, val);
        }

        void setNext(Node<E> val)
        {
            nextUpdater.set(this, val);
        }

    }

    private static final AtomicReferenceFieldUpdater<ConcurrentLinkedQueue, Node> tailUpdater =
            AtomicReferenceFieldUpdater.newUpdater(ConcurrentLinkedQueue.class, Node.class, "tail");

    private static final AtomicReferenceFieldUpdater<ConcurrentLinkedQueue, Node> headUpdater =
            AtomicReferenceFieldUpdater.newUpdater(ConcurrentLinkedQueue.class, Node.class, "head");

    private boolean casTail(Node<E> cmp, Node<E> val)
    {
        return tailUpdater.compareAndSet(this, cmp, val);
    }

    private boolean casHead(Node<E> cmp, Node<E> val)
    {
        return headUpdater.compareAndSet(this, cmp, val);
    }

    private transient volatile Node<E> head = new Node<E>(null, null);

    private transient volatile Node<E> tail = head;

    /**
     * Creates a <tt>ConcurrentLinkedQueue</tt> that is initially empty.
     */
    public ConcurrentLinkedQueue()
    {
    }

    /**
     * Creates a <tt>ConcurrentLinkedQueue</tt> 
     * initially containing the elements of the given collection,
     * added in traversal order of the collection's iterator.
     * @param c the collection of elements to initially contain
     * @throws NullPointerException if <tt>c</tt> or any element within it
     * is <tt>null</tt>
     */
    public ConcurrentLinkedQueue(Collection<? extends E> c)
    {
        for (Iterator<? extends E> it = c.iterator(); it.hasNext();)
            add(it.next());
    }

    /**
     * Adds the specified element to the tail of this queue.
     * @param o the element to add.
     * @return <tt>true</tt> (as per the general contract of
     * <tt>Collection.add</tt>).
     *
     * @throws NullPointerException if the specified element is <tt>null</tt>
     */
    public boolean add(E o)
    {
        return offer(o);
    }

    /**
     * Inserts the specified element to the tail of this queue.
     *
     * @param o the element to add.
     * @return <tt>true</tt> (as per the general contract of
     * <tt>Queue.offer</tt>).
     * @throws NullPointerException if the specified element is <tt>null</tt>
     */
    public boolean offer(E o)
    {
        if (o == null)
            throw new NullPointerException();
        Node<E> n = new Node<E>(o, null);
        for (;;)
        {
            Node<E> t = tail;
            Node<E> s = t.getNext();
            if (t == tail)
            {
                if (s == null)
                {
                    if (t.casNext(s, n))
                    {
                        casTail(t, n);
                        return true;
                    }
                }
                else
                {
                    casTail(t, s);
                }
            }
        }
    }

    public E poll()
    {
        for (;;)
        {
            Node<E> h = head;
            Node<E> t = tail;
            Node<E> first = h.getNext();
            if (h == head)
            {
                if (h == t)
                {
                    if (first == null)
                        return null;
                    else
                        casTail(t, first);
                }
                else if (casHead(h, first))
                {
                    E item = first.getItem();
                    if (item != null)
                    {
                        first.setItem(null);
                        return item;
                    }

                }
            }
        }
    }

    public E peek()
    {
        for (;;)
        {
            Node<E> h = head;
            Node<E> t = tail;
            Node<E> first = h.getNext();
            if (h == head)
            {
                if (h == t)
                {
                    if (first == null)
                        return null;
                    else
                        casTail(t, first);
                }
                else
                {
                    E item = first.getItem();
                    if (item != null)
                        return item;
                    else

                        casHead(h, first);
                }
            }
        }
    }

    /**
     * Returns the first actual (non-header) node on list.  This is yet
     * another variant of poll/peek; here returning out the first
     * node, not element (so we cannot collapse with peek() without
     * introducing race.)
     */
    Node<E> first()
    {
        for (;;)
        {
            Node<E> h = head;
            Node<E> t = tail;
            Node<E> first = h.getNext();
            if (h == head)
            {
                if (h == t)
                {
                    if (first == null)
                        return null;
                    else
                        casTail(t, first);
                }
                else
                {
                    if (first.getItem() != null)
                        return first;
                    else

                        casHead(h, first);
                }
            }
        }
    }

    public boolean isEmpty()
    {
        return first() == null;
    }

    /**
     * Returns the number of elements in this queue.  If this queue
     * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * <p>Beware that, unlike in most collections, this method is
     * <em>NOT</em> a constant-time operation. Because of the
     * asynchronous nature of these queues, determining the current
     * number of elements requires an O(n) traversal.
     *
     * @return  the number of elements in this queue.
     */
    public int size()
    {
        int count = 0;
        for (Node<E> p = first(); p != null; p = p.getNext())
        {
            if (p.getItem() != null)
            {

                if (++count == Integer.MAX_VALUE)
                    break;
            }
        }
        return count;
    }

    public boolean contains(Object o)
    {
        if (o == null)
            return false;
        for (Node<E> p = first(); p != null; p = p.getNext())
        {
            E item = p.getItem();
            if (item != null && o.equals(item))
                return true;
        }
        return false;
    }

    public boolean remove(Object o)
    {
        if (o == null)
            return false;
        for (Node<E> p = first(); p != null; p = p.getNext())
        {
            E item = p.getItem();
            if (item != null && o.equals(item) && p.casItem(item, null))
                return true;
        }
        return false;
    }

    public Object[] toArray()
    {

        ArrayList<E> al = new ArrayList<E>();
        for (Node<E> p = first(); p != null; p = p.getNext())
        {
            E item = p.getItem();
            if (item != null)
                al.add(item);
        }
        return al.toArray();
    }

    public <T> T[] toArray(T[] a)
    {

        int k = 0;
        Node<E> p;
        for (p = first(); p != null && k < a.length; p = p.getNext())
        {
            E item = p.getItem();
            if (item != null)
                a[k++] = (T)item;
        }
        if (p == null)
        {
            if (k < a.length)
                a[k] = null;
            return a;
        }

        ArrayList<E> al = new ArrayList<E>();
        for (Node<E> q = first(); q != null; q = q.getNext())
        {
            E item = q.getItem();
            if (item != null)
                al.add(item);
        }
        return (T[])al.toArray(a);
    }

    public Iterator<E> iterator()
    {
        return new Itr();
    }

    private class Itr implements Iterator<E>
    {

        private Node<E> nextNode;

        private E nextItem;

        private Node<E> lastRet;

        Itr()
        {
            advance();
        }

        private E advance()
        {
            lastRet = nextNode;
            E x = nextItem;

            Node<E> p = (nextNode == null) ? first() : nextNode.getNext();
            for (;;)
            {
                if (p == null)
                {
                    nextNode = null;
                    nextItem = null;
                    return x;
                }
                E item = p.getItem();
                if (item != null)
                {
                    nextNode = p;
                    nextItem = item;
                    return x;
                }
                else

                    p = p.getNext();
            }
        }

        public boolean hasNext()
        {
            return nextNode != null;
        }

        public E next()
        {
            if (nextNode == null)
                throw new NoSuchElementException();
            return advance();
        }

        public void remove()
        {
            Node<E> l = lastRet;
            if (l == null)
                throw new IllegalStateException();

            l.setItem(null);
            lastRet = null;
        }
    }

    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException
    {

        s.defaultWriteObject();

        for (Node<E> p = first(); p != null; p = p.getNext())
        {
            Object item = p.getItem();
            if (item != null)
                s.writeObject(item);
        }

        s.writeObject(null);
    }

    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException,
            ClassNotFoundException
    {
        s.defaultReadObject();
        head = new Node<E>(null, null);
        tail = head;
        for (;;)
        {
            E item = (E)s.readObject();
            if (item == null)
                break;
            else
                offer(item);
        }
    }

}
