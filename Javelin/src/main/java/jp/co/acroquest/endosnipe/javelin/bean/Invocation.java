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
package jp.co.acroquest.endosnipe.javelin.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.event.EventConstants;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogConstants;
import jp.co.acroquest.endosnipe.javelin.CallTreeNode;
import jp.co.acroquest.endosnipe.javelin.converter.util.CalledMethodCounter;
import jp.co.acroquest.endosnipe.javelin.util.concurrent.ConcurrentHashMap;

/**
 * メソッド呼び出しの統計情報を記録する。
 * @author eriguchi
 */
public class Invocation implements InvocationMBean, Serializable
{

    /** バッファサイズのデフォルト値 */
    private static final int DEF_BUFFER_SIZE = 256;

    /** シリアルID */
    private static final long serialVersionUID = -6941143619225037990L;    

    /** 初期値 */
    private static final long INITIAL = -1;

    /** レスポンス回数が 0 以外のときの tatCallZeroValueStartTime_ の値 */
    public static final long TAT_ZERO_KEEP_TIME_NULL_VALUE = 0;

    /** アラーム閾値が指定されていないときの alarmThreshold の値 */
    public static final long THRESHOLD_NOT_SPECIFIED = -1;

    /** クラス名 */
    private final String className_;

    /** メソッド名 */
    private final String methodName_;

    /** CallTree内の計測データを保存するために使用する、クラス名、メソッド名を結合したキー情報。 */
    private final String key_;

    /** RootInvocationManagerが使用するキー情報。  */
    private final String rootInvocationManagerKey_;
    
    /** メソッド呼び出し回数 */
    private long count_;

    /** プロセス起動後からの呼び出し回数 */
    private transient long countFromStartup_;

    private static final int SUM = 0;
    private static final int MAX = 1;
    private static final int MIN = 2;
    
    private long[] intervals_                = new long[]{0, INITIAL, INITIAL};
    private long[] cpuIntervals_             = new long[]{0, INITIAL, INITIAL};
    private long[] userIntervals_            = new long[]{0, INITIAL, INITIAL};
    private long[] accumulatedIntervals_     = new long[]{0, INITIAL, INITIAL};
    private long[] accumulatedCpuIntervals_  = new long[]{0, INITIAL, INITIAL};
    private long[] accumulatedUserIntervals_ = new long[]{0, INITIAL, INITIAL};
    
    /** 発生した例外の個数 */
    private int throwableCount_ = 0;

    /** 検出したストールの個数 */
    private int methodStallCount_ = 0;

    /** HTTPステータスの個数 */
    private int httpStatusCount_ = 0;
    
    /** 呼び出し元のSet */
    private final Map<Invocation, Invocation> callerSet_ =
            new ConcurrentHashMap<Invocation, Invocation>();

    /** メソッドの最終実行時間 */
    private long accumulatedTime_;

    /** accumulatedTime_の最大値。 {@link #setAccumulatedTime}の中でaccumulatedTime_と共に更新判定を行う。　*/
    private long maxAccumulatedTime_;

    /** 呼び出し情報を赤くブリンクする際の閾値。 値（ミリ秒）を下回る処理時間の呼び出し情報は赤くブリンクしない。 */
    private long alarmThreshold_;

    /** 警告を発生させるCPU時間の閾値 */
    private long alarmCpuThreshold_ = THRESHOLD_NOT_SPECIFIED;

    /** プロセス名 */
    private final String processName_;

    /** ハッシュコード。 */
    private int code_ = 0;

    /** 最終更新時刻 */
    private long lastUpdatedTime_;

    /** 最終アラーム発生時刻 */
    private long lastAlarmTime_;

    /** 追加で値を保存する場合に利用する。 */
    private Map<String, Object> optValueMap_;

    /** Invocationがrootかどうか(true:root、false:not root。デフォルト値はfalse) */
    private boolean isRoot_ = false;

    /** 計測対象か否か */
    private TripleState measurementTarget_ = TripleState.NOT_SPECIFIED;

    /** レスポンスグラフを出力するかどうか */
    private TripleState responseGraphOutput_ = TripleState.NOT_SPECIFIED;

    /** AccumulatedTimeリセットの最終時刻 */
    private long lastResetAccumulatedTime_;

    /** メソッド呼び出し回数が 0 である状態がスタートした時刻。 */
    private long tatCallZeroValueStartTime_;

    /** TATの総和。rootのInvocationのみ有効値が設定される */
    private long accumulatedTimeSum_;

    /** TATの最大値。rootのInvocationのみ有効値が設定される */
    private long accumulatedMax_;

    /** TATの最小値。rootのInvocationのみ有効値が設定される */
    private long accumulatedMin_;

