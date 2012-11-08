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
package jp.co.acroquest.endosnipe.common.parser;

import java.io.IOException;
import java.io.InputStream;

/**
 * Javelin ログのデータを取得する手段を提供するインタフェース。<br />
 *
 * @author y-sakamoto
 */
public abstract class JavelinLogAccessor
{
    /**
     * Javelin ログのファイル名。<br />
     */
    private String fileName_;

    /**
     * Javelin ログのファイル名を設定します。<br />
     *
     * @param fileName ファイル名
     */
    public void setFileName(final String fileName)
    {
        this.fileName_ = fileName;
    }

    /**
     * Javelin ログのファイル名を返します。<br />
     *
     * @return ファイル名
     */
    public String getFileName()
    {
        return this.fileName_;
    }

    /**
     * Javelin ログデータの入力ストリームを返します。<br /> 
     *
     * @return Javelin ログデータの入力ストリーム
     * @throws IOException 入力ストリーム作成時にエラーが発生した場合
     */
    public abstract InputStream getInputStream()
        throws IOException;

}
