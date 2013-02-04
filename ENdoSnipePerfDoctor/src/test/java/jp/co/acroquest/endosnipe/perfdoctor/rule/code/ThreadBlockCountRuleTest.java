package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

public class ThreadBlockCountRuleTest extends PerformanceRuleTestCase
{

    /**
     * 閾値を指定してThreadBlockCountRuleを生成する。<br>
     * @param threshold 閾値
     * @return ThreadBlockCountRule
     */
    private ThreadBlockCountRule createRule(long threshold)
    {
        ThreadBlockCountRule rule = createInstance(ThreadBlockCountRule.class);
        rule.id = "COD.THRD.BLK_CNT";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 2-3-1
     */
    public void testDoJudge_01()
    {
        ThreadBlockCountRule rule = createRule(100);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal1_under.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-3-2
     */
    public void testDoJudge_02()
    {
        ThreadBlockCountRule rule = createRule(100);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal1_on.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)100,
                            "executeInstance2InstanceMethodCall");
    }

    /**
     * [項番] 2-3-3
     */
    public void testDoJudge_03()
    {
        ThreadBlockCountRule rule = createRule(100);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal1_over.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)101,
                            "executeInstance2InstanceMethodCall");
    }

    /**
     * [項番] 2-3-5
     */
    public void testDoJudge_05()
    {
        ThreadBlockCountRule rule = createRule(5);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal2_on.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)5,
                            "executeInstance2InstanceMethodCall");
    }

    /**
     * [項番] 2-3-10
     */
    public void testDoJudge_10()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_not_number.jvn");

        ThreadBlockCountRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-3-11
     */
    public void testDoJudge_11()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_empty.jvn");

        ThreadBlockCountRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-3-12
     */
    public void testDoJudge_12()
    {
        ThreadBlockCountRule rule = createRule(5);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_no_param.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-3-14
     */
    public void testDoJudge_14()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_no_JMXInfo.jvn");

        ThreadBlockCountRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-3-15
     */
    public void testDoJudge_15()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_no_return.jvn");

        ThreadBlockCountRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-3-26
     */
    public void testDoJudge_26()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_multi.jvn");

        ThreadBlockCountRule rule = createRule(100);
        rule.doJudge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)101,
                            "executeInstance2InstanceMethodCall");
        assertErrorOccurred(elementList.get(2), rule.threshold, (long)101,
                            "executeInstance2InstanceMethodCall");
    }

    /**
     * [項番] 2-3-27<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・あるJavelinLogElementで実行時例外が発生<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_RuntimeException()
    {
        ThreadBlockCountRule rule = createRule(100);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal1_over.jvn");

        elementList.add(0, null);

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), rule.threshold, (long)101,
                            "executeInstance2InstanceMethodCall");
    }

}
