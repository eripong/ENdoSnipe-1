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

import java.util.List;

/**
 * 計測項目詳細オブジェクト
 * 
 * @author fujii
 */
public class TermMeasurementDetailData
{
    /** 計測項目ID */
    public long         measurement_id = -1;

    /** 項目名 */
    public String      item_name;

    /** 計測項目タイプ */
    public int          measurement_type;

    /** 計測項目値(Stringに変換して使うこと) */
    public List<String> measurement_values;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "TermMeasurementDetailData [id=" + measurement_id + ", item_name=" + this.item_name
                + ", type=" + measurement_type + ", values=" + measurement_values.toString() + "]";
    }

}
