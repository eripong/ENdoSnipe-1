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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.event.EventConstants;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.CallTree;
import jp.co.acroquest.endosnipe.javelin.CallTreeRecorder;
import jp.co.acroquest.endosnipe.javelin.StatsJavelinRecorder;
import jp.co.acroquest.endosnipe.javelin.conf.JavelinMessages;
import jp.co.acroquest.endosnipe.javelin.event.CommonEvent;
import jp.co.acroquest.endosnipe.javelin.event.LeakDetectEvent;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;
import jp.co.acroquest.endosnipe.javelin.util.StatsUtil;
import jp.co.acroquest.endosnipe.javelin.util.ThreadUtil;
import jp.co.acroquest.endosnipe.javelin.util.concurrent.ConcurrentHashMap;

import org.netbeans.insane.scanner.ScannerUtils;

/**
 * コレクションクラス、マップクラスのサイズを常時監視する監視クラス
 * 監視対象に対する弱参照とキーを保持し、一定間隔に登録されたコレクションサイズを取得する
 * 監視対象となるクラスが生成時に以下の処理を実施することで、監視対処に自らを追加する。
 * 「CollectionTracer.addTraceTarget(this);」
 * 
 * TODO このクラス内部で、監視対象のMapなどを利用しているため、StackOverFlowErrorになる場合がある。ThreadLocalな状態を持つべきか？
 * 
 * @author eriguchi
 */
public class CollectionMonitor
{
    private static JavelinConfig javelinConfig__ = new JavelinConfig();

    /** 監視対象（コレクション）を保持するマップ */
    private static Map<String, CollectionMonitorEntry> listMap__ =
            new ConcurrentHashMap<String, CollectionMonitorEntry>();

    /** 監視対象（コレクション）を保持するマップ */
    private static Map<String, CollectionMonitorEntry> queueMap__ =
            new ConcurrentHashMap<String, CollectionMonitorEntry>();

    /** 監視対象（コレクション）を保持するマップ */
    private static Map<String, CollectionMonitorEntry> setMap__ =
            new ConcurrentHashMap<String, CollectionMonitorEntry>();

    /** 監視対象（マップ）を保持するマップ */
    private static Map<String, CollectionMonitorEntry> mapMap__ =
            new ConcurrentHashMap<String, CollectionMonitorEntry>();

    /** サイズの大きい方から、以下の数のエントリーをリスト化して返す */
    public static final int TOPTRACENUMBER = 5;

