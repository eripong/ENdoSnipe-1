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
package jp.co.acroquest.endosnipe.data.dao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.data.dto.MeasurementValueDto;
import jp.co.acroquest.endosnipe.test.DataAccessorTestUtil;

public class MeasurementNamedValueAccessorTest extends AbstractDaoTest
{
    private static final String[] MEASUREMENT_VALUE_DATA =
    {
        "1, 1, 2009/08/04 21:13:23, 31, 1, 95",
        "2, 1, 2009/08/04 21:13:23, 32, 6, 7",
        "3, 1, 2009/08/04 21:13:23, 38, 11, 200",
        "4, 1, 2009/08/04 21:13:23, 39, 16, 25",
        "5, 1, 2009/08/04 21:13:23, 31, 2, 610",
        "6, 1, 2009/08/04 21:13:23, 32, 7, 5",
        "7, 1, 2009/08/04 21:13:23, 38, 12, 2100",
        "8, 1, 2009/08/04 21:13:23, 39, 17, 100",
        "9, 1, 2009/08/04 21:13:23, 31, 3, 900",
        "10, 1, 2009/08/04 21:13:23, 32, 8, 2",
        "11, 1, 2009/08/04 21:13:23, 38, 13, 1500",
        "12, 1, 2009/08/04 21:13:23, 39, 18, 300",
        "13, 1, 2009/08/04 21:13:23, 31, 4, 600",
        "14, 1, 2009/08/04 21:13:23, 32, 9, 3",
        "15, 1, 2009/08/04 21:13:23, 38, 14, 890",
        "16, 1, 2009/08/04 21:13:23, 39, 19, 320",
        "17, 1, 2009/08/04 21:13:23, 31, 5, 600",
        "18, 1, 2009/08/04 21:13:23, 32, 10, 1",
        "19, 1, 2009/08/04 21:13:23, 38, 15, 600",
        "20, 1, 2009/08/04 21:13:23, 39, 20, 600",
        "21, 1, 2009/08/04 21:13:23, 40, 21, 650",
        "22, 1, 2009/08/04 21:13:23, 41, 24, 190",
        "23, 1, 2009/08/04 21:13:23, 40, 22, 720",
        "24, 1, 2009/08/04 21:13:23, 41, 25, 121",
        "25, 1, 2009/08/04 21:13:23, 40, 23, 1020",
        "26, 1, 2009/08/04 21:13:23, 41, 26, 512",
        "27, 2, 2009/08/04 21:13:29, 31, 1, 50",
        "28, 2, 2009/08/04 21:13:29, 32, 6, 9",
        "29, 2, 2009/08/04 21:13:29, 38, 11, 60",
        "30, 2, 2009/08/04 21:13:29, 39, 16, 30",
        "31, 2, 2009/08/04 21:13:29, 31, 2, 905",
        "32, 2, 2009/08/04 21:13:29, 32, 7, 4",
        "33, 2, 2009/08/04 21:13:29, 38, 12, 3200",
        "34, 2, 2009/08/04 21:13:29, 39, 17, 40",
        "35, 2, 2009/08/04 21:13:29, 31, 3, 783",
        "36, 2, 2009/08/04 21:13:29, 32, 8, 8",
        "37, 2, 2009/08/04 21:13:29, 38, 13, 1200",
        "38, 2, 2009/08/04 21:13:29, 39, 18, 490",
        "39, 2, 2009/08/04 21:13:29, 31, 4, 75",
        "40, 2, 2009/08/04 21:13:29, 32, 9, 10",
        "41, 2, 2009/08/04 21:13:29, 38, 14, 200",
        "42, 2, 2009/08/04 21:13:29, 39, 19, 40",
        "43, 2, 2009/08/04 21:13:29, 31, 5, 800",
        "44, 2, 2009/08/04 21:13:29, 32, 10, 1",
        "45, 2, 2009/08/04 21:13:29, 38, 15, 800",
        "46, 2, 2009/08/04 21:13:29, 39, 20, 800",
        "47, 2, 2009/08/04 21:13:29, 40, 21, 650",
        "48, 2, 2009/08/04 21:13:29, 41, 24, 190",
        "49, 2, 2009/08/04 21:13:29, 40, 22, 720",
        "50, 2, 2009/08/04 21:13:29, 41, 25, 121",
        "51, 2, 2009/08/04 21:13:29, 40, 23, 1020",
        "52, 2, 2009/08/04 21:13:29, 41, 26, 512",
        "53, 3, 2009/08/04 21:13:41, 31, 2, 120",
        "54, 3, 2009/08/04 21:13:41, 32, 7, 8",
        "55, 3, 2009/08/04 21:13:41, 38, 12, 310",
        "56, 3, 2009/08/04 21:13:41, 39, 17, 80",
        "57, 3, 2009/08/04 21:13:41, 31, 3, 180",
        "58, 3, 2009/08/04 21:13:41, 32, 8, 4",
        "59, 3, 2009/08/04 21:13:41, 38, 13, 230",
        "60, 3, 2009/08/04 21:13:41, 39, 18, 110",
        "61, 3, 2009/08/04 21:13:41, 31, 4, 570",
        "62, 3, 2009/08/04 21:13:41, 32, 9, 2",
        "63, 3, 2009/08/04 21:13:41, 38, 14, 720",
        "64, 3, 2009/08/04 21:13:41, 39, 19, 420",
        "65, 4, 2009/08/04 21:13:47, 31, 1, 500",
        "66, 4, 2009/08/04 21:13:47, 32, 6, 4",
        "67, 4, 2009/08/04 21:13:47, 38, 11, 900",
        "68, 4, 2009/08/04 21:13:47, 39, 16, 200",
        "69, 4, 2009/08/04 21:13:47, 31, 2, 700",
        "70, 4, 2009/08/04 21:13:47, 32, 7, 1",
        "71, 4, 2009/08/04 21:13:47, 38, 12, 700",
        "72, 4, 2009/08/04 21:13:47, 39, 17, 700"
    };
    
