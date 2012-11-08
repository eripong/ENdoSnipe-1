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
package jp.co.acroquest.endosnipe.web.dashboard.entity;

import java.util.Date;
import java.util.List;

import net.arnx.jsonic.JSONHint;

/**
 * 計測項目自動通知開始要求オブジェクト
 * 
 * @author fujii
 */
public class TermMeasurementEntity
{
    /** イベントID */
    public long                      event_id;

    /** グラフID */
    public long                      graph_id;

    /** 計測時刻のリスト */
    @JSONHint(format="yyyy/MM/dd HH:mm:ss")
    public List<Date>                timestamps;

    /** エージェント毎の計測データ */
    public List<TermMeasurementData> measurement_data;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "TermMeasurementEntity [event_id=" + event_id + ", graph_id="
                + graph_id + ", timestamps=" + timestamps.toString()
                + ", measurement_data=" + measurement_data.toString() + "]";
    }
}
