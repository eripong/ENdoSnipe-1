package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

public class ThreadWaitTimeRuleTest extends PerformanceRuleTestCase
{

    /**
     * 閾値を指定してThreadWaitTimeRuleを生成する。<br>
     * @param threshold 閾値
     * @return ThreadWaitTimeRule
     */
    private ThreadWaitTimeRule createRule(long threshold)
    {
        ThreadWaitTimeRule rule = createInstance(ThreadWaitTimeRule.class);
        rule.id = "COD.THRD.WAIT_TIME";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 2-2-1
     */
    public void testDoJudge_01()
    {
        ThreadWaitTimeRule rule = createRule(1000);
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
     * [項番] 2-2-2
     */
    public void testDoJudge_02()
    {
        ThreadWaitTimeRule rule = createRule(1000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal1_on.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)1000,
                            "executeInstance2InstanceMethodCall");
    }

    /**
     * [項番] 2-2-3
     */
    public void testDoJudge_03()
    {
        ThreadWaitTimeRule rule = createRule(1000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal1_over.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)1001,
                            "executeInstance2InstanceMethodCall");
    }

    /**
     * [項番] 2-2-5
     */
    public void testDoJudge_05()
    {
        ThreadWaitTimeRule rule = createRule(100);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal2_on.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)100,
                            "executeInstance2InstanceMethodCall");
    }

    /**
     * [項番] 2-2-10
     */
    public void testDoJudge_10()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_not_number.jvn");

        ThreadWaitTimeRule rule = createRule(100);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-2-11
     */
    public void testDoJudge_11()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_empty.jvn");

        ThreadWaitTimeRule rule = createRule(100);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-2-12
     */
    public void testDoJudge_12()
    {
        ThreadWaitTimeRule rule = createRule(100);
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
     * [項番] 2-2-14
     */
    public void testDoJudge_14()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_no_JMXInfo.jvn");

        ThreadWaitTimeRule rule = createRule(100);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-2-15
     */
    public void testDoJudge_15()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_no_return.jvn");

        ThreadWaitTimeRule rule = createRule(100);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-2-26
     */
    public void testDoJudge_26()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_multi.jvn");

        ThreadWaitTimeRule rule = createRule(1000);
        rule.doJudge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)1001,
                            "executeInstance2InstanceMethodCall");
        assertErrorOccurred(elementList.get(2), rule.threshold, (long)1001,
                            "executeInstance2InstanceMethodCall");
    }

    /**
     * [項番] 2-2-27
     * 
     * doJudgeのテスト。<br>
     * ・あるJavelinLogElementで実行時例外が発生<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_RuntimeException()
    {
        ThreadWaitTimeRule rule = createRule(1000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal1_over.jvn");

        elementList.add(0, null);

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), rule.threshold, (long)1001,
                            "executeInstance2InstanceMethodCall");

    }

}
