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

public class MeasurementValueSearchTest extends AbstractDaoTest
{
    private static final String[] MEASUREMENT_VALUE_DATA =
    {
        "1, 1, 2009/05/29 12:21:33, 1, 1, 350, time, åªç›éûçè, Test1" ,
        "2, 1, 2009/05/29 12:22:33, 1, 1, 420, time, åªç›éûçè, Test1" ,
        "3, 1, 2009/05/29 12:22:45, 2, 1, 470, time, åªç›éûçè, Test1" ,
        "4, 2, 2009/05/29 12:24:12, 1, 1, 500, time, åªç›éûçè, Test1" ,
        "5, 2, 2009/05/29 12:24:31, 1, 1, 300, time, åªç›éûçè, Test1" ,
        "6, 3, 2009/05/29 12:27:31, 2, 1, 521, time, åªç›éûçè, Test1" ,
        "7, 3, 2009/05/29 12:31:31, 1, 1, 200, time, åªç›éûçè, Test1"
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
    
    public void testValueInsert1()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        initDatabase();

        List<Object> actualList = new ArrayList<Object>();
        
        // é¿é{
        try
        {
            actualList.addAll(MeasurementValueDao.selectAll(DB_NAME));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        List<Object> expectedData 
            = DataAccessorTestUtil.createMeasurementValueEntities(MEASUREMENT_VALUE_DATA);
        
        DataAccessorTestUtil.assertMeasurementValue(expectedData, actualList);
    }

    public void testValueInsert2()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^

        List<Object> actualList = new ArrayList<Object>();
        
        // é¿é{
        try
        {
            actualList.addAll(MeasurementValueDao.selectAll(DB_NAME));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        assertEquals(0, actualList.size());
    }
    
    public void testSelectByTerm1()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        initDatabase();

        List<Object> actualList = new ArrayList<Object>();
        
        // é¿é{
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp searchStart = new Timestamp(format.parse("2009/05/29 12:22:33").getTime());
            Timestamp searchEnd   = new Timestamp(format.parse("2009/05/29 12:24:31").getTime());
            actualList.addAll(MeasurementValueDao.selectByTerm(DB_NAME, searchStart, searchEnd));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        catch (ParseException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        List<Object> expectedData 
            = DataAccessorTestUtil.createMeasurementValueEntities(
                  new String[]{MEASUREMENT_VALUE_DATA[1],
                               MEASUREMENT_VALUE_DATA[2],
                               MEASUREMENT_VALUE_DATA[3],
                               MEASUREMENT_VALUE_DATA[4]});
        
        DataAccessorTestUtil.assertMeasurementValue(expectedData, actualList);
    }

    public void testSelectByTerm2()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        initDatabase();

        List<Object> actualList = new ArrayList<Object>();
        
        // é¿é{
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp searchStart = new Timestamp(format.parse("2009/05/29 12:35:11").getTime());
            Timestamp searchEnd   = new Timestamp(format.parse("2009/05/29 12:39:24").getTime());
            actualList.addAll(MeasurementValueDao.selectByTerm(DB_NAME, searchStart, searchEnd));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        catch (ParseException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        assertEquals(0, actualList.size());
    }

    public void testSelectByTerm3()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        initDatabase();

        List<Object> actualList = new ArrayList<Object>();
        
        // é¿é{
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp searchStart = new Timestamp(format.parse("2009/05/29 12:13:43").getTime());
            Timestamp searchEnd   = new Timestamp(format.parse("2009/05/29 12:20:14").getTime());
            actualList.addAll(MeasurementValueDao.selectByTerm(DB_NAME, searchStart, searchEnd));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        catch (ParseException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        assertEquals(0, actualList.size());
    }

    public void testSelectByTerm4()
    {
        // èåè

        List<Object> actualList = new ArrayList<Object>();
        
        // é¿é{
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp searchStart = new Timestamp(format.parse("2009/05/29 12:22:33").getTime());
            Timestamp searchEnd   = new Timestamp(format.parse("2009/05/29 12:24:31").getTime());
            actualList.addAll(MeasurementValueDao.selectByTerm(DB_NAME, searchStart, searchEnd));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        catch (ParseException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        assertEquals(0, actualList.size());
    }

    public void testSelectByTermAndType1()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        initDatabase();

        List<Object> actualList = new ArrayList<Object>();
        
        // é¿é{
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp searchStart = new Timestamp(format.parse("2009/05/29 12:22:33").getTime());
            Timestamp searchEnd   = new Timestamp(format.parse("2009/05/29 12:24:31").getTime());
            actualList.addAll(
                MeasurementValueDao.selectByTermAndMeasurementType(
                    DB_NAME, searchStart, searchEnd, 1));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        catch (ParseException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        List<Object> expectedData 
            = DataAccessorTestUtil.createMeasurementValueDtoEntities(
              new String[]{MEASUREMENT_VALUE_DATA[1],
                           MEASUREMENT_VALUE_DATA[3],
                           MEASUREMENT_VALUE_DATA[4]});
    
        DataAccessorTestUtil.assertMeasurementValueDto(expectedData, actualList);
    }

    public void testSelectByTermAndType2()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        initDatabase();

        List<Object> actualList = new ArrayList<Object>();
        
        // é¿é{
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp searchStart = new Timestamp(format.parse("2009/05/29 12:22:33").getTime());
            Timestamp searchEnd   = new Timestamp(format.parse("2009/05/29 12:24:31").getTime());
            actualList.addAll(
                MeasurementValueDao.selectByTermAndMeasurementType(
                    DB_NAME, searchStart, searchEnd, 3));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        catch (ParseException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        assertEquals(0, actualList.size());
    }

    
    public void testSelectByTermAndType3()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        initDatabase();

        List<Object> actualList = new ArrayList<Object>();
        
        // é¿é{
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp searchStart = new Timestamp(format.parse("2009/05/29 12:35:11").getTime());
            Timestamp searchEnd   = new Timestamp(format.parse("2009/05/29 12:39:24").getTime());
            actualList.addAll(
                MeasurementValueDao.selectByTermAndMeasurementType(
                    DB_NAME, searchStart, searchEnd, 1));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        catch (ParseException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        assertEquals(0, actualList.size());
    }
    
    public void testSelectByTermAndType4()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        initDatabase();

        List<Object> actualList = new ArrayList<Object>();
        
        // é¿é{
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp searchStart = new Timestamp(format.parse("2009/05/29 12:13:43").getTime());
            Timestamp searchEnd   = new Timestamp(format.parse("2009/05/29 12:20:14").getTime());
            actualList.addAll(
                MeasurementValueDao.selectByTermAndMeasurementType(
                    DB_NAME, searchStart, searchEnd, 1));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        catch (ParseException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        assertEquals(0, actualList.size());
    }

    public void testSelectByTermAndType5()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        initDatabase();

        List<Object> actualList = new ArrayList<Object>();
        
        // é¿é{
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp searchStart = new Timestamp(format.parse("2009/05/29 12:13:43").getTime());
            Timestamp searchEnd   = new Timestamp(format.parse("2009/05/29 12:20:14").getTime());
            actualList.addAll(
                MeasurementValueDao.selectByTermAndMeasurementType(
                    DB_NAME, searchStart, searchEnd, 4));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        catch (ParseException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        assertEquals(0, actualList.size());
    }
    
    public void testSelectByTermAndType6()
    {
        // èåè

        List<Object> actualList = new ArrayList<Object>();
        
        // é¿é{
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp searchStart = new Timestamp(format.parse("2009/05/29 12:22:33").getTime());
            Timestamp searchEnd   = new Timestamp(format.parse("2009/05/29 12:24:31").getTime());
            actualList.addAll(
                MeasurementValueDao.selectByTermAndMeasurementType(
                    DB_NAME, searchStart, searchEnd, 1));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        catch (ParseException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
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
