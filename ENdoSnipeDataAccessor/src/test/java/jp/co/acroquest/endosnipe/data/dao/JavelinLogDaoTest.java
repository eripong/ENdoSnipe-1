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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import jp.co.acroquest.endosnipe.data.entity.JavelinLog;

/**
 * {@link JavelinLogDao} クラスのテストケース。<br />
 *
 * @author sakamoto
 */
public class JavelinLogDaoTest extends AbstractDaoTest
{
    /** Javelinログのファイル名 */
    private static final String JVN_FILENAME = "JavelinLogFile.jvn";

    /** Javelinログデータ */
    private static final byte[] JAVELIN_DATA = "1234567890abcdefg".getBytes();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    /**
     * @test 空のテーブルに対して、 insert() を実行します。<br />
     * @throws SQLException SQL 実行時に例外が発生した場合
     */
    public void testInsert_one()
        throws SQLException
    {
        // 準備
        Timestamp time = new Timestamp(System.currentTimeMillis());
        JavelinLog javelinLog = createJavelinLog(0, time, time);

        // 実行
        JavelinLogDao.insert(DB_NAME, javelinLog);

        // 検証
        List<JavelinLog> actual = JavelinLogDao.selectByTerm(DB_NAME, time, time);
        assertEquals(1, actual.size());
    }

    /**
     * @test 存在しない Javelin ログを取得します。<br />
     * @throws Exception 例外
     */
    public void testSelectJavelinLogByLogId_notExist()
        throws Exception
    {
        // 準備
        Timestamp time = new Timestamp(System.currentTimeMillis());
        JavelinLog javelinLog = createJavelinLog(0, time, time);
        JavelinLogDao.insert(DB_NAME, javelinLog);

        // 実行
        InputStream actual = JavelinLogDao.selectJavelinLogByLogId(DB_NAME, -1);

        // 検証
        assertNull(actual);
    }

    /**
     * @target {@link JavelinLogDao#selectByLogFileName(String, String)}
     * 
     * @test DBから存在するファイル名でJavelinログを検索する。
     * @condition <li>DBに"JavelinLogFile.jvn"という名前のログをinsertする。</li>
     * <li>DBから"JavelinLogFile.jvn"という名前でファイルを検索する。
     * 
     * @result 検索結果がnull でないこと。
     * 
     * @throws Exception 例外が発生した場合
     */
    public void testSelectByLogFileName_existFile()
        throws Exception
    {
        // 準備
        Timestamp time = new Timestamp(System.currentTimeMillis());
        JavelinLog javelinLog = createJavelinLog(0, time, time);
        JavelinLogDao.insert(DB_NAME, javelinLog);

        // Javelinログファイル名
        String jvnFileName = JVN_FILENAME;

        // 実行
        JavelinLog jvnLog = JavelinLogDao.selectByLogFileName(DB_NAME, jvnFileName);

        // 検証
        assertNotNull(jvnLog);
    }

    /**
     * @target {@link JavelinLogDao#selectByLogFileName(String, String)}
     * 
     * @test DBから存在しないファイル名でJavelinログを検索する。
     * @condition <li>DBに"JavelinLogFile.jvn"という名前のログをinsertする。</li>
     * <li>DBから"NotExist.jvn"という名前でファイルを検索する。
     * 
     * @result 検索結果がnull であること。
     * 
     * @throws Exception 例外が発生した場合
     */
    public void testSelectByLogFileName_notExist()
        throws Exception
    {
        // 準備
        Timestamp time = new Timestamp(System.currentTimeMillis());
        JavelinLog javelinLog = createJavelinLog(0, time, time);
        JavelinLogDao.insert(DB_NAME, javelinLog);

        // Javelinログファイル名
        String jvnFileName = "NotExist.jvn";

        // 実行
        JavelinLog jvnLog = JavelinLogDao.selectByLogFileName(DB_NAME, jvnFileName);

        // 検証
        assertNull(jvnLog);
    }

