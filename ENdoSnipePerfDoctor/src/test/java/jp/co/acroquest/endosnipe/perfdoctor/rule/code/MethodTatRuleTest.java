package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

/**
 * メソッドのTATルールのテスト<br>
 * @author tooru
 *
 */
public class MethodTatRuleTest extends PerformanceRuleTestCase
{
    /**
     * 閾値を指定してCodeMetricsRuleを生成する。<br>
     * @param threshold 閾値
     * @return CodeMetricsRule
     */
    private MethodTatRule createRule(long threshold)
    {
        MethodTatRule rule = createInstance(MethodTatRule.class);
        rule.id = "COD.MTRC.METHOD_CNT";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 2-8-1<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・TATが4999。<br>
     * ・閾値が5000。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_01()
    {
        MethodTatRule rule = createRule(5000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodTatRuleTest_testDoJudge_4999.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-8-2<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・TATが5000。<br>
     * ・閾値が5000。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_02()
    {
        MethodTatRule rule = createRule(5000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodTatRuleTest_testDoJudge_5000.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 5000L, 5000L);
    }

    /**
     * [項番] 2-8-3<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・TATが5001。<br>
     * ・閾値が5000。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_03()
    {
        MethodTatRule rule = createRule(5000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodTatRuleTest_testDoJudge_5001.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 5000L, 5001L);
    }

    /**
     * [項番] 2-8-5<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・TATが10。<br>
     * ・閾値が10。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_05()
    {
        MethodTatRule rule = createRule(10);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodTatRuleTest_testDoJudge_10.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 10L, 10L);
    }

    /**
     * [項番] 2-8-10<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・TATの値が数値ではない。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_10()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodTatRuleTest_testDoJudge_invalid.jvn");

        MethodTatRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-8-11<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・TATの値が空白。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_11()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodTatRuleTest_testDoJudge_empty.jvn");

        MethodTatRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-8-13<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・[TIME]というタグがない。<br>
     * →警告を表示しない。<br>
     */
    public void testDoJudge_13()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodTatRuleTest_testDoJudge_no_time.jvn");

        MethodTatRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-8-14<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・detailInfoがない。<br>
     * →警告を表示しない。<br>
     */
    public void testDoJudge_14()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodTatRuleTest_testDoJudge_no_detailInfo.jvn");

        MethodTatRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-8-15<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・CALLがない。<br>
     * →警告を表示しない。<br>
     */
    public void testDoJudge_15()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodTatRuleTest_testDoJudge_no_call.jvn");

        MethodTatRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-8-26<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・複数のJavelinLogElementで警告が出る。<br>
     */
    public void testDoJudge_26()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodTatRuleTest_testDoJudge_multi.jvn");

        MethodTatRule rule = createRule(5000);
        rule.doJudge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 5000L, 5001L);
        assertErrorOccurred(elementList.get(4), 5000L, 5001L);
    }

    /**
     * [項番] 2-8-27<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・あるJavelinLogElementで実行時例外が発生<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_RuntimeException()
    {
        MethodTatRule rule = createRule(5000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodTatRuleTest_testDoJudge_5001.jvn");

        elementList.add(0, null);

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), 5000L, 5001L);
    }

}
