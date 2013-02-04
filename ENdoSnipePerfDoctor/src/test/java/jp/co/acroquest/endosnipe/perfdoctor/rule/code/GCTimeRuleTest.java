package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

/**
 * GC実行時間判定ルール用のテストケース<br>
 * ver3.3SCR対応用のテストコードとして作成した<br>
 * 試験仕様書は ENdoSnipeクライアント 単体試験仕様書.xls<br>
 * @author SUZUKI TOORU
 */
public class GCTimeRuleTest extends PerformanceRuleTestCase
{

    /**
     * 閾値を指定してGCTimeRuleを生成する。<br>
     * @param threshold 閾値
     * @return GCTimeRule
     */
    private GCTimeRule createRule(double threshold)
    {
        GCTimeRule rule = createInstance(GCTimeRule.class);
        rule.id = "COD.MTRC.GC_TIME";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 1-2-1<br>
     * <br>
     * judgeのテスト。<br>
     * ・検出値が0。<br>
     * ・閾値が1。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_01()
    {
        // 準備
        String jvnFile = "GCTimeRule_testDoJudge_00.jvn";
        List<JavelinLogElement> elementList = createJavelinLogElement(jvnFile);

        // 実行
        GCTimeRule rule = createRule(1);
        rule.judge(elementList);

        // 検証
        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }

    }

    /**
     * [項番] 1-2-2<br>
     * <br>
     * judgeのテスト。<br>
     * ・検出値が1。<br>
     * ・閾値が1。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_02()
    {
        // 準備
        String jvnFile = "GCTimeRule_testDoJudge_01.jvn";
        List<JavelinLogElement> elementList = createJavelinLogElement(jvnFile);

        // 実行
        GCTimeRule rule = createRule(1);
        rule.judge(elementList);

        // 検証
        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 1.0, 1.0, 3.0);
    }

    /**
     * [項番] 1-2-3<br>
     * <br>
     * judgeのテスト。<br>
     * ・検出値が2。<br>
     * ・閾値が1。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_03()
    {
        // 準備
        String jvnFile = "GCTimeRule_testDoJudge_02.jvn";
        List<JavelinLogElement> elementList = createJavelinLogElement(jvnFile);

        // 実行
        GCTimeRule rule = createRule(1);
        rule.judge(elementList);

        // 検証
        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 1.0, 2.0, 3.0);
    }

    /**
     * [項番] 1-2-4<br>
     * <br>
     * judgeのテスト。<br>
     * ・検出値が20。<br>
     * ・閾値が20。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_05()
    {
        // 準備
        String jvnFile = "GCTimeRule_testDoJudge_20.jvn";
        List<JavelinLogElement> elementList = createJavelinLogElement(jvnFile);

        // 実行
        GCTimeRule rule = createRule(20);
        rule.judge(elementList);

        // 検証
        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 20.0, 20.0, 3.0);
    }

    /**
     * [項番] 1-2-5<br>
     * <br>
     * judgeのテスト。<br>
     * ・GC回数の値が数値ではない。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_10()
    {
        // 準備
        String jvnFile = "GCTimeRule_testDoJudge_invalid.jvn";
        List<JavelinLogElement> elementList = createJavelinLogElement(jvnFile);

        // 実行
        GCTimeRule rule = createRule(5);
        rule.judge(elementList);

        // 検証
        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 1-2-6<br>
     * <br>
     * judgeのテスト。<br>
     * ・GC回数の値が空白。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_11()
    {
        // 準備
        String jvnFile = "GCTimeRule_testDoJudge_empty.jvn";
        List<JavelinLogElement> elementList = createJavelinLogElement(jvnFile);

        // 実行
        GCTimeRule rule = createRule(5);
        rule.judge(elementList);

        // 検証
        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 1-2-7<br>
     * <br>
     * 異常ログ<br>
     * ログ内にパラメータが無い。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_12()
    {
        // 準備
        GCTimeRule rule = createRule(5);
        String jvnFile = "GCTimeRule_testDoJudge_no_param.jvn";
        List<JavelinLogElement> elementList = createJavelinLogElement(jvnFile);

        // 実行
        rule.judge(elementList);

        // 検証
        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 1-2-8<br>
     * <br>
     * judgeのテスト。<br>
     * ・detailInfoがない。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_14()
    {
        // 準備
        String jvnFile = "GCTimeRule_testDoJudge_no_detailInfo.jvn";
        List<JavelinLogElement> elementList = createJavelinLogElement(jvnFile);

        // 実行
        GCTimeRule rule = createRule(5);
        rule.judge(elementList);

        // 検証
        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 1-2-9<br>
     * <br>
     * judgeのテスト。<br>
     * ・CALLがない。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_15()
    {
        // 準備
        String jvnFile = "GCTimeRule_testDoJudge_no_call.jvn";
        List<JavelinLogElement> elementList = createJavelinLogElement(jvnFile);

        // 実行
        GCTimeRule rule = createRule(5);
        rule.judge(elementList);

        // 検証
        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 1-2-10<br>
     * <br>
     * judgeのテスト。<br>
     * ・複数のJavelinLogElementで警告が出る。<br>
     */
    public void testDoJudge_26()
    {
        // 準備
        String jvnFile = "GCTimeRule_testDoJudge_multi.jvn";
        List<JavelinLogElement> elementList = createJavelinLogElement(jvnFile);

        // 実行
        GCTimeRule rule = createRule(1);
        rule.judge(elementList);

        // 検証
        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 1.0, 2.0, 3.0);
        assertErrorOccurred(elementList.get(4), 1.0, 2.0, 3.0);
    }

}
