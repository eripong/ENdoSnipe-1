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
package jp.co.acroquest.endosnipe.collector.config;

import jp.co.acroquest.endosnipe.collector.exception.InitializeException;

/**
 * Javelin エージェントへの接続設定を保持するクラスです。<br />
 * 
 * @author y-komori
 */
public class AgentSetting
{
    /** 接続先ポート番号のデフォルト値 */
    public static final int        DEF_PORT                              = 18000;

    /** BottleneckEye からの接続待ち受けポート番号のデフォルト値 */
    public static final int        DEF_ACCEPT_PORT                       = DEF_PORT + 10000;

    /** Javelinログの最大蓄積期間のデフォルト値 */
    public static final String     DEF_JVN_LOG_STRAGE_PERIOD             = "7d";

    /** 計測データの最大蓄積期間のデフォルト値 */
    public static final String     DEF_MEASUREMENT_JVN_LOG_STRAGE_PERIOD = "7d";

    public static final String     NONE                                  = "NONE";

    public static final int        DEF_PERIOD                            = 7;

    public static final PeriodUnit DEF_PERIOD_UNIT                       = PeriodUnit.DAY;

    /** 1日の時間数 */
    public static final int        HOURS_PER_DAY                         = 24;

    /** エージェント ID */
    public int                      agentId;

    /** データベース名 */
    public String                   databaseName;

    /** 接続先ホスト名 */
    public String                   hostName;

    /** 接続先ポート番号 */
    public int                      port                                  = DEF_PORT;

    /** BottleneckEye からの接続待ち受けポート番号 */
    public int                      acceptPort                            = DEF_ACCEPT_PORT;

    /** Javelinログの最大蓄積期間 */
    public String                   jvnLogStragePeriod                    =
                                                                            DEF_JVN_LOG_STRAGE_PERIOD;

    /** 計測データの最大蓄積件数 */
    public String                   measureStragePeriod                   =
                                                                            DEF_MEASUREMENT_JVN_LOG_STRAGE_PERIOD;

    /** Javelinログの蓄積期間(単位) */
    private PeriodUnit              jvnLogStragePeriodUnit_;

    /** 計測データの蓄積期間(単位) */
    private PeriodUnit              measureStragePeriodUnit_;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "Host:" + hostName + " Port:" + port;
    }

    /**
     * Javelinログの蓄積期間(値)を取得します。<br />
     * 
     * @return Javelinログの蓄積期間(値)
     * @throws InitializeException パラメータの初期化に失敗した場合
     */
    public int getJavelinRotatePeriod()
        throws InitializeException
    {
        if (NONE.equals(this.jvnLogStragePeriod))
        {
            this.jvnLogStragePeriodUnit_ = DEF_PERIOD_UNIT;
            return 0;
        }
        if (this.jvnLogStragePeriod == null || this.jvnLogStragePeriod.length() < 1)
        {
            this.jvnLogStragePeriodUnit_ = DEF_PERIOD_UNIT;
            return DEF_PERIOD;
        }
        String storagePriodStr =
                                 this.jvnLogStragePeriod.substring(
                                                                   0,
                                                                   this.jvnLogStragePeriod.length() - 1);
        int storagePeriod = DEF_PERIOD;
        try
        {
            storagePeriod = Integer.parseInt(storagePriodStr);
            String storagePriodUnitStr =
                                         this.jvnLogStragePeriod.substring(this.jvnLogStragePeriod.length() - 1);
            if ("d".equals(storagePriodUnitStr))
            {
                this.jvnLogStragePeriodUnit_ = PeriodUnit.DAY;
            }
            else if ("m".equals(storagePriodUnitStr))
            {
                this.jvnLogStragePeriodUnit_ = PeriodUnit.MONTH;
            }
            else if ("h".equals(storagePriodUnitStr))
            {
                // 時間単位で記述されている場合は、日付単位に切り上げる
                this.jvnLogStragePeriodUnit_ = PeriodUnit.DAY;
                storagePeriod = (storagePeriod + HOURS_PER_DAY - 1) / HOURS_PER_DAY;
            }
            else
            {
                throw new InitializeException("Invalid Unit.");
            }
        }
        catch (NumberFormatException ex)
        {
            throw new InitializeException(ex);
        }
        return storagePeriod;
    }

    /**
     * Javelinログの蓄積期間(単位)を取得します。<br />
     * 
     * @return Javelinログの蓄積期間(単位)
     */
    public PeriodUnit getJavelinRotatePeriodUnit()
    {
        return this.jvnLogStragePeriodUnit_;
    }

    /**
     * 計測データの蓄積期間(値)を取得します。<br />
     * 
     * @return 計測データの蓄積期間(値)
     * @throws InitializeException パラメータの初期化に失敗した場合
     */
    public int getMeasurementRotatePeriod()
        throws InitializeException
    {
        if (NONE.equals(this.measureStragePeriod))
        {
            this.measureStragePeriodUnit_ = DEF_PERIOD_UNIT;
            return 0;
        }
        if (this.measureStragePeriod == null || this.measureStragePeriod.length() < 1)
        {
            this.measureStragePeriodUnit_ = DEF_PERIOD_UNIT;
            return DEF_PERIOD;
        }
        String storagePriodStr =
                                 this.measureStragePeriod.substring(
                                                                    0,
                                                                    this.measureStragePeriod.length() - 1);
        int storagePeriod = DEF_PERIOD;
        try
        {
            storagePeriod = Integer.parseInt(storagePriodStr);
            String storagePriodUnitStr =
                                         this.measureStragePeriod.substring(this.measureStragePeriod.length() - 1);
            if ("d".equals(storagePriodUnitStr))
            {
                this.measureStragePeriodUnit_ = PeriodUnit.DAY;
            }
            else if ("m".equals(storagePriodUnitStr))
            {
                this.measureStragePeriodUnit_ = PeriodUnit.MONTH;
            }
            else if ("h".equals(storagePriodUnitStr))
            {
                // 時間単位で記述されている場合は、日付単位に切り上げる
                this.measureStragePeriodUnit_ = PeriodUnit.DAY;
                storagePeriod = (storagePeriod + HOURS_PER_DAY - 1) / HOURS_PER_DAY;
            }
            else
            {
                throw new InitializeException("Invalid Unit.");
            }
        }
        catch (NumberFormatException ex)
        {
            throw new InitializeException(ex);
        }
        return storagePeriod;
    }

    /**
     * 計測データの蓄積期間(単位)を取得します。<br />
     * 
     * @return 計測データの蓄積期間(単位)
     */
    public PeriodUnit getMeasurementRotatePeriodUnit()
    {
        return this.measureStragePeriodUnit_;
    }
}
