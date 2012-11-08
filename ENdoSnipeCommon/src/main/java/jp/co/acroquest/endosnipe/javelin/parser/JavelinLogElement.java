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
package jp.co.acroquest.endosnipe.javelin.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.parser.JavelinConstants;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogColumnNum;

/**
 * 動作ログファイルから切り出した一要素分のログ。 一要素分のログは、ログの出力時刻、メソッド名、クラス名、 オブジェクトIDなどを表す「基本情報」と
 * 変数の値やスタックトレースなどを表す「詳細情報」からなる。
 * 
 * @author kameda
 */
public class JavelinLogElement
{
    private String logFileName_;

    private int startLogLine_;

    private int endLogLine_;

    /** JavelinのIPアドレス。このデータはDataCollectorのみが使用する。 */
    private String ipAddress_;

    /** Javelinのポート番号。このデータはDataCollectorのみが使用する。 */
    private int port_;

    /** Javelinのポート番号。このデータはDataCollectorのみが使用する。 */
    private String databaseName_;

    /** アラーム閾値 */
    private long alarmThreshold_;

    /** アラームCPU閾値 */
    private long cpuAlarmThreshold_;

    private List<String> baseInfoList_;

    private Map<String, String> detailInfoMap_;

    private String[] args_;

    /**
     * JavalinLogElementの初期化
     */
    public JavelinLogElement()
    {
        this.detailInfoMap_ = new HashMap<String, String>();
    }

    /**
     * 格納している基本情報を返す。
     * 
     * @return 基本情報のリスト
     */
    public List<String> getBaseInfo()
    {
        return this.baseInfoList_;
    }

    /**
     * 格納している要素の識別子を返す
     * 
     * @return 識別子
     */
    public String getLogIDType()
    {
        if (this.baseInfoList_ == null)
        {
            return null;
        }

        return this.baseInfoList_.get(JavelinLogColumnNum.ID);
    }

    /**
     * 格納している詳細情報から、 引数で指定したタグの種類に対応するログ文字列を返す。
     * 
     * @param tagType
     *            詳細情報の種類
     * @return 詳細情報
     */
    public String getDetailInfo(final String tagType)
    {
        return this.detailInfoMap_.get(tagType);
    }

    /**
     * CSVで分割した基本情報をセットする。
     * 
     * @param baseInfoList
     *            基本情報
     */
    public void setBaseInfo(final List<String> baseInfoList)
    {
        this.baseInfoList_ = baseInfoList;
    }

    /**
     * 詳細情報のタグの種類と対応するログ文字列をセットする
     * 
     * @param tagType
     *            詳細情報のタグの種類
     * @param data
     *            詳細情報の内容
     */
    public void setDetailInfo(final String tagType, final String data)
    {
        this.detailInfoMap_.put(tagType, data);
    }

    /**
     * 詳細情報のマップをセットします。<br />
     * 
     * @param detailMap
     *            詳細情報のマップ
     */
    public void setDetailInfo(final Map<String, String> detailMap)
    {
        this.detailInfoMap_ = detailMap;
    }

    /**
     * 詳細情報のマップを返します。<br />
     * 
     * @return 詳細情報のマップ
     */
    public Map<String, String> getDetailMap()
    {
        return this.detailInfoMap_;
    }

    /**
     * このログの終了行を返します。<br />
     * 
     * @return 終了行
     */
    public int getEndLogLine()
    {
        return this.endLogLine_;
    }

    /**
     * このログの終了行をセットします。<br />
     * 
     * @param endLogLine
     *            終了行
     */
    public void setEndLogLine(final int endLogLine)
    {
        this.endLogLine_ = endLogLine;
    }

    /**
     * このログを出力したJavelinのIPアドレスを返します。<br />
     * 
     * @return IPアドレス
     */
    public String getIpAddress()
    {
        return ipAddress_;
    }

    /**
     * このログを出力したJavelinのIPアドレスをセットします。<br />
     * 
     * @param ipAddress IPアドレス
     */
    public void setIpAddress(final String ipAddress)
    {
        ipAddress_ = ipAddress;
    }

    /**
     * このログを出力したJavelinのポート番号を返します。<br />
     * 
     * @return port ポート番号
     */
    public int getPort()
    {
        return port_;
    }

    /**
     * このログを出力したJavelinのポート番号をセットします。<br />
     * 
     * @param port ポート番号
     */
    public void setPort(final int port)
    {
        port_ = port;
    }

    /**
     * このログが保存されているDBの名前を返します。<br />
     * 
     * @return databaseName DB名
     */
    public String getDatabaseName()
    {
        return databaseName_;
    }

    /**
     * このログが保存されているDBの名前をセットします。<br />
     * 
     * @param databaseName DB名
     */
    public void setDatabaseName(final String databaseName)
    {
        databaseName_ = databaseName;
    }

    /**
     * このログの開始行を返します。<br />
     * 
     * @return 開始行
     */
    public int getStartLogLine()
    {
        return this.startLogLine_;
    }

    /**
     * このログの開始行をセットします。<br />
     * 
     * @param startLogLine
     *            開始行
     */
    public void setStartLogLine(final int startLogLine)
    {
        this.startLogLine_ = startLogLine;
    }

    /**
     * ログファイル名を返します。<br />
     * 
     * @return ログファイル名
     */
    public String getLogFileName()
    {
        return this.logFileName_;
    }

    /**
     * ログファイル名をセットします。
     * 
     * @param logFileName
     *            ログファイル名
     */
    public void setLogFileName(final String logFileName)
    {
        this.logFileName_ = logFileName;
    }

    /**
     * スレッド名称を取得します。
     * 
     * @return スレッド名称
     */
    public String getThreadName()
    {
        String ret;

        // ログの種類を取得する。
        String id = this.baseInfoList_.get(JavelinLogColumnNum.ID);

        // ログの種類に応じて、基本情報部からスレッド名称を取得し、返す。
        if (JavelinConstants.MSG_CALL.equals(id))
        {
            ret = this.baseInfoList_.get(JavelinLogColumnNum.CALL_THREADID);
        }
        else if (JavelinConstants.MSG_RETURN.equals(id))
        {
            ret = this.baseInfoList_.get(JavelinLogColumnNum.RETURN_THREADID);
        }
        else if (JavelinConstants.MSG_CATCH.equals(id))
        {
            ret = this.baseInfoList_.get(JavelinLogColumnNum.CATCH_THREADID);
        }
        else if (JavelinConstants.MSG_THROW.equals(id))
        {
            ret = this.baseInfoList_.get(JavelinLogColumnNum.THROW_THREADID);
        }
        else
        {
            ret = this.baseInfoList_.get(JavelinLogColumnNum.READ_WRITE_THREADID);
        }

        return ret;
    }

    public String[] getArgs()
    {
        return args_;
    }

    public void setArgs(final String[] args)
    {
        args_ = args;
    }

    public long getAlarmThreshold()
    {
        return alarmThreshold_;
    }

    public void setAlarmThreshold(final long alarmThreshold)
    {
        alarmThreshold_ = alarmThreshold;
    }

    public long getCpuAlarmThreshold()
    {
        return cpuAlarmThreshold_;
    }

    public void setCpuAlarmThreshold(final long cpuAlarmThreshold)
    {
        cpuAlarmThreshold_ = cpuAlarmThreshold;
    }

}
