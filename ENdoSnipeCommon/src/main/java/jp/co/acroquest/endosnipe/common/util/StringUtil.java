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

import java.util.ArrayList;
import java.util.List;

/**
 * 文字列操作用ユーティリティ
 * 
 * @author eriguchi
 */
public class StringUtil
{
    /**
     * インスタンス化を禁止するためのコンストラクタ
     */
    private StringUtil()
    {

    }

    /**
     * 文字列の分割を行う。
     * 
     * @param input 分割対象の文字列
     * @param separator セパレータ
     * @return 分割結果の配列
     */
    public static List<String> split(final String input, final String separator)
    {
        List<String> result = new ArrayList<String>();
        if (input == null)
        {
            return result;
        }
        if (separator == null)
        {
            result.add(input);
            return result;
        }

        int separatorLength = separator.length();
        int fromIndex = 0;
        int separatorIndex = input.indexOf(separator, fromIndex);
        while (separatorIndex >= 0)
        {
            result.add(input.substring(fromIndex, separatorIndex));
            fromIndex = separatorIndex + separatorLength;
            separatorIndex = input.indexOf(separator, fromIndex);
        }
        if (fromIndex <= input.length())
        {
            result.add(input.substring(fromIndex, input.length()));
        }

        return result;
    }
}
