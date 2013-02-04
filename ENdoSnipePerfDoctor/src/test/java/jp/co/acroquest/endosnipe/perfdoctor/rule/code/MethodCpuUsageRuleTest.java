package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

/**
 * @author tsukano
 */
public class MethodCpuUsageRuleTest extends PerformanceRuleTestCase
{
    /**
     * 閾値を指定してCpuUsageRuleを生成する。<br>
     * @param threshold 閾値
     * @return CpuUsageRule
     */
    private MethodCpuUsageRule createRule(long threshold)
    {
        MethodCpuUsageRule rule = createInstance(MethodCpuUsageRule.class);
        rule.id = "COD.MTRC.METHOD_CPU";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 2-6-1<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・CPU時間が2999。<br>
     * ・閾値が3000。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_01()
    {
        // 準備
        MethodCpuUsageRule rule = createRule(3000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadCpuUsageRuleTest_testDoJudge_2999.jvn");

        // 実行
        rule.doJudge(elementList);

        // 検証
        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-6-2<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・CPU時間が3000。<br>
     * ・閾値が3000。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_02()
    {
        // 準備
        MethodCpuUsageRule rule = createRule(3000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadCpuUsageRuleTest_testDoJudge_3000.jvn");

        // 実行
        rule.doJudge(elementList);

        // 検証
        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        assertEquals(1, errorList.size());

        assertErrorOccurred(elementList.get(0), rule.threshold, (long)3000,
                            "/employee/employeeSearch.html");
    }

    /**
     * [項番] 2-6-3<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・CPU時間が3001。<br>
     * ・閾値が3000。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_03()
    {
        // 準備
        MethodCpuUsageRule rule = createRule(3000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadCpuUsageRuleTest_testDoJudge_3001.jvn");

        // 実行
        rule.doJudge(elementList);

        // 検証
        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        assertEquals(1, errorList.size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)3001,
                            "/employee/employeeSearch.html");
    }

    /**
     * [項番] 2-6-5<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・CPU時間が10。<br>
     * ・閾値が10。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_05()
    {
        MethodCpuUsageRule rule = createRule(10);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadCpuUsageRuleTest_testDoJudge_10.jvn");

        rule.doJudge(elementList);

        assertErrorOccurred(elementList.get(0), rule.threshold, (long)10,
                            "/employee/employeeSearch.html");
    }

    /**
     * [項番] 2-6-10<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・CPU時間の値が数値ではない。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_10()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadCpuUsageRuleTest_testDoJudge_invalid.jvn");

        MethodCpuUsageRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-6-11<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・CPU時間の値が空白。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_11()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadCpuUsageRuleTest_testDoJudge_empty.jvn");

        MethodCpuUsageRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-6-12<br>
     * <br>
     * 異常ログ<br>
     * ログ内にパラメータが無い。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_12()
    {
        MethodCpuUsageRule rule = createRule(5);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadCpuUsageRuleTest_testDoJudge_no_param.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-6-14<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・detailInfoがない。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_14()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadCpuUsageRuleTest_testDoJudge_no_detailInfo.jvn");

        MethodCpuUsageRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-6-15<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・RETURNがない。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_15()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadCpuUsageRuleTest_testDoJudge_no_return.jvn");

        MethodCpuUsageRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-6-26<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・複数のJavelinLogElementで警告が出る。<br>
     */
    public void testDoJudge_26()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadCpuUsageRule_testDoJudge_multi.jvn");

        MethodCpuUsageRule rule = createRule(3000);
        rule.doJudge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), (long)3000, (long)3001,
                            "/employee/employeeSearch.html");
        assertErrorOccurred(elementList.get(1), (long)3000, (long)3001,
                            "/employee/employeeSearch.html");
    }

    /**
     * [項番] 2-6-27<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・あるJavelinLogElementで実行時例外が発生<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_RuntimeException()
    {
        MethodCpuUsageRule rule = createRule(3000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("ThreadCpuUsageRuleTest_testDoJudge_3001.jvn");

        elementList.add(0, null);

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), rule.threshold, (long)3001,
                            "/employee/employeeSearch.html");
    }

}