    /** ルートとして呼び出された回数。rootのInvocationのみ有効値が設定される */
    private int accumulatedTimeCount_;

    /** Turn Around Timeを計測するかどうかtrue:計測する、false:計測しない。デフォルト値はtrue) */
    private boolean isTatEnabled_ = true;

    /** Turn Around Timeの保持期間 */
    private long tatKeepTime_;

    /** InvocationがJavaクラスに対して設定されたものかどうか */
    private boolean isJavaClass_ = false;

    /** 起動後、メソッドが呼び出されたか */
    private transient boolean isCalledAfterStarted_ = false;

    private boolean isExcludePreffered_;

    private boolean isTargetPreferred_;

    private boolean isExclude_;

    private boolean isTarget_;

    private Map<String, Integer> throwableCountMap_;
    
    private Map<String, Integer> httpStatusCountMap_;
    
    /**
     * コンストラクタ
     * @param processName プロセス名
     * @param className クラス名
     * @param methodName メソッド名
     * @param alarmThreshold アラーム発生のためのメソッド実行時間の閾値
     */
    public Invocation(
            final String processName, 
            final String className, 
            final String methodName, 
            final long   alarmThreshold)
    {
        this.processName_    = processName;
        this.className_      = className;
        this.methodName_     = methodName;
        this.alarmThreshold_ = alarmThreshold;
        this.isJavaClass_    = judgeIsJavaClass(className, methodName);

        this.key_  = className + "#" + methodName;
        this.code_ = key_.hashCode();

        if (methodName.startsWith("/"))
        {
            if ("/".equals(className))
            {
                // クラス名が "/" のみの場合、クラス名とメソッド名をつなげると "//path" というように
                // "/" が連続するため、クラス名を除いたものを識別文字列とする
                this.rootInvocationManagerKey_ = methodName;
            }
            else
            {
                this.rootInvocationManagerKey_ = className + methodName;
            }
        }
        else
        {
            this.rootInvocationManagerKey_ = this.key_;
        }
        
        this.lastAlarmTime_   = 0;
        this.lastUpdatedTime_ = System.currentTimeMillis();
        this.tatCallZeroValueStartTime_ = TAT_ZERO_KEEP_TIME_NULL_VALUE;
        this.isTarget_ = true;
        this.isExclude_ = false;
        this.isTargetPreferred_ = false;
        this.isExcludePreffered_ = false;
        this.throwableCountMap_ = new HashMap<String, Integer>(5);
        this.httpStatusCountMap_ = new HashMap<String, Integer>(5);
    }

    /**
     * クラス名を取得する。
     * @return クラス名
     */
    public String getClassName()
    {
        return this.className_;
    }

    /**
     * メソッド名を取得する。
     * @return メソッド名
     */
    public String getMethodName()
    {
        return this.methodName_;
    }

    /**
     * キー情報を取得する。
     * @return キー情報
     */
    public String getKey()
    {
        return this.key_;
    }

    /**
     * RootInvocationManagerで使用するキー情報を取得する。
     * @return キー情報
     */
    public String getRootInvocationManagerKey()
    {
        return rootInvocationManagerKey_;
    }

    /**
     * メソッド呼び出し回数を取得する。
     * @return メソッド呼び出し回数
     */
    public long getCount()
    {
        return this.count_;
    }

    /**
     * InvocationIntervalの各要素の最小値を保存している{@link InvocationInterval}
     * @return InvocationInterval
     */
    public long getMinimum()
    {
        return this.intervals_[MIN];
    }

    /**
     * InvocationIntervalの各要素の最大値を保存している{@link InvocationInterval}
     * @return InvocationInterval
     */
    public long getMaximum()
    {
        return this.intervals_[MAX];
    }

    /**
     * メソッド実行時間の平均値を取得する。
     * @return メソッド実行時間の平均値
     */
    public long getAverage()
    {
        // 0除算を避ける。
        if (this.count_ == 0)
        {
            return 0;
        }

        return this.intervals_[SUM] / this.count_;
    }

    /**
     * メソッド実行時間の合計値を取得します。<br />
     *
     * @return メソッド実行時間の合計値
     */
    public long getTotal()
    {
        return this.intervals_[SUM];
    }

    /**
     * メソッドのCPU時間の最小値を取得する。
     * @return メソッドのCPU時間の最小値
     */
    public long getCpuMinimum()
    {
        return this.cpuIntervals_[MIN];
    }

    /**
     * メソッドのCPU時間の最大値を取得する。
     * @return メソッドのCPU時間の最大値
     */
    public long getCpuMaximum()
    {
        return this.cpuIntervals_[MAX];
    }

