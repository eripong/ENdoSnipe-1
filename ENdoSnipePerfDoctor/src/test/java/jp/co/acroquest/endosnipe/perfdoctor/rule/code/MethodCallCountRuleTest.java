package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;
import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;
import jp.co.acroquest.endosnipe.perfdoctor.rule.PerformanceRuleFacade;

/**
 * メソッドの呼び出し回数ルールのテスト<br>
 * @author tooru
 *
 */
public class MethodCallCountRuleTest extends PerformanceRuleTestCase
{
    /**
     * 閾値を指定してCodeMetricsRuleを生成する。<br>
     * @param threshold 閾値
     * @return CodeMetricsRule
     */
    private MethodCallCountRule createRule(int threshold)
    {
        MethodCallCountRule rule = createInstance(MethodCallCountRule.class);
        rule.id = "COD.MTRC.METHOD_CNT";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * 閾値を指定してCodeMetricsRuleを生成する(警告レベル指定付き)。<br>
     * @param threshold 閾値
     * @param level 警告レベル
     * @return CodeMetricsRule
     */
    private MethodCallCountRule createRule(int threshold, String level)
    {
        MethodCallCountRule rule = createInstance(MethodCallCountRule.class);
        rule.id = "COD.MTRC.METHOD_CNT";
        rule.active = true;
        rule.level = level;
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 2-7-1<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・呼び出し回数が9。<br>
     * ・閾値が10。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_01()
    {
        MethodCallCountRule rule = createRule(10);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("CodeMetricsRuleTest_testDoJudge_th10_call9.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-7-2<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・呼び出し回数が10。<br>
     * ・閾値が10。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_02()
    {
        MethodCallCountRule rule = createRule(10);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("CodeMetricsRuleTest_testDoJudge_th10_call10.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(18), rule.threshold, 10);
    }

    /**
     * [項番] 2-7-3<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・呼び出し回数が11。<br>
     * ・閾値が10。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_03()
    {
        MethodCallCountRule rule = createRule(10);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("CodeMetricsRuleTest_testDoJudge_th10_call11.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(18), rule.threshold, 11);
    }

    /**
     * [項番] 2-7-5<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・呼び出し回数が3。<br>
     * ・閾値が3。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_05()
    {
        MethodCallCountRule rule = createRule(3);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodCallCountRuleTest_testDoJudge_03.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(4), rule.threshold, 3);
    }

    /**
     * [項番] 2-7-9<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・8回呼ばれるメソッドと、3回呼ばれるメソッドが存在。<br>
     * ・閾値が10。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_09()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodCallCountRuleTest_testDoJudge_8_and_3.jvn");

        MethodCallCountRule rule = createRule(10);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-7-11<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・メソッド名が空白。<br>
     * ・クラス名、スレッド名は同一。<br>
     * ・合計回数は閾値を超える。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_11()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodCallCountRuleTest_testDoJudge_empty.jvn");

        MethodCallCountRule rule = createRule(10);
        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(18), rule.threshold, 11);
    }

    /**
     * [項番] 2-7-15<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・CALLがない。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_15()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodCallCountRuleTest_testDoJudge_no_call.jvn");

        MethodCallCountRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-7-23<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・同一のクラス。<br>
     * ・異なるメソッド。<br>
     * ・合計回数は閾値を超える。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_23()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("CodeMetricsRuleTest_testDoJudge_methods_in_same_class.jvn");

        MethodCallCountRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-7-24<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・同一のメソッド名。<br>
     * ・異なるクラス。<br>
     * ・合計回数は閾値を超える。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_24()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("CodeMetricsRuleTest_testDoJudge_same_method_name.jvn");

        MethodCallCountRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-7-25<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・同一のメソッド。<br>
     * ・異なるスレッド。<br>
     * ・合計回数は閾値を超える。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_25()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("CodeMetricsRuleTest_testDoJudge_different_thread.jvn");

        MethodCallCountRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-7-26<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・複数のJavelinLogElementで警告が出る。<br>
     */
    public void testDoJudge_26()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodCallCountRuleTest_testDoJudge_multi.jvn");

        MethodCallCountRule rule = createRule(10);
        rule.doJudge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(18), 10, 11);
        assertErrorOccurred(elementList.get(40), 10, 11);
    }

    /**
     * [項番] 2-7-27<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・あるJavelinLogElementで実行時例外が発生<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_RuntimeException()
    {
        MethodCallCountRule rule = createRule(10);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("CodeMetricsRuleTest_testDoJudge_th10_call11.jvn");

        elementList.add(0, null);

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(19), rule.threshold, 11);
    }

    /**
     * SCR対応<br>
     * [項番] 1-3-1
     * ERRORが発生。
     * WARN、INFOが発生しないことを確認する。<br>
     */
    public void testJudge_RuleFacade_Error()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodCallCountRuleTest_testJudge_RuleFacade_Error.jvn");

        MethodCallCountRule errorRule = createRule(5, "ERROR");
        MethodCallCountRule warnRule = createRule(3, "WARN");
        MethodCallCountRule infoRule = createRule(1, "INFO");

        PerformanceRuleFacade facade = new PerformanceRuleFacade();
        facade.setErrorRule(errorRule);
        facade.setWarnRule(warnRule);
        facade.setInfoRule(infoRule);

        List<WarningUnit> list = facade.judge(elementList);

        assertEquals(1, list.size());
        assertEquals("ERROR", list.get(0).getLevel());
    }

    /**
     * SCR対応<br>
     * [項番] 1-3-2
     * WARNが発生。
     * ERROR、INFOが発生しないことを確認する。<br>
     */
    public void testJudge_RuleFacade_Warn()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodCallCountRuleTest_testJudge_RuleFacade_Warn.jvn");

        MethodCallCountRule errorRule = createRule(5, "ERROR");
        MethodCallCountRule warnRule = createRule(3, "WARN");
        MethodCallCountRule infoRule = createRule(1, "INFO");

        PerformanceRuleFacade facade = new PerformanceRuleFacade();
        facade.setErrorRule(errorRule);
        facade.setWarnRule(warnRule);
        facade.setInfoRule(infoRule);

        List<WarningUnit> list = facade.judge(elementList);

        assertEquals(1, list.size());
        assertEquals("WARN", list.get(0).getLevel());
    }

    /**
     * SCR対応<br>
     * [項番] 1-3-3
     * INFOが発生。
     * ERROR、WARNが発生しないことを確認する。<br>
     */
    public void testJudge_RuleFacade_Info()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodCallCountRuleTest_testJudge_RuleFacade_Info.jvn");

        MethodCallCountRule errorRule = createRule(5, "ERROR");
        MethodCallCountRule warnRule = createRule(3, "WARN");
        MethodCallCountRule infoRule = createRule(1, "INFO");

        PerformanceRuleFacade facade = new PerformanceRuleFacade();
        facade.setErrorRule(errorRule);
        facade.setWarnRule(warnRule);
        facade.setInfoRule(infoRule);

        List<WarningUnit> list = facade.judge(elementList);

        assertEquals(1, list.size());
        assertEquals("INFO", list.get(0).getLevel());
    }

    /**
     * SCR対応<br>
     * [項番] 1-5-1
     * INFOが発生。
     * ERROR、WARNが発生しないことを確認する。<br>
     */
    public void testJudge_RuleFacade_IgnoreSQL()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodCallCountRuleTest_testJudge_RuleFacade_IgnoreSQL.jvn");

        MethodCallCountRule errorRule = createRule(5, "ERROR");
        MethodCallCountRule warnRule = createRule(3, "WARN");
        MethodCallCountRule infoRule = createRule(1, "INFO");

        PerformanceRuleFacade facade = new PerformanceRuleFacade();
        facade.setErrorRule(errorRule);
        facade.setWarnRule(warnRule);
        facade.setInfoRule(infoRule);

        List<WarningUnit> list = facade.judge(elementList);

        assertEquals(1, list.size());
        assertEquals("INFO", list.get(0).getLevel());
    }
}
