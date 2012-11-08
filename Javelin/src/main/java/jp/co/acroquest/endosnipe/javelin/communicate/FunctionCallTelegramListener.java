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
package jp.co.acroquest.endosnipe.javelin.communicate;

import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;

/**
 * 機能呼び出し要求電文受信処理を行います。<br />
 *
 * @author sakamoto
 */
public class FunctionCallTelegramListener implements TelegramListener, TelegramConstants
{

    /**
     * 機能呼び出し要求電文を受け取ったときのみ処理を行います。<br />
     * 
     * @param telegram 電文オブジェクト
     * @return 常に <code>null</code>
     */
    public Telegram receiveTelegram(final Telegram telegram)
    {
        Header header = telegram.getObjHeader();
        if (header.getByteTelegramKind() == BYTE_TELEGRAM_KIND_FUNCTIONCALL
                && header.getByteRequestKind() == BYTE_REQUEST_KIND_REQUEST)
        {
            Body[] bodies = telegram.getObjBody();
            if (bodies != null && bodies.length >= 1)
            {
                sortByFunction(bodies);
            }
        }
        return null;
    }

    /**
     * 要求電文により、機能を呼び分けます。<br />
     *
     * @param bodies 電文本体
     */
    private void sortByFunction(final Body[] bodies)
    {
        Body body = bodies[0];
        String objectName = body.getStrObjName();
        if (OBJECTNAME_FORCEFULLGC.equals(objectName))
        {
            String itemName = body.getStrItemName();
            if (ITEMNAME_FORCEFULLGC.equals(itemName))
            {
                System.gc();
            }
        }
    }

}
