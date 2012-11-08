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
package jp.co.acroquest.endosnipe.data.service;

import java.sql.SQLException;
import java.util.List;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.data.ENdoSnipeDataAccessorPluginProvider;
import jp.co.acroquest.endosnipe.data.LogMessageCodes;
import jp.co.acroquest.endosnipe.data.TableNames;
import jp.co.acroquest.endosnipe.data.dao.HostInfoDao;
import jp.co.acroquest.endosnipe.data.entity.HostInfo;

import org.seasar.framework.util.StringUtil;

/**
 * データベースのホスト情報に関する管理を行うためのクラスです。<br />
 * ホスト情報に関するアクセスは、DAO を直接使用せずに本クラスを使用します。<br />
 * 
 * @author y-komori
 */
public class HostInfoManager implements LogMessageCodes, TableNames
{
    private static final ENdoSnipeLogger LOGGER =
            ENdoSnipeLogger.getLogger(HostInfoManager.class,
                                      ENdoSnipeDataAccessorPluginProvider.INSTANCE);

    private HostInfoManager()
    {
        // Do nothing.
    }

    /**
     * 指定されたデータベースのホスト情報を取得します。<br />
     * 
     * @param database データベース
     * @param log ホスト情報が取得できない場合にエラー内容をログに出力する場合は <code>true</code>
     * @return ホスト情報。見つからない場合は <code>null</code>。
     */
    public static HostInfo getHostInfo(final String database, final boolean log)
    {
        HostInfo hostInfo = null;
        try
        {
            List<HostInfo> hostInfoList = HostInfoDao.selectAll(database);
            if (hostInfoList.size() == 1)
            {
                hostInfo = hostInfoList.get(0);
            }
        }
        catch (SQLException ex)
        {
            if (log)
            {
                LOGGER.log(DB_ACCESS_ERROR, ex, ex.getMessage());
            }
        }
        return hostInfo;
    }

    /**
     * ホスト情報を DB に登録します。<br />
     *
     * @param database データベース名
     * @param hostName ホスト名（ <code>null</code> も可）
     * @param ipAddress IPアドレス（ <code>null</code> は不可）
     * @param port ポート番号
     * @param description データベースの説明
     */
    public static synchronized void registerHostInfo(final String database, final String hostName,
            final String ipAddress, final int port, final String description)
    {
        try
        {
            // 指定されたホスト情報とDBに登録されているホスト情報を比較し、
            // 変更があれば更新する
            HostInfo oldHostInfo = getHostInfo(database, false);
            if (oldHostInfo != null)
            {
                if (StringUtil.equals(hostName, oldHostInfo.hostName)
                        && StringUtil.equals(ipAddress, oldHostInfo.ipAddress)
                        && port == oldHostInfo.port
                        && StringUtil.equals(description, oldHostInfo.description))
                {
                    // ホスト情報が一致したので何もしない
                    return;
                }
            }
            // 変更があったら、一旦削除してから登録しなおす
            HostInfoDao.deleteAll(database);

            // ホスト情報が更新されているため、再登録を行う
            HostInfo newHostInfo = new HostInfo();
            newHostInfo.hostName = hostName;
            newHostInfo.ipAddress = ipAddress;
            newHostInfo.port = port;
            newHostInfo.description = description;
            HostInfoDao.insert(database, newHostInfo);
            LOGGER.log(HOST_REGISTERED, hostName, ipAddress, port);
        }
        catch (SQLException ex)
        {
            LOGGER.log(DB_ACCESS_ERROR, ex, ex.getMessage());
        }
    }
}
