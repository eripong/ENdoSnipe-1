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

import java.util.Collection;
import java.util.Map;

import jp.co.acroquest.endosnipe.javelin.bean.Component;
import jp.co.acroquest.endosnipe.javelin.bean.ExcludeMonitor;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;
import jp.co.acroquest.endosnipe.javelin.bean.TripleState;
import jp.co.acroquest.endosnipe.javelin.converter.util.CalledMethodCounter;
import jp.co.acroquest.endosnipe.javelin.util.concurrent.ConcurrentHashMap;

/**
 * コンポーネントを管理するクラス。<br />
 *
 * @author acroquest
 */
public class MBeanManager
{
    /** ComponentMBeanを登録したマップ。 */
    private static ConcurrentHashMap<String, Component> mBeanMap__;

    static
    {
        Map<String, Component> deserializedMap = MBeanManagerSerializer.deserialize();
        mBeanMap__ = new ConcurrentHashMap<String, Component>(deserializedMap);

        // デシリアライズした Invocation のうち、レスポンスグラフの出力が ON になっているものをグラフに出すために、
        // RootInvocationManager に Invocation を登録する。
        // また、計測対象から除外した Invocation は、デシリアライズ時に ExcludeMonitor に登録する。
        for (Component component : mBeanMap__.values())
        {
            for (Invocation invocation : component.getAllInvocation())
            {
                if (invocation.isResponseGraphOutputTarget())
                {
                    RootInvocationManager.addInvocation(invocation);
                }
                if (invocation.getMeasurementTarget() == TripleState.OFF)
                {
                    ExcludeMonitor.addExclude(invocation);
                    ExcludeMonitor.removeTarget(invocation);
                    ExcludeMonitor.removeTargetPreferred(invocation);
                    ExcludeMonitor.removeExcludePreferred(invocation);
                }
            }
        }

        // shutdownHookの追加
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                if (mBeanMap__ != null)
                {
                    synchronized (mBeanMap__)
                    {
                        MBeanManagerSerializer.serialize(mBeanMap__);
                    }
                }
            }
        });
    }

    /**
     * コンストラクタを隠蔽します。<br />
     */
    private MBeanManager()
    {
        // Do nothing.
    }

    /**
     * すべてのコンポーネントを返します。<br />
     *
     * @return すべてのコンポーネント
     */
    public static Component[] getAllComponents()
    {
        Collection<Component> mBeanMapValues = mBeanMap__.values();
        int size = mBeanMapValues.size();
        Component[] components = mBeanMapValues.toArray(new Component[size]);
        return components;
    }

    /**
     * 指定された名前のコンポーネントを返します。<br />
     *
     * @param className 取得するコンポーネントの名前
     * @return 指定されたコンポーネントが存在する場合はコンポーネントオブジェクト、存在しない場合は <code>null</code>
     */
    public static Component getComponent(final String className)
    {
        return mBeanMap__.get(className);
    }

    /**
     * 指定された名前のコンポーネントを登録します。<br />
     *
     * @param className コンポーネントの名前
     * @param component コンポーネントオブジェクト
     * 
     * @return コンポーネント
     */
    public static Component setComponent(final String className, final Component component)
    {
        return mBeanMap__.putIfAbsent(className, component);
    }

    /**
     * 指定された名前のコンポーネントを削除します。<br />
     *
     * @param className コンポーネントの名前
     */
    public static void removeComponent(final String className)
    {
        mBeanMap__.remove(className);
    }

    /**
     * コンポーネント内の Invocation の値をリセットします。<br />
     */
    public static void reset()
    {
        for (Component component : mBeanMap__.values())
        {
            synchronized (component)
            {
                component.reset();
            }
        }
        CalledMethodCounter.clear();
    }
}
