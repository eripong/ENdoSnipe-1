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
package jp.co.acroquest.endosnipe.data.entity;

/**
 * 計測値情報テーブルに対するイミュータブルなエンティティクラスです。<br />
 *
 * @author y-sakamoto
 */
public class MeasurementInfo
{

    /**
     * 計測値種別を表す ID 。<br />
     *
     * JAVELIN_MESUREMENT テーブルの MESUREMENT_TYPE カラムに利用される値。
     */
    private final long measurementType_;

    /**
     * リソース通知電文の項目名。<br />
     */
    private final String itemName_;

    /**
     * 計測値の表示名称。<br />
     *
     * BottleneckEye などの解析アプリケーションで表示するときに利用します。
     */
    private final String displayName_;

    /**
     * 計測値に関する説明。<br />
     *
     * 解析アプリケーションで計測値の詳細説明を表示するときに利用します。
     */
    private final String description_;

    /**
     * 計測値情報テーブルに対するエンティティオブジェクトを生成します。<br />
     *
     * @param measurementType 計測値種別
     * @param itemName リソース通知電文の項目名
     * @param displayName 計測値の表示名称
     * @param description 計測値に関する説明
     */
    public MeasurementInfo(final long measurementType, final String itemName,
            final String displayName, final String description)
    {
        this.measurementType_ = measurementType;
        this.itemName_ = itemName;
        this.displayName_ = displayName;
        this.description_ = description;
    }

    /**
     * 計測値種別を返します。<br />
     *
     * @return 計測値種別
     */
    public long getMeasurementType()
    {
        return this.measurementType_;
    }

    /**
     * リソース通知電文の項目名を返します。<br />
     *
     * @return リソース通知電文の項目名
     */
    public String getItemName()
    {
        return this.itemName_;
    }

    /**
     * 計測値の表示名称を返します。<br />
     *
     * @return 計測値の表示名称
     */
    public String getDisplayName()
    {
        return this.displayName_;
    }

    /**
     * 計測値に関する説明を返します。<br />
     *
     * @return 計測値に関する説明
     */
    public String getDescription()
    {
        return this.description_;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("{measurementType=");
        builder.append(this.measurementType_);
        builder.append(",itemName=");
        builder.append(this.itemName_);
        builder.append("}");
        return builder.toString();
    }
}
