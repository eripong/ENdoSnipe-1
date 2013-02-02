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
package jp.co.acroquest.endosnipe.javelin.converter.jdbc;
import java.lang.instrument.IllegalClassFormatException;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.StatsJavelinRecorder;
import jp.co.acroquest.endosnipe.javelin.converter.AbstractConverter;
import jp.co.acroquest.endosnipe.javelin.jdbc.instrument.JdbcJavelinTransformer;
import jp.co.smg.endosnipe.javassist.CannotCompileException;
import jp.co.smg.endosnipe.javassist.NotFoundException;

/**
 * JDBCJavelin用コンバータ
 * 
 * @author yamasaki
 */
public class JdbcConverter extends AbstractConverter
{
    /** JDBCJavelinで最初に読まれるクラス */
    private static JdbcJavelinTransformer transformer__;

    /**
     * {@inheritDoc}
     */
    public void init()
    {
        // StatsJavelinRecorderを初期化する
        synchronized (StatsJavelinRecorder.class)
        {
            if (StatsJavelinRecorder.isInitialized() == false)
            {
                StatsJavelinRecorder.javelinInit(new JavelinConfig());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void convertImpl()
        throws CannotCompileException,
            NotFoundException
    {
        // JDBCJavelinTransformerを呼び出す。
        synchronized (JdbcConverter.class)
        {
            if (transformer__ == null)
            {
                transformer__ = new JdbcJavelinTransformer();
                transformer__.init();
            }
        }

        // コード埋め込みを行い、クラスファイルバッファに設定する。
        try
        {
            byte[] newClassfileBuffer =
                    transformer__.transform(getClassName(), getClassfileBuffer(), getClassPool(),
                                            getCtClass());

            setNewClassfileBuffer(newClassfileBuffer);
        }
        catch (IllegalClassFormatException ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
    }
}
