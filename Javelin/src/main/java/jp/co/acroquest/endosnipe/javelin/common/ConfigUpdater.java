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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.config.JavelinConfigUtil;
import jp.co.acroquest.endosnipe.javelin.RootInvocationManager;
import jp.co.acroquest.endosnipe.javelin.event.JavelinEventCounter;
import jp.co.acroquest.endosnipe.javelin.jdbc.common.JdbcJavelinConfig;
import jp.co.acroquest.endosnipe.javelin.util.LinkedHashMap;
/**
 * リモート設定機能から現在の設定値を更新するアダプタ
 * 
 * @author kimura
 */
public class ConfigUpdater
{
    /** ログレベルとして許容する文字列 */
    private static final Set<String> LOGLEVELS = new HashSet<String>();

    /** イベントレベルとして許容する文字列 */
    private static final Set<String> EVENTLEVELS = new HashSet<String>();

    private static Map<String, ConfigUpdateRequest> updateLatorMap__ = new HashMap<String, ConfigUpdateRequest>(); 
    
    static
    {
        LOGLEVELS.add("FATAL");
        LOGLEVELS.add("ERROR");
        LOGLEVELS.add("WARN");
        LOGLEVELS.add("INFO");
        LOGLEVELS.add("DEBUG");

        EVENTLEVELS.add("ERROR");
        EVENTLEVELS.add("WARN");
        EVENTLEVELS.add("INFO");

    }

    /**
     * デフォルトコンストラクタ
     */
    private ConfigUpdater()
    {
        // Do Nothing.
    }

