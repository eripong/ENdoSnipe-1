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
package jp.co.acroquest.endosnipe.javelin.jdbc.instrument;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.common.JavassistUtil;
import jp.co.acroquest.endosnipe.javelin.jdbc.common.JdbcJavelinConfig;
import jp.co.acroquest.endosnipe.javelin.jdbc.common.JdbcJavelinMessages;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.JdbcJavelinRecorder;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.JdbcJavelinStatement;
import jp.co.acroquest.endosnipe.javelin.util.HashMap;
import jp.co.smg.endosnipe.javassist.CannotCompileException;
import jp.co.smg.endosnipe.javassist.ClassPool;
import jp.co.smg.endosnipe.javassist.CtBehavior;
import jp.co.smg.endosnipe.javassist.CtClass;
import jp.co.smg.endosnipe.javassist.CtField;
import jp.co.smg.endosnipe.javassist.CtMethod;
import jp.co.smg.endosnipe.javassist.Modifier;
import jp.co.smg.endosnipe.javassist.NotFoundException;

/**
 * Java Instrumentation APIにより、javaagentとしてクラスの変換を行う.
 */
public class JdbcJavelinTransformer implements ClassFileTransformer
{
    private static Map<ClassLoader, ClassPool> loaderPoolMap__ =
            new HashMap<ClassLoader, ClassPool>();

    /** 実行計画取得用PreparedStatementフィールド名. */
    public static final String STMTFORPLAN_FIELD_NAME = "stmtForPlan_";

    /** 実行計画取得用PreparedStatementフィールド宣言. */
    private static final String STMTFORPLAN_FIELD_DEF =
            "public jp.co.acroquest.endosnipe.javelin.jdbc.instrument.PreparedStatementPair[] "
                    + STMTFORPLAN_FIELD_NAME + ";";

    /** 実行計画取得用コード. */
    private static final String STMTFORPLAN_GETTER_METHOD_DEF =
            "public jp.co.acroquest.endosnipe.javelin.jdbc.instrument.PreparedStatementPair[] getStmtForPlan() " 
            + "{return this." 
            + STMTFORPLAN_FIELD_NAME +";}";
    
    /** 実行計画設定用コード. */
    private static final String STMTFORPLAN_SETTER_METHOD_DEF =
            "public void setStmtForPlan(jp.co.acroquest.endosnipe.javelin.jdbc.instrument.PreparedStatementPair[] stmts) " 
            + "{this." 
            + STMTFORPLAN_FIELD_NAME +" = stmts;}";

    /** 実行計画取得用PreparedStatementパラメタ設定終了フラグフィールド名. */
    protected static final String FLAGFORPLANSTMT_FIELD_NAME = "flagForPlanStmt_";

    /** 実行計画取得用PreparedStatementパラメタ設定終了フラグフィールド宣言. */
    private static final String FLAGFORPLANSTMT_FIELD_DEF =
            "private boolean " + FLAGFORPLANSTMT_FIELD_NAME + " = false;";

    /** SQL取得用コード. */
    private static final String SQL_GETTER_METHOD_DEF =
            "public java.util.List getJdbcJavelinSql() " + "{return this.jdbcJavelinSql_;}";

    private static final String SQL_GETTER_INTERFACE_DEF =
            "public java.util.List getJdbcJavelinSql();";

    /** 発行したSQLを保存する. */
    public static final String SQL_FIELD_NAME = "jdbcJavelinSql_";

    /** 発行したSQLを保存するフィールドの定義. */
    private static final String SQL_FIELD_DEF =
            "public java.util.List " + SQL_FIELD_NAME + " = new java.util.ArrayList();";

    private static final String BINDVAL_GETTER_INTERFACE_DEF =
            "public java.util.List getJdbcJavelinBindVal();";

    /** バインド変数取得用コード. */
    private static final String BINDVAL_GETTER_METHOD_DEF =
            "public java.util.List getJdbcJavelinBindVal()" + "{return this.jdbcJavelinBindVal_;}";

    /** バインド変数を保存するフィールド. */
    private static final String BINDVAL_FIELD_NAME = "jdbcJavelinBindVal_";

    /** バインド変数を保存するフィールドの定義. */
    private static final String BINDVAL_FIELD_DEF =
            "public java.util.List jdbcJavelinBindVal_ = " + "new java.util.ArrayList();";


    /** バインド変数取得用コード. */
    private static final String BINDVAL_INDEX_GETTER_METHOD_DEF =
            "public int getJdbcJavelinBindIndex()" + "{return this.jdbcJavelinBindValIndex_;}";

    public static final String BINDVAL_IDX_FIELD_NAME = "jdbcJavelinBindValIndex_";

    private static final String BINDVAL_IDX_FIELD_DEF = "public int jdbcJavelinBindValIndex_ = 0;";

    /** メソッドのパラメタの型を取り除くための正規表現 */
    private static final Pattern PARAM_TYPE_PATTERN = Pattern.compile("[A-Za-z\\.]+[\\[\\]]* ()");

    /**
     * コンストラクタ.
     */
    public JdbcJavelinTransformer()
    {
        // 何もしない
    }

