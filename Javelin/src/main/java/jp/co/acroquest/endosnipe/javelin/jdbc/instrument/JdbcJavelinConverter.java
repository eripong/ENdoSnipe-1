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

import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.jdbc.common.JdbcJavelinMessages;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.DBProcessor;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.JdbcJavelinConnection;
import jp.co.acroquest.endosnipe.javelin.jdbc.stats.JdbcJavelinRecorder;
import jp.co.smg.endosnipe.javassist.CannotCompileException;
import jp.co.smg.endosnipe.javassist.ClassPool;
import jp.co.smg.endosnipe.javassist.CtBehavior;
import jp.co.smg.endosnipe.javassist.CtClass;
import jp.co.smg.endosnipe.javassist.CtField;
import jp.co.smg.endosnipe.javassist.CtMethod;
import jp.co.smg.endosnipe.javassist.Modifier;
import jp.co.smg.endosnipe.javassist.NotFoundException;

/**
 * 
 * 
 */
public class JdbcJavelinConverter
{
    /**
     * BCIにてsetXXXに埋め込むコード（パターンA）
     * 1.PreparedStatementオブジェクトのバインド変数保持フィールド（List）を取得する
     * 2.TreeMapが無ければ、作成してListに登録する
     * 3.バインド変数をTreeMapに登録する。その際StatsUtil#toStrでString型に変換する。
     * 　また設定値にて文字列長制限する。
     */
    protected static final String BCI_METHOD_A =
            "if(this != null) {                                                                 \n"
                    + "jp.co.acroquest.endosnipe.javelin.jdbc.stats.BindValUtil.recordBindVal(\n"
                    + "    this.getJdbcJavelinBindVal(), this.jdbcJavelinBindValIndex_,         \n"
                    + "    $1, $2, true);                                                       \n"
                    + "}                                                                        \n";

    /**
     * BCIにてsetXXXに埋め込むコード（パターンB）
     * 1.PreparedStatementオブジェクトのバインド変数保持フィールド（List）を取得する
     * 2.TreeMapが無ければ、作成してListに登録する
     * 3.バインド変数をTreeMapに登録する。その際StatsUtil#toStrでString型に変換する。
     */
    protected static final String BCI_METHOD_B =
            "if(this != null) {                                                                 \n"
                    + "jp.co.acroquest.endosnipe.javelin.jdbc.stats.BindValUtil.recordBindVal(\n"
                    + "    this.getJdbcJavelinBindVal(), this.jdbcJavelinBindValIndex_,         \n"
                    + "    $1, $2, false);                                                      \n"
                    + "}                                                                        \n";

    /**
     * BCIにてsetXXXに埋め込むコード（パターンC）
     * 1.PreparedStatementオブジェクトのバインド変数保持フィールド（List）を取得する
     * 2.TreeMapが無ければ、作成してListに登録する
     * 3.バインド変数をTreeMapに登録する。その際String#valueOfでString型に変換する。
     */
    protected static final String BCI_METHOD_C =
            "if(this != null) {                                                                 \n"
                    + "jp.co.acroquest.endosnipe.javelin.jdbc.stats.BindValUtil.recordBindVal(\n"
                    + "    this.getJdbcJavelinBindVal(), this.jdbcJavelinBindValIndex_,         \n"
                    + "    $1, java.lang.String.valueOf($2));                                   \n"
                    + "}                                                                        \n";

    /**
     * BCIにてsetXXXに埋め込むコード（パターンD）
     * 1.PreparedStatementオブジェクトのバインド変数保持フィールド（List）を取得する
     * 2.TreeMapが無ければ、作成してListに登録する
     * 3.バインド変数をTreeMapに登録する。その際"byte[length]:FFFF..."に変換する。
     */
    protected static final String BCI_METHOD_D =
            "if(this != null) {                                                                 \n"
                    + "jp.co.acroquest.endosnipe.javelin.jdbc.stats.BindValUtil.recordBindVal(\n"
                    + "    this.getJdbcJavelinBindVal(), this.jdbcJavelinBindValIndex_,         \n"
                    + "    $1, $2);                                                             \n"
                    + "}                                                                        \n";

