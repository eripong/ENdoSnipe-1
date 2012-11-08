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
package jp.co.acroquest.endosnipe.data.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.util.SQLUtil;
import jp.co.acroquest.endosnipe.common.util.StreamUtil;
import jp.co.acroquest.endosnipe.data.ENdoSnipeDataAccessorPluginProvider;
import jp.co.acroquest.endosnipe.data.LogMessageCodes;
import jp.co.acroquest.endosnipe.data.TableNames;
import jp.co.acroquest.endosnipe.data.db.DBManager;
import jp.co.acroquest.endosnipe.data.entity.JavelinLog;
import jp.co.acroquest.endosnipe.data.util.ZipUtil;
import jp.co.acroquest.endosnipe.util.ResourceDataDaoUtil;

/**
 * {@link JavelinLog} のための DAO です。
 * 
 * @author y-komori
 */
public class JavelinLogDao extends AbstractDao implements LogMessageCodes, TableNames
{
    private static final ENdoSnipeLogger LOGGER =
            ENdoSnipeLogger.getLogger(JavelinLogDao.class,
                                      ENdoSnipeDataAccessorPluginProvider.INSTANCE);

    /** ZIP 圧縮用ストリームのバッファサイズ */
    private static final int BUF_SIZE = 8192;

    /** 蓄積期間を取得する SQL */
    private static final String GET_LOG_TERM_SQL_PARTITION = createGetLogTermSql();

    /** 蓄積期間を取得する SQL */
    private static final String          GET_LOG_TERM_SQL           =
                                                                        "select min(START_TIME) START_TIME, max(END_TIME) END_TIME from "
                                                                            + JAVELIN_LOG;


    /**
     * データを挿入するテーブルの名前を返します。
     *
     * @param date 挿入するデータの日付
     * @return テーブル名
     */
    public static String getTableNameToInsert(final Date date)
    {
        String tableName = ResourceDataDaoUtil.getTableNameToInsert(date, JAVELIN_LOG);
        return tableName;
    }

