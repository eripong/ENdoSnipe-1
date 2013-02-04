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

import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;

/**
 * スレッドに関する情報を表すクラス。
 * 
 * @author tsukano
 * @param <E> スレッド情報として保持する値の型
 */
public class ThreadInfo<E>
{
    /** 本クラスのインスタンス作成に利用したJavelinLogElement */
    private JavelinLogElement javelinLogElement_;

    /** スレッド名 */
    private String            threadName_;

    /** スレッドごとの計測値 */
    private E                 value_;

    /**
     * @return javelinLogElement
     */
    public JavelinLogElement getJavelinLogElement()
    {
        return this.javelinLogElement_;
    }

    /**
     * @param javelinLogElement 設定する javelinLogElement
     */
    public void setJavelinLogElement(final JavelinLogElement javelinLogElement)
    {
        this.javelinLogElement_ = javelinLogElement;
    }

    /**
     * @return value
     */
    public E getValue()
    {
        return this.value_;
    }

    /**
     * @param value 設定する value
     */
    public void setValue(final E value)
    {
        this.value_ = value;
    }

    /**
     * @return threadName
     */
    public String getThreadName()
    {
        return this.threadName_;
    }

    /**
     * @param threadName 設定する threadName
     */
    public void setThreadName(final String threadName)
    {
        this.threadName_ = threadName;
    }
}