    /**
     * BCIにてsetXXXに埋め込むコード（パターンE）
     * 1.PreparedStatementオブジェクトのバインド変数保持フィールド（List）を取得する
     * 2.TreeMapが無ければ、作成してListに登録する
     * 3.バインド変数"(UNSUPPORTED)"をTreeMapに登録する。
     */
    protected static final String BCI_METHOD_E =
            "if(this != null) {                                                                 \n"
                    + "jp.co.acroquest.endosnipe.javelin.jdbc.stats.BindValUtil.recordBindVal(\n"
                    + "    this.getJdbcJavelinBindVal(), this.jdbcJavelinBindValIndex_,         \n"
                    + "    $1, \"(UNSUPPORTED)\");                                              \n"
                    + "}                                                                        \n";

    /**
     * BCIにてsetXXXに埋め込むコード（パターンF）
     * 1.PreparedStatementオブジェクトのバインド変数保持フィールド（List）を取得する
     * 2.TreeMapが無ければ、作成してListに登録する
     * 3.バインド変数"null"をTreeMapに登録する。その際String#valueOfでString型に変換する。
     */
    protected static final String BCI_METHOD_F =
            "if(this != null) {                                                                 \n"
                    + "jp.co.acroquest.endosnipe.javelin.jdbc.stats.BindValUtil.recordBindVal(\n"
                    + "    this.getJdbcJavelinBindVal(), this.jdbcJavelinBindValIndex_,         \n"
                    + "    $1, \"null\");                                                       \n"
                    + "}                                                                        \n";

    /**
     * BCIにてaddBatchに埋め込むコード（jdbcJavelinBindValIndex_のインクリメント）
     */
    protected static final String BCI_METHOD_ADD_BATCH =
            "if(this != null) {                                         \n"
                    + "	this.jdbcJavelinBindValIndex_++;                \n"
                    + "}                                                \n";

    /**
     * BCIにてclearBatchに埋め込むコード（jdbcJavelinBindValのクリア）
     */
    protected static final String BCI_METHOD_CLEAR_BATCH =
            "if(this != null) {                                         \n"
                    + "	this.getJdbcJavelinBindVal().clear();           \n"
                    + "}                                                \n";

    /**
     * 実行計画取得用PreparedStatementにsetXXXするために、
     * 適したメソッド名に置換する置換対象文字列。
     */
    protected static final String REPLACETARGET_OF_PLANPREPARED = "setXXX";

    /**
     * 実行計画取得用PreparedStatementにsetXXXするためのコード。
     * setXXXは適したメソッド名に置換する。
     */
    protected static final String BCI_METHOD_PLANFORPREPARED_SETXXX =
            "if (this != null && this." + JdbcJavelinTransformer.FLAGFORPLANSTMT_FIELD_NAME
                    + " == false && " + "this." + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + " != null) {\n" + "    int indexOfPreparedStmt = 0;\n"
                    + "    int indexForPlanOfStmt = $1;\n"
                    + "    while (indexForPlanOfStmt > this."
                    + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + "[indexOfPreparedStmt].getBindValCount()) {\n"
                    + "        indexForPlanOfStmt -= this."
                    + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + "[indexOfPreparedStmt].getBindValCount();\n"
                    + "        indexOfPreparedStmt++;\n" + "    }\n" + "    int backup = $1;\n"
                    + "    $1 = indexForPlanOfStmt;\n" + "    this."
                    + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + "[indexOfPreparedStmt].getPreparedStatement()."
                    + REPLACETARGET_OF_PLANPREPARED + "($$);\n" + "    $1 = backup;\n"
                    + "}\n";

    /**
     * 実行計画取得用PreparedStatementにsetXXXするためのコード。
     * setXXXは適したメソッド名に置換する。
     * 以下のメソッドの場合に使用する。
     * setAsciiStream
     * setBinaryStream
     * setUnicodeStream
     */
    protected static final String BCI_METHOD_PLANFORPREPARED_SETINPUTSTREAM =
            "if (this != null && this." + JdbcJavelinTransformer.FLAGFORPLANSTMT_FIELD_NAME
                    + " == false && " + "this." + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + " != null) {\n" + "    int indexOfPreparedStmt = 0;\n"
                    + "    int indexForPlanOfStmt = $1;\n"
                    + "    while (indexForPlanOfStmt > this."
                    + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + "[indexOfPreparedStmt].getBindValCount()) {\n"
                    + "        indexForPlanOfStmt -= this."
                    + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + "[indexOfPreparedStmt].getBindValCount();\n"
                    + "        indexOfPreparedStmt++;\n" + "    }\n" + "    int backup = $1;\n"
                    + "    $1 = indexForPlanOfStmt;\n" + "    this."
                    + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + "[indexOfPreparedStmt].getPreparedStatement()." + "setXXX"
                    + "($1, (java.io.InputStream)"
                    + "new java.io.ByteArrayInputStream(new byte[0]), $3);\n"
                    + "    $1 = backup;\n" + "}\n";

