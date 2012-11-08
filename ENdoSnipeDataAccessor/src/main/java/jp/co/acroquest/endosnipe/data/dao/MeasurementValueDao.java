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
import java.util.Date;
import java.util.List;

import jp.co.acroquest.endosnipe.common.util.SQLUtil;
import jp.co.acroquest.endosnipe.data.TableNames;
import jp.co.acroquest.endosnipe.data.db.DBManager;
import jp.co.acroquest.endosnipe.data.dto.MeasurementValueDto;
import jp.co.acroquest.endosnipe.data.entity.MeasurementValue;
import jp.co.acroquest.endosnipe.util.ResourceDataDaoUtil;

import org.apache.log4j.Logger;

/**
 * {@link MeasurementValue} のための DAO です。
 *
 * @author sakamoto
 */
public class MeasurementValueDao extends AbstractDao implements TableNames
{
    /** 時刻でソートするSQL */
    private static final String WITH_NAME_ACCESS_SQL_TIME_ORDER =
            "SELECT mv.measurement_value_id measurement_value_id, mv.measurement_num measurement_num," +
            " mv.measurement_time measurement_time, mv.measurement_type measurement_type," +
            " mv.measurement_item_id measurement_item_id, mv.value resultvalue," +
            " mi.item_name measurement_type_name, mi.display_name measurement_display_name," +
            " ji.item_name measurement_item_name" +
            " FROM measurement_value mv, measurement_info mi," +
            " javelin_measurement_item ji" +
            " WHERE mi.item_name = ? AND mv.measurement_type = mi.measurement_type" +
            " AND mv.measurement_item_id = ji.measurement_item_id" +
            " AND ( mv.measurement_time BETWEEN ? AND ? ) ORDER BY mv.measurement_time";

    private static final String WITH_NAME_SUM_VALUE_ACCESS_SQL_TIME_ORDER =
            "SELECT 0 measurement_value_id, 0 measurement_num," +
            " mv.measurement_time measurement_time," +
            " mv.measurement_type measurement_type," +
            " mv.measurement_item_id measurement_item_id," +
            " sum(mv.value) resultvalue, mi.item_name measurement_type_name," +
            " mi.display_name measurement_display_name, ji.item_name measurement_item_name" +
            " FROM measurement_value mv, measurement_info mi, javelin_measurement_item ji" +
            " WHERE mi.item_name = ? AND mv.measurement_type = mi.measurement_type" +
            " AND mv.measurement_item_id = ji.measurement_item_id" +
            " AND ( mv.measurement_time BETWEEN ? AND ? )" +
            " GROUP BY mv.measurement_type, mv.measurement_item_id," +
            " mv.measurement_time, mi.item_name, mi.display_name," +
            " ji.item_name ORDER BY mv.measurement_time";

    /** 項目名でソートするSQL */
    private static final String WITH_NAME_ACCESS_SQL =
            "SELECT mv.measurement_value_id measurement_value_id, mv.measurement_num measurement_num," +
            " mv.measurement_time measurement_time, mv.measurement_type measurement_type," +
            " mv.measurement_item_id measurement_item_id, mv.value resultvalue," +
            " mi.item_name measurement_type_name, mi.display_name measurement_display_name," +
            " ji.item_name measurement_item_name" +
            " FROM measurement_value mv, measurement_info mi, javelin_measurement_item ji" +
            " WHERE mi.item_name = ? AND mv.measurement_type = mi.measurement_type" +
            " AND mv.measurement_item_id = ji.measurement_item_id" +
            " AND ( mv.measurement_time BETWEEN ? AND ? ) ORDER BY ji.item_name";

    private static final String WITH_NAME_MAX_VALUE_ACCESS_SQL =
            "SELECT 0 measurement_value_id, 0 measurement_num, '1970-01-01' measurement_time," +
            " mv.measurement_type measurement_type, mv.measurement_item_id measurement_item_id," +
            " max(mv.value) resultvalue, mi.item_name measurement_type_name," +
            " mi.display_name measurement_display_name, ji.item_name measurement_item_name" +
            " FROM measurement_value mv, measurement_info mi, javelin_measurement_item ji" +
            " WHERE mi.item_name = ? AND mv.measurement_type = mi.measurement_type" +
            " AND mv.measurement_item_id = ji.measurement_item_id" +
            " AND ( mv.measurement_time BETWEEN ? AND ? )" +
            " GROUP BY mv.measurement_type, mv.measurement_item_id, mi.item_name, mi.display_name, ji.item_name ORDER BY ji.item_name";

