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
 * 計測した値の表示方法を表す列挙体です。<br />
 * データ型は以下のコードで示します。<br />
 * <br />
 * <table border="1" cellspacing="0">
 * <tr>
 *  <th>表示方法</th>
 *  <th>数値</th>
 * </tr>
 * <tr>
 *  <td>不明</td>
 *  <td>-1</td>
 * </tr>
 * <tr>
 *  <td>計測値表示</td>
 *  <td>0</td>
 * </tr>
 * <tr>
 *  <td>差分表示</td>
 *  <td>1</td>
 * </tr>
 * </table>
 *
 * @author fujii
 */
public enum DisplayType
{
    /** 不明 */
    DISPLAYTYPE_UNKNOWN, // -1
    /** 計測値表示 */
    DISPLAYTYPE_NORMAL, // 0
    /** 差分表示 */
    DISPLAYTYPE_DIFFERENCE; // 1

    /**
     * 数値からデータ型を返す。
     *
     * @param n 数値
     * @return データ型
     */
    public static DisplayType getDisplayType(final byte n)
        throws IllegalArgumentException
    {
        switch (n)
        {
        case -1:
            return DISPLAYTYPE_UNKNOWN;
        case 0:
            return DISPLAYTYPE_NORMAL;
        case 1:
            return DISPLAYTYPE_DIFFERENCE;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * データ型に応じた数値を取得します。
     *
     * @param type データ型
     * @return 数値
     * @throws IllegalArgumentException IllegalArgumentException
     */
    public static byte getDisplayTypeNumber(final DisplayType type)
        throws IllegalArgumentException
    {
        switch (type)
        {
        case DISPLAYTYPE_UNKNOWN:
            return -1;
        case DISPLAYTYPE_NORMAL:
            return 0;
        case DISPLAYTYPE_DIFFERENCE:
            return 1;
        default:
            throw new IllegalArgumentException();
        }
    }
}