    /**
     * 実行計画取得用PreparedStatementにsetXXXするためのコード。
     * setXXXは適したメソッド名に置換する。
     * 以下のメソッドの場合に使用する。
     * setCharacterStream
     */
    protected static final String BCI_METHOD_PLANFORPREPARED_SETREADER =
            "if (this != null && this." + JdbcJavelinTransformer.FLAGFORPLANSTMT_FIELD_NAME
                    + " == false && " + "this." + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + " != null) {\n" + "    int indexOfPreparedStmt = 0;\n"
                    + "    int indexForPlanOfStmt = $1;\n"
                    + "    while (indexForPlanOfStmt > this."
                    + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + "[indexOfPreparedStmt].getBindValCount()) {\n"
                    + "        indexForPlanOfStmt -= this."
                    + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + "[indexOfPreparedStmt].getBindValCount();\n"
                    + "        indexOfPreparedStmt++;\n" + "    }\n" + "    int backup = $1;\n"
                    + "    $1 = indexForPlanOfStmt;\n" + "    this."
                    + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + "[indexOfPreparedStmt].getPreparedStatement()." + "setXXX"
                    + "($1, new java.io.StringReader(\"\"), $3);\n" + "    $1 = backup;\n" + "}\n";

    /**
     * 実行計画取得用PreparedStatementでaddBatchのときに実行されるコード。
     * パラメタ設定終了フラグをONにする。
     */
    protected static final String BCI_METHOD_PLANFORPREPARED_ADDBATCH =
            "if (this != null && this." + JdbcJavelinTransformer.FLAGFORPLANSTMT_FIELD_NAME
                    + " == false && " + "this." + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + " != null) {\n" + "    this."
                    + JdbcJavelinTransformer.FLAGFORPLANSTMT_FIELD_NAME + " = true;\n" + "}\n";

    /**
     * 実行計画取得用PreparedStatementでclearBatchのときに実行されるコード。
     * パラメタ設定終了フラグをOFFにする。
     */
    protected static final String BCI_METHOD_PLANFORPREPARED_CLEARBATCH =
            "if (this != null && this." + JdbcJavelinTransformer.FLAGFORPLANSTMT_FIELD_NAME
                    + " == false && " + "this." + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + " != null) {\n" + "    " + JdbcJavelinTransformer.FLAGFORPLANSTMT_FIELD_NAME
                    + " = false;\n" + "}\n";

    /**
     * 実行計画取得用PreparedStatementでcloseのときに実行されるコード。
     * 実行計画取得用PreparedStatementをcloseする。
     */
    protected static final String BCI_METHOD_PLANFORPREPARED_CLOSE =
            "if (this != null && this." + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + " != null) {\n" + "    for (int indexOfPlanStmt = 0; indexOfPlanStmt < this."
                    + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + ".length; indexOfPlanStmt++) {\n" + "        this."
                    + JdbcJavelinTransformer.STMTFORPLAN_FIELD_NAME
                    + "[indexOfPlanStmt].getPreparedStatement().close();\n" + "    }\n" + "}\n";