    private static final String WITH_NAME_MIN_VALUE_ACCESS_SQL =
            "SELECT 0 measurement_value_id, 0 measurement_num, '1970-01-01' measurement_time," +
            " mv.measurement_type measurement_type, mv.measurement_item_id measurement_item_id," +
            " min(mv.value) resultvalue, mi.item_name measurement_type_name," +
            " mi.display_name measurement_display_name, ji.item_name measurement_item_name" +
            " FROM measurement_value mv, measurement_info mi, javelin_measurement_item ji" +
            " WHERE mi.item_name = ? AND mv.measurement_type = mi.measurement_type" +
            " AND mv.measurement_item_id = ji.measurement_item_id" +
            " AND ( mv.measurement_time BETWEEN ? AND ? )" +
            " GROUP BY mv.measurement_type, mv.measurement_item_id, mi.item_name, mi.display_name," +
            " ji.item_name ORDER BY ji.item_name";

    private static final String WITH_NAME_SUM_VALUE_ACCESS_SQL =
            "SELECT 0 measurement_value_id, 0 measurement_num, '1970-01-01' measurement_time," +
            " mv.measurement_type measurement_type, mv.measurement_item_id measurement_item_id," +
            " sum(mv.value) resultvalue, mi.item_name measurement_type_name," +
            " mi.display_name measurement_display_name, ji.item_name measurement_item_name" +
            " FROM measurement_value mv, measurement_info mi, javelin_measurement_item ji" +
            " WHERE mi.item_name = ? AND mv.measurement_type = mi.measurement_type" +
            " AND mv.measurement_item_id = ji.measurement_item_id" +
            " AND ( mv.measurement_time BETWEEN ? AND ? )" +
            " GROUP BY mv.measurement_type, mv.measurement_item_id, mi.item_name," +
            " mi.display_name, ji.item_name" +
            " ORDER BY ji.item_name";

    /** 系列名一覧を取得するSQL */
    private static final String WITH_NAME_ITEM_NAME =
        "SELECT 0 measurement_value_id, 0 measurement_num, '1970-01-01' measurement_time," +
        " 0 measurement_type, 0 measurement_item_id, 0 resultvalue, '0' measurement_type_name," +
        " '0' measurement_display_name, ji.item_name measurement_item_name" +
        " FROM measurement_info mi, javelin_measurement_item ji" +
        " WHERE mi.item_name = ? AND ji.measurement_type = mi.measurement_type";

    /**
     * 時刻でソートするSQL
     * 系列名も指定する
     */
    private static final String WITH_ITEM_NAME_ACCESS_SQL_TIME_ORDER =
            "SELECT mv.measurement_value_id measurement_value_id, mv.measurement_num measurement_num," +
            " mv.measurement_time measurement_time, mv.measurement_type measurement_type," +
            " mv.measurement_item_id measurement_item_id, mv.value resultvalue," +
            " mi.item_name measurement_type_name, mi.display_name measurement_display_name," +
            " ji.item_name measurement_item_name" +
            " FROM measurement_value mv, measurement_info mi, javelin_measurement_item ji" +
            " WHERE mi.item_name = ? AND mv.measurement_type = mi.measurement_type" +
            " AND ji.item_name = ? AND mv.measurement_item_id = ji.measurement_item_id" +
            " AND ( mv.measurement_time BETWEEN ? AND ? ) ORDER BY mv.measurement_time";

    /** 蓄積期間を取得する SQL */
    private static final String GET_TERM_SQL_PARTIOTION = createGetTermSql();

    /** 蓄積期間を取得する SQL */
    private static final String GET_TERM_SQL                              =
                                                                                  "select min(MEASUREMENT_TIME) MIN_TIME, max(MEASUREMENT_TIME) MAX_TIME from " +
                                                                                          MEASUREMENT_VALUE;

