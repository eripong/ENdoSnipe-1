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
package jp.co.acroquest.endosnipe.common.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import jp.co.acroquest.endosnipe.common.util.StreamUtil;

/**
 * javelin の設定ファイルへアクセスするためのユーティリティクラスです。<br />
 * 本クラスは、値をはじめて取得しようとしたとき（getterメソッドをはじめて呼んだとき）
 * にファイルから設定をロードします。<br />
 *
 * @author sakamoto
 */
public class JavelinConfigUtil
{
    /** Javelinオプションキー1 */
    private static final String JAVELIN_OPTION_KEY_1 = JavelinConfig.JAVELIN_PREFIX + "property";

    /** Javelinオプションキー2 */
    private static final String JAVELIN_OPTION_KEY_2 = JavelinConfig.JAVELIN_PREFIX + "properties";

    /** 設定ファイル名1 */
    private final String fileName1_;

    /** 設定ファイル名2 */
    private final String fileName2_;

    /** Javelin プロパティ */
    private Properties properties_;

    /** 本クラスのインスタンス */
    private static JavelinConfigUtil instance__ = new JavelinConfigUtil();

    /** Javelin実行Jarファイルの存在ディレクトリ */
    private static String absoluteJarDirectory_;

    /** PropertyFileのパス */
    private String propertyFilePath_;

    /** PropertyFileの存在ディレクトリ */
    private String propertyFileDirectory_;

    /** Booleanの値を保持する。 */
    private Map<String, Boolean> booleanMap_ = new ConcurrentHashMap<String, Boolean>();

    /** Longの値を保持する。 */
    private Map<String, Long> longMap_ = new ConcurrentHashMap<String, Long>();

    /** Doubleの値を保持する。 */
    private Map<String, Double> doubleMap_ = new ConcurrentHashMap<String, Double>();

    /** Integerの値を保持する。 */
    private Map<String, Integer> intMap_ = new ConcurrentHashMap<String, Integer>();

    private JavelinConfigUtil()
    {
        this.fileName1_ = System.getProperty(JAVELIN_OPTION_KEY_1);
        this.fileName2_ = System.getProperty(JAVELIN_OPTION_KEY_2);
    }

    /**
     * {@link JavelinConfigUtil} のインスタンスを取得します。<br />
     *
     * @return {@link JavelinConfigUtil} のインスタンス
     */
    public static JavelinConfigUtil getInstance()
    {
        return instance__;
    }

    /** イベントレベルとして入力可能な値 */
    private static final String[] EVENT_LEVELS = {"ERROR", "WARN", "INFO"};

    /** ログレベルとして入力可能な値 */
    private static final String[] LOG_LEVELS = {"FATAL", "ERROR", "WARN", "INFO", "DEBUG"};

    /**
     * 設定ファイルを読み込みます。<br />
     */
    private void load()
    {
        this.properties_ = new Properties();

        String fileName = null;

        if (getFileName1() != null)
        {
            fileName = getFileName1();
        }
        else if (getFileName2() != null)
        {
            fileName = getFileName2();
        }
        else
        {
            fileName = "../conf/javelin.properties";
        }

        this.propertyFilePath_ = convertRelPathFromJartoAbsPath(fileName);

        if (this.propertyFilePath_ != null)
        {
            File file = null;
            InputStream stream = null;
            try
            {
                file = new File(this.propertyFilePath_);
                if (!file.exists())
                {
                    System.err.println("プロパティファイルが存在しません。" + "(" + file.getAbsolutePath() + ")");
                    return;
                }
                stream = ConfigPreprocessor.process(file);
                this.properties_.load(stream);

                // 設定ファイル（*.conf）のあるディレクトリを取得する
                File optionFile = new File(this.propertyFilePath_);
                File optionPath = optionFile.getParentFile();
                if (optionPath != null)
                {
                    setPropertyFileDirectory(optionPath.getAbsolutePath());
                    properties_.setProperty(JAVELIN_OPTION_KEY_1, optionPath.getAbsolutePath());
                }
            }
            catch (Exception ex)
            {
                if (file != null)
                {
                    System.err.println("プロパティファイルの読み込みに失敗しました。" + "(" + file.getAbsolutePath()
                            + ")");
                }
            }
            finally
            {
                StreamUtil.closeStream(stream);
            }
        }
        else
        {
            System.err.println("必要なプロパティ(-Djavelin.property)が指定されていません。");
        }
    }

