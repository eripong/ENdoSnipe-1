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
import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.data.entity.JavelinLog;
import jp.co.acroquest.endosnipe.test.DataAccessorTestUtil;

/**
 * Javelinログ蓄積機能のテストクラス
 * 
 * @author M.Yoshida
 *
 */
public class JavelinLogAccumulationTest extends AbstractDaoTest
{
    private static final String[] JAVELIN_DATA =
        {
        "0, 0, 0, src/test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, 2009/05/29 12:21:33, 2009/05/29 12:24:12, session description, 1, calleeMethod, public, CalleeClass, int, 2, callerMethod, protected, CallerClass, 3, 30, 2500, modifier, thread_name, ThreadClass, 5"     ,
        "1, 1, 0, src/test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, 2009/05/29 12:24:37, 2009/05/29 12:24:41, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4" ,
        "2, 2, 0, , javelin_log.jvn, 2009/05/29 12:24:37, 2009/05/29 12:24:41, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4"                                                            ,
        "3, 3, 0, src/test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, , 2009/05/29 12:24:37, 2009/05/29 12:24:41, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4"                ,
        "4, 4, 0, src/test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, , 2009/05/29 12:24:41, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4"                    ,
        "5, 5, 0, src/test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, 2009/05/29 12:24:37, , session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4"                    ,
        "6, 6, 0, src/test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, 2009/05/29 12:24:37, 2009/05/29 12:24:41, , 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4"                    ,
        "7, 7, 0, src/test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, 2009/05/29 12:24:37, 2009/05/29 12:24:41, session description, 2, , private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4"              ,
        "8, 8, 0, src/test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, 2009/05/29 12:24:37, 2009/05/29 12:24:41, session description, 2, calleeMethod2, , CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4"        ,
        "9, 9, 0, src/test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, 2009/05/29 12:24:37, 2009/05/29 12:24:41, session description, 2, calleeMethod2, private, , String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4"             ,
        "10, 10, 0, src/test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, 2009/05/29 12:24:37, 2009/05/29 12:24:41, session description, 2, calleeMethod2, private, CalleeClass2, , 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4"     ,
        "11, 11, 0, src/test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, 2009/05/29 12:24:37, 2009/05/29 12:24:41, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, , public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4"            ,
        "12, 12, 0, src/test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, 2009/05/29 12:24:37, 2009/05/29 12:24:41, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, , CallerClass2, 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4"     ,
        "13, 13, 0, src/test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, 2009/05/29 12:24:37, 2009/05/29 12:24:41, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, , 3, 20, 3000, modifier2, thread_name2, ThreadClass2, 4"           ,
        "14, 14, 0, src/test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, 2009/05/29 12:24:37, 2009/05/29 12:24:41, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, , thread_name2, ThreadClass2, 4"        ,
        "15, 15, 0, src/test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, 2009/05/29 12:24:37, 2009/05/29 12:24:41, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, , ThreadClass2, 4"           ,
        "16, 16, 0, src/test/resources/jp/co/acroquest/endosnipe/data/dao/javelin_log.jvn, javelin_log.jvn, 2009/05/29 12:24:37, 2009/05/29 12:24:41, session description, 2, calleeMethod2, private, CalleeClass2, String, 2, callerMethod2, public, CallerClass2, 3, 20, 3000, modifier2, thread_name2, , 4"
        };
    
    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    public void testInsert1()
    {
        // 条件
        // --> DB登録済みデータ
        // <-- データが無いとき -->
        
        // --> 追加対象データ
        List<Object> insertTarget
            = DataAccessorTestUtil.createJavelinEntities(
                new String[]{JAVELIN_DATA[0]});
        
        // 実施
        for(Object entity : insertTarget)
        {
            try
            {
                JavelinLogDao.insert(DB_NAME, (JavelinLog)entity);
            }
            catch (SQLException ex)
            {
                fail(ex.getMessage());
            }
        }
        
        // 検証
        List<Object> actualInsert = new ArrayList<Object>();
        try
        {
            actualInsert.addAll(JavelinLogDao.selectAll(DB_NAME));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        DataAccessorTestUtil.assertJavelinLog(insertTarget, actualInsert);
    }

    public void testInsert2()
    {
        // 条件
        // --> DB登録済みデータ
        DataAccessorTestUtil.initializeJavelinLogTable(
            new String[]{JAVELIN_DATA[0]});
        
        // --> 追加対象データ
        List<Object> insertTarget = null;
        
        insertTarget
            = DataAccessorTestUtil.createJavelinEntities(
                new String[]{JAVELIN_DATA[1]});
        
        // 実施
        for(Object entity : insertTarget)
        {
            try
            {
                JavelinLogDao.insert(DB_NAME, (JavelinLog)entity);
            }
            catch (SQLException ex)
            {
                fail(ex.getMessage());
            }
        }
        
        List<Object> expectedData = null;
        expectedData 
            = DataAccessorTestUtil.createJavelinEntities(
                new String[]{JAVELIN_DATA[0], JAVELIN_DATA[1]});
        
        
        // 検証
        List<Object> actualInsert = new ArrayList<Object>();
        try
        {
            actualInsert.addAll(JavelinLogDao.selectAll(DB_NAME));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        DataAccessorTestUtil.assertJavelinLog(expectedData, actualInsert);
    }

    public void testInsert3()
    {
        // 条件
        // --> DB登録済みデータ
        // <-- データが無いとき -->
        
        // --> 追加対象データ
        List<Object> insertTarget
            = DataAccessorTestUtil.createJavelinEntities(
                new String[]{JAVELIN_DATA[2]});
        
        // 実施
        for(Object entity : insertTarget)
        {
            try
            {
                JavelinLogDao.insert(DB_NAME, (JavelinLog)entity);
            }
            catch (SQLException ex)
            {
                fail(ex.getMessage());
            }
        }
        
        // 検証
        List<Object> actualInsert = new ArrayList<Object>();
        try
        {
            actualInsert.addAll(JavelinLogDao.selectAll(DB_NAME));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        assertEquals(0, actualInsert.size());
    }

    public void testInsert4()
    {
        abnormalInsertTest(JAVELIN_DATA[3]);
    }

    public void testInsert5()
    {
        abnormalInsertTest(JAVELIN_DATA[4]);
    }

    public void testInsert6()
    {
        abnormalInsertTest(JAVELIN_DATA[5]);
    }

    public void testInsert7()
    {
        abnormalInsertTest(JAVELIN_DATA[6]);
    }

    public void testInsert8()
    {
        abnormalInsertTest(JAVELIN_DATA[7]);
    }

    public void testInsert9()
    {
        abnormalInsertTest(JAVELIN_DATA[8]);
    }

    public void testInsert10()
    {
        abnormalInsertTest(JAVELIN_DATA[9]);
    }

    public void testInsert11()
    {
        abnormalInsertTest(JAVELIN_DATA[10]);
    }

    public void testInsert12()
    {
        abnormalInsertTest(JAVELIN_DATA[11]);
    }

    public void testInsert13()
    {
        abnormalInsertTest(JAVELIN_DATA[12]);
    }

    public void testInsert14()
    {
        abnormalInsertTest(JAVELIN_DATA[13]);
    }

    public void testInsert15()
    {
        abnormalInsertTest(JAVELIN_DATA[14]);
    }

    public void testInsert16()
    {
        abnormalInsertTest(JAVELIN_DATA[15]);
    }

    public void testInsert17()
    {
        abnormalInsertTest(JAVELIN_DATA[16]);
    }
    
    public void abnormalInsertTest(String insertData)
    {
        // 条件
        // --> DB登録済みデータ
        // <-- データが無いとき -->
        
        // --> 追加対象データ
        List<Object> insertTarget
            = DataAccessorTestUtil.createJavelinEntities(
                new String[]{insertData});
        
        // 実施
        for(Object entity : insertTarget)
        {
            try
            {
                JavelinLogDao.insert(DB_NAME, (JavelinLog)entity);
            }
            catch (SQLException ex)
            {
                fail(ex.getMessage());
            }
        }
        
        // 検証
        List<Object> actualInsert = new ArrayList<Object>();
        try
        {
            actualInsert.addAll(JavelinLogDao.selectAll(DB_NAME));
        }
        catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        
        DataAccessorTestUtil.assertJavelinLog(insertTarget, actualInsert);
    }
    

}