    /**
     * メソッドのCPU時間の平均値を取得する。
     * @return メソッドのCPU時間の平均値
     */
    public long getCpuAverage()
    {
        // 0除算を避ける。
        if (this.count_ == 0)
        {
            return 0;
        }
        
        return this.cpuIntervals_[SUM] / this.count_;
    }

    /**
     * メソッドの CPU 時間の合計値を取得します。<br />
     *
     * @return メソッドの CPU 時間の合計値
     */
    public long getCpuTotal()
    {
        return this.cpuIntervals_[SUM];
    }

    /**
     * メソッドのユーザ時間の最小値を取得する。
     * @return メソッドのユーザ時間の最小値
     */
    public long getUserMinimum()
    {
        return this.userIntervals_[MIN];
    }

    /**
     * メソッドのユーザ時間の最大値を取得する。
     * @return メソッドのユーザ時間の最大値
     */
    public long getUserMaximum()
    {
        return this.userIntervals_[MAX];
    }

    /**
     * メソッドのユーザ時間の平均値を取得する。
     * @return メソッドのユーザ時間の平均値
     */
    public long getUserAverage()
    {
        // 0除算を避ける。
        if (this.count_ == 0)
        {
            return 0;
        }

        return this.userIntervals_[SUM] / this.count_;
    }

    /**
     * メソッドのユーザ時間の合計値を取得します。<br />
     *
     * @return メソッドのユーザ時間の合計値
     */
    public long getUserTotal()
    {
        return this.userIntervals_[SUM];
    }

    /**
     * InvocationIntervalの各要素の最小値を保存している{@link InvocationInterval}
     * @return InvocationInterval
     */
    public long getAccumulatedMinimum()
    {
        return this.accumulatedIntervals_[MIN];
    }

    /**
     * InvocationIntervalの各要素の最大値を保存している{@link InvocationInterval}
     * @return InvocationInterval
     */
    public long getAccumulatedMaximum()
    {
        return this.accumulatedIntervals_[MAX];
    }

    /**
     * メソッド実行時間の平均値を取得する。
     * @return メソッド実行時間の平均値
     */
    public long getAccumulatedAverage()
    {
        // 0除算を避ける。
        if (this.count_ == 0)
        {
            return 0;
        }

        return this.accumulatedIntervals_[SUM] / this.count_;
    }

    /**
     * メソッド実行時間の合計値を取得します。<br />
     *
     * @return メソッド実行時間の合計値
     */
    public long getAccumulatedTotal()
    {
        return this.accumulatedIntervals_[SUM];
    }

    /**
     * メソッドのCPU時間の最小値を取得する。
     * @return メソッドのCPU時間の最小値
     */
    public long getAccumulatedCpuMinimum()
    {
        return this.accumulatedCpuIntervals_[MIN];
    }

    /**
     * メソッドのCPU時間の最大値を取得する。
     * @return メソッドのCPU時間の最大値
     */
    public long getAccumulatedCpuMaximum()
    {
        return this.accumulatedCpuIntervals_[MAX];
    }

    /**
     * メソッドのCPU時間の平均値を取得する。
     * @return メソッドのCPU時間の平均値
     */
    public long getAccumulatedCpuAverage()
    {
        // 0除算を避ける。
        if (this.count_ == 0)
        {
            return 0;
        }

        return this.accumulatedCpuIntervals_[SUM] / this.count_;
    }

    /**
     * メソッドの CPU 時間の合計値を取得します。<br />
     *
     * @return メソッドの CPU 時間の合計値
     */
    public long getAccumulatedCpuTotal()
    {
        return this.accumulatedCpuIntervals_[SUM];
    }

    /**
     * メソッドのユーザ時間の最小値を取得する。
     * @return メソッドのユーザ時間の最小値
     */
    public long getAccumulatedUserMinimum()
    {
        return this.accumulatedUserIntervals_[MIN];
    }

    /**
     * メソッドのユーザ時間の最大値を取得する。
     * @return メソッドのユーザ時間の最大値
     */
    public long getAccumulatedUserMaximum()
    {
        return this.accumulatedUserIntervals_[MAX];
    }

    /**
     * メソッドのユーザ時間の平均値を取得する。
     * @return メソッドのユーザ時間の平均値
     */
    public long getAccumulatedUserAverage()
    {
        // 0除算を避ける。
        if (this.count_ == 0)
        {
            return 0;
        }
        
        return this.accumulatedUserIntervals_[SUM] / this.count_;
    }

    /**
     * メソッドのユーザ時間の合計値を取得します。<br />
     *
     * @return メソッドのユーザ時間の合計値
     */
    public long getAccumulatedUserTotal()
    {
        return this.accumulatedUserIntervals_[SUM];
    }
    
    /**
     * 例外の発生回数を返す。
     * @return 例外の発生回数
     */
    public long getThrowableCount()
    {
        return this.throwableCount_;
    }