    /**
     * データを挿入するテーブルの名前を返します。
     *
     * @param date 挿入するデータの日付
     * @return テーブル名
     */
    public static String getTableNameToInsert(final Date date)
    {
        String tableName = ResourceDataDaoUtil.getTableNameToInsert(date, MEASUREMENT_VALUE);
        return tableName;
    }

    /**
     * レコードを挿入します。<br />
     *
     * @param database データベース名
     * @param measurementValue 挿入する値
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static void insert(final String database, final MeasurementValue measurementValue)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String tableName = getTableNameToInsert(measurementValue.measurementTime);
        try
        {
            conn = getConnection(database);
            String sql = "insert into " + tableName +
                         " (MEASUREMENT_TIME, MEASUREMENT_ITEM_ID, MEASUREMENT_VALUE)" +
                         " values (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            // CHECKSTYLE:OFF
            pstmt.setTimestamp(1, measurementValue.measurementTime);
            pstmt.setInt(2, measurementValue.measurementItemId);
            pstmt.setObject(3, measurementValue.value);
            
            Logger logger = Logger.getLogger(MeasurementValueDao.class);
            if (logger.isDebugEnabled())
            {
                logger.debug("MeasurementValueDto: SQL=[" + sql + "]" +
                             ", 1=" + measurementValue.measurementTime +
                             ", 2=" + measurementValue.measurementItemId +
                             ", 3=" + measurementValue.value);
            }
           
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
        String tableName = String.format("%s_%02d", MEASUREMENT_VALUE, tableIndex);
        truncate(database, tableName);
        alterCheckConstraint(database, tableName, tableIndex, "MEASUREMENT_TIME", year);
    }

    /**
     * すべてのレコードを取得します。<br />
     *
     * @param database データベース名
     * @return {@link MeasurementValue} オブジェクトのリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<MeasurementValue> selectAll(final String database)
        throws SQLException
    {
        List<MeasurementValue> result = null;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);

            stmt = conn.createStatement();
            String sql = "select * from " + MEASUREMENT_VALUE + " order by MEASUREMENT_TIME";
            rs = stmt.executeQuery(sql);
            result = getMeasurementValuesFromResultSet(rs);
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
     * @return {@link MeasurementValue} のリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<MeasurementValue> selectByTerm(final String database, final Timestamp start,
            final Timestamp end)
        throws SQLException
    {
        List<MeasurementValue> result = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);
            String sql =
                    "select * from " + MEASUREMENT_VALUE + " where "
                            + "MEASUREMENT_TIME >= ? and MEASUREMENT_TIME <= ?"
                            + "order by MEASUREMENT_TIME";
            pstmt = conn.prepareStatement(sql);
            // CHECKSTYLE:OFF
            pstmt.setTimestamp(1, start);
            pstmt.setTimestamp(2, end);
            // CHECKSTYLE:ON
            rs = pstmt.executeQuery();
            result = getMeasurementValuesFromResultSet(rs);
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
     * 期間を指定して、特定のグラフのレコードを取得します。<br />
     *
     * レコードは時刻で昇順に並べ替えて返します。
     *
     * @param database データベース名
     * @param start 開始時刻
     * @param end 終了時刻
     * @param measurementType 計測値種別
     * @return {@link MeasurementValue} のリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<MeasurementValueDto> selectByTermAndMeasurementType(final String database,
            final Timestamp start, final Timestamp end, final long measurementType)
        throws SQLException
    {
        List<MeasurementValueDto> result = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);
            String sql =
                "select " +
                ", mv.measurement_time" +
                ", mv.measurement_item_id" +
                ", mv.value resultvalue" +
                ", jmi.item_name measurement_item_name" +
                " from " + MEASUREMENT_VALUE + " mv, " +
                JAVELIN_MEASUREMENT_ITEM + " jmi" +
                " where"
                    + "mv.measurement_item_id = jmi.measurement_item_id "
                    + " and (mv.MEASUREMENT_TIME between ? and ?)"
                    + " and jmi.MEASUREMENT_ITEM_ID = ?"
                    + " order by mv.MEASUREMENT_TIME";
            pstmt = conn.prepareStatement(sql);
            // CHECKSTYLE:OFF
            pstmt.setTimestamp(1, start);
            pstmt.setTimestamp(2, end);
            pstmt.setLong(3, measurementType);
            // CHECKSTYLE:ON
            rs = pstmt.executeQuery();
            result = getMeasurementValueDtosFromResultSet(rs);
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
     * 期間と系列名を指定して、特定のグラフのレコードを取得します。<br />
     * JMXパラメータを表示するための専用メソッドです。
     *
     * レコードは時刻で昇順に並べ替えて返します。
     *
     * @param database データベース名
     * @param start 開始時刻
     * @param end 終了時刻
     * @param itemName 項目名
     * @return {@link MeasurementValue} のリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<MeasurementValueDto> selectByTermAndJMXItemName(final String database,
            final Timestamp start, final Timestamp end, final String itemName)
        throws SQLException
    {
        List<MeasurementValueDto> result = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);
            String sql =
                "select " +
                " mv.measurement_value_id" +
                ", mv.measurement_num" +
                ", mv.measurement_time" +
                ", mv.measurement_type" +
                ", mv.measurement_item_id" +
                ", mv.value resultvalue" +
                ", jmi.item_name measurement_item_name" +
                ", 'jmx' || '' measurement_type_name" +
                ", jmi.item_name measurement_display_name" +
                " from " + MEASUREMENT_VALUE + " mv, " + JAVELIN_MEASUREMENT_ITEM
                    + " jmi where"
                    + " mv.measurement_item_id = jmi.measurement_item_id "
                    + " and mv.measurement_type = jmi.measurement_type "
                    + " and (mv.MEASUREMENT_TIME between ? and ?)"
                    + " and mv.MEASUREMENT_TYPE > 255"
                    + " and jmi.ITEM_NAME like ?"
                    + " order by mv.MEASUREMENT_TIME";
            pstmt = conn.prepareStatement(sql);
            // CHECKSTYLE:OFF
            pstmt.setTimestamp(1, start);
            pstmt.setTimestamp(2, end);
            pstmt.setString(3, itemName +"%");
            // CHECKSTYLE:ON
            rs = pstmt.executeQuery();
            result = getMeasurementValueDtosFromResultSet(rs);
        }
        finally
        {
            SQLUtil.closeResultSet(rs);
            SQLUtil.closeStatement(pstmt);
            SQLUtil.closeConnection(conn);
        }

        return result;
    }

    /** 期間と項目名を指定して計測値の系列を取得するSQL。 */
    private static final String SQL_SELECT_BY_TERM_AND_MEASUREMENT_ITEM_NAME =
        "SELECT jmi.measurement_item_name," +
        "       mv.measurement_item_id," +
        "       mv.measurement_time," +
        "       mv.measurement_value" +
        "  FROM measurement_value mv, javelin_measurement_item jmi" +
        "  WHERE mv.measurement_item_id = jmi.measurement_item_id" +
        "    AND (mv.measurement_time BETWEEN ? and ?)" +
        "    AND jmi.measurement_item_name LIKE ?" +
        "  ORDER BY mv.measurement_time, measurement_item_name";

