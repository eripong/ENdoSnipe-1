package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

public class ThreadBlockTimeRuleTest extends PerformanceRuleTestCase
{

    /**
     * 閾値を指定してThreadBlockTimeRuleを生成する。<br>
     * @param threshold 閾値
     * @return ThreadBlockTimeRule
     */
    private ThreadBlockTimeRule createRule(long threshold)
    {
        ThreadBlockTimeRule rule = createInstance(ThreadBlockTimeRule.class);
        rule.id = "COD.THRD.WAIT_TIME";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 2-4-1
     */
    public void testDoJudge_01()
    {
        ThreadBlockTimeRule rule = createRule(1000);
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
     * [項番] 2-4-2
     */
    public void testDoJudge_02()
    {
        ThreadBlockTimeRule rule = createRule(1000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal1_on.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)1000,
                            "executeInstance2InstanceMethodCall");
    }

    /**
     * [項番] 2-4-3
     */
    public void testDoJudge_03()
    {
        ThreadBlockTimeRule rule = createRule(1000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal1_over.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)1001,
                            "executeInstance2InstanceMethodCall");
    }

    /**
     * [項番] 2-4-5
     */
    public void testDoJudge_05()
    {
        ThreadBlockTimeRule rule = createRule(100);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal2_on.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)100,
                            "executeInstance2InstanceMethodCall");
    }

    /**
     * [項番] 2-4-10
     */
    public void testDoJudge_10()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_not_number.jvn");

        ThreadBlockTimeRule rule = createRule(100);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-4-11
     */
    public void testDoJudge_11()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_empty.jvn");

        ThreadBlockTimeRule rule = createRule(100);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-4-12
     */
    public void testDoJudge_12()
    {
        ThreadBlockTimeRule rule = createRule(100);
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
     * [項番] 2-4-14
     */
    public void testDoJudge_14()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_no_JMXInfo.jvn");

        ThreadBlockTimeRule rule = createRule(100);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-4-15
     */
    public void testDoJudge_15()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_no_return.jvn");

        ThreadBlockTimeRule rule = createRule(100);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-4-26
     */
    public void testDoJudge_26()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_multi.jvn");

        ThreadBlockTimeRule rule = createRule(1000);
        rule.doJudge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)1001,
                            "executeInstance2InstanceMethodCall");
        assertErrorOccurred(elementList.get(2), rule.threshold, (long)1001,
                            "executeInstance2InstanceMethodCall");
    }

    /**
     * [項番] 2-4-27<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・あるJavelinLogElementで実行時例外が発生<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_RuntimeException()
    {
        ThreadBlockTimeRule rule = createRule(1000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal1_over.jvn");

        elementList.add(0, null);

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), rule.threshold, (long)1001,
                            "executeInstance2InstanceMethodCall");

    }

}
