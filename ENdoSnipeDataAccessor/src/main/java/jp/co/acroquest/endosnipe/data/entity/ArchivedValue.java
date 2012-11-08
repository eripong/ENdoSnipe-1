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

import java.sql.Timestamp;

/**
 * Javelin 計測値情報アーカイブテーブルに対するエンティティクラスです。<br />
 *
 * @author y-sakamoto
 */
public class ArchivedValue
{

    /**
     * 計測値 ID 。<br />
     *
     * 計測値を一意に識別する ID 。
     */
    public long measurementValueId;

    /**
     * 計測 No.　。<br />
     *
     * 同時に計測された計測値群を紐づけるための ID 。<br />
     *
     * 同時に計測された計測値は MESUREMENT_ID が同じ値となります。<br />
     */
    public long measurementNum;

    /**
     * 計測データを取得したホストの ID 。<br />
     */
    public int hostId;

    /**
     * 計測時刻。<br />
     */
    public Timestamp measurementTime;

    /**
     * 計測値種別。<br />
     *
     * 計測値の種別を表す値。<br />
     *
     * 計測値の表示名称については MESUREMENT_INFO テーブルを参照します。
     */
    public int measurementType;

    /**
     * 計測値が系列名を持つ場合（コレクション数など）の系列 ID 。<br />
     */
    public int measurementItemId;

    /**
     * 計測値。<br />
     *
     * 実際の計測値。
     */
    public Number value;

}
