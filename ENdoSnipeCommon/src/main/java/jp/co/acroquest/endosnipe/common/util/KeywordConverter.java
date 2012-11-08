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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 文字列に含まれるキーワードを変換するクラス。</br>
 * キーワードのPrefix、Suffixを指定することができる。
 * addConverterメソッドを利用してキーワードの置換文字列を指定し、
 * convertメソッドで置換する。
 * 
 * @author tsukano
 */
public class KeywordConverter
{
    /** キーワードのPrefix */
    private final String keywordPrefix_;

    /** キーワードのSuffix */
    private final String keywordSuffix_;

    /** キーワードを変換する文字列を定義したリスト */
    private final Map<String, String> converterMap_ = new LinkedHashMap<String, String>();

    /**
     * Prefix、Suffixなしの変換クラスを生成する。</br>
     */
    public KeywordConverter()
    {
        this.keywordPrefix_ = "";
        this.keywordSuffix_ = "";
    }

    /**
     * Prefix、Suffixを指定して変換クラスを生成する。</br>
     * 
     * @param keywordPrefix キーワードのPrefix
     * @param keywordSuffix キーワードのSuffix
     */
    public KeywordConverter(final String keywordPrefix, final String keywordSuffix)
    {
        this.keywordPrefix_ = keywordPrefix;
        this.keywordSuffix_ = keywordSuffix;
    }

    /**
     * キーワードと置換文字列を追加する。</br>
     * 
     * @param keyword キーワード
     * @param convertedString キーワードの置換文字列
     */
    public void addConverter(final String keyword, final String convertedString)
    {
        converterMap_.put(keywordPrefix_ + keyword + keywordSuffix_, convertedString);
    }

    /**
     * キーワードと置換文字列を追加する。</br>
     * 置換文字列にint値を設定する為の簡易メソッド。
     * 
     * @param keyword キーワード
     * @param convertedValue キーワードの置換文字列(int値)
     */
    public void addConverter(final String keyword, final int convertedValue)
    {
        addConverter(keyword, String.valueOf(convertedValue));
    }

    /**
     * キーワードと置換文字列を追加する。</br>
     * 置換文字列にlong値を設定する為の簡易メソッド。
     * 
     * @param keyword キーワード
     * @param convertedValue キーワードの置換文字列(long値)
     */
    public void addConverter(final String keyword, final long convertedValue)
    {
        addConverter(keyword, String.valueOf(convertedValue));
    }

    /**
     * キーワードと置換文字列を追加する。</br>
     * 置換文字列にObjectの文字列を設定する為の簡易メソッド。
     * toString()を実装しているObjectならばその出力で置換する。
     * 
     * @param keyword キーワード
     * @param convertedValue キーワードの置換文字列(Object)
     */
    public void addConverter(final String keyword, final Object convertedValue)
    {
        addConverter(keyword, String.valueOf(convertedValue));
    }

    /**
     * 登録した置換文字列にキーワードを置換する。</br>
     * 
     * @param source 置換前の文字列
     * @return 置換後の文字列
     */
    public String convert(final String source)
    {
        String retValue = source;

        // 登録してある情報を利用して置換する
        Set<Map.Entry<String, String>> entries = converterMap_.entrySet();
        for (Map.Entry<String, String> entry : entries)
        {
            if (entry.getValue() == null)
            {
                retValue = retValue.replace(entry.getKey(), "null");
            }
            else
            {
                retValue = retValue.replace(entry.getKey(), entry.getValue());
            }
        }

        // 置換後の文字列を返す
        return retValue;
    }
}
