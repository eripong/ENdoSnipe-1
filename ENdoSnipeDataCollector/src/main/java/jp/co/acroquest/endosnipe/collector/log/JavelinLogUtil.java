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
package jp.co.acroquest.endosnipe.collector.log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.parser.JavelinConstants;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogColumnNum;
import jp.co.acroquest.endosnipe.common.util.CSVTokenizer;
import jp.co.acroquest.endosnipe.data.entity.JavelinLog;

/**
 * Javelinログを読み込み、JavelinLogインスタンスにするためのユーティリティです。<br />
 * 
 * @author eriguchi
 */
public class JavelinLogUtil
{
    /** "Call  "、"Return"などの種別文字列をキーに、Javelinログの1行目からJavelinLog用のデータを取得するためのリストを取得します。 */
    private static Map<String, List<Integer>> indexListMap__       =
                                                                     new HashMap<String, List<Integer>>();

    /** "Call  "、"Return"などの種別文字列をキーに、logTypeの値とするマップです。 */
    private static Map<String, Integer>       logTypeMap__         = new HashMap<String, Integer>();

    /** START_TIMEのインデックス。 */
    private static final int                  DB_START_TIME        = 0;

    /** END_TIMEのインデックス。 */
    // private static final int DB_END_TIME = 1;
    /** SESSION_DESCのインデックス。 */
    private static final int                  DB_SESSION_DESC      = 2;

    /** LOG_TYPEのインデックス。 */
    private static final int                  DB_LOG_TYPE          = 3;

    /** CALLEE_NAMEのインデックス。 */
    private static final int                  DB_CALLEE_NAME       = 4;

    /** CALLEE_SIGNATUREのインデックス。 */
    private static final int                  DB_CALLEE_SIGNATURE  = 5;

    /** CALLEE_CLASSのインデックス。 */
    private static final int                  DB_CALLEE_CLASS      = 6;

    /** CALLEE_FIELD_TYPEのインデックス。 */
    private static final int                  DB_CALLEE_FIELD_TYPE = 7;

    /** CALLEE_OBJECTIDのインデックス。 */
    private static final int                  DB_CALLEE_OBJECTID   = 8;

    /** CALLER_NAMEのインデックス。 */
    private static final int                  DB_CALLER_NAME       = 9;

    /** CALLER_SIGNATUREのインデックス。 */
    private static final int                  DB_CALLER_SIGNATURE  = 10;

    /** CALLER_CLASSのインデックス。 */
    private static final int                  DB_CALLER_CLASS      = 11;

    /** CALLER_OBJECTIDのインデックス。 */
    private static final int                  DB_CALLER_OBJECTID   = 12;

    /** DB_EVENT_LEVELのインデックス。 */
    private static final int                  DB_EVENT_LEVEL       = 13;

    /** ELAPSED_TIMEのインデックス。 */
    //private static final int                  DB_ELAPSED_TIME      = 14;
    /** MODIFIERのインデックス。 */
    private static final int                  DB_MODIFIER          = 15;

    /** THREAD_NAMEのインデックス。 */
    private static final int                  DB_THREAD_NAME       = 16;

    /** THREAD_CLASSのインデックス。 */
    private static final int                  DB_THREAD_CLASS      = 17;

    /** THREAD_OBJECTIDのインデックス。 */
    private static final int                  DB_THREAD_OBJECTID   = 18;

    /** durationを取得する際のキー。 */
    public static final String                DURATION_KEY         = "duration = ";

    /**
     * インスタンス化を避けるためのprivateコンストラクタです。<br />
     */
    private JavelinLogUtil()
    {
    }