    /**
     * 範囲内の Javelin ログを取得します。<br />
     *
     * Javelin ログの開始時刻と、指定開始時刻が一致し、
     * Javelin ログの終了時刻と、指定終了時刻が一致する試験です。
     *
     * @throws Exception 例外
     */
    public void testSelectByTerm_inJustRange()
        throws Exception
    {
        // 準備
        long time = System.currentTimeMillis();
        Timestamp timeStart1 = new Timestamp(time);
        Timestamp timeEnd1 = new Timestamp(time + 1000);
        Timestamp timeStart2 = new Timestamp(time + 2000);
        Timestamp timeEnd2 = new Timestamp(time + 3000);
        Timestamp timeStart3 = new Timestamp(time + 4000);
        Timestamp timeEnd3 = new Timestamp(time + 5000);
        Timestamp timeStart4 = new Timestamp(time + 6000);
        Timestamp timeEnd4 = new Timestamp(time + 7000);
        JavelinLog javelinLog = createJavelinLog(0, timeStart1, timeEnd1);
        JavelinLogDao.insert(DB_NAME, javelinLog);
        javelinLog = createJavelinLog(1, timeStart2, timeEnd2);
        JavelinLogDao.insert(DB_NAME, javelinLog);
        javelinLog = createJavelinLog(2, timeStart3, timeEnd3);
        JavelinLogDao.insert(DB_NAME, javelinLog);
        javelinLog = createJavelinLog(3, timeStart4, timeEnd4);
        JavelinLogDao.insert(DB_NAME, javelinLog);

        // 実行
        List<JavelinLog> actual = JavelinLogDao.selectByTerm(DB_NAME, timeStart3, timeEnd3);

        // 検証
        assertEquals(1, actual.size());
        assertJavelinLog(timeStart3, timeEnd3, actual.get(0));
    }

    /**
     * 範囲内の Javelin ログを取得します。<br />
     *
     * Javelin ログの開始時刻と、指定開始時刻が一致せず、
     * Javelin ログの終了時刻と、指定終了時刻が一致しない試験です。
     *
     * @throws Exception 例外
     */
    public void testSelectByTerm_inRange()
        throws Exception
    {
        // 準備
        long time = System.currentTimeMillis();
        Timestamp timeStart1 = new Timestamp(time);
        Timestamp timeEnd1 = new Timestamp(time + 1000);
        Timestamp timeStart2 = new Timestamp(time + 2000);
        Timestamp timeEnd2 = new Timestamp(time + 3000);
        Timestamp timeStart3 = new Timestamp(time + 4000);
        Timestamp timeEnd3 = new Timestamp(time + 5000);
        Timestamp timeStart4 = new Timestamp(time + 6000);
        Timestamp timeEnd4 = new Timestamp(time + 7000);
        JavelinLog javelinLog = createJavelinLog(0, timeStart1, timeEnd1);
        JavelinLogDao.insert(DB_NAME, javelinLog);
        javelinLog = createJavelinLog(1, timeStart2, timeEnd2);
        JavelinLogDao.insert(DB_NAME, javelinLog);
        javelinLog = createJavelinLog(2, timeStart3, timeEnd3);
        JavelinLogDao.insert(DB_NAME, javelinLog);
        javelinLog = createJavelinLog(3, timeStart4, timeEnd4);
        JavelinLogDao.insert(DB_NAME, javelinLog);

        // 実行
        List<JavelinLog> actual = JavelinLogDao.selectByTerm(DB_NAME, timeEnd1, timeStart4);

        // 検証
        assertEquals(2, actual.size());
        
        assertJavelinLog(timeStart3, timeEnd3, actual.get(0));
        assertJavelinLog(timeStart2, timeEnd2, actual.get(1));
    }

