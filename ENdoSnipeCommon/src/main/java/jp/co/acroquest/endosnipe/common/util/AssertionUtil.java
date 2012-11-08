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

import jp.co.acroquest.endosnipe.common.logger.CommonLogMessageCodes;

import org.seasar.framework.exception.SIllegalArgumentException;

/**
 * アサーションを行うためのユーティリティクラスです。<br />
 * 
 * @author y-komori
 */
public class AssertionUtil implements CommonLogMessageCodes
{
    private AssertionUtil()
    {

    }

    /**
     * <code>arg</code> が <code>null</code> でないことをチェックします。<br />
     * 
     * @param name
     *            オブジェクト名称
     * @param arg
     *            チェック対象オブジェクト
     */
    public static void assertNotNull(final String name, final Object arg)
    {
        if (arg == null)
        {
            throw new SIllegalArgumentException(CANT_BE_NULL, new Object[]{name});
        }
    }

    /**
     * <code>arg</code> が <code>null</code> または空文字列ではないことをチェックします。<br />
     * 
     * @param name
     *            オブジェクト名称
     * @param arg
     *            チェック対象文字列
     */
    public static void assertNotEmpty(final String name, final String arg)
    {
        if (arg == null || arg.length() == 0)
        {
            throw new SIllegalArgumentException(CANT_BE_EMPTY_STRING, new Object[]{name});
        }
    }

    /**
     * <code>arg</code> が <code>clazz</code> のサブクラスであることをチェックします。<br />
     * 
     * @param name
     *            オブジェクト名称
     * @param clazz
     *            クラス
     * @param arg
     *            チェック対象オブジェクト
     */
    public static void assertInstanceOf(final String name, final Class<?> clazz, final Object arg)
    {
        if (!clazz.isAssignableFrom(arg.getClass()))
        {
            throw new SIllegalArgumentException(TYPE_MISS_MATCH, new Object[]{name,
                    clazz.getClass().getName()});
        }
    }
}