    private static final String[] JAVELIN_MEASUREMENT_ITEM_DATA =
    {
        "1, 31, jp.co.acroquest.mc.event.io.ListIOStream#print, 2010/10/9 11:23:56",
        "2, 31, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish, 2010/10/9 11:23:56",
        "3, 31, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process, 2010/10/9 11:23:56",
        "4, 31, jp.co.acroquest.gms.journal.JournalManager#onEvent, 2010/10/9 11:23:56",
        "5, 31, jp.co.acroquest.gms.nameconv.NamingConverter#convert, 2010/10/9 11:23:56",
        "6, 32, jp.co.acroquest.mc.event.io.ListIOStream#print, 2010/10/9 11:23:56",
        "7, 32, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish, 2010/10/9 11:23:56",
        "8, 32, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process, 2010/10/9 11:23:56",
        "9, 32, jp.co.acroquest.gms.journal.JournalManager#onEvent, 2010/10/9 11:23:56",
        "10, 32, jp.co.acroquest.gms.nameconv.NamingConverter#convert, 2010/10/9 11:23:56",
        "11, 38, jp.co.acroquest.mc.event.io.ListIOStream#print, 2010/10/9 11:23:56",
        "12, 38, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish, 2010/10/9 11:23:56",
        "13, 38, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process, 2010/10/9 11:23:56",
        "14, 38, jp.co.acroquest.gms.journal.JournalManager#onEvent, 2010/10/9 11:23:56",
        "15, 38, jp.co.acroquest.gms.nameconv.NamingConverter#convert, 2010/10/9 11:23:56",
        "16, 39, jp.co.acroquest.mc.event.io.ListIOStream#print, 2010/10/9 11:23:56",
        "17, 39, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish, 2010/10/9 11:23:56",
        "18, 39, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process, 2010/10/9 11:23:56",
        "19, 39, jp.co.acroquest.gms.journal.JournalManager#onEvent, 2010/10/9 11:23:56",
        "20, 39, jp.co.acroquest.gms.nameconv.NamingConverter#convert, 2010/10/9 11:23:56",
        "21, 40, GMS-AP-Server1, 2010/10/9 11:23:56",
        "22, 40, GMS-AP-Server2, 2010/10/9 11:23:56",
        "23, 40, GMS-GW-Server1, 2010/10/9 11:23:56",
        "24, 41, GMS-AP-Server1, 2010/10/9 11:23:56",
        "25, 41, GMS-AP-Server2, 2010/10/9 11:23:56",
        "26, 41, GMS-GW-Server1, 2010/10/9 11:23:56"
    };
    
