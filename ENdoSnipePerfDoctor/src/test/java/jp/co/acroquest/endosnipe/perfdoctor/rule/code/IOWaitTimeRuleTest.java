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
package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

/**
 * ENdoSnipeVer.4.0の新ルール
 * TATは長いが、CPU時間、WAIT時間、ブロック時間が短いルールのテスト
 * @author akita
 *
 */
public class IOWaitTimeRuleTest extends PerformanceRuleTestCase
{
    /**
     * 閾値を指定してLongTATShortCPUWaitBlockRuleを生成する。<br>
     * @param threshold 閾値
     * @return IOWaitTimeRule
     */
    private IOWaitTimeRule createRule(final long threshold)
    {
        IOWaitTimeRule rule = createInstance(IOWaitTimeRule.class);
        rule.id = "COD.MTRC.METHOD_IO_WAIT_TIME";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 3-12-1<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・検出値が4999。<br>
     * ・閾値が5000。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_01()
    {
        IOWaitTimeRule rule = createRule(5000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IOWaitTimeRuleTest_testDoJudge_4999.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-12-2<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・検出値が5000。<br>
     * ・閾値が5000。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_02()
    {
        IOWaitTimeRule rule = createRule(5000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IOWaitTimeRuleTest_testDoJudge_5000.jvn");

        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 5000L, 5000L);
    }

    /**
     * [項番] 3-12-3<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・検出値が5001。<br>
     * ・閾値が5000。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_03()
    {
        IOWaitTimeRule rule = createRule(5000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IOWaitTimeRuleTest_testDoJudge_5001.jvn");

        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 5000L, 5001L);
    }

    /**
     * [項番] 3-12-4<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・検出値が10。<br>
     * ・閾値が10。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_05()
    {
        IOWaitTimeRule rule = createRule(10);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IOWaitTimeRuleTest_testDoJudge_10.jvn");

        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 10L, 10L);
    }

    /**
     * [項番] 3-12-5<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・TATの値が数値ではなく文字列である場合。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_10()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IOWaitTimeRuleTest_testDoJudge_invalid.jvn");

        IOWaitTimeRule rule = createRule(5);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-12-6<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・TATの値が空白。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_11()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IOWaitTimeRuleTest_testDoJudge_no_value.jvn");

        IOWaitTimeRule rule = createRule(5);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-12-7<br>
     * <br>
     * doJudgeのテスト。<br>
     *　指定されたパラメータがない。ブロックのパラメータが無い場合<br>
     * →警告を表示しない。<br>
     */
    public void testDoJudge_12()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IOWaitTimeRuleTest_testDoJudge_no_time.jvn");

        IOWaitTimeRule rule = createRule(5);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-12-8<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・detailInfoがない。ブロック時間が無い場合<br>
     * →警告を表示しない。<br>
     */
    public void testDoJudge_14()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IOWaitTimeRuleTest_testDoJudge_no_detailInfo.jvn");

        IOWaitTimeRule rule = createRule(5);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-12-9<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・CALLがない。<br>
     * →警告を表示しない。<br>
     */
    public void testDoJudge_15()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IOWaitTimeRuleTest_testDoJudge_no_call.jvn");

        IOWaitTimeRule rule = createRule(5);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-12-10<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・複数のJavelinLogElementで警告が出る。<br>
     */
    public void testDoJudge_26()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IOWaitTimeRuleTest_testDoJudge_multi.jvn");

        IOWaitTimeRule rule = createRule(5000);
        rule.doJudge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 5000L, 5000L);
        assertErrorOccurred(elementList.get(4), 5000L, 5000L);
    }

    /**
     * [項番] 3-12-11<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・あるJavelinLogElementで実行時例外が発生<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_RuntimeException()
    {
        IOWaitTimeRule rule = createRule(5000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IOWaitTimeRuleTest_testDoJudge_5000.jvn");

        elementList.add(0, null);

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), 5000L, 5000L);
    }

}
