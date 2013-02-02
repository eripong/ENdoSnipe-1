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
package jp.co.acroquest.endosnipe.javelin.jdbc.common;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.config.JavelinConfigUtil;

/**
 * JDBCJavelinの設定を保持するクラス。
 * @author eriguchi
 */
public class JdbcJavelinConfig extends JavelinConfig
{
    /** 実行計画を取得するか否かを設定するキー。 */
    public static final String RECORDEXECPLAN_KEY =
            JavelinConfig.JAVELIN_PREFIX + "jdbc.recordExecPlan";
    
    /** SQL文のFull Scanを監視するか否かを設定するキー。 */ 
    public static final String FULLSCAN_MONITOR_KEY =
        JavelinConfig.JAVELIN_PREFIX + "jdbc.fullScan.monitor";

    /** JDBC呼出し重複出力を行うか否かを設定するキー。 */
    public static final String RECORDDUPLJDBCCALL_KEY =
            JavelinConfig.JAVELIN_PREFIX + "jdbc.recordDuplJdbcCall";

    /** バインド変数を取得するか否かを設定するキー。 */
    public static final String RECORDBINDVAL_KEY =
            JavelinConfig.JAVELIN_PREFIX + "jdbc.recordBindVal";

    /** 実行計画を取得する閾値を設定するキー。 */
    public static final String EXECPLANTHRESHOLD_KEY =
            JavelinConfig.JAVELIN_PREFIX + "jdbc.execPlanThreshold";

    /** バインド変数出力での文字列長制限を設定するキー。 */
    public static final String STRINGLIMITLENGTH_KEY =
            JavelinConfig.JAVELIN_PREFIX + "jdbc.stringLimitLength";

    /** 同一トランザクション内の同一SQL呼び出し回数超過の閾値を監視するか否かを設定するキー。 */
    public static final String SQLCOUNT_MONITOR_KEY =
            JavelinConfig.JAVELIN_PREFIX + "jdbc.sqlcount.monitor";

    /** 同一トランザクション内の同一SQL呼び出し回数超過の閾値。 */
    public static final String SQLCOUNT_KEY =
            JavelinConfig.JAVELIN_PREFIX + "jdbc.sqlcount";

    /** SQLトレース出力を行うか否かを設定するキー。 */
    public static final String ORACLE_ALLOW_SQL_TRACE_KEY =
            JavelinConfig.JAVELIN_PREFIX + "jdbc.oracle.allowSqlTrace";

    /** PostgreSQLで詳細な実行計画を取得するか否かを設定するキー。 */
    public static final String POSTGRES_VERBOSE_PLAN_KEY =
            JavelinConfig.JAVELIN_PREFIX + "jdbc.postgres.verbosePlan";

    /** 最大クエリ保存数を設定するキー。 */
    public static final String RECORD_STATEMENT_NUM_MAXIMUM_KEY =
            JavelinConfig.JAVELIN_PREFIX + "jdbc.record.statement.num.maximum";

    /** JDBCJavelinでスタックトレース出力のON/OFFを設定するキー。 */
    public static final String RECORD_STACKTRACE_KEY =
            JavelinConfig.JAVELIN_PREFIX + "jdbc.record.stackTrace";

    /** JDBCJavelinでスタックトレースを出力するための閾値。 */
    public static final String RECORD_STACKTRACE_THREADHOLD_KEY =
            JavelinConfig.JAVELIN_PREFIX + "jdbc.record.stacktraceThreashold";

    /** JDBCJavelinを有効にするかどうかのキー */
    public static final String JDBC_JAVELIN_ENABLED_KEY = JAVELIN_PREFIX + "jdbc.enable";

    /** 実行計画出力ON/OFFフラグのデフォルト値 */
    private static final boolean DEFAULT_RECORDEXECPLAN = false;
    
    /** SQL文のFull Scanを監視するか否かを設定する値のデフォルト値 */
    private static final boolean DEFAULT_FULLSCAN_MONITOR = true; 