    /**
     * 更新可能な設定値を取得する
     * 
     * @return 更新可能な設定値のMap
     */
    public static Map<String, String> getUpdatableConfig()
    {
        Map<String, String> properties = new LinkedHashMap<String, String>();

        JavelinConfig config = new JavelinConfig();
        // 呼び出し情報をアラームとして出力する際の閾値
        properties.put(JavelinConfig.ALARMTHRESHOLD_KEY, 
                       String.valueOf(config.getAlarmThreshold()));
        // 警告を発生させる際のCPU時間の閾値
        properties.put(JavelinConfig.ALARM_CPUTHRESHOLD,
                       String.valueOf(config.getAlarmCpuThreashold()));
        // 例外をアラーム通知するかのフラグ
        properties.put(JavelinConfig.ALARM_EXCEPTION_KEY, 
                       String.valueOf(config.isAlarmException()));
        // HTTPステータスエラーを通知するかのフラグ
        properties.put(JavelinConfig.HTTP_STATUS_ERROR_KEY, 
                       String.valueOf(config.isHttpStatusError()));        
        // 引数をファイルに出力するかのフラグ
        properties.put(JavelinConfig.LOG_ARGS_KEY, String.valueOf(config.isLogArgs()));
        // 引数詳細を出力するかのフラグ
        properties.put(JavelinConfig.ARGS_DETAIL_KEY, String.valueOf(config.isArgsDetail()));
        // 引数詳細出力時の深度
        properties.put(JavelinConfig.ARGS_DETAIL_DEPTH_KEY,
                       String.valueOf(config.getArgsDetailDepth()));
        
        // 返り値をファイルに出力するかのフラグ
        properties.put(JavelinConfig.LOG_RETURN_KEY, String.valueOf(config.isLogReturn()));
        // 返り値詳細を出力するかのフラグ
        properties.put(JavelinConfig.RETURN_DETAIL_KEY, String.valueOf(config.isReturnDetail()));
        // 返り値詳細出力時の深度
        properties.put(JavelinConfig.RETURN_DETAIL_DEPTH_KEY,
                       String.valueOf(config.getReturnDetailDepth()));
        
        // スタックトレースをファイルに出力するかのフラグ
        properties.put(JavelinConfig.LOG_STACKTRACE_KEY, String.valueOf(config.isLogStacktrace()));
        
        // HTTPセッションをファイルに出力するかのフラグ
        properties.put(JavelinConfig.LOG_HTTP_SESSION_KEY, String.valueOf(config.isLogHttpSession()));
        // HTTPセッション詳細を出力するかのフラグ
        properties.put(JavelinConfig.HTTP_SESSION_DETAIL_KEY, String.valueOf(config.isHttpSessionDetail()));
        // HTTPセッション詳細出力時の深度
        properties.put(JavelinConfig.HTTP_SESSION_DETAIL_DEPTH_KEY,
                       String.valueOf(config.getHttpSessionDetailDepth()));

        // JMX情報をファイルに出力するかのフラグ
        properties.put(JavelinConfig.LOG_MBEANINFO_KEY, String.valueOf(config.isLogMBeanInfo()));
        // JMX情報をファイルに出力するかのフラグ
        properties.put(JavelinConfig.LOG_MBEANINFO_ROOT_KEY,
                       String.valueOf(config.isLogMBeanInfoRoot()));
        // イベントレベル
        properties.put(JavelinConfig.EVENT_LEVEL_KEY, config.getEventLevel());
        
        // スレッドモデル定義
        properties.put(JavelinConfig.THREADMODEL_KEY, String.valueOf(config.getThreadModel()));
        // Collectionのメモリリーク検出を行うかどうか
        properties.put(JavelinConfig.COLLECTION_MONITOR,
                       String.valueOf(config.isCollectionMonitor()));
        // メモリリーク閾値
        properties.put(JavelinConfig.COLLECTION_SIZE_THRESHOLD,
                       String.valueOf(config.getCollectionSizeThreshold())); // メモリリーク閾値
        // メモリリーク検出時、リークを起こしたコレクションのオブジェクトサイズを出力するかのフラグ
        properties.put(JavelinConfig.LEAK_COLLECTIONSIZE_OUT,
                       String.valueOf(config.isLeakCollectionSizePrint()));
        // クラスヒストグラムを取得するかどうか　
        properties.put(JavelinConfig.CLASS_HISTO, String.valueOf(config.getClassHisto()));
        // クラスヒストグラム取得間隔(ミリ秒) 
        properties.put(JavelinConfig.CLASS_HISTO_INTERVAL,
                       String.valueOf(config.getClassHistoInterval()));
        // クラスヒストグラムの上位何件を取得するか 　
        properties.put(JavelinConfig.CLASS_HISTO_MAX, String.valueOf(config.getClassHistoMax()));
        // クラスヒストグラムを取得する際に、GCを行うかどうか
        properties.put(JavelinConfig.CLASS_HISTO_GC, String.valueOf(config.getClassHistoGC()));
        // 線形検索を行うかどうか
        properties.put(JavelinConfig.LINEARSEARCH_ENABLED_KEY,
                       String.valueOf(config.isLinearSearchMonitor()));
        // 線形検索検出を行うリストサイズの閾値
        properties.put(JavelinConfig.LINEARSEARCH_SIZE,
                       String.valueOf(config.getLinearSearchListSize()));
        // 線形検索検出を行う線形アクセス回数の割合の閾値
        properties.put(JavelinConfig.LINEARSEARCH_RATIO,
                       String.valueOf(config.getLinearSearchListRatio()));
        // ネットワーク入力量を取得するか
        properties.put(JavelinConfig.NET_INPUT_MONITOR, String.valueOf(config.isNetInputMonitor()));
        // ネットワーク出力量を取得するか
        properties.put(JavelinConfig.NET_OUTPUT_MONITOR,
                       String.valueOf(config.isNetOutputMonitor()));
        // ファイル入力量を取得するか
        properties.put(JavelinConfig.FILE_INPUT_MONITOR,
                       String.valueOf(config.isFileInputMonitor()));
        // ファイル出力量を取得するか
        properties.put(JavelinConfig.FILE_OUTPUT_MONITOR,
                       String.valueOf(config.isFileOutputMonitor()));
        // ファイナライズ待ちオブジェクト数を取得するか
        properties.put(JavelinConfig.FINALIZATION_COUNT_MONITOR,
                       String.valueOf(config.isFinalizationCount()));
        // メソッド呼び出し間隔超過を監視するか
        properties.put(JavelinConfig.INTERVAL_ERROR_MONITOR,
                       String.valueOf(config.isIntervalMonitor()));
        // スレッド監視を行うかどうか
        properties.put(JavelinConfig.THREAD_MONITOR, String.valueOf(config.getThreadMonitor()));
        // スレッド監視を行う周期(ミリ秒)
        properties.put(JavelinConfig.THREAD_MONITOR_INTERVAL,
                       String.valueOf(config.getThreadMonitorInterval()));
        // スレッド監視の際に出力するスタックトレースの深さ
        properties.put(JavelinConfig.THREAD_MONITOR_DEPTH,
                       String.valueOf(config.getThreadMonitorDepth()));
        // スレッド監視の際にブロック回数異常を検知する閾値。
        // 現状はイベントが発生しないため、設定できないようにしておく（チケット#729参照）
        //        properties.put(JavelinConfig.THREAD_BLOCK_THRESHOLD,
        //                       String.valueOf(config.getBlockThreshold()));
        // スレッド監視の際にブロック回数閾値を超えた際に取得するスレッド情報の数
        // 現状はイベントが発生しないため、設定できないようにしておく（チケット#730参照）
        //        properties.put(JavelinConfig.THREAD_BLOCK_THREADINFO_NUM,
        //                       String.valueOf(config.getBlockThreadInfoNum()));
        // ブロック継続イベントを出力する際のブロック回数の閾値
        properties.put(JavelinConfig.THREAD_BLOCK_THRESHOLD,
                       String.valueOf(config.getBlockThreshold()));
        // ブロック継続イベントを出力する際のブロック継続時間の閾値
        properties.put(JavelinConfig.THREAD_BLOCKTIME_THRESHOLD,
                       String.valueOf(config.getBlockTimeThreshold()));
        // フルスレッドダンプを出力するかどうか
        properties.put(JavelinConfig.THREAD_DUMP_MONITOR, String.valueOf(config.isThreadDump()));
        // フルスレッドダンプ出力判定を行う周期(ミリ秒)
        properties.put(JavelinConfig.THREAD_DUMP_INTERVAL,
                       String.valueOf(config.getThreadDumpInterval()));
        // フルスレッドダンプ出力のスレッド数の閾値
        properties.put(JavelinConfig.THREAD_DUMP_THREAD,
                       String.valueOf(config.getThreadDumpThreadNum()));
        // フルスレッドダンプ出力のCPU使用率の閾値
        properties.put(JavelinConfig.THREAD_DUMP_CPU, String.valueOf(config.getThreadDumpCpu()));
        // フルGCを検出するかどうか
        properties.put(JavelinConfig.FULLGC_MONITOR, String.valueOf(config.isFullGCMonitor()));
        // フルGC検出のGC時間の閾値
        properties.put(JavelinConfig.FULLGC_THREASHOLD, 
                       String.valueOf(config.getFullGCThreshold()));
        
        // Java6以降でデッドロック監視を行うかどうか
        properties.put(JavelinConfig.THREAD_DEADLOCK_MONITOR, String.valueOf(config.isDeadLockMonitor()));
        
        // アラーム送信間隔の最小値
        properties.put(JavelinConfig.ALARM_MINIMUM_INTERVAL_KEY,
                       String.valueOf(config.getAlarmMinimumInterval()));
        // Turn Around Timeを計測するかどうか。
        properties.put(JavelinConfig.TAT_ENABLED_KEY, String.valueOf(config.isTatEnabled()));
        // Turn Around Timeの保持期間。
        properties.put(JavelinConfig.TAT_KEEP_TIME_KEY, String.valueOf(config.getTatKeepTime()));
        // HttpSessionのインスタンス数を取得するか
        properties.put(JavelinConfig.HTTP_SESSION_COUNT_MONITOR,
                       String.valueOf(config.isHttpSessionCount()));
        // HttpSessionへの登録オブジェクト総サイズを取得するか
        properties.put(JavelinConfig.HTTP_SESSION_SIZE_MONITOR,
                       String.valueOf(config.isHttpSessionSize()));
        // 複数スレッドアクセスを監視するかどうか。
        properties.put(JavelinConfig.CONCURRENT_ENABLED_KEY,
                       String.valueOf(config.isConcurrentAccessMonitored()));
        // タイムアウト値設定の監視を行うかどうか。
        properties.put(JavelinConfig.TIMEOUT_MONITOR, String.valueOf(config.isTimeoutMonitor()));
        // Log4jのスタックトレースを行うログレベルの閾値
        properties.put(JavelinConfig.LOG4J_PRINTSTACK_LEVEL, config.getLog4jPrintStackLevel());
        
        // CallTreeサイズの最大値。
        properties.put(JavelinConfig.CALL_TREE_ENABLE_KEY, String.valueOf(config.isCallTreeEnabled()));
        
        // CallTreeサイズの最大値。
        properties.put(JavelinConfig.CALL_TREE_MAX_KEY, String.valueOf(config.getCallTreeMax()));

        // Jvnログファイルを出力するかどうか。
        properties.put(JavelinConfig.LOG_JVN_FILE, String.valueOf(config.isLogJvnFile()));
        
        // システムのリソースデータを取得するかどうか。
        properties.put(JavelinConfig.COLLECT_SYSTEM_RESOURCES, String.valueOf(config.getCollectSystemResources()));
        
        // InvocationFullEventを送信するかどうか。
        properties.put(JavelinConfig.SEND_INVOCATION_FULL_EVENT, String.valueOf(config.getSendInvocationFullEvent()));

        /** JdbcJavelinConfigから取得可能な設定を取得する */
        JdbcJavelinConfig jdbcConfig = new JdbcJavelinConfig();
        
        // JDBCJavelinを有効にするかどうか
        properties.put(JdbcJavelinConfig.JDBC_JAVELIN_ENABLED_KEY,
                       String.valueOf(jdbcConfig.isJdbcJavelinEnabled()));

        // 実行計画取得フラグ
        properties.put(JdbcJavelinConfig.RECORDEXECPLAN_KEY,
                       String.valueOf(jdbcConfig.isRecordExecPlan()));
        // SQLの実行計画を記録する際の閾値
        properties.put(JdbcJavelinConfig.EXECPLANTHRESHOLD_KEY,
                       String.valueOf(jdbcConfig.getExecPlanThreshold()));
        // JDBC呼出し重複出力フラグ
        properties.put(JdbcJavelinConfig.RECORDDUPLJDBCCALL_KEY,
                       String.valueOf(jdbcConfig.isRecordDuplJdbcCall()));
        // バインド変数出力フラグ
        properties.put(JdbcJavelinConfig.RECORDBINDVAL_KEY,
                       String.valueOf(jdbcConfig.isRecordBindVal()));
        // バインド変数出力における文字列長制限
        properties.put(JdbcJavelinConfig.STRINGLIMITLENGTH_KEY,
                       String.valueOf(jdbcConfig.getJdbcStringLimitLength()));
        // 同一トランザクション内の同一SQL呼び出し回数超過の閾値を監視するか否か
        properties.put(JdbcJavelinConfig.SQLCOUNT_MONITOR_KEY,
                       String.valueOf(jdbcConfig.isSqlcountMonitor()));
        // 同一トランザクション内の同一SQL呼び出し回数超過の閾値
        properties.put(JdbcJavelinConfig.SQLCOUNT_KEY,
                       String.valueOf(jdbcConfig.getSqlcount()));
        // Oracleに対するSQLトレースの出力指示フラグ
        properties.put(JdbcJavelinConfig.ORACLE_ALLOW_SQL_TRACE_KEY,
                       String.valueOf(jdbcConfig.isAllowSqlTraceForOracle()));
        // PostgreSQLに対する実行計画詳細取得フラグ
        properties.put(JdbcJavelinConfig.POSTGRES_VERBOSE_PLAN_KEY,
                       String.valueOf(jdbcConfig.isVerbosePlanForPostgres()));
        // JDBCJavelinでスタックトレースを出力するためのフラグ
        properties.put(JdbcJavelinConfig.RECORD_STACKTRACE_KEY,
                       String.valueOf(jdbcConfig.isRecordStackTrace()));
        // JDBCJavelinでスタックトレースを出力するためのSQL実行時間の閾値
        properties.put(JdbcJavelinConfig.RECORD_STACKTRACE_THREADHOLD_KEY,
                       String.valueOf(jdbcConfig.getRecordStackTraceThreshold()));

        
        return properties;
    }

