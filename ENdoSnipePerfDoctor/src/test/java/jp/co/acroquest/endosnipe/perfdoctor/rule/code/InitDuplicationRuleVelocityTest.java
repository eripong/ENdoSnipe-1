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
 * Velocityの複数回初期化チェックルールのテスト
 * @author fujii
 *
 */
public class InitDuplicationRuleVelocityTest extends PerformanceRuleTestCase
{
    /** Velocityの初期化メソッド */
    private static final String VELOCITY_INITIAL_METHOD = "init";

    /** Velocityの初期化クラス */
    private static final String VELOCITY_INITIAL_CLASS = "org.apache.velocity.app.Velocity";

    /**
     * InitDupulicationRuleを生成する。<br />
     * @param threshold 閾値
     * @return InitDupulicationRule
     */
    private InitDupulicationRule createRule(long threshold)
    {
        InitDupulicationRule rule = createInstance(InitDupulicationRule.class);
        rule.id = "LIB.VELOCITY.INIT_DUPLICATION";
        rule.active = true;
        rule.level = "WARN";
        rule.threshold = threshold;
        rule.classNameList = VELOCITY_INITIAL_CLASS;
        rule.methodNameList = VELOCITY_INITIAL_METHOD;
        return rule;
    }

    /**
     * [項番] 3-17-1<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・境界値分析(閾値より1小さい場合)<br />
     * →警告が発生する。<br />
     */
    public void testDoJudge_1()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("InitDuplicateTest_testDoJudge_velocity_999.jvn");

        long threshold = 1000;

        InitDupulicationRule rule = createRule(threshold);

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), threshold, (long)999, VELOCITY_INITIAL_CLASS,
                            VELOCITY_INITIAL_METHOD);

    }

    /**
     * [項番] 3-17-2<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・境界値分析(閾値と等しい場合)<br />
     * →警告が発生する。<br />
     */
    public void testDoJudge_2()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("InitDuplicateTest_testDoJudge_velocity_1000.jvn");

        long threshold = 1000;

        InitDupulicationRule rule = createRule(threshold);

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), threshold, (long)1000, VELOCITY_INITIAL_CLASS,
                            VELOCITY_INITIAL_METHOD);

    }

    /**
     * [項番] 3-17-3<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・境界値分析(閾値より1大きい場合)<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_3()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("InitDuplicateTest_testDoJudge_velocity_1001.jvn");

        long threshold = 1000;

        InitDupulicationRule rule = createRule(threshold);

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-17-4<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・境界値分析(閾値を別の値に設定/閾値と等しい場合)<br />
     * →警告が発生する。<br />
     */
    public void testDoJudge_5()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("InitDuplicateTest_testDoJudge_velocity_500.jvn");

        long threshold = 500;

        InitDupulicationRule rule = createRule(threshold);

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), threshold, (long)500, VELOCITY_INITIAL_CLASS,
                            VELOCITY_INITIAL_METHOD);
    }

    /**
     * [項番] 3-17-5<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・境界値分析(閾値0、検出値0の場合)<br />
     * →警告が発生する。<br />
     */
    public void testDoJudge_7()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("InitDuplicateTest_testDoJudge_velocity_0.jvn");

        long threshold = 0;

        InitDupulicationRule rule = createRule(threshold);

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), threshold, (long)0, VELOCITY_INITIAL_CLASS,
                            VELOCITY_INITIAL_METHOD);
    }

    /**
     * [項番] 3-17-6<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・境界値分析(閾値0、検出値が正の値の場合)<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_8()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("InitDuplicateTest_testDoJudge_velocity_threshold0.jvn");

        long threshold = 0;

        InitDupulicationRule rule = createRule(threshold);

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-17-7<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・指定されたパラメータの値が文字列になっている場合<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_10()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("InitDuplicateTest_testDoJudge_velocity_parameterString.jvn");

        long threshold = 1000;

        InitDupulicationRule rule = createRule(threshold);

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-17-8<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・指定されたパラメータの値が空になっている場合<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_11()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("InitDuplicateTest_testDoJudge_velocity_parameterEmpty.jvn");

        long threshold = 1000;

        InitDupulicationRule rule = createRule(threshold);

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-17-9<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・指定されたパラメータがない場合<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_12()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("InitDuplicateTest_testDoJudge_velocity_parameterNone.jvn");

        long threshold = 1000;

        InitDupulicationRule rule = createRule(threshold);

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-17-10<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・指定されたInfoがない場合<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_14()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("InitDuplicateTest_testDoJudge_velocity_NoInfo.jvn");

        long threshold = 1000;

        InitDupulicationRule rule = createRule(threshold);

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-17-11<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・指定されたタイプのメッセージがない場合<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_15()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("InitDuplicateTest_testDoJudge_velocity_NoType.jvn");

        long threshold = 1000;

        InitDupulicationRule rule = createRule(threshold);

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-17-12<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・指定されたイベント名がない場合<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_16()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("InitDuplicateTest_testDoJudge_velocity_NoEventName.jvn");

        long threshold = 1000;

        InitDupulicationRule rule = createRule(threshold);

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-17-13<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・クラス名が同一だが、メソッド名が異なる場合<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_24()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("InitDuplicateTest_testDoJudge_velocity_wrongMethodName.jvn");

        long threshold = 1000;

        InitDupulicationRule rule = createRule(threshold);

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-17-14<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・メソッド名が同一だが、クラス名が異なる場合<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_25()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("InitDuplicateTest_testDoJudge_velocity_wrongClassName.jvn");

        long threshold = 1000;

        InitDupulicationRule rule = createRule(threshold);

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(0, getErrorJavelinLogElements().size());
    }

    /**
     * [項番] 3-17-15<br />
     * <br />
     * doJudgeのテスト。<br />
     * ・複数のJavelinLogElementで警告が出る場合<br />
     * →警告が発生しない。<br />
     */
    public void testDoJudge_27()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("InitDuplicateTest_testDoJudge_velocity_multiElement.jvn");

        long threshold = 1000;

        InitDupulicationRule rule = createRule(threshold);

        // 実行
        rule.judge(elementList);

        // 検証
        assertEquals(2, getErrorJavelinLogElements().size());

        // CALL → EVENT → RETURN → CALL → EVENT → RETURN
        // の順にelementListのイベントが作成されるので、
        // 2番目と5番目のイベントが警告に出力されることを確認する。
        assertErrorOccurred(elementList.get(1), threshold, (long)1000, VELOCITY_INITIAL_CLASS,
                            VELOCITY_INITIAL_METHOD);
        assertErrorOccurred(elementList.get(4), threshold, (long)500, VELOCITY_INITIAL_CLASS,
                            VELOCITY_INITIAL_METHOD);
    }

    /**
     * [項番] 3-17-16<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・実行時例外が発生する場合<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_29_RuntimeException()
    {
        // 準備
        List<JavelinLogElement> elementList =
                createJavelinLogElement("InitDuplicateTest_testDoJudge_velocity_1000.jvn");

        long threshold = 1000;
        InitDupulicationRule rule = createRule(threshold);

        elementList.add(0, null);

        // 実行
        rule.doJudge(elementList);

        // 検証
        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), threshold, (long)1000, VELOCITY_INITIAL_CLASS,
                            VELOCITY_INITIAL_METHOD);
    }

}
