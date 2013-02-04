package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

public class ThreadWaitCountRuleTest extends PerformanceRuleTestCase
{

    /**
     * 閾値を指定してThreadWaitCountRuleを生成する。
     * @param threshold 閾値<br>
     * @return ThreadWaitCountRule<br>
     */
    private ThreadWaitCountRule createRule(long threshold)
    {
        ThreadWaitCountRule rule = createInstance(ThreadWaitCountRule.class);
        rule.id = "COD.THRD.WAIT_CNT";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 2-1-1<br>
     */
    public void testDoJudge_01()
    {
        ThreadWaitCountRule rule = createRule(100);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal1_under.jvn");

        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-1-2<br>
     */
    public void testDoJudge_02()
    {
        ThreadWaitCountRule rule = createRule(100);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal1_on.jvn");

        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)100,
                            "executeInstance2InstanceMethodCall");
    }

    /**
     * [項番] 2-1-3<br>
     */
    public void testDoJudge_03()
    {
        ThreadWaitCountRule rule = createRule(100);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal1_over.jvn");

        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)101,
                            "executeInstance2InstanceMethodCall");
    }

    /**
     * [項番] 2-1-5<br>
     */
    public void testDoJudge_05()
    {
        ThreadWaitCountRule rule = createRule(5);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal2_on.jvn");

        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)5,
                            "executeInstance2InstanceMethodCall");
    }

    /**
     * [項番] 2-1-10<br>
     */
    public void testDoJudge_10()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_not_number.jvn");

        ThreadWaitCountRule rule = createRule(5);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-1-11<br>
     */
    public void testDoJudge_11()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_empty.jvn");

        ThreadWaitCountRule rule = createRule(5);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-1-12<br>
     */
    public void testDoJudge_12()
    {
        ThreadWaitCountRule rule = createRule(5);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_no_param.jvn");

        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-1-14<br>
     */
    public void testDoJudge_14()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_no_JMXInfo.jvn");

        ThreadWaitCountRule rule = createRule(5);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-1-15<br>
     */
    public void testDoJudge_15()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_no_return.jvn");

        ThreadWaitCountRule rule = createRule(5);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-1-26<br>
     */
    public void testDoJudge_26()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_multi.jvn");

        ThreadWaitCountRule rule = createRule(100);
        rule.judge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)101,
                            "executeInstance2InstanceMethodCall");
        assertErrorOccurred(elementList.get(2), rule.threshold, (long)101,
                            "executeInstance2InstanceMethodCall");
    }

    /**
     * [項番] 2-1-27<br>
     * <br>
     * judgeのテスト。<br>
     * ・あるJavelinLogElementで実行時例外が発生<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_RuntimeException()
    {
        ThreadWaitCountRule rule = createRule(100);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadXXXRuleTest_testDoJudge_normal1_over.jvn");

        JavelinLogElement element = new JavelinLogElement();
        element.setDetailInfo("", null);
        elementList.add(0, element);

        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), rule.threshold, (long)101,
                            "executeInstance2InstanceMethodCall");

    }

}
