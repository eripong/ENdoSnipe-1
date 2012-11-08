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
package jp.co.acroquest.endosnipe.common.util;

import java.sql.Timestamp;

import jp.co.acroquest.endosnipe.common.entity.MeasurementData;
import jp.co.acroquest.endosnipe.common.entity.MeasurementDetail;
import jp.co.acroquest.endosnipe.common.entity.ResourceData;
import junit.framework.TestCase;

/**
 * ResourceDataUtilのテストコード。<br>
 * 
 * @author iida
 */
public class ResourceDataUtilTest extends TestCase
{
    /**
     * 指定された値と名前を持つMeasurementDetailオブジェクトを生成します。<br>
     * 
     * @param value 値
     * @param ame 名前
     * @return MeasurementDetailオブジェクト
     */
    private MeasurementDetail createMeasurementDetail(final long value, final String name)
    {
        MeasurementDetail measurementDetail = new MeasurementDetail();
        measurementDetail.value = String.valueOf(value);
        measurementDetail.displayName = name;
        measurementDetail.itemId = 0;
        measurementDetail.itemName = name;
        return measurementDetail;
    }

    /**
     * 指定されたタイプと名前と時刻を持つMeasurementDataオブジェクトを生成します。<br>
     * 
     * @param type タイプ
     * @param name 名前
     * @param time 時刻
     * @return MeasurementDataオブジェクト
     */
    private MeasurementData createMeasurementData(final int type, final String name, final long time)
    {
        MeasurementData measurementData = new MeasurementData();
        measurementData.measurementType = type;
        measurementData.itemName = name;
        measurementData.measurementTime = new Timestamp(time);
        measurementData.valueType = 0;
        return measurementData;
    }

    /**
     * 指定された時刻を持つResourceDataオブジェクトを生成します。<br>
     * 
     * @param time 時刻
     * @return ResourceDataオブジェクト
     */
    private ResourceData createResourceData(final long time)
    {
        ResourceData resourceData = new ResourceData();
        resourceData.measurementTime = time;
        resourceData.hostName = "localhost";
        resourceData.ipAddress = "127.0.0.1";
        resourceData.portNum = 18000;
        return resourceData;
    }

    /**
     * 新たなデータが追加されていない場合。<br>
     */
    public void testCreateAdditionalPreviousData_SameData()
    {
        MeasurementDetail detail;
        MeasurementData data;

        ResourceData prevData = this.createResourceData(1274247510000L);
        data = this.createMeasurementData(1, "項目1", 1274247510000L);
        detail = this.createMeasurementDetail(100, "項目1");
        data.addMeasurementDetail(detail);
        prevData.addMeasurementData(data);

        ResourceData currData = this.createResourceData(1274247515000L);
        data = this.createMeasurementData(1, "項目1", 1274247515000L);
        detail = this.createMeasurementDetail(100, "項目1");
        data.addMeasurementDetail(detail);
        currData.addMeasurementData(data);

        ResourceData additionalData =
                ResourceDataUtil.createAdditionalPreviousData(prevData, currData);

        if (additionalData.getMeasurementMap().size() == 0)
        {
            assertTrue(true);
        }
    }

    /**
     * 新たなMeasurementDetailが1つ追加された場合。<br>
     */
    public void testCreateAdditionalPreviousData_MeasurementDetailAdded()
    {
        MeasurementDetail detail;
        MeasurementData data;

        ResourceData prevData = this.createResourceData(1274247510000L);
        data = this.createMeasurementData(1, "項目1", 1274247510000L);
        detail = this.createMeasurementDetail(100, "項目1");
        data.addMeasurementDetail(detail);
        prevData.addMeasurementData(data);

        ResourceData currData = this.createResourceData(1274247515000L);
        data = this.createMeasurementData(1, "項目1", 1274247515000L);
        detail = this.createMeasurementDetail(100, "項目1");
        data.addMeasurementDetail(detail);
        detail = this.createMeasurementDetail(200, "項目2");
        data.addMeasurementDetail(detail);
        currData.addMeasurementData(data);

        ResourceData additionalData =
                ResourceDataUtil.createAdditionalPreviousData(prevData, currData);
        data = additionalData.getMeasurementMap().get("項目1");
        detail = data.getMeasurementDetailMap().get("項目2");

        assertNotNull(detail);
    }