    /**
     * ストールの発生回数を返す。
     * @return ストールの発生回数
     */
    public long getMethodStallCount()
    {
        return this.methodStallCount_;
    }
    
    /**
     * ストールの発生回数を加算する。
     * @param count ストールの発生回数
     */
    public synchronized void addMethodStallCount(long count)
    {
        this.methodStallCount_ += count; 
    }

    /**
     * メソッドの呼び出し元を配列として取得する。<br />
     * 形式： クラス名#メソッド名
     * @return メソッドの呼び出し元の配列
     */
    public synchronized String[] getAllCallerName()
    {
        Invocation[] invocations = 
            callerSet_.keySet().toArray(new Invocation[callerSet_.size()]);
        String[] objNames = new String[invocations.length];

        for (int index = 0; index < invocations.length; index++)
        {
            objNames[index] =
                    invocations[index].getClassName() 
                    + "#" + invocations[index].getMethodName();
        }
        return objNames;
    }

    /**
     * Invocationが保存している全てのメソッドの呼び出し元に対応するInvocationを返す。
     * @return 全てのメソッドの呼び出し元に対応するInvocation
     */
    public synchronized Invocation[] getAllCallerInvocation()
    {
        Invocation[] invocations = 
            callerSet_.keySet().toArray(new Invocation[callerSet_.size()]);
        return invocations;
    }

    /**
     * 保存するInvocationIntervalの情報を更新する。<br />
     * 併せて、起動後初めて呼び出され、かつJavaクラスだった場合は呼び出されたことをカウンタに反映する。<br />
     * 更新対象:
     * <ul>
     * 　　<li>count_:メソッド呼び出し回数</li>
     * 　　<li>intervalSum_:InvocationIntervalの各要素の合計</li>
     * 　　<li>intervalList_:Invocationが保存するInvocationのリスト</li>
     * 　　<li>intervalMax_:InvocationInteralの各要素の最大値</li>
     * 　　<li>minimumInterval_:InvocationIntervalの各要素の最小値</li>
     * </ul>
     * @param interval {@link InvocationInterval}オブジェクト
     */
    public synchronized void addInterval(
            final CallTreeNode node, 
            final long interval,
            final long cpuInterval,
            final long userInterval)
    {
        if (this.isCalledAfterStarted_ == false && this.isJavaClass_ == true)
        {
            this.isCalledAfterStarted_ = true;
            CalledMethodCounter.incrementCounter();
        }

        intervals_[SUM]     += interval;
        cpuIntervals_[SUM]  += cpuInterval;
        userIntervals_[SUM] += userInterval;

        if (intervals_[MAX] < interval)
        {
            intervals_[MAX] = interval;
        }
        if (cpuIntervals_[MAX] < cpuInterval)
        {
            cpuIntervals_[MAX] = cpuInterval;
        }
        if (userIntervals_[MAX] < userInterval)
        {
            userIntervals_[MAX] = userInterval;
        }
        
        if (intervals_[MIN] == INITIAL || intervals_[MIN] > interval)
        {
            intervals_[MIN] = interval;
        }
        if (cpuIntervals_[MIN] == INITIAL || cpuIntervals_[MIN] > cpuInterval)
        {
            cpuIntervals_[MIN] = cpuInterval;
        }
        if (userIntervals_[MIN] == INITIAL || userIntervals_[MIN] > userInterval)
        {
            userIntervals_[MIN] = userInterval;
        }
        
        long accumulatedTime = node.getAccumulatedTime();
        long cpuTime         = node.getCpuTime();
        long userTime        = node.getUserTime();
        
        accumulatedIntervals_[SUM]     += accumulatedTime;
        accumulatedCpuIntervals_[SUM]  += cpuTime;
        accumulatedUserIntervals_[SUM] += userTime;
        
        if (accumulatedIntervals_[MAX] < accumulatedTime)
        {
            accumulatedIntervals_[MAX] = accumulatedTime;
        }
        if (accumulatedCpuIntervals_[MAX] < cpuTime)
        {
            accumulatedCpuIntervals_[MAX] = cpuTime;
        }
        if (accumulatedUserIntervals_[MAX] < userTime)
        {
            accumulatedUserIntervals_[MAX] = userTime;
        }
        
        if (accumulatedIntervals_[MIN] == INITIAL || accumulatedIntervals_[MIN] > accumulatedTime)
        {
            accumulatedIntervals_[MIN] = accumulatedTime;
        }
        if (accumulatedCpuIntervals_[MIN] == INITIAL || accumulatedCpuIntervals_[MIN] > cpuTime)
        {
            accumulatedCpuIntervals_[MIN] = cpuTime;
        }
        if (accumulatedUserIntervals_[MIN] == INITIAL || accumulatedUserIntervals_[MIN] > userTime)
        {
            accumulatedUserIntervals_[MIN] = userTime;
        }
        
        this.count_++;
        this.countFromStartup_++;
        
        updateLastUpdatedTime();
    }

