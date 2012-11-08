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
package jp.co.acroquest.endosnipe.javelin.testutil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PrivateAccessor
{
    private PrivateAccessor()
    {
    }

    /**
     * Returns the value of the field on the specified object.  The name
     * parameter is a <code>String</code> specifying the simple name of the
     * desired field.<p>
     *
     * The object is first searched for any matching field.  If no matching
     * field is found, the superclasses are recursively searched.
     *
     * @exception NoSuchFieldException if a field with the specified name is
     * not found.
     */
    public static Object getField(final Object object, final String name)
        throws NoSuchFieldException
    {
        if (object == null)
        {
            throw new IllegalArgumentException("Invalid null object argument");
        }
        for (Class cls = object.getClass(); cls != null; cls = cls.getSuperclass())
        {
            try
            {
                Field field = cls.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(object);
            }
            catch (Exception ex)
            {
                /* in case of an exception, we will throw a new
                 * NoSuchFieldException object */
                ;
            }
        }
        throw new NoSuchFieldException("Could get value for field " + object.getClass().getName()
                + "." + name);
    }

    /**
     * Returns the value of the field on the specified class.  The name
     * parameter is a <code>String</code> specifying the simple name of the
     * desired field.<p>
     *
     * The class is first searched for any matching field.  If no matching
     * field is found, the superclasses are recursively searched.
     *
     * @exception NoSuchFieldException if a field with the specified name is
     * not found.
     */
    public static Object getField(final Class cls, final String name)
        throws NoSuchFieldException
    {
        if (cls == null)
        {
            throw new IllegalArgumentException("Invalid null cls argument");
        }
        Class base = cls;
        while (base != null)
        {
            try
            {
                Field field = base.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(base);
            }
            catch (Exception ex)
            {
                /* in case of an exception, we will throw a new
                 * NoSuchFieldException object */
                ;
            }
            base = base.getSuperclass();
        }
        throw new NoSuchFieldException("Could not get value for static field " + cls.getName()
                + "." + name);
    }

    /**
     * Sets the field represented by the name value on the specified object
     * argument to the specified new value.  The new value is automatically
     * unwrapped if the underlying field has a primitive type.<p>
     *
     * The object is first searched for any matching field.  If no matching
     * field is found, the superclasses are recursively searched.
     *
     * @exception NoSuchFieldException if a field with the specified name is
     * not found.
     */
    public static void setField(final Object object, final String name, final Object value)
        throws NoSuchFieldException
    {
        if (object == null)
        {
            throw new IllegalArgumentException("Invalid null object argument");
        }
        for (Class cls = object.getClass(); cls != null; cls = cls.getSuperclass())
        {
            try
            {
                Field field = cls.getDeclaredField(name);
                field.setAccessible(true);
                field.set(object, value);
                return;
            }
            catch (Exception ex)
            {
                /* in case of an exception, we will throw a new
                 * NoSuchFieldException object */
                ;
            }
        }
        throw new NoSuchFieldException("Could set value for field " + object.getClass().getName()
                + "." + name);
    }

    /**
     * Sets the field represented by the name value on the specified class
     * argument to the specified new value.  The new value is automatically
     * unwrapped if the underlying field has a primitive type.<p>
     *
     * The class is first searched for any matching field.  If no matching
     * field is found, the superclasses are recursively searched.
     *
     * @exception NoSuchFieldException if a field with the specified name is
     * not found.
     */
    public static void setField(final Class cls, final String name, final Object value)
        throws NoSuchFieldException
    {
        if (cls == null)
        {
            throw new IllegalArgumentException("Invalid null cls argument");
        }
        Class base = cls;
        while (base != null)
        {
            try
            {
                Field field = base.getDeclaredField(name);
                field.setAccessible(true);
                field.set(base, value);
                return;
            }
            catch (Exception ex)
            {
                /* in case of an exception, we will throw a new
                 * NoSuchFieldException object */
                ;
            }
            base = base.getSuperclass();
        }
        throw new NoSuchFieldException("Could set value for static field " + cls.getName() + "."
                + name);
    }

    /**
     * Invokes the method represented by the name value on the specified object
     * with the specified parameters. Individual parameters are automatically
     * unwrapped to match primitive formal parameters, and both primitive and
     * reference parameters are subject to widening conversions as necessary.
     * The value returned by the method is automatically wrapped in an object
     * if it has a primitive type.<p>
     *
     * The object is first searched for any matching method.  If no matching
     * method is found, the superclasses are recursively searched.
     *
     * @exception NoSuchMethodException if a matching method is not found or
     * if the name is "<init>"or "<clinit>".
     */
    public static Object invoke(final Object object, final String name,
            final Class parameterTypes[], final Object args[])
        throws Throwable
    {
        if (object == null)
        {
            throw new IllegalArgumentException("Invalid null object argument");
        }
        Class cls = object.getClass();
        while (cls != null)
        {
            try
            {
                Method method = cls.getDeclaredMethod(name, parameterTypes);
                method.setAccessible(true);
                return method.invoke(object, args);
            }
            catch (InvocationTargetException e)
            {
                /* if the method throws an exception, it is embedded into an
                 * InvocationTargetException. */
                throw e.getTargetException();
            }
            catch (Exception ex)
            {
                /* in case of an exception, we will throw a new
                 * NoSuchFieldException object */
                ;
            }
            cls = cls.getSuperclass();
        }
        throw new NoSuchMethodException("Failed method invocation: " + object.getClass().getName()
                + "." + name + "()");
    }

    /**
     * Invokes the method represented by the name value on the specified class
     * with the specified parameters. Individual parameters are automatically
     * unwrapped to match primitive formal parameters, and both primitive and
     * reference parameters are subject to widening conversions as necessary.
     * The value returned by the method is automatically wrapped in an object
     * if it has a primitive type.<p>
     *
     * The class is first searched for any matching method.  If no matching
     * class is found, the superclasses are recursively searched.
     *
     * @exception NoSuchMethodException if a matching method is not found or
     * if the name is "<init>"or "<clinit>".
     */
    public static Object invoke(final Class cls, final String name, final Class parameterTypes[],
            final Object args[])
        throws Exception
    {
        if (cls == null)
        {
            throw new IllegalArgumentException("Invalid null cls argument");
        }
        Class base = cls;
        while (base != null)
        {
            try
            {
                Method method = base.getDeclaredMethod(name, parameterTypes);
                method.setAccessible(true);
                return method.invoke(base, args);
            }
            catch (InvocationTargetException e)
            {
                /* if the method throws an exception, it is embedded into an
                 * InvocationTargetException. */
                throw (Exception)e.getTargetException();
            }
            catch (Exception ex)
            {
                /* in case of an exception, we will throw a new
                 * NoSuchFieldException object */
                ;
            }
            base = base.getSuperclass();
        }
        throw new NoSuchMethodException("Failed static method invocation: " + cls.getName() + "."
                + name + "()");
    }

}
