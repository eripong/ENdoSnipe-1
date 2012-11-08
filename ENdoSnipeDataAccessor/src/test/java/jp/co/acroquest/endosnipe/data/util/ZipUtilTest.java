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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jp.co.acroquest.endosnipe.common.util.ResourceUtil;
import jp.co.acroquest.endosnipe.common.util.StreamUtil;
import junit.framework.TestCase;

/**
 * {@link ZipUtil} のためのテストクラスです。<br />
 * 
 * @author y-komori
 */
public class ZipUtilTest extends TestCase
{
    private static final int BUF_SIZE = 8192;

    /**
     * 圧縮と解凍のテストを行います。<br />
     */
    public void testZipAndUnzip()
    {
        ByteArrayOutputStream out = null;
        BufferedReader br1 = null;
        BufferedReader br2 = null;
        try
        {
            // 圧縮処理
            String path = "javelin_010k.jvn";
            File file = ResourceUtil.getResourceAsFile(getClass(), path);
            out = new ByteArrayOutputStream(BUF_SIZE);
            ZipUtil.createZip(out, new FileInputStream(file), path);
            byte[] zippedArray = out.toByteArray();
            StreamUtil.closeStream(out);

            // 展開処理とオリジナルとの比較

            // 展開したストリーム
            InputStream is = ZipUtil.unzipFromByteArray(new ByteArrayInputStream(zippedArray));
            br1 = new BufferedReader(new InputStreamReader(is));
            // オリジナルのストリーム
            br2 = new BufferedReader(new FileReader(file));

            String line1;
            String line2;
            int lineCount = 1;
            while ((line1 = br1.readLine()) != null)
            {
                line2 = br2.readLine();
                assertEquals("line:" + lineCount++, line2, line1);
            }
        }
        catch (FileNotFoundException ex)
        {
            fail(ex.getMessage());
        }
        catch (IOException ex)
        {
            fail(ex.getMessage());
        }
        finally
        {
            StreamUtil.closeStream(out);
            StreamUtil.closeStream(br1);
            StreamUtil.closeStream(br2);
        }
    }
}