    static
    {
        // JavleinログからJavelinLogへのカラムの添え字のマップを初期化する。
        indexListMap__.put(JavelinConstants.MSG_CALL, Arrays.asList(new Integer[]{ //
        JavelinLogColumnNum.CALL_TIME, // START_TIME
            -1, // END_TIME
            -1, // SESSION_DESC
            JavelinLogColumnNum.ID, // LOG_TYPE
            JavelinLogColumnNum.CALL_CALLEE_METHOD, // CALLEE_NAME
            -1, // CALLEE_SIGNATURE
            JavelinLogColumnNum.CALL_CALLEE_CLASS, // CALLEE_CLASS
            -1, // CALLEE_FIELD_TYPE
            JavelinLogColumnNum.CALL_CALLEE_OBJECTID, // CALLEE_OBJECTID
            JavelinLogColumnNum.CALL_CALLER_METHOD, // CALLER_NAME
            -1, // CALLER_SIGNATURE
            JavelinLogColumnNum.CALL_CALLER_CLASS, // CALLER_CLASS
            JavelinLogColumnNum.CALL_CALLER_OBJECTID, // CALLER_OBJECTID
            -1, // EVENT_LEVEL
            -1, // ELAPSED_TIME
            JavelinLogColumnNum.CALL_CALLEE_METHOD_MODIFIER, // MODIFIER
            JavelinLogColumnNum.CALL_THREADID, // THREAD_NAME
            -1, // THREAD_CLASS
            -1, // THREAD_OBJECTID
        }));

        indexListMap__.put(JavelinConstants.MSG_RETURN, Arrays.asList(new Integer[]{ //
        JavelinLogColumnNum.RETURN_TIME, // START_TIME
            -1, // END_TIME
            -1, // SESSION_DESC
            JavelinLogColumnNum.ID, // LOG_TYPE
            JavelinLogColumnNum.RETURN_CALLEE_METHOD, // CALLEE_NAME
            -1, // CALLEE_SIGNATURE
            JavelinLogColumnNum.RETURN_CALLEE_CLASS, // CALLEE_CLASS
            -1, // CALLEE_FIELD_TYPE
            JavelinLogColumnNum.RETURN_CALLEE_OBJECTID, // CALLEE_OBJECTID
            JavelinLogColumnNum.RETURN_CALLER_METHOD, // CALLER_NAME
            -1, // CALLER_SIGNATURE
            JavelinLogColumnNum.RETURN_CALLER_CLASS, // CALLER_CLASS
            JavelinLogColumnNum.RETURN_CALLER_OBJECTID, // CALLER_OBJECTID
            -1, // EVENT_LEVEL
            -1, // ELAPSED_TIME
            JavelinLogColumnNum.RETURN_CALLEE_METHOD_MODIFIER, // MODIFIER
            JavelinLogColumnNum.RETURN_THREADID, // THREAD_NAME
            -1, // THREAD_CLASS
            -1, // THREAD_OBJECTID
        }));

        indexListMap__.put(JavelinConstants.MSG_FIELD_READ, Arrays.asList(new Integer[]{ //
        JavelinLogColumnNum.READ_WRITE_TIME, // START_TIME
            -1, // END_TIME
            -1, // SESSION_DESC
            JavelinLogColumnNum.ID, // LOG_TYPE
            JavelinLogColumnNum.READ_WRITE_ACCESSEE_FIELD, // CALLEE_NAME
            -1, // CALLEE_SIGNATURE
            JavelinLogColumnNum.READ_WRITE_ACCESSEE_CLASS, // CALLEE_CLASS
            -1, // CALLEE_FIELD_TYPE
            JavelinLogColumnNum.READ_WRITE_ACCESSEE_OBJECTID, // CALLEE_OBJECTID
            JavelinLogColumnNum.READ_WRITE_ACCESSOR_METHOD, // CALLER_NAME
            -1, // CALLER_SIGNATURE
            JavelinLogColumnNum.READ_WRITE_ACCESSOR_CLASS, // CALLER_CLASS
            JavelinLogColumnNum.READ_WRITE_ACCESSOR_OBJECTID, // CALLER_OBJECTID
            -1, // EVENT_LEVEL
            -1, // ELAPSED_TIME
            -1, // MODIFIER
            JavelinLogColumnNum.READ_WRITE_THREADID, // THREAD_NAME
            -1, // THREAD_CLASS
            -1, // THREAD_OBJECTID
        }));

        indexListMap__.put(JavelinConstants.MSG_FIELD_WRITE,
                           indexListMap__.get(JavelinConstants.MSG_FIELD_WRITE));

        indexListMap__.put(JavelinConstants.MSG_THROW, Arrays.asList(new Integer[]{ //
        JavelinLogColumnNum.THROW_TIME, // START_TIME
            -1, // END_TIME
            -1, // SESSION_DESC
            JavelinLogColumnNum.ID, // LOG_TYPE
            -1, // CALLEE_NAME
            -1, // CALLEE_SIGNATURE
            JavelinLogColumnNum.THROW_EX_CLASS, // CALLEE_CLASS
            -1, // CALLEE_FIELD_TYPE
            JavelinLogColumnNum.THROW_EX_OBJECTID, // CALLEE_OBJECTID
            JavelinLogColumnNum.THROW_THROWER_METHOD, // CALLER_NAME
            -1, // CALLER_SIGNATURE
            JavelinLogColumnNum.THROW_THROWER_CLASS, // CALLER_CLASS
            JavelinLogColumnNum.THROW_THROWER_OBJECTID, // CALLER_OBJECTID
            -1, // EVENT_LEVEL
            -1, // ELAPSED_TIME
            -1, // MODIFIER
            JavelinLogColumnNum.THROW_THREADID, // THREAD_NAME
            -1, // THREAD_CLASS
            -1, // THREAD_OBJECTID
        }));

        indexListMap__.put(JavelinConstants.MSG_CATCH, Arrays.asList(new Integer[]{ //
        JavelinLogColumnNum.CATCH_TIME, // START_TIME
            -1, // END_TIME
            -1, // SESSION_DESC
            JavelinLogColumnNum.ID, // LOG_TYPE
            -1, // CALLEE_NAME
            -1, // CALLEE_SIGNATURE
            JavelinLogColumnNum.CATCH_EX_CLASS, // CALLEE_CLASS
            -1, // CALLEE_FIELD_TYPE
            JavelinLogColumnNum.CATCH_EX_OBJECTID, // CALLEE_OBJECTID
            JavelinLogColumnNum.CATCH_CATCHER_METHOD, // CALLER_NAME
            -1, // CALLER_SIGNATURE
            JavelinLogColumnNum.CATCH_CATCHER_CLASS, // CALLER_CLASS
            JavelinLogColumnNum.CATCH_CATCHER_OBJECTID, // CALLER_OBJECTID
            -1, // EVENT_LEVEL
            -1, // ELAPSED_TIME
            -1, // MODIFIER
            JavelinLogColumnNum.CATCH_THREADID, // THREAD_NAME
            -1, // THREAD_CLASS
            -1, // THREAD_OBJECTID
        }));

        // イベント。
        indexListMap__.put(JavelinConstants.MSG_EVENT, Arrays.asList(new Integer[]{ //
        JavelinLogColumnNum.THROW_TIME, // START_TIME
            -1, // END_TIME
            -1, // SESSION_DESC
            JavelinLogColumnNum.ID, // LOG_TYPE
            -1, // CALLEE_NAME
            -1, // CALLEE_SIGNATURE
            JavelinLogColumnNum.EVENT_NAME, // CALLEE_CLASS
            -1, // CALLEE_FIELD_TYPE
            -1, // CALLEE_OBJECTID
            JavelinLogColumnNum.EVENT_METHOD, // CALLER_NAME
            -1, // CALLER_SIGNATURE
            JavelinLogColumnNum.EVENT_CLASS, // CALLER_CLASS
            -1, // CALLER_OBJECTID
            JavelinLogColumnNum.EVENT_LEVEL, // EVENT_LEVEL
            -1, // ELAPSED_TIME
            -1, // MODIFIER
            JavelinLogColumnNum.EVENT_THREADID, // THREAD_NAME
            -1, // THREAD_CLASS
            -1, // THREAD_OBJECTID
        }));

        // logTypeのマップを初期化する。
        logTypeMap__.put(JavelinConstants.MSG_CALL, 1);
        logTypeMap__.put(JavelinConstants.MSG_RETURN, 2);
        logTypeMap__.put(JavelinConstants.MSG_FIELD_READ, 3);
        logTypeMap__.put(JavelinConstants.MSG_FIELD_WRITE, 4);
        logTypeMap__.put(JavelinConstants.MSG_THROW, 5);
        logTypeMap__.put(JavelinConstants.MSG_CATCH, 6);
        logTypeMap__.put(JavelinConstants.MSG_EVENT, 7);
    }

