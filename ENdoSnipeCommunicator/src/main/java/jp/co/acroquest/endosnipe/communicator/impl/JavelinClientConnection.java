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
package jp.co.acroquest.endosnipe.communicator.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.communicator.TelegramUtil;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;

/**
 * クライアントと通信を行うクラスです。<br />
 * 
 * @author eriguchi
 *
 */
public class JavelinClientConnection
{
    private static final int SO_TIMEOUT = 10000;

    private static final int SEND_QUEUE_SIZE = 100;

    private static final int BUFFER_SIZE = 512 * 1024;

    /** 電文破棄を表示する間隔 */
    private static final int TELEGRAM_DISCARD_INTERVAL = 5000;

    /** 前回電文を破棄した時間 */
    private long lastDiscard_;

    /** 破棄した電文の合計サイズ */
    private int discardSum_;

    /** クライアントソケット */
    private Socket clientSocket_ = null;

    /** 入力用ストリーム */
    private BufferedInputStream inputStream_ = null;

    /** 入力用ストリーム */
    private BufferedOutputStream outputStream_ = null;

    private final BlockingQueue<byte[]> queue_;

    private boolean discard_;

    /**
     * 送信用キュー、ソケット、入出力ストリームを構築します。<br />
     * 
     * @param objSocket クライアントソケット
     * @param discard アラーム送信間隔内に発生した同じアラームを破棄するかどうか
     * @throws IOException 入出力例外が発生した場合
     */
    public JavelinClientConnection(final Socket objSocket, boolean discard)
        throws IOException
    {
        this.discard_ = discard;
        this.queue_ = new ArrayBlockingQueue<byte[]>(SEND_QUEUE_SIZE);
        this.clientSocket_ = objSocket;
        this.lastDiscard_ = 0;
        this.discardSum_ = 0;
        try
        {
            this.inputStream_ = new BufferedInputStream(this.clientSocket_.getInputStream());
            this.outputStream_ = new BufferedOutputStream(this.clientSocket_.getOutputStream());
        }
        catch (IOException ioe)
        {
            close();
            throw ioe;
        }
    }

    /**
     * 終了処理です。<br />
     * 
     */
    void close()
    {
        String key = "";
        String message = "";
        try
        {
            if (this.clientSocket_ != null && this.clientSocket_.isClosed() == false)
            {
                key = "javelin.communicate.commonMessage.clientDisconnected";
                message = CommunicatorMessages.getMessage(key, this.clientSocket_.getInetAddress());
                stopSendThread();
                this.clientSocket_.close();
                SystemLogger.getInstance().info(message);
            }
        }
        catch (IOException ioe)
        {
            key = "javelin.communicate.commonMessage.clientSocketCloseError";
            message = CommunicatorMessages.getMessage(key);
            SystemLogger.getInstance().warn(message, ioe);
        }

    }

    private void stopSendThread()
    {
        this.sendAlarm(new byte[0]);
    }

    /**
     * 送信処理です。<br />
     * 
     * @param byteOutputArr 出力データのバイト配列
     * @throws IOException 入出力例外が発生した場合
     */
    void send(final byte[] byteOutputArr)
        throws IOException
    {
        if (this.clientSocket_.isClosed() == true)
        {
            return;
        }

        int headerLength = TelegramUtil.TELEGRAM_HEADER_LENGTH;
        this.outputStream_.write(byteOutputArr, 0, headerLength);
        this.outputStream_.flush();

        int currentPos = headerLength;
        int remainLength = byteOutputArr.length - headerLength;
        while(remainLength > 0)
        {
            int writeLength = Math.min(BUFFER_SIZE, remainLength);
            this.outputStream_.write(byteOutputArr, currentPos, writeLength);
            this.outputStream_.flush();
            remainLength -= writeLength;
            currentPos += writeLength;
        }
    }

    /**
     * 電文をデバッグ出力します。<br />
     * 
     * @param message メッセージ
     * @param response 受信電文
     * @param length 電文長
     */
    public void logTelegram(final String message, final Telegram response, final int length)
    {
        String telegramStr = TelegramUtil.toPrintStr(response, length);
        String hostAddress = this.clientSocket_.getInetAddress().getHostAddress();
        SystemLogger.getInstance().warn(
                                        message + hostAddress + ":" + this.clientSocket_.getPort()
                                                + SystemLogger.NEW_LINE + telegramStr);
    }

