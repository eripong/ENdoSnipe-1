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
package jp.co.acroquest.endosnipe.common.jmx;

import java.util.HashMap;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.logger.SystemLogger;

/**
 * ユーザが設定したJMX情報を管理するためのクラスです。<br />
 * 
 * @author y_asazuma
 */
public class JMXManager
{
    /** インスタンス */
    private static JMXManager manager__ = new JMXManager();

    /** ユーザが指定したJMX項目と計測値種別のマップ(DataCollector用) */
    private final Map<String, Map<String, Long>> measurmentTypeMap_;

    /** JMXの取得値のうち、割合を示す値 */
    public static final String[] JMX_RATIO_ITEMNAME_ARRAY = {"HitRatio"};

    /** 初期化フラグ */
    private boolean initFlag_ = false;

    private NotifyJMXItem callBack_ = null;

    /**
     * インスタンス化を防ぐコンストラクタ
     */
    private JMXManager()
    {
        this.measurmentTypeMap_ = new HashMap<String, Map<String, Long>>(0);
    }

    /**
     * インスタンスを取得します。
     * 
     * @return JMXManagerインスタンス
     */
    public static JMXManager getInstance()
    {
        return manager__;
    }

    /**
     * JMX項目の変更を通知するコールバックメソッドを設定します。
     * 
     * @param callBack コールバックメソッド
     */
    public void setCallBack(final NotifyJMXItem callBack)
    {
        callBack_ = callBack;
    }

    /**
     * 初期化が完了するまでスレッドを待機させます。<br />
     * 初期化完了後に必ず{@link #initCompleted()}を呼び出して下さい。
     */
    public void waitInitialize()
    {
        if (!initFlag_)
        {
            try
            {
                wait();
            }
            catch (Exception e)
            {
                SystemLogger.getInstance().warn(e);
            }
        }
    }

    /**
     * 初期化の完了を通知し、<br />
     * 初期化待ちスレッドがある場合はすべて再開させます。
     */
    public synchronized void initCompleted()
    {
        this.initFlag_ = true;
        notifyAll();
    }

    /**
     * JMX項目と計測値種別のマップを取得します。
     * 
     * @return JMX項目と計測値種別のマップ
     */
    public Map<String, Long> getMeasurmentTypeMap(final String dbName)
    {
        return measurmentTypeMap_.get(dbName);
    }

    /**
     * JMX項目と計測値種別のマップを取得します。
     * 
     * @param dbName データベース名
     * @param map JMX項目と計測値種別のマップ
     */
    public void setMeasurementTypeMap(final String dbName, final Map<String, Long> map)
    {
        this.measurmentTypeMap_.put(dbName, map);
    }

    /**
     * JMX項目に対する計測値種別をマッピングします。
     * 
     * @param dbName データベース名
     * @param objectName オブジェクト名
     * @param objDispName オブジェクト表示名
     * 
     * @return 計測値種別ID<br />既に項目が存在している場合は-1
     */
    public long addMeasurementType(final String dbName, final String objectName,
            final String objDispName)
    {
        long retID = -1L;

        Map<String, Long> map = this.measurmentTypeMap_.get(dbName);
        if (map == null)
        {
            map = new HashMap<String, Long>(1);
        }

        if (!map.containsKey(objectName))
        {
            if (callBack_ != null)
            {
                retID = this.callBack_.addItem(dbName, objectName, objDispName);
            }
            map.put(objectName, Long.valueOf(retID));
            this.measurmentTypeMap_.put(dbName, map);
        }

        return retID;
    }
}