    /**
     * 初期化する.
     * 設定を読み込む.
     */
    public void init()
    {
        // JdbcJavelin関連の設定値をjavelin.confファイルから読込む
        JdbcJavelinConfig config = new JdbcJavelinConfig();
        JdbcJavelinRecorder.setJdbcJavelinConfig(config);

        // JDBCの設定値を取得する
        boolean recordExecPlan = config.isRecordExecPlan();
        boolean fullScanMonitor = config.isFullScanMonitor();
        boolean recordDuplJdbcCall = config.isRecordDuplJdbcCall();
        boolean recordBindVal = config.isRecordBindVal();
        long execPlanThreshold = config.getExecPlanThreshold();
        long jdbcStringLimitLength = config.getJdbcStringLimitLength();
        boolean allowSqlTraceForOracle = config.isAllowSqlTraceForOracle();
        boolean verbosePlanForPostgres = config.isVerbosePlanForPostgres();
        int queryLimitCount = config.getRecordStatementNumMax();
        boolean recordStackTrace = config.isRecordStackTrace();
        int recordStackTraceThreshold = config.getRecordStackTraceThreshold();

        // JDBCの設定値を標準出力する
        PrintStream out = System.out;
        String key =
                "javelin.jdbc.instrument.JdbcJavelinTransformer.PropertiesRelatedWithJDBCJavelin";
        String message = JdbcJavelinMessages.getMessage(key);
        out.println(">>>> " + message);
        out.println("\tjavelin.jdbc.recordExecPlan               : " + recordExecPlan);
        out.println("\tjavelin.jdbc.fullScan.monitor             : " + fullScanMonitor);
        out.println("\tjavelin.jdbc.recordDuplJdbcCall           : " + recordDuplJdbcCall);
        out.println("\tjavelin.jdbc.recordBindVal                : " + recordBindVal);
        out.println("\tjavelin.jdbc.execPlanThreshold            : " + execPlanThreshold);
        out.println("\tjavelin.jdbc.stringLimitLength            : " + jdbcStringLimitLength);
        out.println("\tjavelin.jdbc.oracle.allowSqlTrace         : " + allowSqlTraceForOracle);
        out.println("\tjavelin.jdbc.postgres.verbosePlan         : " + verbosePlanForPostgres);
        out.println("\tjavelin.jdbc.record.statement.num.maximum : " + queryLimitCount);
        out.println("\tjavelin.jdbc.record.stackTrace            : " + recordStackTrace);
        out.println("\tjavelin.jdbc.record.stackTraceThreshold   : " + recordStackTraceThreshold);
        out.println("<<<<");
    }

    /**
     * コード埋め込み対象クラスかどうかを判定し、
     * 対象クラスに対してコードを埋め込む。
     * @param loader クラスローダ
     * @param className クラス名
     * @param classBeingRedefined 再定義したクラス
     * @param protectionDomain 保護領域
     * @param classfileBuffer クラスファイルのバッファ
     * @throws IllegalClassFormatException 不正なクラスのフォーマット
     * @return 変換後のクラス
     */
    public byte[] transform(final ClassLoader loader, final String className,
            final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain,
            final byte[] classfileBuffer)
        throws IllegalClassFormatException
    {
        // クラスが埋め込めない形式である場合は
        // IllegalClassFormatExceptionを投げる
        try
        {
            ClassPool pool;
            if (loader instanceof URLClassLoader)
            {
                pool = loaderPoolMap__.get(loader);
                if (pool == null)
                {
                    pool = new ClassPool(ClassPool.getDefault());
                    appendLoaderClassPath(pool, (URLClassLoader)loader);

                    loaderPoolMap__.put(loader, pool);
                }
            }
            else
            {
                pool = ClassPool.getDefault();
            }

            // Interfaceの「Statement」を転換する
            CtClass statement = createModifiedStatement(pool);
            if ("java/sql/Statement".equals(className))
            {
                return statement.toBytecode();
            }

            // バインド変数出力対応
            // Interfaceの「PreparedStatementStatement」を転換する
            CtClass pStatement = createModifiedPreparedStatement(pool);
            if ("java/sql/PreparedStatement".equals(className))
            {
                return pStatement.toBytecode();
            }

            // 計測対象から、転換クラスを作る
            ByteArrayInputStream stream = new ByteArrayInputStream(classfileBuffer);

            // CtClass取得の決まりごと。
            // クラスにメソッドを埋め込めるようにする。
            // もしクラス名が完全修飾名でなかった場合は、
            // バイト配列からCtClassを作成する。
            CtClass ctClass = null;
            try
            {
                // ClassPoolが管理するクラス名のセパレータを
                // Javaの完全修飾名用に変換する
                ctClass = pool.get(className.replaceAll("/", "."));
                if (ctClass.isFrozen() == true)
                {
                    ctClass.defrost();
                    ctClass.stopPruning(true);
                }
                else
                {
                    ctClass = null;
                }
            }
            catch (Exception ex)
            {
                // 何もしない
            }
            if (ctClass == null)
            {
                ctClass = pool.makeClass(stream);
                ctClass.defrost();
                ctClass.stopPruning(true);
            }

            return transform(className, classfileBuffer, pool, ctClass);
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
            IllegalClassFormatException e = new IllegalClassFormatException();
            e.initCause(ex);
            throw e;
        }
    }

