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
package jp.co.acroquest.endosnipe.communicator.accessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.co.acroquest.endosnipe.common.entity.ItemType;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.ConnectNotifyData;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.ResponseBody;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;

/**
 * Javelin ログ通知電文のためのアクセサクラスです。<br />
 * 
 * @author y-komori
 */
public class ConnectNotifyAccessor
{
    private ConnectNotifyAccessor()
    {

    }

    /**
     * 接続情報通知電文から内容を取り出します。<br />
     * 電文種別が接続情報通知通知電文ではない場合や、内容が不正である場合は
     * <code>null</code> を返します。<br />
     * 
     * @param telegram Javelin 接続情報通知電文
     * @return 電文内容
     */
    public static ConnectNotifyData getConnectNotifyData(final Telegram telegram)
    {
        if (!isConnectNotifyTelegram(telegram))
        {
            return null;
        }

        ConnectNotifyData data = new ConnectNotifyData();

        Body[] bodies = telegram.getObjBody();
        for (Body body : bodies)
        {
            String objectName = body.getStrObjName();
            if (objectName == null)
            {
                continue;
            }

            if (objectName.equals(TelegramConstants.OBJECTNAME_CONNECTINFO))
            {
                getFromConnectInfoObject(body, data);
            }
        }

        if (data.getKind() < 0)
        {
            return null;
        }
        if (data.getDbName() == null || data.getDbName().length() == 0)
        {
            return null;
        }

        return data;
    }

    /**
     * 接続情報通知電文から内容を取り出します。<br />
     * 電文種別が接続情報通知通知電文ではない場合や、内容が不正である場合は
     * <code>null</code> を返します。<br />
     * 
     * @param telegram Javelin 接続情報通知電文
     * @return 電文内容
     */
    public static Set<String> getDataBaseNameList(final Telegram telegram)
    {
        if (!isDatabaseNameTelegram(telegram))
        {
            return null;
        }

        Set<String> databaseNameList = new HashSet<String>();

        Body[] bodies = telegram.getObjBody();
        for (Body body : bodies)
        {
            String objectName = body.getStrObjName();
            if (objectName == null)
            {
                continue;
            }

            if (objectName.equals(TelegramConstants.OBJECTnAME_DATABASE_NAME))
            {
                String databaseName = getFromDatabaseNameObject(body);
                if (databaseName != null)
                {
                    databaseNameList.add(databaseName);
                }
            }
        }

        return databaseNameList;
    }

    /**
     * 接続情報通知電文を作成します。
     * @param data 電文に設定する値
     * @return 接続情報通知電文を返します。
     */
    public static final Telegram createTelegram(ConnectNotifyData data)
    {
        Telegram telegram = new Telegram();
        telegram.setObjHeader(createHeader());
        telegram.setObjBody(createBodys(data));

        return telegram;
    }

    /**
     * 接続DB名増加通知電文を作成します。
     * @param databaseNameList 電文に設定する値
     * @return 接続DB通知電文を返します。
     */
    public static final Telegram createAddDatabaseNameTelegram(Set<String> databaseNameList)
    {
        Telegram telegram = new Telegram();
        telegram.setObjHeader(createAddDatabaseNameHeader());
        telegram.setObjBody(createDatabaseNameBodys(databaseNameList));

        return telegram;
    }

    /**
     * 接続DB名減少通知電文を作成します。
     * @param databaseNameList 電文に設定する値
     * @return 接続DB通知電文を返します。
     */
    public static final Telegram createDelDatabaseNameTelegram(Set<String> databaseNameList)
    {
        Telegram telegram = new Telegram();
        telegram.setObjHeader(createDelDatabaseNameHeader());
        telegram.setObjBody(createDatabaseNameBodys(databaseNameList));

        return telegram;
    }

