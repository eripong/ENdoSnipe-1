package jp.co.acroquest.endosnipe.perfdoctor.rule.dbaccess;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;
import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;

/**
 * MethodTotalElapsedTimeRuleのテストクラス。
 * @author tooru
 *
 */
public class OneSqlExecTimeRuleTest extends PerformanceRuleTestCase
{
    /**
     * 閾値を指定してMethodTotalElapsedTimeRuleTestを生成する。<br>
     * @param threshold 閾値
     * @return MethodTotalElapsedTimeRuleTest
     */
    private OneSqlExecTimeRule createRule(long threshold)
    {
        OneSqlExecTimeRule rule = createInstance(OneSqlExecTimeRule.class);
        rule.id = "DBA.MTRC.ONE_SQL_EXEC_TIME";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 2-2-1<br>
     * <br>
     * doJudgeのテスト。<br>
     */
    public void testDoJudge_th100000_call99999()
    {
        OneSqlExecTimeRule rule = createRule(100000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlExecTimeRuleTest_testDoJudge_th100000_call99999.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        assertEquals(0, errorListSize);
    }

    /**
     * [項番] 2-2-2<br>
     * <br>
     * doJudgeのテスト。<br>
     */
    public void testDoJudge_th100000_call100000()
    {
        OneSqlExecTimeRule rule = createRule(100000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlExecTimeRuleTest_testDoJudge_th100000_call100000.jvn");

        JavelinParser.initDetailInfo(elementList);
        List<WarningUnit> list = rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, 100000L, 1,
                            "/employee/employeeSearch.html");
        assertEquals(
                     "同一SQLの総実行時間が閾値を超えています。(閾値:100,000msec、検出値:100,000msec、引数パターン数：1、スレッド:/employee/employeeSearch.html)",
                     list.get(0).getDescription());
    }

    /**
     * [項番] 2-2-3<br>
     * <br>
     * doJudgeのテスト。<br>
     */
    public void testDoJudge_th100000_call100001()
    {
        OneSqlExecTimeRule rule = createRule(100000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlExecTimeRuleTest_testDoJudge_th100000_call100001.jvn");

        JavelinParser.initDetailInfo(elementList);
        List<WarningUnit> list = rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, 100001L, 1,
                            "/employee/employeeSearch.html");
        assertEquals(
                     "同一SQLの総実行時間が閾値を超えています。(閾値:100,000msec、検出値:100,001msec、引数パターン数：1、スレッド:/employee/employeeSearch.html)",
                     list.get(0).getDescription());
    }

    /**
     * [項番] 2-2-4<br>
     * <br>
     * doJudgeのテスト。<br>
     */
    public void testDoJudge_th5000_call5000()
    {
        OneSqlExecTimeRule rule = createRule(5000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlExecTimeRuleTest_testDoJudge_th5000_call5000.jvn");

        JavelinParser.initDetailInfo(elementList);
        List<WarningUnit> list = rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, 5000L, 1,
                            "/employee/employeeSearch.html");
        assertEquals(
                     "同一SQLの総実行時間が閾値を超えています。(閾値:5,000msec、検出値:5,000msec、引数パターン数：1、スレッド:/employee/employeeSearch.html)",
                     list.get(0).getDescription());
    }

    /**
     * [項番] 2-2-5<br>
     * <br>
     * doJudgeのテスト。<br>

     */
    public void testDoJudge_40000_times_4()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlExecTimeRuleTest_testDoJudge_40000_times_4.jvn");
        OneSqlExecTimeRule rule = createRule(100000);

        JavelinParser.initDetailInfo(elementList);
        List<WarningUnit> list = rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(2), rule.threshold, 160000L, 1,
                            "/employee/employeeSearch.html");
        assertEquals(
                     "同一SQLの総実行時間が閾値を超えています。(閾値:100,000msec、検出値:160,000msec、引数パターン数：1、スレッド:/employee/employeeSearch.html)",
                     list.get(0).getDescription());
    }

    /**
     * [項番] 2-2-6<br>
     * <br>
     * doJudgeのテスト。<br>
     */
    public void testDoJudge_empty_SQL()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlExecTimeRuleTest_testDoJudge_empty_SQL.jvn");
        OneSqlExecTimeRule rule = createRule(100000);