    /**
     * 範囲内の Javelin ログを取得します。<br />
     *
     * 範囲内に Javelin ログが存在しない試験です。
     *
     * @throws Exception 例外
     */
    public void testSelectByTerm_outOfRange()
        throws Exception
    {
        // 準備
        long time = System.currentTimeMillis();
        Timestamp timeStart1 = new Timestamp(time);
        Timestamp timeEnd1 = new Timestamp(time + 1000);
        Timestamp timeStart2 = new Timestamp(time + 2000);
        Timestamp timeEnd2 = new Timestamp(time + 3000);
        Timestamp timeStart3 = new Timestamp(time + 4000);
        Timestamp timeEnd3 = new Timestamp(time + 5000);
        Timestamp timeStart4 = new Timestamp(time + 6000);
        Timestamp timeEnd4 = new Timestamp(time + 7000);
        JavelinLog javelinLog = createJavelinLog(0, timeStart1, timeEnd1);
        JavelinLogDao.insert(DB_NAME, javelinLog);
        javelinLog = createJavelinLog(1, timeStart2, timeEnd2);
        JavelinLogDao.insert(DB_NAME, javelinLog);
        javelinLog = createJavelinLog(2, timeStart3, timeEnd3);
        JavelinLogDao.insert(DB_NAME, javelinLog);
        javelinLog = createJavelinLog(3, timeStart4, timeEnd4);
        JavelinLogDao.insert(DB_NAME, javelinLog);

        // 実行
        List<JavelinLog> actual = JavelinLogDao.selectByTerm(DB_NAME, timeEnd2, timeStart3);

        // 検証
        assertEquals(0, actual.size());
    }

    /**
     * レコードが格納されていないテーブルの Javelin ログの範囲を取得します。<br />
     *
     * @throws Exception 例外
     */
    public void testGetLogTerm_empty()
        throws Exception
    {
        // 実行
        Timestamp[] actual = JavelinLogDao.getLogTerm(DB_NAME);

        // 検証
        assertEquals(2, actual.length);
        assertNull(actual[0]);
        assertNull(actual[1]);
    }

    /**
     * 1 レコードが格納されているテーブルの Javelin ログの範囲を取得します。<br />
     *
     * @throws Exception 例外
     */
    public void testGetLogTerm_one()
        throws Exception
    {
        // 準備
        long time = System.currentTimeMillis();
        Timestamp timeStart1 = new Timestamp(time);
        Timestamp timeEnd1 = new Timestamp(time + 1000);
        JavelinLog javelinLog = createJavelinLog(0, timeStart1, timeEnd1);
        JavelinLogDao.insert(DB_NAME, javelinLog);

        // 実行
        Timestamp[] actual = JavelinLogDao.getLogTerm(DB_NAME);

        // 検証
        assertEquals(2, actual.length);
        assertEquals(timeStart1, actual[0]);
        assertEquals(timeEnd1, actual[1]);
    }

    /**
     * 2 レコードが格納されているテーブルの Javelin ログの範囲を取得します。<br />
     *
     * @throws Exception 例外
     */
    public void testGetLogTerm_two()
        throws Exception
    {
        // 準備
        long time = System.currentTimeMillis();
        Timestamp timeStart1 = new Timestamp(time);
        Timestamp timeEnd1 = new Timestamp(time + 1000);
        Timestamp timeStart2 = new Timestamp(time + 2000);
        Timestamp timeEnd2 = new Timestamp(time + 3000);
        JavelinLog javelinLog = createJavelinLog(0, timeStart1, timeEnd1);
        JavelinLogDao.insert(DB_NAME, javelinLog);
        javelinLog = createJavelinLog(1, timeStart2, timeEnd2);
        JavelinLogDao.insert(DB_NAME, javelinLog);

        // 実行
        Timestamp[] actual = JavelinLogDao.getLogTerm(DB_NAME);

        // 検証
        assertEquals(2, actual.length);
        assertEquals(timeStart1, actual[0]);
        assertEquals(timeEnd2, actual[1]);
    }

