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
package jp.co.acroquest.endosnipe.web.dashboard.listener.collector;

import java.util.Map;
import java.util.Map.Entry;

import jp.co.acroquest.endosnipe.communicator.AbstractTelegramListener;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.web.dashboard.config.ResourceAlarmSetting;
import jp.co.acroquest.endosnipe.web.dashboard.constants.EventConstants;
import jp.co.acroquest.endosnipe.web.dashboard.entity.ResourceAlarmEntity;
import jp.co.acroquest.endosnipe.web.dashboard.manager.EventManager;
import jp.co.acroquest.endosnipe.web.dashboard.manager.MessageSender;
import net.arnx.jsonic.JSON;

/**
 * 閾値超過アラームをクライアントに通知するリスナクラスです。
 * @author fujii
 *
 */
public class ResourceAlarmListener extends AbstractTelegramListener
{

    /** メッセージ送信用オブジェクトです。 */
    private MessageSender messageSender_;

    /** エージェントID */
    private int           agentId_;

    /**
     * 
     * コンストラクタです。
     * @param messageSender {@link MessageSender}オブジェクト
     * @param agentId エージェントID
     */
    public ResourceAlarmListener(MessageSender messageSender, int agentId)
    {
        this.messageSender_ = messageSender;
        this.agentId_ = agentId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Telegram doReceiveTelegram(Telegram telegram)
    {
        ResourceAlarmEntity alarmEntity = new ResourceAlarmEntity();
        alarmEntity.event_id = EventConstants.EVENT_NOTIFY_RESOURCE_ALARM;
        alarmEntity.agent_id = this.agentId_;

        Body[] resourceAlarmBodys = telegram.getObjBody();

        // DataCollectorから送信された電文から、
        // Dashboardのクライアントに通知するためのイベントを作成する。
        for (Body body : resourceAlarmBodys)
        {
            String objectNameInTelegram = body.getStrObjName();
            String itemNameInTelegram = body.getStrItemName();
            if (TelegramConstants.OBJECTNAME_RESOURCEALARM.equals(objectNameInTelegram) == false)
            {
                continue;
            }
            int loopCount = body.getIntLoopCount();
            Object[] measurementItemValues = body.getObjItemValueArr();
            // 計測IDの項目に対する処理
            if (TelegramConstants.ITEMNAME_MEASUREMENT_TYPE.equals(itemNameInTelegram))
            {
                alarmEntity.measurement_types =
                                                getValuesInTelegramObject(loopCount,
                                                                          measurementItemValues);
            }
            // アラームレベルの項目に対する処理
            else if (TelegramConstants.ITEMNAME_ALARM_LEVEL.equals(itemNameInTelegram))
            {
                alarmEntity.alarm_levels =
                                           getValuesInTelegramObject(loopCount,
                                                                     measurementItemValues);
            }
            // アラーム種類の項目に対する処理
            else if (TelegramConstants.ITEMNAME_ALARM_TYPE.equals(itemNameInTelegram))
            {
                alarmEntity.alarm_types =
                                          getValuesInTelegramObject(loopCount,
                                                                    measurementItemValues);
            }
        }

        // クライアントに対する通知処理
        String message = JSON.encode(alarmEntity);
        EventManager manager = EventManager.getInstance();
        Map<String, ResourceAlarmSetting> clientSettings = manager.getResourceAlarmSettings();
        for (Entry<String, ResourceAlarmSetting> clientEntry : clientSettings.entrySet())
        {
            ResourceAlarmSetting alarmSetting = clientEntry.getValue();
            boolean containAgent = alarmSetting.containAgent(this.agentId_);
            if (containAgent)
            {
                String clientId = clientEntry.getKey();
                this.messageSender_.send(clientId, message);
            }
        }

        return null;
    }

    private int[] getValuesInTelegramObject(int loopCount, Object[] telegramValuesOfobject)
    {
        int[] telegramValues = new int[loopCount];
        for (int cnt = 0; cnt < telegramValuesOfobject.length; cnt++)
        {
            if (cnt >= loopCount)
            {
                break;
            }
            Integer alarmType = (Integer)telegramValuesOfobject[cnt];
            telegramValues[cnt] = alarmType.intValue();
        }
        return telegramValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected byte getByteRequestKind()
    {
        return TelegramConstants.BYTE_REQUEST_KIND_NOTIFY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected byte getByteTelegramKind()
    {
        return TelegramConstants.BYTE_TELEGRAM_RESOURCE_ALARM;
    }

}