        JavelinParser.initDetailInfo(elementList);
        List<WarningUnit> list = rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, 100000L, 1,
                            "/employee/employeeSearch.html");
        assertEquals(
                     "同一SQLの総実行時間が閾値を超えています。(閾値:100,000msec、検出値:100,000msec、引数パターン数：1、スレッド:/employee/employeeSearch.html)",
                     list.get(0).getDescription());
    }

    /**
     * [項番] 2-2-7<br>
     * <br>
     * doJudgeのテスト。<br>
     */
    public void testDoJudge_empty_Time()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlExecTimeRuleTest_testDoJudge_empty_Time.jvn");
        OneSqlExecTimeRule rule = createRule(100000);

        JavelinParser.initDetailInfo(elementList);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        assertEquals(0, errorListSize);
    }

    /**
     * [項番] 2-2-8<br>
     * <br>
     * doJudgeのテスト。<br>
     */
    public void testDoJudge_no_call()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlExecTimeRuleTest_testDoJudge_no_call.jvn");
        OneSqlExecTimeRule rule = createRule(100000);

        JavelinParser.initDetailInfo(elementList);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        assertEquals(0, errorListSize);
    }

    /**
     * [項番] 2-2-9<br>
     * <br>
     * doJudgeのテスト。<br>
     */
    public void testDoJudge_different_thread()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlExecTimeRuleTest_testDoJudge_different_thread1.jvn");
        List<JavelinLogElement> elementList2 =
                createJavelinLogElement("OneSqlExecTimeRuleTest_testDoJudge_different_thread2.jvn");
        elementList.addAll(elementList2);
        OneSqlExecTimeRule rule = createRule(100000);

        JavelinParser.initDetailInfo(elementList);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        assertEquals(0, errorListSize);
    }

    /**
     * [項番] 3-1-10<br>
     * <br>
     * doJudgeのテスト。<br>
     */
    public void testDoJudge_different_thread_multi()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlExecTimeRuleTest_testDoJudge_different_thread_multi1.jvn");
        List<JavelinLogElement> elementList2 =
                createJavelinLogElement("OneSqlExecTimeRuleTest_testDoJudge_different_thread_multi2.jvn");
        List<JavelinLogElement> elementList3 =
                createJavelinLogElement("OneSqlExecTimeRuleTest_testDoJudge_different_thread_multi3.jvn");
        List<JavelinLogElement> elementList4 =
                createJavelinLogElement("OneSqlExecTimeRuleTest_testDoJudge_different_thread_multi4.jvn");
        List<JavelinLogElement> elementList5 =
                createJavelinLogElement("OneSqlExecTimeRuleTest_testDoJudge_different_thread_multi5.jvn");
        elementList.addAll(elementList2);
        elementList.addAll(elementList3);
        elementList.addAll(elementList4);
        elementList.addAll(elementList5);
        OneSqlExecTimeRule rule = createRule(100000);

        JavelinParser.initDetailInfo(elementList);
        List<WarningUnit> list = rule.judge(elementList);

        assertEquals(5, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, 100001L, 1,
                            "/employee/employeeSearch1.html");
        assertErrorOccurred(elementList.get(1), rule.threshold, 100002L, 1,
                            "/employee/employeeSearch2.html");
        assertErrorOccurred(elementList.get(2), rule.threshold, 100003L, 1,
                            "/employee/employeeSearch3.html");
        assertErrorOccurred(elementList.get(3), rule.threshold, 100004L, 1,
                            "/employee/employeeSearch4.html");
        assertErrorOccurred(elementList.get(4), rule.threshold, 100005L, 1,
                            "/employee/employeeSearch5.html");

        sortWarningUnit(list);
        assertEquals(
                     "同一SQLの総実行時間が閾値を超えています。(閾値:100,000msec、検出値:100,001msec、引数パターン数：1、スレッド:/employee/employeeSearch1.html)",
                     list.get(0).getDescription());
        assertEquals(
                     "同一SQLの総実行時間が閾値を超えています。(閾値:100,000msec、検出値:100,002msec、引数パターン数：1、スレッド:/employee/employeeSearch2.html)",
                     list.get(1).getDescription());
        assertEquals(
                     "同一SQLの総実行時間が閾値を超えています。(閾値:100,000msec、検出値:100,003msec、引数パターン数：1、スレッド:/employee/employeeSearch3.html)",
                     list.get(2).getDescription());
        assertEquals(
                     "同一SQLの総実行時間が閾値を超えています。(閾値:100,000msec、検出値:100,004msec、引数パターン数：1、スレッド:/employee/employeeSearch4.html)",
                     list.get(3).getDescription());
        assertEquals(
                     "同一SQLの総実行時間が閾値を超えています。(閾値:100,000msec、検出値:100,005msec、引数パターン数：1、スレッド:/employee/employeeSearch5.html)",
                     list.get(4).getDescription());
    }

    /**
     * [項番] 2-2-11<br>
     * <br>
     * doJudgeのテスト。<br>
     */
    public void testDoJudge_two_sqls()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlExecTimeRuleTest_testDoJudge_two_sqls.jvn");
        OneSqlExecTimeRule rule = createRule(100000);

        JavelinParser.initDetailInfo(elementList);
        List<WarningUnit> list = rule.judge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(2), rule.threshold, 120000L, 1,
                            "/employee/employeeSearch.html");
        assertEquals(
                     "同一SQLの総実行時間が閾値を超えています。(閾値:100,000msec、検出値:120,000msec、引数パターン数：1、スレッド:/employee/employeeSearch.html)",
                     list.get(1).getDescription());
        assertErrorOccurred(elementList.get(3), rule.threshold, 140000L, 1,
                            "/employee/employeeSearch.html");
        assertEquals(
                     "同一SQLの総実行時間が閾値を超えています。(閾値:100,000msec、検出値:140,000msec、引数パターン数：1、スレッド:/employee/employeeSearch.html)",
                     list.get(0).getDescription());
    }
}
