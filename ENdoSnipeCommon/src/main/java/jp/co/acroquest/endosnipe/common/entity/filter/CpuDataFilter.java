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
package jp.co.acroquest.endosnipe.common.entity.filter;

import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.Constants;
import jp.co.acroquest.endosnipe.common.entity.MeasurementData;
import jp.co.acroquest.endosnipe.common.entity.MeasurementDetail;
import jp.co.acroquest.endosnipe.common.entity.series.GraphResource;
import jp.co.acroquest.endosnipe.common.entity.series.GraphResourceEntry;

/**
 * CPU使用時間から、CPU使用率を算出するフィルタ。<br/>
 *
 * @author akiba
 */
public class CpuDataFilter // extends GraphDataFilter
{
    private static final long   VALUE_NOT_SET = -1;

    private static final double MAX_CPU_RATE  = 100.0;

    private long                prevCpuTime_  = VALUE_NOT_SET;

    private long                prevUpTime_   = VALUE_NOT_SET;

    /**
     * CPU 使用率を扱うフィルタを生成します。<br />
     */
    public CpuDataFilter()
    {
        super();
    }

    /**
     * MeasurementDataから値を変換する。
     * 
     * @param valuesMap 変換対象の計測データ。
     * @return 変換後のデータ。
     */
    public GraphResource filter(final List<MeasurementData> valuesMap)
    {
        if (valuesMap == null ||
            valuesMap.size() == 0)
        {
            return null;
        }

        Map<String, MeasurementDetail> detailMap = valuesMap.get(0).getMeasurementDetailMap();
        MeasurementDetail processorCountObject =
                detailMap.get(Constants.ITEMNAME_SYSTEM_CPU_PROCESSOR_COUNT);

        GraphResource graphResource = new GraphResource("");
        int index = 0;
        if (this.prevCpuTime_ == VALUE_NOT_SET ||
            this.prevUpTime_ == VALUE_NOT_SET)
        {
            detailMap = valuesMap.get(index).getMeasurementDetailMap();

            MeasurementDetail cpuTimeObject =
                    detailMap.get(Constants.ITEMNAME_PROCESS_CPU_TOTAL_TIME);
            MeasurementDetail upTimeObject = detailMap.get(Constants.ITEMNAME_JAVAUPTIME);
            this.prevCpuTime_ = Long.valueOf(cpuTimeObject.value).longValue();
            this.prevUpTime_ = Long.valueOf(upTimeObject.value).longValue();
            index++;
        }

        while (index < valuesMap.size())
        {
            detailMap = valuesMap.get(index).getMeasurementDetailMap();

            MeasurementDetail cpuTimeObject =
                    detailMap.get(Constants.ITEMNAME_PROCESS_CPU_TOTAL_TIME);
            MeasurementDetail upTimeObject = detailMap.get(Constants.ITEMNAME_JAVAUPTIME);
            long nowCpuTime = Long.valueOf(cpuTimeObject.value).longValue();
            long nowUpTime = Long.valueOf(upTimeObject.value).longValue();
            if (nowUpTime != this.prevUpTime_)
            {
                long processorCount = Long.valueOf(processorCountObject.value).longValue();
                long time = valuesMap.get(index).measurementTime.getTime();
                double cpuUsage = (nowCpuTime - this.prevCpuTime_) /
                                  ((nowUpTime - this.prevUpTime_) * 10000.0 * processorCount);
                GraphResourceEntry entry = new GraphResourceEntry(time, Double.valueOf(cpuUsage));
                graphResource.addGraphResourceEntry(null, entry);
                this.prevUpTime_ = nowUpTime;
            }
            this.prevCpuTime_ = nowCpuTime;
            index++;
        }
        graphResource.setMaxValue(MAX_CPU_RATE);

        return graphResource;
    }

    /**
     * 値をクリアする。
     */
    public void clear()
    {
        this.prevCpuTime_ = VALUE_NOT_SET;
        this.prevUpTime_ = VALUE_NOT_SET;
    }
}