    /**
     * {@link JavelinLog} オブジェクトを挿入します。<br />
     *
     * @param database 挿入先データベース名
     * @param javelinLog 対象オブジェクト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static void insert(final String database, final JavelinLog javelinLog)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        InputStream is = null;
        String tableName = getTableNameToInsert(javelinLog.endTime);
        try
        {
            conn = getConnection(database);
            String sql =
                    "insert into " + tableName + " (" + "SESSION_ID, " + "SEQUENCE_ID, "
                            + "JAVELIN_LOG, " + "LOG_FILE_NAME, " + "START_TIME, END_TIME, "
                            + "SESSION_DESC, " + "LOG_TYPE, " + "CALLEE_NAME, "
                            + "CALLEE_SIGNATURE, " + "CALLEE_CLASS, " + "CALLEE_FIELD_TYPE, "
                            + "CALLEE_OBJECTID, " + "CALLER_NAME, " + "CALLER_SIGNATURE, "
                            + "CALLER_CLASS, " + "CALLER_OBJECTID, " + "EVENT_LEVEL, "
                            + "ELAPSED_TIME, " + "MODIFIER, " + "THREAD_NAME, " + "THREAD_CLASS, "
                            + "THREAD_OBJECTID" + ") values (" + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            PreparedStatement delegated = getDelegatingStatement(pstmt);
            // CHECKSTYLE:OFF
            delegated.setLong(1, javelinLog.sessionId);
            delegated.setInt(2, javelinLog.sequenceId);
            ByteArrayOutputStream baos = zip(javelinLog.javelinLog, javelinLog.logFileName);
            delegated.setBytes(3, baos.toByteArray());
            delegated.setString(4, javelinLog.logFileName);
            delegated.setTimestamp(5, javelinLog.startTime);
            delegated.setTimestamp(6, javelinLog.endTime);
            delegated.setString(7, javelinLog.sessionDesc);
            delegated.setInt(8, javelinLog.logType);
            delegated.setString(9, javelinLog.calleeName);
            delegated.setString(10, javelinLog.calleeSignature);
            delegated.setString(11, javelinLog.calleeClass);
            delegated.setString(12, javelinLog.calleeFieldType);
            delegated.setInt(13, javelinLog.calleeObjectId);
            delegated.setString(14, javelinLog.callerName);
            delegated.setString(15, javelinLog.callerSignature);
            delegated.setString(16, javelinLog.callerClass);
            delegated.setInt(17, javelinLog.callerObjectId);
            delegated.setInt(18, javelinLog.eventLevel);
            delegated.setLong(19, javelinLog.elapsedTime);
            delegated.setString(20, javelinLog.modifier);
            delegated.setString(21, javelinLog.threadName);
            delegated.setString(22, javelinLog.threadClass);
            delegated.setInt(23, javelinLog.threadObjectId);
            // CHECKSTYLE:ON

            pstmt.execute();
        }
        catch (IOException ex)
        {
            LOGGER.log(EXCEPTION_OCCURED_WITH_RESASON, ex, ex.getMessage());
        }
        catch (SQLException ex)
        {
            LOGGER.log(DB_ACCESS_ERROR, ex, ex.getMessage());
            throw ex;
        }
        finally
        {
            StreamUtil.closeStream(is);
            SQLUtil.closeStatement(pstmt);
            SQLUtil.closeConnection(conn);
        }
    }

    /**
     * Javelin ログ ID を指定してレコードを取得します。<br />
     * JAVELIN_LOG テーブルに対して検索を行い、見つかったレコードを 1 件返します。<br />
     * 
     * {@link JavelinLog#javelinLog} は取得しません。
     * 別途、 {@link JavelinLogDao#selectJavelinLogByLogId(String, long)} を使用してください。
     *
     * @param database データベース名
     * @param logId ログ ID
     * @return レコード。取得できない場合は <code>null</code>
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static JavelinLog selectByLogId(final String database, final long logId)
        throws SQLException
    {
        JavelinLog javelinLog = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        conn = getConnection(database, true);

        try
        {
            String sql =
                    "select LOG_ID, SESSION_ID, SEQUENCE_ID, JAVELIN_LOG, LOG_FILE_NAME, "
                            + "START_TIME, END_TIME, SESSION_DESC, LOG_TYPE, "
                            + "CALLEE_NAME, CALLEE_SIGNATURE, CALLEE_CLASS, "
                            + "CALLEE_FIELD_TYPE, CALLEE_OBJECTID, CALLER_NAME, "
                            + "CALLER_SIGNATURE, CALLER_CLASS, CALLER_OBJECTID, "
                            + "EVENT_LEVEL, ELAPSED_TIME, MODIFIER, THREAD_NAME, "
                            + "THREAD_CLASS, THREAD_OBJECTID from " + JAVELIN_LOG
                            + " where LOG_ID = ?";
            pstmt = conn.prepareStatement(sql);
            PreparedStatement delegated = getDelegatingStatement(pstmt);
            delegated.setLong(1, logId);
            rs = delegated.executeQuery();

            if (rs.next() == true)
            {
                javelinLog = new JavelinLog();
                setJavelinLogFromResultSet(javelinLog, rs);
            }
        }
        finally
        {
            SQLUtil.closeResultSet(rs);
            SQLUtil.closeStatement(pstmt);
            SQLUtil.closeConnection(conn);
        }

        return javelinLog;
    }

    /**
     * Javelin ログファイル名を指定してレコードを取得します。<br />
     * JAVELIN_LOG テーブルに対して検索を行い、見つかったレコードをすべて返します。<br />
     * 
     * {@link JavelinLog#javelinLog} を取得します。
     *
     * @param database データベース名
     * @param fileName ログファイル名
     * @return レコード。取得できない場合は <code>null</code>
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static JavelinLog selectByLogFileNameWithBinary(final String database, final String fileName)
        throws SQLException
    {
        return selectByLogFileName(database, fileName, true);
    }
    
    /**
     * Javelin ログファイル名を指定してレコードを取得します。<br />
     * JAVELIN_LOG テーブルに対して検索を行い、見つかったレコードをすべて返します。<br />
     * 
     * {@link JavelinLog#javelinLog} は取得しません。
     * 別途、 {@link JavelinLogDao#selectJavelinLogByLogId(String, long)} を使用してください。
     *
     * @param database データベース名
     * @param fileName ログファイル名
     * @return レコード。取得できない場合は <code>null</code>
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static JavelinLog selectByLogFileName(final String database, final String fileName)
        throws SQLException
    {
        return selectByLogFileName(database, fileName, false);
    }
    
    /**
     * Javelin ログファイル名を指定してレコードを取得します。<br />
     * JAVELIN_LOG テーブルに対して検索を行い、見つかったレコードをすべて返します。<br />
     * 
     * {@link JavelinLog#javelinLog} は取得しません。
     * 別途、 {@link JavelinLogDao#selectJavelinLogByLogId(String, long)} を使用してください。
     *
     * @param database データベース名
     * @param fileName ログファイル名
     * @param outputLog trueの場合は{@link JavelinLog#javelinLog}を取得する。falseの場合は取得しない。
     * @return レコード。取得できない場合は <code>null</code>
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    private static JavelinLog selectByLogFileName(final String database, final String fileName,
        final boolean outputLog)
        throws SQLException
    {
        JavelinLog javelinLog = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        conn = getConnection(database, true);

        try
        {
            String sql =
                    "select LOG_ID, SESSION_ID, SEQUENCE_ID, JAVELIN_LOG, LOG_FILE_NAME, "
                            + "START_TIME, END_TIME, SESSION_DESC, LOG_TYPE, "
                            + "CALLEE_NAME, CALLEE_SIGNATURE, CALLEE_CLASS, "
                            + "CALLEE_FIELD_TYPE, CALLEE_OBJECTID, CALLER_NAME, "
                            + "CALLER_SIGNATURE, CALLER_CLASS, CALLER_OBJECTID, "
                            + "EVENT_LEVEL, ELAPSED_TIME, MODIFIER, THREAD_NAME, "
                            + "THREAD_CLASS, THREAD_OBJECTID from " + JAVELIN_LOG
                            + " where LOG_FILE_NAME = ?";
            pstmt = conn.prepareStatement(sql);
            PreparedStatement delegated = getDelegatingStatement(pstmt);
            delegated.setString(1, fileName);
            rs = delegated.executeQuery();

            if (rs.next() == true)
            {
                javelinLog = new JavelinLog();
                setJavelinLogFromResultSet(javelinLog, rs, outputLog);
            }
        }
        finally
        {
            SQLUtil.closeResultSet(rs);
            SQLUtil.closeStatement(pstmt);
            SQLUtil.closeConnection(conn);
        }

        return javelinLog;
    }

    /**
     * 期間を指定して全ホストのレコードを取得します。<br />
     * 開始時刻、終了時刻がnull の場合は、指定が行われていないものとし、<br />
     * 全データを取得します。
     *
     * {@link JavelinLog#javelinLog} は取得しません。
     * 別途、 {@link JavelinLogDao#selectJavelinLogByLogId(String, long)} を使用してください。
     * 
     * @param database データベース名
     * @param start 開始時刻
     * @param end 終了時刻
     * @return {@link JavelinLog} のリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<JavelinLog> selectByTerm(final String database, final Timestamp start,
            final Timestamp end)
        throws SQLException
    {
        return selectByTerm(database, start, end, false);
    }

    /**
     * 期間を指定して全ホストのレコードを取得します。<br />
     * 開始時刻、終了時刻がnull の場合は、指定が行われていないものとし、<br />
     * 全データを取得します。
     *
     * このメソッドは{@link JavelinLog#javelinLog} を取得します。
     * オブジェクトサイズが大きくなる場合があるので、注意してください。
     * 
     * @param database データベース名
     * @param start 開始時刻
     * @param end 終了時刻
     * @return {@link JavelinLog} のリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<JavelinLog> selectByTermWithLog(final String database,
        final Timestamp start, final Timestamp end)
        throws SQLException
    {
        return selectByTerm(database, start, end, true);
    }

    /**
     * 期間を指定して全ホストのレコードを取得します。<br />
     * 開始時刻、終了時刻がnull の場合は、指定が行われていないものとし、<br />
     * 全データを取得します。
     *
     * {@link JavelinLog#javelinLog} は取得しません。
     * 別途、 {@link JavelinLogDao#selectJavelinLogByLogId(String, long)} を使用してください。
     * 
     * @param database データベース名
     * @param start 開始時刻
     * @param end 終了時刻
     * @param outputLog Javelinログを出力する場合<code>true</code>
     * @return {@link JavelinLog} のリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<JavelinLog> selectByTerm(final String database, final Timestamp start,
        final Timestamp end, final boolean outputLog)
        throws SQLException
    {
        List<JavelinLog> result = new ArrayList<JavelinLog>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {

            conn = getConnection(database, true);
            String sql = createSelectSqlByTerm(JAVELIN_LOG, start, end);
            pstmt = conn.prepareStatement(sql);
            PreparedStatement delegated = getDelegatingStatement(pstmt);
            setTimestampByTerm(delegated, start, end);
            rs = delegated.executeQuery();

            // 結果をリストに１つずつ格納する
            while (rs.next() == true)
            {
                JavelinLog log = new JavelinLog();
                setJavelinLogFromResultSet(log, rs, outputLog);
                result.add(log);
            }
        }
        finally
        {
            SQLUtil.closeResultSet(rs);
            SQLUtil.closeStatement(pstmt);
            SQLUtil.closeConnection(conn);
        }

        return result;
    }

    /**
     * 時刻の指定に応じたSELECT文のSQLを作成します。<br />
     * 
     * @param tableName テーブル名
     * @param start 開始時刻
     * @param end 終了時刻
     * @return
     */
    private static String createSelectSqlByTerm(final String tableName, final Timestamp start,
            final Timestamp end)
    {
        String sql = "select * from " + tableName;
        if (start != null && end != null)
        {
            sql += " where ? <= START_TIME and END_TIME <= ?";
        }
        else if (start != null && end == null)
        {
            sql += " where ? <= START_TIME";
        }
        else if (start == null && end != null)
        {
            sql += " where END_TIME <= ?";
        }
        sql += " order by START_TIME desc";
        return sql;
    }

