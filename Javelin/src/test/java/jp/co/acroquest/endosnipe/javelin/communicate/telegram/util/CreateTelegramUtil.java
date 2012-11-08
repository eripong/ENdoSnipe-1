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
package jp.co.acroquest.endosnipe.javelin.communicate.telegram.util;

import jp.co.acroquest.endosnipe.common.entity.ItemType;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.RequestBody;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;

/**
 * 電文作成に関するユーティリティクラス
 * @author fujii
 *
 */
public class CreateTelegramUtil
{
    /**
     * 電文を作成する。
     * @param header ヘッダ
     * @param body Body
     * @return 電文
     */
    public static Telegram createTelegram(final Header header, final Body[] body)
    {
        Telegram telegram = new Telegram();
        telegram.setObjHeader(header);
        telegram.setObjBody(body);
        return telegram;

    }

    /**
     * ヘッダを作成する。
     * @param requestKind 電文応答種別
     * @param telegramKind 電文種別
     * @return
     */
    public static Header createHeader(final byte requestKind, final byte telegramKind)
    {
        Header header = new Header();
        header.setByteRequestKind(requestKind);
        header.setByteTelegramKind(telegramKind);

        return header;
    }

    /**
     * 電文のBodyを作成する。
     * @param objectName オブジェクト名
     * @param itemName アイテム名
     * @param itemType 型
     * @param loopCount アイテム数
     * @param objArr 計測値
     * 
     * @return Body 電文のBody
     */
    public static RequestBody createBodyValue(final String objectName, final String itemName,
            final ItemType itemType, final int loopCount, final Object[] objArr)
    {
        RequestBody body = new RequestBody();
        body.setStrObjName(objectName);
        body.setStrItemName(itemName);
        body.setByteItemMode(itemType);
        body.setIntLoopCount(loopCount);
        body.setObjItemValueArr(objArr);

        return body;
    }
}
