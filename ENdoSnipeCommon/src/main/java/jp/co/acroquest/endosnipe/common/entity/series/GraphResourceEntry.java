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
package jp.co.acroquest.endosnipe.common.entity.series;

/**
 * グラフに表示するリソースの　1　つの点を表します。<br />
 *
 * 点の情報は、時刻と値から構成されます。<br />
 *
 * @author eriguchi
 */
public class GraphResourceEntry
{
    /** この値が取得された時刻（ミリ秒） */
    private final long time_;

    /** 値 */
    private final Number value_;

    /**
     * グラフに表示するリソースの 1 つの点を生成します。<br />
     *
     * @param time この値が取得された時刻（ミリ秒）
     * @param value 値
     */
    public GraphResourceEntry(final long time, final Number value)
    {
        this.time_ = time;
        this.value_ = value;
    }

    /**
     * {@link GraphResourceEntry} のコピーコンストラクタです。<br />
     *
     * @param entry コピーするオブジェクト
     */
    public GraphResourceEntry(final GraphResourceEntry entry)
    {
        this.time_ = entry.time_;
        this.value_ = entry.value_;
    }

    /**
     * この値が取得された時刻をミリ秒で返します。<br />
     *
     * @return 時刻
     */
    public long getTime()
    {
        return this.time_;
    }

    /**
     * 値を取得する。
     * 
     * @return 値。
     */
    public Number getValue()
    {
        return this.value_;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("{time=");
        builder.append(getTime());
        builder.append(",value=");
        builder.append(getValue());
        builder.append("}");
        return builder.toString();
    }

}