    /**
     * 期間と項目名を指定して、特定のグラフのレコードを取得します。<br />
     * レコードは時刻で昇順に並べ替えて返します。
     *
     * @param database データベース名。
     * @param start 開始時刻。
     * @param end 終了時刻。
     * @param measurementItemName 計測項目名。
     * @return {@link MeasurementValueDto} のリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<MeasurementValueDto> selectByTermAndMeasurementItemName(String database,
            Date start, Date end, String measurementItemName)
        throws SQLException
    {
        List<MeasurementValueDto> result = null;
        
        // Date → Timestampへの変換
        Timestamp tsStart = new Timestamp(start.getTime());
        Timestamp tsEnd   = new Timestamp(end.getTime());

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);
            pstmt = conn.prepareStatement(SQL_SELECT_BY_TERM_AND_MEASUREMENT_ITEM_NAME);
            // CHECKSTYLE:OFF
            pstmt.setTimestamp(1, tsStart);
            pstmt.setTimestamp(2, tsEnd);
            pstmt.setString(3, measurementItemName);
            // CHECKSTYLE:ON
            rs = pstmt.executeQuery();
            result = getMeasurementValueDtosFromResultSet(rs);
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
     * 時刻の範囲を指定して、特定のグラフのレコードを取得します。<br />
     *
     * レコードは時刻で昇順に並べ替えて返します。
     *
     * @param database データベース名
     * @param startTime 開始時刻
     * @param endTime 終了時刻
     * @param measurementType 計測値種別
     * @return {@link MeasurementValue} のリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<MeasurementValueDto> selectByTimeAndMeasurementType(final String database,
            final Timestamp startTime, final Timestamp endTime, final long measurementType)
        throws SQLException
    {
        List<MeasurementValueDto> result = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);
            String sql =
                "select " +
                " mv.measurement_value_id" +
                ", mv.measurement_num" +
                ", mv.measurement_time" +
                ", mv.measurement_type" +
                ", mv.measurement_item_id" +
                ", mv.value resultvalue" +
                ", jmi.item_name measurement_item_name" +
                ", mi.item_name measurement_type_name" +
                ", mi.display_name measurement_display_name" +
                " from " + MEASUREMENT_VALUE + " mv, " + JAVELIN_MEASUREMENT_ITEM
                    + " jmi, " + MEASUREMENT_INFO + " mi where"
                    + " mv.measurement_type = mi.measurement_type "
                    + " and mv.measurement_item_id = jmi.measurement_item_id "
                    + " and mv.measurement_type = jmi.measurement_type "
                    + " and (mv.MEASUREMENT_TIME between ? and ?)" + " and mv.MEASUREMENT_TYPE = ?"
                    + " order by mv.MEASUREMENT_TIME";
            pstmt = conn.prepareStatement(sql);
            // CHECKSTYLE:OFF
            pstmt.setTimestamp(1, startTime);
            pstmt.setTimestamp(2, endTime);
            pstmt.setLong(3, measurementType);
            // CHECKSTYLE:ON
            rs = pstmt.executeQuery();
            result = getMeasurementValueDtosFromResultSet(rs);
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
     * 期間、および計測値種別名を指定して、計測値を取得します。<br/>
     * 取得した結果には、計測値種別名、計測値系列名を付加します。
     *
     * @deprecated measurement_infoテーブルは廃止されました。
     * 
     * @param database データベース名
     * @param start    検索条件（開始時刻）
     * @param end      検索条件（終了時刻）
     * @param typeName 検索条件（計測値種別名）
     * @return 検索条件に合致したデータのリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<MeasurementValueDto> selectByTermAndMeasurementTypeWithName(String database,
            Timestamp start, Timestamp end, String typeName)
        throws SQLException
    {
        return MeasurementValueDao.selectByTermAndMeasurementTypeWithNameBase(
                                                                              database,
                                                                              start,
                                                                              end,
                                                                              typeName,
                                                                              MeasurementValueDao.WITH_NAME_ACCESS_SQL);
    }
    
    /**
     * 計測値種別名を指定して系列名一覧を取得します。
     *
     * @deprecated measurement_infoテーブルは廃止されました。
     * 
     * @param database データベース名
     * @param typeName 検索条件（計測値種別名）
     * @return 検索条件に合致したデータのリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<MeasurementValueDto> selectByMeasurementTypeWithName(String database,
            String typeName)
        throws SQLException
    {
        return MeasurementValueDao.selectByMeasurementTypeWithNameBase(
                                                                              database,
                                                                              typeName,
                                                                              MeasurementValueDao.WITH_NAME_ITEM_NAME);
    }

    /**
     * 期間、および計測値種別名を指定して、時系列でソートした計測値を取得します。<br/>
     * 取得した結果には、計測値種別名、計測値系列名を付加します。
     *
     * @deprecated measurement_infoテーブルは廃止されました。
     * 
     * @param database データベース名
     * @param start    検索条件（開始時刻）
     * @param end      検索条件（終了時刻）
     * @param typeName 検索条件（計測値種別名）
     * @return 検索条件に合致したデータのリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<MeasurementValueDto> selectByTermAndMeasurementTypeWithNameOrderByTime(
            String database, Timestamp start, Timestamp end, String typeName)
        throws SQLException
    {
        return MeasurementValueDao.selectByTermAndMeasurementTypeWithNameBase(
                                                                              database,
                                                                              start,
                                                                              end,
                                                                              typeName,
                                                                              MeasurementValueDao.WITH_NAME_ACCESS_SQL_TIME_ORDER);
    }

    /**
     * 期間、および計測値種別名を指定して、時系列でソートした計測値を取得します。<br/>
     * 取得した結果には、計測値種別名、計測値系列名を付加します。
     *
     * @deprecated measurement_infoテーブルは廃止されました。
     * 
     * @param database データベース名
     * @param start    検索条件（開始時刻）
     * @param end      検索条件（終了時刻）
     * @param typeName 検索条件（計測値種別名）
     * @param itemName 検索条件（系列名）
     * @return 検索条件に合致したデータのリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<MeasurementValueDto> selectByTermAndMeasurementTypeWithItemNameOrderByTime(
            String database, Timestamp start, Timestamp end, String typeName, String itemName)
        throws SQLException
    {
        return MeasurementValueDao.selectByTermAndMeasurementTypeWithItemNameBase(
                                                                              database,
                                                                              start,
                                                                              end,
                                                                              typeName,
                                                                              itemName,
                                                                              MeasurementValueDao.WITH_ITEM_NAME_ACCESS_SQL_TIME_ORDER );
    }

    /**
     * 期間、および計測値種別名を指定して、計測値の系列毎の最大値を取得します。
     * 取得した結果には、計測値種別名、計測値系列名を付加します。
     *
     * @deprecated measurement_infoテーブルは廃止されました。
     * 
     * @param database データベース名
     * @param start    検索条件（開始時刻）
     * @param end      検索条件（終了時刻）
     * @param typeName 検索条件（計測値種別名）
     * @return 検索条件に合致したデータのリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<MeasurementValueDto> selectMaxValueByTermAndMeasurementTypeWithName(
            String database, Timestamp start, Timestamp end, String typeName)
        throws SQLException
    {
        return MeasurementValueDao.selectByTermAndMeasurementTypeWithNameBase(
                                                                              database,
                                                                              start,
                                                                              end,
                                                                              typeName,
                                                                              MeasurementValueDao.WITH_NAME_MAX_VALUE_ACCESS_SQL);
    }

    /**
     * 期間、および計測値種別名を指定して、計測値の系列毎の最小値を取得します。
     * 取得した結果には、計測値種別名、計測値系列名を付加します。
     *
     * @deprecated measurement_infoテーブルは廃止されました。
     * 
     * @param database データベース名
     * @param start    検索条件（開始時刻）
     * @param end      検索条件（終了時刻）
     * @param typeName 検索条件（計測値種別名）
     * @return 検索条件に合致したデータのリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<MeasurementValueDto> selectMinValueByTermAndMeasurementTypeWithName(
            String database, Timestamp start, Timestamp end, String typeName)
        throws SQLException
    {
        return MeasurementValueDao.selectByTermAndMeasurementTypeWithNameBase(
                                                                              database,
                                                                              start,
                                                                              end,
                                                                              typeName,
                                                                              MeasurementValueDao.WITH_NAME_MIN_VALUE_ACCESS_SQL);
    }

    /**
     * 期間、および計測値種別名を指定して、計測値の系列毎の合計値を取得します。
     * 取得した結果には、計測値種別名、計測値系列名を付加します。
     * 合計値算出の基準は「系列名」になります。
     *
     * @deprecated measurement_infoテーブルは廃止されました。
     * 
     * @param database データベース名
     * @param start    検索条件（開始時刻）
     * @param end      検索条件（終了時刻）
     * @param typeName 検索条件（計測値種別名）
     * @return 検索条件に合致したデータのリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<MeasurementValueDto> selectSumValueByTermAndMeasurementTypeWithName(
            String database, Timestamp start, Timestamp end, String typeName)
        throws SQLException
    {
        return MeasurementValueDao.selectByTermAndMeasurementTypeWithNameBase(
                                                                              database,
                                                                              start,
                                                                              end,
                                                                              typeName,
                                                                              MeasurementValueDao.WITH_NAME_SUM_VALUE_ACCESS_SQL);
    }

    /**
     * 期間、および計測値種別名を指定して、計測値の系列毎の合計値を取得します。
     * 取得した結果には、計測値種別名、計測値系列名を付加します。
     * 合計値算出の基準は「計測時刻」になります。
     *
     * @deprecated measurement_infoテーブルは廃止されました。
     * 
     * @param database データベース名
     * @param start    検索条件（開始時刻）
     * @param end      検索条件（終了時刻）
     * @param typeName 検索条件（計測値種別名）
     * @return 検索条件に合致したデータのリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<MeasurementValueDto> selectSumValueByTermAndMeasurementTypeGroupingTime(
            String database, Timestamp start, Timestamp end, String typeName)
        throws SQLException
    {
        return MeasurementValueDao.selectByTermAndMeasurementTypeWithNameBase(
                                                                              database,
                                                                              start,
                                                                              end,
                                                                              typeName,
                                                                              MeasurementValueDao.WITH_NAME_SUM_VALUE_ACCESS_SQL_TIME_ORDER);

    }

    /**
     * 期間、および計測値種別名、およびSQLクエリを指定して、計測値を取得します。
     * 取得した結果には、計測値種別名、計測値系列名を付加します。
     *
     * @param database データベース名
     * @param start    検索条件（開始時刻）
     * @param end      検索条件（終了時刻）
     * @param typeName 検索条件（計測値種別名）
     * @param sqlBase  発行するSQLクエリ
     * @return 検索条件に合致したデータのリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    private static List<MeasurementValueDto> selectByTermAndMeasurementTypeWithNameBase(
            String database, Timestamp start, Timestamp end, String typeName, String sqlBase)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List<MeasurementValueDto> result = null;
        try
        {
            conn = getConnection(database, true);
            pstmt = conn.prepareStatement(sqlBase);
            // CHECKSTYLE:OFF
            pstmt.setString(1, typeName);
            pstmt.setTimestamp(2, start);
            pstmt.setTimestamp(3, end);
            // CHECKSTYLE:ON
            rs = pstmt.executeQuery();
            result = getMeasurementValueDtosFromResultSet(rs);
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
     * 期間、計測値種別名、系列名およびSQLクエリを指定して、計測値を取得します。
     * 取得した結果には、計測値種別名、計測値系列名を付加します。
     *
     * @param database データベース名
     * @param start    検索条件（開始時刻）
     * @param end      検索条件（終了時刻）
     * @param typeName 検索条件（計測値種別名）
     * @param typeName 検索条件（系列名）
     * @param sqlBase  発行するSQLクエリ
     * @return 検索条件に合致したデータのリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    private static List<MeasurementValueDto> selectByTermAndMeasurementTypeWithItemNameBase(
            String database, Timestamp start, Timestamp end,
            String typeName, String itemName, String sqlBase)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List<MeasurementValueDto> result = null;
        try
        {
            conn = getConnection(database, true);
            pstmt = conn.prepareStatement(sqlBase);
            // CHECKSTYLE:OFF
            pstmt.setString(1, typeName);
            pstmt.setString(2, itemName);
            pstmt.setTimestamp(3, start);
            pstmt.setTimestamp(4, end);
            // CHECKSTYLE:ON
            rs = pstmt.executeQuery();
            result = getMeasurementValueDtosFromResultSet(rs);
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
     * 計測値種別名、およびSQLクエリを指定して、系列名を取得します。
     *
     * @param database データベース名
     * @param typeName 検索条件（計測値種別名）
     * @param sqlBase  発行するSQLクエリ
     * @return 検索条件に合致したデータのリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    private static List<MeasurementValueDto> selectByMeasurementTypeWithNameBase(
            String database, String typeName, String sqlBase)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List<MeasurementValueDto> result = null;
        try
        {
            conn = getConnection(database, true);
            pstmt = conn.prepareStatement(sqlBase);
            // CHECKSTYLE:OFF
            pstmt.setString(1, typeName);
            // CHECKSTYLE:ON
            rs = pstmt.executeQuery();
            result = getMeasurementValueDtosFromResultSet(rs);
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
     * {@link ResultSet} から {@link MeasurementValue} のリストを作成します。<br />
     *
     * @param rs {@link ResultSet}
     * @throws SQLException SQL 実行結果取得時に例外が発生した場合
     * @return 生成結果
     */
    private static List<MeasurementValue> getMeasurementValuesFromResultSet(final ResultSet rs)
        throws SQLException
    {
        List<MeasurementValue> result = new ArrayList<MeasurementValue>();

        while (rs.next() == true)
        {
            MeasurementValue measurementValue = new MeasurementValue();
            // CHECKSTYLE:OFF
            measurementValue.measurementTime = rs.getTimestamp(1);
            measurementValue.measurementItemId = rs.getInt(2);
            measurementValue.value = rs.getString(3);
            // CHECKSTYLE:ON
            result.add(measurementValue);
        }
        return result;
    }

