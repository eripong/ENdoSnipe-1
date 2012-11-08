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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.common.util.SQLUtil;
import jp.co.acroquest.endosnipe.data.entity.ArchivedValue;

/**
 * {@link ArchivedValue} のための DAO です。
 *
 * @author y-sakamoto
 */
public class ArchivedValueDao extends AbstractDao
{
    /** ARCHIVED_VALUE テーブル名。 */
    private static final String ARCHIVED_VALUE_TABLE = "ARCHIVED_VALUE";

    /** 計測 No. の値を生成するシーケンス名。 */
    private static final String SEQ_MEASUREMENT_NUM = "SEQ_MEASUREMENT_NUM";

    /**
     * レコードを挿入します。<br />
     *
     * {@link ArchivedValue#measurementValueId} は使用されません。
     *
     * @param database データベース名
     * @param measurementValue 挿入する値
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static void insert(final String database, ArchivedValue measurementValue)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = getConnection(database);
            String sql =
                    "insert into " + ARCHIVED_VALUE_TABLE + "(" + "MEASUREMENT_NUM, " + "HOST_ID, "
                            + "MEASUREMENT_TIME, " + "MEASUREMENT_TYPE, " + "MEASUREMENT_ITEM_ID, "
                            + "VALUE" + ")" + " values (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            // CHECKSTYLE:OFF
            pstmt.setLong(1, measurementValue.measurementNum);
            pstmt.setInt(2, measurementValue.hostId);
            pstmt.setTimestamp(3, measurementValue.measurementTime);
            pstmt.setInt(4, measurementValue.measurementType);
            pstmt.setInt(5, measurementValue.measurementItemId);
            pstmt.setObject(6, measurementValue.value);
            // CHECKSTYLE:ON
            pstmt.execute();
        }
        finally
        {
            SQLUtil.closeStatement(pstmt);
            SQLUtil.closeConnection(conn);
        }
    }

    /**
     * すべてのレコードを取得します。<br />
     *
     * @param database データベース名
     * @return {@link ArchivedValue} オブジェクトのリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<ArchivedValue> selectAll(final String database)
        throws SQLException
    {
        List<ArchivedValue> result = new ArrayList<ArchivedValue>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);
            stmt = conn.createStatement();
            String sql = "select * from " + ARCHIVED_VALUE_TABLE + " order by MEASUREMENT_TIME";
            rs = stmt.executeQuery(sql);
            getMeasurementValuesFromResultSet(result, rs);
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
     * 期間を指定してレコードを取得します。<br />
     *
     * レコードは時刻で昇順に並べ替えて返します。
     *
     * @param database データベース名
     * @param start 開始時刻
     * @param end 終了時刻
     * @return {@link ArchivedValue} のリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<ArchivedValue> selectByTerm(final String database, final Timestamp start,
            final Timestamp end)
        throws SQLException
    {
        List<ArchivedValue> result = new ArrayList<ArchivedValue>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);
            String sql =
                    "select * from " + ARCHIVED_VALUE_TABLE + " where "
                            + "MEASUREMENT_TIME >= ? and MEASUREMENT_TIME <= ? "
                            + "order by MEASUREMENT_TIME";
            pstmt = conn.prepareStatement(sql);
            pstmt.setTimestamp(1, start);
            pstmt.setTimestamp(2, end);
            rs = pstmt.executeQuery();
            getMeasurementValuesFromResultSet(result, rs);
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
     * {@link ResultSet} から {@link ArchivedValue} のリストを作成します。<br />
     *
     * @param result {@link ArchivedValue} オブジェクトの格納先
     * @param rs {@link ResultSet}
     * @throws SQLException SQL 実行結果取得時に例外が発生した場合
     */
    private static void getMeasurementValuesFromResultSet(List<ArchivedValue> result, ResultSet rs)
        throws SQLException
    {
        while (rs.next() == true)
        {
            ArchivedValue measurementValue = new ArchivedValue();
            // CHECKSTYLE:OFF
            measurementValue.measurementValueId = rs.getLong(1);
            measurementValue.measurementNum = rs.getLong(2);
            measurementValue.hostId = rs.getInt(3);
            measurementValue.measurementTime = rs.getTimestamp(4);
            measurementValue.measurementType = rs.getInt(5);
            measurementValue.measurementItemId = rs.getInt(6);
            measurementValue.value = rs.getBigDecimal(7);
            // CHECKSTYLE:ON
            result.add(measurementValue);
        }
    }

    /**
     * Javelin 計測値テーブルに登録されているデータの最小時刻と最大時刻を返します。<br />
     *
     * @param database データベース名
     * @return Javelin 計測値テーブルに登録されているデータの (最小時刻, 最大時刻) を表す配列
     *         （失敗した場合、それぞれの要素は <code>null</code> ）
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static Timestamp[] getTerm(final String database)
        throws SQLException
    {
        Timestamp[] result = new Timestamp[2];
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);
            stmt = conn.createStatement();
            String sql =
                    "select min(MEASUREMENT_TIME), max(MEASUREMENT_TIME) from "
                            + ARCHIVED_VALUE_TABLE;
            rs = stmt.executeQuery(sql);
            if (rs.next() == true)
            {
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
     * 時刻を指定して、それより古いレコードを削除します。
     * 削除期限時刻のキーとしては、計測時刻を基準とします。
     * 
     * @param database データベース名
     * @param deleteLimit 削除期限時刻
     * @param hostId ホストID
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static void deleteOldRecordByTime(final String database, final Timestamp deleteLimit,
            final int hostId)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try
        {
            conn = getConnection(database, true);
            String sql = "delete from " + ARCHIVED_VALUE_TABLE + " where MEASUREMENT_TIME <= ?";
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
        deleteAll(database, ARCHIVED_VALUE_TABLE);
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
        int count = count(database, ARCHIVED_VALUE_TABLE, "MEASUREMENT_VALUE_ID");
        return count;
    }

    /**
     * 計測 No. の値を生成します。 <br />
     *
     * @param database データベース名
     * @return 計測 No. の値
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static int createMeasurementNum(final String database)
        throws SQLException
    {
        int value = createValueFromSequenceId(database, SEQ_MEASUREMENT_NUM);
        return value;
    }

}
