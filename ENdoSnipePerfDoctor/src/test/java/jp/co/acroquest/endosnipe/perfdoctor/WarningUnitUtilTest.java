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
package jp.co.acroquest.endosnipe.perfdoctor;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.acroquest.endosnipe.common.parser.JavelinLogColumnNum;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import junit.framework.TestCase;

/**
 * {@link WarningUnitUtil}のテストケース
 * @author fujii
 *
 */
public class WarningUnitUtilTest extends TestCase
{
    /** 日付のフォーマット */
    private static final String DATE_FORMAT = "yyyy/M/d HH:mm:ss.SSS";

    /**
     * calculateStartTimeExistDate のテストケース。
     * 存在する日付に対するテスト。
     */
    public void testCalculateStartTimeExistDate()
        throws Exception
    {
        // 準備
        Method method = getCalculateStartTimeMethod();
        String startTimeStr = "2010/10/10 10:10:10.111";

        List<String> infoList = new ArrayList<String>(10);
        infoList.add(JavelinLogColumnNum.ID, "CALL");
        infoList.add(JavelinLogColumnNum.CALL_TIME, startTimeStr);
        JavelinLogElement element = new JavelinLogElement();
        element.setBaseInfo(infoList);

        // 実行
        Long result = (Long)method.invoke(null, new Object[]{element});

        // 結果
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Date resultDate = new Date(result.longValue());
        assertEquals(startTimeStr, formatter.format(resultDate));
    }

    /**
     * calculateStartTimeExistDate のテストケース。
     * 月が1ケタ
     */
    public void testCalculateStartTimeMonthSingleDigit()
        throws Exception
    {
        // 準備
        Method method = getCalculateStartTimeMethod();
        String startTimeStr = "2010/1/10 10:10:10.111";
    
        List<String> infoList = new ArrayList<String>(10);
        infoList.add(JavelinLogColumnNum.ID, "CALL");
        infoList.add(JavelinLogColumnNum.CALL_TIME, startTimeStr);
        JavelinLogElement element = new JavelinLogElement();
        element.setBaseInfo(infoList);
    
        // 実行
        Long result = (Long)method.invoke(null, new Object[]{element});
    
        // 結果
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Date resultDate = new Date(result.longValue());
        assertEquals(startTimeStr, formatter.format(resultDate));
    }

    /**
     * calculateStartTimeExistDate のテストケース。
     * 日が1ケタ
     */
    public void testCalculateStartTimeDaySingleDigit()
        throws Exception
    {
        // 準備
        Method method = getCalculateStartTimeMethod();
        String startTimeStr = "2010/10/1 10:10:10.111";
    
        List<String> infoList = new ArrayList<String>(10);
        infoList.add(JavelinLogColumnNum.ID, "CALL");
        infoList.add(JavelinLogColumnNum.CALL_TIME, startTimeStr);
        JavelinLogElement element = new JavelinLogElement();
        element.setBaseInfo(infoList);
    
        // 実行
        Long result = (Long)method.invoke(null, new Object[]{element});
    
        // 結果
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Date resultDate = new Date(result.longValue());
        assertEquals(startTimeStr, formatter.format(resultDate));
    }
    
    /**
     * calculateStartTimeExistDate のテストケース。
     * 時間が1ケタ
     */
    public void testCalculateStartTimeHourSingleDigit()
        throws Exception
    {
        // 準備
        Method method = getCalculateStartTimeMethod();
        String startTimeStr = "2010/10/10 1:10:10.111";
        String expectedResult = "2010/10/10 01:10:10.111";
    
        List<String> infoList = new ArrayList<String>(10);
        infoList.add(JavelinLogColumnNum.ID, "CALL");
        infoList.add(JavelinLogColumnNum.CALL_TIME, startTimeStr);
        JavelinLogElement element = new JavelinLogElement();
        element.setBaseInfo(infoList);
    
        // 実行
        Long result = (Long)method.invoke(null, new Object[]{element});
    
        // 結果
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Date resultDate = new Date(result.longValue());
        assertEquals(expectedResult, formatter.format(resultDate));
    }
    
    /**
     * calculateStartTimeExistDate のテストケース。
     * 分が1ケタ
     */
    public void testCalculateStartTimeMinuteSingleDigit()
        throws Exception
    {
        // 準備
        Method method = getCalculateStartTimeMethod();
        String startTimeStr = "2010/10/10 10:1:10.111";
        String expectedResult = "2010/10/10 10:01:10.111";
    
        List<String> infoList = new ArrayList<String>(10);
        infoList.add(JavelinLogColumnNum.ID, "CALL");
        infoList.add(JavelinLogColumnNum.CALL_TIME, startTimeStr);
        JavelinLogElement element = new JavelinLogElement();
        element.setBaseInfo(infoList);
    
        // 実行
        Long result = (Long)method.invoke(null, new Object[]{element});
    
        // 結果
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Date resultDate = new Date(result.longValue());
        assertEquals(expectedResult, formatter.format(resultDate));
    }
    
