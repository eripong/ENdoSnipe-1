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
package jp.co.acroquest.endosnipe.javelin.resource.proc;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.config.JavelinConfigUtil;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;

/**
 * @author ochiai
 *
 */
public class PerfCounter
{
    /** CPU使用率取得のための文字列 */
    public static final String PROCESSOR_TOTAL_PROCESSOR_TIME =
        "\\\\Processor(_Total)\\\\% Processor Time";
    
    /** CPU使用率（システム）取得のための文字列 */
    public static final String PROCESSOR_TOTAL_PRIVILEGED_TIME =
        "\\\\Processor(_Total)\\\\% Privileged Time";
    
    /** CPU使用率（ユーザ）取得のための文字列 */
    public static final String PROCESSOR_TOTAL_USER_TIME =
        "\\\\Processor(_Total)\\\\% User Time";
    
    /** 物理メモリ（最大）取得のための文字列 */
    public static final String MEMORY_TOTAL =
        "Memory Total";
    
    /** 物理メモリ（空き）取得のための文字列 */
    public static final String MEMORY_AVAILABLE_BYTES =
        "\\\\Memory\\\\Available Bytes";
    
    /** ページファイル使用率取得のための文字列（） */
    public static final String PAGING_FILE_USAGE =
        "\\\\PAGING FILE\\\\% USAGE";
    
    /** ページファイル使用量取得のための文字列 */
    public static final String PROCESS_TOTAL_PAGE_FILE_BYTES =
        "\\\\Processor(_Total)\\\\Page File Bytes";
    
    /** 仮想メモリ使用量取得のための文字列 */
    public static final String PROCESS_TOTAL_VIRTUAL_BYTES =
        "\\\\Process(_Total)\\\\Virtual Bytes";
    
    /** システム全体のハンドル使用数取得のための文字列 */
    public static final String PROCESS_TOTAL_NUMBER_FDS =
        "\\\\Process(_Total)\\\\Handle Count";
    
    /** ページイン取得のための文字列 */
    public static final String MEMORY_PAGES_INPUT_SEC =
        "\\\\Memory\\\\Pages Input/sec";
    
    /** ページアウト取得のための文字列 */
    public static final String MEMORY_PAGES_OUTPUT_SEC =
        "\\\\Memory\\\\Pages Output/sec";
    
    /** % User Time取得のための文字列 */
    public static final String PROCESS_USER_TIME =
        "\\\\Process(xxx)\\\\% User Time";
    
    /** % Privileged Time取得のための文字列 */
    public static final String PROCESS_PRIVILEGED_TIME =
        "\\\\Processor(xxx)\\\\% Privileged Time";
    
    /** メジャーフォールト取得のための文字列 */
    public static final String PROCESS_PAGE_FAULTS_SEC =
        "\\\\Process(xxx)\\\\Page Faults/sec";
    
    /** vsize取得のための文字列 */
    public static final String PROCESS_VIRTUAL_BYTES =
        "\\\\Process(xxx)\\\\Virtual Bytes";
    
    /** rss取得のための文字列 */
    public static final String PROCESS_WORKING_SET =
        "\\\\Process(xxx)\\\\Working Set";
    
    /** スレッド数取得のための文字列 */
    public static final String PROCESS_THREAD_COUNT =
        "\\\\Process(xxx)\\\\Thread Count";
    
    /** プロセスのハンドル使用数取得のための文字列 */
    public static final String PROCESS_NUMBER_FDS =
        "\\\\Process(xxx)\\\\Handle Count";

    /** 計測間隔（実際の値） */
    public static final String INTERVAL = "Interval";

    /** ミリ秒から秒への変換 */
    private static final double MILLI_SECONDS_TO_SECONDS = 1000.0;
    
    /** 前回の計測時刻（long値 ミリ秒） */
    private long lastMeasuredTime_;

    private Map<String, Double> prevResourceMap_;