    /**
     * Invocationが保存するメソッドの呼び出し元の更新を行う。
     * @param caller メソッドの呼び出し元の{@link Invocation}オブジェクト
     */
    public void addCaller(final Invocation caller)
    {
        if (caller != null)
        {
            this.callerSet_.put(caller, caller);
        }
        updateLastUpdatedTime();
    }

    /**
     * Invocationが保存する例外の更新を行う。
     * @param throwable 新規で発生した{@link Throwable}オブジェクト
     */
    public synchronized void addThrowable(final Throwable throwable)
    {
        String name = throwable.getClass().getName();
        Integer count = this.throwableCountMap_.get(name);
        if(count == null)
        {
            count = Integer.valueOf(1);
        }
        else
        {
            count = Integer.valueOf(count.intValue() + 1);
        }
        this.throwableCountMap_.put(name, count);
        
        this.throwableCount_++;
        updateLastUpdatedTime();
    }

    /**
     * アラーム発生判定のTATの閾値を返す。
     * @return アラーム発生判定のTATの閾値（個別指定されていない場合は -1 ）
     */
    public long getAlarmThreshold()
    {
        return this.alarmThreshold_;
    }

    /**
     * アラーム発生判定のTATの閾値を設定する。
     * @param alarmThreshold アラーム発生判定のTATの閾値
     */
    public void setAlarmThreshold(final long alarmThreshold)
    {
        this.alarmThreshold_ = alarmThreshold;
        updateLastUpdatedTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer(DEF_BUFFER_SIZE);
        buffer.append(processName_);
        buffer.append(":");
        buffer.append(className_);
        buffer.append("#");
        buffer.append(methodName_);
        buffer.append(",");
        buffer.append(getCount());
        buffer.append(",");
        buffer.append(getMinimum());
        buffer.append(",");
        buffer.append(getMaximum());
        buffer.append(",");
        buffer.append(getAverage());
        buffer.append(",");
        buffer.append(getThrowableCount());
        return buffer.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object target)
    {
        if (!(target instanceof Invocation))
        {
            return false;
        }

        Invocation invocation = (Invocation)target;
        
//        if (!this.className_.equals(invocation.getClassName())
//                || !this.methodName_.equals(invocation.getMethodName()))
        if (!this.key_.equals(invocation.getKey()))
        {
            return false;
        }
        
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return this.code_;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void reset()
    {
        count_ = 0;
        this.throwableCount_ = 0;
        this.methodStallCount_ = 0;
        this.httpStatusCount_ = 0;
        this.isCalledAfterStarted_ = false;

        intervals_[SUM] = 0;
        intervals_[MAX] = INITIAL;
        intervals_[MIN] = INITIAL;
        cpuIntervals_[SUM] = 0;
        cpuIntervals_[MAX] = INITIAL;
        cpuIntervals_[MIN] = INITIAL;
        userIntervals_[SUM] = 0;
        userIntervals_[MAX] = INITIAL;
        userIntervals_[MIN] = INITIAL;
        accumulatedIntervals_[SUM] = 0;
        accumulatedIntervals_[MAX] = INITIAL;
        accumulatedIntervals_[MIN] = INITIAL;
        accumulatedCpuIntervals_[SUM] = 0;
        accumulatedCpuIntervals_[MAX] = INITIAL;
        accumulatedCpuIntervals_[MIN] = INITIAL;
        accumulatedUserIntervals_[SUM] = 0;
        accumulatedUserIntervals_[MAX] = INITIAL;
        accumulatedUserIntervals_[MIN] = INITIAL;

        updateLastUpdatedTime();
    }

    /**
     * プロセス名を取得する。
     * @return プロセス名
     */
    public String getProcessName()
    {
        return processName_;
    }

    /**
     * メソッドの最終実行時間を取得する。
     * @return メソッドの最終実行時間
     */
    public long getAccumulatedTime()
    {
        return accumulatedTime_;
    }

    /**
     * メソッドの最終実行時間を設定し、今までのメソッド実行時間の最大値を超えている場合は、最大値を更新する。
     * @param accumulatedTime メソッドの最終実行時間
     */
    public void setAccumulatedTime(final long accumulatedTime)
    {
        this.accumulatedTime_ = accumulatedTime;
        if (this.accumulatedTime_ > this.maxAccumulatedTime_)
        {
            this.maxAccumulatedTime_ = this.accumulatedTime_;
        }
        
        updateLastUpdatedTime();
        // TAT計測対象であり、かつ、TATを計測する設定の場合、
        // AccumulatedTimeの総和と設定回数を更新する
        if (isResponseGraphOutputTarget() && this.isTatEnabled_ == true)
        {
            if (this.lastResetAccumulatedTime_ != 0
                    && this.lastUpdatedTime_ - this.lastResetAccumulatedTime_ > this.tatKeepTime_)
            {
                // TAT保持期間より古いデータは消去する
                this.accumulatedTimeSum_ = accumulatedTime;
                this.accumulatedMin_ = accumulatedTime;
                this.accumulatedMax_ = accumulatedTime;
                this.accumulatedTimeCount_ = 1;
            }
            else
            {
                // AccumulatedTimeの総和と設定回数を更新する
                if (accumulatedTime < this.accumulatedMin_ || this.accumulatedTimeCount_ == 0)
                {
                    this.accumulatedMin_ = accumulatedTime;
                }
                if (accumulatedTime > this.accumulatedMax_)
                {
                    this.accumulatedMax_ = accumulatedTime;
                }
                this.accumulatedTimeSum_ += accumulatedTime;
                this.accumulatedTimeCount_++;
            }
            this.lastResetAccumulatedTime_ = this.lastUpdatedTime_;
        }
    }

    /**
     * メソッドの実行時間の最大値を取得する。
     * @return メソッドの実行時間の最大値
     */
    public long getMaxAccumulatedTime()
    {
        return this.maxAccumulatedTime_;
    }

    /**
     * メソッド実行時間の最大値を更新した回数を取得する。
     * @return メソッド実行時間の最大値を更新した回数
     */
    public long getCountFromStartup()
    {
        return this.countFromStartup_;
    }

    /**
     * 警告を発生させるCPU時間の閾値を取得する
     * @return CPU時間の閾値
     */
    public long getAlarmCpuThreshold()
    {
        return this.alarmCpuThreshold_;
    }

    /**
     * 警告を発生させるCPU時間の閾値を設定する
     * @param alarmCpuThreshold CPU時間の閾値
     */
    public void setAlarmCpuThreshold(final long alarmCpuThreshold)
    {
        this.alarmCpuThreshold_ = alarmCpuThreshold;
        updateLastUpdatedTime();
    }

    /**
     * 最終更新時刻を取得する。
     * @return 最終更新時刻
     */
    public long getLastUpdatedTime()
    {
        return this.lastUpdatedTime_;
    }

    /**
     * 最終更新時刻を更新する。
     */
    private void updateLastUpdatedTime()
    {
        this.lastUpdatedTime_ = System.currentTimeMillis();
    }

    /**
     * 最終アラーム発生時刻を取得する。
     * @return 最終アラーム発生時刻
     */
    public long getLastAlarmTime()
    {
        return this.lastAlarmTime_;
    }

    /**
     * 最終アラーム発生時刻を更新する。
     * @param lastAlarmTime 最終アラーム発生時刻
     */
    public void setLastAlarmTime(final long lastAlarmTime)
    {
        this.lastAlarmTime_ = lastAlarmTime;
    }

    /**
     * 追加で保存する値を更新する。
     * @param key 更新する値に対応するキー
     * @param value 更新する値
     */
    public synchronized void putOptValue(String key, Object value)
    {
        if(this.optValueMap_ == null)
        {
            this.optValueMap_ = new ConcurrentHashMap<String, Object>();
        }
        
        this.optValueMap_.put(key, value);
    }

    /**
     * keyに対応する値をInvocationから取得する。
     * @param key キー
     * @return Invocationが保存するkeyに対応する値
     */
    public synchronized Object getOptValue(String key)
    {
        if(this.optValueMap_ == null)
        {
            this.optValueMap_ = new ConcurrentHashMap<String, Object>();
        }
        
        return this.optValueMap_.get(key);
    }

    /**
     * Invocationがrootかどうかを表す値を返す。
     * @return true:root、false:not root
     */
    public boolean isRoot()
    {
        return this.isRoot_;
    }

    /**
     * Invocationがrootかどうかを表す値を設定する。
     * @param isRoot Invocationがrootかどうかを表す値
     */
    public void setRoot(final boolean isRoot)
    {
        this.isRoot_ = isRoot;
    }

    /**
     * トランザクショングラフ出力対象設定を返します。<br />
     *
     * @return {@link TripleState} の値
     */
    public TripleState getTransactionGraphOutput()
    {
        return this.responseGraphOutput_;
    }

    /**
     * 計測対象か否かをセットします。<br />
     *
     * @param state 計測対象の場合は <code>ON</code> 、計測対象でない場合は <code>OFF</code> 、
     *              指定されていない場合は <code>NOT_SPECIFIED</code>
     */
    public void setMeasurementTarget(TripleState state)
    {
        this.measurementTarget_ = state;
    }

    /**
     * 計測対象か否かを返します。<br />
     *
     * @return 計測対象の場合は <code>ON</code> 、計測対象でない場合は <code>OFF</code> 、
     *         指定されていない場合は <code>NOT_SPECIFIED</code>
     */
    public TripleState getMeasurementTarget()
    {
        return this.measurementTarget_;
    }

    /**
     * トランザクショングラフ出力対象設定をセットします。<br />
     *
     * @param output {@link TripleState} の値
     */
    public void setResponseGraphOutput(TripleState output)
    {
        this.responseGraphOutput_ = output;
    }

    /**
     * トランザクショングラフ出力対象か否かを返します。<br />
     *
     * @return トランザクショングラフ出力対象の場合は <code>true</code>
     */
    public boolean isResponseGraphOutputTarget()
    {
        TripleState output = getTransactionGraphOutput();
        return (output == TripleState.ON || (output == TripleState.NOT_SPECIFIED && isRoot()));
    }

    /**
     * 本クラスが持つ、AccumulatedTimeの総和と設定回数をリセットし、
     * 前回リセットからの、AccumulatedTimeの平均値を返す。<br />
     * 前回リセットから {@link #setAccumulatedTime} が呼び出されていない場合は0を返す。
     *
     * @return 前回リセットから今回までの TAT 情報
     */
    public synchronized TurnAroundTimeInfo resetAccumulatedTimeCount()
    {
        this.lastResetAccumulatedTime_ = System.currentTimeMillis();

        if (this.accumulatedTimeCount_ == 0)
        {
            if (this.tatCallZeroValueStartTime_ == TAT_ZERO_KEEP_TIME_NULL_VALUE)
            {
                // 呼び出し回数の値が 0 である状態が開始した時刻をセットする
                this.tatCallZeroValueStartTime_ = this.lastResetAccumulatedTime_;
            }
            this.accumulatedTimeSum_ = 0;
            this.accumulatedMin_ = 0;
            this.resetThrowableCountMap(false);
            this.resetHttpStatusCountMap(false);
            TurnAroundTimeInfo retVal = new TurnAroundTimeInfo(this.accumulatedTimeSum_, this.accumulatedMax_,
                                          this.accumulatedMin_, this.accumulatedTimeCount_,
                                          this.throwableCountMap_, this.httpStatusCountMap_,
                                          this.methodStallCount_);
            this.throwableCountMap_ = new HashMap<String, Integer>(5);
            this.httpStatusCountMap_ = new HashMap<String, Integer>(5);
            this.methodStallCount_ = 0;
            return retVal;
        }

        this.tatCallZeroValueStartTime_ = TAT_ZERO_KEEP_TIME_NULL_VALUE;

        TurnAroundTimeInfo retVal =
                new TurnAroundTimeInfo(accumulatedTimeSum_, this.accumulatedMax_,
                                       this.accumulatedMin_, accumulatedTimeCount_,
                                       this.throwableCountMap_, this.httpStatusCountMap_,
                                       this.methodStallCount_);
        this.accumulatedTimeSum_ = 0;
        this.accumulatedTimeCount_ = 0;
        this.accumulatedMax_ = 0;
        this.accumulatedMin_ = Long.MAX_VALUE;
        this.resetThrowableCountMap(true);
        this.resetHttpStatusCountMap(true);
        this.methodStallCount_ = 0;
        return retVal;
    }
    
    /**
     * 本クラスが持つThrowableCountMapをリセットします。<br />
     * 値が1以上の要素は値を0にし、値が0の要素はそのまま残すかMapから取り除きます。<br />
     * 
     * @param removeZeroCountData countが0のデータをMapから取り除くかどうか
     */
    private synchronized void resetThrowableCountMap(boolean removeZeroCountData)
    {
        Map<String, Integer> newMap = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> orgEntry : this.throwableCountMap_.entrySet())
        {
            String name = orgEntry.getKey();
            Integer count = orgEntry.getValue();
            if (removeZeroCountData == false || 0 < count)
            {
                newMap.put(name, 0);
            }
        }
        this.throwableCountMap_ = newMap;
    }

