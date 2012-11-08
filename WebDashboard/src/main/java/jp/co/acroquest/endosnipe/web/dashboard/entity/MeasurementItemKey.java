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

/**
 * 計測項目を一意にするキーを表すクラスです。<br />
 * 計測値種別と計測項目IDによって表します。
 * @author fujii
 *
 */
public class MeasurementItemKey
{
    /** 計測項目ID */
    public int measurementId_;

    /** 計測値種別 */
    public int measurementType_;

    /**
     * コンストラクタ。計測項目IDと計測値種別を設定します。
     * @param measurementId 計測項目ID
     * @param measurementType 計測値種別
     */
    public MeasurementItemKey(int measurementId, int measurementType)
    {
        this.measurementId_ = measurementId;
        this.measurementType_ = measurementType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + measurementId_;
        result = PRIME * result + measurementType_;
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
        MeasurementItemKey other = (MeasurementItemKey) obj;
        if (measurementId_ != other.measurementId_)
        {
            return false;
        }
        if (measurementType_ != other.measurementType_)
        {
            return false;
        }
        return true;
    }

}
