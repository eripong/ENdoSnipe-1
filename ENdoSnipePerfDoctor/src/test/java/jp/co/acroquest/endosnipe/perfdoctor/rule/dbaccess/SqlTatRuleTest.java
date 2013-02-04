package jp.co.acroquest.endosnipe.perfdoctor.rule.dbaccess;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

/**
 * {@link SqlTatRule} のためのテストクラス。
 * 
 * @author y-komori
 */
public class SqlTatRuleTest extends PerformanceRuleTestCase
{
    /**
     * {@link SqlTatRule} を生成する。
     * 
     * @param threshold 閾値
     * @return {@link SqlTatRule} オブジェクト
     */
    private SqlTatRule createRule(long threshold)
    {
        SqlTatRule rule = createInstance(SqlTatRule.class);
        rule.id = "DBA.MTRC.SQL_TAT";
        rule.active = true;
        rule.level = "WARN";
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 3-1-1<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・TATが499。<br>
     * ・閾値が500。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_01()
    {
        SqlTatRule rule = createRule(500);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("SqlTatRuleTest_testDoJudge_499.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-1-2<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・TATが500。<br>
     * ・閾値が500。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_02()
    {
        SqlTatRule rule = createRule(500);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("SqlTatRuleTest_testDoJudge_500.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 500L, 500L);
    }

    /**
     * [項番] 3-1-3<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・TATが501。<br>
     * ・閾値が500。<br>
     * →警告が発生する。<br>
     */
    public void testDoJudge_03()
    {
        SqlTatRule rule = createRule(500);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("SqlTatRuleTest_testDoJudge_501.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 500L, 501L);
    }

    /**
     * [項番] 3-1-5<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・TATが10。<br>
     * ・閾値が10。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_05()
    {
        SqlTatRule rule = createRule(10);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("SqlTatRuleTest_testDoJudge_10.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 10L, 10L);
    }

    /**
     * [項番] 3-1-10<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・TATの値が数値ではない。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_10()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("SqlTatRuleTest_testDoJudge_invalid.jvn");

        SqlTatRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-1-11<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・TATの値が空白。<br>
     * →警告が発生しない。<br>
     */
    public void testDoJudge_11()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("SqlTatRuleTest_testDoJudge_empty.jvn");

        SqlTatRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-1-13<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・[TIME]というタグがない。<br>
     * →警告を表示しない。<br>
     */
    public void testDoJudge_13()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("SqlTatRuleTest_testDoJudge_no_time.jvn");

        SqlTatRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-1-14<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・detailInfoがない。<br>
     * →警告を表示しない。<br>
     */
    public void testDoJudge_14()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("SqlTatRuleTest_testDoJudge_no_detailInfo.jvn");

        SqlTatRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-1-15<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・CALLがない。<br>
     * →警告を表示しない。<br>
     */
    public void testDoJudge_15()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("SqlTatRuleTest_testDoJudge_no_call.jvn");

        SqlTatRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-1-26<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・複数のJavelinLogElementで警告が出る。<br>
     */
    public void testDoJudge_26()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("SqlTatRuleTest_testDoJudge_multi.jvn");

        SqlTatRule rule = createRule(500);
        rule.doJudge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0), 500L, 501L);
        assertErrorOccurred(elementList.get(1), 500L, 501L);
    }

    /**
     * [項番] 3-1-27<br>
     * <br>
     * doJudgeのテスト。<br>
     * ・あるJavelinLogElementで実行時例外が発生<br>
     * →そのJavelinLogElementはスキップして処理する。<br>
     */
    public void testDoJudge_RuntimeException()
    {
        SqlTatRule rule = createRule(500);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("SqlTatRuleTest_testDoJudge_501.jvn");

        elementList.add(0, null);

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), 500L, 501L);

    }

}
