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
 * 共通ログのためのメッセージコードです。<br />
 * 
 * @author y-komori
 */
public interface CommonLogMessageCodes
{
    /**
     * 例外が発生した場合のメッセージコードです。
     * <dl>
     * <dt><b>値 :</b></dt>
     * <dd>{@value}</dd>
     * </dl>
     */
    String EXCEPTION_OCCURED = "EECM0000";

    /**
     * 例外が発生した場合のメッセージコードです。
     * <dl>
     * <dt><b>値 :</b></dt>
     * <dd>{@value}</dd>
     * </dl>
     */
    String EXCEPTION_OCCURED_WITH_RESASON = "EECM0001";

    /**
     * パラメータが <code>null</code> であってはいけない場合のメッセージコードです。
     * <dl>
     * <dt><b>値 :</b></dt>
     * <dd>{@value}</dd>
     * </dl>
     */
    String CANT_BE_NULL = "EECM0002";

    /**
     * パラメータが空文字列であってはいけない場合のメッセージコードです。
     * <dl>
     * <dt><b>値 :</b></dt>
     * <dd>{@value}</dd>
     * </dl>
     */
    String CANT_BE_EMPTY_STRING = "EECM0003";

    /**
     * 型が想定している型に一致しない場合のメッセージコードです。
     * <dl>
     * <dt><b>値 :</b></dt>
     * <dd>{@value}</dd>
     * </dl>
     */
    String TYPE_MISS_MATCH = "EECM0004";

    /**
     * 予期せぬエラーが発生した場合のメッセージコードです。
     * <dl>
     * <dt><b>値 :</b></dt>
     * <dd>{@value}</dd>
     * </dl>
     */
    String UNEXPECTED_ERROR = "EECM0005";

    /**
     * Eclipse 3.4 以降が必要な機能を読み込んだ場合のメッセージコードです。
     * <dl>
     * <dt><b>値 :</b></dt>
     * <dd>{@value}</dd>
     * </dl>
     */
    String ECLIPSE_3_4_IS_REQUIRED = "WECM0011";

    /**
     * クリップボードにコピーするデータが大きすぎる場合のメッセージコードです。
     * <dl>
     * <dt><b>値 :</b></dt>
     * <dd>{@value}</dd>
     * </dl>
     */
    String TOO_LARGE_COPY_TO_CLIPBOARD = "EECM0012";
}
