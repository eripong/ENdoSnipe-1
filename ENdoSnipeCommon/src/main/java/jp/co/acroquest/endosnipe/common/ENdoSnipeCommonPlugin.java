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
package jp.co.acroquest.endosnipe.common;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * ENdoSnipeCommon のためのプラグインクラスです。<br />
 * 
 * @author y-komori
 */
public class ENdoSnipeCommonPlugin extends AbstractUIPlugin
{
    private static ENdoSnipeCommonPlugin plugin__;

    /**
     * {@link ENdoSnipeCommonPlugin} を構築します<br /> 
     */
    public ENdoSnipeCommonPlugin()
    {
        super();
        this.plugin__ = this;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(final BundleContext context)
        throws Exception
    {
        super.start(context);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext context)
        throws Exception
    {
        super.stop(context);
    }

    /**
     * {@link ENdoSnipeCommonPlugin} のインスタンスを返します。<br />
     * 
     * @return {@link ENdoSnipeCommonPlugin} のインスタンス
     */
    public static ENdoSnipeCommonPlugin getDefault()
    {
        return plugin__;
    }
}
