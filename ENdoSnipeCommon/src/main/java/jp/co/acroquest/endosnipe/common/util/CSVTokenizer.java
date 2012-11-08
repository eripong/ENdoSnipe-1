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

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * 1行のCSV形式のデータを解析し、それぞれの項目に分解するクラス。
 * CSV形式に対応した java.util.StringTokenizer のようなもの。
 *
 *－－－－－－－－－－－－－仕様－－－－－－－－－－－－－－－－－
 *　・セパレータは、半角カンマ(,)を用いる。
 *
 *　・各要素は、半角のダブルクォーテーション(")で囲んでも囲まなくても良い。
 *　　ただし、
 *　　　→　要素内に半角カンマを含む場合は、ダブルクォーテーションで
 *　　　　　囲まなくてはならない。
 *
 *　・ダブルクォーテーションを文字列の一部として認識させる場合は、
 *　　連続したダブルクォーテーションを用いる。
 *　　単独のダブルクォーテーションがあった場合の動作は保証しない。
 *
 *　・セパレータの両側には、スペースを入れた場合は、
 *　　スペースとして認識される。
 * @author unknown
 */
public class CSVTokenizer implements Enumeration<String>
{
    private final String source_; // 対象となる文字列

    private int currentPosition_; // 次の読み出し位置

    private final int maxPosition_;

    /**
     * CSV 形式の line を解析する CSVTokenizer のインスタンスを
     * 作成する。
     *
     * @param line CSV形式の文字列  改行コードを含まない。
     */
    public CSVTokenizer(final String line)
    {
        source_ = line.trim();
        currentPosition_ = 0;
        maxPosition_ = source_.length();
    }

    /**
     * 次のカンマがある位置を返す。
     * カンマが残っていない場合は nextComma() == maxPosition となる。
     * また最後の項目が空の場合も nextComma() == maxPosition となる。
     *
     * @param ind 検索を開始する位置
     * @return 次のカンマがある位置。カンマがない場合は、文字列の
     * 長さの値となる。
     */
    private int nextComma(int ind)
    {
        boolean inquote = false;
        while (ind < maxPosition_)
        {
            char ch = source_.charAt(ind);
            if (!inquote && ch == ',')
            {
                break;
            }
            else if ('"' == ch)
            {
                inquote = !inquote; // ""の処理もこれでOK
            }
            ind++;
        }
        return ind;
    }

    /**
     * 含まれている項目の数を返す。
     *
     * @return 含まれている項目の数
     */
    public int countTokens()
    {
        int i = 0;
        int ret = 1;
        while ((i = nextComma(i)) < maxPosition_)
        {
            i++;
            ret++;
        }
        return ret;
    }

    /**
     * 次の項目の文字列を返す。
     *
     * @return 次の項目
     */
    public String nextToken()
    {
        // ">=" では末尾の項目を正しく処理できない。
        // 末尾の項目が空（カンマで1行が終わる）場合、例外が発生して
        // しまうので。
        if (currentPosition_ > maxPosition_)
        {
            throw new NoSuchElementException(toString() + "#nextToken");
        }

        int st = currentPosition_;
        currentPosition_ = nextComma(currentPosition_);

        StringBuffer strb = new StringBuffer();
        boolean inquote = false;

        while (st < currentPosition_)
        {
            char ch = source_.charAt(st++);
            if (ch == '"')
            {
                // quoteの外であれば、いつでもquoteの中に入る
                if (inquote == false)
                {
                    inquote = true;
                }
                // quote内であり、その次の文字も"であれば、1文字分の"として扱う
                // "が単独で現れたときは何もしない
                else if ((st < currentPosition_) && (source_.charAt(st) == '"'))
                {
                    strb.append(ch);
                    st++;
                }
                // それ以外であればquoteを出る。
                else
                {
                    inquote = false;
                }
            }
            else
            {
                strb.append(ch);
            }
        }
        currentPosition_++;
        return new String(strb);
    }

    /**
     * <code>nextToken</code>メソッドと同じで、
     * 次の項目の文字列を返す。<br>
     * ただし返値は、String型ではなく、Object型である。<br>
     * java.util.Enumerationを実装しているため、このメソッドが
     * ある。
     *
     * @return 次の項目
     * @see java.util.Enumeration
     */
    public String nextElement()
    {
        return nextToken();
    }

    /**
     * まだ項目が残っているかどうか調べる。
     *
     * @return まだ項目がのこっているならtrue
     */
    public boolean hasMoreTokens()
    {
        // "<=" でなく、"<" だと末尾の項目を正しく処理できない。
        return (nextComma(currentPosition_) <= maxPosition_);
    }

    /**
     * <code>hasMoreTokens</code>メソッドと同じで、
     * まだ項目が残っているかどうか調べる。<br>
     * java.util.Enumerationを実装しているため、このメソッドが
     * ある。
     *
     * @return まだ項目がのこっているならtrue
     * @see java.util.Enumeration
     */
    public boolean hasMoreElements()
    {
        return hasMoreTokens();
    }

    /**
     * インスタンスの文字列表現を返す。
     *
     * @return インスタンスの文字列表現。
     */
    @Override
    public String toString()
    {
        return "CSVTokenizer(\"" + source_ + "\")";
    }
}
