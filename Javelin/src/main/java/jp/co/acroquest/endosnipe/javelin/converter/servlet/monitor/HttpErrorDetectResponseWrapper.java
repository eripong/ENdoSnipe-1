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
package jp.co.acroquest.endosnipe.javelin.converter.servlet.monitor;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class HttpErrorDetectResponseWrapper extends HttpServletResponseWrapper
{
    /** Httpレスポンスステータス実行結果 */
    int resultCode_;

    /**
     * コンストラクタ
     * @param response レスポンス
     */
    public HttpErrorDetectResponseWrapper(HttpServletResponse response)
    {
        super(response);
    }

    /**
     * HttpServletResponseをスーパークラスから取得
     * @return HttpServletResponse
     */
    private HttpServletResponse getHttpServletResponse()
    {
        return (HttpServletResponse)super.getResponse();
    }

    /**
     * HttpServletResponse#setStatus(int sc)呼び出し時、
     * エラー番号を取得し、保持する
     * 
     * @param sc エラー番号
     */
    public void setStatus(int sc)
    {
        this.resultCode_ = sc;
        getHttpServletResponse().setStatus(sc);
    }

    /**
     * HttpServletResponse#setStatus(int sc, String sm)呼び出し時、
     * エラー番号を取得し、保持する
     * 
     * @param sc エラー番号
     * @param sm メッセージ
     */
    public void setStatus(int sc, String sm)
    {
        this.resultCode_ = sc;
        getHttpServletResponse().setStatus(sc, sm);
    }

    /**
     * HttpServletResponse#sendError(int sc, String msg)呼び出し時、
     * エラー番号を取得し、保持する
     * 
     * @param sc エラー番号
     */
    public void sendError(int sc, String msg)
        throws IOException
    {
        this.resultCode_ = sc;
        getHttpServletResponse().sendError(sc, msg);
    }

    /**
     * HttpServletResponse#sendError(int sc)呼び出し時、
     * エラー番号を取得し、保持する
     * 
     * @param sc エラー番号
     */
    public void sendError(int sc)
        throws IOException
    {
        this.resultCode_ = sc;
        getHttpServletResponse().sendError(sc);
    }

    /**
     * Httpレスポンスステータス実行結果を取得
     * @return Httpレスポンスステータス実行結果
     */
    public int getResultCode()
    {
        return this.resultCode_;
    }
}
