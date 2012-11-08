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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import jp.co.acroquest.endosnipe.common.logger.SystemLogger;

/**
 * 設定ファイルのプリプロセッサ。<br />
 * インポート定義を展開する。
 * 
 * @author nagai
 *
 */
public class ConfigPreprocessor
{
    private static final String IMPORT_DEFINITION = "[import]";

    private static final String FILE_NOT_FOUND_MESSAGE = "File was not found : ";

    private static final String FILE_OPERATION_FAILED_MESSAGE = "File operation failed : ";

    private static final String CIRCULAR_IMPORT_MESSAGE = "Circular import definition : ";

    private static final String LINE_BREAK = System.getProperty("line.separator");

    private static HashMap<Integer, File> canonicalFiles__ = new HashMap<Integer, File>();

    private ConfigPreprocessor()
    {
        //cannot create instance
    }

    /**
     * インポート定義されているファイルをすべて読み込み、
     * １つにまとめてInputStreamとして返す。
     * @param file ルートとなるファイルのパス
     * @return インポート定義を置き換えたInputStream。文字コードMS932に対応しないJava VMだとnullを返す。
     */
    public static InputStream process(final File file)
    {
        String result = readFileWithImport(file);
        return stringToInputStream(result);
    }

    private static String readFileWithImport(final File file)
    {
        File canonicalFile;
        try
        {
            canonicalFile = file.getCanonicalFile();
            if (canonicalFiles__.containsKey(canonicalFile.hashCode()))
            {
                fileError(null, CIRCULAR_IMPORT_MESSAGE, canonicalFile);
                return "";
            }
        }
        catch (IOException ex)
        {
            fileError(ex, FILE_OPERATION_FAILED_MESSAGE, file);
            return "";
        }
        StringBuilder fileContent = null;
        BufferedReader br;
        try
        {
            br = new BufferedReader(new FileReader(file));
        }
        catch (FileNotFoundException ex)
        {
            fileError(ex, FILE_NOT_FOUND_MESSAGE, file);
            return "";
        }
        try
        {
            fileContent = new StringBuilder();
            while (true)
            {
                String line = br.readLine();
                if (line == null)
                {
                    break;
                }
                if (line.startsWith(IMPORT_DEFINITION))
                {
                    String definedFileName = line.substring(IMPORT_DEFINITION.length()).trim();
                    File childFile = new File(definedFileName);
                    if (!childFile.isAbsolute())
                    {
                        File parent = file.getParentFile();
                        childFile = new File(parent, definedFileName);
                    }
                    fileContent.append(readFileWithImport(childFile));
                }
                else
                {
                    fileContent.append(line);
                    fileContent.append(LINE_BREAK);
                }
            }
            canonicalFiles__.put(canonicalFile.hashCode(), canonicalFile);
        }
        catch (IOException ex)
        {
            fileError(ex, FILE_OPERATION_FAILED_MESSAGE, file);
            fileContent = new StringBuilder();
        }
        finally
        {
            try
            {
                br.close();
            }
            catch (IOException ex)
            {
                //do nothing
            }
        }

        return fileContent.toString();
    }

    private static void fileError(final Throwable th, final String messagePrefix, final File file)
    {
        String path;
        try
        {
            path = file.getCanonicalPath();
        }
        catch (IOException ioe)
        {
            path = file.getAbsolutePath();
        }
        String message = messagePrefix + path;
        SystemLogger.getInstance().warn(message, th);
    }

    private static InputStream stringToInputStream(final String str)
    {
        try
        {
            byte[] bytes = str.getBytes("MS932");
            InputStream inputStream = new ByteArrayInputStream(bytes);
            return inputStream;
        }
        catch (UnsupportedEncodingException e)
        {
            return null;
        }
    }

}