    /**
     * 呼び出し情報を記録する際の閾値を更新する
     * 
     * @param alarmThreshold 閾値（ミリ秒）
     */
    public static void updateAlarmThreshold(final long alarmThreshold)
    {
        JavelinConfig config = new JavelinConfig();
        config.setAlarmThreshold(alarmThreshold);
    }

    /**
     * ログを取得する際のCPU時間の閾値を更新する
     * 
     * @param alarmCpuThreshold CPU時間の閾値（ミリ秒）
     */
    public static void updateAlarmCpuThreshold(final long alarmCpuThreshold)
    {
        JavelinConfig config = new JavelinConfig();
        config.setAlarmCpuThreashold(alarmCpuThreshold);
    }

    /**
     * スタックトレースをファイルに出力するかのフラグを更新する
     * 
     * @param isLogStacktrace スタックトレースをファイルに出力するかのフラグ
     */
    public static void updateLogStacktrace(final boolean isLogStacktrace)
    {
        JavelinConfig javelinConfig = new JavelinConfig();
        javelinConfig.setLogStacktrace(isLogStacktrace);
    }

    /**
     * アプリケーション例外時にアラーム通知するかのフラグを更新する
     * 
     * @param isAlarmException アラーム通知するかのフラグ
     */
    public static void updateAlarmException(final boolean isAlarmException)
    {
        JavelinConfig javelinConfig = new JavelinConfig();
        javelinConfig.setAlarmException(isAlarmException);
    }

    /**
     * HTTPステータスエラー発生時にアラーム通知するかのフラグを更新する
     * 
     * @param isHttpStatusError アラーム通知するかのフラグ
     */
    public static void updateHttpStatusError(final boolean isHttpStatusError)
    {
        JavelinConfig javelinConfig = new JavelinConfig();
        javelinConfig.setHttpStatusError(isHttpStatusError);
    }
    
    /**
     * 引数をファイルに出力するかのフラグを更新する
     * 
     * @param isLogArgs 引数をファイルに出力するかのフラグ
     */
    public static void updateLogArgs(final boolean isLogArgs)
    {
        JavelinConfig javelinConfig = new JavelinConfig();
        javelinConfig.setLogArgs(isLogArgs);
    }

    /**
     * 返り値をファイルに出力するかのフラグを更新する
     * 
     * @param isLogReturn 返り値をファイルに出力するかのフラグ
     */
    public static void updateLogReturn(final boolean isLogReturn)
    {
        JavelinConfig javelinConfig = new JavelinConfig();
        javelinConfig.setLogReturn(isLogReturn);
    }

    /**
     * 引数をファイルに出力するかのフラグを更新する
     * 
     * @param isLogArgs 引数をファイルに出力するかのフラグ
     */
    public static void updateLogHttpSession(final boolean isLogArgs)
    {
        JavelinConfig javelinConfig = new JavelinConfig();
        javelinConfig.setLogHttpSession(isLogArgs);
    }

    /**
     * MBean情報をファイルに出力するかのフラグを更新する
     * 
     * @param isLogMBeanInfo MBean情報をファイルに出力するかのフラグ
     */
    public static void updateLogMBeanInfo(final boolean isLogMBeanInfo)
    {
        JavelinConfig javelinConfig = new JavelinConfig();
        javelinConfig.setLogMBeanInfo(isLogMBeanInfo);
    }

    /**
     * MBean情報をファイルに出力するかのフラグを更新する
     * 
     * @param isLogMBeanInfo MBean情報をファイルに出力するかのフラグ
     */
    public static void updateLogMBeanInfoRoot(final boolean isLogMBeanInfo)
    {
        JavelinConfig javelinConfig = new JavelinConfig();
        javelinConfig.setLogMBeanInfoRoot(isLogMBeanInfo);
    }

    /**
     * 引数詳細を出力するかのフラグを更新する
     * 
     * @param isArgsDetail 引数詳細を出力するかのフラグ
     */
    public static void updateArgsDetail(final boolean isArgsDetail)
    {
        JavelinConfig javelinConfig = new JavelinConfig();
        javelinConfig.setArgsDetail(isArgsDetail);
    }

    /**
     * 引数詳細出力時の深度を更新する
     * 
     * @param argsDetailDepth 引数詳細出力時の深度
     */
    public static void updateArgsDetailDepth(final int argsDetailDepth)
    {
        JavelinConfig javelinConfig = new JavelinConfig();
        javelinConfig.setArgsDetailDepth(argsDetailDepth);
    }

    /**
     * 返り値詳細を出力するかのフラグを更新する
     * 
     * @param isReturnDetail 返り値詳細を出力するかのフラグ
     */
    public static void updateReturnDetail(final boolean isReturnDetail)
    {
        JavelinConfig javelinConfig = new JavelinConfig();
        javelinConfig.setReturnDetail(isReturnDetail);
    }

    /**
     * 返り値詳細出力時の深度
     * 
     * @param returnDetailDepth 返り値詳細出力時の深度
     */
    public static void updateReturnDetailDepth(final int returnDetailDepth)
    {
        JavelinConfig javelinConfig = new JavelinConfig();
        javelinConfig.setReturnDetailDepth(returnDetailDepth);
    }

