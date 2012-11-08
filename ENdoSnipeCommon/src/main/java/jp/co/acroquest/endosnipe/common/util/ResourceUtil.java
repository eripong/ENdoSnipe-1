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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * リソースを扱うためのユーティリティクラスです。<br />
 * 
 * @author y-komori
 */
public class ResourceUtil
{
    private ResourceUtil()
    {

    }

    /**
     * 指定されたクラスのパッケージを表すリソースパスを返します。<br />
     * 
     * @param clazz パッケージのリソースパスを取得するクラス
     * @return リソースパス
     */
    public static String getPackagePath(final Class<?> clazz)
    {
        return "/" + clazz.getPackage().getName().replace('.', '/');
    }

    /**
     * 指定されたクラスと同じパッケージにあるリソースの絶対パスを返します。<br />
     * 
     * @param clazz クラス
     * @param name リソース名
     * @return 絶対パス
     */
    public static String getAbsolutePath(final Class<?> clazz, final String name)
    {
        String prefix = getPackagePath(clazz);
        return prefix + "/" + name;
    }

    /**
     * リソースを {@link File} オブジェクトとして取得します。<br />
     * 
     * @param clazz リソースと同じパッケージのクラスオブジェクト
     * @param name リソース名
     * @return {@link File} オブジェクト
     */
    public static File getResourceAsFile(final Class<?> clazz, final String name)
    {
        String absolutePath = getAbsolutePath(clazz, name);
        try
        {
            URL url = clazz.getResource(absolutePath);
            if (url != null)
            {
                return new File(url.toURI());
            }
            else
            {
                return null;
            }
        }
        catch (URISyntaxException ex)
        {
            return null;
        }
    }

    /**
     * JAR ファイルの MANIFEST.MF から、バージョンを取得します。
     *
     * @param clazz JAR ファイル内に存在するクラス
     * @return バージョン。バージョンが取得できない場合は "(Unknown version)"
     */
    public static String getJarVersion(final Class<?> clazz)
    {
        // Javelinのバージョンを取得する
        String version = null;
        try
        {
            version = getVersionFromManifest(clazz);
        }
        catch (IOException ex)
        // CHECKSTYLE:OFF
        {
            // Do nothing.
        }
        // CHECKSTYLE:ON
        if (version != null)
        {
            version = "Ver." + version;
        }
        else
        {
            version = "(Unknown version)";
        }
        return version;
    }

    /**
     * JAR ファイルの MANIFEST.MF から、バージョンを取得します。
     *
     * @param clazz JAR ファイル内に存在するクラス
     * @return バージョン
     * @throws IOException MANIFEST.MF の読み込みに失敗した場合 
     */
    private static String getVersionFromManifest(final Class<?> clazz)
        throws IOException
    {
        // このクラスがJARファイルに含まれるものと仮定し、JARファイルのパスを含むこのクラスの絶対パスを取得する;
        URL classUrl = clazz.getResource(clazz.getSimpleName() + ".class");
        String fullPath = classUrl.toExternalForm();

        // jarファイル内のこのクラス指定を、マニフェストファイル指定に差し替える
        String packagePath = clazz.getPackage().getName().replace('.', '/');
        String jar = fullPath.substring(0, fullPath.lastIndexOf(packagePath));
        URL manifestUrl = new URL(jar + "META-INF/MANIFEST.MF");

        // MANIFEST.MFを開き、 "Version: " の値を取得する
        InputStream is = null;
        Manifest mf = null;
        try
        {
            is = manifestUrl.openStream();
            mf = new Manifest(is);
        }
        finally
        {
            is.close();
        }
        Attributes attributes = mf.getMainAttributes();
        String version = attributes.getValue("Version");

        return version;
    }
}