    /**
     * 受信電文のbyte配列を返す。
     * 
     * @return byte配列
     * @throws IOException 入出力例外の発生
     */
    byte[] recvRequest()
        throws IOException
    {
        this.clientSocket_.setSoTimeout(SO_TIMEOUT);

        byte finalTelegram = TelegramConstants.HALFWAY_TELEGRAM;
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        do
        {
            // ヘッダを読み込む
            byte[] header = new byte[Header.HEADER_LENGTH];
            readFull(header, 0, header.length);

            ByteBuffer headerBuffer = ByteBuffer.wrap(header);
            int telegramLength = headerBuffer.getInt();
            headerBuffer.getLong();
            finalTelegram = headerBuffer.get();
            
            if (resultStream.size() == 0)
            {
                resultStream.write(headerBuffer.array());
            }

            if (telegramLength - Header.HEADER_LENGTH < 0)
            {
                throw new IOException("Telegram length is abnormal.");
            }
            if (telegramLength > TelegramUtil.TELEGRAM_LENGTH_MAX)
            {
                throw new IOException("Telegram length is too long.");
            }

            SystemLogger.getInstance().debug("telegramLength  = [" + telegramLength + "]");

            // ヘッダと本文を含めたデータを格納する配列
            byte[] telegram = new byte[telegramLength - header.length];

            // 本文を読み込む
            readFull(telegram, 0, telegramLength - header.length);

            resultStream.write(telegram);

        }
        while (finalTelegram != TelegramConstants.FINAL_TELEGRAM);
        
        byte[] telegramBytes = resultStream.toByteArray();
        int telegramLength = telegramBytes.length;
        ByteBuffer outputBuffer = ByteBuffer.wrap(telegramBytes);

        // ヘッダを変換する
        outputBuffer.rewind();
        outputBuffer.putInt(telegramLength);

        return outputBuffer.array();
    }

    /**
     * ストリームから、指定されたバイト数を読み込みます。<br />
     *
     * 指定されたバイト数分のデータを読み込めるまで、このメソッドは処理を返しません。<br />
     *
     * データ格納先には、最低 <code>(offset + length)</code> 分の領域が必要です。<br />
     *
     * @param data データ格納先
     * @param offset データを格納する最初の位置（ data[offset] に、最初のデータが格納される）
     * @param length データを読み込むバイト数
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private void readFull(byte[] data, final int offset, final int length)
        throws IOException
    {
        // read() は1回ですべてのデータを読み込むことができないため、すべてのデータを読み込むまで繰り返す
        int pos = offset;
        int remainLength = length;
        while (remainLength > 0)
        {
            int inputCount = this.inputStream_.read(data, pos, remainLength);
            if (inputCount < 0)
            {
                throw new IOException("Cannot read.");
            }
            pos += inputCount;
            remainLength -= inputCount;
        }
    }

    /**
     * アラームを送信します。<br />
     * このメソッドはスレッド外から呼ばれます。<br />
     * 
     * @param telegramArray 電文のバイト配列
     */
    public void sendAlarm(final byte[] telegramArray)
    {
        Socket clientSocket = this.clientSocket_;
        if (clientSocket == null || clientSocket.isClosed())
        {
            return;
        }        
        
        boolean offerResult = this.queue_.offer(telegramArray);
        
        if (offerResult == false && telegramArray != null && telegramArray.length > 0)
        {
            long time = System.currentTimeMillis();
            if (time - this.lastDiscard_ > TELEGRAM_DISCARD_INTERVAL)
            {
                SystemLogger.getInstance().warn(
                                                "Telegram Discard:length = "
                                                        + telegramArray.length);
                this.lastDiscard_ = time;
                this.discardSum_ = 0;
            }
            else
            {
                this.discardSum_ += telegramArray.length;
            }
        }
    }

    /**
     * ソケットが閉じているかどうかを返します。<br />
     * 
     * @return ソケットが閉じている場合、<code>true</code>
     */
    public boolean isClosed()
    {
        Socket clientSocket = this.clientSocket_;
        return clientSocket == null || clientSocket.isClosed();
    }
    
    /**
     * ソケットの接続状態を返します。
     * @return ソケットが接続されている場合、<code>true</code>
     */
    public boolean isConnected()
    {
        Socket clientSocket = this.clientSocket_;
        return clientSocket != null && clientSocket.isConnected();
    }

    /**
     * キューからデータを取り出します。<br />
     *
     * @return 取り出したデータ
     * @throws InterruptedException 割り込み処理が入ったとき
     */
    byte[] take()
        throws InterruptedException
    {
        return queue_.take();
    }
}
