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
package jp.co.acroquest.endosnipe.perfdoctor.classfier;

import java.io.Serializable;
import java.util.Comparator;

import jp.co.acroquest.endosnipe.perfdoctor.WarningUnit;

/**
 * メソッドの開始時刻、終了時刻を基に並び変える。
 * @author fujii
 *
 */
public class FileLineComparator implements Comparator<WarningUnit>, Serializable
{
    /** シリアルID */
    private static final long serialVersionUID = 1L;

    /**
     * ファイルの行数を比較し、行数が遅いものを優先する。
     * @param unit1 比較対象のWarningUnit
     * @param unit2 比較対象のWarningUnit
     * @return 1:2番目の警告ルールのファイル行数が大きい、-1:1番目の警告ルールのファイル行数が大きい、
     *         0:ファイル行数が同じ。
     */
    public int compare(final WarningUnit unit1, final WarningUnit unit2)
    {
        int lineNumber1 = unit1.getLogFileLineNumber();
        int lineNumber2 = unit2.getLogFileLineNumber();
        if (lineNumber1 > lineNumber2)
        {
            return -1;
        }
        else if (lineNumber1 < lineNumber2)
        {
            return 1;
        }
        return 0;
    }
}