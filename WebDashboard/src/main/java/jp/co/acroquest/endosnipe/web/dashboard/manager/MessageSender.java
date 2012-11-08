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
package jp.co.acroquest.endosnipe.web.dashboard.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import javax.servlet.http.HttpServletResponse;

import jp.co.acroquest.endosnipe.web.dashboard.constants.LogMessageCodes;
import jp.co.acroquest.endosnipe.web.dashboard.dto.CometEventWrapper;
import jp.co.acroquest.endosnipe.web.dashboard.entity.MessageEntity;
import jp.co.acroquest.endosnipe.web.dashboard.util.ResponseUtil;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;

import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.comet.CometEvent.EventType;

/**
 * イベント送信用スレッド
 * @author fujii
 *
 */
public class MessageSender implements Runnable
{
    /** ロガー */
    private static final ENdoSnipeLogger LOGGER = ENdoSnipeLogger.getLogger(MessageSender.class);

    /** メッセージのタイムアウト値 */
    private static final long TIMEOUT = 60 * 1000;

    /** 各クライアントに送信することのできるキューのサイズ */
    private static final int MESSAGE_QUEUE_SIZE = 100;

    /** 通信しているかどうか */
    private boolean running_ = true;

    /** 最後にメッセージを削除した時間 */
    private long lastDeleteTime_ = System.currentTimeMillis();

    /** 送信メッセージ */
    protected Map<String, Queue<MessageEntity>> messages_ =
                                                            new HashMap<String, Queue<MessageEntity>>();

    /** 通信用レスポンスオブジェクト */
    protected Map<String, CometEventWrapper> eventMap_ = new HashMap<String, CometEventWrapper>();

    /**
     * 通信を終了する。
     */
    public void stop()
    {
        this.running_ = false;
    }

    /**
     * Add message for sending.
     * @param clientId クライアントID
     * @param message メッセージ
     */
    public void send(String clientId, String message)
    {
        synchronized (this.messages_)
        {
            Queue<MessageEntity> clientMessages = this.messages_.get(clientId);
            if (clientMessages == null)
            {
                clientMessages = new ArrayBlockingQueue<MessageEntity>(MESSAGE_QUEUE_SIZE);
            }
            MessageEntity entity = new MessageEntity(message);
            boolean success = clientMessages.offer(entity);
            if (success == false)
            {
                LOGGER.log(LogMessageCodes.QUEUE_FULL);
                deleteOldMessage();
            }
            this.messages_.put(clientId, clientMessages);
            this.messages_.notify();
        }
    }

    /**
     * WebDashBoardに通知を行う。
     */
    public void run()
    {
        boolean waitFlag = false;
        while (this.running_)
        {
            try
            {
                synchronized (this.messages_)
                {
                    if (this.messages_.size() == 0 || waitFlag == true)
                    {
                        this.messages_.wait();
                    }
                }
            }
            catch (InterruptedException e)
            {
                // Ignore
            }
            waitFlag = false;
            synchronized (this.eventMap_)
            {
                if (this.eventMap_.size() == 0)
                {
                    waitFlag = true;
                    continue;
                }
                Map<String, String> pendingMessagesMap = createSendMessageMap();
                if (pendingMessagesMap.size() > 0)
                {
                    try
                    {
                        sendMessage(pendingMessagesMap);
                    }
                    catch (Exception ex)
                    {
                        LOGGER.log(LogMessageCodes.COMET_ERROR, ex);
                    }
                }
                else
                {
                    waitFlag = true;
                }
            }
            deleteOldMessage();
        }
    }

