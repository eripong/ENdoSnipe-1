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
import jp.co.acroquest.endosnipe.communicator.TelegramCreator;
import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.RequestBody;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;

/**
 * JVNログファイルダウンロード。
 * 
 * @author eriguchi
 */
public class JvnLogDownloadTelegramListener implements TelegramListener, TelegramConstants
{

    /**
     * {@inheritDoc}
     */
    public Telegram receiveTelegram(final Telegram telegram)
    {
        if (telegram.getObjHeader().getByteTelegramKind() == BYTE_TELEGRAM_KIND_JVN_FILE
                && telegram.getObjHeader().getByteRequestKind() == BYTE_REQUEST_KIND_REQUEST)
        {
            Body[] bodyArray = telegram.getObjBody();
            if (bodyArray != null)
            {
                long telegramId = telegram.getObjHeader().getId();
                for (Body body : bodyArray)
                {
                    String objectName = body.getStrObjName();
                    String itemName = body.getStrItemName();
                    RequestBody requestBody = (RequestBody)body;
                    if (OBJECTNAME_JVN_FILE.equals(objectName) == true)
                    {
                        Object[] objItemValueArr = requestBody.getObjItemValueArr();
                        if (objItemValueArr == null || objItemValueArr.length == 0)
                        {
                            continue;
                        }

                        if (ITEMNAME_JVN_FILE_NAME.equals(itemName) == true)
                        {
                            Object[] jvnFileNames = objItemValueArr;
                            Telegram response = null;
                            try
                            {
                                response =
                                           TelegramCreator.createJvnLogDownloadTelegram(
                                                                                        BYTE_REQUEST_KIND_RESPONSE,
                                                                                        jvnFileNames,
                                                                                        null,
                                                                                        telegramId);
                            }
                            catch (IllegalArgumentException ex)
                            {
                                SystemLogger logger = SystemLogger.getInstance();
                                logger.warn(ex);
                            }
                            return response;
                        }
                    }
                }
            }
        }

        return null;
    }
}
