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
package jp.co.acroquest.endosnipe.javelin.common;

import java.util.List;
import java.util.Map;

import jp.co.smg.endosnipe.javassist.ClassPool;
import jp.co.smg.endosnipe.javassist.CtClass;
import jp.co.smg.endosnipe.javassist.NotFoundException;
import jp.co.smg.endosnipe.javassist.bytecode.ClassFile;
import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.conf.JavelinMessages;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;
import jp.co.acroquest.endosnipe.javelin.util.concurrent.ConcurrentHashMap;

/**
 * Javassistを使うためのユーティリティクラス。
 * 
 * @author yamasaki
 */
public class JavassistUtil
{
    /** 継承関係のキャッシュ保持最大数。 */
    private static final int CACHE_MAX = 50000;

    /** detach対象のクラス。 */
    private static ThreadLocal<List<CtClass>> detachClasses__;
    static
    {
        detachClasses__ = new ThreadLocal<List<CtClass>>() {
            @Override
            public List<CtClass> initialValue()
            {
                return new ArrayList<CtClass>();
            }
        };
    }

    private static Map<String, Boolean> inheritedMap__ = new ConcurrentHashMap<String, Boolean>();

    /**
     * デフォルトコンストラクタ
     */
    private JavassistUtil()
    {
        // Do Nothing.
    }

    /** 継承を調べる深さの最大値 */
    private static int maximumDepth__ = new JavelinConfig().getInheritanceDepth();

    /**
     * 継承または実装しているかどうかを調べる。
     * 
     * @param ctClass CtClass
     * @param pool ClassPool
     * @param inheritedClassName クラスorインタフェース名
     * @return inheritedClassNameを継承または実装していればtrue
     */
    public static boolean isInherited(final CtClass ctClass, final ClassPool pool,
            final String inheritedClassName)
    {
        String className = ctClass.getName();

        String inheritedKey = createInheritedKey(inheritedClassName, className);
        Boolean cachedResult = inheritedMap__.get(inheritedKey);
        if (cachedResult != null)
        {
            return cachedResult.booleanValue();
        }

        boolean result;
        if (inheritedClassName.equals(className))
        {
            result = true;
        }
        else if (maximumDepth__ == 0)
        {
            result = false;
        }
        else
        {
            result = isInherited(ctClass, pool, inheritedClassName, 0);
        }

        addInheritedCache(inheritedKey, result);
        return result;
    }

    private static void addInheritedCache(String inheritedKey, boolean result)
    {
        if (inheritedMap__.size() > CACHE_MAX)
        {
            return;
        }

        inheritedMap__.put(inheritedKey, Boolean.valueOf(result));
    }

    private static String createInheritedKey(final String inheritedClassName, String className)
    {
        return className + "==" + inheritedClassName;
    }

    /**
     * 継承しているかどうかを調べる。
     * 
     * @param ctClass CtClass
     * @param pool ClassPool
     * @param inheritedClassName クラスorインタフェース名
     * @param depth 継承を調べる深さ
     * @return inheritedClassNameを継承または実装していればtrue
     */
    private static boolean isInherited(final CtClass ctClass, final ClassPool pool,
            final String inheritedClassName, int depth)
    {
        String className = ctClass.getName();
        String inheritedKey = createInheritedKey(inheritedClassName, className);
        Boolean cachedResult = inheritedMap__.get(inheritedKey);
        if (cachedResult != null)
        {
            return cachedResult.booleanValue();
        }

        ClassFile classFile = ctClass.getClassFile2();
        String superClassName = classFile.getSuperclass();
        if (superClassName != null && superClassName.equals(inheritedClassName))
        {
            inheritedMap__.put(inheritedKey, Boolean.TRUE);
            return true;
        }

        String[] interfaces = classFile.getInterfaces();
        if (interfaces != null)
        {
            for (String anInterfaceName : interfaces)
            {
                if (anInterfaceName.equals(inheritedClassName))
                {
                    inheritedMap__.put(inheritedKey, Boolean.TRUE);
                    return true;
                }
            }
        }

        depth++;
        if (depth >= maximumDepth__)
        {
            return false;
        }

        boolean result = false;
        if (interfaces != null)
        {
            for (String anInterfaceName : interfaces)
            {
                CtClass anInterface = null;
                try
                {
                    anInterface = pool.get(anInterfaceName);
                    result = isInherited(anInterface, pool, inheritedClassName, depth);
                    if (result == true)
                    {
                        inheritedMap__.put(inheritedKey, Boolean.TRUE);
                        return true;
                    }
                }
                catch (NotFoundException nfe)
                {
                    String key = "javelin.common.JavassistUtil.FailGetInterface";
                    String message = JavelinMessages.getMessage(key, className, anInterfaceName);
                    SystemLogger.getInstance().warn(message, nfe);
                }
                finally
                {
                    if (anInterface != null)
                    {
                        addDetachClass(anInterface);
                    }
                }
            }
        }

        CtClass superClass = null;
        try
        {
            if (superClassName == null || Object.class.getCanonicalName().equals(superClassName))
            {
                result = false;
            }
            else
            {
                superClass = pool.get(superClassName);
                result = isInherited(superClass, pool, inheritedClassName, depth);
            }
        }
        catch (NotFoundException nfe)
        {
            String message =
                    JavelinMessages.getMessage("javelin.common.JavassistUtil.FailGetSuperClass",
                                               ctClass.getName(), superClassName);
            SystemLogger.getInstance().warn(message, nfe);
            result = false;
        }
        finally
        {
            if (superClass != null)
            {
                addDetachClass(superClass);
            }
        }

        addInheritedCache(inheritedKey, result);
        return result;
    }

    /**
     * CtClassのdetachを行うリストに追加する。
     * 
     * @param ctClass detach対象のクラス。
     */
    public static void addDetachClass(final CtClass ctClass)
    {
        detachClasses__.get().add(ctClass);
    }

    /**
     * CtClassのdetachをまとめて行う。
     */
    public static void detachAll()
    {
        for (CtClass ctClass : detachClasses__.get())
        {
            try
            {
                ctClass.detach();
            }
            // CHECKSTYLE:OFF
            catch (RuntimeException re)
            {
            }
            // CHECKSTYLE:ON
        }
    }

    /**
     * detach対象のリストをクリアする。
     */
    public static void clearDetachClass()
    {
        detachClasses__.set(new ArrayList<CtClass>());
    }
}
