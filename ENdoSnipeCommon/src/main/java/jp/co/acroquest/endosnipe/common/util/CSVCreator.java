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

import java.util.Collection;

/**
 * CSVを生成するユーティリティクラスです。<br />
 *
 * @author Tatsuo Suzuki
 * @author Nakamura Yuuki
 */
public class CSVCreator
{
    /**
     * {@link CSVCreator} を構築します。<br />
     */
    public CSVCreator()
    {
    }

    /**
     * String型の配列を、CSV形式に出力する。
     * 配列にnullが含まれている場合の動作は、setNullTreatment()によって決まる。
     * どのクォーテーション文字をするかは、setQuotation()によって決まる。
     *
     * @param list String型の配列
     * @return 変換されたCSV形式の文字列
     * @throws NullPointerException
     * @see #setNullTreatment()
     * @see #setQuotation()
     */
    public String createCSVString(final String[] list)
    {
        // 結果出力用のバッファ
        StringBuffer buffer = new StringBuffer();

        for (int cnt = 0; cnt < list.length; cnt++)
        {
            String element = list[cnt];
            if (element == null)
            {
                if (getNullTreatment() == CSVCreator.NULL_TO_EMPTY_STRING())
                {
                    element = "";
                }
                else
                {
                    throw new NullPointerException("list[" + cnt + "] is null");
                }
            }
            // 空文字列では無い場合、ダブルクォーテーションで囲む。
            if (element.length() > 0)
            {
                if (getQuotation() == CSVCreator.DOUBLE_QUOTATION())
                {
                    element = insertDoubleQuote(element);
                }
            }
            buffer.append(element);
            // 最後の要素で無い限り、","をつける。
            if (cnt < list.length - 1)
            {
                buffer.append(CSVCreator.SEPARATOR);
            }
        }
        return buffer.toString();
    }

    /**
     * @param element 元の文字列。
     * @return 前後にダブルクォート文字を付加した文字列。
     */
    private String insertDoubleQuote(final String element)
    {
        // 文字列にダブルクォーテーションが含まれていたら、ダブルクォーテーション
        // 二つに置き換える。
        StringBuffer tmpBuf = new StringBuffer();
        for (int cnt = 0; cnt < element.length(); cnt++)
        {
            char chr = element.charAt(cnt);
            tmpBuf.append(chr);
            if (chr == CSVCreator.DOUBLE_QUOTE_CHAR)
            {
                tmpBuf.append(CSVCreator.DOUBLE_QUOTE_CHAR);
            }
        }
        return tmpBuf.toString();
    }

    /**
     * コレクションを CSV 形式に変換します。<br />
     *
     * @param list コレクション
     * @return コレクションのすべての要素をカンマで結合した文字列
     * @see #createCSVString(String[])
     */
    public String createCSVString(final Collection<?> list)
    {
        return createCSVString(list.toArray(new String[0]));
    }

    //:=====================================================

    /**
     * String型の配列を、CSV形式に出力する。
     * 配列にnullが含まれている場合は、NullPointerExceptionが発生する。
     * 互換性のテストが完了したら、このメソッドの実装をcreateCSVString(String[])に置き換える。
     *
     * @param list String型の配列
     * @return 変換されたCSV形式の文字列
     */
    public static String createCSV(final String[] list)
    {
        try
        {
            // 結果出力用のバッファ
            StringBuffer buffer = new StringBuffer();

            for (int listCnt = 0; listCnt < list.length; listCnt++)
            {
                String element = list[listCnt];

                // 空文字列では無い場合、ダブルクォーテーションで囲む。
                if (element.length() > 0)
                {
                    // 文字列にダブルクォーテーションが含まれていたら、ダブルクォーテーション
                    // 二つに置き換える。
                    if (element.indexOf("\"") > -1)
                    {
                        StringBuffer tmpBuffer = new StringBuffer();
                        for (int cnt = 0; cnt < element.length(); cnt++)
                        {
                            char chr = element.charAt(cnt);
                            tmpBuffer.append(chr);

                            if (chr == '\"')
                            {
                                tmpBuffer.append('\"');
                            }
                        }
                        element = tmpBuffer.toString();
                    }

                    buffer.append("\"" + element + "\"");
                }

                // 最後の要素で無い限り、","をつける。
                if (listCnt < list.length - 1)
                {
                    buffer.append(",");
                }
            }

            return buffer.toString();
        }
        catch (NullPointerException ex)
        {
            throw ex;
        }

    }

    /**
     * Collectionを、CSV形式に出力する。
     * Collectionにnullが含まれている場合は、NullPointerExceptionが発生する。
     * 文字列型に変換できない場合は、ArrayStoreExceptionが発生する。
     * 互換性のテストが完了したら、このメソッドの実装をcreateCSVString(Collection)に置き換える。
     *
     * @param list Collection
     * @return 変換されたCSV形式の文字列
     */
    public static String createCSV(final Collection<?> list)
    {
        try
        {
            String[] arrays = list.toArray(new String[0]);

            return createCSV(arrays);
        }
        catch (ArrayStoreException aex)
        {
            throw aex;
        }
        catch (NullPointerException nex)
        {
            throw nex;
        }
    }

    /**
     * クォーテーションをセットします。<br />
     *
     * @param newQuotation クォーテーション
     */
    public void setQuotation(final int newQuotation)
    {
        quotation_ = newQuotation;
    }

    /**
     * クォーテーションを返します。<br />
     *
     * デフォルト値は DOUBLE_QUOTATION 。
     *
     * @return クォーテーション
     */
    public int getQuotation()
    {
        return quotation_;
    }

    public void setNullTreatment(final int newNullTreatment)
    {
        nullTreatment_ = newNullTreatment;
    }

    /**
     * デフォルト値は NULL_POINTER_EXCEPTION 。
     */
    public int getNullTreatment()
    {
        return nullTreatment_;
    }

    private int quotation_ = CSVCreator.DOUBLE_QUOTATION();

    private int nullTreatment_ = CSVCreator.NULL_POINTER_EXCEPTION();

    /**
     * nullに遭遇した場合、NullPointerExceptionを投げる指示。
     */
    public static final int NULL_POINTER_EXCEPTION()
    {
        return 1 << CSVCreator.NULL_TREATMENT_GROUP;
    }

    /**
     * nullに遭遇した場合、""として扱う指示。
     */
    public static final int NULL_TO_EMPTY_STRING()
    {
        return 2 << CSVCreator.NULL_TREATMENT_GROUP;
    }

    /**
     * nullに遭遇した場合の指示のビット範囲。
     */
    private static final int NULL_TREATMENT_GROUP = 0;

    /**
     * クォーテーションを付加しない指示。
     */
    public static final int NO_QUOTATION()
    {
        return 1 << CSVCreator.QUOTATION_GROUP;
    }

    /**
     * ダブルクォーテーション"\""を付加する指示。
     */
    public static final int DOUBLE_QUOTATION()
    {
        return 2 << CSVCreator.QUOTATION_GROUP;
    }

    /**
     * クォーテーションを付加する指示のビット範囲。
     */
    private static final int QUOTATION_GROUP = 4;

    /**
     * CSV文字列の区切り文字。
     */
    private static final String SEPARATOR = ",";

    /**
     * 文字列中にあるダブルクォーテーションに付加する文字。
     */
    private static final char DOUBLE_QUOTE_CHAR = '\"';
}