    // dll ファイルをロードする
    static {
        SystemLogger logger = SystemLogger.getInstance();

        // ライブラリをロードします
        JavelinConfigUtil javelinConfigUtil = JavelinConfigUtil.getInstance();
        
        // CPU bit数
        String bit = System.getProperty("sun.arch.data.model");
        if (bit == null || bit.length() == 0)
        {
            logger.warn("you have to set \"sun.arch.data.model\" system properties.");
            bit = "";
        }
        
        String libPath = "./PerfCounter_" +  bit + ".dll";
        
        libPath = javelinConfigUtil.convertRelPathFromJartoAbsPath(libPath);

        if (logger.isDebugEnabled())
        {
            logger.debug("loading dll for read performance counter : " + libPath);
        }
        
        try 
        {
            System.load(libPath);
        }
        catch (SecurityException se)
        {
            logger.error("Can't load dll library : " + libPath, se);
        }
        catch (UnsatisfiedLinkError ule)
        {
            logger.error("Can't load dll library : " + libPath, ule);
        }
    }
    
    /**
     * 新規クエリーを作成
     * @return 作成に成功したら true
     */
    private native boolean openQuery();

    /**
     * クエリーを追加
     * @return 追加に成功したら true
     */
    private native boolean addCounter(String counterPath);

    /**
     * ハンドルを更新します。
     *
     * @return ハンドルを更新した場合は <code>true</code> 、更新しなかった場合は <code>false</code>
     */
    private native boolean updateHandles();

    /**
     * 計測
     * @return 計測に成功したらtrue
     */
    private native boolean collectQueryData();

    /**
     * システムのCPU使用率（System）を取得
     * @return 取得した値
     */
    private native double getFormattedCounterValueSysCPUSys();

    /**
     * システムのCPU使用率（User）を取得
     * @return 取得した値
     */
    private native double getFormattedCounterValueSysCPUUser();

    /**
     * 物理メモリ（最大）を取得
     * @return 取得した値
     */
    private native double getMemoryTotal();

    /**
     * 物理メモリ（空き）を取得
     * @return 取得した値
     */
    private native double getFormattedCounterValueMemAvailable();

    /**
     * ページファイル使用率を取得
     * @return 取得した値
     */
    private native double getFormattedCounterValuePageFileUsage();

    /**
     * ページファイル使用量を取得
     * @return 取得した値
     */
    private native double getFormattedCounterValuePageFileBytes();

    /**
     * ページファイル使用量を取得
     * @return 取得した値
     */
    private native double getFormattedCounterValueVirtualBytes();

    /**
     * システム全体のFD数を取得
     * @return 取得した値
     */
    private native double getFormattedCounterValueSystemFDs();

    /**
     * ページインを取得
     * @return 取得した値
     */
    private native double getFormattedCounterValuePageIn();

    /**
     * ページアウトを取得
     * @return 取得した値
     */
    private native double getFormattedCounterValuePageOut();

    /**
     * % User Timeを取得
     * @return 取得した値
     */
    private native double getFormattedCounterValueProcessUserTime();

    /**
     * % Privileged Timeを取得
     * @return 取得した値
     */
    private native double getFormattedCounterValueProcessPrivilegedTime();

    /**
     * メジャーフォールトを取得
     * @return 取得した値
     */
    private native double getFormattedCounterValueMajFlt();

    /**
     * vsizeを取得
     * @return 取得した値
     */
    private native double getFormattedCounterValueVSize();

    /**
     * rssを取得
     * @return 取得した値
     */
    private native double getFormattedCounterValueRSS();

    /**
     * スレッド数を取得
     * @return 取得した値
     */
    private native double getFormattedCounterValueNumThreads();

    /**
     * プロセスのFD数を取得
     * @return 取得した値
     */
    private native double getFormattedCounterValueProcFDs();
    
    /**
     * クエリーの使用を終了
     * @return 終了に成功したら true
     */
    private native boolean closeQuery();

    /**
     * コンストラクタ
     */
    public PerfCounter()
    {
        // do nothing
    }
    
    /**
     * システムリソース取得処理の初期化メソッド
     * @return 初期化に成功したらtrue
     */
    public boolean init()
    {
        this.lastMeasuredTime_ = Calendar.getInstance().getTimeInMillis();
        boolean result = openQuery();
        if (result)
        {
            result = addCounter(PROCESSOR_TOTAL_PRIVILEGED_TIME);
        }
        if (result)
        {
            result = collectQueryData();
        }
        return result;
    }
    
    /**
     * システムリソース取得処理の終了メソッド
     * @return 終了に成功したらtrue
     */
    public boolean destroy()
    {
        closeQuery();
        return true;
    }
    