    private static final String[] MEASUREMENT_INFO_DATA = 
    {
        "31, process.response.time.average, 平均レスポンスタイム, TATの平均値",
        "32, process.response.total.count, アクセス数, アクセス回数",
        "38, process.response.time.max, 最大レスポンスタイム, TATの最大値",
        "39, process.response.time.min, 最小レスポンスタイム, TATの最小値",
        "40, callTreeNodeCount, CallTreeノード数, CallTreeのノード数",
        "41, convertedMethodCount, 変換メソッド数, Javelinが変換したメソッドの数"
    };
    
    private void initDataBase()
    {
//        DataAccessorTestUtil.initializeMeasurementInfoTable(MEASUREMENT_INFO_DATA);
        DataAccessorTestUtil.initializeJavelinMeasurementItemTable(JAVELIN_MEASUREMENT_ITEM_DATA);
        DataAccessorTestUtil.initializeMeasurementValueTable(MEASUREMENT_VALUE_DATA);
    }
    
    private Timestamp convertString2TimeStamp(String dateString) throws ParseException
    {
        return new Timestamp(
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(dateString).getTime());

    }
    
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    public void testNamedDataSelect1()
    {
        commonTestSelectByTermAndMeasurementTypeWithName(
            "2009/08/04 21:13:21", 
            "2009/08/04 21:13:50", 
            "process.response.total.count", 
            new String[] 
            {
                "14, 1, 2009/08/04 21:13:23, 32, 9, 3, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                "40, 2, 2009/08/04 21:13:29, 32, 9, 10, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.journal.JournalManager#onEvent, 2010/10/9 11:23:56", 
                "62, 3, 2009/08/04 21:13:41, 32, 9, 2, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.journal.JournalManager#onEvent, 2010/10/9 11:23:56", 
                "18, 1, 2009/08/04 21:13:23, 32, 10, 1, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.nameconv.NamingConverter#convert, 2010/10/9 11:23:56", 
                "44, 2, 2009/08/04 21:13:29, 32, 10, 1, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.nameconv.NamingConverter#convert, 2010/10/9 11:23:56", 
                "6, 1, 2009/08/04 21:13:23, 32, 7, 5, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish, 2010/10/9 11:23:56", 
                "32, 2, 2009/08/04 21:13:29, 32, 7, 4, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish, 2010/10/9 11:23:56", 
                "54, 3, 2009/08/04 21:13:41, 32, 7, 8, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish, 2010/10/9 11:23:56", 
                "70, 4, 2009/08/04 21:13:47, 32, 7, 1, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish, 2010/10/9 11:23:56", 
                "10, 1, 2009/08/04 21:13:23, 32, 8, 2, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process, 2010/10/9 11:23:56", 
                "36, 2, 2009/08/04 21:13:29, 32, 8, 8, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process, 2010/10/9 11:23:56", 
                "58, 3, 2009/08/04 21:13:41, 32, 8, 4, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process, 2010/10/9 11:23:56", 
                "2, 1, 2009/08/04 21:13:23, 32, 6, 7, process.response.total.count, レスポンス回数, jp.co.acroquest.mc.event.io.ListIOStream#print, 2010/10/9 11:23:56", 
                "28, 2, 2009/08/04 21:13:29, 32, 6, 9, process.response.total.count, レスポンス回数, jp.co.acroquest.mc.event.io.ListIOStream#print, 2010/10/9 11:23:56", 
                "66, 4, 2009/08/04 21:13:47, 32, 6, 4, process.response.total.count, レスポンス回数, jp.co.acroquest.mc.event.io.ListIOStream#print, 2010/10/9 11:23:56"
            }
        );
    }
    
