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
package jp.co.acroquest.endosnipe.data;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * DataAccessorがEclipseプラグインとして使われるためのメインクラスです。
 * 
 * @author fujii
 *
 */
public class ENdoSnipeDataAccessorPlugin extends AbstractUIPlugin
{

    private static ENdoSnipeDataAccessorPlugin plugin__;

    /**
     * コンストラクタ。<br />
     * <br />
     * Eclipseがインスタンスを作成するためにpublicにしているもので、
     * これをnewするプログラムを書くのは誤りです。<br />
     * {@link #getDefault()}メソッドを用いて、Eclipseの作成したインスタンスを取得してください。
     */
    public ENdoSnipeDataAccessorPlugin()
    {
        super();
        plugin__ = this;
    }

    /**
     * Eclipseが生成したプラグインインスタンスを返します。
     * 
     * @return DataAccessorのプラグインインスタンス。
     */
    public static ENdoSnipeDataAccessorPlugin getDefault()
    {
        return plugin__;
    }
}
