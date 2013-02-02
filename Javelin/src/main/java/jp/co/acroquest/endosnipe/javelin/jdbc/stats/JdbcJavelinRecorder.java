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
package jp.co.acroquest.endosnipe.javelin.jdbc.stats;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.db.AbstractExecutePlanChecker;
import jp.co.acroquest.endosnipe.common.event.EventConstants;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.CallTree;
import jp.co.acroquest.endosnipe.javelin.CallTreeNode;
import jp.co.acroquest.endosnipe.javelin.CallTreeRecorder;
import jp.co.acroquest.endosnipe.javelin.Callback;
import jp.co.acroquest.endosnipe.javelin.MBeanManager;
import jp.co.acroquest.endosnipe.javelin.RecordStrategy;
import jp.co.acroquest.endosnipe.javelin.StatsJavelinRecorder;
import jp.co.acroquest.endosnipe.javelin.bean.Component;
import jp.co.acroquest.endosnipe.javelin.bean.ExcludeMonitor;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;
import jp.co.acroquest.endosnipe.javelin.converter.leak.monitor.CollectionMonitor;
import jp.co.acroquest.endosnipe.javelin.event.FullScanEvent;
import jp.co.acroquest.endosnipe.javelin.jdbc.common.JdbcJavelinConfig;
import jp.co.acroquest.endosnipe.javelin.jdbc.common.JdbcJavelinMessages;
import jp.co.acroquest.endosnipe.javelin.jdbc.common.SqlUtil;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.db2.DB2Processor;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.mysql.MySQLProcessor;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.oracle.OracleProcessor;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.oracle.OracleSessionStopCallback;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.postgres.PostgresProcessor;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.sqlserver.SQLServerProcessor;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;
import jp.co.acroquest.endosnipe.javelin.util.LinkedList;
import jp.co.acroquest.endosnipe.javelin.util.ThreadUtil;

/**
 * Jdbcのジャベリンログを記録する.
 * @author eriguchi
 *
 */
public class JdbcJavelinRecorder
{
    /** SQLの最初のインデックス. */
    private static final int         FIRST_SQL_INDEX       = 0;

    /** SQL処理時間のプレフィックス. */
    public static final String       TIME_PREFIX           = "[Time] ";

    /** バインド変数のプレフィックス. */
    public static final String       BIND_PREFIX           = "[VALUE] ";

    /** 実行計画のプレフィックス. */
    public static final String       PLAN_PREFIX           = "[PLAN] ";

    /** スタックトレースのプレフィックス. */
    public static final String       STACKTRACE_PREFIX     = "[STACKTRACE] ";

    private static final String      STACKTRACE_BASE       =
                                                             STACKTRACE_PREFIX
                                                                     + "Get a stacktrace." + '\n';

    /** 実行メソッドのパラメタのプレフィックス. */
    public static final String       PARAM_PREFIX          = "[ExecuteParam] ";
    
    /** javelin.jdbc.stringLimitLengthによって、SQL文が切り詰められた時に表示される記号. */
    private static final String      STRING_LIMITED_MARK   = "...";

    /** 実行計画取得に失敗した場合のメッセージ。 */
    public static final String       EXPLAIN_PLAN_FAILED   =
                                                             JdbcJavelinMessages.getMessage("javelin.jdbc.stats."
                                                                     + "JdbcJavelinRecorder.FailExplainPlanMessage");

    /** 設定値保持Bean */
    private static JdbcJavelinConfig config__;

    private static JavelinConfig     logArgsConfig__       = new JavelinConfig() {
                                                               public boolean isLogArgs()
                                                               {
                                                                   return true;
                                                               }
                                                           };

    /** 対象文字列が見つからないとき */
    public static final int          NOT_FOUND             = -1;

    /** 複数行コメントの開始を表す文字列の長さ */
    public static final int          COMMENT_FOOTER_LENGTH = "*/".length();

    /** 複数行コメントの終了を表す文字列の長さ */
    public static final int          COMMENT_HEADER_LENGTH = "/*".length();

    /** DBProcessorのリスト。 */
    private static List<DBProcessor> processorList__;

    static
    {
        config__ = new JdbcJavelinConfig();
        processorList__ = new ArrayList<DBProcessor>();

        processorList__.add(new OracleProcessor());
        processorList__.add(new PostgresProcessor());
        processorList__.add(new SQLServerProcessor());
        processorList__.add(new MySQLProcessor());
        processorList__.add(new DB2Processor());
    }

    /**
     * デフォルトコンストラクタ
     */
    private JdbcJavelinRecorder()
    {
        // Do Nothing.
    }

    /**
     * 前処理。(SQLがargsに指定されている場合)
     * 
     * @param stmt 対象となるStatement
     * @param args SQLのString配列
     */
    public static void preProcessSQLArgs(final Statement stmt, final Object[] args)
    {
        JdbcJvnStatus    jdbcJvnStatus    = JdbcJvnStatus.getInstance();
        CallTreeRecorder callTreeRecorder = jdbcJvnStatus.getCallTreeRecorder();
        CallTree         tree             = callTreeRecorder.getCallTree();
        if (tree.getRootNode() == null)
        {
            tree.loadConfig();
        }
        
        if (tree.isJdbcEnabled() == false)
        {
            return;
        }

        jdbcJvnStatus.setExecPlanSql(args);
        preProcess(stmt, args, jdbcJvnStatus);
    }