    /**
     * Javelinログの要素を読み込み、JavelinLogインスタンスを生成します。<br />
     *
     * @param javelinLog Javelinログ
     * @param javelinElemList Javelinログの要素
     */
    public static void parse(final JavelinLog javelinLog, final List<String> javelinElemList)
    {
        String logType = javelinElemList.get(JavelinLogColumnNum.ID);
        List<Integer> indexList = indexListMap__.get(logType);
        if (indexList == null)
        {
            return;
        }

        javelinLog.startTime = getTimestamp(javelinElemList, indexList, DB_START_TIME);
        javelinLog.endTime = javelinLog.startTime;
        javelinLog.sessionDesc = getString(javelinElemList, indexList, DB_SESSION_DESC);
        javelinLog.logType = getLogType(javelinElemList, indexList, DB_LOG_TYPE);
        javelinLog.calleeName = getString(javelinElemList, indexList, DB_CALLEE_NAME);
        javelinLog.calleeSignature = getString(javelinElemList, indexList, DB_CALLEE_SIGNATURE);
        javelinLog.calleeClass = getString(javelinElemList, indexList, DB_CALLEE_CLASS);
        javelinLog.calleeFieldType = getString(javelinElemList, indexList, DB_CALLEE_FIELD_TYPE);
        javelinLog.calleeObjectId = getInteger(javelinElemList, indexList, DB_CALLEE_OBJECTID);
        javelinLog.callerName = getString(javelinElemList, indexList, DB_CALLER_NAME);
        javelinLog.callerSignature = getString(javelinElemList, indexList, DB_CALLER_SIGNATURE);
        javelinLog.callerClass = getString(javelinElemList, indexList, DB_CALLER_CLASS);
        javelinLog.callerObjectId = getInteger(javelinElemList, indexList, DB_CALLER_OBJECTID);
        javelinLog.eventLevel = getInteger(javelinElemList, indexList, DB_EVENT_LEVEL);
        javelinLog.elapsedTime = 0;
        javelinLog.modifier = getString(javelinElemList, indexList, DB_MODIFIER);
        javelinLog.threadName = getString(javelinElemList, indexList, DB_THREAD_NAME);
        javelinLog.threadClass = getString(javelinElemList, indexList, DB_THREAD_CLASS);
        javelinLog.threadObjectId = getInteger(javelinElemList, indexList, DB_THREAD_OBJECTID);
    }

