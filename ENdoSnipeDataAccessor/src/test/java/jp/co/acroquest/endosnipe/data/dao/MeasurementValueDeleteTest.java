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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.test.DataAccessorTestUtil;

public class MeasurementValueDeleteTest extends AbstractDaoTest
{
    private static final String[] MEASUREMENT_VALUE_DATA =
    {
        "1, 1, 2009/05/29 12:21:33, 1, 1, 350" ,
        "2, 1, 2009/05/29 12:22:33, 1, 1, 420" ,
        "3, 1, 2009/05/29 12:22:45, 2, 1, 470" ,
        "4, 2, 2009/05/29 12:24:12, 1, 1, 500" ,
        "5, 2, 2009/05/29 12:24:31, 1, 1, 300" ,
        "6, 3, 2009/05/29 12:27:31, 2, 1, 521" ,
        "7, 3, 2009/05/29 12:31:31, 1, 1, 200"
    };

    private static final String[] JAVELIN_MEASUREMENT_ITEM_DATA = 
    {
        "1, 1, Test1, 2010/10/9 11:57:01",
        "2, 1, Test2, 2010/10/9 11:57:01",
    };

    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    public void testDeleteOldRecord1()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        initDatabase();
        
        // é¿é{
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp deleteLimit = new Timestamp(format.parse("2009/05/29 12:24:12").getTime());
            MeasurementValueDao.deleteOldRecordByTime(DB_NAME, deleteLimit);
        }
        catch (ParseException ex1)
        {
            fail(ex1.getMessage());
        }
        catch (SQLException ex1)
        {
            fail(ex1.getMessage());
        }
        
        // åüèÿ
        List<Object> actualList = new ArrayList<Object>();
        try
        {
            actualList.addAll(MeasurementValueDao.selectAll(DB_NAME));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        List<Object> expectedList
            = DataAccessorTestUtil.createMeasurementValueEntities(
                new String[]{MEASUREMENT_VALUE_DATA[4],
                             MEASUREMENT_VALUE_DATA[5],
                             MEASUREMENT_VALUE_DATA[6]});

        DataAccessorTestUtil.assertMeasurementValue(expectedList, actualList);
    }
    
    public void testDeleteOldRecord2()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        initDatabase();
        
        // é¿é{
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp deleteLimit = new Timestamp(format.parse("2009/05/29 12:20:33").getTime());
            MeasurementValueDao.deleteOldRecordByTime(DB_NAME, deleteLimit);
        }
        catch (ParseException ex1)
        {
            fail(ex1.getMessage());
        }
        catch (SQLException ex1)
        {
            fail(ex1.getMessage());
        }
        
        // åüèÿ
        List<Object> actualList = new ArrayList<Object>();
        try
        {
            actualList.addAll(MeasurementValueDao.selectAll(DB_NAME));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        List<Object> expectedList
            = DataAccessorTestUtil.createMeasurementValueEntities(MEASUREMENT_VALUE_DATA);

        DataAccessorTestUtil.assertMeasurementValue(expectedList, actualList);
    }

    public void testDeleteOldRecord3()
    {
        // èåè
        
        // é¿é{
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp deleteLimit = new Timestamp(format.parse("2009/05/29 12:24:12").getTime());
            MeasurementValueDao.deleteOldRecordByTime(DB_NAME, deleteLimit);
        }
        catch (ParseException ex1)
        {
            fail(ex1.getMessage());
        }
        catch (SQLException ex1)
        {
            fail(ex1.getMessage());
        }
        
        // åüèÿ
        List<Object> actualList = new ArrayList<Object>();
        try
        {
            actualList.addAll(MeasurementValueDao.selectAll(DB_NAME));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        assertEquals(0, actualList.size());
    }
    
    public void testDeleteAll1()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        initDatabase();
        
        // é¿é{
        try
        {
            MeasurementValueDao.deleteAll(DB_NAME);
        }
        catch (SQLException ex1)
        {
            fail(ex1.getMessage());
        }
        
        // åüèÿ
        List<Object> actualList = new ArrayList<Object>();
        try
        {
            actualList.addAll(MeasurementValueDao.selectAll(DB_NAME));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        assertEquals(0, actualList.size());
    }
    
    public void testDeleteAll2()
    {
        // èåè
        
        // é¿é{
        try
        {
            MeasurementValueDao.deleteAll(DB_NAME);
        }
        catch (SQLException ex1)
        {
            fail(ex1.getMessage());
        }
        
        // åüèÿ
        List<Object> actualList = new ArrayList<Object>();
        try
        {
            actualList.addAll(MeasurementValueDao.selectAll(DB_NAME));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        assertEquals(0, actualList.size());
    }

    
    private void initDatabase()
    {
        DataAccessorTestUtil.initializeJavelinMeasurementItemTable(
            JAVELIN_MEASUREMENT_ITEM_DATA);
        DataAccessorTestUtil.initializeMeasurementValueTable(
            MEASUREMENT_VALUE_DATA);
    }

}