    /**
     * Turn Around Timeを計測するかどうかを設定する。
     * @param tatEnabled Turn Around Timeを計測するならtrue
     */
    public void setTatEnabled(final boolean tatEnabled)
    {
        this.isTatEnabled_ = tatEnabled;
    }

    /**
     * Turn Around Timeの保持期間をセットする。
     * @param tatKeepTime Turn Around Timeの保持期間
     */
    public void setTatKeepTime(final long tatKeepTime)
    {
        this.tatKeepTime_ = tatKeepTime;
    }

    /**
     * メソッド呼び出し回数が 0 である状態がスタートした時刻を返します。<br />
     *
     * @return メソッド呼び出し回数が 0 である状態がスタートした時刻
     */
    public long getTatCallZeroValueStartTime()
    {
        return this.tatCallZeroValueStartTime_;
    }

    /**
     * Turn Around Timeの最大値を返します。<br />
     * 
     * @return Turn Around Timeの最大値
     */
    public long getAccumulatedMax()
    {
        return this.accumulatedMax_;
    }

    /**
     * Turn Around Timeの最小値を返します。<br />
     * 
     * @return Turn Around Timeの最小値
     */
    public long getAccumulatedMin()
    {
        return this.accumulatedMin_;
    }

    /**
     * Invocationクラスのクラス名称、メソッド名称を基にJavaクラスかどうかを判定する。
     * @param className クラス名称
     * @param methodName メソッド名称
     * @return true Javaクラスだった場合
     *         false Javaクラスではない場合
     */
    private static boolean judgeIsJavaClass(final String className, final String methodName)
    {
        //イベント発報用に生成されたInvocationを対象から除外
        if (className == null || className.length() == 0 || methodName == null
                || methodName.length() == 0)
        {
            return false;
        }

        // S2JavelinFilter/HttpServletMonitor、JrubyConverter、
        // Event出力時に生成されたInvocationを対象から除外
        if (className.startsWith("/") == true
                || EventConstants.EVENT_CLASSNAME.equals(className) == true)
        {
            return false;
        }

        // JDBCJavelinによる変換を除外
        if (className.startsWith("jdbc:"))
        {
            return false;
        }

        //Javelinログ要素作成用Invocationと、Event送信用Invocation、
        //S2JavelinFilter/HttpServletMonitorに作成されたInvocationを除外
        if (JavelinLogConstants.DEFAULT_LOGMETHOD.equals(methodName) == true
                || methodName.startsWith("/") == true
                || EventConstants.EVENT_METHODNAME.equals(className) == true)
        {
            return false;
        }

        return true;
    }

