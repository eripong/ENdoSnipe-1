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

import jp.co.acroquest.endosnipe.javelin.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.javelin.MBeanManager;
import jp.co.acroquest.endosnipe.javelin.bean.Component;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;

/**
 * 状態取得処理クラス。
 * 
 * @author tsukano
 */
public class GetRequestTelegramListener implements TelegramListener, TelegramConstants
{
    /**
     * {@inheritDoc}
     */
    public Telegram receiveTelegram(final Telegram telegram)
    {
        if (telegram.getObjHeader().getByteTelegramKind() == BYTE_TELEGRAM_KIND_GET
                && telegram.getObjHeader().getByteRequestKind() == BYTE_REQUEST_KIND_REQUEST)
        {
            Component[] objComponentArr = MBeanManager.getAllComponents();
            List<Invocation> invocationList = new ArrayList<Invocation>();

            // 電文数を統計する
            for (int i = 0; i < objComponentArr.length; i++)
            {
                invocationList.addAll(Arrays.asList(objComponentArr[i].getAllInvocation()));
            }

            Telegram objTelegram =
                    JavelinTelegramCreator.create(invocationList, BYTE_TELEGRAM_KIND_GET,
                                                  BYTE_REQUEST_KIND_RESPONSE);
            return objTelegram;
        }
        return null;
    }
}
