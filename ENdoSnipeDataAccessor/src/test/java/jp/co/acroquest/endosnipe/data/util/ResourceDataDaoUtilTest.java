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
package jp.co.acroquest.endosnipe.data.util;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import jp.co.acroquest.endosnipe.common.entity.MeasurementData;
import jp.co.acroquest.endosnipe.common.entity.MeasurementDetail;
import jp.co.acroquest.endosnipe.common.entity.ResourceData;
import jp.co.acroquest.endosnipe.data.dao.AbstractDaoTest;
import jp.co.acroquest.endosnipe.data.dao.JavelinMeasurementItemDao;
import jp.co.acroquest.endosnipe.data.dao.MeasurementInfoDao;
import jp.co.acroquest.endosnipe.data.dao.MeasurementValueDao;
import jp.co.acroquest.endosnipe.data.entity.HostInfo;
import jp.co.acroquest.endosnipe.data.entity.JavelinMeasurementItem;
import jp.co.acroquest.endosnipe.data.entity.MeasurementInfo;
import jp.co.acroquest.endosnipe.data.entity.MeasurementValue;
import jp.co.acroquest.endosnipe.util.ResourceDataDaoUtil;

/**
 * {@link ResourceDataDaoUtil} クラスのテストケース。<br />
 *
 * @author sakamoto
 */
public class ResourceDataDaoUtilTest extends AbstractDaoTest
{
    /**
     * ホスト情報。<br />
     */
    private HostInfo hostInfo_;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();

        this.hostInfo_ = new HostInfo();
        this.hostInfo_.hostName = "localhost";
        this.hostInfo_.ipAddress = "127.0.0.1";
        this.hostInfo_.port = 12345;
    }

    /**
     * 2 つのグラフで、　1　つは 1 系列、もう 1 つは 2 系列のデータを作成します。<br />
     *
     * <ul>
     *   <li>ResourceData</li>
     *   <ul>
     *     <li>ResourceInfo</li>
     *     <ul>
     *       <li>displayName : "A"</li>
     *     </ul>
     *     <li>ResourceInfo</li>
     *     <ul>
     *       <li>displayName : "B"</li>
     *       <li>displayName : "C"</li>
     *     </ul>
     *   </ul>
     * </ul>
     *
     * @param hostInfo ホスト情報
     * @return {@link ResourceData} オブジェクト
     */
    private ResourceData createResourceData(final HostInfo hostInfo)
    {
        MeasurementDetail detail;
        MeasurementData measurementInfo;
        ResourceData resourceData = new ResourceData();
        resourceData.hostName = hostInfo.hostName;
        resourceData.ipAddress = hostInfo.ipAddress;
        resourceData.portNum = hostInfo.port;
        resourceData.measurementTime = System.currentTimeMillis();

        ////////// MeasurementInfo 1 Start //////////
        measurementInfo = new MeasurementData();
        measurementInfo.measurementType = 1;
        measurementInfo.itemName = "ItemName1";

        detail = new MeasurementDetail();
        detail.displayName = "A";
        detail.value = "1";
        measurementInfo.addMeasurementDetail(detail);
        resourceData.addMeasurementData(measurementInfo);
        ////////// MeasurementInfo 1 End //////////

        ////////// MeasurementInfo 2 Start //////////
        measurementInfo = new MeasurementData();
        measurementInfo.measurementType = 2;
        measurementInfo.itemName = "ItemName2";

        detail = new MeasurementDetail();
        detail.displayName = "B";
        detail.value = "2";
        measurementInfo.addMeasurementDetail(detail);
        detail = new MeasurementDetail();
        detail.displayName = "C";
        detail.value = "3";
        measurementInfo.addMeasurementDetail(detail);

        resourceData.addMeasurementData(measurementInfo);
        ////////// MeasurementInfo 2 End //////////

        return resourceData;
    }

    /**
     * 計測値種別が 101 から　104 の計測値情報を登録します。
     *
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    private void insertMeasurementInfo()
        throws SQLException
    {
        for (int index = 101; index <= 104; index++)
        {
            MeasurementInfo measurementInfo =
                    new MeasurementInfo(index, "itemName" + index, "Name" + index, "Description"
                            + index);
            MeasurementInfoDao.insert(DB_NAME, measurementInfo);
        }
    }

    /**
     * @target testInsert
     * @test 挿入
     *   condition:: 2 つのグラフで合計 3 つの系列を登録する。
     *   result:: MEASUREMENT_VALUE テーブルに 3 つのレコードと
     *            JAVELIN_MEASUREMENT_ITEM に 3 つのレコードが登録されていること。
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public void testInsert_oneTime()
        throws SQLException
    {
        // 準備
        insertMeasurementInfo();
        ResourceData resourceData = createResourceData(this.hostInfo_);

        // 実行
        ResourceDataDaoUtil.insert(DB_NAME, resourceData, 7, Calendar.DATE);

        // 検証
        List<MeasurementValue> measurementValueList = MeasurementValueDao.selectAll(DB_NAME);
        assertEquals(3, measurementValueList.size());
        List<JavelinMeasurementItem> measurementItemList =
                JavelinMeasurementItemDao.selectAll(DB_NAME);
        assertEquals(3, measurementItemList.size());
    }

    /**
     * @target testInsert
     * @test 挿入
     *   condition:: 2 つのグラフで合計 3 つの系列を２個登録する。
     *   result:: MEASUREMENT_VALUE テーブルに 6 つのレコードと
     *            JAVELIN_MEASUREMENT_ITEM に 3 つのレコードが登録されていること。
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public void testInsert_twoTime()
        throws SQLException
    {
        // 準備
        ResourceData resourceData1 = createResourceData(this.hostInfo_);
        ResourceData resourceData2 = createResourceData(this.hostInfo_);

        // 実行
        ResourceDataDaoUtil.insert(DB_NAME, resourceData1, 7, Calendar.DATE);
        ResourceDataDaoUtil.insert(DB_NAME, resourceData2, 7, Calendar.DATE);

        // 検証
        List<MeasurementValue> measurementValueList = MeasurementValueDao.selectAll(DB_NAME);
        assertEquals(6, measurementValueList.size());
        List<JavelinMeasurementItem> measurementItemList =
                JavelinMeasurementItemDao.selectAll(DB_NAME);
        assertEquals(3, measurementItemList.size());
    }

}
