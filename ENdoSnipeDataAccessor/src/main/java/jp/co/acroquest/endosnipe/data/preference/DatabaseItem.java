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
package jp.co.acroquest.endosnipe.data.preference;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import jp.co.acroquest.endosnipe.data.dao.JavelinLogDao;
import jp.co.acroquest.endosnipe.data.dao.MeasurementValueDao;
import jp.co.acroquest.endosnipe.data.entity.HostInfo;
import jp.co.acroquest.endosnipe.data.service.HostInfoManager;

/**
 * プリファレンスページに表示するデータベース一覧の 1 項目を表すクラス。<br />
 *
 * @author sakamoto
 */
public class DatabaseItem
{
    private final String databaseName_;

    private HostInfo hostInfo_;

    private long startTime_;

    private long endTime_;

    /**
     * プリファレンスページに表示するデータベース一覧の 1 項目を生成します。<br />
     *
     * @param databaseName データベース名
     */
    private DatabaseItem(final String databaseName)
        throws SQLException
    {
        this.databaseName_ = databaseName;
    }

    /**
     * プリファレンスページに表示するデータベース一覧の 1 項目を生成します。<br />
     *
     * @param databaseName データベース名 
     * @return {@link DatabaseItem} オブジェクト
     * @throws SQLException データベースから値を取得できなかった場合
     */
    public static DatabaseItem createDatabaseItem(final String databaseName)
        throws SQLException
    {
        DatabaseItem databaseItem = new DatabaseItem(databaseName);
        if (databaseItem.init())
        {
            return databaseItem;
        }
        return null;
    }

    /**
     * フィールドの初期化を行います。<br />
     *
     * @return 初期化に成功した場合は <code>true</code> 、失敗した場合は <code>false</code>
     * @throws SQLException データベースから値を取得できなかった場合
     */
    private boolean init()
        throws SQLException
    {
        this.hostInfo_ = HostInfoManager.getHostInfo(this.databaseName_, true);
        if (this.hostInfo_ == null)
        {
            return false;
        }

        Timestamp[] javelinLogTerm = JavelinLogDao.getLogTerm(this.databaseName_);
        Timestamp[] measurementTerm = MeasurementValueDao.getTerm(this.databaseName_);
        if (javelinLogTerm.length == 2 && measurementTerm.length == 2)
        {
            // Javelinログとグラフ出力用データが共に存在する場合
            if (javelinLogTerm[0] != null && measurementTerm[0] != null)
            {
                this.startTime_ =
                        Math.min(javelinLogTerm[0].getTime(), measurementTerm[0].getTime());
            }
            if (javelinLogTerm[1] != null && measurementTerm[1] != null)
            {
                this.endTime_ = Math.min(javelinLogTerm[1].getTime(), measurementTerm[1].getTime());
            }
        }
        else if (javelinLogTerm.length == 2)
        {
            // Javelinログデータのみが格納されている場合
            this.startTime_ = javelinLogTerm[0].getTime();
            this.endTime_ = javelinLogTerm[1].getTime();
        }
        else if (measurementTerm.length == 2)
        {
            // グラフ出力用でデータのみが格納されている場合
            this.startTime_ = measurementTerm[0].getTime();
            this.endTime_ = measurementTerm[1].getTime();
        }
        else
        {
            // 共にデータが格納されていない場合
            this.startTime_ = 0;
            this.endTime_ = 0;
        }
        return true;
    }

    /**
     * データベース名を返します。<br />
     *
     * @return データベース名
     */
    public String getDatabaseName()
    {
        return this.databaseName_;
    }

    /**
     * ホスト名を返します。<br />
     *
     * @return ホスト名（データベースが不正な場合は <code>null</code> ）
     */
    public String getHostName()
    {
        if (this.hostInfo_ == null)
        {
            return null;
        }
        return this.hostInfo_.hostName;
    }

    /**
     * IP アドレスを返します。<br />
     *
     * @return IP アドレス（データベースが不正な場合は <code>null</code> ）
     */
    public String getIpAddress()
    {
        if (this.hostInfo_ == null)
        {
            return null;
        }
        return this.hostInfo_.ipAddress;
    }

    /**
     * ポート番号を返します。<br />
     *
     * @return ポート番号（データベースが不正な場合は <code>-1</code> ）
     */
    public int getPort()
    {
        if (this.hostInfo_ == null)
        {
            return -1;
        }
        return this.hostInfo_.port;
    }

    /**
     * ホストの説明を返します。<br />
     *
     * @return ホストの説明
     */
    public String getDescription()
    {
        if (this.hostInfo_ == null)
        {
            return "";
        }
        return this.hostInfo_.description;
    }

    /**
     * データベースに蓄積されている情報の期間の最小日時を返します。<br />
     *
     * @return データベースに蓄積されている情報の期間の最小日時（ミリ秒）
     */
    public long getStartTime()
    {
        return this.startTime_;
    }

    /**
     * データベースに蓄積されている情報の期間の最大日時を返します。<br />
     *
     * @return データベースに蓄積されている情報の期間の最大日時（ミリ秒）
     */
    public long getEndTime()
    {
        return this.endTime_;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("{databaseName=");
        builder.append(this.databaseName_);
        builder.append(",hostInfo=");
        builder.append(this.hostInfo_);
        builder.append(",startTime=");
        builder.append((this.startTime_ != 0) ? new Date(this.startTime_) : "none");
        builder.append(",endTime=");
        builder.append((this.endTime_ != 0) ? new Date(this.endTime_) : "none");
        builder.append("}");
        return builder.toString();
    }
}