    /**
     * 設定ファイルを読み込みます。<br />
     * @param relPath 設定ファイル(jarファイルからの相対パス)
     * @return 読み込んだ設定
     */
    public static Properties loadProperties(final String relPath)
    {
        Properties properties = new Properties();

        String fileName = convertRelPathFromJartoAbsPath(relPath);

        File file = null;
        InputStream stream = null;
        try
        {
            file = new File(fileName);
            if (!file.exists())
            {
                System.err.println("プロパティファイルが存在しません。" + "(" + file.getAbsolutePath() + ")");
            }
            stream = ConfigPreprocessor.process(file);
            properties.load(stream);
        }
        catch (Exception ex)
        {
            if (file != null)
            {
                System.err.println("プロパティファイルの読み込みに失敗しました。" + "(" + file.getAbsolutePath() + ")");
            }
        }
        finally
        {
            StreamUtil.closeStream(stream);
        }
        return properties;
    }

    /**
     * ログレベルの取得において、指定されたキーに対応する値を返します。<br />
     * 初期設定が行われていないとき、設定値が異常の場合には、デフォルト値を返します。<br />
     *
     * @param key キー
     * @param defaultValue デフォルト値
     * @return 値
     */
    public String getLogLevel(final String key, final String defaultValue)
    {
        return getLevel(key, defaultValue, LOG_LEVELS);
    }

    /**
     * イベントのレベルの取得において、指定されたキーに対応する値を返します。<br />
     * 初期設定が行われていないとき、設定値が異常の場合には、デフォルト値を返します。<br />
     *
     * @param key キー
     * @param defaultValue デフォルト値
     * @return 値
     */
    public String getEventLevel(final String key, final String defaultValue)
    {
        return getLevel(key, defaultValue, EVENT_LEVELS);
    }

    /**
     * レベルの取得において、指定されたキーに対応する値を返します。<br />
     * 初期設定が行われていないとき、設定値が異常の場合には、デフォルト値を返します。<br />
     *
     * @param key キー
     * @param defaultValue デフォルト値
     * @param levelArray レベルとして入力可能な値の配列
     * 
     * @return 値
     */
    private String getLevel(final String key, final String defaultValue, final String[] levelArray)
    {
        String value = this.properties_.getProperty(key);
        if (value == null)
        {
            value = defaultValue.toUpperCase();
            this.properties_.put(key, value);
            return value;
        }
        value = value.toUpperCase();
        for (int num = 0; num < levelArray.length; num++)
        {
            if (levelArray[num].equals(value))
            {
                return value;
            }
        }
        System.out.println(key + "に不正な値が入力されました。デフォルト値(" + defaultValue.toUpperCase() + ")を使用します。");
        this.properties_.put(key, defaultValue.toUpperCase());
        return defaultValue;
    }

    /**
     * 指定されたキーに対応する値を返します。<br />
     * 初期設定が行われていないときには、デフォルト値を返します。<br />
     *
     * @param key キー
     * @param defaultValue デフォルト値
     * @return 値
     */
    public String getString(final String key, final String defaultValue)
    {
        synchronized (this)
        {
            if (this.properties_ == null)
            {
                load();
            }
        }
        String value = this.properties_.getProperty(key);
        if (value == null)
        {
            value = defaultValue;
        }
        return value;
    }

    /**
     * 指定されたキーに対応する数値を返します。<br />
     * 初期設定が行われていないときや、不正な値が入力されているときは、
     * デフォルト値を返します。<br />
     *
     * @param key キー
     * @param defaultValue デフォルト値
     * @return 値
     */
    public int getInteger(final String key, final int defaultValue)
    {
        Integer intValue = intMap_.get(key);
        if (intValue == null)
        {
            String value = getString(key, null);
            intValue = Integer.valueOf(defaultValue);
            if (value != null)
            {
                try
                {
                    intValue = Integer.valueOf(value);
                }
                catch (NumberFormatException nfe)
                {
                    System.out.println(key + "に不正な値が入力されました。デフォルト値(" + intValue + ")を使用します。");
                    setInteger(key, intValue);
                }
            }

            intMap_.put(key, intValue);
        }

        return intValue.intValue();
    }

    /**
     * 指定されたキーに対応する数値を返します。<br />
     * 初期設定が行われていないときや不正な値が入力されているときは、
     * デフォルト値を返します。<br />
     *
     * @param key キー
     * @param defaultValue デフォルト値
     * @return 値
     */
    public long getLong(final String key, final long defaultValue)
    {
        Long longValue = longMap_.get(key);
        if (longValue == null)
        {
            String value = getString(key, null);
            longValue = Long.valueOf(defaultValue);
            if (value != null)
            {
                try
                {
                    longValue = Long.valueOf(value);
                }
                catch (NumberFormatException nfe)
                {
                    System.out.println(key + "に不正な値が入力されました。デフォルト値(" + defaultValue + ")を使用します。");
                    setLong(key, longValue);
                }
            }

            longMap_.put(key, longValue);
        }

        return longValue.longValue();
    }

