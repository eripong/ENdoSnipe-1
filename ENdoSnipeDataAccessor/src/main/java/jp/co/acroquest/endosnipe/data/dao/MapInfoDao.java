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
import jp.co.acroquest.endosnipe.data.entity.MapInfo;

/**
 * {@link MapInfo} のための DAO です。
 *
 * @author fujii
 */
public class MapInfoDao extends AbstractDao
{
    private static final String MAP_INFO_TABLE = "MAP_INFO";

    /**
     * レコードを挿入します。<br />
     *
     * {@link ArchivedValue#measurementValueId} は使用されません。
     *
     * @param database データベース名
     * @param mapInfo 挿入する値
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static void insert(final String database, MapInfo mapInfo)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = getConnection(database);
            String sql =
                "insert into " + MAP_INFO_TABLE + "(NAME, " + "DATA" + ")" + " values (?, ?)";
            pstmt = conn.prepareStatement(sql);
            // CHECKSTYLE:OFF
            pstmt.setString(1, mapInfo.name);
            pstmt.setString(2, mapInfo.data);
            Timestamp current = new Timestamp(System.currentTimeMillis());
            pstmt.setTimestamp(3, current);
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
    public static List<MapInfo> selectAll(final String database)
        throws SQLException
    {
        List<MapInfo> result = new ArrayList<MapInfo>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);
            stmt = conn.createStatement();
            String sql = "select * from " + MAP_INFO_TABLE + " order by NAME";
            rs = stmt.executeQuery(sql);
            getMapInfoFromResultSet(result, rs);
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
     * @param mapId マップID
     * @return {@link MapInfo} オブジェクト
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static MapInfo selectById(final String database, final long mapId)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        MapInfo mapInfo = new MapInfo();
        try
        {
            conn = getConnection(database, true);
            String sql = "select * from " + MAP_INFO_TABLE + " where MAP_ID = ? order by NAME";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, mapId);
            rs = pstmt.executeQuery();
            // CHECKSTYLE:OFF
            mapInfo.mapId = rs.getLong(1);
            mapInfo.name = rs.getString(2);
            mapInfo.data = rs.getString(3);
            mapInfo.lastUpdate = rs.getTimestamp(4);
            // CHECKSTYLE:ON
        }
        finally
        {
            SQLUtil.closeResultSet(rs);
            SQLUtil.closeStatement(pstmt);
            SQLUtil.closeConnection(conn);
        }

        return mapInfo;
    }

    /**
     * マップを登録する。
     * @param database データベース名
     * @param mapInfo マップ情報
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public static void update(String database, final MapInfo mapInfo)
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(database, true);
            String sql =
                "update " + MAP_INFO_TABLE + "set data = ? , last_update = ? where MAP_ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, mapInfo.data);
            Timestamp current = new Timestamp(System.currentTimeMillis());
            pstmt.setTimestamp(2, current);
            pstmt.setLong(3, mapInfo.mapId);
            rs = pstmt.executeQuery();
        }
        finally
        {
            SQLUtil.closeResultSet(rs);
            SQLUtil.closeStatement(pstmt);
            SQLUtil.closeConnection(conn);
        }
    }

    /**
     * {@link ResultSet} から {@link MapInfo} のリストを作成します。<br />
     *
     * @param result {@link MapInfo} オブジェクトの格納先
     * @param rs {@link ResultSet}
     * @throws SQLException SQL 実行結果取得時に例外が発生した場合
     */
    private static void getMapInfoFromResultSet(List<MapInfo> result, ResultSet rs)
        throws SQLException
    {
        while (rs.next() == true)
        {
            MapInfo mapInfo = new MapInfo();
            // CHECKSTYLE:OFF
            mapInfo.mapId = rs.getInt(1);
            mapInfo.name = rs.getString(2);
            mapInfo.data = rs.getString(3);
            mapInfo.lastUpdate = rs.getTimestamp(4);
            // CHECKSTYLE:ON
            result.add(mapInfo);
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
        deleteAll(database, MAP_INFO_TABLE);
    }

}