    /**
     * レコードが格納されていないテーブルのレコード数を取得します。<br />
     *
     * @throws Exception 例外
     */
    public void testCount_empty()
        throws Exception
    {
        // 実行
        int actual = JavelinLogDao.count(DB_NAME);

        // 検証
        assertEquals(0, actual);
    }

    /**
     * レコードが格納されているテーブルのレコード数を取得します。<br />
     *
     * @throws Exception 例外
     */
    public void testCount_one()
        throws Exception
    {
        // 準備
        long time = System.currentTimeMillis();
        Timestamp timeStart1 = new Timestamp(time);
        Timestamp timeEnd1 = new Timestamp(time + 1000);
        JavelinLog javelinLog = createJavelinLog(0, timeStart1, timeEnd1);
        JavelinLogDao.insert(DB_NAME, javelinLog);

        // 実行
        int actual = JavelinLogDao.count(DB_NAME);

        // 検証
        assertEquals(1, actual);
    }

    /**
     * Javelin ログオブジェクトを生成します。<br />
     *
     * @param logId ログ ID
     * @param start 開始時刻
     * @param end 終了時刻
     * @return Javelin ログオブジェクト
     */
    private JavelinLog createJavelinLog(final long logId, final Timestamp start, final Timestamp end)
    {
        JavelinLog javelinLog = new JavelinLog();
        javelinLog.logId = logId;
        javelinLog.sessionId = 0;
        javelinLog.sequenceId = 0;
        javelinLog.javelinLog = new ByteArrayInputStream(JAVELIN_DATA);
        javelinLog.logFileName = JVN_FILENAME;
        javelinLog.startTime = start;
        javelinLog.endTime = end;
        javelinLog.sessionDesc = "session description";
        javelinLog.calleeName = "calleeMethod";
        javelinLog.calleeSignature = "public";
        javelinLog.calleeClass = "CalleeClass";
        javelinLog.calleeFieldType = "int";
        javelinLog.calleeObjectId = 2;
        javelinLog.callerName = "callerMethod";
        javelinLog.callerSignature = "protected";
        javelinLog.callerClass = "CallerClass";
        javelinLog.callerObjectId = 3;
        javelinLog.elapsedTime = 123;
        javelinLog.modifier = "a";
        javelinLog.threadName = "thread_name";
        javelinLog.threadClass = "ThreadClass";
        javelinLog.threadObjectId = 5;
        return javelinLog;
    }

    /**
     * Javelin オブジェクトをチェックします。<br />
     *
     * ログ ID と Javelin ログはチェックしません。
     *
     * @param start 開始時刻
     * @param end 終了時刻
     * @param actual 取得した Javelin ログオブジェクト
     */
    private void assertJavelinLog(final Timestamp start, final Timestamp end,
            final JavelinLog actual)
    {
        assertEquals(0, actual.sequenceId);
        assertEquals(JVN_FILENAME, actual.logFileName);
        assertEquals(start, actual.startTime);
        assertEquals(end, actual.endTime);
        assertEquals("session description", actual.sessionDesc);
        assertEquals("calleeMethod", actual.calleeName);
        assertEquals("public", actual.calleeSignature);
        assertEquals("CalleeClass", actual.calleeClass);
        assertEquals("int", actual.calleeFieldType);
        assertEquals(2, actual.calleeObjectId);
        assertEquals("callerMethod", actual.callerName);
        assertEquals("protected", actual.callerSignature);
        assertEquals("CallerClass", actual.callerClass);
        assertEquals(3, actual.callerObjectId);
        assertEquals(123, actual.elapsedTime);
        assertEquals("a", actual.modifier);
        assertEquals("thread_name", actual.threadName);
        assertEquals("ThreadClass", actual.threadClass);
        assertEquals(5, actual.threadObjectId);
    }
}
