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

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.javelin.RootInvocationManager;
import jp.co.acroquest.endosnipe.javelin.util.LinkedList;
import jp.co.acroquest.endosnipe.javelin.util.concurrent.ConcurrentHashMap;

public class Component implements ComponentMBean, Serializable
{
    private static final long serialVersionUID = 934662584633636762L;

    private final String className_;

    private final Map<String, Invocation> invocationMap_ =
            new ConcurrentHashMap<String, Invocation>();

    private final List<String> methodNameList_ = Collections.synchronizedList(new LinkedList<String>());

    public Component(final String className)
    {
        className_ = className;
    }

    public String getClassName()
    {
        return className_;
    }

    public Invocation[] getAllInvocation()
    {
        int size = invocationMap_.values().size();
        Invocation[] invocations = invocationMap_.values().toArray(new Invocation[size]);
        return invocations;
    }

    public synchronized void addInvocation(final Invocation invocation)
    {
        String methodName = invocation.getMethodName();
        methodNameList_.add(methodName);
        invocationMap_.put(methodName, invocation);
    }

    /**
     * 古いメソッド情報を削除し、新しいメソッド情報を追加します。
     *
     * @param invocation 追加するメソッド情報
     * @return 削除されたメソッド情報
     */
    public synchronized Invocation addAndDeleteOldestInvocation(final Invocation invocation)
    {
        Invocation removedInvoction = null;
        if (this.methodNameList_.size() > 0 && this.invocationMap_.size() > 0)
        {
            long averageDuration = getTotalDuration() / this.invocationMap_.size();
            Iterator<String> methodIterator = this.methodNameList_.iterator();
            while (methodIterator.hasNext())
            {
                String deleteCandidateKey = methodIterator.next();
                Invocation deleteCandidateInvocation = this.invocationMap_.get(deleteCandidateKey);
                if (deleteCandidateInvocation != null && deleteCandidateInvocation.getTotal() <= averageDuration)
                {
                    methodIterator.remove();
                    removedInvoction = this.invocationMap_.remove(deleteCandidateKey);
                    RootInvocationManager.removeInvocation(deleteCandidateInvocation);
                    break;
                }
            }
        }

        addInvocation(invocation);
        return removedInvoction;
    }

    public Invocation getInvocation(final String methodName)
    {
        return invocationMap_.get(methodName);
    }

    public int getRecordedInvocationNum()
    {
        return invocationMap_.size();
    }

    public void reset()
    {
        for (Invocation invocation : invocationMap_.values())
        {
            invocation.reset();
        }
    }

    /**
     * このクラス内にあるすべてのメソッドの合計時間の合計を計算します。
     *
     * @return 実行時間の合計（ミリ秒）
     */
    private long getTotalDuration()
    {
        long totalDuration = 0;
        for (Map.Entry<String, Invocation> entry : this.invocationMap_.entrySet())
        {
            totalDuration += entry.getValue().getTotal();
        }
        return totalDuration;
    }
}
