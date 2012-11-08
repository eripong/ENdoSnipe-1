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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import jp.co.smg.endosnipe.javassist.ClassPool;
import jp.co.smg.endosnipe.javassist.CtClass;
import jp.co.acroquest.endosnipe.javelin.common.JavassistUtil;

/**
 * Javelinのコード埋め込み設定。
 * 
 * @author yamasaki
 */
public class JavelinTransformConfig
{
    /** コンバータの設定リスト */
    private final List<ConverterConfig> converterConfigList_ = new ArrayList<ConverterConfig>();

    /** Includeの設定リスト */
    private final List<IncludeConversionConfig> includeConfigList_ =
            new ArrayList<IncludeConversionConfig>();

    /** Excludeの設定リスト */
    private final List<ExcludeConversionConfig> excludeConfigList_ =
            new ArrayList<ExcludeConversionConfig>();

    /** 設定ファイルのリーダ */
    private BufferedReader reader_;

    /**
     * 設定ファイルを読み込む。
     * @param includeStream Includeファイル用入力ストリーム
     * @param excludeStream Excludeファイル用入力ストリーム
     * @throws IOException ファイル読み込み時に発生する入出力例外
     */
    public void readConfig(final InputStream includeStream, final InputStream excludeStream)
        throws IOException
    {
        this.reader_ = new BufferedReader(new InputStreamReader(includeStream));

        String line;

        line = readLine();
        while (line != null)
        {
            if (line.startsWith(ConverterConfig.PREFIX))
            {
                ConverterConfig config = new ConverterConfig();
                config.readConfig(line);
                this.converterConfigList_.add(config);
            }
            else
            {
                IncludeConversionConfig config = new IncludeConversionConfig();
                config.readConfig(line);
                this.includeConfigList_.add(config);
            }

            line = readLine();
        }

        this.reader_ = new BufferedReader(new InputStreamReader(excludeStream));

        line = readLine();
        while (line != null)
        {
            ExcludeConversionConfig config = new ExcludeConversionConfig();
            config.readConfig(line);
            this.excludeConfigList_.add(config);

            line = readLine();
        }
    }

    /**
     * コード埋め込み対象から除外されるかどうかを返す。
     * @param className クラス名
     * @return コード埋め込み対象から除外されるかどうか
     */
    public boolean isExcludeClass(final String className)
    {
        boolean result = false;

        for (ExcludeConversionConfig config : this.excludeConfigList_)
        {
            Matcher matcher = config.getClassNamePattern().matcher(className);
            if (matcher.matches() && ".*".equals(config.getMethodNamePattern()))
            {
                result = true;
                break;
            }
        }

        return result;
    }

    /**
     * コード埋め込み対象から除外されるクラスのリストを返す。
     * @param className クラス名
     * @return コード埋め込み対象から除外されるクラスのリスト
     */
    public List<ExcludeConversionConfig> matchesToExclude(final String className)
    {
        List<ExcludeConversionConfig> list = new ArrayList<ExcludeConversionConfig>();

        for (ExcludeConversionConfig config : this.excludeConfigList_)
        {
            Matcher matcher = config.getClassNamePattern().matcher(className);
            if (matcher.matches())
            {
                list.add(config);
            }
        }

        return list;
    }

    /**
     * コード埋め込み対象クラスのリストを返す。
     * @param className クラス名
     * @param ctClass CtClass
     * @param pool ClassPool
     * @return コード埋め込み対象クラスのリスト
     */
    public List<IncludeConversionConfig> matchesToInclude(final String className,
            final CtClass ctClass, final ClassPool pool)
    {
        List<IncludeConversionConfig> list = new ArrayList<IncludeConversionConfig>();

        for (IncludeConversionConfig config : this.includeConfigList_)
        {
            if (config.isInheritance())
            {
                boolean isInherited =
                        JavassistUtil.isInherited(ctClass, pool, config.getClassName());
                if (isInherited)
                {
                    list.add(config);
                }
            }
            else
            {
                Matcher matcher = config.getClassNamePattern().matcher(className);
                if (matcher.matches())
                {
                    list.add(config);
                }
            }
        }

        return list;
    }

    /**
     * コンバータのクラス名のリストを返す。
     * @param converterName コンバータ名
     * @return コンバータのクラス名のリスト
     */
    public List<String> getConverterClassNames(final String converterName)
    {
        for (ConverterConfig config : this.converterConfigList_)
        {
            if (config.getName().equals(converterName))
            {
                return Arrays.asList(config.getConverterNames());
            }
        }

        return Arrays.asList(new String[]{converterName});
    }

    /**
     * コンバータ名のリストを返す。
     * @return コンバータ名のリスト
     */
    public List<String> getConverterNames()
    {
        List<String> converterNameList = new ArrayList<String>();
        for (ConverterConfig config : this.converterConfigList_)
        {
            converterNameList.add(config.getName());
        }

        return converterNameList;
    }

    /**
     * コンバータの設定リスト、Includeの設定リスト、Excludeの設定リストを返す。
     * @return コンバータの設定リスト、Includeの設定リスト、Excludeの設定リスト
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(super.toString());

        builder.append(this.converterConfigList_.toString());
        builder.append(this.includeConfigList_.toString());
        builder.append(this.excludeConfigList_.toString());

        return builder.toString();
    }

    /**
     * 1行読み込み、コメント行の場合、次の行を読み込む。
     * @return コメント以外の行
     * @throws IOException 入出力例外
     */
    private String readLine()
        throws IOException
    {
        String line = this.reader_.readLine();
        while (line != null && (line.startsWith("#") || line.length() == 0))
        {
            line = this.reader_.readLine();
        }

        return line;
    }
}
