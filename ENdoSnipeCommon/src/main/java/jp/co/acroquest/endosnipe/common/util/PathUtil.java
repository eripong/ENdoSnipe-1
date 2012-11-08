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
import java.net.URL;

/**
 * パスを扱うためのユーティリティクラスです。<br />
 * 
 * @author y-komori
 */
public class PathUtil
{
    /** Excel で読み込めるファイルパスの最大長 */
    public static final int MAX_PATH_LENGTH = 218;

    /** ファイルパスの最大長を超えたときに、残すパスの長さ（拡張子を含む） */
    public static final int CUT_PATH_LENGTH = 209;

    private PathUtil()
    {

    }

    /**
     * クラスパス配下にあるリソースのパスを生成します。<br />
     * 
     * @param clazz リソースと同じパッケージに存在するクラスの {@link Class} オブジェクト
     * @param path リソース名
     * @return リソースパス
     */
    public static String convertPath(final Class<?> clazz, final String path)
    {
        if (clazz == null || path == null)
        {
            return null;
        }

        String packageName = clazz.getPackage().getName() + '.';
        return packageName.replace('.', '/') + path;
    }

    /**
     * 指定されたパスが相対パスであるかどうかを調べます。<br />
     * 相対パスであるかどうかは、以下のようにして調べます。<br />
     * なお、指定されたパス中の \ 記号は / に変換した上で調べます。<br />
     * <ul>
     * <li>Windows環境の場合
     *   <ul>
     *     <li>最初の3文字が [A-Za-z]:/ に一致していれば絶対パス
     *     <li>最初の2文字が // に一致していれば絶対パス(UNCパスの場合)
     *     <li>それ以外は相対パス
     *   </ul>
     * </li>
     * <li>Windows環境以外の場合
     *   <ul>
     *     <li>最初の1文字が / であれば絶対パス
     *     <li>最初の1文字が ~ であれば絶対パス
     *     <li>それ以外は相対パス
     *   </ul>
     * </li>
     * </ul>
     * 
     * @param path パス
     * @return 相対パスである場合は <code>true</code>
     */
    public static boolean isRelativePath(final String path)
    {
        if (path == null)
        {
            return false;
        }

        String normalizedPath = path.replace('\\', '/');
        if (OSUtil.isWindows())
        {
            // Windows 環境の場合
            char drive = normalizedPath.charAt(0);
            if ((('A' <= drive && drive <= 'Z') || ('a' <= drive && drive <= 'z'))
                    && normalizedPath.charAt(1) == ':' && normalizedPath.charAt(2) == '/')
            {
                // パスの先頭にドライブレターが存在する場合
                return false;
            }
            if (normalizedPath.startsWith("//"))
            {
                // UNC パスの場合
                return false;
            }
            return true;
        }
        else
        {
            // Windows 以外の場合
            if (normalizedPath.charAt(0) == '/')
            {
                // パスが / から始まる場合
                return false;
            }
            if (normalizedPath.charAt(0) == '~')
            {
                // パスが ~ から始まる場合
                return false;
            }
            return true;
        }
    }

    /**
     * 指定されたクラスが含まれている Jar ファイルの存在するディレクトリを返します。<br />
     * 指定クラスが Jar ファイルに含まれていない場合、空文字列を返します。<br />
     * 
     * @param clazz 調査対象クラスの {@link Class} オブジェクト
     * @return ディレクトリのパス
     */
    public static String getJarDir(final Class<?> clazz)
    {
        if (clazz == null)
        {
            return null;
        }

        String classFileName = clazz.getName().replace('.', '/') + ".class";
        URL url = clazz.getClassLoader().getResource(classFileName);
        String urlPath = url.getPath();
        if ("jar".equals(url.getProtocol()) == true)
        {
            String jarPath = urlPath.substring("file:".length(), urlPath.indexOf('!'));
            return normalizeUrlPath(jarPath.substring(0, jarPath.lastIndexOf('/') + 1));
        }
        else
        {
            return "";
        }
    }

    /**
     * パス文字列を使用中のOSに合わせて正規化します。<br />
     * Windows ではパスの最初に / が入るため、除外します。<br />
     * 
     * @param path パス文字列
     * @return 正規化結果
     */
    private static String normalizeUrlPath(final String path)
    {
        int startPos = 0;
        if (OSUtil.isWindows())
        {
            // Windows ではパスの最初に / が入るため、除外する
            startPos = 1;
        }
        return path.substring(startPos);
    }

    /**
     * 指定されたファイル名を有効なファイル名にする。<br />
     * "\", "/", ":" , ",", "*", "?", """, "<", ">", "|", "(", ")","\n" を "_"に置換する。<br />
     * 
     * @param fileName ファイル名
     * @return 有効なファイル名
     */
    public static String getValidFileName(String fileName)
    {
        String removeRegex = "\\\\|/|:|,|\\*|\\?|\"|<|>|\\||\\(|\\)|\n";
        String replaceChar = "_";
        fileName = fileName.replaceAll(removeRegex, replaceChar);
        return fileName;
    }

    /**
     * 指定されたファイルの絶対パスの長さを有効な長さに調節します。<br />
     *
     * パスの長さが 218 バイトを超えた場合は、
     * パスの前半 209 バイトに "_" とハッシュコードを追加します。<br />
     *
     * ファイル名部分に全角文字が存在する場合は、ファイル名が文字化けを起こす可能性があります。
     *
     * @param absolutePath 絶対パス
     * @return 長さ調節を行ったファイル名
     */
    public static String getValidLengthPath(final String absolutePath)
    {
        return getValidLengthPath(absolutePath, null);
    }

    /**
     * 指定されたファイルの絶対パスの長さを有効な長さに調節します。<br />
     *
     * パスの長さが 218 バイトを超えた場合は、
     * パスの前半 209 バイトに "_" とハッシュコードを追加します。<br />
     *
     * ファイル名部分に全角文字が存在する場合は、ファイル名が文字化けを起こす可能性があります。
     *
     * @param absolutePath 絶対パス
     * @param addition 追加文字列
     * @return 長さ調節を行ったファイル名
     */
    public static String getValidLengthPath(final String absolutePath, final String addition)
    {
        String fileName = new File(absolutePath).getName();
        String folderPath = new File(absolutePath).getParentFile().getAbsolutePath();

        String newFileName = "";
        int absolutePathLength = absolutePath.getBytes().length;
        if (absolutePathLength > MAX_PATH_LENGTH)
        {
            // 拡張子を取得する
            int extensionPos = fileName.lastIndexOf('.');
            String extension = "";
            String fileNameWithoutExtension = fileName;
            if (extensionPos != -1)
            {
                extension = fileName.substring(extensionPos);
                fileNameWithoutExtension = fileName.substring(0, extensionPos);
            }

            // ファイル名として残す長さ（拡張子は除く）を計算する
            int folderLength = absolutePathLength - fileNameWithoutExtension.length();
            int remainLength = CUT_PATH_LENGTH - folderLength;

            if (remainLength > 0)
            {
                newFileName = fileName.substring(0, remainLength) + "_";
            }

            if (addition == null)
            {
                String hashCodeString = Integer.toHexString(absolutePath.hashCode());
                newFileName = newFileName + hashCodeString + extension;
            }
            else
            {
                newFileName = newFileName + addition + extension;
            }
        }
        else
        {
            newFileName = fileName;
        }
        return folderPath + File.separator + newFileName;
    }
}