    /**
     * HTTPセッション詳細を出力するかのフラグを更新する
     * 
     * @param isHttpSessionDetail HTTPセッション詳細を出力するかのフラグ
     */
    public static void updateHttpSessionDetail(final boolean isHttpSessionDetail)
    {
        JavelinConfig javelinConfig = new JavelinConfig();
        javelinConfig.setHttpSessionDetail(isHttpSessionDetail);
    }

    /**
     * HTTPセッション詳細出力時の深度を更新する
     * 
     * @param httpSessionDetailDepth HTTPセッション詳細出力時の深度
     */
    public static void updateHttpSessionDetailDepth(final int httpSessionDetailDepth)
    {
        JavelinConfig javelinConfig = new JavelinConfig();
        javelinConfig.setHttpSessionDetailDepth(httpSessionDetailDepth);
    }
    
    /**
     * スレッドモデル定義を更新する
     * 
     * @param threadModel スレッドモデル定義
     */
    public static void updateThreadModel(final int threadModel)
    {
        JavelinConfig javelinConfig = new JavelinConfig();
        javelinConfig.setThreadModel(threadModel);
    }

    /**
     * JDBC Javelinの有効/無効フラグを更新する
     * 
     * @param isJdbcEnabled JDBC Javelinの有効/無効フラグ
     */
    public static void updateJdbcEnabled(final boolean isJdbcEnabled)
    {
        JdbcJavelinConfig jdbcJavelinConfig = new JdbcJavelinConfig();
        jdbcJavelinConfig.setJdbcJavelinEnabled(isJdbcEnabled);
    }
    
    /**
     * 実行計画取得フラグを更新する
     * 
     * @param isRecordExecPlan SQLの実行計画を記録する際の閾値
     */
    public static void updateRecordExecPlan(final boolean isRecordExecPlan)
    {
        JdbcJavelinConfig jdbcJavelinConfig = new JdbcJavelinConfig();
        jdbcJavelinConfig.setRecordExecPlan(isRecordExecPlan);
    }

    /**
     * SQLの実行計画を記録する際の閾値を更新する
     * 
     * @param execPlanThreshold SQLの実行計画を記録する際の閾値
     */
    public static void updateExecPlanThreshold(final long execPlanThreshold)
    {
        JdbcJavelinConfig jdbcJavelinConfig = new JdbcJavelinConfig();
        jdbcJavelinConfig.setExecPlanThreshold(execPlanThreshold);
    }

    /**
     * JDBC呼出し重複出力フラグを更新する
     * 
     * @param isRecordDuplJdbcCall JDBC呼出し重複出力フラグ
     */
    public static void updateRecordDuplJdbcCall(final boolean isRecordDuplJdbcCall)
    {
        JdbcJavelinConfig jdbcJavelinConfig = new JdbcJavelinConfig();
        jdbcJavelinConfig.setRecordDuplJdbcCall(isRecordDuplJdbcCall);
    }

    /**
     * バインド変数出力フラグを更新する
     * 
     * @param isRecordBindVal バインド変数出力フラグ
     */
    public static void updateRecordBindVal(final boolean isRecordBindVal)
    {
        JdbcJavelinConfig jdbcJavelinConfig = new JdbcJavelinConfig();
        jdbcJavelinConfig.setRecordBindVal(isRecordBindVal);
    }

    /**
     * 同一トランザクション内の同一SQL呼び出し回数超過の閾値を監視するか否かを更新する
     * 
     * @param isSqlcountMonitor 同一トランザクション内の同一SQL呼び出し回数超過の閾値を監視するか否か
     */
    public static void updateSqlcountMonitor(final boolean isSqlcountMonitor)
    {
        JdbcJavelinConfig jdbcJavelinConfig = new JdbcJavelinConfig();
        jdbcJavelinConfig.setSqlcountMonitor(isSqlcountMonitor);
    }

    /**
     * 同一トランザクション内の同一SQL呼び出し回数超過の閾値を更新する
     * 
     * @param sqlCount 同一トランザクション内の同一SQL呼び出し回数超過の閾値
     */
    public static void updateSqlcount(final long sqlCount)
    {
        JdbcJavelinConfig jdbcJavelinConfig = new JdbcJavelinConfig();
        jdbcJavelinConfig.setSqlcount(sqlCount);
    }

    /**
     * バインド変数出力における文字列長制限を更新する
     * 
     * @param jdbcStringLimitLength バインド変数出力における文字列長制限
     */
    public static void updateJdbcStringLimitLength(final long jdbcStringLimitLength)
    {
        JdbcJavelinConfig jdbcJavelinConfig = new JdbcJavelinConfig();
        jdbcJavelinConfig.setJdbcStringLimitLength(jdbcStringLimitLength);
    }

    /**
     * Oracleに対するSQLトレースの出力指示フラグを更新する
     * 
     * @param isAllowSqlTraceForOracle Oracleに対するSQLトレースの出力指示フラグ
     */
    public static void updateAllowSqlTraceForOracle(final boolean isAllowSqlTraceForOracle)
    {
        JdbcJavelinConfig jdbcJavelinConfig = new JdbcJavelinConfig();
        jdbcJavelinConfig.setAllowSqlTraceForOracle(isAllowSqlTraceForOracle);
    }

    /**
     * PostgreSQLに対する実行計画詳細取得フラグを更新する
     * 
     * @param isVerbosePlanForPostgres PostgreSQLに対する実行計画詳細取得フラグ
     */
    public static void updateVerbosePlanForPostgres(final boolean isVerbosePlanForPostgres)
    {
        JdbcJavelinConfig jdbcJavelinConfig = new JdbcJavelinConfig();
        jdbcJavelinConfig.setVerbosePlanForPostgres(isVerbosePlanForPostgres);
    }

    /**
     * JDBCJavelinスタックトレース取得フラグを更新する
     * 
     * @param recordStackTrace JDBCJavelinスタックトレース取得フラグ
     */
    public static void updateRecordStackTrace(final boolean recordStackTrace)
    {
        JdbcJavelinConfig jdbcJavelinConfig = new JdbcJavelinConfig();
        jdbcJavelinConfig.setRecordStackTrace(recordStackTrace);
    }

    /**
     * JDBCJavelinスタックトレース取得フラグを更新する
     * 
     * @param stackTraceThreshold JDBCJavelinでスタックトレースを取得するための閾値
     */
    public static void updateRecordStackTraceThreshold(final int stackTraceThreshold)
    {
        JdbcJavelinConfig jdbcJavelinConfig = new JdbcJavelinConfig();
        jdbcJavelinConfig.setRecordStackTraceThreshold(stackTraceThreshold);
    }

    /**
     * JDBC Javelinを使用するかどうかを設定する。
     * 
     * @param isJdbcJavelinEnabled JDBC Javelinを使用するかどうか
     */
    public static void updateJdbcJavelinEnabled(final boolean isJdbcJavelinEnabled)
    {
        JdbcJavelinConfig config = new JdbcJavelinConfig();
        config.setJdbcJavelinEnabled(isJdbcJavelinEnabled);
    }
    
    /**
     * Collectionのメモリリーク検出を行うかどうかを更新します。<br />
     * 
     * @param collectionMonitor Collectionのメモリリーク検出を行う場合、<code>true</code>
     */
    public static void updateCollectionMonitor(final boolean collectionMonitor)
    {
        JavelinConfig config = new JavelinConfig();
        config.setCollectionMonitor(collectionMonitor);
    }

