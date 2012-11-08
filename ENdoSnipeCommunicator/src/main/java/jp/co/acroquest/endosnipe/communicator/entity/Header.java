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
package jp.co.acroquest.endosnipe.communicator.entity;

/**
 * 電文ヘッダのためのエンティティクラスです。<br />
 * 
 * @author y-komori
 */
public class Header
{
    /** ヘッダ長 */
    public static final int HEADER_LENGTH = 18;

    /** 電文長 */
    private int intSize_ = 0;

    /** 電文ID */
    private long id_ = 0;

    /** 終了フラグ(0:終了でない 1:終了) */
    private byte lastTelegram_ = 0;

    /** 電文種別 */
    private byte byteTelegramKind_ = 0;

    /** 要求応答種別 */
    private byte byteRequestKind_ = 0;

    /**
     * 電文長を取得します。<br />
     * 
     * @return 電文長
     */
    public int getIntSize()
    {
        return intSize_;
    }

    /**
     * 電文長を設定します。<br />
     * 
     * @param intSize 電文長
     */
    public void setIntSize(final int intSize)
    {
        this.intSize_ = intSize;
    }

    /**
     * 電文 ID を取得します。
     *
     * @return 電文 ID
     */
    public long getId()
    {
        return this.id_;
    }

    /**
     * 電文 ID を設定します。
     *
     * @param id 電文 ID
     */
    public void setId(final long id)
    {
        this.id_ = id;
    }

    
    /**
     * 終了フラグを取得します。
     *
     * @return 終了フラグ
     */
    public byte getLastTelegram()
    {
        return lastTelegram_;
    }

    /**
     * 終了フラグを設定します。
     *
     * @param lastTelegram 終了フラグ
     */
    public void setLastTelegram(byte lastTelegram)
    {
        lastTelegram_ = lastTelegram;
    }

    /**
     * 電文種別を取得します。<br />
     * 
     * @return 電文種別
     */
    public byte getByteTelegramKind()
    {
        return byteTelegramKind_;
    }

    /**
     * 電文種別を設定します。<br />
     * 
     * @param byteTelegramKind 電文種別
     */
    public void setByteTelegramKind(final byte byteTelegramKind)
    {
        this.byteTelegramKind_ = byteTelegramKind;
    }

    /**
     * 要求応答種別を取得します。<br />
     * 
     * @return 要求応答種別
     */
    public byte getByteRequestKind()
    {
        return byteRequestKind_;
    }

    /**
     * 要求応答種別を設定します。<br />
     * 
     * @param byteRequestKind 要求応答種別
     */
    public void setByteRequestKind(final byte byteRequestKind)
    {
        this.byteRequestKind_ = byteRequestKind;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Id=");
        builder.append(getId());
        builder.append(",RequestKind=");
        builder.append(getByteRequestKind());
        builder.append(",TelegramKind=");
        builder.append(getByteTelegramKind());
        builder.append(",IntSize=");
        builder.append(getIntSize());
        return builder.toString();
    }

}
