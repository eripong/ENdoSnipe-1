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
package jp.co.acroquest.endosnipe.javelin.converter.leak.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.event.EventConstants;
import jp.co.acroquest.endosnipe.javelin.StatsJavelinRecorder;
import jp.co.acroquest.endosnipe.javelin.event.CommonEvent;
import jp.co.acroquest.endosnipe.javelin.util.concurrent.ConcurrentHashMap;
import jp.co.acroquest.endosnipe.javelin.util.concurrent.CopyOnWriteArraySet;

/**
 * インスタンス数を監視する。
 * 
 * @author eriguchi
 */
public class InstanceMonitor
{
    private static final int TRACE_MAX = 1000;

    private static ConcurrentHashMap<String, AtomicInteger> instanceNumMap__ =
            new ConcurrentHashMap<String, AtomicInteger>();

    private static Set<Integer> traceSet__ = new CopyOnWriteArraySet<Integer>();

    private static JavelinConfig javelinConfig__ = new JavelinConfig();

    /**
     * コンストラクタ。
     */
    private InstanceMonitor()
    {
    }

    /**
     * インスタンス生成時に呼び出し、インスタンス数を増加させる。
     * 
     * @param className 追加するクラス名。
     */
    public static void add(String className)
    {
        if (className == null)
        {
            return;
        }

        instanceNumMap__.putIfAbsent(className, new AtomicInteger(0));
        AtomicInteger count = instanceNumMap__.get(className);
        int currentCount = count.incrementAndGet();

        int collectionSizeThreshold = javelinConfig__.getCollectionSizeThreshold();
        if (currentCount <= collectionSizeThreshold)
        {
            return;
        }
        
        CommonEvent event =
                            CollectionMonitor.createLeakDetectedEvent(className, null,
                                                                      currentCount, null);
        int hashCode = event.getParam(EventConstants.PARAM_LEAK_STACK_TRACE).hashCode();
        if (traceSet__.contains(hashCode) == false)
        {
            StatsJavelinRecorder.addEvent(event);
            if (traceSet__.size() > TRACE_MAX)
            {
                traceSet__.clear();
            }
            traceSet__.add(hashCode);
        }
        else
        {
            if (currentCount % collectionSizeThreshold == 0)
            {
                StatsJavelinRecorder.addEvent(event, true);
            }
        }
    }

    /**
     * インスタンス生成時に呼び出し、インスタンス数を減少させる。
     * 
     * @param className 削除するクラス名。
     */
    public static void remove(String className)
    {
        if (className == null)
        {
            return;
        }

        AtomicInteger count = instanceNumMap__.get(className);
        count.decrementAndGet();
    }

    /**
     * インスタンス数のMapを取得する。
     * 
     * @return　インスタンス数のMap
     */
    public static Map<String, AtomicInteger> getInstanceNumMap()
    {
        return Collections.unmodifiableMap(instanceNumMap__);
    }

    /**
     * インスタンス数のListを取得する。
     * 
     * @return　インスタンス数のMap
     */
    public static List<ClassHistogramEntry> getHistogramList()
    {
        Map<String, AtomicInteger> instanceNumMap = InstanceMonitor.getInstanceNumMap();
        List<ClassHistogramEntry> histgramList = new ArrayList<ClassHistogramEntry>();
        for (Map.Entry<String, AtomicInteger> entry : instanceNumMap.entrySet())
        {
            ClassHistogramEntry histogramEntry = new ClassHistogramEntry();
            histogramEntry.setClassName(entry.getKey());
            histogramEntry.setInstances(entry.getValue().get());
            histgramList.add(histogramEntry);
        }
        
        return histgramList;
    }
}