    /**
     * 保存するコレクションオブジェクトのサイズの閾値を更新する。
     * 
     * @param collectionSizeThreshold 保存するコレクションオブジェクトのサイズ
     */
    public static void updateCollectionSizesThreshold(final int collectionSizeThreshold)
    {
        JavelinConfig config = new JavelinConfig();
        config.setCollectionSizeThreshold(collectionSizeThreshold);
    }

    /**
     * クラスヒストグラムを取得する際に、GCを行うかどうかを設定する。
     * 
     * @param classHistoGC クラスヒストグラムを取得する際に、GCを行うかどうか。
     */
    public static void updateClassHistoGC(final boolean classHistoGC)
    {
        JavelinConfig config = new JavelinConfig();
        config.setClassHistoGC(classHistoGC);
    }

    /**
     * クラスヒストグラム取得間隔(ミリ秒)を設定する。
     * 
     * @param classHistoInterval クラスヒストグラム取得間隔(ミリ秒)。
     */
    public static void updateClassHistoInterval(final int classHistoInterval)
    {
        JavelinConfig config = new JavelinConfig();
        config.setClassHistoInterval(classHistoInterval);
    }

    /**
     * クラスヒストグラムの上位何件を取得するかを設定する。
     * 
     * @param classHistoMax クラスヒストグラムの上位何件を取得するか。
     */
    public static void updateClassHistoMax(final int classHistoMax)
    {
        JavelinConfig config = new JavelinConfig();
        config.setClassHistoMax(classHistoMax);
    }

    /**
     * クラスヒストグラムを取得するかどうかを設定する。
     * 
     * @param classHisto クラスヒストグラムを取得するかどうか。
     */
    public static void updateClassHisto(final boolean classHisto)
    {
        JavelinConfig config = new JavelinConfig();
        config.setClassHisto(classHisto);
    }

    /**
     * 線形検索検出を行うかどうかを設定します。<br />
     * 
     * @param isLinearSearchMonitor 線形検索検出を行うかどうか
     */
    public static void updateLinearSearchMonitor(final boolean isLinearSearchMonitor)
    {
        JavelinConfig config = new JavelinConfig();
        config.setLinearSearchMonitor(isLinearSearchMonitor);
    }

    /**
     * 線形検索対象となるリストサイズの閾値を設定します。
     * 
     * @param size 線形検索対象となるリストサイズの閾値
     */
    public static void updateLinearSearchSize(final int size)
    {
        JavelinConfig config = new JavelinConfig();
        config.setLinearSearchListSize(size);
    }

    /**
     * リストサイズに対する線形アクセス回数の割合の閾値を設定します。
     * 
     * @param ratio リストサイズに対する線形アクセス回数の割合
     */
    public static void updateLinearSearchRatio(final double ratio)
    {
        JavelinConfig config = new JavelinConfig();
        config.setLinearSearchListRatio(ratio);
    }

    /**
     * ネットワーク入力量を取得するかを設定する。
     * 
     * @param isNetInputMonitor クラスヒストグラムの上位何件を取得するか。
     */
    public static void updateNetInputMonitor(final boolean isNetInputMonitor)
    {
        JavelinConfig config = new JavelinConfig();
        config.setNetInputMonitor(isNetInputMonitor);
    }

    /**
     * ネットワーク出力量を取得するかを設定する
     * 
     * @param isNetOutputMonitor ネットワーク出力量を取得するか
     */
    public static void updateNetOutputMonitor(final boolean isNetOutputMonitor)
    {
        JavelinConfig config = new JavelinConfig();
        config.setNetOutputMonitor(isNetOutputMonitor);
    }

    /**
     * ファイル入力量を取得するかを設定する
     * 
     * @param isFileInputMonitor ファイル入力量を取得するか
     */
    public static void updateFileInputMonitor(final boolean isFileInputMonitor)
    {
        JavelinConfig config = new JavelinConfig();
        config.setFileInputMonitor(isFileInputMonitor);
    }

    /**
     * ファイル出力量を取得するかを設定する
     * 
     * @param isFileOutputMonitor ファイル出力量を取得するか
     */
    public static void updateFileOutputMonitor(final boolean isFileOutputMonitor)
    {
        JavelinConfig config = new JavelinConfig();
        config.setFileOutputMonitor(isFileOutputMonitor);
    }

    /**
     * ファイナライズ待ちオブジェクト数を取得するかを設定する
     * 
     * @param isFinalizationCount ファイナライズ待ちオブジェクト数を取得するか
     */
    public static void updateFinalizationCount(final boolean isFinalizationCount)
    {
        JavelinConfig config = new JavelinConfig();
        config.setFinalizationCount(isFinalizationCount);
    }

    /**
     * メソッド呼び出し間隔超過を監視するかを設定する
     * 
     * @param isIntervalMonitor メソッド呼び出し間隔超過を監視するか
     */
    public static void updateIntervalMonitor(final boolean isIntervalMonitor)
    {
        JavelinConfig config = new JavelinConfig();
        config.setIntervalMonitor(isIntervalMonitor);
    }

    /**
     * HttpSessionのインスタンス数を監視するかを設定する
     * 
     * @param isHttpSessionCount HttpSessionのインスタンス数を監視するか
     */
    public static void updateHttpSessionCount(final boolean isHttpSessionCount)
    {
        JavelinConfig config = new JavelinConfig();
        config.setHttpSessionCount(isHttpSessionCount);
    }

    /**
     * HttpSessionへの登録オブジェクト総サイズを監視するかを設定する
     * 
     * @param isHttpSessionSize HttpSessionへの登録オブジェクト総サイズを監視するか
     */
    public static void updateHttpSessionSize(final boolean isHttpSessionSize)
    {
        JavelinConfig config = new JavelinConfig();
        config.setHttpSessionSize(isHttpSessionSize);
    }

    /**
     * スレッド監視を行うかどうかを設定する。
     * 
     * @param threadMonitor スレッド監視を行う場合はtrue。
     */
    public static void updateThreadMonitor(final boolean threadMonitor)
    {
        JavelinConfig config = new JavelinConfig();
        config.setThreadMonitor(threadMonitor);
    }

    /**
     * スレッド監視を行う間隔(ミリ秒)を設定する。
     * 
     * @param threadMonitorInterval スレッド監視を行う間隔(ミリ秒)。
     */
    public static void updateThreadMonitorInterval(final long threadMonitorInterval)
    {
        JavelinConfig config = new JavelinConfig();
        config.setThreadMonitorInterval(threadMonitorInterval);
    }

    /**
     * スレッド監視の際に出力するスタックトレースの深さを取得する。
     * 
     * @param threadMonitorDepth スレッド監視の際に出力するスタックトレースの深さ。
     */
    public static void updateThreadMonitorDepth(final int threadMonitorDepth)
    {
        JavelinConfig config = new JavelinConfig();
        config.setThreadMonitorDepth(threadMonitorDepth);
    }

    /**
     * ブロック回数が多すぎるかどうかの閾値を更新する。
     * 
     * @param blockThreshold ブロック回数が多すぎるかどうかの閾値。
     */
    public static void updateBlockThreshold(final long blockThreshold)
    {
        JavelinConfig config = new JavelinConfig();
        config.setBlockThreshold(blockThreshold);
    }