    /**
     * Windows のシステムリソースのMapを返す
     * 
     * @return システムリソースのMap
     */
    public Map<String, Double> getPerfData()
    {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        // 計測間隔を秒単位に変換する
        double measurementInterval =
            (currentTime - this.lastMeasuredTime_) / MILLI_SECONDS_TO_SECONDS;
        this.lastMeasuredTime_ = currentTime;

        boolean updated = updateHandles();

        // 登録されているカウンタ値を計測する
        collectQueryData();

        // それぞれのカウンタ値の計測結果を得る
        double sysCPUSys = getFormattedCounterValueSysCPUSys();
        double sysCPUUser = getFormattedCounterValueSysCPUUser();
        double memTotal = getMemoryTotal();
        double memAvailable = getFormattedCounterValueMemAvailable();
        double pageFileUsage = getFormattedCounterValuePageFileUsage();
        double pageFileBytes = getFormattedCounterValuePageFileBytes();
        double virtualBytes = getFormattedCounterValueVirtualBytes();
        double systemFDs = getFormattedCounterValueSystemFDs();
        double pageIn = getFormattedCounterValuePageIn();
        double pageOut = getFormattedCounterValuePageOut();
        double procUserTime = getFormattedCounterValueProcessUserTime();
        double procSysTime = getFormattedCounterValueProcessPrivilegedTime();
        double majFlt = getFormattedCounterValueMajFlt();
        double vsize = getFormattedCounterValueVSize();
        double rss = getFormattedCounterValueRSS();
        double numThreads = getFormattedCounterValueNumThreads();
        double procFDs = getFormattedCounterValueProcFDs();

        if (updated && this.prevResourceMap_ != null)
        {
            procUserTime = this.prevResourceMap_.get(PROCESS_USER_TIME);
            procSysTime = this.prevResourceMap_.get(PROCESS_PRIVILEGED_TIME);
            majFlt = this.prevResourceMap_.get(PROCESS_PAGE_FAULTS_SEC);
            vsize = this.prevResourceMap_.get(PROCESS_VIRTUAL_BYTES);
            rss = this.prevResourceMap_.get(PROCESS_WORKING_SET);
            numThreads = this.prevResourceMap_.get(PROCESS_THREAD_COUNT);
            procFDs = this.prevResourceMap_.get(PROCESS_NUMBER_FDS);
        }

        Map<String, Double> systemResourceMap = new HashMap<String, Double>();
        systemResourceMap.put(PROCESSOR_TOTAL_PRIVILEGED_TIME, sysCPUSys);
        systemResourceMap.put(PROCESSOR_TOTAL_USER_TIME, sysCPUUser);
        systemResourceMap.put(MEMORY_TOTAL, memTotal);
        systemResourceMap.put(MEMORY_AVAILABLE_BYTES, memAvailable);
        systemResourceMap.put(PAGING_FILE_USAGE, pageFileUsage);
        systemResourceMap.put(MEMORY_PAGES_INPUT_SEC, pageIn);
        systemResourceMap.put(MEMORY_PAGES_OUTPUT_SEC, pageOut);
        systemResourceMap.put(PROCESS_TOTAL_NUMBER_FDS, systemFDs);
        systemResourceMap.put(PROCESS_TOTAL_PAGE_FILE_BYTES, pageFileBytes);
        systemResourceMap.put(PROCESS_TOTAL_VIRTUAL_BYTES, virtualBytes);
        systemResourceMap.put(PROCESS_USER_TIME, procUserTime);
        systemResourceMap.put(PROCESS_PRIVILEGED_TIME, procSysTime);
        systemResourceMap.put(PROCESS_PAGE_FAULTS_SEC, majFlt);
        systemResourceMap.put(PROCESS_VIRTUAL_BYTES, vsize);
        systemResourceMap.put(PROCESS_WORKING_SET, rss);
        systemResourceMap.put(PROCESS_THREAD_COUNT, numThreads);
        systemResourceMap.put(PROCESS_NUMBER_FDS, procFDs);

        systemResourceMap.put(INTERVAL, measurementInterval);

        this.prevResourceMap_ = systemResourceMap;
        return systemResourceMap;
    }
}
