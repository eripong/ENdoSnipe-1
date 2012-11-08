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
package jp.co.acroquest.endosnipe.javelin.converter.util;

/**
 * コード埋め込み時に使用するユーティリティクラス。
 *
 * @author Sakamoto
 */
public class ConverterUtil
{
    /**
     * コンストラクタ
     */
    private ConverterUtil()
    {
        // Do Nothing.
    }

    /**
     * クラス名から、$$以降を削除して、単純なクラス名に変える。<br>
     * Seasarアプリケーションでは、コンテナによって、実装クラスが生成される事が多く、 クラス名が変化するため、この対処が必要になります。
     * 
     * @param className 更新対象のクラス名。
     * @return $$以降を削除したクラス名。
     */
    public static String toSimpleName(String className)
    {
        if (className == null)
        {
            return null;
        }
        
        if(className.startsWith("$$"))
        {
            className = className.substring("$$".length());
        }

        int indexOfDollarDollar = className.indexOf("$$", 1);
        if (indexOfDollarDollar < 0)
        {
            return className;
        }

        return className.substring(0, indexOfDollarDollar);
    }

    
}
