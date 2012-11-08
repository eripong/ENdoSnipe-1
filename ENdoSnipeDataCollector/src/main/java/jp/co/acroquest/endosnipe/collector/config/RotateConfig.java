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

import jp.co.acroquest.endosnipe.collector.rotate.RotateUtil;

/**
 * ファイルローテートに用いる設定
 * 
 * @author S.Kimura
 *
 */
public class RotateConfig
{
    /** データベース名称 */
    private String database_;

    /** Javelinログ保持期限の値 */
    private int javelinRotatePeriod_;

    /** Javelinログ保持期限の単位種別 */
    private PeriodUnit javelinRotatePeriodUnit_ = PeriodUnit.DAY;

    /** 計測データ保持期限の値 */
    private int measureRotatePeriod_;

    /** 計測データ保持期限の単位種別 */
    private PeriodUnit measureRotatePeriodUnit_ = PeriodUnit.DAY;

    /**
     * @return javelinRotatePeriod
     */
    public int getJavelinRotatePeriod()
    {
        return this.javelinRotatePeriod_;
    }

    /**
     * @param javelinRotatePeriod セットする javelinRotatePeriod
     */
    public void setJavelinRotatePeriod(final int javelinRotatePeriod)
    {
        this.javelinRotatePeriod_ = javelinRotatePeriod;
    }

    /**
     * @param javelinRotatePeriodUnit セットする javelinRotatePeriodUnit
     */
    public void setJavelinRotatePeriodUnit(final PeriodUnit javelinRotatePeriodUnit)
    {
        this.javelinRotatePeriodUnit_ = javelinRotatePeriodUnit;
    }

    /**
     * @param measureRotatePeriod セットする measureRotatePeriod
     */
    public void setMeasureRotatePeriod(final int measureRotatePeriod)
    {
        this.measureRotatePeriod_ = measureRotatePeriod;
    }

    /**
     * @return measureRotatePeriod
     */
    public int getMeasureRotatePeriod()
    {
        return this.measureRotatePeriod_;
    }

    /**
     * @param measureRotatePeriodUnit セットする measureRotatePeriodUnit
     */
    public void setMeasureRotatePeriodUnit(final PeriodUnit measureRotatePeriodUnit)
    {
        this.measureRotatePeriodUnit_ = measureRotatePeriodUnit;
    }

    /**
     * Javelinログ保持期限の単位を
     * Calendarクラスのインデックスとして取得
     * 
     * @return Javelinログ保持期限の単位
     */
    public int getJavelinUnitByCalendar()
    {
        return convertUnit(this.javelinRotatePeriodUnit_);
    }

    /**
     * 計測データ保持期限の単位を
     * Calendarクラスのインデックスとして取得
     * 
     * @return Javelinログ保持期限の単位
     */
    public int getMeasureUnitByCalendar()
    {
        return convertUnit(this.measureRotatePeriodUnit_);
    }

    /**
     * 保持期間の単位をCalendarクラスのインデックスに変換
     * 
     * @param unit 保持期間の単位
     * @return Calendarクラスのインデックス
     */
    private int convertUnit(final PeriodUnit unit)
    {
        int result = RotateUtil.convertUnit(unit);
        return result;
    }

    /**
     * @return database
     */
    public String getDatabase()
    {
        return this.database_;
    }

    /**
     * @param database データベース名
     */
    public void setDatabase(final String database)
    {
        this.database_ = database;
    }

}
