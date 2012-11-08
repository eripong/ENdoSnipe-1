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

/**
 * メソッドの統計情報
 * @author eriguchi
 *
 */
public class InvocationInterval implements Serializable
{
    /** シリアルID */
    private static final long serialVersionUID = 7855547784390235717L;

    /** 初期値 */
    private static final long INITIAL = -1;

    /** メソッドの時刻時間 */
    private long interval_;

    /** メソッドのCPU時間 */
    private long cpuInterval_;

    /** メソッドのユーザ時間 */
    private long userInterval_;

    /**
     * コンストラクタ
     */
    public InvocationInterval()
    {
        this.interval_ = INITIAL;
        this.cpuInterval_ = INITIAL;
        this.userInterval_ = INITIAL;
    }

    /**
     * メソッドの実行時間を取得する。
     * @return メソッドの実行時間
     */
    public long getInterval()
    {
        return this.interval_;
    }

    /**
     * メソッドの実行時間を設定する。
     * @param interval メソッドの実行時間
     */
    public void setInterval(final long interval)
    {
        this.interval_ = interval;
    }

    /**
     * メソッドのCPU時間を取得する。
     * @return メソッドのCPU時間
     */
    public long getCpuInterval()
    {
        return this.cpuInterval_;
    }

    /**
     * メソッドのCPU時間を設定する。
     * @param cpuInterval メソッドのCPU時間
     */
    public void setCpuInterval(final long cpuInterval)
    {
        this.cpuInterval_ = cpuInterval;
    }

    /**
     * メソッドのユーザ時間を取得する。
     * @return メソッドのユーザ時間
     */
    public long getUserInterval()
    {
        return this.userInterval_;
    }

    /**
     * メソッドのユーザ時間を設定する。
     * @param userInterval メソッドのユーザ時間
     */
    public void setUserInterval(final long userInterval)
    {
        this.userInterval_ = userInterval;
    }

    /**
     * コンストラクタ
     * @param interval メソッドの実行時間
     * @param cpuInterval メソッドのCPU時間
     * @param userInterval メソッドのユーザ時間
     */
    public InvocationInterval(final long interval, final long cpuInterval, final long userInterval)
    {
        super();
        this.interval_ = interval;
        this.cpuInterval_ = cpuInterval;
        this.userInterval_ = userInterval;
    }
}