    /**
     * クラスパスを追加する.
     * @param pool クラスプール
     * @param loader クラスローダ
     */
    private void appendLoaderClassPath(final ClassPool pool, final URLClassLoader loader)
    {
        URL[] urls = loader.getURLs();
        for (URL url : urls)
        {
            String value = url.toExternalForm();
            if (value.startsWith("file:"))
            {
                try
                {
                    pool.appendClassPath(value.substring(5));
                }
                catch (NotFoundException ex)
                {
                    SystemLogger.getInstance().warn(ex);
                }
            }
        }

        ClassLoader parentLoader = loader.getParent();
        if (parentLoader instanceof URLClassLoader)
        {
            appendLoaderClassPath(pool, (URLClassLoader)parentLoader);
        }
    }

    /**
     * 
     * @param className クラス名
     * @param classfileBuffer クラスファイルバッファ
     * @param pool クラスプール
     * @param ctClass ｃｔクラス
     * @return 変換後のクラス
     * @throws IllegalClassFormatException 不正なクラスフォーマット
     * @throws NotFoundException クラスが見つからない
     * @throws CannotCompileException コンパイルできない
     */
    public byte[] transform(final String className, final byte[] classfileBuffer,
            final ClassPool pool, CtClass ctClass)
        throws IllegalClassFormatException,
            NotFoundException,
            CannotCompileException
    {
        // Interfaceを避ける
        if (ctClass.isInterface())
        {
            return null;
        }

        if (JavassistUtil.isInherited(ctClass, pool, "java.sql.Connection"))
        {
            ctClass = JdbcJavelinConverter.convertConnection(pool, ctClass);
            try
            {
                return ctClass.toBytecode();
            }
            catch (Exception ex)
            {
                SystemLogger.getInstance().warn(ex);
            }
        }

        // JDBCJavelinのBCI対象以外クラスを避ける
        boolean inheritedStatement = JavassistUtil.isInherited(ctClass, pool, "java.sql.Statement");
        if (inheritedStatement == false)
        {
            return null;
        }

        // BCI対象Statementに対する、変換を行う
        boolean inheritedPreparedStatement =
                                             JavassistUtil.isInherited(ctClass,
                                                                              pool, "java.sql.PreparedStatement");
        if (inheritedPreparedStatement)
        {
            // バインド変数出力のため追加
            ctClass = createModifiedInheritedPreparedStatement(className, pool);
        }
        else
        {
            ctClass = createModifiedInheritedStatement(className, pool);
        }

        // 変換結果を戻す
        String tagKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
        String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tagKey);
        String messageKey = "javelin.jdbc.instrument.JdbcJavelinTransformer.BCIClassNameLabel";
        String message = JdbcJavelinMessages.getMessage(messageKey, ctClass.getName());
        SystemLogger.getInstance().info(jdbcJavelinTag + message);

        CtBehavior[] behaviors = ctClass.getDeclaredBehaviors();
        for (int index = 0; index < behaviors.length; index++)
        {
            CtBehavior behaviour = behaviors[index];
            // メソッドの定義がない場合、あるいはpublicでない
            // (->インターフェースに定義されていない)場合は実行しない。
            final int modifier = behaviour.getModifiers();
            if (Modifier.isAbstract(modifier) || !Modifier.isPublic(modifier))
            {
                continue;
            }
            JdbcJavelinConverter.convertStatement(pool, ctClass, behaviour, inheritedStatement,
                                                  inheritedPreparedStatement);
            
        }

        if (ctClass == null)
        {
            return null;
        }

        try
        {
            return ctClass.toBytecode();
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
        return null;
    }

    /**
     * Statement実装クラス用のBCI処理。
     *
     * @param className PreparedStatement実装クラス
     * @param pool クラスプール
     * @return 変更したPreparedStatement実装クラス
     * @throws NotFoundException classNameで指定されたクラスが見つからない場合
     * @throws CannotCompileException メソッドを追加できない場合
     */
    private CtClass createModifiedInheritedStatement(String className, final ClassPool pool)
        throws NotFoundException,
            CannotCompileException
    {

        className = className.replace('/', '.');
        CtClass ctClassStatement = pool.get(className);
        CtField ctSQLField;
        CtMethod ctGetSQLField;

        synchronized (this)
        {
            try
            {
                ctClassStatement.getDeclaredField(SQL_FIELD_NAME);
            }
            catch (NotFoundException nofExp)
            {
                // fieldを作る
                ctSQLField = CtField.make(SQL_FIELD_DEF, ctClassStatement);
                // fieldを追加する
                ctClassStatement.addField(ctSQLField);

                try
                {
                    ctClassStatement.getDeclaredMethod("getJdbcJavelinSql");
                }
                catch (NotFoundException nfe)
                {
                    // Mehtodを作る
                    ctGetSQLField = CtMethod.make(SQL_GETTER_METHOD_DEF, ctClassStatement);
                    // Methodを追加する
                    ctClassStatement.addMethod(ctGetSQLField);

                    String tagKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
                    String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tagKey);
                    String messageKey =
                            "javelin.jdbc.instrument.JdbcJavelinTransformer.AddJdbcJavelinSqlMessage";
                    String message = JdbcJavelinMessages.getMessage(messageKey, className);
                    SystemLogger.getInstance().info(jdbcJavelinTag + message);
                }
            }
        }

