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
package jp.co.acroquest.endosnipe.web.dashboard.entity;

/**
 * クライアントに送信するメッセージのエンティティです。
 * @author fujii
 *
 */
public class MessageEntity
{
    /** メッセージ生成時刻 */
    private long   receiveTime_;

    /** メッセージ */
    private String message_;

    /**
     * コンストラクです。
     * @param message メッセージ
     */
    public MessageEntity(String message)
    {
        this.message_ = message;
        this.receiveTime_ = System.currentTimeMillis();
    }
    
    /**
     * メッセージを取得します。
     * @return メッセージ
     */
    public String getMessage()
    {
        return this.message_;
    }
    
    /**
     * メッセージがタイムアウトしているかどうかを返します。
     * @param now 現在時刻
     * @param timeout タイムアウト値
     * @return 現在時刻が、メッセージ生成時刻とタイムアウト値を超えるときに、<code>true</code>
     */
    public boolean isTimeout(long now, long timeout)
    {
        if (this.receiveTime_ + timeout < now)
        {
            return true;
        }
        return  false;
    }

}