    /**
     * 時刻の指定に応じたSELECT文のSQLを作成します。<br />
     * 
     * @param delegated PreparedStatement
     * @param start 開始時刻
     * @param end 終了時刻
     */
    private static void setTimestampByTerm(final PreparedStatement delegated,
            final Timestamp start, final Timestamp end)
        throws SQLException
    {
        if (start != null && end != null)
        {
            delegated.setTimestamp(1, start);
            delegated.setTimestamp(2, end);
        }
        else if (start != null && end == null)
        {
            delegated.setTimestamp(1, start);
        }
        else if (start == null && end != null)
        {
            delegated.setTimestamp(1, end);
        }
    }

    /**
     * すべてのレコードを取得します。<br />
     * 
     * @param database データベース名
     * @return {@link JavelinLog} のリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<JavelinLog> selectAll(final String database)
        throws SQLException
    {
        List<JavelinLog> result = new ArrayList<JavelinLog>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);

            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from " + JAVELIN_LOG + " order by START_TIME desc");

            // 結果をリストに１つずつ格納する
            while (rs.next())
            {
                JavelinLog log = new JavelinLog();
                setJavelinLogFromResultSet(log, rs);
                result.add(log);
            }
        }
        finally
        {
            SQLUtil.closeResultSet(rs);
            SQLUtil.closeStatement(stmt);
            SQLUtil.closeConnection(conn);
        }

        return result;
    }

    /**
     * １レコードを Javelin ログエンティティに格納します。<br />
     * ただし、JavelinLog フィールドは取得しません。<br />
     * 
     * @param log 格納先 Javelin ログエンティティ
     * @param rs {@link ResultSet} オブジェクト
     * @throws SQLException SQL 実行結果取得時に例外が発生した場合
     */
    private static void setJavelinLogFromResultSet(final JavelinLog log, final ResultSet rs)
        throws SQLException
    {
        setJavelinLogFromResultSet(log, rs, false);
    }

