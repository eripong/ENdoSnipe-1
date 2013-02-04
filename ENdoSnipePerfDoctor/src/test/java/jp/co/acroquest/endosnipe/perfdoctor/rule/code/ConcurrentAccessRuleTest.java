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
public class ConcurrentAccessRuleTest extends PerformanceRuleTestCase
{
    /** ログファイルに利用するMapのオブジェクトID */
    private static final String MAP_OBJECT_ID = "java.util.HashMap@1b134a0";

    /**
     * ConcurrentAccessRuleを生成する。<br>
     * @return ConcurrentAccessRule
     */
    private ConcurrentAccessRule createRule()
    {
        ConcurrentAccessRule rule = createInstance(ConcurrentAccessRule.class);
        rule.id = "COD.THRD.CONCURRENT_ACCESS";
        rule.active = true;
        rule.level = "ERROR";
        return rule;
    }

    /**
     * [項番] 3-14-1<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・指定された値が文字列である場合。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_10()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ConcurrentAccessRuleTest_testDoJudge_valstring.jvn");

        ConcurrentAccessRule rule = createRule();

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(1, getErrorJavelinLogElements().size());

        // elementListには、CALL→EVENTの順に入力されているので、
        // 2番目のJavelinLogElementが警告に出力される。
        assertErrorOccurred(elementList.get(1), MAP_OBJECT_ID, "Thread-0,Thread-1");

    }

    /**
     * [項番] 3-14-2<br>
     * <br>
     * doJudgeのテスト。<br>
     * 指定された値が空白。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_11()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ConcurrentAccessRuleTest_testDoJudge_empty.jvn");

        ConcurrentAccessRule rule = createRule();

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-14-3<br>
     * <br>
     * doJudgeのテスト。<br>
     *　指定されたパラメータがない。ブロックのパラメータが無い場合<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_12()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ConcurrentAccessRuleTest_testDoJudge_no_param.jvn");
        ConcurrentAccessRule rule = createRule();

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-14-4<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・EventInfoがない。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_14()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ConcurrentAccessRuleTest_testDoJudge_no_EventInfo.jvn");

        ConcurrentAccessRule rule = createRule();

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-14-5<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・EVENTがない。<br>
     * →警告を表示しない。<br>
     */
    public void testDoJudge_15()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ConcurrentAccessRuleTest_testDoJudge_no_type.jvn");
        ConcurrentAccessRule rule = createRule();

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-14-6<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・EVENTがない。<br>
     * →警告を表示しない。<br>
     */
    public void testDoJudge_16()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ConcurrentAccessRuleTest_testDoJudge_no_eventname.jvn");
        ConcurrentAccessRule rule = createRule();

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-14-7<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・複数のJavelinLogElementで警告が出る。<br>
     */
    public void testDoJudge_27()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ConcurrentAccessRuleTest_testDoJudge_multi_Element.jvn");
        ConcurrentAccessRule rule = createRule();

        // 実行
        rule.doJudge(elementList);

        // 検証
        assertEquals(2, getErrorJavelinLogElements().size());
        // elementListには、CALL→EVENT→EVENTの順に入力されているので、
        // 2番目、3番目のJavelinLogElementが警告に出力される。
        assertErrorOccurred(elementList.get(1), MAP_OBJECT_ID, "Thread-0,Thread-1");
        assertErrorOccurred(elementList.get(2), MAP_OBJECT_ID, "Thread-1,Thread-0");
    }

    /**
     * [項番] 3-14-8<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・あるJavelinLogElementで実行時例外が発生<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_28_RuntimeException()
    {
        // 準備
        ConcurrentAccessRule rule = createRule();
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ConcurrentAccessRuleTest_testDoJudge_valstring.jvn");
        elementList.add(0, null);

        // 実行
        rule.doJudge(elementList);

        // 検証
        assertEquals(1, getErrorJavelinLogElements().size());
        // elementListには、null→CALL→EVENTの順に入力されているので、
        // 2番目のJavelinLogElementが警告に出力される。
        assertErrorOccurred(elementList.get(2), MAP_OBJECT_ID, "Thread-0,Thread-1");
    }

}