    /**
     * {@link ResultSet}インスタンスから、{@link MeasurementValueDto}のリストを生成します。
     *
     * @param rs データが含まれている{@link ResultSet} インスタンス
     * @throws SQLException SQL 実行結果取得時に例外が発生した場合
     */
    private static List<MeasurementValueDto> getMeasurementValueDtosFromResultSet(final ResultSet rs)
        throws SQLException
    {
        List<MeasurementValueDto> result = new ArrayList<MeasurementValueDto>();

        while (rs.next() == true)
        {
            MeasurementValueDto measurementValueDto = new MeasurementValueDto();

            measurementValueDto.measurementItemId = rs.getInt("measurement_item_id");
            measurementValueDto.measurementTime = rs.getTimestamp("measurement_time");
            measurementValueDto.value = rs.getString("measurement_value");
            measurementValueDto.measurementItemName = rs.getString("measurement_item_name");
            //measurementValueDto.measurementValueId = rs.getLong("measurement_value_id");
            //measurementValueDto.measurementNum = rs.getLong("measurement_num");
            //measurementValueDto.measurementType = rs.getInt("measurement_type");
            //measurementValueDto.value = rs.getBigDecimal("resultvalue");
            //measurementValueDto.measurementTypeItemName = rs.getString("measurement_type_name");
            //measurementValueDto.measurementTypeDisplayName =
            //        rs.getString("measurement_display_name");

            result.add(measurementValueDto);
        }
        return result;
    }

