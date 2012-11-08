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
package jp.co.acroquest.endosnipe.data.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import jp.co.acroquest.endosnipe.common.util.ResourceUtil;
import jp.co.acroquest.endosnipe.common.util.StreamUtil;
import junit.framework.TestCase;

/**
 * {@link SQLReader} のためのテストクラスです。<br />
 * 
 * @author y-komori
 */
public class SQLReaderTest extends TestCase
{
    public void testReadSql()
    {
        String fileName1 = getClass().getSimpleName() + ".ddl";
        File file1 = ResourceUtil.getResourceAsFile(getClass(), fileName1);
        BufferedReader br = null;
        try
        {
            // 実行
            SQLReader reader = new SQLReader(new FileReader(file1));
            List<String> sqls = reader.readSql();

            // 検証
            String fileName2 = getClass().getSimpleName() + ".expected.ddl";
            File file2 = ResourceUtil.getResourceAsFile(getClass(), fileName2);
            br = new BufferedReader(new FileReader(file2));

            int count = 0;
            for (String line : sqls)
            {
                assertEquals("line:" + count++, br.readLine(), line);
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
            StreamUtil.closeStream(br);
        }
    }
}