    /**
     * 指定されたキーに対応する数値を返します。<br />
     * 初期設定が行われていないときや不正な値が入力されているときは、
     * デフォルト値を返します。<br />
     *
     * @param key キー
     * @param defaultValue デフォルト値
     * @return 値
     */
    public double getDouble(final String key, final double defaultValue)
    {
        Double doubleValue = doubleMap_.get(key);
        if (doubleValue == null)
        {
            String value = getString(key, null);
            doubleValue = Double.valueOf(defaultValue);
            if (value != null)
            {
                try
                {
                    doubleValue = Double.valueOf(value);
                }
                catch (NumberFormatException nfe)
                {
                    System.out.println(key + "に不正な値が入力されました。デフォルト値(" + defaultValue + ")を使用します。");
                    setDouble(key, doubleValue);
                }
            }

            doubleMap_.put(key, doubleValue);
        }

        return doubleValue.doubleValue();
    }

    /**
     * 指定されたキーに対応するBoolean値を返します。<br />
     * 初期設定が行われていないや、不正な値が入力されているときは、
     * デフォルト値を返します。
     *
     * @param key キー
     * @param defaultValue デフォルト値
     * @return 値
     */
    public boolean getBoolean(final String key, final boolean defaultValue)
    {
        Boolean value = booleanMap_.get(key);

        if (value == null)
        {
            String valueStr = getString(key, null);

            value = defaultValue;
            if (valueStr != null)
            {
                if ("true".equals(valueStr))
                {
                    value = true;
                }
                else if ("false".equals(valueStr))
                {
                    value = false;
                }
                else
                {
                    System.out.println(key + "に不正な値が入力されました。デフォルト値(" + defaultValue + ")を使用します。");
                    setBoolean(key, value);
                }
            }

            booleanMap_.put(key, value);
        }

        return value;
    }

    /**
     * 指定されたキーに文字列をセットします。<br />
     *
     * @param key キー
     * @param value 値
     */
    public void setString(final String key, final String value)
    {
        synchronized (this)
        {
            if (this.properties_ == null)
            {
                load();
            }
        }
        this.properties_.setProperty(key, value);
    }

    /**
     * 指定されたキーに数値をセットします。<br />
     *
     * @param key キー
     * @param value 値
     */
    public void setInteger(final String key, final int value)
    {
        String valueString = String.valueOf(value);
        setString(key, valueString);
    }

    /**
     * 指定されたキーに数値をセットします。<br />
     *
     * @param key キー
     * @param value 値
     */
    public void setLong(final String key, final long value)
    {
        String valueString = String.valueOf(value);
        setString(key, valueString);
    }

    /**
     * 指定されたキーに数値をセットします。<br />
     *
     * @param key キー
     * @param value 値
     */
    public void setDouble(final String key, final double value)
    {
        String valueString = String.valueOf(value);
        setString(key, valueString);
    }

    /**
     * 指定されたキーにboolean値をセットします。<br />
     *
     * @param key キー
     * @param value 値
     */
    public void setBoolean(final String key, final boolean value)
    {
        String valueString = String.valueOf(value);
        setString(key, valueString);
    }

    /**
     * 指定されたキーが設定に存在するかどうかを調べます。<br />
     *
     * @param key キー
     * @return 存在する場合 <code>true</code>、存在しない場合、<code>false</code>
     */
    public boolean isKeyExist(final String key)
    {
        synchronized (this)
        {
            if (this.properties_ == null)
            {
                load();
            }
        }
        return this.properties_.containsKey(key);
    }

    /**
     * 設定ファイル名を返します。<br />
     *
     * @return 設定ファイル名
     */
    public String getFileName()
    {
        return getFileName1();
    }

    /**
     * 設定ファイル名を返します。<br />
     *
     * @return 設定ファイル名
     */
    private String getFileName1()
    {
        return this.fileName1_;
    }

    /**
     * 設定ファイル名を返します。<br />
     *
     * @return 設定ファイル名
     */
    private String getFileName2()
    {
        return this.fileName2_;
    }

    /**
     * Javelin実行Jarファイルの存在ディレクトリを返します。<br />
     * 
     * @return Javelin実行Jarファイルの存在ディレクトリ
     */
    public String getAbsoluteJarDirectory()
    {
        return this.absoluteJarDirectory_;
    }

    /**
     * Javelin実行Jarファイルの存在ディレクトリを設定します。<br />
     * 
     * @param absoluteJarDirectory Javelin実行Jarファイルの存在ディレクトリ
     */
    public void setAbsoluteJarDirectory(final String absoluteJarDirectory)
    {
        this.absoluteJarDirectory_ = absoluteJarDirectory;
    }