    /** 設定値保持Bean */
    private static final String   JAVELIN_RECORDER_NAME                     =
                                                                                    JdbcJavelinRecorder.class.getName();
    /**
     * Statementのメソッドに対して計測コードを埋め込む。
     *
     * @param pool Statementを含むプール。 
     * @param ctClass Statementを実装するクラス
     * @return 変換結果
     */
    public static CtClass convertConnection(final ClassPool pool, final CtClass ctClass)
    {
        CtClass jvnConnction;
        try
        {
            jvnConnction = pool.get(JdbcJavelinConnection.class.getCanonicalName());
            boolean hasInterface = hasInterface(ctClass, jvnConnction);
            if (hasInterface == false)
            {
                ctClass.addInterface(jvnConnction);
            }
            
            // すでにメソッドを追加している場合は処理を行わない
            if (hasBehavior(ctClass, "getJdbcJavelinProcessor"))
            {
                return ctClass;
            }

            CtField procField =
                                CtField.make("private " + DBProcessor.class.getCanonicalName()
                                        + " dbProcessor_;", ctClass);
            ctClass.addField(procField);
            CtMethod procGetMethod =
                    CtMethod.make("public " + DBProcessor.class.getCanonicalName()
                            + " getJdbcJavelinProcessor(){ return dbProcessor_; }", ctClass);
            ctClass.addMethod(procGetMethod);
            CtMethod procSetMethod =
                                     CtMethod.make("public void setJdbcJavelinProcessor("
                                             + DBProcessor.class.getCanonicalName()
                                             + " dbProcessor){ dbProcessor_ = dbProcessor; }",
                                                   ctClass);
            ctClass.addMethod(procSetMethod);

            CtField urlField = CtField.make("private String jdbcUrl_;", ctClass);
            ctClass.addField(urlField);
            CtMethod urlMethod =
                                 CtMethod.make("public String getJdbcUrl(){ return jdbcUrl_; }",
                                               ctClass);
            ctClass.addMethod(urlMethod);
            CtMethod urlSetMethod =
                                    CtMethod.make("public void setJdbcUrl(String jdbcUrl){ jdbcUrl_ = jdbcUrl; }", ctClass);
            ctClass.addMethod(urlSetMethod);
        }
        catch (NotFoundException ex)
        {
            SystemLogger.getInstance().warn(ex);
            return null;
        }
        catch (CannotCompileException ex)
        {
            SystemLogger.getInstance().warn(ex);
            return null;
        }

        
        
        CtBehavior[] behaviors = ctClass.getDeclaredBehaviors();
        for (int index = 0; index < behaviors.length; index++)
        {
            CtBehavior method = behaviors[index];
            // メソッドの定義がない場合、あるいはpublicでない
            // (->インターフェースに定義されていない)場合は実行しない。
            final int MODIFIER = method.getModifiers();
            if (Modifier.isAbstract(MODIFIER) || !Modifier.isPublic(MODIFIER))
            {
                continue;
            }
            // BCI対象クラス「java.sql.Connection」に対して、コード転換を行う
            String methodName = method.getName();
            try
            {
                if ("prepareStatement".equals(methodName))
                {
                    addSqlToFieldCon(ctClass, method);
                }
                else if ("prepareCall".equals(methodName))
                {
                    addSqlToFieldCon(ctClass, method);
                }
            }
            catch (CannotCompileException ex)
            {
                SystemLogger.getInstance().warn(ex);
            }
        }
        
        try
        {
            return ctClass;
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
            return null;
        }        
    }
    /**
     * Statementのメソッドに対して計測コードを埋め込む。
     *
     * @param pool Statementを含むプール。 
     * @param ctClass Statementを実装するクラス
     * @param method メソッド
     * @param inheritedStatement Statementを実装したクラスを変換する場合は <code>true</code>
     * @param inheritedPreparedStatement PreparedStatementを実装したクラスを変換する場合は <code>true</code>
     */
    public static void convertStatement(final ClassPool pool, final CtClass ctClass,
            final CtBehavior method, boolean inheritedStatement, boolean inheritedPreparedStatement)
    {
        try
        {
            // BCI対象クラス「java.sql.Connection」に対して、コード転換を行う
            String methodName = method.getName();

            // BCI対象クラス「java.sql.Statement」に対して、コード転換を行う
            if (inheritedStatement)
            {
                if (SystemLogger.getInstance().isDebugEnabled())
                {
                    SystemLogger.getInstance().debug(
                                                     "JDBC JAVELIN:-->Running ctMethodName:"
                                                             + methodName);
                }
                if ("execute".equals(methodName))
                {
                    convertStatementMethod(pool, ctClass, method);
                }
                else if ("executeQuery".equals(methodName))
                {
                    convertStatementMethod(pool, ctClass, method);
                }
                else if ("executeUpdate".equals(methodName))
                {
                    convertStatementMethod(pool, ctClass, method);
                }
                else if ("addBatch".equals(methodName))
                {
                    addSqlToFieldStat(ctClass, method);
                }
                else if ("clearBatch".equals(methodName))
                {
                    if (inheritedPreparedStatement == false)
                    {
                        delSqlFromField(ctClass, method);
                    }
                }
                else if ("executeBatch".equals(methodName))
                {
                    convertStatementMethod(pool, ctClass, method);
                }

            }

            // BCI対象クラス「java.sql.PreparedStatement」に対して、コード転換を行う
            if (inheritedPreparedStatement)
            {
                if (SystemLogger.getInstance().isDebugEnabled())
                {
                    SystemLogger.getInstance().debug(
                                                     "JDBC JAVELIN:-->Running ctMethodName:"
                                                             + methodName);
                }
                if ("setString".equals(methodName) || "setObject".equals(methodName))
                {
                    // パターンAのsetter
                    // 文字列長制限ラベルに具体的な設定値を入れる
                    long jdbcStringLimitLength =
                            JdbcJavelinRecorder.getConfig().getJdbcStringLimitLength();
                    String code =
                            BCI_METHOD_A.replaceAll("BCI_METHOD_A_LENGTH",
                                                    String.valueOf(jdbcStringLimitLength));
                    convertPreparedMethod(ctClass, method, code, BCI_METHOD_PLANFORPREPARED_SETXXX);
                }
                else if ("setBigDecimal".equals(methodName) || "setDate".equals(methodName)
                        || "setTime".equals(methodName) || "setTimestamp".equals(methodName))
                {
                    // パターンBのsetter
                    convertPreparedMethod(ctClass, method, BCI_METHOD_B,
                                          BCI_METHOD_PLANFORPREPARED_SETXXX);

                }
                else if ("setBoolean".equals(methodName) || "setShort".equals(methodName)
                        || "setInt".equals(methodName) || "setLong".equals(methodName)
                        || "setFloat".equals(methodName) || "setDouble".equals(methodName))
                {
                    // パターンCのsetter
                    convertPreparedMethod(ctClass, method, BCI_METHOD_C,
                                          BCI_METHOD_PLANFORPREPARED_SETXXX);
                }
                else if ("setByte".equals(methodName) || "setBytes".equals(methodName))
                {
                    // パターンDのsetter
                    convertPreparedMethod(ctClass, method, BCI_METHOD_D,
                                          BCI_METHOD_PLANFORPREPARED_SETXXX);
                }
                else if ("setArray".equals(methodName) || "setBlob".equals(methodName)
                        || "setClob".equals(methodName) || "setRef".equals(methodName)
                        || "setURL".equals(methodName))
                {
                    // パターンEのsetter
                    convertPreparedMethod(ctClass, method, BCI_METHOD_E,
                                          BCI_METHOD_PLANFORPREPARED_SETXXX);
                }
                else if ("setNull".equals(methodName))
                {
                    // パターンFのsetter
                    convertPreparedMethod(ctClass, method, BCI_METHOD_F,
                                          BCI_METHOD_PLANFORPREPARED_SETXXX);
                }
                else if ("setAsciiStream".equals(methodName)
                        || "setBinaryStream".equals(methodName)
                        || "setUnicodeStream".equals(methodName))
                {
                    // TODO Java6.0 でインターフェースの仕様が変わったため、引数の数が3以外のものも対応が必要。
                    if (method.getParameterTypes().length == 3)
                    {
                        // パターンGのsetter
                        convertPreparedMethod(ctClass, method, BCI_METHOD_E,
                                              BCI_METHOD_PLANFORPREPARED_SETINPUTSTREAM);
                    }
                }
                else if ("setCharacterStream".equals(methodName))
                {
                    // TODO Java6.0 でインターフェースの仕様が変わったため、引数の数が3以外のものも対応が必要。
                    if (method.getParameterTypes().length == 3)
                    {
                        // パターンHのsetter
                        convertPreparedMethod(ctClass, method, BCI_METHOD_E,
                                              BCI_METHOD_PLANFORPREPARED_SETREADER);
                    }
                }
                else if ("addBatch".equals(methodName))
                {
                    convertPreparedMethod_addBatch(ctClass, method);
                }
                else if ("clearBatch".equals(methodName))
                {
                    convertPreparedMethod_clearBatch(ctClass, method);
                }
                else if ("close".equals(methodName))
                {
                    convertPreparedMethod_close(ctClass, method);
                }
                if ("execute".equals(methodName) || "executeBatch".equals(methodName)
                        || "executeQuery".equals(methodName) || "executeUpdate".equals(methodName))
                {
                    convertExecuteMethod(ctClass, method);
                }
            }
        }
        catch (Throwable ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
    }

    /**
     * PreparedStatementのexecuteメソッドに、
     * バインド引数保存用ArrayList初期化処理を追加する。
     * 
     * @param ctClass 変換対象のクラス。
     * @param method メソッド。
     * @throws CannotCompileException javassistがコンパイルに失敗した場合。
     */
    private static void convertExecuteMethod(final CtClass ctClass, final CtBehavior method)
        throws CannotCompileException
    {
        String className = ctClass.getName();
        className = className.substring(className.lastIndexOf('.') + 1);

        // 前処理を埋め込む
        String key = "javelin.jdbc.instrument.JdbcJavelinConverter.ModifiedMethodLabel";
        String message = JdbcJavelinMessages.getMessage(key, className, method.getName());
        SystemLogger.getInstance().info(message);
        method.insertAfter(//
                "this.jdbcJavelinBindValIndex_ = 0;" //
                + "this.flagForPlanStmt_ = false;", true);
    }

    /**
     * PreparedStatement用にパターン別のコードを埋め込む
     * 
     * @param ctClass クラス
     * @param method メソッド
     * @param bindValCode バインド変数取得用コード
     * @param explainCodeTemplate 実行計画取得用コード
     * @throws CannotCompileException コード埋め込みに失敗した場合
     */
    public static void convertPreparedMethod(final CtClass ctClass, final CtBehavior method,
            final String bindValCode, final String explainCodeTemplate)
        throws CannotCompileException
    {
        String className = ctClass.getName();
        String methodName = method.getName();
        className = className.substring(className.lastIndexOf('.') + 1);

        // setメソッドの引数の1番目がintのときのみ、
        // 実行計画取得用処理、バインド引数取得処理を追加する
        try
        {
            CtClass[] paramTypes;
            paramTypes = method.getParameterTypes();
            if (paramTypes.length >= 1 && "int".equals(paramTypes[0].getName()))
            {
                // 実行計画取得用PreparedStatementのsetXXXを適したメソッド名に変更する
                String explainCode =
                                     explainCodeTemplate.replaceAll(REPLACETARGET_OF_PLANPREPARED,
                                                                    methodName);
                
                // 前処理を埋め込む
                String key = "javelin.jdbc.instrument.JdbcJavelinConverter.ModifiedMethodLabel";
                String message = JdbcJavelinMessages.getMessage(key, className, methodName);
                SystemLogger.getInstance().info(message);

                method.insertBefore(bindValCode + explainCode);
            }
        }
        catch (NotFoundException e)
        {
            SystemLogger.getInstance().warn(e);
        }

    }

    /**
     * PreparedStatement#addBatch用にコードを埋め込む
     * 
     * @param ctClass PreparedStatementを実装するクラス
     * @param method addBatchメソッド
     * @throws CannotCompileException コード埋め込みに失敗した場合
     */
    public static void convertPreparedMethod_addBatch(final CtClass ctClass, final CtBehavior method)
        throws CannotCompileException
    {
        String className = ctClass.getName();
        className = className.substring(className.lastIndexOf('.') + 1);

        // 前処理を埋め込む
        String key = "javelin.jdbc.instrument.JdbcJavelinConverter.ModifiedMethodLabel";
        String message = JdbcJavelinMessages.getMessage(key, className, method.getName());
        SystemLogger.getInstance().info(message);
        method.insertAfter(BCI_METHOD_ADD_BATCH + BCI_METHOD_PLANFORPREPARED_ADDBATCH);
    }

    /**
     * PreparedStatement#clearBatch用にコードを埋め込む
     * 
     * @param ctClass PreparedStatementを実装するクラス
     * @param method clearBatchメソッド
     * @throws CannotCompileException コード埋め込みに失敗した場合
     */
    public static void convertPreparedMethod_clearBatch(final CtClass ctClass,
            final CtBehavior method)
        throws CannotCompileException
    {
        String className = ctClass.getName();
        className = className.substring(className.lastIndexOf('.') + 1);

        // 前処理を埋め込む
        String key = "javelin.jdbc.instrument.JdbcJavelinConverter.ModifiedMethodLabel";
        String message = JdbcJavelinMessages.getMessage(key, className, method.getName());
        SystemLogger.getInstance().info(message);
        method.insertAfter(BCI_METHOD_CLEAR_BATCH + BCI_METHOD_PLANFORPREPARED_CLEARBATCH);
    }

    /**
     * PreparedStatement#close用にコードを埋め込む
     *
     * @param ctClass PreparedStatementを実装するクラス
     * @param method closeメソッド
     * @throws CannotCompileException コード埋め込みに失敗した場合
     */
    public static void convertPreparedMethod_close(final CtClass ctClass, final CtBehavior method)
        throws CannotCompileException
    {
        String className = ctClass.getName();
        className = className.substring(className.lastIndexOf('.') + 1);

        // 前処理を埋め込む
        String key = "javelin.jdbc.instrument.JdbcJavelinConverter.ModifiedMethodLabel";
        String message = JdbcJavelinMessages.getMessage(key, className, method.getName());
        SystemLogger.getInstance().info(message);
        method.insertAfter(BCI_METHOD_PLANFORPREPARED_CLOSE);
    }

    /**
     * Statementを実装するクラスに計測コードを埋め込む。
     *
     * @param pool クラスプール
     * @param ctClass Statementを実装するクラス
     * @param method 埋め込み対象メソッド
     * @throws CannotCompileException コード埋め込みに失敗した場合
     */
    public static void convertStatementMethod(final ClassPool pool, final CtClass ctClass,
            final CtBehavior method)
        throws CannotCompileException
    {
        // StatsJavelinによる計測コードを埋め込む（Statementの方用）
        addRecordCode(ctClass, method);
        convertCatch(pool, ctClass, method);

        if (SystemLogger.getInstance().isDebugEnabled())
        {
            String tegKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
            String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tegKey);
            String messageKey = "javelin.jdbc.instrument.JdbcJavelinConverter.ConvertLabel";
            String message = JdbcJavelinMessages.getMessage(messageKey, method.getName());
            SystemLogger.getInstance().debug(jdbcJavelinTag + message);
        }
    }

