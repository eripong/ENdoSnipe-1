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
package jp.co.acroquest.endosnipe.javelin.bean;

import jp.co.acroquest.endosnipe.javelin.CallTreeNode;
import junit.framework.TestCase;

/**
 * @author iida
 *
 */
public class ComponentTest extends TestCase
{
    /** クラス名。 */
    private static final String CLASS_NAME = "ClassName";
    
    /** プロセス名。 */
    private static final String PROCESS_NAME = "ProcessName";
    
    /** 合計実行時間を設定する時に必要なCallTreeNodeオブジェクト。 */
    private static CallTreeNode callTreeNode__ = new CallTreeNode();
    
    /**
     * 指定されたメソッド名と合計実行時間を持つInvocationを生成し、返します。<br />
     * 
     * @param methodName メソッド名
     * @param totalTime 合計実行時間
     * @return 新しいInvocationオブジェクト
     */
    private Invocation createInvocation(String methodName, int totalTime)
    {
        Invocation invocation = new Invocation(PROCESS_NAME, CLASS_NAME, methodName,
                                               Invocation.THRESHOLD_NOT_SPECIFIED);
        invocation.addInterval(callTreeNode__, totalTime, totalTime, totalTime);
        return invocation;
    }
    
    /**
     * Invocationを最大数まで入れた後、新たなInvocationを加え、古い要素の削除を確認します。<br />
     * 削除されるのは、最初のInvocationです。<br />
     */
    public void testAddAndDeleteOldestInvocation_RemoveFirstInvocation()
    {
        Component component = new Component(CLASS_NAME);
        final String removedMethodName = "MethodName1";
        
        // javelin.record.invocation.num.maxで指定した数になるまで、Invocationを加える。
        component.addInvocation(createInvocation(removedMethodName, 10));
        for (int count = 2; count <= 1024; count++)
        {
            component.addInvocation(createInvocation("MethodName" + count, 20));
        }
        
        // 判定1:サイズが最大値、かつ削除予定の要素がまだ存在する。
        int size = component.getRecordedInvocationNum();
        Invocation removedInvocation = component.getInvocation(removedMethodName);
        assertEquals(size, 1024);
        assertNotNull(removedInvocation);
        
        // 新たなInvocationを、addAndDeleteOldestInvocationメソッドで加える。
        component.addAndDeleteOldestInvocation(createInvocation("MethodName1025", 20));
        
        // 判定2：サイズが最大値、かつ削除予定の要素が削除されている。
        size = component.getRecordedInvocationNum();
        removedInvocation = component.getInvocation(removedMethodName);
        assertEquals(size, 1024);
        assertNull(removedInvocation);
    }
    
    /**
     * Invocationを最大数まで入れた後、新たなInvocationを加え、古い要素の削除を確認します。<br />
     * 削除されるのは、途中のInvocationです。<br />
     */
    public void testAddAndDeleteOldestInvocation_RemoveMiddleInvocation()
    {
        Component component = new Component(CLASS_NAME);
        final String removedMethodName = "MethodName512";
        
        // javelin.record.invocation.num.maxで指定した数になるまで、Invocationを加える。
        for (int count = 1; count <= 511; count++)
        {
            component.addInvocation(createInvocation("MethodName" + count, 20));
        }
        component.addInvocation(createInvocation(removedMethodName, 10));
        for (int count = 513; count <= 1024; count++)
        {
            component.addInvocation(createInvocation("MethodName" + count, 20));
        }
        
        // 判定1:サイズが最大値、かつ削除予定の要素がまだ存在する。
        int size = component.getRecordedInvocationNum();
        Invocation removedInvocation = component.getInvocation(removedMethodName);
        assertEquals(size, 1024);
        assertNotNull(removedInvocation);
        
        // 新たなInvocationを、addAndDeleteOldestInvocationメソッドで加える。
        component.addAndDeleteOldestInvocation(createInvocation("MethodName1025", 20));
        
        // 判定2：サイズが最大値、かつ削除予定の要素が削除されている。
        size = component.getRecordedInvocationNum();
        removedInvocation = component.getInvocation(removedMethodName);
        assertEquals(size, 1024);
        assertNull(removedInvocation);
    }
    
    /**
     * Invocationを最大数まで入れた後、新たなInvocationを加え、古い要素の削除を確認します。<br />
     * 削除されるのは、一番最後のInvocationです。<br />
     */
    public void testAddAndDeleteOldestInvocation_RemoveLastInvocation()
    {
        Component component = new Component(CLASS_NAME);
        final String removedMethodName = "MethodName1024";
        
        // javelin.record.invocation.num.maxで指定した数になるまで、Invocationを加える。
        for (int count = 1; count <= 1023; count++)
        {
            component.addInvocation(createInvocation("MethodName" + count, 20));
        }
        component.addInvocation(createInvocation(removedMethodName, 10));
        
        // 判定1:サイズが最大値、かつ削除予定の要素がまだ存在する。
        int size = component.getRecordedInvocationNum();
        Invocation removedInvocation = component.getInvocation(removedMethodName);
        assertEquals(size, 1024);
        assertNotNull(removedInvocation);
        
        // 新たなInvocationを、addAndDeleteOldestInvocationメソッドで加える。
        component.addAndDeleteOldestInvocation(createInvocation("MethodName1025", 20));
        
        // 判定2：サイズが最大値、かつ削除予定の要素が削除されている。
        size = component.getRecordedInvocationNum();
        removedInvocation = component.getInvocation(removedMethodName);
        assertEquals(size, 1024);
        assertNull(removedInvocation);
    }
    
    /**
     * Invocationを最大数まで入れた後、新たなInvocationを加え、古い要素の削除を確認します。<br />
     * ただし、全てのInvocationの合計実行時間が同じです。<br />
     */
    public void testAddAndDeleteOldestInvocation_SameTotalTimeInvocations()
    {
        Component component = new Component(CLASS_NAME);
        final String removedMethodName = "MethodName1";
        
        // javelin.record.invocation.num.maxで指定した数になるまで、Invocationを加える。
        component.addInvocation(createInvocation(removedMethodName, 20));
        for (int count = 2; count <= 1024; count++)
        {
            component.addInvocation(createInvocation("MethodName" + count, 20));
        }
        
        // 判定1:サイズが最大値、かつ削除予定の要素がまだ存在する。
        int size = component.getRecordedInvocationNum();
        Invocation removedInvocation = component.getInvocation(removedMethodName);
        assertEquals(size, 1024);
        assertNotNull(removedInvocation);
        
        // 新たなInvocationを、addAndDeleteOldestInvocationメソッドで加える。
        component.addAndDeleteOldestInvocation(createInvocation("MethodName1025", 20));
        
        // 判定2：サイズが最大値、かつ削除予定の要素が削除されている。
        size = component.getRecordedInvocationNum();
        removedInvocation = component.getInvocation(removedMethodName);
        assertEquals(size, 1024);
        assertNull(removedInvocation);
    }
}