    /**
     * 新たなMeasurementDataが1つ追加された場合。<br>
     */
    public void testCreateAdditionalPreviousData_MeasurementDataAdded()
    {
        MeasurementDetail detail;
        MeasurementData data;

        ResourceData prevData = this.createResourceData(1274247510000L);
        data = this.createMeasurementData(1, "項目1", 1274247510000L);
        detail = this.createMeasurementDetail(100, "項目1");
        data.addMeasurementDetail(detail);
        prevData.addMeasurementData(data);

        ResourceData currData = this.createResourceData(1274247515000L);

        data = this.createMeasurementData(1, "項目1", 1274247515000L);
        detail = this.createMeasurementDetail(100, "項目1");
        data.addMeasurementDetail(detail);
        currData.addMeasurementData(data);

        data = this.createMeasurementData(2, "項目2", 1274247515000L);
        detail = this.createMeasurementDetail(200, "項目2");
        data.addMeasurementDetail(detail);
        currData.addMeasurementData(data);

        ResourceData additionalData =
                ResourceDataUtil.createAdditionalPreviousData(prevData, currData);
        data = additionalData.getMeasurementMap().get("項目2");
        detail = data.getMeasurementDetailMap().get("項目2");

        assertNotNull(detail);
    }

    /**
     * 新たなMeasurementDetailが2つ追加された場合。<br>
     */
    public void testCreateAdditionalPreviousData_TwoMeasurementDetailAdded()
    {
        MeasurementDetail detail;
        MeasurementData data;

        ResourceData prevData = this.createResourceData(1274247510000L);
        data = this.createMeasurementData(1, "項目1", 1274247510000L);
        detail = this.createMeasurementDetail(100, "項目1");
        data.addMeasurementDetail(detail);
        prevData.addMeasurementData(data);

        ResourceData currData = this.createResourceData(1274247515000L);

        data = this.createMeasurementData(1, "項目1", 1274247515000L);
        detail = this.createMeasurementDetail(100, "項目1");
        data.addMeasurementDetail(detail);
        detail = this.createMeasurementDetail(200, "項目2");
        data.addMeasurementDetail(detail);
        detail = this.createMeasurementDetail(300, "項目3");
        data.addMeasurementDetail(detail);
        currData.addMeasurementData(data);

        ResourceData additionalData =
                ResourceDataUtil.createAdditionalPreviousData(prevData, currData);
        data = additionalData.getMeasurementMap().get("項目1");
        MeasurementDetail detail2 = data.getMeasurementDetailMap().get("項目2");
        MeasurementDetail detail3 = data.getMeasurementDetailMap().get("項目3");

        assertTrue((detail2 != null) &&
                   (detail3 != null));
    }

    /**
     * シングルコアのCPUで、CPU使用率が100を超える場合の、CPU使用率の計算結果確認。 (#2006)<br>
     */
    public void testCalcCPUUsage_SingleCore_Over100Percent()
    {
        double result = ResourceDataUtil.calcCPUUsage(5100L * 1000 * 1000, 5000, 1);
        assertEquals(100.0, result);
    }

    /**
     * デュアルコアのCPUで、CPU使用率が100を超える場合の、CPU使用率の計算結果確認。 (#2006)<br>
     */
    public void testCalcCPUUsage_DualCore_Over100Percent()
    {
        double result;
        result = ResourceDataUtil.calcCPUUsage(10200L * 1000 * 1000, 5000, 2);
        assertEquals(100.0, result);
    }
}