    /**
     * オブジェクト名が{@link TelegramConstants#OBJECTNAME_CONNECTINFO}である
     * {@link Body}から、{@link ConnectNotifyData}の値を取得する。
     * @param body 取得元
     * @param toData 取得した値の設定先
     */
    private static void getFromConnectInfoObject(Body body, ConnectNotifyData toData)
    {
        String itemName = body.getStrItemName();
        if (itemName == null)
        {
            return;
        }

        Object[] objItemValueArr = body.getObjItemValueArr();
        if (objItemValueArr.length == 0)
        {
            return;
        }

        if (itemName.equals(TelegramConstants.ITEMNAME_CONNECTNOTIFY_KIND))
        {
            toData.setKind((Integer)objItemValueArr[0]);
        }
        else if (itemName.equals(TelegramConstants.ITEMNAME_CONNECTNOTIFY_DBNAME))
        {
            toData.setDbName((String)objItemValueArr[0]);
        }
        else if (itemName.equals(TelegramConstants.ITEMNAME_CONNECTNOTIFY_PURPOSE))
        {
            toData.setPurpose((Integer)objItemValueArr[0]);
        }
    }

    /**
     * オブジェクト名が{@link TelegramConstants#OBJECTnAME_DATABASE_NAME}である
     * {@link Body}から、DB名の一覧を取得する。
     * @param body 取得元
     * @return DB名の一覧
     */
    private static String getFromDatabaseNameObject(Body body)
    {
        Set<String> databaseList = new HashSet<String>();
        String itemName = body.getStrItemName();
        String databaseName = null;
        if (itemName == null)
        {
            return databaseName;
        }

        Object[] objItemValueArr = body.getObjItemValueArr();
        if (objItemValueArr.length == 0)
        {
            return databaseName;
        }

        if (itemName.equals(TelegramConstants.ITEMNAME_CONNECTNOTIFY_DBNAME))
        {
            return (String)objItemValueArr[0];
        }
        //        else if (itemName.equals(TelegramConstants.ITEMNAME_CONNECTNOTIFY_DBNAME))
        //        {
        //            toData.setDbName((String)objItemValueArr[0]);
        //        }
        //        else if (itemName.equals(TelegramConstants.ITEMNAME_CONNECTNOTIFY_PURPOSE))
        //        {
        //            toData.setPurpose((Integer)objItemValueArr[0]);
        //        }

        return databaseName;
    }

    /**
     * 接続情報通知電文のヘッダを作成します。
     * @return 接続情報通知電文のヘッダを返します。
     */
    private static Header createHeader()
    {
        Header header = new Header();
        header.setByteTelegramKind(TelegramConstants.BYTE_TELEGRAM_KIND_CONNECT_NOTIFY);
        header.setByteRequestKind(TelegramConstants.BYTE_REQUEST_KIND_NOTIFY);
        return header;
    }

    /**
     * 接続情報通知電文のボディを作成します。
     * @param data ボディに設定する値
     * @return 接続情報通知電文のボディを返します。
     */
    private static Body[] createBodys(ConnectNotifyData data)
    {
        List<Body> bodyList = new ArrayList<Body>();

        Body bodyKind = new ResponseBody();
        bodyKind.setStrObjName(TelegramConstants.OBJECTNAME_CONNECTINFO);
        bodyKind.setStrItemName(TelegramConstants.ITEMNAME_CONNECTNOTIFY_KIND);
        bodyKind.setIntLoopCount(1);
        bodyKind.setByteItemMode(ItemType.ITEMTYPE_INT);
        bodyKind.setObjItemValueArr(new Object[]{data.getKind()});
        bodyList.add(bodyKind);

        Body bodyDbName = new ResponseBody();
        bodyDbName.setStrObjName(TelegramConstants.OBJECTNAME_CONNECTINFO);
        bodyDbName.setStrItemName(TelegramConstants.ITEMNAME_CONNECTNOTIFY_DBNAME);
        bodyDbName.setIntLoopCount(1);
        bodyDbName.setByteItemMode(ItemType.ITEMTYPE_STRING);
        bodyDbName.setObjItemValueArr(new Object[]{data.getDbName()});
        bodyList.add(bodyDbName);

        Body bodypurpose = new ResponseBody();
        bodypurpose.setStrObjName(TelegramConstants.OBJECTNAME_CONNECTINFO);
        bodypurpose.setStrItemName(TelegramConstants.ITEMNAME_CONNECTNOTIFY_PURPOSE);
        bodypurpose.setIntLoopCount(1);
        bodypurpose.setByteItemMode(ItemType.ITEMTYPE_INT);
        bodypurpose.setObjItemValueArr(new Object[]{data.getPurpose()});
        bodyList.add(bodypurpose);

        return bodyList.toArray(new Body[bodyList.size()]);
    }