    /**
     * １レコードを Javelin ログエンティティに格納します。<br />
     * ただし、JavelinLog フィールドは取得しません。<br />
     * 
     * @param log 格納先 Javelin ログエンティティ
     * @param rs {@link ResultSet} オブジェクト
     * @throws SQLException SQL 実行結果取得時に例外が発生した場合
     */
    private static void setJavelinLogFromResultSet(final JavelinLog log, final ResultSet rs,
        final boolean outputLog)
        throws SQLException
    { // CHECKSTYLE:OFF
        log.logId = rs.getLong(1);
        log.sessionId = rs.getLong(2);
        log.sequenceId = rs.getInt(3);
        if (outputLog == true)
        {
            InputStream is = rs.getBinaryStream(4);
            try
            {
                log.javelinLog = ZipUtil.unzipFromByteArray(is);
            }
            catch (IOException ex)
            {
                log.javelinLog = null;
            }
        }
        log.logFileName = rs.getString(5);
        log.startTime = rs.getTimestamp(6);
        log.endTime = rs.getTimestamp(7);
        log.sessionDesc = rs.getString(8);
        log.logType = rs.getInt(9);
        log.calleeName = rs.getString(10);
        log.calleeSignature = rs.getString(11);
        log.calleeClass = rs.getString(12);
        log.calleeFieldType = rs.getString(13);
        log.calleeObjectId = rs.getInt(14);
        log.callerName = rs.getString(15);
        log.callerSignature = rs.getString(16);
        log.callerClass = rs.getString(17);
        log.callerObjectId = rs.getInt(18);
        log.eventLevel = rs.getInt(19);
        log.elapsedTime = rs.getLong(20);
        log.modifier = rs.getString(21);
        log.threadName = rs.getString(22);
        log.threadClass = rs.getString(23);
        log.threadObjectId = rs.getInt(24);
        // CHECKSTYLE:ON
    }

