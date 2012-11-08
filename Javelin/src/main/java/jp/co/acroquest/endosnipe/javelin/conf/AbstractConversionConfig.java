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

import java.util.regex.Pattern;

/**
 * Include/Exclude設定を扱う抽象クラス。
 *
 * @author yamasaki
 */
public abstract class AbstractConversionConfig
{
    /** 継承用設定を行うための接頭辞 */
    private static final String INHERITANCE = "[inheritance]";

    /** 継承用設定かどうかを示すフラグ。 */
    private boolean isInheritance_ = false;

    /** クラス名のパターン。 */
    private String className_;

    /** メソッド名のパターン。 */
    private String methodNamePattern_;

    /** クラス名のパターン。 */
    private Pattern classNamePattern_;

    /**
     * 設定ファイルを読み込む
     * @param configLine 設定ファイルの行
     */
    public void readConfig(final String configLine)
    {
        if (configLine == null || configLine.length() == 0)
        {
            String key = "javelin.conf.AbstractConversionConfig.AnIllegalConfigurationLabel";
            String message = JavelinMessages.getMessage(key, configLine);
            throw new IllegalArgumentException(message);
        }

        String[] configElements = configLine.split(",");
        try
        {
            parseConfig(configElements);
        }
        catch (Exception ex)
        {
            String key = "javelin.conf.AbstractConversionConfig.AnIllegalConfigurationLabel";
            String message = JavelinMessages.getMessage(key, configLine);
            throw new IllegalArgumentException(message, ex);
        }
    }

    /**
     * 設定ファイルのパターンを読み込む。
     * inheritanceのON/OFF、クラス名、メソッド名を取得する。
     * @param configElements コード埋め込み対象
     */
    protected void parseConfig(final String[] configElements)
    {
        configElements[0] = configElements[0].trim();
        if (configElements[0].startsWith(INHERITANCE))
        {
            setInheritance(true);
            configElements[0] = configElements[0].substring(INHERITANCE.length());
        }

        int splitIndex = configElements[0].indexOf("#");
        if (splitIndex < 0)
        {
            // 設定に#が含まれていなければ、指定クラスの全てのメソッドを対象とする。
            setClassName(configElements[0]);
            setClassNamePattern(Pattern.compile(configElements[0]));
            setMethodNamePattern(".*");
        }
        else
        {
            // 設定に#が含まれていれば、クラス名、メソッド名両方に指定がある。
            String className = configElements[0].substring(0, splitIndex);
            String methodName = configElements[0].substring(splitIndex + 1);
            setClassName(className);
            setClassNamePattern(Pattern.compile(className));
            setMethodNamePattern(methodName);
        }
    }

    /**
     * 継承用設定のON/OFFを返す。
     * @return true:継承用設定が行われている、false:継承用設定が行われていない。
     */
    public boolean isInheritance()
    {
        return this.isInheritance_;
    }

    /**
     * 継承用フラグの設定を行う。
     * @param isInheritance true:継承用設定を行う、false:継承用設定を行わない。
     */
    public void setInheritance(final boolean isInheritance)
    {
        this.isInheritance_ = isInheritance;
    }

    /**
     * クラス名のパターンを取得する。
     * @return クラス名のパターン
     */
    public String getClassName()
    {
        return this.className_;
    }

    /**
     * クラス名のパターンを取得する。
     * @return クラス名のパターン
     */
    public Pattern getClassNamePattern()
    {
        return this.classNamePattern_;
    }

    /**
     * クラス名のパターンを設定する。
     * @param classNamePattern クラス名のパターン
     */
    public void setClassName(final String classNamePattern)
    {
        this.className_ = classNamePattern;
    }

    /**
     * クラス名のパターンを設定する。
     * @param classNamePattern クラス名のパターン
     */
    public void setClassNamePattern(final Pattern classNamePattern)
    {
        this.classNamePattern_ = classNamePattern;
    }

    /**
     * メソッド名のパターンを取得する。
     * @return メソッド名のパターン
     */
    public String getMethodNamePattern()
    {
        return this.methodNamePattern_;
    }

    /**
     * メソッド名のパターンを設定する。
     * @param methodNamePattern メソッド名のパターン
     */
    public void setMethodNamePattern(final String methodNamePattern)
    {
        this.methodNamePattern_ = methodNamePattern;
    }
}