    public void testNamedDataSelect2()
    {
        commonTestSelectByTermAndMeasurementTypeWithName(
            "2009/08/04 21:13:24", 
            "2009/08/04 21:13:45", 
            "process.response.total.count", 
            new String[] 
            {
                "40, 2, 2009/08/04 21:13:29, 32, 9, 10, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                "62, 3, 2009/08/04 21:13:41, 32, 9, 2, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                "44, 2, 2009/08/04 21:13:29, 32, 10, 1, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.nameconv.NamingConverter#convert", 
                "32, 2, 2009/08/04 21:13:29, 32, 7, 4, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                "54, 3, 2009/08/04 21:13:41, 32, 7, 8, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                "36, 2, 2009/08/04 21:13:29, 32, 8, 8, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                "58, 3, 2009/08/04 21:13:41, 32, 8, 4, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                "28, 2, 2009/08/04 21:13:29, 32, 6, 9, process.response.total.count, レスポンス回数, jp.co.acroquest.mc.event.io.ListIOStream#print" 
            }
        );
    }

    public void testNamedDataSelect3()
    {
        commonTestSelectByTermAndMeasurementTypeWithName(
            "2009/08/04 21:13:29", 
            "2009/08/04 21:13:41", 
            "process.response.total.count", 
            new String[] 
            {
                "40, 2, 2009/08/04 21:13:29, 32, 9, 10, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                "62, 3, 2009/08/04 21:13:41, 32, 9, 2, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                "44, 2, 2009/08/04 21:13:29, 32, 10, 1, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.nameconv.NamingConverter#convert", 
                "32, 2, 2009/08/04 21:13:29, 32, 7, 4, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                "54, 3, 2009/08/04 21:13:41, 32, 7, 8, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                "36, 2, 2009/08/04 21:13:29, 32, 8, 8, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                "58, 3, 2009/08/04 21:13:41, 32, 8, 4, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                "28, 2, 2009/08/04 21:13:29, 32, 6, 9, process.response.total.count, レスポンス回数, jp.co.acroquest.mc.event.io.ListIOStream#print" 
            }
        );
    }

    public void testNamedDataSelect4()
    {
        commonTestSelectByTermAndMeasurementTypeWithName(
            "2009/08/04 21:13:32", 
            "2009/08/04 21:13:38", 
            "process.response.total.count", 
            new String[0] 
        );
    }

    public void testNamedDataSelect5()
    {
        commonTestSelectByTermAndMeasurementTypeWithName(
            "2009/08/04 21:13:21", 
            "2009/08/04 21:13:50", 
            "process.response.time.min", 
            new String[] 
            {
                "16, 1, 2009/08/04 21:13:23, 39, 19, 320, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                "42, 2, 2009/08/04 21:13:29, 39, 19, 40, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                "64, 3, 2009/08/04 21:13:41, 39, 19, 420, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                "20, 1, 2009/08/04 21:13:23, 39, 20, 600, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.nameconv.NamingConverter#convert", 
                "46, 2, 2009/08/04 21:13:29, 39, 20, 800, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.nameconv.NamingConverter#convert", 
                "8, 1, 2009/08/04 21:13:23, 39, 17, 100, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                "34, 2, 2009/08/04 21:13:29, 39, 17, 40, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                "56, 3, 2009/08/04 21:13:41, 39, 17, 80, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                "72, 4, 2009/08/04 21:13:47, 39, 17, 700, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                "12, 1, 2009/08/04 21:13:23, 39, 18, 300, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                "38, 2, 2009/08/04 21:13:29, 39, 18, 490, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                "60, 3, 2009/08/04 21:13:41, 39, 18, 110, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                "4, 1, 2009/08/04 21:13:23, 39, 16, 25, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.mc.event.io.ListIOStream#print", 
                "30, 2, 2009/08/04 21:13:29, 39, 16, 30, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.mc.event.io.ListIOStream#print", 
                "68, 4, 2009/08/04 21:13:47, 39, 16, 200, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.mc.event.io.ListIOStream#print"
            }
        );
    }