    /**
     * Javelin 計測値テーブルに登録されているデータの最小時刻と最大時刻を返します。<br />
     *
     * @param database データベース名
     * @return Javelin 計測値テーブルに登録されているデータの (最小時刻, 最大時刻) を表す配列
     *         （失敗した場合は空の配列）
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static Timestamp[] getTerm(final String database)
        throws SQLException
    {
        Timestamp[] result = new Timestamp[0];
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(getTermSql());
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
    private static String getTermSql()
    {
        if(DBManager.isDefaultDb())
        {
            return GET_TERM_SQL;
        }
        return GET_TERM_SQL_PARTIOTION;
    }

    /**
     * 時刻を指定して、それより古いレコードを削除します。
     * 削除期限時刻のキーとしては、計測時刻を基準とします。
     *
     * @param database データベース名
     * @param deleteLimit 削除期限時刻
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static void deleteOldRecordByTime(final String database,
            final Timestamp deleteLimit)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try
        {
            conn = getConnection(database, true);
            String sql = "delete from " + MEASUREMENT_VALUE + " where MEASUREMENT_TIME <= ?";
            pstmt = conn.prepareStatement(sql);
            PreparedStatement delegated = getDelegatingStatement(pstmt);
            delegated.setTimestamp(1, deleteLimit);
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
        deleteAll(database, MEASUREMENT_VALUE);
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
        int count = count(database, MEASUREMENT_VALUE, "MEASUREMENT_VALUE_ID");
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

    /**
     * 蓄積期間を取得する SQL を生成します。
     *
     * @return SQL
     */
    private static String createGetTermSql()
    {
        String unionSql = "";
        StringBuilder sql = new StringBuilder("select min(MIN_TIME), max(MAX_TIME) from (");
        for (int index = 1; index <= ResourceDataDaoUtil.PARTITION_TABLE_COUNT; index++)
        {
            sql.append(unionSql);
            String tableName = ResourceDataDaoUtil.getTableName(MEASUREMENT_VALUE, index);
            sql.append("select min(MEASUREMENT_TIME) MIN_TIME, max(MEASUREMENT_TIME) MAX_TIME from ");
            sql.append(tableName);
            unionSql = " union all ";
        }
        sql.append(") MERGED_TIME");
        return sql.toString();
    }
}
