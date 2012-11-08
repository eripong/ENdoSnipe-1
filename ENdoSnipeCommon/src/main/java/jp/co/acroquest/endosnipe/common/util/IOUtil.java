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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jp.co.acroquest.endosnipe.common.logger.SystemLogger;

/**
 * ファイル I/O に関するユーティリティクラスです。<br />
 * 
 * @author y-komori
 */
public class IOUtil
{
    private static final int BUFFER_SIZE = 1024;

    private IOUtil()
    {
    }

    /**
     * 入力ストリームから出力ストリームへコピーします。<br />
     * 
     * @param input 入力ストリーム
     * @param output 出力ストリーム
     * @return コピーしたバイト数
     * @throws IOException 入出力エラーが発生した場合
     */
    public static long copy(final InputStream input, final OutputStream output)
        throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer)))
        {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * {@link Reader} から {@link Writer} へコピーします。<br />
     * 
     * @param input {@link Reader} オブジェクト
     * @param output {@link Writer} オブジェクト
     * @return コピーしたバイト数
     * @throws IOException 入出力エラーが発生した場合
     */
    public static long copy(final Reader input, final Writer output)
        throws IOException
    {
        return copy(input, output, -1);
    }

    /**
     * {@link Reader} から {@link Writer} へ最大サイズを指定してコピーします。<br />
     * 
     * @param input {@link Reader} オブジェクト
     * @param output {@link Writer} オブジェクト
     * @param maxBytes 最大バイト数
     * @return コピーしたバイト数
     * @throws IOException 入出力エラーが発生した場合
     */
    public static long copy(final Reader input, final Writer output, final int maxBytes)
        throws IOException
    {
        char[] buffer = new char[BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (count < maxBytes && (-1 != (n = input.read(buffer))))
        {
            if (count + n > maxBytes)
            {
                n = (int)(maxBytes - count);
            }

            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * ファイルを読み込んで文字列で返します。<br />
     * 
     * @param fileName ファイル名
     * @return 読み込んだ文字列
     */
    public static String readFileToString(final String fileName)
    {
        return readFileToString(fileName, -1);
    }

    /**
     * 最大バイト数を指定してファイルを読み込み、文字列で返します。<br />
     * 
     * @param fileName ファイル名
     * @param maxBytes 最大バイト数
     * @return 読み込んだ文字列
     */
    public static String readFileToString(final String fileName, final int maxBytes)
    {
        String content = "";
        Reader input = null;
        StringWriter output = null;
        try
        {
            input = new FileReader(fileName);
            output = new StringWriter();
            copy(input, output, maxBytes);
        }
        catch (FileNotFoundException fnfe)
        {
            SystemLogger.getInstance().warn(fnfe);
        }
        catch (IOException ioe)
        {
            SystemLogger.getInstance().warn(ioe);
        }
        finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException ioe)
                {
                    SystemLogger.getInstance().warn(ioe);
                }
            }
        }

        if (output != null)
        {
            content = output.toString();
        }
        return content;
    }

    /**
     * ディレクトリを作成します。<br />
     * 親ディレクトリが存在しない場合、同時に作成します。<br />
     * 
     * @param dirPath 作成するディレクトリのパス
     * @return 成功した場合は <code>true</code>、失敗した場合は <code>false</code>
     */
    public static boolean createDirs(final String dirPath)
    {
        File file = new File(dirPath);
        if (file.exists() == false)
        {
            return file.mkdirs();
        }

        return false;
    }

    /**
     * 拡張子を指定してディレクトリ配下のファイルを列挙します。<br />
     * 
     * @param dirPath ディレクトリのパス
     * @param extention 拡張子
     * @return 列挙したファイル。存在しない場合は <code>null</code>。
     */
    public static File[] listFile(final String dirPath, final String extention)
    {
        File dir = new File(dirPath);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(final File dir, final String name)
            {
                if (name != null && name.endsWith(extention))
                {
                    return true;
                }
                return false;
            }
        });

        if (files == null)
        {
            return null;
        }
        Arrays.sort(files);

        return files;
    }

    /**
     * 指定したディレクトリ配下における指定した拡張子のファイルをチェックし、
     * 最大数を超えている場合は削除します。<br />
     * 
     * @param maxFileCount 最大ファイル数
     * @param dirPath ディレクトリのパス
     * @param extention 拡張子
     */
    public static void removeFiles(final int maxFileCount, final String dirPath,
            final String extention)
    {
        File[] files = listFile(dirPath, extention);

        if (files == null)
        {
            return;
        }

        for (int index = files.length; index > maxFileCount; index--)
        {
            files[files.length - index].delete();
        }
    }

    /**
     * 指定されたファイルを ZIP 圧縮します。<br />
     * 
     * @param zStream 出力先ストリーム
     * @param file 圧縮するファイル
     * @throws IOException 入出力エラーが発生した場合
     */
    public static void zipFile(final ZipOutputStream zStream, final File file)
        throws IOException
    {
        FileInputStream fileInputStream = null;
        try
        {
            fileInputStream = new FileInputStream(file);
            ZipEntry target = new ZipEntry(file.getName());
            zStream.putNextEntry(target);
            copy(fileInputStream, zStream);
            zStream.closeEntry();
        }
        catch (IOException ex)
        {
            StreamUtil.closeStream(fileInputStream);
            throw ex;
        }
        finally
        {
            StreamUtil.closeStream(fileInputStream);
        }
    }

    /**
     * OS 標準のテンポラリディレクトリを表す {@link File} オブジェクトを返します。<br />
     * テンポラリディレクトリが存在しない場合は、自動的に作成します。<br />
     * 
     * @return テンポラリディレクトリを表す {@link File} オブジェクト
     */
    public static File getTmpDirFile()
    {
        String tmpPath = System.getProperty("java.io.tmpdir");
        File tmpFile = new File(tmpPath);
        if (tmpFile.exists() == false)
        {
            tmpFile.mkdirs();
        }
        return tmpFile;
    }
}