    /**
     * 接続DB名情報増加通知電文のヘッダを作成します。
     * @return 接続DB名情報通知電文のヘッダを返します。
     */
    private static Header createAddDatabaseNameHeader()
    {
        Header header = new Header();
        header.setByteTelegramKind(TelegramConstants.BYTE_TELEGRAM_KIND_ADD_DATABASE_NAME);
        header.setByteRequestKind(TelegramConstants.BYTE_REQUEST_KIND_NOTIFY);
        return header;
    }

    /**
     * 接続DB名情報通知減少電文のヘッダを作成します。
     * @return 接続DB名情報通知電文のヘッダを返します。
     */
    private static Header createDelDatabaseNameHeader()
    {
        Header header = new Header();
        header.setByteTelegramKind(TelegramConstants.BYTE_TELEGRAM_KIND_DEL_DATABASE_NAME);
        header.setByteRequestKind(TelegramConstants.BYTE_REQUEST_KIND_NOTIFY);
        return header;
    }

    /**
     * 接続DB名情報通知電文のボディを作成します。
     * @param data ボディに設定する値
     * @return 接続DB名情報通知電文のボディを返します。
     */
    private static Body[] createDatabaseNameBodys(Set<String> databaseNameList)
    {
        List<Body> bodyList = new ArrayList<Body>();

        for (String databaseName : databaseNameList)
        {
            Body bodyDBName = new ResponseBody();
            Object[] valueArr = new Object[]{databaseName};
            bodyDBName.setStrObjName(TelegramConstants.OBJECTnAME_DATABASE_NAME);
            bodyDBName.setStrItemName(TelegramConstants.ITEMNAME_CONNECTNOTIFY_DBNAME);
            bodyDBName.setIntLoopCount(valueArr.length);
            bodyDBName.setByteItemMode(ItemType.ITEMTYPE_STRING);
            bodyDBName.setObjItemValueArr(valueArr);
            bodyList.add(bodyDBName);
        }

        return bodyList.toArray(new Body[bodyList.size()]);
    }

    /**
     * 電文が接続情報通知であるかを確認します。
     * @param telegram 確認対象の電文
     * @return 指定された電文が接続通知情報であれば<code>true</code>を返します。
     */
    private static boolean isConnectNotifyTelegram(final Telegram telegram)
    {
        Header header = telegram.getObjHeader();
        if (header.getByteTelegramKind() == TelegramConstants.BYTE_TELEGRAM_KIND_CONNECT_NOTIFY)
        {
            return true;
        }
        return false;
    }

    /**
     * 電文がDB名の増減情報通知であるかを確認します。
     * @param telegram 確認対象の電文
     * @return 指定された電文がDB名の増減通知情報であれば<code>true</code>を返します。
     */
    private static boolean isDatabaseNameTelegram(final Telegram telegram)
    {
        Header header = telegram.getObjHeader();
        if (header.getByteTelegramKind() == TelegramConstants.BYTE_TELEGRAM_KIND_ADD_DATABASE_NAME
                || header.getByteTelegramKind() == TelegramConstants.BYTE_TELEGRAM_KIND_DEL_DATABASE_NAME)
        {
            return true;
        }
        return false;
    }

}