    /**
     * プロパティファイルの存在ディレクトリを返します。<br />
     * 
     * @return プロパティファイルの存在ディレクトリ
     */
    public String getPropertyFileDirectory()
    {
        return this.propertyFileDirectory_;
    }

    /**
     * プロパティファイルの存在ディレクトリを設定します。<br />
     * 
     * @param propertyFileDirectory プロパティファイルの存在ディレクトリ
     */
    public void setPropertyFileDirectory(final String propertyFileDirectory)
    {
        this.propertyFileDirectory_ = propertyFileDirectory;
    }

    /**
     * プロパティファイルのパスを設定します。<br />
     * 
     * @param propertyFilePath プロパティファイルのパス
     */
    public void setPropertyFilePath(final String propertyFilePath)
    {
        this.propertyFilePath_ = propertyFilePath;
    }

    /**
     * プロパティファイルのパスを返します。<br />
     * 
     * @return プロパティファイルのパス
     */
    public String getPropertyFilePath()
    {
        return this.propertyFilePath_;
    }

    /**
     * Javelin実行Jarファイルからの相対パスを絶対パスに変換します。<br />
     * 
     * @param relativePath Javelin実行Jarファイルからの相対パス
     * @return 絶対パス
     */
    public static String convertRelPathFromJartoAbsPath(final String relativePath)
    {
        if (relativePath == null)
        {
            return null;
        }

        File relativeFile = new File(relativePath);
        if (relativeFile.isAbsolute())
        {
            return relativePath;
        }
        File targetPath = new File(absoluteJarDirectory_, relativePath);

        String canonicalPath;
        try
        {
            canonicalPath = targetPath.getCanonicalPath();
        }
        catch (IOException ioe)
        {
            return targetPath.getAbsolutePath();
        }

        return canonicalPath;
    }

    /**
     * プロパティファイルからの相対パスを絶対パスに変換します。<br />
     * 
     * @param relativePath プロパティファイルからの相対パス
     * @return 絶対パス
     */
    public String convertRelativePathtoAbsolutePath(final String relativePath)
    {
        if (relativePath == null)
        {
            return null;
        }

        File relativeFile = new File(relativePath);
        if (relativeFile.isAbsolute())
        {
            return relativePath;
        }

        File targetPath = new File(this.propertyFileDirectory_, relativePath);

        String canonicalPath;
        try
        {
            canonicalPath = targetPath.getCanonicalPath();
        }
        catch (IOException ioe)
        {
            return targetPath.getAbsolutePath();
        }

        return canonicalPath;
    }

    /**
     * すべての設定の更新を反映します。<br />
     */
    public void update()
    {
        this.longMap_ = new ConcurrentHashMap<String, Long>();
        this.booleanMap_ = new ConcurrentHashMap<String, Boolean>();
        this.intMap_ = new ConcurrentHashMap<String, Integer>();
        this.doubleMap_ = new ConcurrentHashMap<String, Double>();
    }

    /**
     * 指定したキーを持つBooleanの値の更新を反映します。<br />
     * 
     * 実動作として、指定したキーを持つBooleanの値をbooleanMap_から削除します。
     * 削除することによって、指定されたキーを持つ値が次に使用される際、
     * propertiesから読み出されるため、更新した値が反映されます。
     * 
     * @param key 更新反映対象の値のキー
     */
    public void updateBooleanValue(final String key)
    {
        if (key == null)
        {
            return;
        }
        this.booleanMap_.remove(key);
    }

    /**
     * 指定したキーを持つIntegerの値の更新を反映します。<br />
     * 
     * 実動作として、指定したキーを持つIntegerの値をintMap_から削除します。
     * 削除することによって、指定されたキーを持つ値が次に使用される際、
     * propertiesから読み出されるため、更新した値が反映されます。
     * 
     * @param key 更新反映対象の値のキー
     */
    public void updateIntValue(final String key)
    {
        if (key == null)
        {
            return;
        }
        this.intMap_.remove(key);
    }

    /**
     * 指定したキーを持つLongの値の更新を反映します。<br />
     * 
     * 実動作として、指定したキーを持つLongの値をlongMap_から削除します。
     * 削除することによって、指定されたキーを持つ値が次に使用される際、
     * propertiesから読み出されるため、更新した値が反映されます。
     * 
     * @param key 更新反映対象の値のキー
     */
    public void updateLongValue(final String key)
    {
        if (key == null)
        {
            return;
        }
        this.longMap_.remove(key);
    }
}
