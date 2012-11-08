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
package jp.co.acroquest.endosnipe.data;

/**
 * テーブル名称を定義するインターフェースです。<br />
 * 
 * @author y-komori
 */
public interface TableNames
{
    /** 軽食対象ホスト情報 */
    String HOST_INFO = "HOST_INFO";

    /** 計測値情報 */
    String MEASUREMENT_INFO = "MEASUREMENT_INFO";

    /** Javelin ログ */
    String JAVELIN_LOG = "JAVELIN_LOG";

    /** Javelin 計測項目 */
    String JAVELIN_MEASUREMENT_ITEM = "JAVELIN_MEASUREMENT_ITEM";

    /** Javelin 計測値 */
    String MEASUREMENT_VALUE = "MEASUREMENT_VALUE";

    /** Javelin 計測値アーカイブ */
    String ARCHIVED_VALUE = "ARCHIVED_VALUE";

    /** ログ ID の値を生成するシーケンス名。 */
    String SEQ_LOG_ID = "SEQ_LOG_ID";

    /** セッション ID の値を生成するシーケンス名。 */
    String SEQ_SESSION_ID = "SEQ_SESSION_ID";

    /** ホスト ID の値を生成するシーケンス名。 */
    String SEQ_HOST_ID = "SEQ_HOST_ID";

    /** 計測項目 ID の値を生成するシーケンス名。 */
    String SEQ_MEASUREMENT_ITEM_ID = "SEQ_MEASUREMENT_ITEM_ID";

    /** 計測 No. の値を生成するシーケンス名。 */
    String SEQ_MEASUREMENT_NUM = "SEQ_MEASUREMENT_NUM";
}