    private static ThreadLocal<Boolean> isTracing__ = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue()
        {
            return Boolean.TRUE;
        }
    };

    /**
     * デフォルトコンストラクタ
     */
    private CollectionMonitor()
    {
        // Do Nothing.
    }

    /**
     * コレクションクラスを監視対象に加える
     * 
     * @param target 監視対象に加えるコレクションクラス
     * @param element addやaddAllされた要素
     */
    public static void addTrace(final Collection<?> target, Object element)
    {
        //識別子をつける
        String identifier = StatsUtil.createIdentifier(target);

        if (listMap__.containsKey(identifier) || queueMap__.containsKey(identifier)
                || setMap__.containsKey(identifier))
        {
            CollectionMonitorEntry collectionMonitorEntry = null;
            if (listMap__.containsKey(identifier))
            {
                collectionMonitorEntry = listMap__.get(identifier);
            }
            else if (queueMap__.containsKey(identifier))
            {
                collectionMonitorEntry = queueMap__.get(identifier);
            }
            else if (setMap__.containsKey(identifier))
            {
                collectionMonitorEntry = setMap__.get(identifier);
            }

            if (collectionMonitorEntry != null)
            {
                synchronized (collectionMonitorEntry)
                {
                    detect(identifier, collectionMonitorEntry, element);
                }
            }

            return;
        }

        CollectionMonitorEntry collectionMonitorEntry =
                new CollectionMonitorEntry(identifier, target);
        detect(identifier, collectionMonitorEntry, element);

        if (target instanceof Queue<?>)
        {
            queueMap__.put(identifier, collectionMonitorEntry);
            getSortedQueueList();
        }
        else if (target instanceof List<?>)
        {
            listMap__.put(identifier, collectionMonitorEntry);
            getSortedListList();
        }
        else if (target instanceof Set<?>)
        {
            setMap__.put(identifier, collectionMonitorEntry);
            getSortedSetList();
        }
        else
        {
            // targetはCollectionのインスタンス
            listMap__.put(identifier, collectionMonitorEntry);
            getSortedListList();
        }
    }

    /**
     * Leakを検出した時のイベントを作成します。
     * @param identifier 識別子
     * @param target リークしているオブジェクトを含むコレクション
     * @param count リークしているオブジェクトの要素数
     * @param element addやaddAll、putメソッドの要素
     * @return {@link CommonEvent}オブジェクト
     */
    static CommonEvent createLeakDetectedEvent(final String identifier, final Collection<?> target,
            int count, Object element)
    {
        CommonEvent event = new LeakDetectEvent();

        int leakSize = 0;

        //コレクションのオブジェクトサイズ算出処理の負荷が高いため、
        //サイズは設定フラグがONになっている場合のみ算出/出力を行う
        if (javelinConfig__.isLeakCollectionSizePrint() == true && target != null)
        {
                try
                {
                    leakSize = ScannerUtils.recursiveSizeOf(target, null);
                }
                catch (Exception ex)
                {
                    String key = "javelin.converter.leak.monitor.CannotGetSize";
                    String message = JavelinMessages.getMessage(key);
                    SystemLogger.getInstance().warn(message, ex);
                }
        }
        
        // Leak検出を引き起こしたオブジェクトのクラス名を取得する。
        String className = null;
        if (element != null)
        {
            className = element.getClass().getName();
        }

        StackTraceElement[] stacktraces = ThreadUtil.getCurrentStackTrace();
        String staceTrace = ThreadUtil.getStackTrace(stacktraces, javelinConfig__.getTraceDepth());

        event.addParam(EventConstants.PARAM_LEAK_IDENTIFIER, identifier);
        event.addParam(EventConstants.PARAM_LEAK_THRESHOLD,
                       String.valueOf(javelinConfig__.getCollectionSizeThreshold()));
        event.addParam(EventConstants.PARAM_LEAK_COUNT, String.valueOf(count));
        if (javelinConfig__.isLeakCollectionSizePrint() == true)
        {
            event.addParam(EventConstants.PARAM_LEAK_SIZE, String.valueOf(leakSize));
        }
        if (className != null)
        {
            event.addParam(EventConstants.PARAM_LEAK_CLASS_NAME, className);
        }
        event.addParam(EventConstants.PARAM_LEAK_STACK_TRACE, staceTrace);

        return event;
    }

    /**
     * マップクラスを監視対象に加える
     * 
     * @param target 監視対象に加えるマップクラス
     * @param element putされた要素
     */
    public static void addTrace(final Map<?, ?> target, Object element)
    {
        //識別子をつける
        String identifier = StatsUtil.createIdentifier(target);

        if (mapMap__.containsKey(identifier))
        {
            CollectionMonitorEntry collectionMonitorEntry = mapMap__.get(identifier);
            detect(identifier, collectionMonitorEntry, element);

            return;
        }
        
        CollectionMonitorEntry collectionMonitorEntry =
                new CollectionMonitorEntry(identifier, target);
        
        synchronized (collectionMonitorEntry)
        {
            detect(identifier, collectionMonitorEntry, element);
        }

        mapMap__.put(identifier, collectionMonitorEntry);
        getSortedMapList();
    }

    /**
     * メモリリークの検出を行う。
     * 
     * 数回(※1)に1回スタックトレースを取得し、
     * 既に検出済みのスタックトレース(※2)と一致しない場合のみ
     * メモリリークとして検出する。
     * 
     * <ul>
     * <li>javelin.leak.interval スタックトレースを何回ごとに取得するか(※1)</li>
     * <li>javelin.leak.traceMax スタックトレースを保持する回数(※2)</li>
     * <li>javelin.leak.traceDepth スタックトレース取得の深さ(※2)</li>
     * </ul>
     * 
     * @param identifier リークしたオブジェクトの識別子。
     * @param monitorEntry リークしたオブジェクトの内容。
     * @param element addやaddAll、putされた要素
     */
    static void detect(final String identifier,
            final CollectionMonitorEntry monitorEntry, Object element)
    {
        if (monitorEntry == null)
        {
            return;
        }

        monitorEntry.updateEntryNumber();

        int interval = javelinConfig__.getCollectionInterval();
        if (interval <= 0)
        {
            return;
        }

        if (monitorEntry.getDetectCount() % interval == 0)
        {
            Collection<?> target = getTargetByCollection(monitorEntry);
            CommonEvent detectedEvent;

            int hashCode = getLeakDetectHashCode(identifier);
            if (monitorEntry.containsTrace(hashCode) == false)
            {
                detectedEvent = createLeakDetectedEvent(identifier, target,
                                                        monitorEntry.getEntryNumber(), element);
                addEvent(monitorEntry, detectedEvent, hashCode, false);
            }
            else
            {
                int entryNumber = monitorEntry.getEntryNumber();
                int collectionSizeThreshold = javelinConfig__.getCollectionSizeThreshold();
                int detectedSize = monitorEntry.getDetectedSize();
                if (entryNumber >= detectedSize + collectionSizeThreshold)
                {
                    detectedEvent = createLeakDetectedEvent(identifier, target,
                                                            monitorEntry.getEntryNumber(), element);
                    monitorEntry.clearAllTrace();
                    addEvent(monitorEntry, detectedEvent, hashCode, true);
                }
            }
        }
        monitorEntry.setDetectCount(monitorEntry.getDetectCount() + 1);
    }

    private static void addEvent(final CollectionMonitorEntry monitorEntry,
            CommonEvent detectedEvent, int hashCode, boolean clear)
    {
        StatsJavelinRecorder.addEvent(detectedEvent, clear);
        
        int traceMax = javelinConfig__.getCollectionTraceMax();
        int traceCount = monitorEntry.getTraceCount();
        if (traceMax > 0)
        {
            if (traceCount >= traceMax)
            {
                monitorEntry.removeTrace();
            }
            monitorEntry.addTrace(hashCode);
            monitorEntry.setDetectedSize(monitorEntry.getEntryNumber());
        }
    }

    /**
     * 監視対象コレクションまたはマップを、コレクションで返します。<br />
     *
     * 監視対象が GC で回収されている場合は <code>null</code> を返します。<br />
     *
     * @param monitorEntry Collection のエントリ
     * @return 監視対象コレクション、または監視対象マップを含むコレクション
     */
    private static Collection<?> getTargetByCollection(final CollectionMonitorEntry monitorEntry)
    {
        Collection<?> target = monitorEntry.getCollection();
        if (target == null)
        {
            Map<?, ?> targetMap = monitorEntry.getMap();
            if (targetMap != null)
            {
                target = Arrays.asList(new Object[]{targetMap});
            }
        }
        return target;
    }

    private static int getLeakDetectHashCode(String identifier)
    {
        StackTraceElement[] stacktraces = ThreadUtil.getCurrentStackTrace();
        String staceTrace =
                ThreadUtil.getStackTrace(stacktraces,
                                         javelinConfig__.getCollectionLeakDetectDepth());
        return staceTrace.hashCode();
    }

    /**
     * Listの要素数のリストを取得する。
     * 
     * @return Listの要素数のリスト。
     */
    public static List<CollectionMonitorEntry> getSortedListList()
    {
        return getSortedList(listMap__);
    }

    /**
     * Setの要素数のリストを取得する。
     * 
     * @return Setの要素数のリスト。
     */
    public static List<CollectionMonitorEntry> getSortedSetList()
    {
        return getSortedList(setMap__);
    }

    /**
     * Queueの要素数のリストを取得する。
     * 
     * @return Queueの要素数のリスト。
     */
    public static List<CollectionMonitorEntry> getSortedQueueList()
    {
        return getSortedList(queueMap__);
    }

    /**
     * Mapの要素数のリストを取得する。
     * 
     * @return Mapの要素数のリスト。
     */
    public static List<CollectionMonitorEntry> getSortedMapList()
    {
        List<CollectionMonitorEntry> returnList = getSortedList(mapMap__);

        return makeTopSizeList(returnList);
    }

    private static List<CollectionMonitorEntry> getSortedList(
            final Map<String, CollectionMonitorEntry> listMap)
    {
        List<CollectionMonitorEntry> returnList = new ArrayList<CollectionMonitorEntry>();
        Set<Entry<String, CollectionMonitorEntry>> collectionKeySet = listMap.entrySet();
        for (Entry<String, CollectionMonitorEntry> targetReferenceEntry : collectionKeySet)
        {
            String targetIdentifier = targetReferenceEntry.getKey();
            CollectionMonitorEntry target = targetReferenceEntry.getValue();
            target.updateEntryNumber();

            if (target.exists())
            {
                returnList.add(target);
            }
            else
            {
                listMap.remove(targetIdentifier);
            }
        }

        return makeTopSizeList(returnList);
    }

    /**
     * 対象のリストのサイズTopをリストとして返す
     * 元のリストはソートされた状態となる
     * 
     * @param targetList 対象のリスト
     * @return 対象リスト中のサイズの要素のリスト
     */
    private static List<CollectionMonitorEntry> makeTopSizeList(
            final List<CollectionMonitorEntry> targetList)
    {
        Collections.sort(targetList, new Comparator<CollectionMonitorEntry>() {
            public int compare(final CollectionMonitorEntry entry1,
                    final CollectionMonitorEntry entry2)
            {
                return entry2.getEntryNumber() - entry1.getEntryNumber();
            }
        });

        List<CollectionMonitorEntry> topSizeList =
                targetList.subList(0, (targetList.size() < TOPTRACENUMBER) ? targetList.size()
                        : TOPTRACENUMBER);
        return topSizeList;
    }

    /**
     * 指定したCollectionがサイズが閾値を超えてる場合に、Collectionを監視対象に加える。
     * 
     * @param target 監視対象候補のCollection。
     * @param element addまたはaddAllされた要素
     */
    public static void preProcessCollectionAdd(final Collection<?> target, Object element)
    {
        CallTreeRecorder recorder = CallTreeRecorder.getInstance();
        CallTree         tree     = recorder.getCallTree();
        
        if (tree.isCollectionMonitorEnabled() == false)
        {
            return;
        }
        
        if (isTracing__.get().booleanValue() == false)
        {
            return;
        }

        isTracing__.set(Boolean.FALSE);
        try
        {
            if (isTraceTarget(target))
            {
                addTrace(target, element);
            }
        }
        finally
        {
            isTracing__.set(Boolean.TRUE);
        }
    }

    /**
     * 指定したMapがサイズが閾値を超えてる場合に、Mapを監視対象に加える。
     * 
     * @param targetMap 監視対象候補のMap。
     * @param element putされた要素
     */
    public static void preProcessMapPut(final Map<?, ?> targetMap, Object element)
    {
        CallTreeRecorder recorder = CallTreeRecorder.getInstance();
        CallTree         tree     = recorder.getCallTree();
        
        if (tree.isCollectionMonitorEnabled() == false)
        {
            return;
        }
        
        if (isTracing__.get().booleanValue() == false)
        {
            return;
        }

        isTracing__.set(Boolean.FALSE);
        try
        {
            if (isTraceTarget(targetMap))
            {
                addTrace(targetMap, element);
            }
        }
        finally
        {
            isTracing__.set(Boolean.TRUE);
        }
    }

    /**
     * 監視対象に加えるかどうかを判定する。
     * 
     * @param target 監視対象候補。
     * @return 監視対象に加えるかどうか。
     */
    private static boolean isTraceTarget(final Map<?, ?> target)
    {
        int size;
        try
        {
            size = target.size();
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
            size = 0;
        }
        return size > javelinConfig__.getCollectionSizeThreshold();
    }

    /**
     * 監視対象に加えるかどうかを判定する。
     * 
     * @param target 監視対象候補。
     * @return 監視対象に加えるかどうか。
     */
    private static boolean isTraceTarget(final Collection<?> target)
    {
        int size;
        try
        {
            size = target.size();
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
            size = 0;
        }
        return size > javelinConfig__.getCollectionSizeThreshold();
    }

    /**
     * 監視を実行するかどうかを設定する。
     * 
     * @param isTracing 監視を実行するかどうか。
     */
    public static void setTracing(final Boolean isTracing)
    {
        isTracing__.set(isTracing);
    }

    /**
     * 監視を実行するかどうかを取得する。
     * 
     * @return 監視を実行するかどうか。
     */
    public static Boolean isTracing()
    {
        return isTracing__.get();
    }
}