    public void testNamedMaxSelect1()
    {
        commonTestSelectMaxValueByTermAndMeasurementTypeWithName(
            "2009/08/04 21:13:21",
            "2009/08/04 21:13:50",
            "process.response.time.max",
            new String[]
            {
                "15, 1, 2009/08/04 21:13:23, 38, 14, 890, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                "45, 2, 2009/08/04 21:13:29, 38, 15, 800, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.gms.nameconv.NamingConverter#convert", 
                "33, 2, 2009/08/04 21:13:29, 38, 12, 3200, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                "11, 1, 2009/08/04 21:13:23, 38, 13, 1500, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                "67, 4, 2009/08/04 21:13:47, 38, 11, 900, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.mc.event.io.ListIOStream#print"
            }
        );
    }

    public void testNamedMaxSelect2()
    {
        commonTestSelectMaxValueByTermAndMeasurementTypeWithName(
            "2009/08/04 21:13:24",
            "2009/08/04 21:13:45",
            "process.response.time.max",
            new String[]
            {
                "63, 3, 2009/08/04 21:13:41, 38, 14, 720, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                "45, 2, 2009/08/04 21:13:29, 38, 15, 800, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.gms.nameconv.NamingConverter#convert", 
                "33, 2, 2009/08/04 21:13:29, 38, 12, 3200, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                "37, 2, 2009/08/04 21:13:29, 38, 13, 1200, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                "29, 2, 2009/08/04 21:13:29, 38, 11, 60, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.mc.event.io.ListIOStream#print"
            }
        );
    }

    public void testNamedMaxSelect3()
    {
        commonTestSelectMaxValueByTermAndMeasurementTypeWithName(
            "2009/08/04 21:13:41",
            "2009/08/04 21:13:47",
            "process.response.time.max",
            new String[]
            {
                "63, 3, 2009/08/04 21:13:41, 38, 14, 720, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                "71, 4, 2009/08/04 21:13:47, 38, 12, 700, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                "59, 3, 2009/08/04 21:13:41, 38, 13, 230, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                "67, 4, 2009/08/04 21:13:47, 38, 11, 900, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.mc.event.io.ListIOStream#print" 
            }
        );
    }

    public void testNamedMaxSelect4()
    {
        commonTestSelectMaxValueByTermAndMeasurementTypeWithName(
            "2009/08/04 21:13:32",
            "2009/08/04 21:13:38",
            "process.response.time.max",
            new String[0]
        );
    }

    public void testNamedMaxSelect5()
    {
        commonTestSelectMaxValueByTermAndMeasurementTypeWithName(
            "2009/08/04 21:13:21",
            "2009/08/04 21:13:50",
            "process.response.time.min",
            new String[]
            {
                "64, 3, 2009/08/04 21:13:41, 39, 19, 420, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                "46, 2, 2009/08/04 21:13:29, 39, 20, 800, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.nameconv.NamingConverter#convert", 
                "72, 4, 2009/08/04 21:13:47, 39, 17, 700, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                "38, 2, 2009/08/04 21:13:29, 39, 18, 490, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                "68, 4, 2009/08/04 21:13:47, 39, 16, 200, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.mc.event.io.ListIOStream#print" 
            }
        );
    }
    