    /**
     * calculateStartTimeExistDate のテストケース。
     * 秒が1ケタ
     */
    public void testCalculateStartTimeSecondSingleDigit()
        throws Exception
    {
        // 準備
        Method method = getCalculateStartTimeMethod();
        String startTimeStr = "2010/10/10 10:10:1.111";
        String expectedResult = "2010/10/10 10:10:01.111";
    
        List<String> infoList = new ArrayList<String>(10);
        infoList.add(JavelinLogColumnNum.ID, "CALL");
        infoList.add(JavelinLogColumnNum.CALL_TIME, startTimeStr);
        JavelinLogElement element = new JavelinLogElement();
        element.setBaseInfo(infoList);
    
        // 実行
        Long result = (Long)method.invoke(null, new Object[]{element});
    
        // 結果
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Date resultDate = new Date(result.longValue());
        assertEquals(expectedResult, formatter.format(resultDate));
    }

    /**
     * calculateStartTimeExistDate のテストケース。
     * フォーマットエラー(ミリ秒がない)
     */
    public void testCalculateStartTimeIllegalFormat1()
        throws Exception
    {
        // 準備
        Method method = getCalculateStartTimeMethod();
        String startTimeStr = "2010/10/10 10:10:10";

        List<String> infoList = new ArrayList<String>(10);
        infoList.add(JavelinLogColumnNum.ID, "CALL");
        infoList.add(JavelinLogColumnNum.CALL_TIME, startTimeStr);
        JavelinLogElement element = new JavelinLogElement();
        element.setBaseInfo(infoList);

        // 実行
        Long result = (Long)method.invoke(null, new Object[]{element});

        // 結果
        assertEquals(Long.valueOf(0), result);
    }

    /**
     * calculateStartTimeExistDate のテストケース。
     * 日付のセパレータが"-"
     */
    public void testCalculateStartTimeIllegalFormat2()
        throws Exception
    {
        // 準備
        Method method = getCalculateStartTimeMethod();
        String startTimeStr = "2010-10-10 10:10:10.111";

        List<String> infoList = new ArrayList<String>(10);
        infoList.add(JavelinLogColumnNum.ID, "CALL");
        infoList.add(JavelinLogColumnNum.CALL_TIME, startTimeStr);
        JavelinLogElement element = new JavelinLogElement();
        element.setBaseInfo(infoList);

        // 実行
        Long result = (Long)method.invoke(null, new Object[]{element});

        // 結果
        assertEquals(Long.valueOf(0), result);
    }

    /**
     * calculateStartTimeExistDate のテストケース。
     * 開始時刻が未指定
     */
    public void testCalculateStartTimeSetNull()
        throws Exception
    {
        // 準備
        Method method = getCalculateStartTimeMethod();

        List<String> infoList = new ArrayList<String>(10);
        infoList.add(JavelinLogColumnNum.ID, "CALL");
        JavelinLogElement element = new JavelinLogElement();
        element.setBaseInfo(infoList);

        // 実行
        Long result = (Long)method.invoke(null, new Object[]{element});

        // 結果
        assertEquals(Long.valueOf(0), result);
    }

    /**
     * calculateStartTimeExistDate のテストケース。
     * 空文字を指定
     */
    public void testCalculateStartTimeSetEmpty()
        throws Exception
    {
        // 準備
        Method method = getCalculateStartTimeMethod();
        String startTimeStr = "";

        List<String> infoList = new ArrayList<String>(10);
        infoList.add(JavelinLogColumnNum.ID, "CALL");
        infoList.add(JavelinLogColumnNum.CALL_TIME, startTimeStr);
        JavelinLogElement element = new JavelinLogElement();
        element.setBaseInfo(infoList);

        // 実行
        Long result = (Long)method.invoke(null, new Object[]{element});

        // 結果
        assertEquals(Long.valueOf(0), result);
    }

    /**
     * WarningUnitUtil#calculateStartTimeMethod メソッドを取得する。
     * @return calculateStartTimeMethodメソッド
     */
    private Method getCalculateStartTimeMethod()
        throws Exception
    {
        Class<?> clazz = null;
        try
        {
            clazz = Class.forName("jp.co.acroquest.endosnipe.perfdoctor.WarningUnitUtil");
        }
        catch (ClassNotFoundException ex)
        {
            throw ex;
        }
        Method method;
        try
        {
            method =
                     clazz.getDeclaredMethod("calculateStartTime",
                                             new Class[]{JavelinLogElement.class});
        }
        catch (SecurityException ex)
        {
            throw ex;
        }
        catch (NoSuchMethodException ex)
        {
            throw ex;
        }
        method.setAccessible(true);
        return method;
    }

}