        // もしStatementインタフェース実装クラスに
        // addBatch()、clearBatch()、execute()、executeQury()、
        // executeBatch()、executeUpdateが実装されていなければ、
        // それぞれメソッドを追加し、オーバーライドする。
        addAddBatchMethodOfStatement(pool, className);
        addClearBatchMethod(pool, className, "");
        addExecuteMethodOfStatement(pool, className);
        addExecuteQueryMethodOfStatement(pool, className);
        addExecuteBatchMethodOfStatement(pool, className);
        addExecuteUpdateMethodOfStatement(pool, className);

        return ctClassStatement;
    }

    /**
     * PreparedStatement実装クラス用のBCI処理。
     *
     * @param className PreparedStatement実装クラス
     * @param pool クラスプール
     * @return 変更したPreparedStatement実装クラス
     * @throws NotFoundException classNameで指定されたクラスが見つからない場合
     * @throws CannotCompileException メソッドを追加できない場合
     */
    private CtClass createModifiedInheritedPreparedStatement(String className, final ClassPool pool)
        throws NotFoundException,
            CannotCompileException
    {

        className = className.replace('/', '.');
        CtClass ctClassStatement = pool.get(className);
        CtField ctSQLField;
        CtMethod ctGetSQLField;

        synchronized (this)
        {
            try
            {
                ctClassStatement.getDeclaredField(SQL_FIELD_NAME);
            }
            catch (NotFoundException nofExp)
            {
                // fieldを作る
                ctSQLField = CtField.make(SQL_FIELD_DEF, ctClassStatement);
                // fieldを追加する
                ctClassStatement.addField(ctSQLField);

                try
                {
                    ctClassStatement.getDeclaredMethod("getJdbcJavelinSql");
                }
                catch (NotFoundException nfe)
                {
                    // Mehtodを作る
                    ctGetSQLField = CtMethod.make(SQL_GETTER_METHOD_DEF, ctClassStatement);
                    // Methodを追加する
                    ctClassStatement.addMethod(ctGetSQLField);

                    String tagKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
                    String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tagKey);
                    String messageKey =
                            "javelin.jdbc.instrument.JdbcJavelinTransformer.AddJdbcJavelinSqlMessage";
                    String message = JdbcJavelinMessages.getMessage(messageKey, className);
                    SystemLogger.getInstance().info(jdbcJavelinTag + message);
                }
            }

            try
            {
                ctClassStatement.getDeclaredField(BINDVAL_FIELD_NAME);
            }
            catch (NotFoundException nofExp)
            {
                // fieldを作る
                ctSQLField = CtField.make(BINDVAL_FIELD_DEF, ctClassStatement);
                // fieldを追加する
                ctClassStatement.addField(ctSQLField);

                ctSQLField = CtField.make(BINDVAL_IDX_FIELD_DEF, ctClassStatement);
                // fieldを追加する
                ctClassStatement.addField(ctSQLField);
                
                try
                {
                    ctClassStatement.getDeclaredMethod("getJdbcJavelinBindIndex");
                }
                catch (NotFoundException nfe)
                {
                    // Mehtodを作る
                    CtMethod sqlIndexField =
                                             CtMethod.make(BINDVAL_INDEX_GETTER_METHOD_DEF,
                                                           ctClassStatement);
                    ctClassStatement.addMethod(sqlIndexField);
                }
                

                try
                {
                    ctClassStatement.getDeclaredMethod("getJdbcJavelinBindVal");
                }
                catch (NotFoundException nfe)
                {
                    // Mehtodを作る
                    ctGetSQLField = CtMethod.make(BINDVAL_GETTER_METHOD_DEF, ctClassStatement);
                    // Methodを追加する
                    ctClassStatement.addMethod(ctGetSQLField);

                    String tagKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
                    String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tagKey);
                    String messageKey =
                            "javelin.jdbc.instrument.JdbcJavelinTransformer.AddJdbcJavelinBindValMessage";
                    String message = JdbcJavelinMessages.getMessage(messageKey, className);
                    SystemLogger.getInstance().info(jdbcJavelinTag + message);
                }
            }

            try
            {
                ctClassStatement.getDeclaredField(STMTFORPLAN_FIELD_NAME);
            }
            catch (NotFoundException nofExp)
            {
                // fieldを作る
                ctSQLField = CtField.make(STMTFORPLAN_FIELD_DEF, ctClassStatement);
                // fieldを追加する
                ctClassStatement.addField(ctSQLField);

                // fieldを作る
                ctSQLField = CtField.make(FLAGFORPLANSTMT_FIELD_DEF, ctClassStatement);
                // fieldを追加する
                ctClassStatement.addField(ctSQLField);
                
                try
                {
                    ctClassStatement.getDeclaredMethod("getStmtForPlan");
                }
                catch (NotFoundException ex)
                {
                    // 取得用のメソッドを作成する。
                    CtMethod getterMethod = CtMethod.make(STMTFORPLAN_GETTER_METHOD_DEF, ctClassStatement);
                    ctClassStatement.addMethod(getterMethod);

                    CtMethod setterMethod = CtMethod.make(STMTFORPLAN_SETTER_METHOD_DEF, ctClassStatement);
                    ctClassStatement.addMethod(setterMethod);
                }
                
                String tagKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
                String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tagKey);
                String messageKey =
                        "javelin.jdbc.instrument.JdbcJavelinTransformer.AddSomeFieldMessage";
                String message =
                        JdbcJavelinMessages.getMessage(messageKey, className,
                                                       STMTFORPLAN_FIELD_NAME + ","
                                                               + FLAGFORPLANSTMT_FIELD_NAME);
                SystemLogger.getInstance().info(jdbcJavelinTag + message);
            }

            // もしPreparedStatementインタフェース実装クラスにsetXXXメソッドが実装されていなければ、
            // setXXXメソッドを追加し、オーバーライドする。
            String[] setMethodA =
                    new String[]{"setString(int parameterIndex, java.lang.String x)",
                            "setObject(int parameterIndex, java.lang.Object x)",
                            "setObject(int parameterIndex, java.lang.Object x, int targetSqlType)",
                            "setObject(int parameterIndex, java.lang.Object x, int targetSqlType, int scale)"};
            String[] setMethodB =
                    new String[]{"setBigDecimal(int parameterIndex, java.math.BigDecimal x)",
                            "setDate(int parameterIndex, java.sql.Date x)",
                            "setDate(int parameterIndex, java.sql.Date x, java.util.Calendar cal)",
                            "setTime(int parameterIndex, java.sql.Time x)",
                            "setTime(int parameterIndex, java.sql.Time x, java.util.Calendar cal)",
                            "setTimestamp(int parameterIndex, java.sql.Timestamp x)",
                            "setTimestamp(int parameterIndex, java.sql.Timestamp x, java.util.Calendar cal)"};
            String[] setMethodC =
                    new String[]{"setBoolean(int parameterIndex, boolean x)",
                            "setShort(int parameterIndex, short x)",
                            "setInt(int parameterIndex, int x)",
                            "setLong(int parameterIndex, long x)",
                            "setFloat(int parameterIndex, float x)",
                            "setDouble(int parameterIndex, double x)"};
            String[] setMethodD =
                    new String[]{"setByte(int parameterIndex, byte x)",
                            "setBytes(int parameterIndex, byte[] x)"};
            String[] setMethodE =
                    new String[]{"setArray(int parameterIndex, java.sql.Array x)",
                            "setBlob(int parameterIndex, java.sql.Blob x)",
                            "setClob(int parameterIndex, java.sql.Clob x)",
                            "setRef(int parameterIndex, java.sql.Ref x)",
                            "setURL(int parameterIndex, java.net.URL x)"};
            String[] setMethodF =
                    new String[]{"setNull(int parameterIndex, int sqlType)",
                            "setNull(int parameterIndex, int sqlType, java.lang.String typeName)"};
            String[] setMethodG =
                    new String[]{
                            "setAsciiStream(int parameterIndex, java.io.InputStream x, int length)",
                            "setBinaryStream(int parameterIndex, java.io.InputStream x, int length)",
                            "setUnicodeStream(int parameterIndex, java.io.InputStream x, int length)",};
            String[] setMethodH =
                    new String[]{"setCharacterStream(int parameterIndex, java.io.Reader reader, int length)",};

            // setメソッドにsuperを呼び出すコードを埋め込む
            addSetMethodLoop(pool, className, setMethodA);
            addSetMethodLoop(pool, className, setMethodB);
            addSetMethodLoop(pool, className, setMethodC);
            addSetMethodLoop(pool, className, setMethodD);
            addSetMethodLoop(pool, className, setMethodE);
            addSetMethodLoop(pool, className, setMethodF);
            addSetMethodLoop(pool, className, setMethodG);
            addSetMethodLoop(pool, className, setMethodH);

            // もしPreparedStatementインタフェース実装クラスに
            // addBatch()、close()が実装されていなければ、
            // それぞれメソッドを追加し、オーバーライドする。
            addAddBatchMethodOfPreparedStatement(pool, className);
            addNonParamMethod(pool, className, "clearBatch",
                              JdbcJavelinConverter.BCI_METHOD_CLEAR_BATCH
                                      + JdbcJavelinConverter.BCI_METHOD_PLANFORPREPARED_CLEARBATCH);
            addCloseMethod(pool, className);
        }
        
        addJvnStatementInterface(ctClassStatement);
        
        return ctClassStatement;
    }

    /**
     * JdbcJavelinStatementのインタフェースを追加する。
     * 
     * @param ctClassStatement 追加対象
     */
    private void addJvnStatementInterface(CtClass ctClassStatement)
    {
        try
        {
            ClassPool classPool = ctClassStatement.getClassPool();
            CtClass jvnStatementClass =
                                        classPool.get(JdbcJavelinStatement.class.getCanonicalName());
            boolean hasInterface =
                                   JdbcJavelinConverter.hasInterface(ctClassStatement,
                                                                     jvnStatementClass);
            
            if(hasInterface == false)
            {
                ctClassStatement.addInterface(jvnStatementClass);
            }
        }
        catch (NotFoundException nfe)
        {
            SystemLogger.getInstance().warn(nfe);
        }
    }

    /**
     * Statementにバインド変数取得メソッドを追加する。
     *
     * @param pool クラスプール
     * @return 変更したStatementインタフェース
     * @throws NotFoundException java.sql.Statementが見つからない場合
     * @throws CannotCompileException メソッドを追加できない場合
     */
    private CtClass createModifiedStatement(final ClassPool pool)
        throws NotFoundException,
            CannotCompileException
    {

        CtClass ctClassStatement = pool.get("java.sql.Statement");
        CtMethod ctGetSQLField;

        try
        {
            ctClassStatement.getDeclaredMethod("getJdbcJavelinSql");
        }
        catch (NotFoundException nofExp)
        {
            // Mehtodを作る
            ctGetSQLField = CtMethod.make(SQL_GETTER_INTERFACE_DEF, ctClassStatement);
            // Methodを追加する
            ctClassStatement.addMethod(ctGetSQLField);

            String tagKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
            String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tagKey);
            String messageKey =
                    "javelin.jdbc.instrument.JdbcJavelinTransformer.AddJdbcJavelinSqlToInterfaceMessage";
            String message = JdbcJavelinMessages.getMessage(messageKey);
            SystemLogger.getInstance().info(jdbcJavelinTag + message);
        }
        return ctClassStatement;
    }

    /**
     * PreparedStatementにバインド変数取得メソッドを追加する。
     *
     * @param pool クラスプール
     * @return 変更したPreparedStatementインタフェース
     * @throws NotFoundException java.sql.PreparedStatementが見つからない場合
     * @throws CannotCompileException メソッドを追加できない場合
     */
    private CtClass createModifiedPreparedStatement(final ClassPool pool)
        throws NotFoundException,
            CannotCompileException
    {

        CtClass ctClassStatement = pool.get("java.sql.PreparedStatement");
        CtMethod ctGetSQLField;

        try
        {
            ctClassStatement.getDeclaredMethod("getJdbcJavelinBindVal");
        }
        catch (NotFoundException nofExp)
        {
            // Mehtodを作る
            ctGetSQLField = CtMethod.make(BINDVAL_GETTER_INTERFACE_DEF, ctClassStatement);
            // Methodを追加する
            ctClassStatement.addMethod(ctGetSQLField);

            String tagKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
            String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tagKey);
            String messageKey =
                    "javelin.jdbc.instrument.JdbcJavelinTransformer.AddJdbcJavelinBindValToInterfaceMessage";
            String message = JdbcJavelinMessages.getMessage(messageKey);
            SystemLogger.getInstance().info(jdbcJavelinTag + message);
        }
        return ctClassStatement;
    }

    /**
     * あるタイプのsetメソッドを埋め込む。
     * @param pool クラスプール
     * @param className クラス名
     * @param setMethods setメソッド一覧
     * @throws NotFoundException メソッドが見つからないとき
     * @throws CannotCompileException コンパイルできないとき
     */
    private void addSetMethodLoop(final ClassPool pool, final String className,
            final String[] setMethods)
        throws NotFoundException,
            CannotCompileException
    {
        for (String method : setMethods)
        {
            addSetMethod(pool, className, method);
        }
    }

    /**
     * 指定されたsetメソッドを埋め込む。
     * @param pool クラスプール
     * @param className クラス名
     * @param method setメソッド
     * @throws NotFoundException メソッドが見つからないとき
     * @throws CannotCompileException コンパイルできないとき     */
    private void addSetMethod(final ClassPool pool, final String className, final String method)
        throws NotFoundException,
            CannotCompileException
    {
        CtClass ctClassStatement = pool.get(className);

        int bracketIndex = method.indexOf('(');

        // メソッドの引数
        String paramsAll = method.substring(bracketIndex + 1, method.length() - 1);

        // メソッドの引数の型を取り除く
        Matcher matcher = PARAM_TYPE_PATTERN.matcher(method);
        String params = matcher.replaceAll("");

        // メソッドの名前を取り出す
        String methodName = method.substring(0, bracketIndex);

        // メソッドの型を取り出す
        String[] param = paramsAll.split(",[ \\t\\n]*");
        CtClass[] paramClass = new CtClass[param.length];
        for (int index = 0; index < param.length; index++)
        {
            String paramType = param[index].split("[ \\t\\n]+")[0];
            paramClass[index] = pool.get(paramType);
        }

        try
        {
            ctClassStatement.getDeclaredMethod(methodName, paramClass);
        }
        catch (NotFoundException nfe)
        {
            // Mehtodを作る
            StringBuffer methodDef = new StringBuffer();
            methodDef.append("public void ");
            methodDef.append(method);
            methodDef.append(" throws java.sql.SQLException{\n");
            methodDef.append("    super.");
            methodDef.append(params);
            methodDef.append(";};\n");
            CtMethod ctSetMethod = CtMethod.make(methodDef.toString(), ctClassStatement);
            // Methodを追加する
            ctClassStatement.addMethod(ctSetMethod);

            String tagKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
            String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tagKey);
            String messageKey =
                    "javelin.jdbc.instrument.JdbcJavelinTransformer.AddSomeMethodMessage";
            String message = JdbcJavelinMessages.getMessage(messageKey, className, methodName);
            SystemLogger.getInstance().info(jdbcJavelinTag + message);
        }
    }

    /**
     * 引数がないメソッドを埋め込む。
     * @param pool クラスプール
     * @param className クラス名
     * @param methodName メソッド名
     * @param code 埋め込むコード
     * @throws NotFoundException メソッドが見つからないとき
     * @throws CannotCompileException コンパイルできないとき
     */
    private void addNonParamMethod(final ClassPool pool, final String className,
            final String methodName, final String code)
        throws CannotCompileException,
            NotFoundException
    {
        CtClass ctClassStatement = pool.get(className);

        try
        {
            ctClassStatement.getDeclaredMethod(methodName);
        }
        catch (NotFoundException nfe)
        {
            // Mehtodを作る
            StringBuffer methodDef = new StringBuffer();
            methodDef.append("public void ");
            methodDef.append(methodName);
            methodDef.append("() throws java.sql.SQLException {\n" + "    super." + methodName
                    + "();\n");
            methodDef.append(code);
            methodDef.append("}\n");
            CtMethod ctSetMethod = CtMethod.make(methodDef.toString(), ctClassStatement);
            // Methodを追加する
            ctClassStatement.addMethod(ctSetMethod);

            String tagKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
            String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tagKey);
            String messageKey =
                    "javelin.jdbc.instrument.JdbcJavelinTransformer.AddSomeMethodMessage";
            String message = JdbcJavelinMessages.getMessage(messageKey, className, methodName);
            SystemLogger.getInstance().info(jdbcJavelinTag + message);
        }
    }

    /**
     * Statementを実装するクラスにaddBatchメソッドを埋め込む。
     * @param pool クラスプール
     * @param className クラス名
     * @throws NotFoundException メソッドが見つからないとき
     * @throws CannotCompileException コンパイルできないとき
     */
    private void addAddBatchMethodOfStatement(final ClassPool pool, final String className)
        throws NotFoundException,
            CannotCompileException
    {
        CtClass ctClassStatement = pool.get(className);

        // もしaddbatchメソッドがなければ追加する
        try
        {
            ctClassStatement.getDeclaredMethod("addBatch");
        }
        catch (NotFoundException nfe)
        {
            // Mehtodを作る
            StringBuilder methodDef = new StringBuilder();
            methodDef.append("public void addBatch(java.lang.String sql)"
                    + " throws java.sql.SQLException {\n" + "    super.addBatch(sql);\n}\n");
            CtMethod ctSetMethod = CtMethod.make(methodDef.toString(), ctClassStatement);
            // Methodを追加する
            ctClassStatement.addMethod(ctSetMethod);

            String tagKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
            String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tagKey);
            String messageKey =
                    "javelin.jdbc.instrument.JdbcJavelinTransformer.AddSomeMethodMessage";
            String message = JdbcJavelinMessages.getMessage(messageKey, className, "addBatch");
            SystemLogger.getInstance().info(jdbcJavelinTag + message);
        }
    }

    /**
     * PreparedStatementを実装するクラスにaddBatchメソッドを埋め込む。
     * @param pool クラスプール
     * @param className クラス名
     * @throws NotFoundException メソッドが見つからないとき
     * @throws CannotCompileException コンパイルできないとき
     */
    private void addAddBatchMethodOfPreparedStatement(final ClassPool pool, final String className)
        throws NotFoundException,
            CannotCompileException
    {
        addNonParamMethod(pool, className, "addBatch", "");
        //                          JdbcJavelinConverter.BCI_METHOD_ADD_BATCH
        //                          + JdbcJavelinConverter.BCI_METHOD_PLANFORPREPARED_ADDBATCH);
        //        CtClass ctClass = pool.get(className);
        //        CtMethod method = ctClass.getDeclaredMethod("addBatch");
        //        JdbcJavelinConverter.addSqlToFieldStat(ctClass, method);
    }

    /**
     * Statementを実装するクラスにexecuteメソッドを埋め込む。
     * @param pool クラスプール
     * @param className クラス名
     * @throws NotFoundException executeが実装されていないとき。
     * @throws CannotCompileException コンパイルできないとき
     */
    private void addExecuteMethodOfStatement(final ClassPool pool, final String className)
        throws NotFoundException,
            CannotCompileException
    {
        CtClass ctClassStatement = pool.get(className);

        // もしexecuteメソッドがなければ追加する
        try
        {
            ctClassStatement.getDeclaredMethod("execute");
        }
        catch (NotFoundException nfe)
        {
            // Mehtodを作る
            StringBuffer methodDef = new StringBuffer();
            methodDef.append("public boolean execute(java.lang.String sql)"
                    + " throws java.sql.SQLException {\n" + "    return super.execute(sql);\n");
            methodDef.append("}\n");
            CtMethod ctSetMethod = CtMethod.make(methodDef.toString(), ctClassStatement);
            // Methodを追加する
            ctClassStatement.addMethod(ctSetMethod);

            String tagKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
            String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tagKey);
            String messageKey =
                    "javelin.jdbc.instrument.JdbcJavelinTransformer.AddSomeMethodMessage";
            String message = JdbcJavelinMessages.getMessage(messageKey, className, "execute");
            SystemLogger.getInstance().info(jdbcJavelinTag + message);
        }
    }

    /**
     * Statementを実装するクラスにexecuteQueryメソッドを埋め込む。
     * @param pool クラスプール
     * @param className クラス名
     * @throws NotFoundException executeQueryが実装されていないとき。
     * @throws CannotCompileException コンパイルできないとき
     */
    private void addExecuteQueryMethodOfStatement(final ClassPool pool, final String className)
        throws NotFoundException,
            CannotCompileException
    {
        CtClass ctClassStatement = pool.get(className);

        // もしexecuteQueryメソッドがなければ追加する
        try
        {
            ctClassStatement.getDeclaredMethod("executeQuery");
        }
        catch (NotFoundException nfe)
        {
            // Mehtodを作る
            StringBuffer methodDef = new StringBuffer();
            methodDef.append("public java.sql.ResultSet executeQuery(java.lang.String sql)"
                    + " throws java.sql.SQLException {\n" + "    return super.executeQuery(sql);\n");
            methodDef.append("}\n");

            CtMethod ctSetMethod = CtMethod.make(methodDef.toString(), ctClassStatement);
            // Methodを追加する

            ctClassStatement.addMethod(ctSetMethod);

            String tagKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
            String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tagKey);
            String MessageKey =
                    "javelin.jdbc.instrument.JdbcJavelinTransformer.AddSomeMethodMessage";
            String message = JdbcJavelinMessages.getMessage(MessageKey, className, "executeQuery");
            SystemLogger.getInstance().info(jdbcJavelinTag + message);
        }
    }

    /**
     * Statementを実装するクラスにexecuteUpdateメソッドを埋め込む。
     * @param pool クラスプール
     * @param className クラス名
     * @throws NotFoundException executeUpdateが実装されていないとき。
     * @throws CannotCompileException コンパイルできないとき
     */
    private void addExecuteUpdateMethodOfStatement(final ClassPool pool, final String className)
        throws NotFoundException,
            CannotCompileException
    {
        CtClass ctClassStatement = pool.get(className);

        // もしexecuteUpdateメソッドがなければ追加する
        try
        {
            ctClassStatement.getDeclaredMethod("executeUpdate");
        }
        catch (NotFoundException nfe)
        {
            // Mehtodを作る
            StringBuffer methodDef = new StringBuffer();
            methodDef.append("public int executeUpdate(java.lang.String sql)"
                    + " throws java.sql.SQLException {\n"
                    + "    return super.executeUpdate(sql);\n");
            methodDef.append("}\n");
            CtMethod ctSetMethod = CtMethod.make(methodDef.toString(), ctClassStatement);
            // Methodを追加する
            ctClassStatement.addMethod(ctSetMethod);

            String tagKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
            String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tagKey);
            String messageKey =
                    "javelin.jdbc.instrument.JdbcJavelinTransformer.AddSomeMethodMessage";
            String message = JdbcJavelinMessages.getMessage(messageKey, className, "executeUpdate");
            SystemLogger.getInstance().info(jdbcJavelinTag + message);
        }
    }

    /**
     * Statementを実装するクラスにexecuteBatchメソッドを埋め込む。
     * @param pool クラスプール
     * @param className クラス名
     * @throws NotFoundException executeBatchが実装されていないとき。
     * @throws CannotCompileException コンパイルできないとき
     */
    private void addExecuteBatchMethodOfStatement(final ClassPool pool, final String className)
        throws NotFoundException,
            CannotCompileException
    {
        CtClass ctClassStatement = pool.get(className);

        // もしexecuteBatchメソッドがなければ追加する
        try
        {
            ctClassStatement.getDeclaredMethod("executeBatch");
        }
        catch (NotFoundException nfe)
        {
            // Mehtodを作る
            StringBuffer methodDef = new StringBuffer();
            methodDef.append("public int[] executeBatch()" + " throws java.sql.SQLException {\n"
                    + "    return super.executeBatch();\n");
            methodDef.append("}\n");
            CtMethod ctSetMethod = CtMethod.make(methodDef.toString(), ctClassStatement);
            // Methodを追加する
            ctClassStatement.addMethod(ctSetMethod);

            String tagKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
            String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tagKey);
            String messageKey =
                    "javelin.jdbc.instrument.JdbcJavelinTransformer.AddSomeMethodMessage";
            String message = JdbcJavelinMessages.getMessage(messageKey, className, "executeBatch");
            SystemLogger.getInstance().info(jdbcJavelinTag + message);
        }
    }

    /**
     * clearBatchメソッドを埋め込む。
     * @param pool クラスプール
     * @param className クラス名
     * @param code コード
     * @throws NotFoundException clearBatchが実装されていないとき。
     * @throws CannotCompileException コンパイルできないとき
     */
    private void addClearBatchMethod(final ClassPool pool, final String className, final String code)
        throws NotFoundException,
            CannotCompileException
    {
        addNonParamMethod(pool, className, "clearBatch", code);
        CtClass ctClass = pool.get(className);
        CtMethod method = ctClass.getDeclaredMethod("clearBatch");
        JdbcJavelinConverter.delSqlFromField(ctClass, method);
    }

    /**
     * closeメソッドを埋め込む。
     * @param pool クラスプール
     * @param className クラス名
     * @throws NotFoundException closeが実装されていないとき。
     * @throws CannotCompileException コンパイルできないとき
     */
    private void addCloseMethod(final ClassPool pool, final String className)
        throws NotFoundException,
            CannotCompileException
    {
        addNonParamMethod(pool, className, "close",
                          JdbcJavelinConverter.BCI_METHOD_PLANFORPREPARED_CLOSE);
    }
}
