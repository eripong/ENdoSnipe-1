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
package jp.co.acroquest.endosnipe.communicator.entity;

/**
 * 接続通知情報のデータクラスです。
 * 
 * @author matsuoka
 */
public class ConnectNotifyData
{
    /** 接続種別：Javelin */
    public static final int KIND_JAVELIN = 0;

    /** 接続種別：制御クライアント (BottleneckEye/WebDashboard) */
    public static final int KIND_CONTROLLER = 1;

    /** 接続目的：リソースの取得 */
    public static final int PURPOSE_GET_RESOURCE = 0;

    /** 接続目的：データベース名の取得 */
    public static final int PURPOSE_GET_DATABASE = 1;

    /** 接続種別 */
    private int kind_ = -1;

    /** DB名 */
    private String dbName_;

    /** 接続目的 */
    private int purpose_ = 0;

    /**
     * 接続種別を取得します。
     * @return 接続種別
     */
    public int getKind()
    {
        return kind_;
    }

    /**
     * 接続種別を設定します。
     * @param kind 接続種別
     */
    public void setKind(int kind)
    {
        kind_ = kind;
    }

    /**
     * DB名を取得します。
     * @return DB名
     */
    public String getDbName()
    {
        return dbName_;
    }

    /**
     * DB名を設定します。
     * @param dbName DB名
     */
    public void setDbName(String dbName)
    {
        dbName_ = dbName;
    }

    /**
     * 接続目的を取得します。
     * @return 接続目的
     */
    public int getPurpose()
    {
        return purpose_;
    }

    /**
     * 接続目的を設定します。
     * @param purpose 接続目的
     */
    public void setPurpose(int purpose)
    {
        purpose_ = purpose;
    }

}