    /**
     * StatsJavelinによる計測コードを埋め込む。
     * 
     * @param ctClass クラス
     * @param method メソッド
     * @throws CannotCompileException メソッドの開始位置と終了位置にコードを埋め込めなかった場合
     */
    public static void addRecordCode(final CtClass ctClass, final CtBehavior method)
        throws CannotCompileException
    {
        // 前処理ログコードを作る
        StringBuffer callPreProcessCodeBuffer = new StringBuffer();
        callPreProcessCodeBuffer.append(JAVELIN_RECORDER_NAME);
        CtClass[] parameterTypes = null;
        boolean paramZero = false;
        try
        {
            parameterTypes = method.getParameterTypes();
        }
        catch (NotFoundException ex)
        {
            SystemLogger.getInstance().warn("", ex);
        }
        if (parameterTypes != null && (parameterTypes.length > 0))
        {
            callPreProcessCodeBuffer.append(".preProcessParam(");
            callPreProcessCodeBuffer.append("$0");
            callPreProcessCodeBuffer.append(", $args);");
        }
        else
        {
            callPreProcessCodeBuffer.append(".preProcessSQLArgs(");
            callPreProcessCodeBuffer.append("$0");
            callPreProcessCodeBuffer.append(", this.getJdbcJavelinSql().toArray());");
            paramZero = true;
        }
        String callPreProcessCode;
        callPreProcessCode = callPreProcessCodeBuffer.toString();

        // 後処理ログコードを作る
        StringBuffer callPostProcessCodeBuffer = new StringBuffer();
        callPostProcessCodeBuffer.append(JAVELIN_RECORDER_NAME);
        // Recorderにて実行計画取得にStatemen、クラス名、メソッド名が必要
        callPostProcessCodeBuffer.append(".postProcessOK($0");
        if (paramZero == false)
        { // 引数の数をpostProcessOKに渡す
            callPostProcessCodeBuffer.append(", 1"); // 引数1以上
        }
        else
        {
            callPostProcessCodeBuffer.append(", 0"); // 引数0
        }

        callPostProcessCodeBuffer.append(");");
        String returnPostProcessCode = callPostProcessCodeBuffer.toString();
        //		JavelinErrorLogger.getInstance().log("modified class:" + className);
        method.insertBefore(callPreProcessCode);
        method.insertAfter(returnPostProcessCode);
    }