    /**
     * ブロック継続イベントを出力する際のブロック継続時間の閾値を更新する。
     * 
     * @param blockTimeThreshold ブロック継続イベントを出力する際のブロック継続時間の閾値。
     */
    public static void updateBlockTimeThreshold(final long blockTimeThreshold)
    {
        JavelinConfig config = new JavelinConfig();
        config.setBlockTimeThreshold(blockTimeThreshold);
    }

    /**
     * ブロック回数が閾値を超えた際に取得するスレッド情報の数を更新する。
     * 
     * @param blockThreadInfoNum ブロック回数が閾値を超えた際に取得するスレッド情報の数。
     */
    public static void updateBlockThreadInfoNum(final int blockThreadInfoNum)
    {
        JavelinConfig config = new JavelinConfig();
        config.setBlockThreadInfoNum(blockThreadInfoNum);
    }

    /**
     * フルスレッドダンプの出力を行うかどうかを更新する。
     * 
     * @param threadDumpMonitor フルスレッドダンプを出力するかどうか
     */
    public static void updateThreadDumpMonitor(final boolean threadDumpMonitor)
    {
        JavelinConfig config = new JavelinConfig();
        config.setThreadDumpMonitor(threadDumpMonitor);
    }

    /**
     * フルスレッドダンプ出力判定の間隔を更新します。
     * 
     * @param threadDumpInterval フルスレッドダンプ出力判定の間隔
     */
    public static void updateThreadDumpInterval(final int threadDumpInterval)
    {
        JavelinConfig config = new JavelinConfig();
        config.setThreadDumpInterval(threadDumpInterval);
    }

    /**
     * フルスレッドダンプ出力のスレッド数の閾値を更新します。
     * 
     * @param threadDumpNum フルスレッドダンプ出力のスレッド数の閾値
     */
    public static void updateThreadDumpNum(final int threadDumpNum)
    {
        JavelinConfig config = new JavelinConfig();
        config.setThreadDumpThreadNum(threadDumpNum);
    }

    /**
     * フルスレッドダンプ出力のCPU使用率の閾値を更新します。
     * 
     * @param threadDumpCpu フルスレッドダンプ出力のCPU使用率の閾値
     */
    public static void updateThreadDumpCpu(final int threadDumpCpu)
    {
        JavelinConfig config = new JavelinConfig();
        config.setThreadDumpCpu(threadDumpCpu);
    }

    /**
     * フルGCの検出を行うかどうかを更新する。
     * 
     * @param fullGCMonitor フルGCの検出を行うかどうか
     */
    public static void updateFullGCMonitor(final boolean fullGCMonitor)
    {
        JavelinConfig config = new JavelinConfig();
        config.setFullGCMonitor(fullGCMonitor);
    }

    /**
     * フルGC実行時間の閾値を更新する。
     * 
     * @param threshold フルGC実行時間の閾値
     */
    public static void updateFullGCThreshold(final int threshold)
    {
        JavelinConfig config = new JavelinConfig();
        config.setFullGCThreshold(threshold);
    }

    /**
     * Java6以降でデッドロック監視を行うかどうかを更新する。
     * 
     * @param deadLockMonitor Java6以降でデッドロック監視を行うかどうか
     */
    public static void updateDeadLockMonitor(final boolean deadLockMonitor)
    {
        JavelinConfig config = new JavelinConfig();
        config.setDeadLockMonitor(deadLockMonitor);
    }

    /**
     * アラーム送信間隔の最小値を設定する。
     * 
     * 前回アラーム送信・Javelinログ出力を行った際から 経過した時間がこの値を超えていた場合のみ、アラーム送信・Javelinログ出力を行う。
     * 
     * @param alarmMinimumInterval 閾値。
     */
    public static void updateAlarmMinimumInterval(final long alarmMinimumInterval)
    {
        JavelinConfig config = new JavelinConfig();
        config.setAlarmMinimumInterval(alarmMinimumInterval);
    }

    /**
     * Turn Around Timeを計測するかどうかを更新する。
     * 
     * @param tatEnabled Turn Around Timeを計測する場合はtrue。
     */
    public static void updateTatEnabled(final boolean tatEnabled)
    {
        JavelinConfig config = new JavelinConfig();
        config.setTatEnabled(tatEnabled);
        RootInvocationManager.setTatEnabled(tatEnabled);
    }

    /**
     * Turn Around Timeの保持期間を更新する。
     * 
     * @param tatKeepTime Turn Around Timeの保持期間
     */
    public static void updateTatKeepTime(final long tatKeepTime)
    {
        JavelinConfig config = new JavelinConfig();
        config.setTatKeepTime(tatKeepTime);
        RootInvocationManager.setTatKeepTime(tatKeepTime);
        JavelinEventCounter.getInstance().setPoolStorePeriod(tatKeepTime);
    }

    /**
     * 複数スレッドアクセスを監視するかどうかを更新します。<br />
     * 
     * @param concurrentMonitored 複数スレッドアクセスを監視する場合はtrue。
     */
    public static void updateConcurrentAccessMonitor(final boolean concurrentMonitored)
    {
        JavelinConfig config = new JavelinConfig();
        config.setConcurrentAccessMonitored(concurrentMonitored);
    }

    /**
     * タイムアウト値の設定が行われているかどうかを更新します。<br />
     * 
     * @param timeoutMonitor タイムアウト値の設定の監視を行う場合、<code>true</code>
     */
    public static void updateTimeoutMonitor(final boolean timeoutMonitor)
    {
        JavelinConfig config = new JavelinConfig();
        config.setTimeoutMonitor(timeoutMonitor);
    }

    /**
     * コールツリーの有効/無効フラグを更新します。
     * @param isCallTreeEnabled コールツリーの有効/無効フラグ
     */
    public static void updateCallTreeEnabled(final boolean isCallTreeEnabled)
    {
        JavelinConfig config = new JavelinConfig();
        config.setCallTreeEnabled(isCallTreeEnabled);
    }

    /**
     * CallTreeサイズの最大値を更新します。<br />
     * 
     * @param callTreeMax CallTreeサイズの最大値
     */
    public static void updateCallTreeMaxSize(final int callTreeMax)
    {
        JavelinConfig config = new JavelinConfig();
        config.setCallTreeMax(callTreeMax);
    }

    /**
     * Jvnログファイルを出力するかどうかを更新します。
     * @param logJvnFile ログファイルを出力するかどうか
     */
    public static void updateLogJvnFile(final boolean logJvnFile)
    {
        JavelinConfig config = new JavelinConfig();
        config.setLogJvnFile(logJvnFile);
    }

    /**
     * メモリリーク検出時に、リークしたコレクションのサイズを出力するかどうかを更新します。<br />
     * @param leakCollectionSizePrint メモリリーク検出時に、リークしたコレクションのサイズを出力するかどうか
     */
    public static void updateLeakCollectionSizePrint(final boolean leakCollectionSizePrint)
    {
        JavelinConfig config = new JavelinConfig();
        config.setLeakCollectionSizePrint(leakCollectionSizePrint);
    }

