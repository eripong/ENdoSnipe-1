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
package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.List;

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.perfdoctor.PerformanceRuleTestCase;

/**
 * ENdoSnipeVer.4.0の新ルールです。<br/>
 * メモリリーク検出のルールのテストをします。<br/>
 * @author akita
 *
 */
public class LeakDetectedRuleTest extends PerformanceRuleTestCase
{
    /**
     * LeakDetectedRuleを生成します。<br/>
     * @return LeakDetectedRule
     */
    private LeakDetectedRule createRule()
    {

        LeakDetectedRule rule = createInstance(LeakDetectedRule.class);
        rule.id = "COD.THRD.MEM_LEAK";
        rule.active = true;
        rule.level = "WARN";
        return rule;
    }

    /**
     * [項番] 3-13-1<br/>
     * <br/>
     * doJudgeのテストです。<br/>
     * ・指定された値が文字列である場合。<br/>
     * →警告が発生します。<br/>
     */
    public void testDoJudge_10()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("LeakDetectedRuleTest_testDoJudge_valstring.jvn");

        LeakDetectedRule rule = createRule();
        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());

        assertErrorOccurred(elementList.get(1), "2000", "java.util.HashMap@1420fea"
                            , "2000", "[not recorded] ",
                            "http-8080-1@27(java.lang.Thread@12aa789)", " - ");

    }

    /**
     * [項番] 3-13-2<br/>
     * <br/>
     * doJudgeのテストです。<br/>
     * 指定された値が空白の場合。<br/>
     * →警告が発生しません。<br/>
     */
    public void testDoJudge_11()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("LeakDetectedRuleTest_testDoJudge_empty.jvn");

        LeakDetectedRule rule = createRule();
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生します。");
        }
    }

    /**
     * [項番] 3-13-3<br/>
     * <br/>
     * doJudgeのテストです。<br/>
     *　指定されたパラメータがない（ブロックのパラメータが無い場合）<br/>
     * →警告を表示しません。<br/>
     */
    public void testDoJudge_12()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("src/test/java/jp.co.acroquest/endosnipe/"
                        + "perfdoctor/rule/code/LeakDetectedRuleTest_testDoJudge_no_param.jvn");

        LeakDetectedRule rule = createRule();
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生します。");
        }
    }

    /**
     * [項番] 3-13-4<br/>
     * <br/>
     * doJudgeのテストです。<br/>
     * ・EventInfoがない場合。<br/>
     * →警告を表示しません。<br/>
     */
    public void testDoJudge_14()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("LeakDetectedRuleTest_testDoJudge_no_EventInfo.jvn");

        LeakDetectedRule rule = createRule();
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生します。");
        }
    }

    /**
     * [項番] 3-13-5<br/>
     * <br/>
     * doJudgeのテストです。。<br/>
     * ・EVENTがない場合。<br/>
     * →警告を表示しません。<br/>
     */
    public void testDoJudge_15()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("LeakDetectedRuleTest_testDoJudge_no_type.jvn");

        LeakDetectedRule rule = createRule();
        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生します。");
        }
    }

    /**
     * [項番] 3-13-6<br/>
     * <br/>
     * doJudgeのテストです。<br/>
     * ・複数のJavelinLogElementで警告が出ます。<br/>
     */
    public void testDoJudge_26()
    {
        List<JavelinLogElement> elementList =
                createJavelinLogElement("LeakDetectedRuleTest_testDoJudge_multi_Element.jvn");

        LeakDetectedRule rule = createRule();
        rule.doJudge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(1), "2000", "java.util.HashMap@1420fea", "2000",
                            "[not recorded] ", "http-8080-1@27(java.lang.Thread@12aa789)", " - ");
        assertErrorOccurred(elementList.get(4), "3000", "java.util.HashMap@1421fea", "3000",
                            "[not recorded] ", "http-8080-1@27(java.lang.Thread@12aa789)", " - ");
    }

    /**
     * [項番] 3-13-7<br/>
     * <br/>
     * doJudgeのテストです。<br/>
     * ・あるJavelinLogElementで実行時例外が発生します。<br/>
     * →そのJavelinLogElementはスキップして処理します。<br/>
     */
    public void testDoJudge_RuntimeException()
    {
        LeakDetectedRule rule = createRule();
        List<JavelinLogElement> elementList =
                createJavelinLogElement("LeakDetectedRuleTest_testDoJudge_valstring.jvn");

        elementList.add(0, null);

        rule.doJudge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(2), "2000", "java.util.HashMap@1420fea", "2000",
                            "[not recorded] ", "http-8080-1@27(java.lang.Thread@12aa789)", " - ");
    }

}