    public void setExcludePreffered(boolean isExcludePreffered)
    {
        this.isExcludePreffered_ = isExcludePreffered;
    }

    public boolean isExcludePreffered()
    {
        return isExcludePreffered_;
    }

    public boolean isTargetPreferred()
    {
        return this.isTargetPreferred_;
    }

    public void setTargetPreferred(boolean isTargetPreferred)
    {
        isTargetPreferred_ = isTargetPreferred;
    }

    public void setExclude(boolean isExclude)
    {
        this.isExclude_ = isExclude;
    }

    public boolean isExclude()
    {
        return isExclude_;
    }

    public void setTarget(boolean isTarget)
    {
        this.isTarget_ = isTarget;
    }

    public boolean isTarget()
    {
        return isTarget_;
    }

    /**
     * 引数で指定したHTTPステータスに対してカウントをします。
     * @param httpStatus HTTPステータス
     */
    public synchronized void addHttpStatusCount(final String httpStatus)
    {
        Integer count = this.throwableCountMap_.get(httpStatus);
        if(count == null)
        {
            count = Integer.valueOf(1);
        }
        else
        {
            count = Integer.valueOf(count.intValue() + 1);
        }
        this.throwableCountMap_.put(httpStatus, count);
        
        this.throwableCount_++;
        updateLastUpdatedTime();
/*        
        
        Integer count = this.httpStatusCountMap_.get(httpStatus);
        if(count == null)
        {
            count = Integer.valueOf(1);
        }
        else
        {
            count = Integer.valueOf(count.intValue() + 1);
        }
        this.httpStatusCountMap_.put(httpStatus, count);
        
        updateLastUpdatedTime();
        */
    }

    
    /**
     * 本クラスが持つHttpStatusCountMapをリセットします。<br />
     * 値が1以上の要素は値を0にし、値が0の要素はそのまま残すかMapから取り除きます。<br />
     * 
     * @param removeZeroCountData countが0のデータをMapから取り除くかどうか
     */
    private synchronized void resetHttpStatusCountMap(boolean removeZeroCountData)
    {
        Map<String, Integer> newMap = new HashMap<String, Integer>();
        if (this.httpStatusCountMap_ != null)
        {
            for (Map.Entry<String, Integer> orgEntry : this.httpStatusCountMap_.entrySet())
            {
                String name = orgEntry.getKey();
                Integer count = orgEntry.getValue();
                if (removeZeroCountData == false || 0 < count)
                {
                    newMap.put(name, 0);
                }
            }
        }
        this.httpStatusCountMap_ = newMap;
    }
}
