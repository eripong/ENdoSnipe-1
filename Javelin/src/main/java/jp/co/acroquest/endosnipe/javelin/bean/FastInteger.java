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


/**
 * コレクションに格納するための Integer クラス。<br />
 *
 * 同期化しないため、 AtomicInteger よりも高速です。<br />
 *
 * @author sakamoto
 */
public class FastInteger
{

    private int value_;

    /**
     * 値を 0 出初期化したオブジェクトを生成します。<br />
     */
    public FastInteger()
    {
        this.value_ = 0;
    }

    /**
     * 指定された値で初期化したオブジェクトを生成します。<br />
     *
     * @param value 値
     */
    public FastInteger(final int value)
    {
        this.value_ = value;
    }

    /**
     * 値をセットします。<br />
     *
     * @param value 値
     */
    public void setValue(final int value)
    {
        this.value_ = value;
    }

    /**
     * 値を <code>1</code> 増やします。<br />
     */
    public void increment()
    {
        this.value_++;
    }

    /**
     * 値を <code>1</code> 減らします。<br />
     */
    public void decrement()
    {
        this.value_--;
    }

    /**
     * 値を加算します。<br />
     *
     * @param addValue 加算する値
     */
    public void add(final int addValue)
    {
        this.value_ += addValue;
    }

    /**
     * 値を減算します。<br />
     *
     * @param subValue 減算する値
     */
    public void subtract(final int subValue)
    {
        this.value_ -= subValue;
    }

    /**
     * 値を取得します。<br />
     *
     * @return 値
     */
    public int getValue()
    {
        return this.value_;
    }
}