    /**
     * テーブルに記録されているログの期間を返します。<br />
     *
     * @param database データベース名
     * @return 開始日時、終了日時の配列、取得に失敗した場合は空の配列
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static Timestamp[] getLogTerm(final String database)
        throws SQLException
    {
        Connection conn = null;
        Timestamp[] result = new Timestamp[0];
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(getLogTermSql());
            if (rs.next() == true)
            {
                result = new Timestamp[2];
                result[0] = rs.getTimestamp(1);
                result[1] = rs.getTimestamp(2);
            }
        }
        finally
        {
            SQLUtil.closeResultSet(rs);
            SQLUtil.closeStatement(stmt);
            SQLUtil.closeConnection(conn);
        }
        return result;
    }

    /**
     * 期間を取得するSQLを取得する。
     * @return　期間を取得するSQL
     */
    private static String getLogTermSql()
    {
        if (DBManager.isDefaultDb())
        {
            return GET_LOG_TERM_SQL;
        }
        else
        {
            return GET_LOG_TERM_SQL_PARTITION;
        }
    }

    /**
     * ログ ID を指定して Javelin ログを取得します。
     *
     * @param database データベース名
     * @param logId ログ ID
     * @return Javelin ログ
     * @throws SQLException SQL 実行時に例外が発生した場合
     * @throws IOException 入出力エラーが発生した場合
     */
    public static InputStream selectJavelinLogByLogId(final String database, final long logId)
        throws SQLException,
            IOException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        InputStream result = null;
        try
        {
            conn = getConnection(database, true);
            String sql = "select JAVELIN_LOG from " + JAVELIN_LOG + " where LOG_ID = ?";
            pstmt = conn.prepareStatement(sql);
            PreparedStatement delegated = getDelegatingStatement(pstmt);
            delegated.setLong(1, logId);

            rs = delegated.executeQuery();

            if (rs.next() == true)
            {
                InputStream is = rs.getBinaryStream(1);
                result = ZipUtil.unzipFromByteArray(is);
            }
        }
        finally
        {
            SQLUtil.closeResultSet(rs);
            SQLUtil.closeStatement(pstmt);
            SQLUtil.closeConnection(conn);
        }

        return result;
    }

    /**
     * 時刻を指定して、それより古いレコードを削除します。
     * 削除期限時刻のキーとしては、セッション終了時刻を基準とします。
     * 
     * @param database データベース名
     * @param deleteLimit 削除期限時刻
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static void deleteOldRecordByTime(final String database, final Timestamp deleteLimit)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try
        {
            conn = getConnection(database, true);
            String sql = "delete from " + JAVELIN_LOG + " where END_TIME <= ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setTimestamp(1, deleteLimit);
            pstmt.execute();
        }
        finally
        {
            SQLUtil.closeStatement(pstmt);
            SQLUtil.closeConnection(conn);
        }
    }

    /**
     * すべてのレコードを削除します。<br />
     *
     * @param database データベース名
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static void deleteAll(final String database)
        throws SQLException
    {
        deleteAll(database, JAVELIN_LOG);
    }

    /**
     * 指定したインデックスのテーブルを truncate します。
     *
     * @param database データベース名
     * @param tableIndex テーブルインデックス
     * @param year 次にこのテーブルに入れるデータの年
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static void truncate(final String database, final int tableIndex, final int year)
        throws SQLException
    {
        String tableName = String.format("%s_%02d", JAVELIN_LOG, tableIndex);
        truncate(database, tableName);
        alterCheckConstraint(database, tableName, tableIndex, "END_TIME", year);
    }

    /**
     * レコードの数を返します。<br />
     * 
     * @param database データベース名
     * @return レコードの数
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static int count(final String database)
        throws SQLException
    {
        int count = count(database, JAVELIN_LOG, "LOG_ID");
        return count;
    }

    private static ByteArrayOutputStream zip(final InputStream is, final String path)
        throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream(BUF_SIZE);
        ZipUtil.createZip(out, is, path);
        return out;
    }

    /**
     * 蓄積期間を取得する SQL を生成します。
     *
     * @return SQL
     */
    private static String createGetLogTermSql()
    {
        String unionSql = "";
        StringBuilder sql = new StringBuilder("select min(START_TIME), max(END_TIME) from (");
        for (int index = 1; index <= ResourceDataDaoUtil.PARTITION_TABLE_COUNT; index++)
        {
            sql.append(unionSql);
            String tableName = ResourceDataDaoUtil.getTableName(JAVELIN_LOG, index);
            sql.append("select min(START_TIME) START_TIME, max(END_TIME) END_TIME from ");
            sql.append(tableName);
            unionSql = " union all ";
        }
        sql.append(") MERGED_TIME");
        return sql.toString();
    }
}
