package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

/**
 * メソッドの実CPU使用時間ルール用のテストケース<br>
 */
public class MethodPureCpuUsageRuleTest extends PerformanceRuleTestCase
{
    /**
     * 閾値を指定してMethodPureCpuUsageRuleを生成する。<br>
     * @param threshold 閾値
     * @return MethodPureCpuUsageRule
     */
    private MethodPureCpuUsageRule createRule(long threshold)
    {
        MethodPureCpuUsageRule rule = createInstance(MethodPureCpuUsageRule.class);
        rule.id = "COD.MTRC.METHOD_PURE_CPU";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 2-13-1<br>
     * doJudgeのテスト。<br>
     * ・実CPU使用時間が199<br>
     * ・閾値が200<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th200_val199()
    {
        MethodPureCpuUsageRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodPureCpuUsageRuleTest_testDoJudge_th200_val199.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-13-2<br>
     * doJudgeのテスト。<br>
     * ・実CPU使用時間が200<br>
     * ・閾値が200<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_th200_val200()
    {
        MethodPureCpuUsageRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodPureCpuUsageRuleTest_testDoJudge_th200_val200.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)200, "21");
    }

    /**
     * [項番] 2-13-3<br>
     * doJudgeのテスト。<br>
     * ・実CPU使用時間が201<br>
     * ・閾値が200<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_th200_val201()
    {
        MethodPureCpuUsageRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodPureCpuUsageRuleTest_testDoJudge_th200_val201.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)201, "21");
    }

    /**
     * [項番] 2-13-5<br>
     * doJudgeのテスト。<br>
     * ・実CPU使用時間が5000<br>
     * ・閾値が5000<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_th5000_val5000()
    {
        MethodPureCpuUsageRule rule = createRule(5000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodPureCpuUsageRuleTest_testDoJudge_th5000_val5000.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)5000, "21");
    }

    /**
     * [項番] 2-13-10<br>
     * doJudgeのテスト。<br>
     * ・実CPU使用時間の値が文字列（Test文字列)<br>
     * ・閾値が200<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th200_valstring()
    {
        MethodPureCpuUsageRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodPureCpuUsageRuleTest_testDoJudge_th200_valstring.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-13-11<br>
     * doJudgeのテスト。<br>
     * ・実CPU使用時間の値が空白<br>
     * ・閾値が200<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th200_valblank()
    {
        MethodPureCpuUsageRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodPureCpuUsageRuleTest_testDoJudge_th200_valblank.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-13-12<br>
     * doJudgeのテスト。<br>
     * ・CPU使用時間の値が存在しない。<br>
     * ・閾値が200<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th200_valnovalue()
    {
        MethodPureCpuUsageRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodPureCpuUsageRuleTest_testDoJudge_th200_valnovalue.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-13-14<br>
     * doJudgeのテスト。<br>
     * ・JMXInfoが存在しない。<br>
     * ・閾値が200<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th200_valnoinfotag()
    {
        MethodPureCpuUsageRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodPureCpuUsageRuleTest_testDoJudge_th200_valnoinfotag.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-13-15<br>
     * doJudgeのテスト。<br>
     * ・Call種別のログが存在しない。<br>
     * ・閾値が200<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th200_valnocall()
    {
        MethodPureCpuUsageRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodPureCpuUsageRuleTest_testDoJudge_th200_valnocall.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-13-26<br>
     * doJudgeのテスト。<br>
     * ・複数のJavelinLogElementで警告が出る。<br>
     * →警告が複数発生。<br>
     */
    public void testDoJudge_th200_multierror()
    {
        MethodPureCpuUsageRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodPureCpuUsageRuleTest_testDoJudge_th200_multierror.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.judge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)201, "21");
        assertErrorOccurred(elementList.get(4), rule.threshold, (long)201, "21");
    }

    /**
     * [項番] 2-13-27<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・あるJavelinLogElementで実行時例外が発生<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_RuntimeException()
    {
        MethodPureCpuUsageRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodPureCpuUsageRuleTest_testDoJudge_th200_val201.jvn");

        elementList.add(0, null);

        JavelinParser.initDetailInfo(elementList);
        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), rule.threshold, (long)201, "21");
    }

}
