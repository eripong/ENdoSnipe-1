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
package jp.co.acroquest.endosnipe.javelin;

import jp.co.acroquest.endosnipe.javelin.util.HashMap;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;

/**
 * rootとなるInvocationを管理するクラス。</br>
 * 
 * @author tsukano
 */
public class RootInvocationManager
{
    /** ルートのInvocationを登録したマップ。 */
    private static Map<String, Invocation> rootInvocationMap__ = new HashMap<String, Invocation>();

    /** 設定 */
    private static JavelinConfig javelinConfig__ = new JavelinConfig();

    /**
     * インスタンスは生成しない。
     */
    private RootInvocationManager()
    {
        // Do nothing.
    }

    /**
     * rootのInvocationのリストを返す。
     * @return rootのInvocationのリスト
     */
    public static Invocation[] getAllRootInvocations()
    {
        synchronized (rootInvocationMap__)
        {
            int size = rootInvocationMap__.values().size();
            Invocation[] invocations = rootInvocationMap__.values().toArray(new Invocation[size]);
            return invocations;
        }
    }

    /**
     * 指定されたInvocationをrootとして管理する。
     * @param invocation rootとして管理するInvocation
     */
    public static void addRootInvocation(final Invocation invocation)
    {
        invocation.setRoot(true);
        addInvocation(invocation);
    }

    /**
     * 指定された {@link Invocation} を管理します。<br />
     *
     * @param invocation 管理する {@link Invocation}
     */
    public static void addInvocation(final Invocation invocation)
    {
        synchronized (rootInvocationMap__)
        {
            String key = invocation.getRootInvocationManagerKey();
            if (!rootInvocationMap__.containsKey(key))
            {
                invocation.setTatEnabled(javelinConfig__.isTatEnabled());
                invocation.setTatKeepTime(javelinConfig__.getTatKeepTime());
                rootInvocationMap__.put(key, invocation);
            }
        }
    }

    /**
     * 指定された {@link Invocation} を管理対象から除外します。<br />
     *
     * @param invocation 除外する {@link Invocation}
     */
    public static void removeInvocation(final Invocation invocation)
    {
        synchronized (rootInvocationMap__)
        {
            String key = invocation.getRootInvocationManagerKey();
            rootInvocationMap__.remove(key);
        }
    }

    /**
     * 本クラスが管理するInvocationにTurn Around Timeを計測するかどうかを設定する。
     *
     * @param tatEnabled Turn Around Timeを計測するならtrue
     */
    public static void setTatEnabled(final boolean tatEnabled)
    {
        synchronized (rootInvocationMap__)
        {
            for (Invocation invocaton : rootInvocationMap__.values())
            {
                invocaton.setTatEnabled(tatEnabled);
            }
        }
    }

    /**
     * 本クラスが管理するInvocationにTurn Around Timeの保持期間をセットする。
     *
     * @param tatKeepTime Turn Around Timeの保持期間
     */
    public static void setTatKeepTime(final long tatKeepTime)
    {
        synchronized (rootInvocationMap__)
        {
            for (Invocation invocaton : rootInvocationMap__.values())
            {
                invocaton.setTatKeepTime(tatKeepTime);
            }
        }
    }

}
