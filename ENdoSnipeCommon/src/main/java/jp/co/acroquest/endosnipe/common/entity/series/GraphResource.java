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
import java.util.Iterator;
import java.util.List;

/**
 * 特定のグラフのリソース値を表すクラス。<br />
 *
 * グラフ内の系列の値をすべて持ちます。<br />
 *
 * @author sakamoto
 */
public class GraphResource implements Iterable<GraphSeriesResource>
{
    private final String graphName_;

    private final List<GraphSeriesResource> seriesList_;

    private Number maxValue_;

    /**
     * 特定のグラフ内のすべての値を持つオブジェクトを生成します。<br />
     *
     * @param graphName グラフ名
     */
    public GraphResource(final String graphName)
    {
        this.graphName_ = graphName;
        this.maxValue_ = null;
        this.seriesList_ = new ArrayList<GraphSeriesResource>();
    }

    /**
     * グラフの名前を返します。<br />
     *
     * @return グラフの名前
     */
    public String getGraphName()
    {
        return this.graphName_;
    }

    /**
     * 縦軸の最大値を返します。<br />
     *
     * @return 縦軸の最大値
     */
    public Number getMaxValue()
    {
        return this.maxValue_;
    }

    /**
     * 縦軸の最大値をセットします。<br />
     *
     * @param maxValue 縦軸の最大値
     */
    public void setMaxValue(final Number maxValue)
    {
        this.maxValue_ = maxValue;
    }

    /**
     * 系列数を返します。<br />
     *
     * @return 系列数
     */
    public int getSeriesCount()
    {
        return this.seriesList_.size();
    }

    /**
     * グラフに値を追加します。<br />
     *
     * @param seriesName 系列名
     * @param entry 追加する値
     */
    public void addGraphResourceEntry(final String seriesName, final GraphResourceEntry entry)
    {
        GraphSeriesResource series = getSeries(seriesName);
        if (series == null)
        {
            series = new GraphSeriesResource(seriesName);
            this.seriesList_.add(series);
        }
        series.addGraphResourceEntry(entry);
    }

    /**
     * 指定された系列のグラフに値を追加します。<br />
     *
     * @param seriesIndex 系列番号（系列番号が系列数以上の場合、間に空の系列を挿入する）
     * @param entry 追加する値
     */
    public void addGraphResourceEntry(final int seriesIndex, final GraphResourceEntry entry)
    {
        while (this.seriesList_.size() <= seriesIndex)
        {
            this.seriesList_.add(new GraphSeriesResource(null));
        }
        GraphSeriesResource series = this.seriesList_.get(seriesIndex);
        series.addGraphResourceEntry(entry);
    }

    /**
     * 系列を追加します。<br />
     *
     * @param series 系列
     */
    public void addSeries(final GraphSeriesResource series)
    {
        this.seriesList_.add(series);
    }

    /**
     * 系列の値を返します。<br />
     *
     * @param seriesName 系列名
     * @return 系列の値（指定された系列が存在しない場合は <code>null</code> ）
     */
    public GraphSeriesResource getSeries(final String seriesName)
    {
        for (GraphSeriesResource series : this.seriesList_)
        {
            if (seriesName == null)
            {
                if (series.getSeriesName() == null)
                {
                    return series;
                }
            }
            else if (seriesName.equals(series.getSeriesName()))
            {
                return series;
            }
        }
        return null;
    }

    /**
     * インデックスを指定して系列の値を返します。<br />
     *
     * @param index インデックス
     * @return 最新のデータの時刻
     */
    public GraphSeriesResource getSeries(final int index)
    {
        return this.seriesList_.get(index);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<GraphSeriesResource> iterator()
    {
        return this.seriesList_.iterator();
    }

}
