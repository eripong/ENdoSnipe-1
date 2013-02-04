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
package jp.co.acroquest.endosnipe.perfdoctor.exception;

import jp.co.acroquest.endosnipe.perfdoctor.Messages;

/**
 * ルールの作成失敗例外。
 * @author tanimoto
 *
 */
public class RuleCreateException extends Exception
{
    private static final long serialVersionUID = 1L;

    private String[]          messages_;

    /**
     * コンストラクタ。
     * @param messageId メッセージID
     * @param args メッセージ引数
     */
    public RuleCreateException(final String messageId, final Object[] args)
    {
        super(Messages.getMessage(messageId, args));
    }

    /**
     * コンストラクタ。
     * 複数のメッセージを通知することができる。
     * @param messageId メッセージID
     * @param args メッセージ引数
     * @param messages 上位に伝達するメッセージ一覧
     */
    public RuleCreateException(final String messageId, final Object[] args, final String[] messages)
    {
        super(Messages.getMessage(messageId, args));
        this.messages_ = messages;
    }

    /**
     * メッセージ一覧を取得する。
     * @return メッセージ一覧
     */
    public String[] getMessages()
    {
        return this.messages_;
    }
}
