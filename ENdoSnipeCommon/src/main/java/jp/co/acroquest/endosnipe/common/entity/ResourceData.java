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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 計測データを格納するためのエンティティです。
 * @author fujii
 */
public class ResourceData
{
    /** 計測時刻 */
    public long measurementTime;

    /** ホスト名 */
    public String hostName;

    /** IPアドレス */
    public String ipAddress;

    /** ポート番号 */
    public int portNum;

    /** クライアントID */
    public String clientId;

    /** グラフの計測データを格納するマップ(計測項目名、グラフの計測データ) */
    private final Map<String, MeasurementData> measurementMap_ =
            new LinkedHashMap<String, MeasurementData>();

    /**
     * {@link ResourceData}オブジェクトに{@link MeasurementData}オブジェクトを登録する。
     * 
     * @param mData {@link MeasurementData}オブジェクト
     */
    public void addMeasurementData(final MeasurementData mData)
    {
        measurementMap_.put(mData.itemName, mData);
    }

    /**
     * {@link MeasurementData} オブジェクトを返します。<br />
     *
     * @return {@link MeasurementData} オブジェクト
     */
    public Map<String, MeasurementData> getMeasurementMap()
    {
        return this.measurementMap_;
    }
}
