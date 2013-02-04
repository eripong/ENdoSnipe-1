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
 * タイムアウト値初期化チェックルールのテスト
 * @author fujii
 *
 */
public class NoTimeoutDetectRuleTest extends PerformanceRuleTestCase
{
    /**
     * NoTimeoutDetectRuleを生成する。<br />
     * @return NoTimeoutDetectRule
     */
    private NoTimeoutDetectRule createRule()
    {
        NoTimeoutDetectRule rule = createInstance(NoTimeoutDetectRule.class);
        rule.id = "COD.IO.NOTIMEOUT";
        rule.active = true;
        rule.level = "ERROR";
        return rule;
    }

    /**
     * [項番] 3-22-1<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・境界値分析(閾値と等しい場合)<br />
     * →警告が発生する。<br />
     */
    public void testDoJudge_2()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("NoTimeoutDetect_testDoJudge_0.jvn");

        NoTimeoutDetectRule rule = createRule();

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), "java.net.SocksSocketImpl@f0c0d3", 0);

    }

    /**
     * [項番] 3-22-2<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・指定されたパラメータの値が文字列になっている場合<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_10()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("NoTimeoutDetect_testDoJudge_parameterString.jvn");

        NoTimeoutDetectRule rule = createRule();

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-22-3<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・指定されたパラメータの値が空になっている場合<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_11()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("NoTimeoutDetect_testDoJudge_parameterEmpty.jvn");

        NoTimeoutDetectRule rule = createRule();

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-22-4<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・指定されたパラメータがない場合<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_12()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("NoTimeoutDetect_testDoJudge_parameterNone.jvn");

        NoTimeoutDetectRule rule = createRule();

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-22-5<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・指定されたInfoがない場合<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_14()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("NoTimeoutDetect_testDoJudge_noEventInfo.jvn");

        NoTimeoutDetectRule rule = createRule();

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-22-6<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・指定されたタイプのメッセージがない場合<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_15()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("NoTimeoutDetect_testDoJudge_noEventType.jvn");

        NoTimeoutDetectRule rule = createRule();

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-22-7<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・指定されたイベント名がない場合<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_16()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("NoTimeoutDetect_testDoJudge_noEventName.jvn");

        NoTimeoutDetectRule rule = createRule();

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-22-8<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・複数のJavelinLogElementで警告が出る場合<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_27()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("NoTimeoutDetect_testDoJudge_multiElement.jvn");

        NoTimeoutDetectRule rule = createRule();

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(2, getErrorJavelinLogElements().size());

        // CALL → EVENT → RETURN → CALL → EVENT → RETURN
        // の順にelementListのイベントが作成されるので、
        // 2番目と5番目のイベントが警告に出力されることを確認する。
        assertErrorOccurred(elementList.get(1), "java.net.SocksSocketImpl@5f8172", 0);
        assertErrorOccurred(elementList.get(4), "java.net.SocksSocketImpl@5f8173", 0);
    }

    /**
     * [項番] 3-22-9<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・実行時例外が発生する場合<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_29_RuntimeException()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("NoTimeoutDetect_testDoJudge_0.jvn");

        NoTimeoutDetectRule rule = createRule();

        elementList.add(0, null);

        // 実行
        rule.doJudge(elementList);

        // 検証
        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), "java.net.SocksSocketImpl@f0c0d3", 0);
    }
}
