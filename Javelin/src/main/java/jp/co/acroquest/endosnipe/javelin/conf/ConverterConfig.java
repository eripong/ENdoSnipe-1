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
package jp.co.acroquest.endosnipe.javelin.conf;

/**
 * コンバータの設定
 * @author yamasaki
 *
 */
public class ConverterConfig
{
    /** コンバータを表す接頭辞 */
    public static final String PREFIX = "[Converter]";

    /** 設定ファイルでコンバータ名とコンバータのクラスを区切るためのトークン */
    private static final String NAME_TOKEN = "=";

    /** 設定ファイルでコード埋め込みクラスとコンバータを区切るためのトークン */
    private static final String CONVERTER_TOKEN = ",";

    /** ユーザが設定するコンバータ名 */
    private String name_;

    /** コンバータのクラス名 */
    private String[] converterNames_;

    /**
     * 設定ファイルを読み込む。
     * @param line 設定ファイルの行
     */
    public void readConfig(String line)
    {
        //[Converter]を除去する。
        line = line.substring(PREFIX.length());

        // ユーザの設定するコンバータ名とコンバータのクラス名を分離する。
        String[] nameAndConfig = line.split(NAME_TOKEN);
        this.name_ = nameAndConfig[0].trim();

        this.converterNames_ = nameAndConfig[1].split(CONVERTER_TOKEN);

        for (int index = 0; index < this.converterNames_.length; index++)
        {
            this.converterNames_[index] = this.converterNames_[index].trim();
        }
    }

    /**
     * ユーザの設定したコンバータ名を取得する。
     * @return コンバータ名
     */
    public String getName()
    {
        return this.name_;
    }

    /**
     * コンバータ名を設定する。
     * @param name コンバータ名
     */
    public void setName(final String name)
    {
        this.name_ = name;
    }

    /**
     * コンバータのクラス名を取得する。
     * @return コンバータのクラス名
     */
    public String[] getConverterNames()
    {
        return this.converterNames_;
    }

    /**
     * コンバータのクラス名を設定する。
     * @param converterNames コンバータのクラス名
     */
    public void setConverterNames(final String[] converterNames)
    {
        this.converterNames_ = converterNames;
    }
}
