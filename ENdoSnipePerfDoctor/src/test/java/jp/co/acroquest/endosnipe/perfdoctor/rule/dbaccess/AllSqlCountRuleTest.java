package jp.co.acroquest.endosnipe.perfdoctor.rule.dbaccess;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;
import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;
import jp.co.acroquest.endosnipe.perfdoctor.rule.PerformanceRuleFacade;

/**
 * {@link AllSqlCountRule} 用のテストクラス。
 * 
 * @author S.Kimura
 *
 */
public class AllSqlCountRuleTest extends PerformanceRuleTestCase
{
    /**
     * {@link AllSqlCountRule} を生成する。
     * 
     * @param threshold 閾値
     * @return {@link AllSqlCountRule} オブジェクト
     */
    private AllSqlCountRule createRule(int threshold)
    {
        AllSqlCountRule rule = createInstance(AllSqlCountRule.class);
        rule.id = "DBA.MTRC.ALL_SQL_CNT";
        rule.active = true;
        rule.level = "WARN";
        rule.threshold = threshold;
        return rule;
    }

    /**
     * {@link AllSqlCountRule} を生成する(警告レベルも設定できる)。
     * 
     * @param threshold 閾値
     * @param level 警告レベル
     * @return {@link AllSqlCountRule} オブジェクト
     */
    private AllSqlCountRule createRule(int threshold, String level)
    {
        AllSqlCountRule rule = createInstance(AllSqlCountRule.class);
        rule.id = "DBA.MTRC.ALL_SQL_CNT";
        rule.active = true;
        rule.level = level;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 3-2-1<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・SQLの発行回数が9。<br>
     * ・閾値が10。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_01()
    {
        AllSqlCountRule rule = createRule(10);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("AllSqlCountRuleTest_testDoJudge_09.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-2-2<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・SQLの発行回数が10。<br>
     * ・閾値が10。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_02()
    {
        AllSqlCountRule rule = createRule(10);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("AllSqlCountRuleTest_testDoJudge_10.jvn");

        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(9), 10, 10, "/employee/employeeSearch.html");
    }

    /**
     * [項番] 3-2-3<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・SQLの発行回数が11。<br>
     * ・閾値が10。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_03()
    {
        AllSqlCountRule rule = createRule(10);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("AllSqlCountRuleTest_testDoJudge_11.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(9), 10, 11, "/employee/employeeSearch.html");
    }

    /**
     * [項番] 3-2-5<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・SQLの発行回数が3。<br>
     * ・閾値が3。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_05()
    {
        AllSqlCountRule rule = createRule(3);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("AllSqlCountRuleTest_testDoJudge_03.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(2), 3, 3, "/employee/employeeSearch.html");
    }

    /**
     * [項番] 3-2-13<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・[SQL]というタグがない。<br>
     * →警告を表示しない。<br>
     */
    public void testDoJudge_13()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("AllSqlCountRuleTest_testDoJudge_no_sql.jvn");

        AllSqlCountRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-2-14<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・detailInfoがない。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_14()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("AllSqlCountRuleTest_testDoJudge_no_detailInfo.jvn");

        AllSqlCountRule rule = createRule(5);
        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(4), 5, 6, "main@java.lang.Thread@dda25b");
    }

    /**
     * [項番] 3-2-15<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・CALLがない。<br>
     * →警告を表示しない。<br>
     */
    public void testDoJudge_15()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("AllSqlCountRuleTest_testDoJudge_no_call.jvn");

        AllSqlCountRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-2-27<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・あるJavelinLogElementで実行時例外が発生<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_RuntimeException()
    {
        AllSqlCountRule rule = createRule(10);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("AllSqlCountRuleTest_testDoJudge_11.jvn");

        elementList.add(0, null);

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(10), 10, 11, "/employee/employeeSearch.html");
    }

    /**
     * SCR対応<br>
     * [項番] 1-3-4
     * ERRORが発生。
     * WARN、INFOが発生しないことを確認する。<br>
     */
    public void testJudge_RuleFacade_Error()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("AllSqlCountRuleTest_testJudge_RuleFacade_Error.jvn");

        AllSqlCountRule errorRule = createRule(5, "ERROR");
        AllSqlCountRule warnRule = createRule(3, "WARN");
        AllSqlCountRule infoRule = createRule(1, "INFO");

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
     * [項番] 1-3-5
     * WARNが発生。
     * ERROR、INFOが発生しないことを確認する。<br>
     */
    public void testJudge_RuleFacade_Warn()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("AllSqlCountRuleTest_testJudge_RuleFacade_Warn.jvn");

        AllSqlCountRule errorRule = createRule(5, "ERROR");
        AllSqlCountRule warnRule = createRule(3, "WARN");
        AllSqlCountRule infoRule = createRule(1, "INFO");

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
     * [項番] 1-3-6
     * INFOが発生。
     * ERROR、WARNが発生しないことを確認する。<br>
     */
    public void testJudge_RuleFacade_Info()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("AllSqlCountRuleTest_testJudge_RuleFacade_Info.jvn");

        AllSqlCountRule errorRule = createRule(5, "ERROR");
        AllSqlCountRule warnRule = createRule(3, "WARN");
        AllSqlCountRule infoRule = createRule(1, "INFO");

        PerformanceRuleFacade facade = new PerformanceRuleFacade();
        facade.setErrorRule(errorRule);
        facade.setWarnRule(warnRule);
        facade.setInfoRule(infoRule);

        List<WarningUnit> list = facade.judge(elementList);

        assertEquals(1, list.size());
        assertEquals("INFO", list.get(0).getLevel());
    }

}
