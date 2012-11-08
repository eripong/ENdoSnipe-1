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
package jp.co.acroquest.endosnipe.data.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.co.acroquest.endosnipe.data.dto.MeasurementValueDto;

/**
 * GC 時間を扱うフィルタ。<br />
 *
 * @author fujii
 */
public class GarbageCollectionTimeDataFilter
{
    private static final long VALUE_NOT_SET = -1;

    private long prevGcTotalTime_ = VALUE_NOT_SET;

    /**
     * MeasurementDataから値を変換する。
     * 
     * @param gcTotalTimeList 変換対象の計測データ。
     * @return 変換後のデータ。
     */
    public List<MeasurementValueDto> filter(final List<MeasurementValueDto> gcTotalTimeList)
    {
        if (gcTotalTimeList == null || gcTotalTimeList.size() == 0)
        {
            return null;
        }

        List<MeasurementValueDto> resultList = new ArrayList<MeasurementValueDto>();

        Iterator<MeasurementValueDto> iterator = gcTotalTimeList.iterator();
        if (this.prevGcTotalTime_ == VALUE_NOT_SET)
        {
            MeasurementValueDto valueObject = iterator.next();
            this.prevGcTotalTime_ = Long.valueOf(valueObject.value);
            resultList.add(valueObject);
        }
        
        while (iterator.hasNext())
        {
            MeasurementValueDto valueObject = iterator.next();
            long nowGcTotalTime = Long.valueOf(valueObject.value);
            long plotValue = nowGcTotalTime - this.prevGcTotalTime_;
            if (plotValue > 0)
            {
                valueObject.value = String.valueOf(plotValue);
            }
            else
            {
                valueObject.value = "0";
            }
            this.prevGcTotalTime_ = nowGcTotalTime;
            resultList.add(valueObject);
        }
        return resultList;
    }
}
