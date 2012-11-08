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
package jp.co.acroquest.endosnipe.javelin.communicate.telegram;

import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.javelin.common.ConfigUpdater;

/**
 * サーバプロパティ更新要求処理クラス。
 * 
 * @author tsukano
 */
public class UpdateRequestTelegramListener implements TelegramListener, TelegramConstants
{
    /**
     * {@inheritDoc}
     */
    public synchronized Telegram receiveTelegram(final Telegram telegram)
    {
        Header header = telegram.getObjHeader();
        byte property = BYTE_TELEGRAM_KIND_UPDATE_PROPERTY;
        if (header.getByteTelegramKind() == property
                && header.getByteRequestKind() == BYTE_REQUEST_KIND_REQUEST)
        {
            // サーバ設定を更新する
            Body[] bodyList = telegram.getObjBody();
            for (Body body : bodyList)
            {
                String key = body.getStrObjName();
                String value = body.getStrItemName();
                Object[] objItemValueArr = body.getObjItemValueArr();
                try
                {
                    if (objItemValueArr.length >= 1)
                    {
                        Long delay = (Long)objItemValueArr[0];
                        if (delay != null)
                        {
                            ConfigUpdater.updateLater(key, value, delay.longValue());
                        }
                    }
                    else
                    {
                        ConfigUpdater.update(key, value);
                    }
                }
                catch (NumberFormatException nfex)
                {
                    // ユーザが不正な値を入力した場合
                    SystemLogger.getInstance().warn(nfex);
                }
            }
            // 応答電文を作成する
            Telegram response = GetPropertyRequestTelegramListener.createPropertyResponse(
                    header.getId(), property);
            return response;
        }
        return null;
    }

    /**
     * 指定された値が有効な Boolean 値であるかチェックします。
     *
     * @param value 値
     * @return 有効な値の場合は <code>true</code>
     */
    private boolean isValidBooleanString(final String value)
    {
        boolean result = ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value));
        return result;
    }
}