    public void testNamedMinSelect1()
    {
        commonTestSelectMinValueByTermAndMeasurementTypeWithName(
             "2009/08/04 21:13:21",
             "2009/08/04 21:13:50",
             "process.response.time.min",
             new String[]
             {
                 "42, 2, 2009/08/04 21:13:29, 39, 19, 40, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                 "20, 1, 2009/08/04 21:13:23, 39, 20, 600, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.nameconv.NamingConverter#convert", 
                 "34, 2, 2009/08/04 21:13:29, 39, 17, 40, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                 "60, 3, 2009/08/04 21:13:41, 39, 18, 110, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                 "4, 1, 2009/08/04 21:13:23, 39, 16, 25, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.mc.event.io.ListIOStream#print", 
             }
        );
    }

    public void testNamedMinSelect2()
    {
        commonTestSelectMinValueByTermAndMeasurementTypeWithName(
             "2009/08/04 21:13:24",
             "2009/08/04 21:13:45",
             "process.response.time.min",
             new String[]
             {
                 "42, 2, 2009/08/04 21:13:29, 39, 19, 40, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                 "46, 2, 2009/08/04 21:13:29, 39, 20, 800, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.nameconv.NamingConverter#convert", 
                 "34, 2, 2009/08/04 21:13:29, 39, 17, 40, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                 "60, 3, 2009/08/04 21:13:41, 39, 18, 110, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                 "30, 2, 2009/08/04 21:13:29, 39, 16, 30, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.mc.event.io.ListIOStream#print" 
             }
        );
    }

    public void testNamedMinSelect3()
    {
        commonTestSelectMinValueByTermAndMeasurementTypeWithName(
             "2009/08/04 21:13:41",
             "2009/08/04 21:13:47",
             "process.response.time.min",
             new String[]
             {
                 "64, 3, 2009/08/04 21:13:41, 39, 19, 420, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                 "56, 3, 2009/08/04 21:13:41, 39, 17, 80, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                 "60, 3, 2009/08/04 21:13:41, 39, 18, 110, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                 "68, 4, 2009/08/04 21:13:47, 39, 16, 200, process.response.time.min, レスポンスタイムの最小値, jp.co.acroquest.mc.event.io.ListIOStream#print" 
             }
        );
    }

    public void testNamedMinSelect4()
    {
        commonTestSelectMinValueByTermAndMeasurementTypeWithName(
             "2009/08/04 21:13:32",
             "2009/08/04 21:13:38",
             "process.response.time.min",
             new String[0]
        );
    }

    public void testNamedMinSelect5()
    {
        commonTestSelectMinValueByTermAndMeasurementTypeWithName(
             "2009/08/04 21:13:21",
             "2009/08/04 21:13:50",
             "process.response.time.average",
             new String[]
             {
                 "39, 2, 2009/08/04 21:13:29, 31, 4, 75, process.response.time.average, レスポンスタイムの平均値, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                 "17, 1, 2009/08/04 21:13:23, 31, 5, 600, process.response.time.average, レスポンスタイムの平均値, jp.co.acroquest.gms.nameconv.NamingConverter#convert", 
                 "53, 3, 2009/08/04 21:13:41, 31, 2, 120, process.response.time.average, レスポンスタイムの平均値, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                 "57, 3, 2009/08/04 21:13:41, 31, 3, 180, process.response.time.average, レスポンスタイムの平均値, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                 "27, 2, 2009/08/04 21:13:29, 31, 1, 50, process.response.time.average, レスポンスタイムの平均値, jp.co.acroquest.mc.event.io.ListIOStream#print" 
             }
        );
    }