    /**
     * クライアントに送信するメッセージのMapを作成します。
     * @return クライアントに送信するメッセージのMap
     */
    private Map<String, String> createSendMessageMap()
    {
        Map<String, String> pendingMessagesMap = new HashMap<String, String>();
        synchronized (this.messages_)
        {
            Set<Entry<String, Queue<MessageEntity>>> messageSet = this.messages_.entrySet();
            // ConcurrentModificationExceptionが発生しないように、Iteratorを使用する。
            Iterator<Entry<String, Queue<MessageEntity>>> messageIterator = messageSet.iterator();
            while (messageIterator.hasNext())
            {
                Entry<String, Queue<MessageEntity>> messageEntry = messageIterator.next();
                String clientId = messageEntry.getKey();
                boolean hasResponse = this.eventMap_.containsKey(clientId);
                if (hasResponse == false)
                {
                    continue;
                }
                CometEventWrapper event = this.eventMap_.get(clientId);
                EventType eventType = event.getEventType();
                if (eventType == EventType.END || eventType == EventType.ERROR)
                {
                    this.eventMap_.remove(clientId);
                    continue;
                }

                Queue<MessageEntity> pendingMessageQueue = messageEntry.getValue();
                if (pendingMessageQueue != null && pendingMessageQueue.size() != 0)
                {
                    MessageEntity pendingMessage = pendingMessageQueue.poll();
                    String message = pendingMessage.getMessage();
                    pendingMessagesMap.put(clientId, message);
                    if (pendingMessageQueue.size() == 0)
                    {
                        messageIterator.remove();
                    }
                }
            }
        }
        return pendingMessagesMap;
    }

    /**
     * クライアントにメッセージを送信します。
     * @param pendingMessagesMap クライアントに送信するメッセージを格納しているMapオブジェクト
     */
    private void sendMessage(Map<String, String> pendingMessagesMap)
    {
        Set<Entry<String, CometEventWrapper>> eventSet = this.eventMap_.entrySet();

        // ConcurrentModificationExceptionが発生しないように、Iteratorを使用する。
        Iterator<Entry<String, CometEventWrapper>> eventSetIterator = eventSet.iterator();
        while (eventSetIterator.hasNext())
        {
            Entry<String, CometEventWrapper> eventEntry = eventSetIterator.next();
            String clientId = eventEntry.getKey();
            CometEventWrapper event = eventEntry.getValue();
            EventType eventType = event.getEventType();
            if (eventType == EventType.END || eventType == EventType.ERROR)
            {
                eventSetIterator.remove();
                continue;
            }
            if (event.isValidResponse() == false)
            {
                eventSetIterator.remove();
                continue;
            }

            HttpServletResponse response = event.getResponse();

            ResponseUtil.sendMessageToClient(response, pendingMessagesMap, clientId);
            eventSetIterator.remove();
            event.close();
        }
    }

    /**
     * MessageSenderオブジェクトに一定時間以上、格納されているメッセージを削除します。
     */
    private void deleteOldMessage()
    {
        // 前回削除した時間から一定時間以内の場合、処理を行わない。
        if (this.lastDeleteTime_ + TIMEOUT < System.currentTimeMillis())
        {
            return;
        }
        synchronized (this.messages_)
        {
            Set<Entry<String, Queue<MessageEntity>>> messageSet = this.messages_.entrySet();
            // ConcurrentModificationExceptionが発生しないように、Iteratorを使用する。
            Iterator<Entry<String, Queue<MessageEntity>>> messagesIterator = messageSet.iterator();
            long now = System.currentTimeMillis();
            while (messagesIterator.hasNext())
            {
                Entry<String, Queue<MessageEntity>> messageEntry = messagesIterator.next();
                Queue<MessageEntity> entityList = messageEntry.getValue();
                Iterator<MessageEntity> messageEntityIterator = entityList.iterator();
                while (messageEntityIterator.hasNext())
                {
                    MessageEntity entity = messageEntityIterator.next();
                    boolean isTimeout = entity.isTimeout(now, TIMEOUT);
                    if (isTimeout)
                    {
                        messageEntityIterator.remove();
                    }
                }
                if (entityList.size() == 0)
                {
                    messagesIterator.remove();
                }
            }
            this.lastDeleteTime_ = System.currentTimeMillis();
        }
    }

