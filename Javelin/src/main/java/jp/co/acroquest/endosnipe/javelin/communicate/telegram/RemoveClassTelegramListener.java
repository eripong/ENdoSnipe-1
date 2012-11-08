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

import jp.co.acroquest.endosnipe.communicator.TelegramCreator;
import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.javelin.MBeanManager;
import jp.co.acroquest.endosnipe.javelin.RootInvocationManager;
import jp.co.acroquest.endosnipe.javelin.bean.Component;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;

/**
 * クラス削除要求電文を処理するクラス。<br />
 *
 * @author sakamoto
 */
public class RemoveClassTelegramListener implements TelegramListener, TelegramConstants
{

    /**
     * {@inheritDoc}
     */
    public Telegram receiveTelegram(Telegram telegram)
    {
        if (telegram.getObjHeader().getByteTelegramKind() == BYTE_TELEGRAM_KIND_REMOVE_CLASS
                && telegram.getObjHeader().getByteRequestKind() == BYTE_REQUEST_KIND_REQUEST)
        {
            Body[] bodies = telegram.getObjBody();
            for (Body body : bodies)
            {
                if (ITEMNAME_CLASSTOREMOVE.equals(body.getStrItemName()))
                {
                    String className = body.getStrObjName();
                    removeClass(className);
                }
            }
            Telegram responseTelegram =
                    TelegramCreator.createEmptyTelegram(BYTE_TELEGRAM_KIND_REMOVE_CLASS,
                                                        BYTE_REQUEST_KIND_RESPONSE);
            return responseTelegram;
        }
        return null;
    }

    /**
     * 指定されたクラスの Invocation を削除します。<br />
     *
     * Invocation がルートで管理されている場合は、それも削除します。<br />
     *
     * @param className 削除するクラス
     */
    private void removeClass(String className)
    {
        Component component = MBeanManager.getComponent(className);
        if (component != null)
        {
            Invocation[] invocations = component.getAllInvocation();
            for (Invocation invocation : invocations)
            {
                RootInvocationManager.removeInvocation(invocation);
            }
            MBeanManager.removeComponent(className);
        }
    }

}
