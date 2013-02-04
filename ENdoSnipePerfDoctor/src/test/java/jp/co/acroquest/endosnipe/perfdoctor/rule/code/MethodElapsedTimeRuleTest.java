package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

/**
 * メソッドの実処理時間ルール用のテストケース
 */
public class MethodElapsedTimeRuleTest extends PerformanceRuleTestCase
{
    /**
     * 閾値を指定してMethodElapsedTimeRuleを生成する。<br>
     * @param threshold 閾値
     * @return MethodElapsedTimeRule
     */
    private MethodElapsedTimeRule createRule(long threshold)
    {
        MethodElapsedTimeRule rule = createInstance(MethodElapsedTimeRule.class);
        rule.id = "COD.MTRC.METHOD_ELAPSEDTIME";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 2-14-1<br>
     * doJudgeのテスト。<br>
     * ・実処理時間が199<br>
     * ・閾値が200<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th200_val199()
    {
        MethodElapsedTimeRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodElapsedTimeRuleTest_testDoJudge_th200_val199.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-14-2<br>
     * doJudgeのテスト。<br>
     * ・実処理時間が200<br>
     * ・閾値が200<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_th200_val200()
    {
        MethodElapsedTimeRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodElapsedTimeRuleTest_testDoJudge_th200_val200.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (double)200);
    }

    /**
     * [項番] 2-14-3<br>
     * doJudgeのテスト。<br>
     * ・実処理時間が201<br>
     * ・閾値が200<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_th200_val201()
    {
        MethodElapsedTimeRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodElapsedTimeRuleTest_testDoJudge_th200_val201.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (double)201);
    }

    /**
     * [項番] 2-14-5<br>
     * doJudgeのテスト。<br>
     * ・実処理時間が5000<br>
     * ・閾値が5000<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_th5000_val5000()
    {
        MethodElapsedTimeRule rule = createRule(5000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodElapsedTimeRuleTest_testDoJudge_th5000_val5000.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (double)5000);
    }

    /**
     * [項番] 2-14-10<br>
     * doJudgeのテスト。<br>
     * ・実処理時間の値が文字列（Test文字列)<br>
     * ・閾値が200<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th200_valstring()
    {
        MethodElapsedTimeRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodElapsedTimeRuleTest_testDoJudge_th200_valstring.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-14-11<br>
     * doJudgeのテスト。<br>
     * ・実処理時間の値が空白<br>
     * ・閾値が200<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th200_valblank()
    {
        MethodElapsedTimeRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodElapsedTimeRuleTest_testDoJudge_th200_valblank.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-14-12<br>
     * doJudgeのテスト。<br>
     * ・実処理時間の値が存在しない。<br>
     * ・閾値が200<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th200_valnovalue()
    {
        MethodElapsedTimeRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodElapsedTimeRuleTest_testDoJudge_th200_valnovalue.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-14-14<br>
     * doJudgeのテスト。<br>
     * ・ExtraInfoが存在しない。<br>
     * ・閾値が200<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th200_valnoinfotag()
    {
        MethodElapsedTimeRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodElapsedTimeRuleTest_testDoJudge_th200_valnoinfotag.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-14-15<br>
     * doJudgeのテスト。<br>
     * ・Call種別のログが存在しない。<br>
     * ・閾値が200<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th200_valnocall()
    {
        MethodElapsedTimeRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodElapsedTimeRuleTest_testDoJudge_th200_valnocall.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-14-26<br>
     * doJudgeのテスト。<br>
     * ・複数のJavelinLogElementで警告が出る。<br>
     * →警告が複数発生。<br>
     */
    public void testDoJudge_th200_multierror()
    {
        MethodElapsedTimeRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodElapsedTimeRuleTest_testDoJudge_th200_multierror.jvn");

        JavelinParser.initDetailInfo(elementList);
        rule.doJudge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (double)201);
        assertErrorOccurred(elementList.get(4), rule.threshold, (double)201);
    }

    /**
     * [項番] 2-14-27<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・あるJavelinLogElementで実行時例外が発生<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_RuntimeException()
    {
        MethodElapsedTimeRule rule = createRule(200);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("MethodElapsedTimeRuleTest_testDoJudge_th200_val201.jvn");

        elementList.add(0, null);

        JavelinParser.initDetailInfo(elementList);
        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), rule.threshold, (double)201);

    }

}
