package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

/**
 * IdleTimeRuleのテストケースです。<br/>
 * @author fujii
 *
 */
public class IdleTimeRuleTest extends PerformanceRuleTestCase
{
    /**
     * 閾値を指定してIdleTimeRuleを生成します。<br/>
     * @param threshold 閾値
     * @return IdleTimeRule
     */
    private IdleTimeRule createRule(long threshold)
    {
        IdleTimeRule rule = createInstance(IdleTimeRule.class);
        rule.id = "COD.THRD.IDLE_TIME";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番]3-12-1 <br/>
     * <br/>
     * doJudgeのテストをします。<br/>
     * ・実行時間：2999<br/>
     * ・CPU時間：1000<br/>
     * ・閾値：2000<br/>
     * →警告が発生しません。<br/>
     */
    public void testDoJudgeUnderThreashold()
    {
        IdleTimeRule rule = createRule(2000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IdleTimeRuleTest_testDoJudge_1999.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番]3-12-2 <br/>
     * <br/>
     * doJudgeのテストをします。<br/>
     * ・実行時間：3000<br/>
     * ・CPU時間：1000<br/>
     * ・閾値：2000<br/>
     * →警告が発生します。<br/>
     */
    public void testDoJudgeEqualThreashold()
    {
        IdleTimeRule rule = createRule(2000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IdleTimeRuleTest_testDoJudge_2000.jvn");

        rule.doJudge(elementList);

        assertErrorOccurred(elementList.get(0), rule.threshold, (long)2000, "/add/add.html");
    }

    /**
     * [項番]3-12-3 <br/>
     * <br/>
     * doJudgeのテストをします。<br/>
     * ・実行時間：3001<br/>
     * ・CPU時間：1000<br/>
     * ・閾値：2000<br/>
     * →警告が発生します。<br/>
     */
    public void testDoJudgeOverThreashold()
    {
        IdleTimeRule rule = createRule(2000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IdleTimeRuleTest_testDoJudge_2001.jvn");

        rule.doJudge(elementList);

        assertErrorOccurred(elementList.get(0), rule.threshold, (long)2001, "/add/add.html");
    }

    /**
     * [項番]3-12-4 <br/>
     * <br/>
     * doJudgeのテストをします。<br/>
     * ・実行時間：20<br/>
     * ・CPU時間：10<br/>
     * ・閾値：10<br/>
     * →警告が発生します。<br/>
     */
    public void testDoJudgeOtherThreashold()
    {
        IdleTimeRule rule = createRule(10);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IdleTimeRuleTest_testDoJudge_10.jvn");

        rule.doJudge(elementList);

        assertErrorOccurred(elementList.get(0), rule.threshold, (long)10, "/add/add.html");
    }

    /**
     * [項番]3-12-9 <br/>
     * <br/>
     * doJudgeのテストをします。<br/>
     * ・実行時間：なし<br/>
     * ・CPU時間：なし<br/>
     * ・閾値：2000<br/>
     * →警告が発生しません。<br/>
     */
    public void testDoJudgeExecNoneCpuNone()
    {
        IdleTimeRule rule = createRule(2000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IdleTimeRuleTest_testDoJudge_ExecNoneCpuNone.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番]3-12-10 <br/>
     * <br/>
     * doJudgeのテストをします。<br/>
     * ・実行時間：なし<br/>
     * ・CPU時間：1000<br/>
     * ・閾値：2000<br/>
     * →警告が発生しません。<br/>
     */
    public void testDoJudgeExecNoneCpu1000()
    {
        IdleTimeRule rule = createRule(2000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IdleTimeRuleTest_testDoJudge_ExecNoneCpu1000.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番]3-12-5 <br/>
     * <br/>
     * doJudgeのテストをします。<br/>
     * ・実行時間：文字列<br/>
     * ・CPU時間：1000<br/>
     * ・閾値：2000<br/>
     * →警告が発生しません。<br/>
     */
    public void testDoJudgeExecStringCpu1000()
    {
        IdleTimeRule rule = createRule(2000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IdleTimeRuleTest_testDoJudge_ExecStringCpu1000.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番]3-12-7 <br/>
     * <br/>
     * doJudgeのテストをします。<br/>
     * ・実行時間：空<br/>
     * ・CPU時間：1000<br/>
     * ・閾値：2000<br/>
     * →警告が発生しません。<br/>
     */
    public void testDoJudgeExecEmptyCpu1000()
    {
        IdleTimeRule rule = createRule(2000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IdleTimeRuleTest_testDoJudge_ExecEmptyCpu1000.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番]3-12-11 <br/>
     * <br/>
     * doJudgeのテストをします。<br/>
     * ・実行時間：1000<br/>
     * ・CPU時間：なし<br/>
     * ・閾値：2000<br/>
     * →警告が発生しません。<br/>
     */
    public void testDoJudgeExec1000CpuNone()
    {
        IdleTimeRule rule = createRule(2000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IdleTimeRuleTest_testDoJudge_Exec1000CpuNone.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番]3-12-6 <br/>
     * <br/>
     * doJudgeのテストをします。<br/>
     * ・実行時間：1000<br/>
     * ・CPU時間：文字列<br/>
     * ・閾値：2000<br/>
     * →警告が発生しません。<br/>
     */
    public void testDoJudgeExec1000CpuString()
    {
        IdleTimeRule rule = createRule(2000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IdleTimeRuleTest_testDoJudge_Exec1000CpuString.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番]3-12-8 <br/>
     * <br/>
     * doJudgeのテストをします。<br/>
     * ・実行時間：1000<br/>
     * ・CPU時間：空<br/>
     * ・閾値：2000<br/>
     * →警告が発生しません。<br/>
     */
    public void testDoJudgeExec1000CpuEmpty()
    {
        IdleTimeRule rule = createRule(2000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IdleTimeRuleTest_testDoJudge_Exec1000CpuEmpty.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番]3-12-12 <br/>
     * <br/>
     * doJudgeのテストをします。<br/>
     * ・detailInfoがない場合<br/>
     * ・閾値：2000<br/>
     * →警告が発生しません。<br/>
     */
    public void testDoJudgeNoDetailInfo()
    {
        IdleTimeRule rule = createRule(2000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IdleTimeRuleTest_testDoJudge_NoDetailInfo.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番]3-12-13<br/>
     * <br/>
     * doJudgeのテストをします。。<br/>
     * ・「Call」がない場合。<br/>
     * →警告が発生しません。<br/>
     */
    public void testDoJudgeCall()
    {
        IdleTimeRule rule = createRule(2000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IdleTimeRuleTest_testDoJudge_NoCall.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番]3-12-14 <br/>
     * <br/>
     * doJudgeのテストをします。<br/>
     * ・複数のJavelinLogElementで警告が出ます。<br/>
     */
    public void testDoJudgeMulti()
    {
        IdleTimeRule rule = createRule(2000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IdleTimeRuleTest_testDoJudge_Multi.jvn");

        rule.doJudge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), rule.threshold, (long)2000, "/add/add.html");
        assertErrorOccurred(elementList.get(1), rule.threshold, (long)2001, "/add/add.html");
    }

    /**
     * [項番]3-12-15 <br/>
     * <br/>
     * doJudgeのテストをします。<br/>
     * ・あるJavelinLogElementで実行時例外が発生する場合<br/>
     * →そのJavelinLogElementはスキップして処理します。<br/>
     */
    public void testDoJudgeRuntimeException()
    {
        IdleTimeRule rule = createRule(2000);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("IdleTimeRuleTest_testDoJudge_2000.jvn");

        elementList.add(0, null);

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());

        assertErrorOccurred(elementList.get(1), rule.threshold, (long)2000,
                            "/add/add.html");
    }
}
