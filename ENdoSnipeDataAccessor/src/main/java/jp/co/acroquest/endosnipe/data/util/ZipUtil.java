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
package jp.co.acroquest.endosnipe.data.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * ZIP ファイルを生成するユーティリティクラスです。<br />
 * 
 * @author y-komori
 */
public class ZipUtil
{
    private static final int BUF_SIZE = 4096;

    private ZipUtil()
    {
    }

    /**
     * ZIP 圧縮されたバイト配列を読み込むストリームから内容を読み込むための
     * 入力ストリームを返します。<br />
     * 本メソッドでは最初に格納された ZIP エントリのみを展開対象とします。<br />
     * 本メソッドによって返されたストリームは呼び出し側でクローズしてください。<br />
     * 
     * @param is バイト配列を読み込むためのストリーム
     * @return ZIP 展開結果を読み込むためのストリーム
     * @throws IOException 入出力エラーが発生した場合
     */
    public static InputStream unzipFromByteArray(final InputStream is)
        throws IOException
    {
        if (is == null)
        {
            return null;
        }

        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
        ZipEntry entry = zis.getNextEntry();
        if ((entry != null) && (entry.isDirectory() == false))
        {
            return zis;
        }
        else
        {
            return null;
        }
    }

    /**
     * 入力ストリームから読み込んだバイト列を ZIP 圧縮してファイルに出力します。<br />
     * 
     * @param zipFile ZIP出力ファイル名
     * @param in 入力ストリーム
     * @param entryPath エントリパス
     * @throws IOException 入出力エラーが発生した場合
     */
    public static void createZip(final String zipFile, final InputStream in, final String entryPath)
        throws IOException
    {
        createZip(new FileOutputStream(zipFile), in, entryPath);
    }

    /**
     * 入力ストリームから読み込んだバイト列を ZIP 圧縮してストリームへ出力します。<br />
     * 
     * @param out 出力ストリーム
     * @param in 入力ストリーム
     * @param entryPath エントリパス
     * @throws IOException 入出力エラーが発生した場合
     */
    public static void createZip(final OutputStream out, final InputStream in,
            final String entryPath)
        throws IOException
    {
        ZipOutputStream zout = new ZipOutputStream(out);
        createZip(zout, in, entryPath);
        zout.close();
    }

    /**
     * 引数で与えられたファイル群を ZIP 圧縮してファイルに出力します。<br />
     * 
     * @param zipFile ZIP出力ファイル名
     * @param targetFiles 入力ファイル(またはディレクトリ)配列
     * @throws IOException 入出力エラーが発生した場合
     */
    public static void createZip(final String zipFile, final File[] targetFiles)
        throws IOException
    {

        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
        for (int i = 0; i < targetFiles.length; i++)
        {
            if (targetFiles[i].exists())
            {
                createZip(out, targetFiles[i], targetFiles[i].getPath());
            }
        }
        out.close();
    }

    /**
     * ファイルまたはディレクトリを {@link ZipOutputStream} のエントリへ出力します。<br />
     * 
     * エントリのパスは、ベースパスからの相対パスになります。<br />
     * 
     * @param out ZIP出力先ストリーム
     * @param targetFile 入力ファイル(またはディレクトリ)
     * @param basePath ベースパス
     * @throws IOException 入出力エラーが発生した場合
     */
    public static void createZip(final ZipOutputStream out, final File targetFile,
            final String basePath)
        throws IOException
    {

        if (targetFile.isDirectory())
        {
            File[] files = targetFile.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                String entryPath = getEntryPath(basePath, files[i].getAbsolutePath());
                createZip(out, files[i], entryPath);
            }
        }
        else
        {
            createZip(out, new FileInputStream(targetFile),
                      getEntryPath(basePath, targetFile.getAbsolutePath()));
        }
    }

    /**
     * 入力ストリームから読み込んだバイト列を {@link ZipOutputStream} のエントリへ出力します。<br />
     * 
     * @param out ZIP出力先ストリーム
     * @param in 入力ストリーム
     * @param entryPath エントリパス
     * @throws IOException 入出力エラーが発生した場合
     */
    public static void createZip(final ZipOutputStream out, final InputStream in,
            final String entryPath)
        throws IOException
    {

        ZipEntry target = new ZipEntry(entryPath);
        out.putNextEntry(target);
        byte[] buf = new byte[BUF_SIZE];
        int count;
        BufferedInputStream bis = new BufferedInputStream(in);
        while ((count = bis.read(buf, 0, BUF_SIZE)) != -1)
        {
            out.write(buf, 0, count);
        }
        in.close();
        out.closeEntry();
    }

    private static String getEntryPath(final String basePath, final String path)
    {
        if (basePath != null && path != null)
        {
            if (path.startsWith(basePath))
            {
                return path.substring(basePath.length()).replaceAll("\\\\", "/");
            }
        }
        return path;
    }
}
