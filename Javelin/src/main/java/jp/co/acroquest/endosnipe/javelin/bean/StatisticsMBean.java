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

import java.util.List;

/**
 * 統計処理用MBean。<br>
 * S2JmxJavelinで蓄積した情報に対して統計処理を行った結果を返す。<br>
 * 現状、以下の情報を取得することが可能。
 * <ol>
 * <li>平均値でソートしたメソッドコール情報。</li>
 * <li>最大値でソートしたメソッドコール情報。</li>
 * <li>最小値でソートしたメソッドコール情報。</li>
 * <li>例外の発生回数でソートしたメソッドコール情報。</li>
 * </ol>
 * 
 * @author yamasaki
 * 
 */
public interface StatisticsMBean
{
    List<InvocationMBean> getInvocationListOrderByAverage();

    List<InvocationMBean> getInvocationListOrderByMaximum();

    List<InvocationMBean> getInvocationListOrderByMinimum();

    List<InvocationMBean> getInvocationListOrderByThrowableCount();
}
