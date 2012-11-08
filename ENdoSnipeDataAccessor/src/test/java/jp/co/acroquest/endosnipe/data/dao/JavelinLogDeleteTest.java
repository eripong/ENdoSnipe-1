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

public class JavelinLogDeleteTest extends AbstractDaoTest
{
    private static final String[] JAVELIN_LOG_DATA = {
        "1, 0, 0, test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, 2009/05/29 12:21:33, 2009/05/29 12:24:12, session description, 1, calleeMethod, public, CalleeClass, int, 2, callerMethod, protected, CallerClass, 3, 30, 2500, modifier, thread_name, ThreadClass, 5"           ,
        "2, 1, 0, test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log2.jvn, javelin_log2.jvn, 2009/05/29 12:24:37, 2009/05/29 12:24:41, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4" ,
        "3, 2, 0, test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log3.jvn, javelin_log3.jvn, 2009/05/29 12:25:11, 2009/05/29 12:25:20, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4" ,
        "4, 3, 0, test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log4.jvn, javelin_log4.jvn, 2009/05/29 12:25:41, 2009/05/29 12:26:02, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4" ,
        "5, 4, 0, test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log5.jvn, javelin_log5.jvn, 2009/05/29 12:29:14, 2009/05/29 12:30:11, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4" ,
        "6, 5, 0, test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log6.jvn, javelin_log6.jvn, 2009/05/29 13:14:21, 2009/05/29 13:14:34, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4" ,
        "7, 6, 0, test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log7.jvn, javelin_log7.jvn, 2009/05/29 14:54:11, 2009/05/29 14:54:21, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4" ,
        "8, 7, 0, test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log8.jvn, javelin_log8.jvn, 2009/05/29 15:01:19, 2009/05/29 15:02:01, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4"
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
        DataAccessorTestUtil.initializeJavelinLogTable(JAVELIN_LOG_DATA);
        
        // é¿é{
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp deleteLimit = new Timestamp(format.parse("2009/05/29 12:26:02").getTime());
            JavelinLogDao.deleteOldRecordByTime(DB_NAME, deleteLimit);
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
            actualList.addAll(JavelinLogDao.selectAll(DB_NAME));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        List<Object> expectedList
            = DataAccessorTestUtil.createJavelinEntities(
                 new String[]{JAVELIN_LOG_DATA[4],
                              JAVELIN_LOG_DATA[5],
                              JAVELIN_LOG_DATA[6],
                              JAVELIN_LOG_DATA[7]});
        
        DataAccessorTestUtil.assertJavelinLog(expectedList, actualList);
    }

    public void testDeleteOldRecord2()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        DataAccessorTestUtil.initializeJavelinLogTable(JAVELIN_LOG_DATA);
        
        // é¿é{
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp deleteLimit = new Timestamp(format.parse("2009/05/29 12:23:12").getTime());
            JavelinLogDao.deleteOldRecordByTime(DB_NAME, deleteLimit);
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
            actualList.addAll(JavelinLogDao.selectAll(DB_NAME));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        List<Object> expectedList
            = DataAccessorTestUtil.createJavelinEntities(JAVELIN_LOG_DATA);
        
        DataAccessorTestUtil.assertJavelinLog(expectedList, actualList);
    }

    public void testDeleteOldRecord3()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        
        // é¿é{
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp deleteLimit = new Timestamp(format.parse("2009/05/29 12:26:02").getTime());
            JavelinLogDao.deleteOldRecordByTime(DB_NAME, deleteLimit);
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
            actualList.addAll(JavelinLogDao.selectAll(DB_NAME));
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
        DataAccessorTestUtil.initializeJavelinLogTable(JAVELIN_LOG_DATA);
        
        // é¿é{
        try
        {
            JavelinLogDao.deleteAll(DB_NAME);
        }
        catch (SQLException ex1)
        {
            fail(ex1.getMessage());
        }
        
        // åüèÿ
        List<Object> actualList = new ArrayList<Object>();
        try
        {
            actualList.addAll(JavelinLogDao.selectAll(DB_NAME));
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
            JavelinLogDao.deleteAll(DB_NAME);
        }
        catch (SQLException ex1)
        {
            fail(ex1.getMessage());
        }
        
        // åüèÿ
        List<Object> actualList = new ArrayList<Object>();
        try
        {
            actualList.addAll(JavelinLogDao.selectAll(DB_NAME));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        assertEquals(0, actualList.size());
    }

}
