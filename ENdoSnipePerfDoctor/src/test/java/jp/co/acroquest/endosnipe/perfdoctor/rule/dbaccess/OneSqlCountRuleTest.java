package jp.co.acroquest.endosnipe.perfdoctor.rule.dbaccess;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;
import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;
import jp.co.acroquest.endosnipe.perfdoctor.rule.PerformanceRuleFacade;

/**
 * 同一SQLの発行回数ルールのテスト<br>
 * @author tooru
 *
 */
public class OneSqlCountRuleTest extends PerformanceRuleTestCase
{
    /**
     * 閾値を指定してOneSqlCountRuleを生成する。<br>
     * @param threshold 閾値
     * @return OneSqlCountRule
     */
    private OneSqlCountRule createRule(int threshold)
    {
        OneSqlCountRule rule = createInstance(OneSqlCountRule.class);
        rule.id = "DBA.MTRC.ONE_SQL_CNT";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    private OneSqlCountRule createRule(int threshold, String level)
    {
        OneSqlCountRule rule = createInstance(OneSqlCountRule.class);
        rule.id = "DBA.MTRC.ONE_SQL_CNT";
        rule.active = true;
        rule.level = level;
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 3-3-1<br>
     * <br>
     * 判定：閾値チェック<br>
     * 閾値より小さい値を検出→エラーは出力しない。<br>
     * (閾値3に対し、同一のSQLを2回発行)<br>
     */
    public void testDoJudge_01()
    {
        OneSqlCountRule rule = createRule(3);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountRuleTest_testDoJudge_th3_exe2.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-3-2<br>
     * <br>
     * 判定：閾値チェック<br>
     * 閾値と同じ値を検出→エラーを出力。<br>
     * (閾値3に対し、同一のSQLを3回発行)<br>
     */
    public void testDoJudge_02()
    {
        OneSqlCountRule rule = createRule(3);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountRuleTest_testDoJudge_th3_exe3.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(2), 3, 3L, "50", 1, "/employee/employeeSearch.html");
    }

    /**
     * [項番] 3-3-3<br>
     * <br>
     * 判定：閾値チェック<br>
     * 閾値を超えた値を検出→エラーを出力。<br>
     * (閾値3に対し、同一のSQLを4回発行)<br>
     */
    public void testDoJudge_03()
    {
        OneSqlCountRule rule = createRule(3);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountRuleTest_testDoJudge_th3_exe4.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(2), 3, 4L, "50", 1, "/employee/employeeSearch.html");
    }

    /**
     * [項番] 3-3-5<br>
     * <br>
     * 判定：閾値チェック<br>
     * 閾値と同じ値を検出→エラーを出力。<br>
     * (閾値1に対し、同一のSQLを1回発行)<br>
     */
    public void testDoJudge_05()
    {
        OneSqlCountRule rule = createRule(1);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountRuleTest_testDoJudge_1.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 1, 1L, "50", 1, "/employee/employeeSearch.html");
    }

    /**
     * [項番] 3-3-9<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・2種類のSQL文。<br>
     * ・発行回数はそれぞれ2回ずつ。<br>
     * ・閾値は3。<br>
     * →警告は発生しない。<br>
     */
    public void testDoJudge_09()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountRuleTest_testDoJudge_2sqls_times2.jvn");

        OneSqlCountRule rule = createRule(3);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }

    }

    /**
     * [項番] 3-3-13<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・[SQL]というタグがない。<br>
     * →警告を表示しない。<br>
     */
    public void testDoJudge_13()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountRuleTest_testDoJudge_no_sql.jvn");

        OneSqlCountRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-3-15<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・CALLがない。<br>
     * →警告を表示しない。<br>
     */
    public void testDoJudge_15()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountRuleTest_testDoJudge_no_call.jvn");

        OneSqlCountRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-3-18<br>
     * <br>
     * 異常データ<br>
     * SQL文になっていない→文字列が同一であれば、同じ「SQL文」であると判断し処理する。<br>
     * (閾値3に対し、同一のSQLを4回発行)<br>
     */
    public void testDoJudge_18()
    {
        OneSqlCountRule rule = createRule(3);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountRuleTest_testDoJudge_not_sql.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(2), 3, 4L, "0", 1, "/employee/employeeSearch.html");
    }

    /**
     * [項番] 3-3-26<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・複数のJavelinLogElementで警告が出る。<br>
     */
    public void testDoJudge_26()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountRuleTest_testDoJudge_multi.jvn");

        OneSqlCountRule rule = createRule(3);
        rule.doJudge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(2), 3, 4L, "50", 1, "/employee/employeeSearch.html");
        assertErrorOccurred(elementList.get(6), 3, 4L, "50", 1, "/employee/employeeSearch.html");
    }

    /**
     * [項番] 3-3-27<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・あるJavelinLogElementで実行時例外が発生<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_RuntimeException()
    {
        OneSqlCountRule rule = createRule(3);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountRuleTest_testDoJudge_th3_exe4.jvn");

        elementList.add(0, null);

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(3), 3, 4L, "50", 1, "/employee/employeeSearch.html");

    }

    /**
     * SCR対応<br>
     * [項番] 1-3-7
     * ERRORが発生。
     * WARN、INFOが発生しないことを確認する。<br>
     */
    public void testJudge_RuleFacade_Error()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountRuleTest_testJudge_RuleFacade_Error.jvn");

        OneSqlCountRule errorRule = createRule(5, "ERROR");
        OneSqlCountRule warnRule = createRule(3, "WARN");
        OneSqlCountRule infoRule = createRule(2, "INFO");

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
     * [項番] 1-3-8
     * WARNが発生。
     * ERROR、INFOが発生しないことを確認する。<br>
     */
    public void testJudge_RuleFacade_Warn()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountRuleTest_testJudge_RuleFacade_Warn.jvn");

        OneSqlCountRule errorRule = createRule(5, "ERROR");
        OneSqlCountRule warnRule = createRule(3, "WARN");
        OneSqlCountRule infoRule = createRule(2, "INFO");

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
     * [項番] 1-3-9
     * INFOが発生。
     * ERROR、WARNが発生しないことを確認する。<br>
     */
    public void testJudge_RuleFacade_Info()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountRuleTest_testJudge_RuleFacade_Info.jvn");

        OneSqlCountRule errorRule = createRule(5, "ERROR");
        OneSqlCountRule warnRule = createRule(3, "WARN");
        OneSqlCountRule infoRule = createRule(2, "INFO");

        PerformanceRuleFacade facade = new PerformanceRuleFacade();
        facade.setErrorRule(errorRule);
        facade.setWarnRule(warnRule);
        facade.setInfoRule(infoRule);

        List<WarningUnit> list = facade.judge(elementList);

        assertEquals(1, list.size());
        assertEquals("INFO", list.get(0).getLevel());
    }
}
