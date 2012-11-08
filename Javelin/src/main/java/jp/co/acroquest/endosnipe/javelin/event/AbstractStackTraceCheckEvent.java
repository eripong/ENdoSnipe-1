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
package jp.co.acroquest.endosnipe.javelin.event;


/**
 * スタックトレースの内容によって、イベント出力の抑制を行うイベントクラスです。<br />
 * 
 * @author fujii
 *
 */
abstract class AbstractStackTraceCheckEvent extends CommonEvent
{
    /** 比較のためのスタックトレース。 */
    protected String stackTraceToCompare_;

    /** イベント名とスタックトレースのセパレータ */
    private static final char SEPARATOR = '#';

    /** スタックトレースを表すパラメータ */
    protected String paramStackTrace_;

    /**
     * パラメータを保存するMapに値を保存します。<br />
     * このとき、パラメータが"stackTrace"(大文字・小文字の判別は行わない)で終わるならば、比較用にスタックトレースを保存します。<br />
     * 
     * @param key キー。
     * @param value 値。
     */
    public void addParam(String key, String value)
    {
        if (key.equals(this.paramStackTrace_))
        {
            setStackTraceCompare(value);
        }
        super.addParam(key, value);
    }

    /**
     * スタックトレースでハッシュコードを計算する。
     * 
     * @return ハッシュコード。
     */
    public int hashCode()
    {
        String stackTrace = this.name_ + SEPARATOR + this.stackTraceToCompare_;

        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((stackTrace == null) ? 0 : stackTrace.hashCode());
        return result;
    }

    /**
     * スタックトレースで比較する。
     * 
     * @param obj 比較対象。
     * @return 比較結果。
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        AbstractStackTraceCheckEvent other = (AbstractStackTraceCheckEvent)obj;

        String stackTrace = this.name_ + SEPARATOR + this.stackTraceToCompare_;
        String stackTraceOther = other.getName() + SEPARATOR + other.stackTraceToCompare_;

        return stackTrace.equals(stackTraceOther);
    }

    /**
     * 比較用スタックトレース値を設定する。
     * 
     * @param stackTrace スタックトレース
     */
    abstract void setStackTraceCompare(String stackTrace);

}