    public void testNamedSumSelect1()
    {
        commonTestSelectSumValueByTermAndMeasurementTypeWithName(
             "2009/08/04 21:13:21",
             "2009/08/04 21:13:50",
             "process.response.total.count",
             new String[]
             {
                 "62, 3, 2009/08/04 21:13:41, 32, 9, 15, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                 "18, 1, 2009/08/04 21:13:23, 32, 10, 2, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.nameconv.NamingConverter#convert", 
                 "70, 4, 2009/08/04 21:13:47, 32, 7, 18, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                 "10, 1, 2009/08/04 21:13:23, 32, 8, 14, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                 "66, 4, 2009/08/04 21:13:47, 32, 6, 20, process.response.total.count, レスポンス回数, jp.co.acroquest.mc.event.io.ListIOStream#print" 
             }
        );
    }

    public void testNamedSumSelect2()
    {
        commonTestSelectSumValueByTermAndMeasurementTypeWithName(
             "2009/08/04 21:13:24",
             "2009/08/04 21:13:45",
             "process.response.total.count",
             new String[]
             {
                 "62, 3, 2009/08/04 21:13:41, 32, 9, 12, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                 "44, 2, 2009/08/04 21:13:29, 32, 10, 1, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.nameconv.NamingConverter#convert", 
                 "32, 2, 2009/08/04 21:13:29, 32, 7, 12, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                 "58, 3, 2009/08/04 21:13:41, 32, 8, 12, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                 "28, 2, 2009/08/04 21:13:29, 32, 6, 9, process.response.total.count, レスポンス回数, jp.co.acroquest.mc.event.io.ListIOStream#print", 
             }
        );
    }

    public void testNamedSumSelect3()
    {
        commonTestSelectSumValueByTermAndMeasurementTypeWithName(
             "2009/08/04 21:13:41",
             "2009/08/04 21:13:47",
             "process.response.total.count",
             new String[]
             {
                 "62, 3, 2009/08/04 21:13:41, 32, 9, 2, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                 "70, 4, 2009/08/04 21:13:47, 32, 7, 9, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                 "58, 3, 2009/08/04 21:13:41, 32, 8, 4, process.response.total.count, レスポンス回数, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                 "66, 4, 2009/08/04 21:13:47, 32, 6, 4, process.response.total.count, レスポンス回数, jp.co.acroquest.mc.event.io.ListIOStream#print", 
             }
        );
    }

    public void testNamedSumSelect4()
    {
        commonTestSelectSumValueByTermAndMeasurementTypeWithName(
             "2009/08/04 21:13:32",
             "2009/08/04 21:13:38",
             "process.response.total.count",
             new String[0]
        );
    }

    public void testNamedSumSelect5()
    {
        commonTestSelectSumValueByTermAndMeasurementTypeWithName(
             "2009/08/04 21:13:21",
             "2009/08/04 21:13:50",
             "process.response.time.max",
             new String[]
             {
                 "41, 2, 2009/08/04 21:13:29, 38, 14, 1810, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.gms.journal.JournalManager#onEvent", 
                 "19, 1, 2009/08/04 21:13:23, 38, 15, 1400, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.gms.nameconv.NamingConverter#convert", 
                 "55, 3, 2009/08/04 21:13:41, 38, 12, 6310, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.gms.ncligw.buffer.DataBuffer#publish", 
                 "59, 3, 2009/08/04 21:13:41, 38, 13, 2930, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.gms.snmpgw.dataaccessor.GetRequestProcessor#process", 
                 "29, 2, 2009/08/04 21:13:29, 38, 11, 1160, process.response.time.max, レスポンスタイムの最大値, jp.co.acroquest.mc.event.io.ListIOStream#print", 
             }
        );
    }

    
    private void commonTestSelectByTermAndMeasurementTypeWithName(
            String startTime, String endTime, String valueType, String[] expectDataList)
    {
        // 条件
        // --> DB登録済みデータ
        initDataBase();

        List<MeasurementValueDto> selectResult = null;

        // 実施
        try
        {
            selectResult = 
                MeasurementValueDao.selectByTermAndMeasurementTypeWithName(
                    DB_NAME, 
                    convertString2TimeStamp(startTime),
                    convertString2TimeStamp(endTime), 
                    valueType);
        }
        catch (Exception ex1)
        {
            fail(ex1.getMessage());
        }
        
        // 検証
        List<Object> expectData
            = DataAccessorTestUtil.createMeasurementValueDtoEntities(expectDataList);
            
        List<Object> actualData = new ArrayList<Object>();
        
        for(MeasurementValueDto data : selectResult)
        {
            actualData.add(data);
        }
        
        DataAccessorTestUtil.assertMeasurementValueDto(expectData, actualData);
    }
    
