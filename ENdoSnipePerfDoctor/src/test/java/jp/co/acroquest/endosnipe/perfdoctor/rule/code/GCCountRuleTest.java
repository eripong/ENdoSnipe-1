package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

/**
 * GC実行頻度判定ルール用のテストケース<br>
 * <br>
 * @author S.Kimura
 */
public class GCCountRuleTest extends PerformanceRuleTestCase
{

    /**
     * 閾値を指定してGCFrequencyRuleを生成する。<br>
     * @param threshold 閾値
     * @return GCFrequencyRule
     */
    private GCCountRule createRule(int threshold)
    {
        GCCountRule rule = createInstance(GCCountRule.class);
        rule.id = "COD.MTRC.GC_CNT";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 2-5-1<br>
     * <br>
     * judgeのテスト。<br>
     * ・検出頻度が0。<br>
     * ・閾値が1。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_01()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("GCFrequencyRule_testDoJudge_00.jvn");

        GCCountRule rule = createRule(1);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }

    }

    /**
     * [項番] 2-5-2<br>
     * <br>
     * judgeのテスト。<br>
     * ・検出回数が1。<br>
     * ・閾値が1。<br>
     * →警告が発生しない。(警告リストのサイズが0)<br>
     */
    public void testDoJudge_02()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("GCFrequencyRule_testDoJudge_01.jvn");

        GCCountRule rule = createRule(1);
        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 1, 1, 413.0);
    }

    /**
     * [項番] 2-5-3<br>
     * <br>
     * judgeのテスト。<br>
     * ・検出頻度が2。<br>
     * ・閾値が1。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_03()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("GCFrequencyRule_testDoJudge_02.jvn");

        GCCountRule rule = createRule(1);
        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 1, 2, 413.0);
    }

    /**
     * [項番] 2-5-5<br>
     * <br>
     * judgeのテスト。<br>
     * ・検出頻度が20。<br>
     * ・閾値が20。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_05()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("GCFrequencyRule_testDoJudge_20.jvn");

        GCCountRule rule = createRule(20);
        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 20, 20, 413.0);
    }

    /**
     * [項番] 2-5-10<br>
     * <br>
     * judgeのテスト。<br>
     * ・GC回数の値が数値ではない。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_10()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("GCFrequencyRule_testDoJudge_invalid.jvn");

        GCCountRule rule = createRule(5);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-5-11<br>
     * <br>
     * judgeのテスト。<br>
     * ・GC回数の値が空白。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_11()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("GCFrequencyRule_testDoJudge_empty.jvn");

        GCCountRule rule = createRule(5);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-5-12<br>
     * <br>
     * 異常ログ<br>
     * ログ内にパラメータが無い。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_12()
    {
        GCCountRule rule = createRule(5);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("GCFrequencyRule_testDoJudge_no_param.jvn");

        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-5-14<br>
     * <br>
     * judgeのテスト。<br>
     * ・detailInfoがない。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_14()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("GCFrequencyRule_testDoJudge_no_detailInfo.jvn");

        GCCountRule rule = createRule(5);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-5-15<br>
     * <br>
     * judgeのテスト。<br>
     * ・CALLがない。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_15()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("GCFrequencyRule_testDoJudge_no_call.jvn");

        GCCountRule rule = createRule(5);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-5-26<br>
     * <br>
     * judgeのテスト。<br>
     * ・複数のJavelinLogElementで警告が出る。<br>
     */
    public void testDoJudge_26()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("GCFrequencyRule_testDoJudge_multi.jvn");

        GCCountRule rule = createRule(1);
        rule.judge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 1, 2, 413.0);
        assertErrorOccurred(elementList.get(4), 1, 2, 413.0);
    }

    /**
     * [項番] 2-5-27<br>
     * <br>
     * judgeのテスト。<br>
     * ・あるJavelinLogElementで実行時例外が発生<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_RuntimeException()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("GCFrequencyRule_testDoJudge_02.jvn");

        elementList.add(0, null);

        GCCountRule rule = createRule(1);
        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), 1, 2, 413.0);

    }

}
