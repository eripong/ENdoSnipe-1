package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

/**
 * ネットワーク出力量判定ルール用のテストケース<br>
 * <br>
 * @author S.Kimura
 */
public class NetworkOutputRuleTest extends PerformanceRuleTestCase
{
    /**
     * 閾値を指定してNetworkOutputRuleを生成する。<br>
     * @param threshold 閾値
     * @return NetworkOutputRule
     */
    private NetworkOutputRule createRule(long threshold)
    {
        NetworkOutputRule rule = createInstance(NetworkOutputRule.class);
        rule.id = "COD.IO.NETWORK_OUTPUT";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * 実処理は全てAbstractSingleValueLimitクラスに書いてあるため、<br>
     * 詳細な検証は同じくAbstractSingleValueLimitの実装クラスである、<br>
     * DiskOutputRuleTestにて行う。<br>
     * 本テストケースにおいては、エラーが出力されることのみを確認する。<br>
     */

    /**
     * [項番] 2-11-1<br>
     * doJudgeのテスト。<br>
     * ・ネットワーク入力量が9999999<br>
     * ・閾値が1000000<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_th1000000_val999999()
    {
        NetworkOutputRule rule = createRule(1000000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("NetworkOutputRuleTest_testDoJudge_th1000000_val999999.jvn");

        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 2-11-2<br>
     * doJudgeのテスト。<br>
     * ・ネットワーク入力量が1000000<br>
     * ・閾値が1000000<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_th1000000_val1000000()
    {
        NetworkOutputRule rule = createRule(1000000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("NetworkOutputRuleTest_testDoJudge_th1000000_val1000000.jvn");

        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)1000000);
    }

    /**
     * [項番] 2-11-3<br>
     * doJudgeのテスト。<br>
     * ・ネットワーク入力量が1000001<br>
     * ・閾値が1000000<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_th1000000_val1000001()
    {
        NetworkOutputRule rule = createRule(1000000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("NetworkOutputRuleTest_testDoJudge_th1000000_val1000001.jvn");

        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)1000001);
    }

}
