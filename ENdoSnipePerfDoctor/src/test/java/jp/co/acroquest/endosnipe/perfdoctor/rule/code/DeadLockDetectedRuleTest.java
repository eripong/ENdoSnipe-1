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

public class DeadLockDetectedRuleTest extends PerformanceRuleTestCase
{

    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    /**
     * Javelinログにデッドロックイベントが存在しない場合の判定
     */
    public void testDoJudge_deadlock_none()
    {
        DeadLockDetectedRule rule = createRule();
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DeadLockDetectedRuleTest_testDoJudge_deadlock_none.jvn");

        rule.judge(elementList);

        List<JavelinLogElement> errorList = getErrorJavelinLogElements();
        int errorListSize = errorList.size();
        
        if (errorListSize != 0)
        {
            fail("発生しないはずのエラーが発生。");
        }
    }
    
    /**
     * Javelinログにデッドロックイベント(2つ)が存在する場合の判定
     */
    public void testDoJudge_deadlock_pair()
    {
        DeadLockDetectedRule rule = createRule();
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DeadLockDetectedRuleTest_testDoJudge_deadlock_pair.jvn");

        rule.judge(elementList);
        
        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0));
    }
    
    /**
     * Javelinログにデッドロックイベント(3つ)が存在する場合の判定
     */
    public void testDoJudge_deadlock_trio()
    {
        DeadLockDetectedRule rule = createRule();
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DeadLockDetectedRuleTest_testDoJudge_deadlock_trio.jvn");

        rule.judge(elementList);

        assertEquals(1, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0));
    }
    
    /**
     * Javelinログにデッドロックイベント(2つ)が複数存在する場合の判定
     */
    public void testDoJudge_deadlock_multi()
    {
        DeadLockDetectedRule rule = createRule();
        List<JavelinLogElement> elementList =
                createJavelinLogElement("DeadLockDetectedRuleTest_testDoJudge_deadlock_multi.jvn");

        rule.judge(elementList);

        assertEquals(2, getErrorJavelinLogElements().size());
        assertErrorOccurred(elementList.get(0));
    }
    
    private DeadLockDetectedRule createRule()
    {
        DeadLockDetectedRule rule = createInstance(DeadLockDetectedRule.class);
        rule.id = "COD.THRD.BLK_DEADLOCK";
        rule.active = true;
        rule.level = "WARN";
        rule.durationThreshold = 0;
        
        return rule;
    }
    
}