    private void commonTestSelectMaxValueByTermAndMeasurementTypeWithName(
            String startTime, String endTime, String valueType, String[] expectDataList)
    {
        // 条件
        // --> DB登録済みデータ
        initDataBase();

        List<MeasurementValueDto> selectResult = null;

        // 実施
        try
        {
            selectResult = 
                MeasurementValueDao.selectMaxValueByTermAndMeasurementTypeWithName(
                    DB_NAME,
                    convertString2TimeStamp(startTime),
                    convertString2TimeStamp(endTime),
                    valueType);
        }
        catch (Exception ex1)
        {
            fail(ex1.getMessage());
        }
        
        // 検証
        List<Object> expectData
            = DataAccessorTestUtil.createMeasurementValueDtoEntities(expectDataList);
            
        List<Object> actualData = new ArrayList<Object>();
        
        for(MeasurementValueDto data : selectResult)
        {
            actualData.add(data);
        }
        
        DataAccessorTestUtil.assertMeasurementValueDto(expectData, actualData);
    }

    private void commonTestSelectMinValueByTermAndMeasurementTypeWithName(
            String startTime, String endTime, String valueType, String[] expectDataList)
    {
        // 条件
        // --> DB登録済みデータ
        initDataBase();

        List<MeasurementValueDto> selectResult = null;

        // 実施
        try
        {
            selectResult = 
                MeasurementValueDao.selectMinValueByTermAndMeasurementTypeWithName(
                    DB_NAME,
                    convertString2TimeStamp(startTime),
                    convertString2TimeStamp(endTime),
                    valueType);
        }
        catch (Exception ex1)
        {
            fail(ex1.getMessage());
        }
        
        // 検証
        List<Object> expectData
            = DataAccessorTestUtil.createMeasurementValueDtoEntities(expectDataList);
            
        List<Object> actualData = new ArrayList<Object>();
        
        for(MeasurementValueDto data : selectResult)
        {
            actualData.add(data);
        }
        
        DataAccessorTestUtil.assertMeasurementValueDto(expectData, actualData);
    }
    
    private void commonTestSelectSumValueByTermAndMeasurementTypeWithName(
            String startTime, String endTime, String valueType, String[] expectDataList)
    {
        // 条件
        // --> DB登録済みデータ
        initDataBase();

        List<MeasurementValueDto> selectResult = null;

        // 実施
        try
        {
            selectResult = 
                MeasurementValueDao.selectSumValueByTermAndMeasurementTypeWithName(
                    DB_NAME,
                    convertString2TimeStamp(startTime),
                    convertString2TimeStamp(endTime),
                    valueType);
        }
        catch (Exception ex1)
        {
            fail(ex1.getMessage());
        }
        
        // 検証
        List<Object> expectData
            = DataAccessorTestUtil.createMeasurementValueDtoEntities(expectDataList);
            
        List<Object> actualData = new ArrayList<Object>();
        
        for(MeasurementValueDto data : selectResult)
        {
            actualData.add(data);
        }
        
        DataAccessorTestUtil.assertMeasurementValueDto(expectData, actualData);
    }

}