    /** JDBC呼出し重複出力フラグのデフォルト値 */
    private static final boolean DEFAULT_RECORDDUPLJDBCCALL = false;

    /** バインド変数出力フラグのデフォルト値 */
    private static final boolean DEFAULT_RECORDBINDVAL = true;

    /** SQLの実行計画を記録する際の閾値のデフォルト値 */
    private static final long DEFAULT_EXECPLANTHRESHOLD = 0;

    /** バインド変数出力での文字列長制限のデフォルト値 */
    private static final long DEFAULT_STRINGLIMITLENGTH = 102400;

    /** 同一トランザクション内の同一SQL呼び出し回数超過の閾値を監視するか否かのデフォルト値 */
    private static final boolean DEFAULT_SQLCOUNT_MONITOR = true;

    /** 閾値のデフォルト */
    private static final int DEFAULT_SQLCOUNT = 20;
    
    /** SQLトレース出力フラグのデフォルト値 */
    private static final boolean DEFAULT_ORACLE_ALLOW_SQL_TRACE = false;

    /** PostgreSQL用詳細実行計画出力フラグのデフォルト値 */
    private static final boolean DEFAULT_POSTGRES_VERBOSE_PLAN = false;

    /** 最大クエリ保存数のデフォルト値 */
    private static final int DEFAULT_MAX_RECORD_STATEMENT_NUM_MAXIMUM = 256;

    /** JDBCJavelinでスタックトレース出力のデフォルト値 */
    private static final boolean DEF_RECORD_STACKTRACE = true;

    /** JDBCJavelinでスタックトレース出力のSQL実行時間のデフォルト値 */
    private static final int DEF_RECORD_STACKTRACE_THRESHOLD = 0;

    /** JDBCJavelinを有効にするかどうかのデフォルト値 */
    private static final boolean DEF_JDBC_JAVELIN_ENABLED = true;

    /** 実行計画を取得するか否かを設定するキー。 */
    private static boolean isRecordExecPlan_;
    
    /** SQL文のFull Scanを監視するか否かを設定するキー。*/
    private static boolean isFullScanMonitor_;

    /** JDBC呼出し重複出力を行うか否かを設定するキー。 */
    private static boolean isRecordDuplJdbcCall_;

    /** バインド変数を取得するか否かを設定するキー。 */
    private static boolean isRecordBindVal_;

    /** 実行計画を取得する閾値を設定するキー。 */
    private static long execPlanThreshold__;

    /** バインド変数出力での文字列長制限を設定するキー。 */
    private static long stringLimitLength_;

    /** 同一トランザクション内の同一SQL呼び出し回数超過の閾値を監視するか否か。 */
    private static boolean isSqlcountMonitor_;

    /**  同一トランザクション内の同一SQL呼び出し回数超過の閾値。 */
    private static long sqlcount_;

    /** SQLトレース出力を行うか否かを設定するキー。 */
    private static boolean isOracleAllowSqlTrace_;

    /** PostgreSQLで詳細な実行計画を取得するか否かを設定するキー。 */
    private static boolean isPostgresVerbosePlan_;

    /** 最大クエリ保存数を設定するキー。 */
    private static int recordStatementNumMaximum_;

    /** JDBCJavelinでスタックトレース出力のON/OFFを設定するキー。 */
    private static boolean isRecordStackTrace_;

    /** JDBCJavelinでスタックトレースを出力するための閾値。 */
    private static int recordStacktraceThreashold_;

    static
    {
        initialize();
    }

