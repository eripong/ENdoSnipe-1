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
package jp.co.acroquest.endosnipe.common.logger;

import jp.co.acroquest.endosnipe.common.util.EclipseUtil;

/**
 * {@link PluginProvider} のための基底クラスです。<br />
 * 
 * @author y-komori
 */
public abstract class AbstractPluginProvider implements PluginProvider
{
    private static final String METHOD_NAME = "getDefault";

    private Object plugin_;

    /**
     * {@inheritDoc}
     * @see jp.co.acroquest.endosnipe.common.logger.PluginProvider#getPlugin()
     */
    public Object getPlugin()
    {
        if (EclipseUtil.isEclipseAvailable() == true)
        {
            if (plugin_ == null)
            {
                plugin_ = EclipseUtil.getPlugin(getClassName(), getMethodName(), getClassLoader());
            }
            return plugin_;
        }
        return null;
    }

    /**
     * クラスローダを返します。<br />
     * 
     * @return クラスローダ
     */
    protected abstract ClassLoader getClassLoader();

    /**
     * クラス名を返します。<br >
     * 
     * @return クラス名
     */
    protected abstract String getClassName();

    /**
     * メソッド名を返します。<br />
     * このメソッドはオーバーライドして利用してください。<br />
     * 
     * @return メソッド名
     */
    protected String getMethodName()
    {
        return METHOD_NAME;
    }
}