    private static void addSqlToFieldCon(final CtClass ctClass, final CtBehavior method)
        throws CannotCompileException
    {
        String addSqlCode =
                JdbcJavelinRecorder.class.getName() + ".postPrepareStatement($0, $1, $_, \""
                        + method.getName() + "\");";
        method.insertAfter(addSqlCode);
        String key = "javelin.jdbc.instrument.JdbcJavelinConverter.ModifiedMethodLabel";
        String message = JdbcJavelinMessages.getMessage(key, ctClass.getName(), method.getName());
        SystemLogger.getInstance().info(message);
        if (SystemLogger.getInstance().isDebugEnabled())
        {
            String tegKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
            String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tegKey);
            String messageKey = "javelin.jdbc.instrument.JdbcJavelinConverter.SQLAddedLabel1";
            String logMessage = JdbcJavelinMessages.getMessage(messageKey, method.getName());
            SystemLogger.getInstance().debug(jdbcJavelinTag + logMessage);
        }
    }

    private static void addSqlToFieldStat(final CtClass ctClass, final CtBehavior method)
        throws CannotCompileException
    {
        try
        {
            CtClass[] parameterTypes = method.getParameterTypes();
            if (!(parameterTypes == null) && (parameterTypes.length > 0))
            {
                // 実行しているSQL文を追加する用コードを追加する（StatementのaddBatchメソッド用）
                String addSqlCode = "if(this != null) this.jdbcJavelinSql_.add($1);";
                method.insertAfter(addSqlCode);

                if (SystemLogger.getInstance().isDebugEnabled())
                {
                    String tagKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
                    String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tagKey);
                    String messageKey =
                            "javelin.jdbc.instrument.JdbcJavelinConverter.SQLAddedLabel2";
                    String logMessage =
                            JdbcJavelinMessages.getMessage(messageKey, method.getName());
                    SystemLogger.getInstance().debug(jdbcJavelinTag + logMessage);
                }
            }
        }
        catch (NotFoundException e)
        {
            SystemLogger.getInstance().warn("", e);
        }
    }

    public static void delSqlFromField(final CtClass ctClass, final CtBehavior method)
        throws CannotCompileException
    {
        // 実行しているSQL文を追加する用コードを追加する（Statementの方）
        String addSqlCode = "if(this != null) this.jdbcJavelinSql_.clear();";
        method.insertAfter(addSqlCode);

        if (SystemLogger.getInstance().isDebugEnabled())
        {
            String tagKey = "javelin.jdbc.instrument.JdbcJavelinConverter.JDBCJavelinTag";
            String jdbcJavelinTag = JdbcJavelinMessages.getMessage(tagKey);
            String messageKey = "javelin.jdbc.instrument.JdbcJavelinConverter.SQLDeletedLabel3";
            String logMessage = JdbcJavelinMessages.getMessage(messageKey, method.getName());
            SystemLogger.getInstance().debug(jdbcJavelinTag + logMessage);
        }
    }

    public static void convertCatch(final ClassPool pool, final CtClass ctClass,
            final CtBehavior behaviour)
        throws CannotCompileException
    {
        try
        {
            CtClass throwable = pool.get("java.lang.Throwable");

            // メソッドの定義がない場合は実行しない。
            final int MODIFIER = behaviour.getModifiers();
            if (Modifier.isAbstract(MODIFIER))
            {
                return;
            }

            // JavelinLogger#writeExceptionLogの呼び出しコードを作成する。
            StringBuffer code = new StringBuffer();

            // 後処理（例外場合）
            code.append(JAVELIN_RECORDER_NAME);
            code.append(".postProcessNG(");
            code.append("$e");
            code.append(");");

            // 例外を再throwする。
            code.append("throw $e;");

            // ログ取得コードをThrowableのcatch節として追加する。
            behaviour.addCatch(code.toString(), throwable);
        }
        catch (NotFoundException nfe)
        {
            SystemLogger.getInstance().warn(nfe);
        }
    }

    public static boolean hasInterface(CtClass targetClass, CtClass interfaceClass)
    {
        boolean hasInterface = false;

        try
        {
            for (CtClass stmtInterface : targetClass.getInterfaces())
            {
                if (stmtInterface.equals(interfaceClass))
                {
                    hasInterface = true;
                    break;
                }
            }
        }
        catch (NotFoundException ex)
        {
            // 何もしない。
        }
        return hasInterface;
    }

    /**
     * 指定したクラスに指定したメソッドが存在するかどうかを調べます。
     *
     * @param targetClass メソッドの存在を調べるクラス
     * @param methodName メソッド名
     * @return メソッドが存在する場合は <code>true</code> 、存在しない場合は <code>false</code>
     */
    public static boolean hasBehavior(final CtClass targetClass, final String methodName)
    {
        boolean method = false;
        CtBehavior[] declaredBehaviors = targetClass.getDeclaredBehaviors();
        for (CtBehavior behavior : declaredBehaviors)
        {
            String behaviorName = behavior.getName();
            if (methodName.equals(behaviorName))
            {
                method = true;
                break;
            }
        }
        return method;
    }

}
