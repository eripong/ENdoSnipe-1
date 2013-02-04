/*******************************************************************************
 * ENdoSnipe 5.0 - (https://github.com/endosnipe)
 * 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2012 Acroquest Technology Co.,Ltd.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package jp.co.acroquest.endosnipe.perfdoctor.rule.dbaccess;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

/**
 * {@link OneSqlCountEventRule} のためのテストクラスです。<br/>
 * 
 * @author akita
 */
public class OneSqlCountEventRuleTest extends PerformanceRuleTestCase
{
    /**
     * {@link OneSqlCountEventRule} を生成します。
     * 
     * @param threshold 閾値
     * @return {@link OneSqlCountEventRule} オブジェクト
     */
    private OneSqlCountEventRule createRule(int threshold)
    {
        OneSqlCountEventRule rule = createInstance(OneSqlCountEventRule.class);
        rule.id = "DBA.MTRC.ONE_SQL_CNT_EVT";
        rule.active = true;
        rule.level = "ERROR";
        rule.threshold = threshold;
        return rule;
    }

    /**
     * [項番] 3-15-1<br/>
     * <br/>
     * doJudgeのテストです。<br/>
     * ・SQL実行回数超過イベントでの呼び出した回数の検出値が9である場合。<br/>
     * ・閾値が10である場合。<br/>
     * →警告は発生しません。<br/>
     */
    public void testDoJudge_01()
    {
        OneSqlCountEventRule rule = createRule(10);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountEventRuleTest_testDoJudge_9.jvn");

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-15-2<br/>
     * <br/>
     * doJudgeのテストです。<br/>
     * ・SQL実行回数超過イベントでの呼び出した回数の検出値が10である場合。<br/>
     * ・閾値が10である場合。<br/>
     * →警告が発生します。<br/>
     */
    public void testDoJudge_02()
    {
        OneSqlCountEventRule rule = createRule(10);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountEventRuleTest_testDoJudge_10.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), 10, "2", 10, "http-8080-1@30(java.lang.Thread@afca92)");
    }

    /**
     * [項番] 3-15-3<br/>
     * <br/>
     * doJudgeのテストです。<br/>
     * ・SQL実行回数超過イベントでの呼び出した回数の検出値が11である場合。<br/>
     * ・閾値が10である場合。<br/>
     * →警告が発生します。<br/>
     */
    public void testDoJudge_03()
    {
        OneSqlCountEventRule rule = createRule(10);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountEventRuleTest_testDoJudge_11.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), 10, "2", 11, "http-8080-1@30(java.lang.Thread@afca92)");
    }

    /**
     * [項番] 3-15-4<br/>
     * <br/>
     * doJudgeのテストです。<br/>
     * ・SQL実行回数超過イベントでの呼び出した回数の検出値が3である場合。<br/>
     * ・閾値が3である場合。<br/>
     * →警告が発生します。<br/>
     */
    public void testDoJudge_05()
    {
        OneSqlCountEventRule rule = createRule(3);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountEventRuleTest_testDoJudge_3.jvn");

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), 3, "2", 3, "http-8080-1@30(java.lang.Thread@afca92)");
    }

    /**
     * [項番] 3-15-5<br/>
     * <br/>
     * doJudgeのテストです。<br/>
     * ・SQL実行回数超過イベントでの呼び出した回数の検出値が数値ではない場合。<br/>
     * →警告が発生しません。<br/>
     */
    public void testDoJudge_10()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountEventRuleTest_testDoJudge_stringVal.jvn");

        OneSqlCountEventRule rule = createRule(5);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-15-6<br/>
     * <br/>
     * doJudgeのテストです。<br/>
     * ・SQL実行回数超過イベントでの呼び出した回数の検出値が空白である場合。<br/>
     * →警告が発生しません。<br/>
     */
    public void testDoJudge_11()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountEventRuleTest_testDoJudge_empty.jvn");

        OneSqlCountEventRule rule = createRule(10);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-15-7<br/>
     * <br/>
     * doJudgeのテストです。<br/>
     * ・SQL実行回数超過イベントでの呼び出した回数の検出値のパラメータが存在しない場合。<br/>
     * →警告が発生しません。<br/>
     */
    public void testDoJudge_12()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountEventRuleTest_testDoJudge_no_param.jvn");

        OneSqlCountEventRule rule = createRule(10);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
    * [項番] 3-15-8<br/>
    * <br/>
    * doJudgeのテストです。<br/>
    * ・eventInfoがない場合。<br/>
    * →警告を表示しません。<br/>
    */
    public void testDoJudge_14()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountEventRuleTest_testDoJudge_no_eventInfo.jvn");

        OneSqlCountEventRule rule = createRule(10);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-15-9<br/>
     * <br/>
     * doJudgeのテストです。<br/>
     * ・EVENTがない場合。<br/>
     * →警告を表示しません。<br/>
     */
    public void testDoJudge_15()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountEventRuleTest_testDoJudge_no_event.jvn");

        OneSqlCountEventRule rule = createRule(10);
        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }

    /**
     * [項番] 3-15-10<br/>
     * <br/>
     * doJudgeのテストです。<br/>
     * ・複数のJavelinLogElementで警告が出る場合。<br/>
     *→警告が2つ発生します。<br/>
     */
    public void testDoJudge_26()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountEventRuleTest_testDoJudge_multi.jvn");

        OneSqlCountEventRule rule = createRule(10);
        rule.doJudge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), 10, "2", 11, "http-8080-1@30(java.lang.Thread@afca92)");
        assertErrorOccurred(elementList.get(2), 10, "2", 11, "http-8080-2@30(java.lang.Thread@afca93)");
    }

    /**
     * [項番] 3-15-11<br/>
     * <br/>
     * doJudgeのテストです。<br/>
     * ・あるJavelinLogElementで実行時例外が発生する場合<br/>
     * →そのJavelinLogElementはスキップして処理する。<br/>
     */
    public void testDoJudge_RuntimeException()
    {
        OneSqlCountEventRule rule = createRule(500);
        List<JavelinLogElement> elementList =
                createJavelinLogElement("OneSqlCountEventRuleTest_testDoJudge_11.jvn");

        elementList.add(0, null);

        rule.doJudge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }

    }

}
