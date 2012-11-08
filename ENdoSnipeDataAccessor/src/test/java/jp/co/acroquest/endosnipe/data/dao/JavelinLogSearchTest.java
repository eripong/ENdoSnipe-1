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

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.data.entity.JavelinLog;
import jp.co.acroquest.endosnipe.test.DataAccessorTestUtil;

public class JavelinLogSearchTest extends AbstractDaoTest
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
    
    private static final String[] JAVELIN_LOG_SAMETIME_DATA = {
        "1, 0, 0, test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, 2009/05/29 12:21:33, 2009/05/29 12:21:33, session description, 1, calleeMethod, public, CalleeClass, int, 2, callerMethod, protected, CallerClass, 3, 30, 2500, modifier, thread_name, ThreadClass, 5"           ,
        "2, 1, 0, test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log2.jvn, javelin_log2.jvn, 2009/05/29 12:21:33, 2009/05/29 12:21:33, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4" ,
        "3, 2, 0, test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log3.jvn, javelin_log3.jvn, 2009/05/29 12:21:33, 2009/05/29 12:21:33, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4" ,
    };
    

    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    public void testSelectAll1()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        DataAccessorTestUtil.initializeJavelinLogTable(JAVELIN_LOG_DATA);
        
        // é¿é{
        List<Object> actualInsert = new ArrayList<Object>();
        try
        {
            actualInsert.addAll(JavelinLogDao.selectAll(DB_NAME));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        List<Object> expectedList
            = DataAccessorTestUtil.createJavelinEntities(JAVELIN_LOG_DATA);
        
        DataAccessorTestUtil.assertJavelinLog(expectedList, actualInsert);
    }
    
    public void testSelectAll2()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^ñ≥Çµ
        
        // é¿é{
        List<Object> actualInsert = new ArrayList<Object>();
        try
        {
            actualInsert.addAll(JavelinLogDao.selectAll(DB_NAME));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        assertEquals(0, actualInsert.size());
    }
    
    public void testSelectByLogId1()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        DataAccessorTestUtil.initializeJavelinLogTable(JAVELIN_LOG_DATA);
        
        // é¿é{
        JavelinLog actualLog = null;
        try
        {
            actualLog = JavelinLogDao.selectByLogId(DB_NAME, 3);
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        List<Object> actualList = new ArrayList<Object>();
        actualList.add(actualLog);
        
        // åüèÿ
        List<Object> expectedList
            = DataAccessorTestUtil.createJavelinEntities(new String[]{JAVELIN_LOG_DATA[2]});
        
        DataAccessorTestUtil.assertJavelinLog(expectedList, actualList);
    }

    public void testSelectByLogId2()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        DataAccessorTestUtil.initializeJavelinLogTable(JAVELIN_LOG_DATA);
        
        // é¿é{
        JavelinLog actualLog = null;
        try
        {
            actualLog = JavelinLogDao.selectByLogId(DB_NAME, 100000);
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        assertNull(actualLog);
    }
    
    public void testSelectByLogFileName1()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        DataAccessorTestUtil.initializeJavelinLogTable(JAVELIN_LOG_DATA);
        
        // é¿é{
        JavelinLog actualLog = null;
        try
        {
            actualLog = JavelinLogDao.selectByLogFileName(DB_NAME, "javelin_log5.jvn");
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        List<Object> actualList = new ArrayList<Object>();
        actualList.add(actualLog);
        
        // åüèÿ
        List<Object> expectedList
            = DataAccessorTestUtil.createJavelinEntities(new String[]{JAVELIN_LOG_DATA[4]});
        
        DataAccessorTestUtil.assertJavelinLog(expectedList, actualList);
    }
    
    public void testSelectByLogFileName2()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        DataAccessorTestUtil.initializeJavelinLogTable(JAVELIN_LOG_DATA);
        
        // é¿é{
        JavelinLog actualLog = null;
        try
        {
            actualLog = JavelinLogDao.selectByLogFileName(DB_NAME, "javelin_log120.jvn");
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        assertNull(actualLog);
    }
    
    public void testSelectByTerm1()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        DataAccessorTestUtil.initializeJavelinLogTable(JAVELIN_LOG_DATA);
        
        // é¿é{
        List<Object> actualLog = null;
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp searchStart = new Timestamp(format.parse("2009/05/29 12:24:37").getTime());
            Timestamp searchEnd   = new Timestamp(format.parse("2009/05/29 13:14:35").getTime());
            
            actualLog = new ArrayList<Object>();
            actualLog.addAll(JavelinLogDao.selectByTerm(DB_NAME, searchStart, searchEnd));
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
        List<Object> expectedList
            = DataAccessorTestUtil.createJavelinEntities(
                new String[]{JAVELIN_LOG_DATA[1], JAVELIN_LOG_DATA[2], JAVELIN_LOG_DATA[3], JAVELIN_LOG_DATA[4], JAVELIN_LOG_DATA[5]});
        
        DataAccessorTestUtil.assertJavelinLog(expectedList, actualLog);
    }
    
    public void testSelectByTerm2()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        DataAccessorTestUtil.initializeJavelinLogTable(JAVELIN_LOG_DATA);
        
        // é¿é{
        List<Object> actualLog = null;
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Timestamp searchStart = new Timestamp(format.parse("2010/05/29 12:24:37").getTime());
            Timestamp searchEnd   = new Timestamp(format.parse("2010/05/29 13:14:35").getTime());
            
            actualLog = new ArrayList<Object>();
            actualLog.addAll(JavelinLogDao.selectByTerm(DB_NAME, searchStart, searchEnd));
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
        assertEquals(0, actualLog.size());
    }
    
    public void testGetLogTerm1()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        DataAccessorTestUtil.initializeJavelinLogTable(JAVELIN_LOG_DATA);
        
        // é¿é{
        List<Object> actualLog = null;
        Timestamp[] term = null;
        try
        {
            term = JavelinLogDao.getLogTerm(DB_NAME);
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        // åüèÿ
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Timestamp expectSearchStart = null;
        Timestamp expectSearchEnd   = null;
        try
        {
            expectSearchStart = new Timestamp(format.parse("2009/05/29 12:21:33").getTime());
            expectSearchEnd = new Timestamp(format.parse("2009/05/29 15:02:01").getTime());
        }
        catch (ParseException ex)
        {
            fail(ex.getMessage());
        }

        assertEquals(expectSearchStart, term[0]);
        assertEquals(expectSearchEnd, term[1]);
    }
    
    public void testGetLogTerm2()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        DataAccessorTestUtil.initializeJavelinLogTable(JAVELIN_LOG_SAMETIME_DATA);
        
        // é¿é{
        List<Object> actualLog = null;
        Timestamp[] term = null;
        try
        {
            term = JavelinLogDao.getLogTerm(DB_NAME);
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        // åüèÿ
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Timestamp expectSearchStart = null;
        Timestamp expectSearchEnd   = null;
        try
        {
            expectSearchStart = new Timestamp(format.parse("2009/05/29 12:21:33").getTime());
            expectSearchEnd = new Timestamp(format.parse("2009/05/29 12:21:33").getTime());
        }
        catch (ParseException ex)
        {
            fail(ex.getMessage());
        }

        assertEquals(expectSearchStart, term[0]);
        assertEquals(expectSearchEnd, term[1]);
    }
    
    public void testGetLogTerm3()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        
        // é¿é{
        List<Object> actualLog = null;
        Timestamp[] term = null;
        try
        {
            term = JavelinLogDao.getLogTerm(DB_NAME);
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        // åüèÿ
        
        assertNull(term[0]);
        assertNull(term[1]);
    }
    
    public void testSelectJavelinLogByLogId1()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        DataAccessorTestUtil.initializeJavelinLogTable(JAVELIN_LOG_DATA);
        
        // é¿é{
        List<Object> actualLog = null;
        InputStream actualStream = null;
        try
        {
            actualStream = JavelinLogDao.selectJavelinLogByLogId(DB_NAME, 4);
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        catch (IOException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        List<Object> expectedList
            = DataAccessorTestUtil.createJavelinEntities(new String[]{JAVELIN_LOG_DATA[3]});
        
        
        // åüèÿ
        InputStream expectedStream = ((JavelinLog)expectedList.get(0)).javelinLog;
        
        try
        {
            while(true)
            {
                int actualByte = actualStream.read();
                int expectedByte = expectedStream.read();
                
                if(actualByte == -1 && expectedByte == -1)
                {
                    return;
                }
                
                assertEquals(expectedByte, actualByte);
            }
        }
        catch (IOException ex)
        {
            fail(ex.getMessage());
        }
        
    }
    
    public void testSelectJavelinLogByLogId2()
    {
        // èåè
        // --> DBìoò^çœÇ›ÉfÅ[É^
        DataAccessorTestUtil.initializeJavelinLogTable(JAVELIN_LOG_DATA);
        
        // é¿é{
        List<Object> actualLog = null;
        InputStream actualStream = null;
        try
        {
            actualStream = JavelinLogDao.selectJavelinLogByLogId(DB_NAME, 100);
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        catch (IOException ex)
        {
            fail(ex.getMessage());
        }
        
        // åüèÿ
        assertNull(actualStream);
   }

}