    /**
     * 設定値を初期化します。<br />
     *
     * このメソッドは、 JUnit コードからも呼ばれます。<br />
     * （ {@link jp.co.acroquest.test.util.JavelinTestUtil#camouflageJavelinConfig(Class, String)} ）
     */
    private static void initialize()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        isRecordExecPlan_ = configUtil.getBoolean(RECORDEXECPLAN_KEY, DEFAULT_RECORDEXECPLAN);
        isFullScanMonitor_ = configUtil.getBoolean(FULLSCAN_MONITOR_KEY, DEFAULT_FULLSCAN_MONITOR);
        isRecordDuplJdbcCall_ =
                configUtil.getBoolean(RECORDDUPLJDBCCALL_KEY, DEFAULT_RECORDDUPLJDBCCALL);
        isRecordBindVal_ = configUtil.getBoolean(RECORDBINDVAL_KEY, DEFAULT_RECORDBINDVAL);
        execPlanThreshold__ = configUtil.getLong(EXECPLANTHRESHOLD_KEY, DEFAULT_EXECPLANTHRESHOLD);
        stringLimitLength_ = configUtil.getLong(STRINGLIMITLENGTH_KEY, DEFAULT_STRINGLIMITLENGTH);
        isSqlcountMonitor_ = configUtil.getBoolean(SQLCOUNT_MONITOR_KEY, DEFAULT_SQLCOUNT_MONITOR);
        sqlcount_ = configUtil.getLong(SQLCOUNT_KEY, DEFAULT_SQLCOUNT);
        isOracleAllowSqlTrace_ =
                configUtil.getBoolean(ORACLE_ALLOW_SQL_TRACE_KEY, DEFAULT_ORACLE_ALLOW_SQL_TRACE);
        isPostgresVerbosePlan_ =
                configUtil.getBoolean(POSTGRES_VERBOSE_PLAN_KEY, DEFAULT_POSTGRES_VERBOSE_PLAN);
        recordStatementNumMaximum_ =
                configUtil.getInteger(RECORD_STATEMENT_NUM_MAXIMUM_KEY,
                                      DEFAULT_MAX_RECORD_STATEMENT_NUM_MAXIMUM);
        isRecordStackTrace_ = configUtil.getBoolean(RECORD_STACKTRACE_KEY, DEF_RECORD_STACKTRACE);
        recordStacktraceThreashold_ =
                configUtil.getInteger(RECORD_STACKTRACE_THREADHOLD_KEY,
                                      DEF_RECORD_STACKTRACE_THRESHOLD);
        isJdbcJavelinEnabled__ = configUtil.getBoolean(JDBC_JAVELIN_ENABLED_KEY, DEF_JDBC_JAVELIN_ENABLED);
    }

    /**
     * Oracleの実行計画の出力オプション。
     * "BASIC","SERIAL","TYPICAL","ALL"の何れかを指定する。
     * デフォルト値は"SERIAL"
     */
    private final String outputOption_;

    /** JDBC Javelinを使用するかどうか */
    private static boolean isJdbcJavelinEnabled__;

    /**
     * Javelinの設定オブジェクトを作成する。
     */
    public JdbcJavelinConfig()
    {
        this.outputOption_ = "SERIAL";
    }

    /** 
     * SQLの実行計画を記録する際の閾値を返す。
     * 値（ミリ秒）を下回る処理時間の呼び出し情報は記録しない。
     * デフォルト値は0。
     *
     * @return 閾値（ミリ秒）
     */
    public long getExecPlanThreshold()
    {
        return execPlanThreshold__;
    }

    /**
     * SQLの実行計画を記録する際の閾値を設定する。
     * 
     * @param execPlanThreshold 閾値（ミリ秒）
     */
    public void setExecPlanThreshold(final long execPlanThreshold)
    {
        execPlanThreshold__ = execPlanThreshold;
    }

    /** 
     * JDBC呼出し重複出力フラグを返す。
     * trueの場合はアプリからのStatement呼び出し及びDBMSに依存した
     * 内部的なStatement呼び出しの両者をログに出力。
     * デフォルトはfalse。
     *
     * @return JDBC呼出し重複出力フラグ
     */
    public boolean isRecordDuplJdbcCall()
    {
        return isRecordDuplJdbcCall_;
    }

    /**
     * JDBC呼出し重複出力フラグを設定する。
     * 
     * @param recordDuplJdbcCall JDBC呼出し重複出力フラグ
     */
    public void setRecordDuplJdbcCall(final boolean recordDuplJdbcCall)
    {
        isRecordDuplJdbcCall_ = recordDuplJdbcCall;
    }

    /** 
     * 実行計画出力ON/OFFフラグを返す。
     *
     * @return 実行計画取得フラグ
     */
    public boolean isRecordExecPlan()
    {
        return isRecordExecPlan_;
    }

    /**
     * JDBC呼出し重複出力フラグを設定する。
     * 
     * @param recordExecPlan 実行計画取得フラグ
     */
    public void setRecordExecPlan(final boolean recordExecPlan)
    {
        isRecordExecPlan_ = recordExecPlan;
    }
    
    /** 
     * SQL文のFull Scanを監視するかどうのフラグを返す。
     *
     * @return 実行計画取得フラグ
     */
    public boolean isFullScanMonitor()
    {
        return isFullScanMonitor_;
    }

    /** 
     * SQL文のFull Scanを監視するかどうのフラグをセットする。
     * 
     * @param isFullScanMonitor Full Scan監視フラグ
     */
    public void setIsFullScanMonitor(boolean isFullScanMonitor)
    {
        JdbcJavelinConfig.isFullScanMonitor_ = isFullScanMonitor;
    }

    /** 
     * バインド変数出力フラグを返す。
     * trueの場合はPreparedStatementのバインド変数をログに出力。
     * デフォルトはtrue。
     *
     * @return バインド変数出力フラグ
     */
    public boolean isRecordBindVal()
    {
        return isRecordBindVal_;
    }

    /**
     * バインド変数出力フラグを設定する。
     * 
     * @param recordBindVal 実行計画取得フラグ
     */
    public void setRecordBindVal(final boolean recordBindVal)
    {
        isRecordBindVal_ = recordBindVal;
    }

    /**
     * Oracleの実行計画の出力オプションを返す。
     *
     * @return 出力オプション
     */
    public String getOutputOption()
    {
        return this.outputOption_;
    }

    /**
     * バインド変数出力における文字列長制限を返す。
     * setStringおよびsetObjectメソッドで指定されたバインド変数を文字列化する
     * 際の最大文字列長。
     * デフォルト値は64。
     *
     * @return 最大文字列長
     */
    public long getJdbcStringLimitLength()
    {
        return stringLimitLength_;
    }

    /**
     * バインド変数出力における文字列長制限を設定する。
     * 
     * @param jdbcStringLimitLength 最大文字列長
     */
    public void setJdbcStringLimitLength(final long jdbcStringLimitLength)
    {
        stringLimitLength_ = jdbcStringLimitLength;
    }

    /**
     * 同一トランザクション内の同一SQL呼び出し回数超過の閾値を監視するか否かを返す。
     * 
     * @return 同一トランザクション内の同一SQL呼び出し回数超過の閾値を監視するか否か
     */
    public boolean isSqlcountMonitor()
    {
        return isSqlcountMonitor_;
    }

    /**
     * 同一トランザクション内の同一SQL呼び出し回数超過の閾値を監視するか否かを設定する。
     * 
     * @param sqlcountMonitor 同一トランザクション内の同一SQL呼び出し回数超過の閾値を監視するか否か
     */
    public void setSqlcountMonitor(final boolean sqlcountMonitor)
    {
        isSqlcountMonitor_ = sqlcountMonitor;
    }

    /**
     * 同一トランザクション内の同一SQL呼び出し回数超過の閾値を返す。
     *
     * @return 同一トランザクション内の同一SQL呼び出し回数超過の閾値
     */
    public long getSqlcount()
    {
        return sqlcount_;
    }

    /**
     * 同一トランザクション内の同一SQL呼び出し回数超過の閾値を設定する。
     * 
     * @param sqlcount 同一トランザクション内の同一SQL呼び出し回数超過の閾値
     */
    public void setSqlcount(final long sqlcount)
    {
        sqlcount_ = sqlcount;
    }

    /** 
     * Oracleに対するSQLトレースの出力指示フラグ。
     * trueの場合はスレッドの開始-終了間のSQLトレースを出力する。
     * デフォルトはfalse(出力しない)。
     *
     * @return SQLトレースの出力指示フラグ
     */
    public boolean isAllowSqlTraceForOracle()
    {
        return isOracleAllowSqlTrace_;
    }

    /**
     * Oracleに対するSQLトレースの出力指示フラグを設定する。
     * 
     * @param allowSqlTraceForOracle SQLトレースの出力指示フラグ
     */
    public void setAllowSqlTraceForOracle(final boolean allowSqlTraceForOracle)
    {
        isOracleAllowSqlTrace_ = allowSqlTraceForOracle;
    }

    /**
     * PostgreSQLに対する実行計画詳細取得フラグ。
     * trueの場合は実行計画の要約ではなく、内部表現全てを取得する。
     * falseの場合は要約のみを取得する。
     * 
     * @return PostgreSQLで実行計画詳細を取得する場合は、<code>true</code>
     */
    public boolean isVerbosePlanForPostgres()
    {
        return isPostgresVerbosePlan_;
    }

    /**
     * PostgreSQLに対する実行計画詳細取得フラグを設定する。
     * 
     * @param verbosePlanForPostgres SQLトレースの出力指示フラグ
     */
    public void setVerbosePlanForPostgres(final boolean verbosePlanForPostgres)
    {
        isPostgresVerbosePlan_ = verbosePlanForPostgres;
    }

    /**
     * クエリの最大保存数を取得する。
     * 
     * デフォルト値は1000。
     *
     * @return 最大文字列長
     */
    public int getRecordStatementNumMax()
    {
        return recordStatementNumMaximum_;
    }

    /**
     * クエリの最大保存数を設定する。
     * 
     * @param recordMaxStatementNum クエリの最大保存数
     */
    public void setRecordStatementNumMax(final int recordMaxStatementNum)
    {
        recordStatementNumMaximum_ = recordMaxStatementNum;
    }

    /**
     * JDBCJavelinでスタックトレースを出力するかどうかを返します。<br />
     * 
     * @return JDBCJavelinでスタックトレースを出力する場合、<code>true</code>
     */
    public boolean isRecordStackTrace()
    {
        return isRecordStackTrace_;
    }

    /**
     * JDBCJavelinでスタックトレースを出力するかどうかを返します。<br />
     * 
     * @param isRecordThreashold JDBCJavelinでスタックトレースを出力する場合、<code>true</code>
     */
    public void setRecordStackTrace(boolean isRecordThreashold)
    {
        isRecordStackTrace_ = isRecordThreashold;
    }

    /**
     * JDBCJavelinでスタックトレースを出力するSQL実行時間の閾値を返します。<br />
     * 
     * @return JDBCJavelinでスタックトレースを出力するSQL実行時間の閾値
     */
    public int getRecordStackTraceThreshold()
    {
        return recordStacktraceThreashold_;
    }

    /**
     * JDBCJavelinでスタックトレースを出力するSQL実行時間の閾値を設定します。<br />
     * 
     * @param stackTraceThreshold JDBCJavelinでスタックトレースを出力するSQL実行時間の閾値
     */
    public void setRecordStackTraceThreshold(int stackTraceThreshold)
    {
        recordStacktraceThreashold_ = stackTraceThreshold;
    }

    /**
     * JDBC Javelinが有効かどうかを取得する
     * 
     * @return JDBC Javelinが有効かどうか
     */
    public boolean isJdbcJavelinEnabled()
    {
        return isJdbcJavelinEnabled__;
    }

    /**
     * JDBC Javelinを使用するかどうかを設定する。
     * 
     * @param isJdbcJavelinEnabled JDBC Javelinを使用するかどうか
     */
    public void setJdbcJavelinEnabled(boolean isJdbcJavelinEnabled)
    {
        isJdbcJavelinEnabled__ = isJdbcJavelinEnabled;
    }
}