    /**
     * Log4jのスタックトレースを行うログレベルの閾値を更新します。<br />
     * 
     * @param log4jPrintStackLevel Log4jのスタックトレースを行うログレベルの閾値
     */
    public static void updateLog4jPrintStackLevel(final String log4jPrintStackLevel)
    {
        String log4jLevelToUpper = log4jPrintStackLevel.toUpperCase();
        if (LOGLEVELS.contains(log4jLevelToUpper) == false)
        {
            return;
        }
        JavelinConfig config = new JavelinConfig();
        config.setLog4jPrintStackLevel(log4jLevelToUpper);
    }

    /**
     * Javelinのイベントレベルを更新します。<br />
     * 
     * @param eventLevel Javelinのイベントレベル 
     */
    public static void updateEventLevel(final String eventLevel)
    {
        String eventLevelToUpper = eventLevel.toUpperCase();
        if (LOGLEVELS.contains(eventLevelToUpper) == false)
        {
            return;
        }
        JavelinConfig config = new JavelinConfig();
        config.setEventLevel(eventLevelToUpper);
    }
    
    /**
     * システムのリソースデータを取得するかどうかの設定を更新します。<br />
     * 
     * @param collectSystemResources システムのリソースデータを取得するかどうか
     */
    public static void updateCollectSystemResources(final boolean collectSystemResources)
    {
        JavelinConfig config = new JavelinConfig();
        config.setCollectSystemResources(collectSystemResources);
    }

    /**
     * InvocationFullEventを送信するかどうかの設定を更新します。<br />
     * 
     * @param sendInvocationFullEvent InvocationFullEventを送信するかどうか
     */
    public static void updateSendInvocationFullEvent(final boolean sendInvocationFullEvent)
    {
        JavelinConfig config = new JavelinConfig();
        config.setSendInvocationFullEvent(sendInvocationFullEvent);
    }

    /**
     * 指定した時間後に設定を更新する。
     * 
     * @param key キー
     * @param value 値
     * @param delay 時間(ms)
     */
    public static void updateLater(final String key, final String value, long delay)
    {
        long updateTime = System.currentTimeMillis() + delay;
        synchronized (updateLatorMap__)
        {
            updateLatorMap__.put(key, new ConfigUpdateRequest(key, value, updateTime));
        }
    }

