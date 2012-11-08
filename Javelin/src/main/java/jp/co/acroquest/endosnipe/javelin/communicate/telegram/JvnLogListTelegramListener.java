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

import java.io.File;
import java.io.FilenameFilter;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.communicator.TelegramCreator;
import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;

/**
 * JVNログファイル一覧取得
 * 
 * @author eriguchi
 */
public class JvnLogListTelegramListener implements TelegramListener, TelegramConstants
{

    /**
     * {@inheritDoc}
     */
    public Telegram receiveTelegram(final Telegram telegram)
    {
        if (telegram.getObjHeader().getByteTelegramKind() == BYTE_TELEGRAM_KIND_JVN_FILE_LIST
                && telegram.getObjHeader().getByteRequestKind() == BYTE_REQUEST_KIND_REQUEST)
        {
            JavelinConfig javelinConfig = new JavelinConfig();

            File javelinFileDir = new File(javelinConfig.getJavelinFileDir());
            String[] jvnFileNames = javelinFileDir.list(new FilenameFilter() {
                public boolean accept(final File dir, final String name)
                {
                    if (name != null && name.endsWith(".jvn"))
                    {
                        return true;
                    }
                    return false;
                }
            });
            Telegram response = TelegramCreator.createJvnLogListTelegram(jvnFileNames);
            return response;
        }
        return null;
    }

}
