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
package jp.co.acroquest.endosnipe.common.entity;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 各グラフの計測データを格納するためのエンティティです。
 * @author fujii
 *
 */
public class MeasurementData
{
    /** measurementDetailMap_における、単数系列のデータのキー。 */
    public static final String SINGLE_DETAIL_KEY = "";

    /**
     * 計測値種別を表す ID 。<br />
     *
     * JAVELIN_MESUREMENT テーブルの MESUREMENT_TYPE カラムに利用される値。
     */
    public int measurementType;

    /** リソース通知電文の項目名 */
    public String itemName;

    /** オブジェクトの表示名 */
    public String displayName;

    /** 計測時刻。 */
    public Timestamp measurementTime;

    /** 計測データのマップ(系列名、系列情報) */
    private final Map<String, MeasurementDetail> measurementDetailMap_ =
            new LinkedHashMap<String, MeasurementDetail>();

    /** 計測値の型 */
    public byte valueType;

    /**
     * {@link MeasurementDetail}オブジェクトを保存しているマップを取得します。<br />
     * 
     * @return {@link MeasurementDetail}オブジェクトを保存しているマップ
     */
    public Map<String, MeasurementDetail> getMeasurementDetailMap()
    {
        return this.measurementDetailMap_;
    }

    /**
     * 計測値を保存するエンティティを保存します。<br />
     * 
     * @param measurementDetail {@link MeasurementDetail}オブジェクト
     */
    public void addMeasurementDetail(final MeasurementDetail measurementDetail)
    {
        this.measurementDetailMap_.put(measurementDetail.displayName, measurementDetail);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("(MeasurementType=");
        builder.append(this.measurementType);
        builder.append(",ItemName=");
        builder.append(this.itemName);
        builder.append(",ValueType=");
        builder.append(this.valueType);
        builder.append(",MeasurementDetailMap=");
        builder.append(this.measurementDetailMap_.toString());
        builder.append(")");
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeasurementData clone()
    {
        MeasurementData cloneObj = new MeasurementData();
        cloneObj.itemName = this.itemName;
        cloneObj.measurementType = this.measurementType;
        cloneObj.measurementTime = this.measurementTime;
        cloneObj.valueType = this.valueType;
        cloneObj.displayName = this.displayName;

        for (MeasurementDetail detail : this.measurementDetailMap_.values())
        {
            cloneObj.addMeasurementDetail(detail.clone());
        }

        return cloneObj;
    }
}
