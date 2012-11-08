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
package jp.co.acroquest.endosnipe.javelin;

/**
 * CallTree内で呼ばれたメソッドの計測情報を保存するオブジェクトです。

 * @author fujii
 *
 */
public class CallTreeMeasurement
{
    /** クラス名 */
    private String className_;

    /** メソッド名 */
    private String methodName_;

    /** メソッド呼び出し回数 */
    private long count_;

    /** メソッドの時刻時間 */
    private long total_;

    /**
     * メソッドの呼び出し回数とメソッドの実行時間を設定します。<br />
     * 
     * @param className クラス名
     * @param methodName メソッド名
     * @param count メソッドの呼び出し回数
     * @param total メソッドの実行時間
     */
    public CallTreeMeasurement(String className, String methodName, long count, long total)
    {
        this.className_ = className;
        this.methodName_ = methodName;
        this.count_ = count;
        this.total_ = total;
    }

    /**
     * 計測値を加えます。<br />
     * 
     * @param interval 計測値
     */
    public void addInterval(long interval)
    {
        this.total_ += interval;
    }

    /**
     * 実行回数を１増やします。<br />
     * 
     */
    public void incrementCount()
    {
        this.count_++;
    }

    /**
     * @return count
     */
    public long getCount()
    {
        return this.count_;
    }

    /**
     * @return interval
     */
    public long getTotal()
    {
        return this.total_;
    }

    /**
     * @return className
     */
    public String getClassName()
    {
        return this.className_;
    }

    /**
     * @return methodName
     */
    public String getMethodName()
    {
        return this.methodName_;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((this.className_ == null) ? 0 : this.className_.hashCode());
        result = PRIME * result + ((this.methodName_ == null) ? 0 : this.methodName_.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final CallTreeMeasurement OTHER = (CallTreeMeasurement)obj;
        if (this.className_ == null)
        {
            if (OTHER.className_ != null)
            {
                return false;
            }
        }
        else if (!this.className_.equals(OTHER.className_))
        {
            return false;
        }
        if (this.methodName_ == null)
        {
            if (OTHER.methodName_ != null)
            {
                return false;
            }
        }
        else if (!this.methodName_.equals(OTHER.methodName_))
        {
            return false;
        }
        return true;
    }
}
