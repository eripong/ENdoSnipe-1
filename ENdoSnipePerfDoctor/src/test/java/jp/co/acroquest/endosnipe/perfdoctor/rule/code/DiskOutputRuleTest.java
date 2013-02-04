package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

/**
 * ディスク出力量判定ルール用のテストケース<br>
 * <br>
 * @author S.Kimura
 */
public class DiskOutputRuleTest extends PerformanceRuleTestCase
{
    /**
     * 閾値を指定してDiskOutputRuleを生成する。<br>
     * @param threshold 閾値
     * @return DiskOutputRule
     */
    private DiskOutputRule createRule(long threshold)
    {
        DiskOutputRule rule = createInstance(DiskOutputRule.class);
        rule.id = "COD.IO.DISK_OUTPUT";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 2-10-1<br>
     * doJudgeのテスト。<br>
     * ・ディスク入力量が999999<br>
     * ・閾値が1000000<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th1000000_val999999()
    {
        DiskOutputRule rule = createRule(1000000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DiskOutputRuleTest_testDoJudge_th1000000_val999999.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-10-2<br>
     * doJudgeのテスト。<br>
     * ・ディスク入力量が1000000<br>
     * ・閾値が1000000<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_th1000000_val1000000()
    {
        DiskOutputRule rule = createRule(1000000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DiskOutputRuleTest_testDoJudge_th1000000_val1000000.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)1000000);
    }

    /**
     * [項番] 2-10-3<br>
     * doJudgeのテスト。<br>
     * ・ディスク入力量が1000001<br>
     * ・閾値が1000000<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_th1000000_val1000001()
    {
        DiskOutputRule rule = createRule(1000000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DiskOutputRuleTest_testDoJudge_th1000000_val1000001.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)1000001);
    }
}