    /**
     * CSVに切り出します。<br />
     * 
     * @param line 対象の文字列
     * @return CSVとして分割したリスト
     */
    public static List<String> csvTokenizeHeader(final String line)
    {
        CSVTokenizer csvTokenizer = new CSVTokenizer(line);
        List<String> elemList = new ArrayList<String>();
        while (csvTokenizer.hasMoreTokens())
        {
            String elem = csvTokenizer.nextToken();
            elemList.add(elem);
        }
        return elemList;
    }

    /**
     * durationをパースし、JavelinLogに設定します。<br />
     * 
     * @param javelinLog 設定対象のJavelinLog
     * @param durationStr duration文字列
     */
    public static void setDuration(final JavelinLog javelinLog, final String durationStr)
    {
        long duration;
        try
        {
            duration = Long.parseLong(durationStr);
        }
        catch (NumberFormatException nfe)
        {
            duration = -1;
        }
        javelinLog.elapsedTime = duration;
        Timestamp startTime = javelinLog.startTime;
        if (startTime != null)
        {
            javelinLog.endTime = new Timestamp(startTime.getTime() + duration);
        }
    }

    private static int getInteger(final List<String> javelinElemList,
            final List<Integer> indexList, final int dbIndex)
    {
        int value = -1;
        Integer index = indexList.get(dbIndex);
        if (index == null || index < 0)
        {
            return value;
        }

        try
        {
            value = Integer.parseInt(javelinElemList.get(indexList.get(dbIndex)));
        }
        catch (NumberFormatException nfe)
        {
            value = -1;
        }
        return value;
    }

    private static String getString(final List<String> javelinElemList,
            final List<Integer> indexList, final int dbIndex)
    {
        Integer index = indexList.get(dbIndex);
        if (index == null || index < 0)
        {
            return null;
        }

        return javelinElemList.get(index);
    }

    private static Timestamp getTimestamp(final List<String> javelinElemList,
            final List<Integer> indexList, final int dbIndex)
    {
        String timeStr = getString(javelinElemList, indexList, dbIndex);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date = null;
        try
        {
            date = simpleDateFormat.parse(timeStr);
        }
        catch (ParseException ex)
        {
            return null;
        }

        long time = date.getTime();
        return new Timestamp(time);
    }

    private static int getLogType(final List<String> javelinElemList,
            final List<Integer> indexList, final int dbLogType)
    {
        String logId = getString(javelinElemList, indexList, dbLogType);
        if (logTypeMap__.containsKey(logId))
        {
            return logTypeMap__.get(logId);
        }

        return 0;
    }
}
