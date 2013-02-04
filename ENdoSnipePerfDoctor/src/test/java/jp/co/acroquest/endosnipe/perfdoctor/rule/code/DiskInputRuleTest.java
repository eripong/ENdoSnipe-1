package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

/**
 * ディスク入力量判定ルール用のテストケース<br>
 * <br>
 * @author S.Kimura
 */
public class DiskInputRuleTest extends PerformanceRuleTestCase
{
    /**
     * 閾値を指定してDiskInputRuleを生成する。<br>
     * @param threshold 閾値
     * @return DiskInputRule
     */
    private DiskInputRule createRule(long threshold)
    {
        DiskInputRule rule = createInstance(DiskInputRule.class);
        rule.id = "COD.IO.DISK_INPUT";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 2-9-1<br>
     * doJudgeのテスト。<br>
     * ・ディスク入力量が999999<br>
     * ・閾値が1000000<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th1000000_val999999()
    {
        DiskInputRule rule = createRule(1000000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DiskInputRuleTest_testDoJudge_th1000000_val999999.jvn");

        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-9-2<br>
     * doJudgeのテスト。<br>
     * ・ディスク入力量が1000000<br>
     * ・閾値が1000000<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_th1000000_val1000000()
    {
        DiskInputRule rule = createRule(1000000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DiskInputRuleTest_testDoJudge_th1000000_val1000000.jvn");

        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)1000000);
    }

    /**
     * [項番] 2-9-3<br>
     * doJudgeのテスト。<br>
     * ・ディスク入力量が1000001<br>
     * ・閾値が1000000<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_th1000000_val1000001()
    {
        DiskInputRule rule = createRule(1000000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DiskInputRuleTest_testDoJudge_th1000000_val1000001.jvn");

        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)1000001);
    }

    /**
     * [項番] 2-9-5<br>
     * doJudgeのテスト。<br>
     * ・ディスク入力量が1000<br>
     * ・閾値が1000<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_th1000_val1000()
    {
        DiskInputRule rule = createRule(1000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DiskInputRuleTest_testDoJudge_th1000_val1000.jvn");

        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)1000);
    }

    /**
     * [項番] 2-9-10<br>
     * doJudgeのテスト。<br>
     * ・ディスク入力量の値が文字列（Test文字列)<br>
     * ・閾値が1000000<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th1000000_valstring()
    {
        DiskInputRule rule = createRule(1000000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DiskInputRuleTest_testDoJudge_th1000000_valstring.jvn");

        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-9-11<br>
     * doJudgeのテスト。<br>
     * ・ディスク入力量の値が空白<br>
     * ・閾値が1000000<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th1000000_valblank()
    {
        DiskInputRule rule = createRule(1000000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DiskInputRuleTest_testDoJudge_th1000000_valblank.jvn");

        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-9-12<br>
     * doJudgeのテスト。<br>
     * ・ディスク入力量値が存在しない。<br>
     * ・閾値が1000000<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th1000000_valnovalue()
    {
        DiskInputRule rule = createRule(1000000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DiskInputRuleTest_testDoJudge_th1000000_valnovalue.jvn");

        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-9-14<br>
     * doJudgeのテスト。<br>
     * ・IOInfoが存在しない。<br>
     * ・閾値が1000000<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th1000000_valnoinfotag()
    {
        DiskInputRule rule = createRule(1000000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DiskInputRuleTest_testDoJudge_th1000000_valnoinfotag.jvn");

        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-9-15<br>
     * doJudgeのテスト。<br>
     * ・Call種別のログが存在しない。<br>
     * ・閾値が1000000<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th1000000_valnocall()
    {
        DiskInputRule rule = createRule(1000000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DiskInputRuleTest_testDoJudge_th1000000_valnocall.jvn");

        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-9-26<br>
     * doJudgeのテスト。<br>
     * ・複数のJavelinLogElementで警告が出る。<br>
     * →警告が複数発生。<br>
     */
    public void testDoJudge_th1000000_multierror()
    {
        DiskInputRule rule = createRule(1000000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DiskInputRuleTest_testDoJudge_th1000000_multierror.jvn");

        rule.judge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)1000001);
        assertErrorOccurred(elementList.get(1), rule.threshold, (long)1000001);
    }

    /**
     * [項番] 2-9-27<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・あるJavelinLogElementで実行時例外が発生<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_RuntimeException()
    {
        DiskInputRule rule = createRule(1000000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DiskInputRuleTest_testDoJudge_th1000000_val1000001.jvn");

        elementList.add(0, null);

        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), rule.threshold, (long)1000001);
    }

}
