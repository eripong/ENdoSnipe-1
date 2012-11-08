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
package jp.co.acroquest.endosnipe.common.util;

import java.lang.reflect.Method;

/**
 * Eclipse 環境に関するユーティリティクラスです。<br />
 * 
 * @author y-komori
 */
public class EclipseUtil
{
    private static final String RESOURCES_PLUGIN_CLASS =
            "org.eclipse.core.resources.ResourcesPlugin";

    private EclipseUtil()
    {

    }

    /**
     * Eclipse 環境であるかどうかを返します。<br />
     * 
     * @return Eclipse 環境ならば <code>true</code>。
     */
    public static final boolean isEclipseAvailable()
    {
        boolean result = false;
        // Eclipse 環境かどうかをチェックする
        try
        {
            Class<?> clazz = Class.forName(RESOURCES_PLUGIN_CLASS);
            if (clazz != null)
            {
                // ResourcesPlugin#getPlugin() の結果が null でない場合
                // Eclipse ランタイム環境と判定
                Object plugin = getPlugin(clazz, "getPlugin");
                if (plugin != null)
                {
                    result = true;
                }
            }
        }
        // CHECKSTYLE:OFF
        catch (ClassNotFoundException ex)
        {
            // Do nothing.
        }
        // CHECKSTYLE:ON
        return result;
    }

    /**
     * 指定されたクラスのメソッドを呼び出して結果を返します。<br />
     * Plugin のオブジェクトを取得するのに利用します。<br />
     * 
     * @param className クラス名
     * @param methodName メソッド名
     * @param loader クラスローダ
     * @return オブジェクト
     */
    public static final Object getPlugin(final String className, final String methodName,
            final ClassLoader loader)
    {
        Class<?> clazz;
        try
        {
            clazz = Class.forName(className, true, loader);
            return getPlugin(clazz, methodName);
        }
        catch (Exception ex)
        {
            // ここで例外が発生する場合は明らかなバグ
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 指定されたクラスのメソッドを呼び出して結果を返します。<br />
     * Plugin のオブジェクトを取得するのに利用します。<br />
     *
     * @param clazz クラスオブジェクト
     * @param methodName メソッド名
     * @return オブジェクト
     */
    public static final Object getPlugin(final Class<?> clazz, final String methodName)
    {
        try
        {
            Method method = clazz.getMethod(methodName);
            return method.invoke(null);
        }
        catch (Exception ex)
        {
            // ここで例外が発生する場合は明らかなバグ
            ex.printStackTrace();
        }
        return null;
    }
}