    /**
     * 通信用レスポンスを追加します。
     * @param clientId クライアントID
     * @param event {@link CometEvent}オブジェクト
     * @throws IOException @link {@link CometEvent}オブジェクトのクローズに失敗した場合
     */
    public void addCometEvent(String clientId, CometEvent event)
        throws IOException
    {
        synchronized (this.eventMap_)
        {
            // すでに登録しているレスポンスを一度削除する。
            CometEventWrapper setCometEvent = this.eventMap_.get(clientId);
            if (setCometEvent != null)
            {
                EventType eventType = setCometEvent.getEventType();
                this.eventMap_.remove(clientId);
                if (eventType == EventType.BEGIN)
                {
                    setCometEvent.close();
                }
            }
            HttpServletResponse response = event.getHttpServletResponse();
            CometEventWrapper eventWrapper = new CometEventWrapper(event, response);
            this.eventMap_.put(clientId, eventWrapper);
        }
    }

    /**
     * 送信メッセージキューをnotifyします。
     */
    public void notifyMessage()
    {
        synchronized (this.messages_)
        {
            this.messages_.notify();
        }
    }

    /**
     * 通信用レスポンスを取得します。
     * @param clientId クライアントID
     * @return {@link CometEvent}オブジェクト
     */
    public CometEventWrapper getCometEvent(String clientId)
    {
        synchronized (this.eventMap_)
        {
            return this.eventMap_.get(clientId);
        }
    }

    /**
     * 通信用レスポンスを削除します。
     * @param response {@link HttpServletResponse}オブジェクト
     * @param delSetting 保存した設定情報削除するかどうか。
     */
    public void removeCometEvent(HttpServletResponse response, boolean delSetting)
    {
        String clientId = null;
        synchronized (this.eventMap_)
        {
            Set<Entry<String, CometEventWrapper>> responseSet = this.eventMap_.entrySet();

            // ConcurrentModificationExceptionが発生しないように、Iteratorを使用する。
            Iterator<Entry<String, CometEventWrapper>> responseSetIterator = responseSet.iterator();
            while (responseSetIterator.hasNext())
            {
                Entry<String, CometEventWrapper> entry = responseSetIterator.next();
                CometEventWrapper event = entry.getValue();
                EventType eventType = event.getEventType();
                if (eventType == EventType.END || eventType == EventType.ERROR)
                {
                    responseSetIterator.remove();
                    continue;
                }
                HttpServletResponse settingResponse = event.getResponse();
                if (response.equals(settingResponse))
                {
                    clientId = entry.getKey();
                    deleteSettings(clientId, delSetting);
                    responseSetIterator.remove();
                }
            }
        }
        // サーバにメッセージが残っていれば、削除する。
        if (clientId == null)
        {
            return;
        }
        synchronized (this.messages_)
        {
            this.messages_.remove(clientId);
        }
    }

    /**
     * 通信用レスポンスを削除します。
     * @param clientId クライアントID
     * @param delSetting 保存した設定情報削除するかどうか。<code>true</codeのときに、設定情報を削除する。
     * @throws IOException CometEvent削除時に例外が発生した場合
     */
    public void removeCometEvent(String clientId, boolean delSetting)
        throws IOException
    {
        synchronized (this.eventMap_)
        {
            deleteSettings(clientId, delSetting);
            CometEventWrapper event = this.eventMap_.get(clientId);
            if (event == null)
            {
                return;
            }
            EventType eventType = event.getEventType();
            this.eventMap_.remove(clientId);
            if (eventType == EventType.BEGIN)
            {
                event.close();
            }
        }
    }

    /**
     * 管理クラスが保存しているクライアントの設定情報を削除する。
     * @param clientId クライアントID
     * @param delSetting 保存した設定情報削除するかどうか。<code>true</codeのときに、設定情報を削除する。
     */
    private void deleteSettings(String clientId, boolean delSetting)
    {
        if (delSetting == true)
        {
            EventManager manager = EventManager.getInstance();
            manager.removeClientSetting(clientId);
            manager.removeAlarmSetting(clientId);
            manager.removeResourceAlarmSetting(clientId);
        }
    }

    /**
     * EventMapを取得します。
     * ただし、このメソッドは、外部からEventMapをロックするときのみ利用してください。
     * @return EventMap
     */
    public Map<String, CometEventWrapper> getEventMap()
    {
        return this.eventMap_;
    }

    /**
     * 終了処理を行います。
     */
    public void destroy()
    {
        synchronized (this.eventMap_)
        {
            this.eventMap_.clear();
        }
    }

}
