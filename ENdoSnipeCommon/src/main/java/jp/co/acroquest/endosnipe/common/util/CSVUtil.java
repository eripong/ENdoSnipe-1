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
package jp.co.acroquest.endosnipe.common.util;

import java.util.List;

/**
 * CSV 形式のデータを操作するためのユーティリティクラス。
 *
 * @author y-sakamoto
 */
public class CSVUtil
{
    private CSVUtil()
    {
    }

    /**
     * 指定されたリストの要素を CSV 形式の1行文字列に結合します。<br />
     *
     * @param list リスト
     * @return CSV 形式の1行文字列
     */
    public static String createLine(final List<?> list)
    {
        StringBuffer line = new StringBuffer();
        for (Object element : list)
        {
            String elementString = element.toString();
            elementString = elementString.replaceAll("\"", "\"\"");
            if (elementString.contains(",") == true || elementString.contains("\"") == true)
            {
                elementString = "\"" + elementString + "\"";
            }
            line.append(elementString);
            line.append(',');
        }
        // 末尾のカンマを削除する
        if (line.length() > 0)
        {
            line.deleteCharAt(line.length() - 1);
        }
        return line.toString();
    }

}
