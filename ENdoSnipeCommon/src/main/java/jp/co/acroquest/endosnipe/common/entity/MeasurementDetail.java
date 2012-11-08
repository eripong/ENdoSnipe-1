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
package jp.co.acroquest.endosnipe.common.entity;

/**
 * 計測データを格納するためのエンティティです。
 * @author fujii
 */
public class MeasurementDetail
{
    /** 計測値 */
    public String value;

    /** 系列の表示名 */
    public String displayName;

    /** 計測値が系列名を持つ場合（コレクション数など）の系列 ID 。 */
    public int    itemId;

    /** 計測値系列名称。 */
    public String itemName;

    /** 計測値の表示型 */
    public int    displayType;

    /**
     * 計測 No.。<br/>
     * 同時に計測された計測値群を紐づけるための ID 。<br/>
     * 同時に計測された計測値は MESUREMENT_ID が同じ値となります。
     */
    public long   itemNum;

    /**
     * 計測値種別。<br/>
     * 計測値の種別を表す値。<br/>
     * 計測値の表示名称については MESUREMENT_INFO テーブルを参照します。
     */
    public int    type;

    /** 計測値系列名称。 */
    public String typeItemName;

    /**
     * 計測値 ID 。<br/>
     * 計測値を一意に識別する ID 。
     */
    public long   valueId;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DisplayName=");
        builder.append(this.displayName);
        builder.append(",Value=");
        builder.append(this.value);
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeasurementDetail clone()
    {
        MeasurementDetail cloneObj = new MeasurementDetail();

        cloneObj.displayName = this.displayName;
        cloneObj.itemId = this.itemId;
        cloneObj.itemName = this.itemName;
        cloneObj.itemNum = this.itemNum;
        cloneObj.type = this.type;
        cloneObj.typeItemName = this.typeItemName;
        cloneObj.value = this.value;
        cloneObj.valueId = this.valueId;
        cloneObj.displayType = this.displayType;

        return cloneObj;
    }
}
