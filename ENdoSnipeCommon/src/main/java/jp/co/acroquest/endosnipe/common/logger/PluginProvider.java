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


/**
 * ログ対象のプラグインインスタンスを提供するためのインターフェースです。<br />
 * ENdoSnipeLogger を利用するクラスが Eclipse 環境と非Eclipse 環境の両方で
 * 実行される可能性がある場合、本インターフェースの実装クラスの {@link Class}
 * オブジェクトを
 * 
 * @author y-komori
 */
public interface PluginProvider
{
    /**
     * Plugin のインスタンスを返します。<br />
     * 戻り値は Object 型になっていますが、必ず org.eclipse.core.runtime.Plugin
     * のサブクラス(または <code>null</code>) を返してください。<br />
     * これは、本クラスを Eclipse ライブラリに依存させないようにするためです。<br /> 
     * 
     * @return Plugin のインスタンス
     */
    Object getPlugin();
}
