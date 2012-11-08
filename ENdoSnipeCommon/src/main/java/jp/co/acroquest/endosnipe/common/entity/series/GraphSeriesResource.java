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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * グラフの 1 系列を表すクラス。<br />
 *
 * @author sakamoto
 */
public class GraphSeriesResource implements Iterable<GraphResourceEntry>
{

    /** 系列名 */
    private final String seriesName_;

    private final List<GraphResourceEntry> entries_;

    /**
     * グラフの 1 系列を表すオブジェクトを生成します。<br />
     *
     * @param seriesName 系列名
     */
    public GraphSeriesResource(final String seriesName)
    {
        this.seriesName_ = seriesName;
        this.entries_ = new ArrayList<GraphResourceEntry>();
    }

    /**
     * グラフの 1 系列を表すオブジェクトを生成します。<br />
     *
     * @param seriesName 系列名
     * @param entryList 系列データ
     */
    public GraphSeriesResource(final String seriesName, final List<GraphResourceEntry> entryList)
    {
        this.seriesName_ = seriesName;
        this.entries_ = new ArrayList<GraphResourceEntry>(entryList);
    }

    /**
     * 系列に値を 1 つ追加します。<br />
     *
     * @param entry 追加する値
     */
    public void addGraphResourceEntry(final GraphResourceEntry entry)
    {
        this.entries_.add(entry);
    }

    /**
     * 系列に値を 1 つ追加します。<br />
     *
     * @param time 追加する値が取得された時刻（ミリ秒）
     * @param value 追加する値
     */
    public void addGraphResourceEntry(final long time, final Number value)
    {
        GraphResourceEntry entry = new GraphResourceEntry(time, value);
        addGraphResourceEntry(entry);
    }

    /**
     * 系列名を返します。<br />
     *
     * @return 系列名
     */
    public String getSeriesName()
    {
        return this.seriesName_;
    }

    /**
     * 系列に存在する値の数を返します。<br />
     *
     * @return 値の数
     */
    public int getValueCount()
    {
        return this.entries_.size();
    }

    /**
     * 系列内のデータのリストを返します。<br />
     *
     * 返されたデータは参照のみ可能で、追加や削除は行えません。<br />
     *
     * @return 系列内のデータのリスト
     */
    public List<GraphResourceEntry> getEntryList()
    {
        return Collections.unmodifiableList(this.entries_);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<GraphResourceEntry> iterator()
    {
        return this.entries_.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("{seriesName=");
        builder.append(getSeriesName());
        builder.append(",dataCount=");
        builder.append(this.entries_.size());
        builder.append("}");
        return builder.toString();
    }
}
