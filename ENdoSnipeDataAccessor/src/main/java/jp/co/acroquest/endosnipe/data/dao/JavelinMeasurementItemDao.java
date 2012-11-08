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
import java.util.Map;

import jp.co.acroquest.endosnipe.common.util.SQLUtil;
import jp.co.acroquest.endosnipe.data.TableNames;
import jp.co.acroquest.endosnipe.data.entity.JavelinMeasurementItem;

/**
 * {@link JavelinMeasurementItem} のための DAO です。
 *
 * @author y-sakamoto
 */
public class JavelinMeasurementItemDao extends AbstractDao implements TableNames
{

    /**
     * 項目（系列）名称のレコードを追加します。<br />
     *
     * @param database データベース名
     * @param item 挿入する値
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static void insert(final String database, final JavelinMeasurementItem item)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database);
            pstmt =
                conn.prepareStatement("insert into " + JAVELIN_MEASUREMENT_ITEM +
                                      " (MEASUREMENT_ITEM_NAME, LAST_INSERTED)" +
                                      " values (?,?)");
            PreparedStatement delegated = getDelegatingStatement(pstmt);
            // CHECKSTYLE:OFF
            String measurementItemName = item.itemName;
            delegated.setString(1, measurementItemName);
            delegated.setTimestamp(2, item.lastInserted);
            // CHECKSTYLE:ON
            delegated.execute();
        }
        finally
        {
            SQLUtil.closeResultSet(rs);
            SQLUtil.closeStatement(pstmt);
            SQLUtil.closeConnection(conn);
        }
    }

    /**
     * 指定された計測項目 ID の計測項目情報を取得します。<br />
     *
     * @param database データベース名
     * @param measurementItemId 計測項目 ID
     * @return レコードが存在する場合は計測項目情報オブジェクト、存在しない場合は <code>null</code>
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static JavelinMeasurementItem selectById(final String database,
            final int measurementItemId)
        throws SQLException
    {
        Connection conn = null;
        JavelinMeasurementItem result = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);
            String sql =
                    "select * from " + JAVELIN_MEASUREMENT_ITEM + " where MEASUREMENT_ITEM_ID = ?";
            pstmt = conn.prepareStatement(sql);
            PreparedStatement delegated = getDelegatingStatement(pstmt);
            delegated.setInt(1, measurementItemId);
            rs = delegated.executeQuery();

            if (rs.next() == true)
            {
                result = new JavelinMeasurementItem();
                // CHECKSTYLE:OFF
                result.measurementItemId = rs.getInt(1);
                result.itemName = rs.getString(2);
                result.lastInserted = rs.getTimestamp(3);
                // CHECKSTYLE:ON
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
     * 指定された計測値種別と項目名称のレコードの　ID を返します。<br />
     *
     * @param database データベース名
     * @param itemName 項目（系列）名称
     * @return レコードが存在する場合はレコードの ID 、存在しない場合は <code>-1</code>
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static int selectMeasurementItemIdFromItemName(final String database,
                                                          final String itemName)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int itemId = -1;
        try
        {
            conn = getConnection(database, true);
            pstmt = conn.prepareStatement("select MEASUREMENT_ITEM_ID from "
                                          + JAVELIN_MEASUREMENT_ITEM
                                          + " where MEASUREMENT_ITEM_NAME = ?");
            PreparedStatement delegated = getDelegatingStatement(pstmt);
            String measurementItemID = itemName; 
            delegated.setString(1, measurementItemID);
            rs = delegated.executeQuery();
            if (rs.next() == true)
            {
                itemId = rs.getInt(1);
            }
        }
        finally
        {
            SQLUtil.closeResultSet(rs);
            SQLUtil.closeStatement(pstmt);
            SQLUtil.closeConnection(conn);
        }
        return itemId;
    }

    /**
     * レコードをすべて取得します。<br />
     * 
     * @param database データベース名
     * @return {@link JavelinMeasurementItem} のリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<JavelinMeasurementItem> selectAll(final String database)
        throws SQLException
    {
        List<JavelinMeasurementItem> result = new ArrayList<JavelinMeasurementItem>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);
            stmt = conn.createStatement();
            String sql = "select * from " + JAVELIN_MEASUREMENT_ITEM +
                         " order by MEASUREMENT_ITEM_NAME";
            rs = stmt.executeQuery(sql);
            while (rs.next() == true)
            {
                JavelinMeasurementItem item = new JavelinMeasurementItem();
                // CHECKSTYLE:OFF
                item.measurementItemId = rs.getInt(1);
                item.itemName = rs.getString(2);
                item.lastInserted = rs.getTimestamp(3);
                // CHECKSTYLE:ON
                result.add(item);
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
     * 指定した項目名のレコードを全て取得します。<br />
     * 
     * @param database データベース名
     * @param measurementType 項目名
     * @return {@link JavelinMeasurementItem} のリスト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static List<JavelinMeasurementItem> selectByMeasurementType(final String database,
                                                                       long measurementType)
        throws SQLException
    {
        List<JavelinMeasurementItem> result = new ArrayList<JavelinMeasurementItem>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);
            String sql = "select * from " + JAVELIN_MEASUREMENT_ITEM
                         + " where MEASUREMENT_ITEM_ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, measurementType);
            
            rs = pstmt.executeQuery();
            while (rs.next() == true)
            {
                JavelinMeasurementItem item = new JavelinMeasurementItem();
                // CHECKSTYLE:OFF
                item.measurementItemId = rs.getInt(1);
                item.itemName = rs.getString(2);
                item.lastInserted = rs.getTimestamp(3);
                // CHECKSTYLE:ON
                result.add(item);
            }
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
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
     * 指定された MEASUREMENT_ITEM_ID のレコードを削除します。
     *
     * @param database データベース名
     * @param measurementItemName 削除するレコードの MEASUREMENT_ITEM_NAME
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static void deleteByMeasurementItemId(final String database,
                                                 final String measurementItemName)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = getConnection(database, true);
            String sql =
                "delete from " + JAVELIN_MEASUREMENT_ITEM + " where MEASUREMENT_ITEM_NAME = ?";
            pstmt = conn.prepareStatement(sql);
            PreparedStatement delegated = getDelegatingStatement(pstmt);
            delegated.setString(1, measurementItemName);
            pstmt.execute();
        }
        finally
        {
            SQLUtil.closeStatement(pstmt);
            SQLUtil.closeConnection(conn);
        }
    }

    /**
     * 計測データが挿入された時刻を反映します。
     *
     * @param database データベース名
     * @param map 系列ITEMと最終挿入時刻のマップ
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static void updateLastInserted(final String database, final Map<String, Timestamp> map)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = getConnection(database, true);
            String sql =
                "update " + JAVELIN_MEASUREMENT_ITEM + " set LAST_INSERTED = ?" +
                		" where MEASUREMENT_ITEM_NAME = ?";
            pstmt = conn.prepareStatement(sql);
            PreparedStatement delegated = getDelegatingStatement(pstmt);
            for (Map.Entry<String, Timestamp> entry : map.entrySet())
            {
                delegated.setTimestamp(1, entry.getValue());
                delegated.setString(2, entry.getKey());
                delegated.addBatch();
            }
            pstmt.executeBatch();
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
        deleteAll(database, JAVELIN_MEASUREMENT_ITEM);
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
        int count = count(database, JAVELIN_MEASUREMENT_ITEM, "MEASUREMENT_ITEM_NAME");
        return count;
    }
    
    /**
     * measurement_valueテーブルで使用されていないレコードを、<br />
     * このテーブルから削除します。<br />
     * 
     * @param database データベース名
     * @throws SQLException SQL実行時に例外が発生した場合
     */
    public static void deleteNotUsedRecord(final String database)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try
        {
            conn = getConnection(database, true);
            String sql = "delete from " + JAVELIN_MEASUREMENT_ITEM + " items " +
                    "where not exists( select vals.measurement_item_id from " +
                    MEASUREMENT_VALUE + " vals " +
                    "where items.measurement_item_id = vals.measurement_item_id)";
            pstmt = conn.prepareStatement(sql);
            pstmt.execute();
        }
        finally
        {
            SQLUtil.closeStatement(pstmt);
            SQLUtil.closeConnection(conn);
        }
    }
}