    /**
     * 設定を更新する。
     * 
     * @param key キー
     * @param value 値
     */
    public static void update(String key, String value)
    {
        // JavelinConfigが持つ設定の更新
        // JavelinConfigが持つ設定の更新
        if (JavelinConfig.ALARMTHRESHOLD_KEY.equals(key))
        {
            ConfigUpdater.updateAlarmThreshold(Long.parseLong(value));
        }
        else if (JavelinConfig.LOG_STACKTRACE_KEY.equals(key))
        {
            ConfigUpdater.updateLogStacktrace(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.ALARM_EXCEPTION_KEY.equals(key))
        {
            ConfigUpdater.updateAlarmException(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.LOG_ARGS_KEY.equals(key))
        {
            ConfigUpdater.updateLogArgs(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.LOG_HTTP_SESSION_KEY.equals(key))
        {
            ConfigUpdater.updateLogHttpSession(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.LOG_RETURN_KEY.equals(key))
        {
            ConfigUpdater.updateLogReturn(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.LOG_MBEANINFO_KEY.equals(key))
        {
            ConfigUpdater.updateLogMBeanInfo(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.LOG_MBEANINFO_ROOT_KEY.equals(key))
        {
            ConfigUpdater.updateLogMBeanInfoRoot(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.EVENT_LEVEL_KEY.equals(key))
        {
            ConfigUpdater.updateEventLevel(value);
        }
        else if (JavelinConfig.ARGS_DETAIL_KEY.equals(key))
        {
            ConfigUpdater.updateArgsDetail(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.ARGS_DETAIL_DEPTH_KEY.equals(key))
        {
            ConfigUpdater.updateArgsDetailDepth(Integer.parseInt(value));
        }
        else if (JavelinConfig.RETURN_DETAIL_KEY.equals(key))
        {
            ConfigUpdater.updateReturnDetail(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.RETURN_DETAIL_DEPTH_KEY.equals(key))
        {
            ConfigUpdater.updateReturnDetailDepth(Integer.parseInt(value));
        }
        else if (JavelinConfig.HTTP_SESSION_DETAIL_KEY.equals(key))
        {
            ConfigUpdater.updateHttpSessionDetail(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.HTTP_SESSION_DETAIL_DEPTH_KEY.equals(key))
        {
            ConfigUpdater.updateHttpSessionDetailDepth(Integer.parseInt(value));
        }
        else if (JavelinConfig.THREADMODEL_KEY.equals(key))
        {
            ConfigUpdater.updateThreadModel(Integer.parseInt(value));
        }
        else if (JavelinConfig.ALARM_CPUTHRESHOLD.equals(key))
        {
            ConfigUpdater.updateAlarmCpuThreshold(Long.parseLong(value));
        }
        else if (JavelinConfig.NET_INPUT_MONITOR.equals(key))
        {
            ConfigUpdater.updateNetInputMonitor(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.NET_OUTPUT_MONITOR.equals(key))
        {
            ConfigUpdater.updateNetOutputMonitor(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.FILE_INPUT_MONITOR.equals(key))
        {
            ConfigUpdater.updateFileInputMonitor(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.FILE_OUTPUT_MONITOR.equals(key))
        {
            ConfigUpdater.updateFileOutputMonitor(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.FINALIZATION_COUNT_MONITOR.equals(key))
        {
            ConfigUpdater.updateFinalizationCount(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.INTERVAL_ERROR_MONITOR.equals(key))
        {
            ConfigUpdater.updateIntervalMonitor(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.HTTP_SESSION_COUNT_MONITOR.equals(key))
        {
            ConfigUpdater.updateHttpSessionCount(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.HTTP_SESSION_SIZE_MONITOR.equals(key))
        {
            ConfigUpdater.updateHttpSessionSize(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.CALL_TREE_ENABLE_KEY.equals(key))
        {
            ConfigUpdater.updateCallTreeEnabled(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.CALL_TREE_MAX_KEY.equals(key))
        {
            ConfigUpdater.updateCallTreeMaxSize(Integer.parseInt(value));
        }
        else if (JavelinConfig.LOG4J_PRINTSTACK_LEVEL.equals(key))
        {
            ConfigUpdater.updateLog4jPrintStackLevel(value);
        }
        else if (JavelinConfig.COLLECTION_MONITOR.equals(key))
        {
            ConfigUpdater.updateCollectionMonitor(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.COLLECTION_SIZE_THRESHOLD.equals(key))
        {
            ConfigUpdater.updateCollectionSizesThreshold(Integer.parseInt(value));
        }
        else if (JavelinConfig.LEAK_COLLECTIONSIZE_OUT.equals(key))
        {
            ConfigUpdater.updateLeakCollectionSizePrint(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.CLASS_HISTO.equals(key))
        {
            ConfigUpdater.updateClassHisto(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.CLASS_HISTO_INTERVAL.equals(key))
        {
            ConfigUpdater.updateClassHistoInterval(Integer.parseInt(value));
        }
        else if (JavelinConfig.CLASS_HISTO_MAX.equals(key))
        {
            ConfigUpdater.updateClassHistoMax(Integer.parseInt(value));
        }
        else if (JavelinConfig.CLASS_HISTO_GC.equals(key))
        {
            ConfigUpdater.updateClassHistoGC(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.THREAD_MONITOR.equals(key))
        {
            ConfigUpdater.updateThreadMonitor(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.THREAD_MONITOR_INTERVAL.equals(key))
        {
            ConfigUpdater.updateThreadMonitorInterval(Long.parseLong(value));
        }
        else if (JavelinConfig.THREAD_MONITOR_DEPTH.equals(key))
        {
            ConfigUpdater.updateThreadMonitorDepth(Integer.parseInt(value));
        }
        else if (JavelinConfig.THREAD_BLOCK_THRESHOLD.equals(key))
        {
            ConfigUpdater.updateBlockThreshold(Long.parseLong(value));
        }
        else if (JavelinConfig.THREAD_BLOCKTIME_THRESHOLD.equals(key))
        {
            ConfigUpdater.updateBlockTimeThreshold(Long.parseLong(value));
        }
        else if (JavelinConfig.THREAD_BLOCK_THREADINFO_NUM.equals(key))
        {
            ConfigUpdater.updateBlockThreadInfoNum(Integer.parseInt(value));
        }
        else if (JavelinConfig.THREAD_DUMP_MONITOR.equals(key))
        {
            ConfigUpdater.updateThreadDumpMonitor(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.THREAD_DUMP_INTERVAL.equals(key))
        {
            ConfigUpdater.updateThreadDumpInterval(Integer.parseInt(value));
        }
        else if (JavelinConfig.THREAD_DUMP_THREAD.equals(key))
        {
            ConfigUpdater.updateThreadDumpNum(Integer.parseInt(value));
        }
        else if (JavelinConfig.THREAD_DUMP_CPU.equals(key))
        {
            ConfigUpdater.updateThreadDumpCpu(Integer.parseInt(value));
        }
        else if (JavelinConfig.FULLGC_MONITOR.equals(key))
        {
            ConfigUpdater.updateFullGCMonitor(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.FULLGC_THREASHOLD.equals(key))
        {
            ConfigUpdater.updateFullGCThreshold(Integer.parseInt(value));
        }
        else if (JavelinConfig.THREAD_DEADLOCK_MONITOR.equals(key))
        {
            ConfigUpdater.updateDeadLockMonitor(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.ALARM_MINIMUM_INTERVAL_KEY.equals(key))
        {
            ConfigUpdater.updateAlarmMinimumInterval(Long.parseLong(value));
        }
        else if (JavelinConfig.TAT_ENABLED_KEY.equals(key))
        {
            ConfigUpdater.updateTatEnabled(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.TAT_KEEP_TIME_KEY.equals(key))
        {
            ConfigUpdater.updateTatKeepTime(Long.parseLong(value));
        }
        else if (JavelinConfig.CONCURRENT_ENABLED_KEY.equals(key))
        {
            ConfigUpdater.updateConcurrentAccessMonitor(Boolean.valueOf(value));
        }
        else if (JavelinConfig.TIMEOUT_MONITOR.equals(key))
        {
            ConfigUpdater.updateTimeoutMonitor(Boolean.valueOf(value));
        }
        else if (JavelinConfig.LOG_JVN_FILE.equals(key))
        {
            ConfigUpdater.updateLogJvnFile(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.COLLECT_SYSTEM_RESOURCES.equals(key))
        {
            ConfigUpdater.updateCollectSystemResources(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.SEND_INVOCATION_FULL_EVENT.equals(key))
        {
            ConfigUpdater.updateSendInvocationFullEvent(Boolean.parseBoolean(value));
        }
        // JdbcJavelinが持つ設定の更新
        else if (JdbcJavelinConfig.JDBC_JAVELIN_ENABLED_KEY.equals(key))
        {
            ConfigUpdater.updateJdbcEnabled(Boolean.parseBoolean(value));
        }
        else if (JdbcJavelinConfig.RECORDEXECPLAN_KEY.equals(key))
        {
            ConfigUpdater.updateRecordExecPlan(Boolean.parseBoolean(value));
        }
        else if (JdbcJavelinConfig.EXECPLANTHRESHOLD_KEY.equals(key))
        {
            ConfigUpdater.updateExecPlanThreshold(Long.parseLong(value));
        }
        else if (JdbcJavelinConfig.RECORDDUPLJDBCCALL_KEY.equals(key))
        {
            ConfigUpdater.updateRecordDuplJdbcCall(Boolean.parseBoolean(value));
        }
        else if (JdbcJavelinConfig.RECORDBINDVAL_KEY.equals(key))
        {
            ConfigUpdater.updateRecordBindVal(Boolean.parseBoolean(value));
        }
        else if (JdbcJavelinConfig.STRINGLIMITLENGTH_KEY.equals(key))
        {
            ConfigUpdater.updateJdbcStringLimitLength(Long.parseLong(value));
        }
        else if (JdbcJavelinConfig.SQLCOUNT_MONITOR_KEY.equals(key))
        {
            ConfigUpdater.updateSqlcountMonitor(Boolean.parseBoolean(value));
        }
        else if (JdbcJavelinConfig.SQLCOUNT_KEY.equals(key))
        {
            ConfigUpdater.updateSqlcount(Long.parseLong(value));
        }
        else if (JdbcJavelinConfig.ORACLE_ALLOW_SQL_TRACE_KEY.equals(key))
        {
            ConfigUpdater.updateAllowSqlTraceForOracle(Boolean.parseBoolean(value));
        }
        else if (JdbcJavelinConfig.POSTGRES_VERBOSE_PLAN_KEY.equals(key))
        {
            ConfigUpdater.updateVerbosePlanForPostgres(Boolean.parseBoolean(value));
        }
        else if (JdbcJavelinConfig.RECORD_STACKTRACE_KEY.equals(key))
        {
            ConfigUpdater.updateRecordStackTrace(Boolean.parseBoolean(value));
        }
        else if (JdbcJavelinConfig.RECORD_STACKTRACE_THREADHOLD_KEY.equals(key))
        {
            ConfigUpdater.updateRecordStackTraceThreshold(Integer.parseInt(value));
        }
        else if (JdbcJavelinConfig.JDBC_JAVELIN_ENABLED_KEY.equals(key))
        {
            ConfigUpdater.updateJdbcJavelinEnabled(Boolean.parseBoolean(value));
        }
        else if (JavelinConfig.HTTP_STATUS_ERROR_KEY.equals(key))
        {
            ConfigUpdater.updateHttpStatusError(Boolean.parseBoolean(value));
        }
        JavelinConfigUtil.getInstance().update();
    }
    
    /**
     * 更新時刻を超えている更新要求を実行する。
     */
    public static void executeScheduledRequest()
    {
        List<String> removeList = new ArrayList<String>();
        synchronized (updateLatorMap__)
        {
            long currentTime = System.currentTimeMillis();
            for (ConfigUpdateRequest entry : updateLatorMap__.values())
            {
                if (entry.getUpdateTime() < currentTime)
                {
                    update(entry.getKey(), entry.getValue());
                    removeList.add(entry.getKey());
                }
            }
            
            for (String key : removeList)
            {
                updateLatorMap__.remove(key);
            }
        }
        
    }
}