    /**
     * 前処理。(メソッドのパラメータがargsに指定されている場合)
     * 最初のパラメータをSQLとして扱う。
     * 
     * @param stmt 対象となるStatement
     * @param args メソッドのパラメータ
     */
    public static void preProcessParam(final Statement stmt, final Object[] args)
    {
        JdbcJvnStatus    jdbcJvnStatus    = JdbcJvnStatus.getInstance();
        CallTreeRecorder callTreeRecorder = jdbcJvnStatus.getCallTreeRecorder();
        CallTree         tree             = callTreeRecorder.getCallTree();
        
        if (tree.getRootNode() == null)
        {
            tree.loadConfig();
        }
        
        if (tree.isJdbcEnabled() == false)
        {
            return;
        }

        if (args != null && args.length > 0)
        {
            jdbcJvnStatus.setExecPlanSql(new Object[]{args[0]});
        }
        preProcess(stmt, args, jdbcJvnStatus);
    }

    /**
     * 前処理。
     * 
     * @param stmt ステートメント。
     * @param args 引数。
     * @param jdbcJvnStatus JDBC Javelinの状態
     */
    public static void preProcess(final Statement stmt, final Object[] args,
            JdbcJvnStatus jdbcJvnStatus)
    {
        try
        {
            if (jdbcJvnStatus.getCallDepth() == 0)
            {
                jdbcJvnStatus.clearPreprocessedDepthSet();
            }

            try
            {
                // 引数を拡張子、ロゴを埋め込む。
                // 実行計画取得中であれば、前処理・後処理を呼んではいけない。
                if (jdbcJvnStatus.getNowExpalaining() != null)
                {
                    return;
                }

                // JDBC呼出し重複出力フラグがOFFなら
                // 親ノードを削除して自分をツリーに追加する。
                // このコードは、StatsJavelinRecorder#preProcessを呼び出す前に行う。
                // そうしないと、ルートの場合にVMStatusが格納されないため。
                CallTreeRecorder callTreeRecorder = jdbcJvnStatus.getCallTreeRecorder();
                CallTree tree = callTreeRecorder.getCallTree();

                // セッション終了処理に入っている場合は、処理しない。
                if (tree != null && (config__.isAllowSqlTraceForOracle() // 
                        && (tree.containsFlag(SqlTraceStatus.KEY_SESSION_CLOSING) //
                        || tree.containsFlag(SqlTraceStatus.KEY_SESSION_INITIALIZING))))
                {
                    return;
                }

                recordPre(stmt, args, jdbcJvnStatus);
            }
            finally
            {
                jdbcJvnStatus.incrementCallDepth();
            }
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
    }

    /**
     * 前処理。
     * 
     * @param stmt ステートメント。
     * @param args 引数。
     * @param jdbcJvnStatus JDBC Javelinの状態
     */
    public static void recordPre(final Statement stmt, final Object[] args,
            JdbcJvnStatus jdbcJvnStatus)
    {
        try
        {
            String jdbcUrl = "DB-Server";
            Connection connection = stmt.getConnection();
            JdbcJavelinConnection jvnConnection = null;
            if (connection != null)
            {
                jvnConnection = (JdbcJavelinConnection)connection;
                jdbcUrl = getJdbcUrl(connection, jvnConnection);
            }
            String className = jdbcUrl;
            String methodName = null;
            Boolean noSql;
            if (args.length <= FIRST_SQL_INDEX)
            {
                noSql = Boolean.TRUE;
                jdbcJvnStatus.setNoSql(noSql);
                return;
            }

            methodName = ((String)args[FIRST_SQL_INDEX]);
            
            // SQL文を、javelin.jdbc.stringLimitLengthで設定した長さに切り詰める。
            int stringLimitLength = (int)config__.getJdbcStringLimitLength();
            if (stringLimitLength < methodName.length())
            {
                methodName = methodName.substring(0, stringLimitLength) + STRING_LIMITED_MARK;
            }
            
            noSql = Boolean.FALSE;
            jdbcJvnStatus.setNoSql(noSql);

            Component component = getComponent(className);
            Invocation invocation = StatsJavelinRecorder.getInvocation(component, methodName);
            if (invocation == null)
            {
                invocation =
                             StatsJavelinRecorder.registerInvocation(component, methodName,
                                                                       config__, false);
            }

            boolean isTarget = ExcludeMonitor.isMeasurementTarget(invocation);
            if (isTarget == false)
            {
                return;
            }
            if (config__.isRecordDuplJdbcCall() == false)
            {
                CallTreeRecorder callTreeRecorder = jdbcJvnStatus.getCallTreeRecorder();
                CallTree callTree = callTreeRecorder.getCallTree();
                execNoDuplicateCall(jdbcJvnStatus, callTreeRecorder, callTree);
            }

            // StatsJavelinRecorderに処理を委譲する
            StatsJavelinRecorder.preProcess(component, invocation, args, logArgsConfig__, true);
            jdbcJvnStatus.savePreprocessDepth();

            DBProcessor processor = getProcessor(jdbcUrl, jvnConnection);
            onExecStatement(processor, connection, jdbcJvnStatus);
        }
        catch (Exception ex)
        {
            // 想定外の例外が発生した場合は標準エラー出力に出力しておく。
            SystemLogger.getInstance().warn(ex);
        }
    }

    private static String getJdbcUrl(Connection connection, JdbcJavelinConnection jvnConnection)
        throws SQLException
    {
        String jdbcUrl;
        jdbcUrl = jvnConnection.getJdbcUrl();
        if (jdbcUrl == null)
        {
            DatabaseMetaData metaData = connection.getMetaData();
            if (metaData != null)
            {
                jdbcUrl = metaData.getURL();
                jvnConnection.setJdbcUrl(jdbcUrl);
            }
        }
        return jdbcUrl;
    }

    private static Component getComponent(String className)
    {
        Component component = MBeanManager.getComponent(className);
        if (component == null)
        {
            component = new Component(className);
            Component oldComponent = MBeanManager.setComponent(className, component);
            if (oldComponent != null)
            {
                component = oldComponent;
            }
        }
        return component;
    }

    private static DBProcessor getProcessor(String jdbcUrl, JdbcJavelinConnection jvnConnection)
    {
        DBProcessor processor;
        if (jvnConnection != null)
        {
            processor = jvnConnection.getJdbcJavelinProcessor();
            if (processor == null)
            {
                processor = getProcessor(jdbcUrl);
                jvnConnection.setJdbcJavelinProcessor(processor);
            }
        }
        else
        {
            processor = getProcessor(jdbcUrl);
        }
        return processor;
    }

    private static void execNoDuplicateCall(JdbcJvnStatus jdbcJvnStatus,
            CallTreeRecorder callTreeRecorder, CallTree tree)
    {
        int depth = jdbcJvnStatus.incrementDepth();
        if (depth > 1)
        {
            CallTreeNode node = callTreeRecorder.getCallTreeNode();
            CallTreeNode parent = node.getParent();
            if (parent != null)
            {
                callTreeRecorder.removeChildNode(parent, node);
            }
            else
            {
                // 親ノードがルートの場合は、ルートを null にする
                tree.setRootNode(null);
            }
            callTreeRecorder.setCallerNode(parent);
        }
    }

    private static void onExecStatement(final DBProcessor processor, final Connection connection,
            final JdbcJvnStatus jdbcJvnStatus)
    {
        if (processor == null)
        {
            return;
        }

        CallTree tree = jdbcJvnStatus.getCallTreeRecorder().getCallTree();

        // 対象データベースがOracleで、
        // SQLトレースフラグが設定されており、
        // かつ、セッションではじめてのSQL実行であれば、
        // SQLトレースを開始する。
        if (JdbcJavelinRecorder.config__.isAllowSqlTraceForOracle()
                && processor instanceof OracleProcessor
                && tree.containsFlag(SqlTraceStatus.KEY_SESSION_INITIALIZING) == false
                && tree.containsFlag(SqlTraceStatus.KEY_SESSION_STARTED) == false)
        {
            // 「SQLトレース初期化」に遷移する。
            tree.removeFlag(SqlTraceStatus.KEY_SESSION_CLOSING);
            tree.removeFlag(SqlTraceStatus.KEY_SESSION_FINISHED);
            tree.setFlag(SqlTraceStatus.KEY_SESSION_INITIALIZING,
                         SqlTraceStatus.KEY_SESSION_INITIALIZING);

            Callback callback = new OracleSessionStopCallback(connection);
            tree.addCallback(callback);
            processor.startSqlTrace(connection);

            // 「SQLトレース取得中」に遷移する。
            tree.removeFlag(SqlTraceStatus.KEY_SESSION_INITIALIZING);
            tree.setFlag(SqlTraceStatus.KEY_SESSION_STARTED, SqlTraceStatus.KEY_SESSION_STARTED);
        }
    }

    /**
     * 後処理（本処理成功時）。
     * 
     * @param stmt Statementオブジェクト
     * @param paramNum パラメータの数（0:パラメータなし、1:パラメータ1以上）
     */
    public static void postProcessOK(final Statement stmt, final int paramNum)
    {
        try
        {
            JdbcJvnStatus jdbcJvnStatus = JdbcJvnStatus.getInstance();
            jdbcJvnStatus.decrementCallDepth();

            // 実行計画取得中であれば、前処理・後処理は行わない。
            if (jdbcJvnStatus.getNowExpalaining() != null)
            {
                return;
            }

            try
            {
                boolean result = ignore(jdbcJvnStatus);
                if (result == true)
                {
                    jdbcJvnStatus.removePreProcessDepth();
                    return;
                }
            }
            catch (Exception ex)
            {
                SystemLogger.getInstance().warn(ex);
            }

            if (jdbcJvnStatus.isPreprocessDepth() == false)
            {
                return;
            }
            jdbcJvnStatus.removePreProcessDepth();

            // SQLトレース取得中状態以外の場合は、実行計画は取得しない。
            CallTreeRecorder callTreeRecorder = jdbcJvnStatus.getCallTreeRecorder();
            CallTree tree = callTreeRecorder.getCallTree();
            if (tree == null || (config__.isAllowSqlTraceForOracle() //
                    && (tree.containsFlag(SqlTraceStatus.KEY_SESSION_INITIALIZING) //
                            || tree.containsFlag(SqlTraceStatus.KEY_SESSION_CLOSING) //
                    || tree.containsFlag(SqlTraceStatus.KEY_SESSION_FINISHED))))
            {
                return;
            }

            recordPostOK(stmt, paramNum, jdbcJvnStatus);

        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
    }

    /**
     * 後処理（本処理成功時）。
     * 
     * @param stmt Statementオブジェクト
     * @param paramNum パラメータの数（0:パラメータなし、1:パラメータ1以上）
     * @param jdbcJvnStatus jdbcJvnStatus
     */
    public static void recordPostOK(final Statement stmt, final int paramNum,
            JdbcJvnStatus jdbcJvnStatus)
    {
        CallTreeRecorder callTreeRecorder = jdbcJvnStatus.getCallTreeRecorder();
        CallTree tree = callTreeRecorder.getCallTree();
        CallTreeNode node = null;
        try
        {
            // 呼び出し元情報取得。
            node = callTreeRecorder.getCallTreeNode();

            // オリジナルのargsへの参照をローカル変数に一時保存
            String[] oldArgs = node.getArgs();
            if (oldArgs == null)
            {
                oldArgs = new String[0];
            }

            // クエリ時間算出、args[0]に入れる
            long queryTime = calcQueryTime(stmt, paramNum, node, oldArgs);

            // 再構成したargsを一時的に入れるList
            List<String> tempArgs = new LinkedList<String>();

            tempArgs.add(TIME_PREFIX + queryTime);

            // JDBC接続URL取得
            Connection connection = stmt.getConnection();
            JdbcJavelinConnection jvnConnection = null;
            if (connection != null)
            {
                jvnConnection = (JdbcJavelinConnection)connection;
            }

            // SQL呼び出し回数をrootのCallTreeNodeに保持する
            if (config__.isSqlcountMonitor())
            {
                RecordStrategy rs = getRecordStrategy(tree, EventConstants.NAME_SQLCOUNT);
                if (rs != null && rs instanceof SqlCountStrategy && oldArgs.length > 0)
                {
                    // SQLCountStrategyのSQL呼び出し回数を増加させる
                    SqlCountStrategy strategy = (SqlCountStrategy)rs;
                    strategy.incrementSQLCount(oldArgs[0]);
                }
            }

            DBProcessor processor = null;
            String jdbcUrl = null;
            if (jvnConnection != null)
            {
                jdbcUrl = jvnConnection.getJdbcUrl();
                processor = jvnConnection.getJdbcJavelinProcessor();
                if (processor == null)
                {
                    processor = getProcessor(jdbcUrl);
                    jvnConnection.setJdbcJavelinProcessor(processor);
                }
            }

            // 以下の３つの条件を満たすときのみ、スタックトレースを取得する。
            // 1.実行計画取得対応DBである。
            // 2.クエリ時間が閾値を超えている。
            // 3.実行計画取得フラグがONである。
            if (processor != null && queryTime >= config__.getExecPlanThreshold()
                    && config__.isRecordExecPlan())
            {

                long startTime = System.currentTimeMillis();
                // 実行計画取得
                List<String> newArgs =
                                       getExecPlan(tree, node, processor, jdbcUrl, oldArgs, stmt,
                                                   paramNum, jdbcJvnStatus);
                jdbcJvnStatus.setExecPlanSql(null);
                // argsに追加
                tempArgs.addAll(newArgs);

                long endTime = System.currentTimeMillis();
                node.addJavelinTime(endTime - startTime);
            }
            else
            {
                // 実行計画取得しないなら、SQL文にプレフィックスを付与するのみ。
                addPrefix(stmt, paramNum, tempArgs, oldArgs);
            }

            // スタックトレース取得フラグがONである、かつクエリ時間が閾値を超えているとき、スタックトレースを取得する。
            if (config__.isRecordStackTrace()
                    && queryTime >= config__.getRecordStackTraceThreshold())
            {
                tempArgs.add(getStackTrace());
            }

            // 再構築したargsをnodeにセット
            node.setArgs(tempArgs.toArray(new String[tempArgs.size()]));
        }
        catch (Exception ex)
        {
            // 想定外の例外が発生した場合は標準エラー出力に出力しておく。
            SystemLogger.getInstance().warn(ex);
        }
        finally
        {
            StatsJavelinRecorder.postProcess(null, null, (Object)null, config__, true);
            jdbcJvnStatus.setExecPlanSql(null);
        }

    }

    /**
     * Full Scanイベントを生成し、登録します。
     * 
     * @param processor DBごとのプロセッサ
     * @param newArgs 実行計画
     * @param stmt Statementオブジェクト
     * @param paramNum パラメータ数（0:パラメータなし、1:パラメータ1以上）
     * @param node CallTreeNode
     * @param execPlanSql SQL文
     * @param resultText 実行計画
     */
    private static void sendFullScanEvent(DBProcessor processor,
            List<String> newArgs, final Statement stmt, final int paramNum,
            final CallTreeNode node, String[] execPlanSql)
    {
        try
        {
            AbstractExecutePlanChecker<?> executeChecker = processor.getExecutePlanChecker();
            if (executeChecker != null)
            {
                String executePlan = executeChecker.parseExecutePlan(newArgs);
                
                // フルスキャンの判定中は、CollectionのトレースをOFFにする。
                Boolean prevTracing = CollectionMonitor.isTracing();
                CollectionMonitor.setTracing(Boolean.FALSE);
                Set<String> fullScanTableNameSet;
                try
                {
                    fullScanTableNameSet =
                                           executeChecker.getFullScanTableNameSet(executePlan, null);
                }
                finally
                {
                    CollectionMonitor.setTracing(prevTracing);
                }
                
                if (0 < fullScanTableNameSet.size())
                {
                    // イベントパラメータをセットする。
                    FullScanEvent event = new FullScanEvent();
                    
                    // テーブル名とSQL実行時間は常に出力する。
                    String fullScanTableNames = fullScanTableNameSet.toString();
                    fullScanTableNames = fullScanTableNames
                            .substring(1, fullScanTableNames.length() - 1);
                    event.addParam(EventConstants.PARAM_FULL_SCAN_TABLE_NAME,
                                   fullScanTableNames);
                    String[] oldArgs = node.getArgs();
                    if (oldArgs == null)
                    {
                        oldArgs = new String[0];
                    }
                    long queryTime = calcQueryTime(stmt, paramNum, node, oldArgs);
                    event.addParam(EventConstants.PARAM_FULL_SCAN_DURATION,
                                   String.valueOf(queryTime));
                    
                    // コールツリーを使用するモードの場合。
                    if (logArgsConfig__.isCallTreeEnabled())
                    {
                        // スタックトレース取得を行わない場合、
                        // または取得を行う場合でも、その閾値に達していない場合、スタックトレースを出力する。
                        if (config__.isRecordStackTrace() == false
                                || config__.getRecordStackTraceThreshold() < queryTime)
                        {
                            
                            event.addParam(EventConstants.PARAM_FULL_SCAN_STACK_TRACE,
                                           getStackTrace());
                        }
                    }
                    // コールツリーを使用しないモードの場合。
                    else
                    {
                        // 実行計画の内容、スタックトレースともに出力する。
                        event.addParam(EventConstants.PARAM_FULL_SCAN_EXEC_PLAN,
                                       newArgs.toString());
                        event.addParam(EventConstants.PARAM_FULL_SCAN_STACK_TRACE,
                                       getStackTrace());
                    }
                    
                    StatsJavelinRecorder.addEvent(event);
                }
            }
        }
        catch (Exception ex)
        {
            // 想定外の例外が発生した場合は標準エラー出力に出力しておく。
            SystemLogger.getInstance().warn(ex);
        }
    }

    private static void saveExecPlan(CallTreeNode node, String[] execPlan,
            JdbcJvnStatus jdbcJvnStatus)
    {
        CallTreeRecorder callTreeRecorder = jdbcJvnStatus.getCallTreeRecorder();
        SqlPlanStrategy sqlPlanRecordStrategy = getSqlPlanStrategy(callTreeRecorder.getCallTree());
        if (sqlPlanRecordStrategy == null || execPlan == null || execPlan.length == 0)
        {
            return;
        }

        sqlPlanRecordStrategy.setExecPlan(node, execPlan);

        // 実行計画出力用のSQLを保存する。
        String sql = node.getInvocation().getMethodName();
        sqlPlanRecordStrategy.recordPlanOutputSql(sql);
    }

    private static SqlPlanStrategy getSqlPlanStrategy(CallTree callTree)
    {
        RecordStrategy recordStrategy = getRecordStrategy(callTree, SqlPlanStrategy.KEY);
        SqlPlanStrategy sqlPlanRecordStrategy = null;
        if (recordStrategy instanceof SqlPlanStrategy)
        {
            sqlPlanRecordStrategy = (SqlPlanStrategy)recordStrategy;
        }
        return sqlPlanRecordStrategy;
    }

    private static String[] getPrevExecPlan(CallTree tree, CallTreeNode node)
    {
        SqlPlanStrategy sqlPlanRecordStrategy = getSqlPlanStrategy(tree);
        if (sqlPlanRecordStrategy == null)
        {
            return null;
        }

        return sqlPlanRecordStrategy.getExecPlan(node);
    }

    /**
     *　SQLの実行計画を一度取得した後、一定時間経過したかどうかを判定する。
     * 
     * @param node ノード。
     * @return SQLの実行計画を一度取得した後、一定時間経過したかどうか。 
     */
    private static boolean isRecordIntervalExpired(CallTreeNode node, JdbcJvnStatus jdbcJvnStatus)
    {
        boolean expired = false;

        CallTreeRecorder callTreeRecorder = jdbcJvnStatus.getCallTreeRecorder();
        SqlPlanStrategy sqlPlanRecordStrategy = getSqlPlanStrategy(callTreeRecorder.getCallTree());
        if (sqlPlanRecordStrategy == null)
        {
            return expired;
        }
        String sql = node.getInvocation().getMethodName();
        expired = !sqlPlanRecordStrategy.existPlanOutputSql(sql);
        return expired;
    }

    private static RecordStrategy getRecordStrategy(CallTree callTree, String strategyKey)
    {
        RecordStrategy rs = callTree.getRecordStrategy(strategyKey);
        if (rs == null)
        {
            if (EventConstants.NAME_SQLCOUNT.equals(strategyKey))
            {
                // SQLCountStrategyが登録されていない場合は、新規に登録する
                rs = new SqlCountStrategy();
                callTree.addRecordStrategy(strategyKey, rs);
            }
            else if (SqlPlanStrategy.KEY.equals(strategyKey))
            {
                // SQLPlanStrategyが登録されていない場合は、新規に登録する
                rs = new SqlPlanStrategy();
                callTree.addRecordStrategy(strategyKey, rs);
            }
        }
        return rs;
    }

    /**
     * スタックトレースを取得する。
     * @return スタックトレース
     */
    private static String getStackTrace()
    {
        StackTraceElement[] stacktraces = ThreadUtil.getCurrentStackTrace();

        StringBuilder builder = new StringBuilder(STACKTRACE_BASE);
        builder.append(ThreadUtil.getStackTrace(stacktraces, stacktraces.length));
        return builder.toString();
    }

    private static long calcQueryTime(final Statement stmt, final int paramNum,
            final CallTreeNode node, final String[] oldArgs)
        throws Exception
    {
        long queryTime = System.currentTimeMillis() - node.getStartTime();

        // バッチ実行の場合は、平均時間を算出する
        int addBatchCount = 1;
        if (stmt instanceof PreparedStatement)
        {
            // PreparedStatementの場合は、バインド変数のインデックスを元に、
            // バッチ処理の数を得る
            addBatchCount = SqlUtil.getPreparedStatementAddBatchCount(stmt);
        }
        else
        {
            // 普通のStatementの場合は引数にSQL配列が入っているので、
            // そこから数を得る
            // （paramNum が 0 ならバッチ実行）
            if (paramNum == 0)
            {
                addBatchCount = oldArgs.length;
            }
        }
        if (addBatchCount >= 2)
        {
            // 平均時間を計算
            queryTime /= addBatchCount;
        }
        return queryTime;
    }

    private static boolean ignore(JdbcJvnStatus jdbcJvnStatus)
    {
        boolean result = false;
        if (Boolean.TRUE.equals(jdbcJvnStatus.isNoSql()))
        {
            Boolean noSql = Boolean.FALSE;
            jdbcJvnStatus.setNoSql(noSql);
            result = true;
        }
        else
        {
            // JDBC呼出し重複出力フラグがOFF、かつ最深ノードでなければ、
            // 何もしない。
            int depth = jdbcJvnStatus.getDepth();
            if (config__.isRecordDuplJdbcCall() == false && depth > 0)
            {
                jdbcJvnStatus.decrementDepth();
                if (depth < jdbcJvnStatus.getDepthMax())
                {
                    result = true;
                }
            }
        }

        return result;
    }

    private static void addPrefix(final Statement stmt, final int paramNum,
            final List<String> tempArgs, final String[] oldArgs)
    {
        // バインド変数出力フラグがONなら、バインド変数出力文字列を作成する。
        List<?> bindList = null;
        if (config__.isRecordBindVal())
        {
            bindList = SqlUtil.getJdbcJavelinBindValByRef(stmt);
        }

        if (paramNum == 1)
        {
            String bindVals = SqlUtil.getBindValCsv(bindList, 0);
            if (bindVals != null)
            {
                tempArgs.add(BIND_PREFIX + bindVals);
            }
            for (int count = 1; count < oldArgs.length; count++)
            {
                tempArgs.add(PARAM_PREFIX + oldArgs[count]);
            }
        }
        else
        {
            // SQL（args全て）にプレフィックスを付与
            for (int count = 0; count < oldArgs.length; count++)
            {
                String bindVals = SqlUtil.getBindValCsv(bindList, count);
                if (bindVals != null)
                {
                    tempArgs.add(BIND_PREFIX + bindVals);
                }

            }
        }
    }

    /**
     * 後処理（本処理失敗時）。
     * 
     * @param cause 原因
     */
    public static void postProcessNG(final Throwable cause)
    {
        try
        {
            JdbcJvnStatus    jdbcJvnStatus    = JdbcJvnStatus.getInstance();
            CallTreeRecorder callTreeRecorder = jdbcJvnStatus.getCallTreeRecorder();
            CallTree         tree             = callTreeRecorder.getCallTree();
            
            if (tree.isJdbcEnabled() == false)
            {
                return;
            }

            jdbcJvnStatus.decrementCallDepth();

            // 実行計画取得中であれば、前処理・後処理は行わない。
            if (jdbcJvnStatus.getNowExpalaining() != null)
            {
                return;
            }

            // セッション終了処理に入っている場合は、実行計画は取得しない。
            if ((config__.isAllowSqlTraceForOracle() //
                    && (tree.containsFlag(SqlTraceStatus.KEY_SESSION_INITIALIZING) //
                            || tree.containsFlag(SqlTraceStatus.KEY_SESSION_CLOSING) //
                    || tree.containsFlag(SqlTraceStatus.KEY_SESSION_FINISHED))))
            {
                return;
            }

            if (jdbcJvnStatus.isPreprocessDepth() == false)
            {
                return;
            }
            jdbcJvnStatus.removePreProcessDepth();

            try
            {
                // 親ノードが"DB-Server"、かつJDBC呼出し重複出力フラグがOFFなら処理を終了。
                if (ignore(jdbcJvnStatus))
                {
                    return;
                }
            }
            catch (Exception ex)
            {
                SystemLogger.getInstance().warn(ex);
            }

            recordPostNG(cause, jdbcJvnStatus);
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
    }

    /**
     * 後処理（本処理失敗時）。
     * 
     * @param cause 原因
     * @param jdbcJvnStatus jdbcJvnStatus
     */
    public static void recordPostNG(final Throwable cause, JdbcJvnStatus jdbcJvnStatus)
    {
        // JavelinRecorderに処理委譲
        CallTreeRecorder callTreeRecorder = jdbcJvnStatus.getCallTreeRecorder();
        CallTreeNode node = callTreeRecorder.getCallTreeNode();
        if (node != null)
        {
            StatsJavelinRecorder.postProcess(null, null, cause, config__, true);
            jdbcJvnStatus.setExecPlanSql(null);
        }
    }

    /**
     * 実行計画取得
     * @param callTree CallTree
     * @param node CallTreeNode
     * @param jdbcUrl 接続URL
     * @param args nodeにセットされたargs
     * @param stmt Statementオブジェクト
     * @param paramNum パラメータ数（0:パラメータなし、1:パラメータ1以上）
     * 
     * @return　SQLの実行計画
     */
    private static List<String> getExecPlan(CallTree callTree, CallTreeNode node,
            final DBProcessor processor, final String jdbcUrl, final String[] args,
            final Statement stmt, final int paramNum, JdbcJvnStatus jdbcJvnStatus)
    {
        // 結果リスト（nodeに登録しなおすargs）
        List<String> resultText = new LinkedList<String>();

        // 実行計画取得
        try
        {
            // 実行計画取得中の状態を設定
            jdbcJvnStatus.setNowExpalaining(stmt);

            // argsがなければ(SQLが登録されていない)、実行計画は取得しない
            if (args == null || args.length == 0)
            {
                return resultText;
            }

            // 実行計画を取得すべきSQL文を配列化
            // paramNumが1ならargsの1つめがSQL文。0ならすべてがSQL文。
            String[] originalSql = null;
            String[] execPlanSql = jdbcJvnStatus.getExecPlanSql();
            if (execPlanSql == null)
            {
                String key = "javelin.jdbc.stats.JdbcJavelinRecorder.NotSavedExecPlanSQLMessage";
                String message = JdbcJavelinMessages.getMessage(key);
                SystemLogger.getInstance().warn(message);
            }
            else
            {
                originalSql = execPlanSql;
            }

            // バインド変数取得
            List<?> bindList = null;
            if (config__.isRecordBindVal())
            {
                bindList = SqlUtil.getJdbcJavelinBindValByRef(stmt);
            }
            String bindVals = "";

            // PreparedStatementのバッチ実行の場合は、
            // バッチ実行するSQLの数だけの配列（originalSql）を作成する
            if (stmt instanceof PreparedStatement && paramNum == 0 && args.length == 1)
            {
                originalSql = createBindValArray(stmt, originalSql, execPlanSql);
            }

            StringBuffer execPlanText = new StringBuffer();
            Statement planStmt = null;
            List<String> execPlans = new ArrayList<String>();
            // SQLの数だけループ
            boolean recordIntervalExpired = isRecordIntervalExpired(node, jdbcJvnStatus);
            String[] prevExecPlans = getPrevExecPlan(callTree, node);
            for (int count = 0; originalSql != null && count < originalSql.length; count++)
            {
                execPlanText.setLength(0);

                //バインド変数取得
                bindVals = SqlUtil.getBindValCsv(bindList, count);

                // SQLがDMLではない場合、実行計画は取得しない。
                String originalSqlElement = appendLineBreak(originalSql[count]);
                if (SqlUtil.checkDml(originalSqlElement) == false)
                {

                    if (bindVals != null)
                    {
                        resultText.add(BIND_PREFIX + bindVals);
                    }
                    continue;
                }

                try
                {
                    if (count == 0 || (stmt instanceof PreparedStatement) == false)
                    {
                        // PreparedStatementのときは、
                        // バインド変数がセットされた、実行計画取得用のPreparedStatementを実行する。
                        String execPlanResult = null;

                        // TODO 前回の実行計画が取得できない場合があるので、nullチェックで暫定対処する。
                        if (recordIntervalExpired || prevExecPlans == null)
                        {
                            planStmt = stmt.getConnection().createStatement();
                            
                            if (processor.needsLock())
                            {
                                synchronized (processor)
                                {
                                    recordIntervalExpired =
                                                            isRecordIntervalExpired(node,
                                                                                    jdbcJvnStatus);
                                    prevExecPlans = getPrevExecPlan(callTree, node);
                                    if (recordIntervalExpired || prevExecPlans == null)
                                    {
                                        execPlanResult =
                                                         doExecPlan(processor, stmt, bindList,
                                                                    planStmt, originalSqlElement);
                                    }
                                    else
                                    {
                                        execPlanResult = prevExecPlans[count];
                                    }
                                }
                            }
                            else
                            {
                                execPlanResult =
                                                 doExecPlan(processor, stmt, bindList, planStmt,
                                                            originalSqlElement);
                            }
                            
                            execPlans.add(execPlanResult);
                        }
                        else
                        {
                            execPlanResult = prevExecPlans[count];
                        }
                        execPlanText.append(execPlanResult);
                    }
                }
                catch (Exception ex)
                {
                    // DBアクセスエラー/想定外の例外が発生した場合はエラーログに出力しておく。
                    SystemLogger.getInstance().warn(ex);
                }
                finally
                {
                    // リソース解放
                    if (planStmt != null)
                    {
                        planStmt.close();
                        planStmt = null;
                    }

                    if (paramNum != 1)
                    {
                        // 実行計画を取得すべきSQLが複数ある場合、バインド変数、実行計画を末尾に追加
                                 if (bindVals != null)
                        {
                            resultText.add(BIND_PREFIX + bindVals);
                        }
                    }

                    // 実行計画を取得すべきSQLが1つしかない場合、実行計画を末尾に追加
                    if (execPlanText.length() > 0)
                    {
                        resultText.add(PLAN_PREFIX + execPlanText.toString());
                    }
                }
            }

            // パラメータ数1なら、メソッド引数をargsに追加
            if (paramNum == 1)
            {
                for (int count = 1; count < args.length; count++)
                {
                    resultText.add(PARAM_PREFIX + args[count]);
                }
            }

            if (recordIntervalExpired)
            {
                String[] execPlanArray = execPlans.toArray(new String[execPlans.size()]);
                saveExecPlan(node, execPlanArray, jdbcJvnStatus);

                // Full Scan監視フラグがONであり、実行計画取得が実施され、Full Scanが発生している場合のみ、
                // イベントを発生させる。
                if (config__.isFullScanMonitor())
                {
                    sendFullScanEvent(processor, resultText, stmt,
                                      paramNum, node, execPlanSql);
                }
            }
        }
        catch (Exception ex)
        {
            // 想定外の例外が発生した場合は標準エラー出力に出力しておく。
            SystemLogger.getInstance().warn(ex);
        }
        finally
        {
            // 実行計画取得中の状態を解除
            jdbcJvnStatus.setNowExpalaining(null);
        }

        return resultText;
    }

    private static String appendLineBreak(String str)
    {
        if (str == null || str.endsWith("\n"))
        {
            return str;
        }

        return str + '\n';
    }

    private static String doExecPlan(DBProcessor processor, Statement stmt, List<?> bindList,
            Statement planStmt, String originalSqlElement)
        throws SQLException
    {
        String execPlanResult;
        if (stmt instanceof PreparedStatement)
        {
            execPlanResult = processor.getExecPlanPrepared(stmt, originalSqlElement, bindList);
        }
        else
        {
            // 実行計画を取得（DBMSの種類によって分岐）
            execPlanResult = processor.execPlan(stmt, originalSqlElement, planStmt);
        }
        return execPlanResult;
    }

    private static String[] createBindValArray(final Statement stmt, String[] originalSql,
            final String[] execPlanSql)
        throws Exception
    {
        int bindValCount = SqlUtil.getPreparedStatementAddBatchCount(stmt);
        if (bindValCount > 0)
        {
            if (execPlanSql == null)
            {
                String key = "javelin.jdbc.stats.JdbcJavelinRecorder.NotSavedExecPlanSQLMessage";
                String message = JdbcJavelinMessages.getMessage(key);
                SystemLogger.getInstance().warn(message);
            }
            else
            {
                originalSql = new String[bindValCount];
                for (int index = 0; index < bindValCount; index++)
                {
                    originalSql[index] = execPlanSql[0];
                }
            }
        }
        return originalSql;
    }

    /**
     * JDBCJavelinのパラメータを設定する。
     * @param config JDBCJavelinの設定
     */
    public static void setJdbcJavelinConfig(final JdbcJavelinConfig config)
    {
        JdbcJavelinRecorder.config__ = config;
    }

    /**
     * スレッドIDを設定する.
     * @param threadId スレッドID
     */
    public static void setThreadId(final String threadId)
    {
        // JavelinRecorderに処理委譲
        StatsJavelinRecorder.setThreadId(threadId);
    }

    private static DBProcessor getProcessor(final String jdbcUrl)
    {
        if (jdbcUrl == null)
        {
            return null;
        }

        for (DBProcessor processor : processorList__)
        {
            if (processor.isTarget(jdbcUrl))
            {
                return processor;
            }
        }

        return null;
    }

    /**
     * Connection.prepareStatementメソッド呼び出し後に呼ばれるメソッド。
     *
     * @param connection 接続オブジェクト
     * @param sql PreparedStatement文字列
     * @param pstmt Connection.prepareStatement()の戻り値
     * @param methodName メソッド名
     */
    public static void postPrepareStatement(final Connection connection, final String sql,
            final PreparedStatement pstmt, final String methodName)
    {
        JdbcJvnStatus    jdbcJvnStatus    = JdbcJvnStatus.getInstance();
        CallTreeRecorder callTreeRecorder = jdbcJvnStatus.getCallTreeRecorder();
        CallTree         tree             = callTreeRecorder.getCallTree();

        if (tree.getRootNode() == null)
        {
            tree.loadConfig();
        }
                
        if (tree.isJdbcEnabled() == false)
        {
            return;
        }

        // 実行しているSQL文を追加する
        if (pstmt != null)
        {
            try
            {
                JdbcJavelinStatement jStmt = (JdbcJavelinStatement)pstmt;
                jStmt.getJdbcJavelinSql().clear();
                jStmt.getJdbcJavelinSql().add(sql);
            }
            catch (Exception e)
            {
                SystemLogger.getInstance().warn(e);
            }
        }

        // 以下、実行計画取得用PreparedStatementを作成する

        // Connection.prepareStatement()以外は、実行計画取得用PreparedStatementを作成しない
        if ("prepareStatement".equals(methodName) == false)
        {
            return;
        }

        // 実行計画取得用PreparedStatementを作成中であれば、後処理は行わない
        if (jdbcJvnStatus.getNowCalling_() != null)
        {
            return;
        }

        // ２重呼び出しを禁止する
        jdbcJvnStatus.setNowCalling_(connection);

        try
        {
            JdbcJavelinConnection jvnConnection = (JdbcJavelinConnection)connection;
            String jdbcUrl = jvnConnection.getJdbcUrl();
            DBProcessor processor = jvnConnection.getJdbcJavelinProcessor();
            if (processor == null)
            {
                processor = getProcessor(jdbcUrl);
                jvnConnection.setJdbcJavelinProcessor(processor);
            }

            if (processor != null)
            {
                processor.postPrepareStatement(sql, pstmt);
            }
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
        finally
        {
            jdbcJvnStatus.setNowCalling_(null);
        }
    }

    /**
     * 設定を取得する。
     * 
     * @return 設定。
     */
    public static JdbcJavelinConfig getConfig()
    {
        return config__;
    }
}
