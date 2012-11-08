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

import jp.co.acroquest.endosnipe.javelin.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.ResponseBody;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.javelin.common.ConfigUpdater;

/**
 * サーバプロパティ取得要求処理クラス。
 * 
 * @author tsukano
 */
public class GetPropertyRequestTelegramListener implements TelegramListener, TelegramConstants
{
    /**
     * {@inheritDoc}
     */
    public Telegram receiveTelegram(final Telegram telegram)
    {
        Header header = telegram.getObjHeader();
        if (header.getByteTelegramKind() == BYTE_TELEGRAM_KIND_GET_PROPERTY
                && header.getByteRequestKind() == BYTE_REQUEST_KIND_REQUEST)
        {
            long telegramId = header.getId();
            Telegram response = createPropertyResponse(telegramId, BYTE_TELEGRAM_KIND_GET_PROPERTY);
            return response;
        }
        return null;
    }

    /**
     * 指定された電文種別を用いて、本体に更新可能な設定値一覧を持つ応答電文を作成する
     *
     * @param telegramId 電文 ID
     * @param telegramKind 電文種別
     * @return 指定された電文種別に対応し、本体に更新可能な設定値一覧を持つ応答電文
     */
    public static Telegram createPropertyResponse(final long telegramId, final byte telegramKind)
    {
        // 応答電文
        Telegram telegram = new Telegram();

        // 応答電文のヘッダを作成する
        Header header = new Header();
        header.setId(telegramId);
        header.setByteTelegramKind(telegramKind);
        header.setByteRequestKind(BYTE_REQUEST_KIND_RESPONSE);
        telegram.setObjHeader(header);

        // 応答電文に入れるボディのリストを作成する
        List<Body> list = new ArrayList<Body>();
        Map<String, String> map = ConfigUpdater.getUpdatableConfig();
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries)
        {
            String key = entry.getKey();
            String value = entry.getValue();
            ResponseBody body = new ResponseBody();
            body.setStrObjName(key);
            body.setStrItemName(value);
            body.setObjItemValueArr(new Object[0]);

            list.add(body);
        }
        Body[] bodyList = list.toArray(new Body[list.size()]);
        telegram.setObjBody(bodyList);

        return telegram;
    }
}
