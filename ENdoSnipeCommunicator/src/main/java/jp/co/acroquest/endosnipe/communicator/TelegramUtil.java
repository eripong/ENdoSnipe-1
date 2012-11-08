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
package jp.co.acroquest.endosnipe.communicator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import jp.co.acroquest.endosnipe.common.entity.ItemType;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.RequestBody;
import jp.co.acroquest.endosnipe.communicator.entity.ResourceItemConverter;
import jp.co.acroquest.endosnipe.communicator.entity.ResponseBody;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;

/**
 * 電文に関する基本機能を提供するためのユーティリティクラスです。<br />
 * 
 * @author eriguchi
 */
public final class TelegramUtil implements TelegramConstants
{
    private static final ENdoSnipeLogger LOGGER =
            ENdoSnipeLogger.getLogger(TelegramUtil.class,
                                      ENdoSnipeCommunicatorPluginProvider.INSTANCE);

    /** 送信電文の最大長 */
    public static final int TELEGRAM_LENGTH_MAX = 10 * 1024 * 1024;

    /** 受信電文の最大長 */
    public static final int TELEGRAM_READ_LENGTH_MAX = 1 * 1024 * 1024;

    /** shortからbyte配列への変換時に必要なバイト数 */
    private static final int SHORT_BYTE_SWITCH_LENGTH = 2;

    /** intからbyte配列への変換時に必要なバイト数 */
    private static final int INT_BYTE_SWITCH_LENGTH = 4;

    /** longからbyte配列への変換時に必要なバイト数 */
    private static final int LONG_BYTE_SWITCH_LENGTH = 8;

    /** floatからbyte配列への変換時に必要なバイト数 */
    private static final int FLOAT_BYTE_SWITCH_LENGTH = 4;

    /** doubleからbyte配列への変換時に必要なバイト数 */
    private static final int DOUBLE_BYTE_SWITCH_LENGTH = 8;

    /** ヘッダの長さ */
    public static final int TELEGRAM_HEADER_LENGTH = 18;

    /** 改行文字。 */
    public static final String NEW_LINE = System.getProperty("line.separator");

    /** 文字列の文字コード */
    private static final String CHAR_CODE = "UTF-8";

    private static final long TELEGRAM_ID_START = 1;

    /** 電文 ID */
    private static final AtomicLong TELEGRAM_ID = new AtomicLong(TELEGRAM_ID_START);

    private TelegramUtil()
    {
        // Do nothing.
    }

    /**
     * 電文 ID を生成します。
     *
     * @return 電文 ID
     */
    public static long generateTelegramId()
    {
        // 電文 ID が最大値に達した場合は、値を戻す
        TELEGRAM_ID.compareAndSet(Long.MAX_VALUE, TELEGRAM_ID_START);
        return TELEGRAM_ID.getAndIncrement();
    }

    /**
     * 文字列をバイト配列（４バイト文字列長＋UTF8）に変換します。<br />
     *
     * @param text 文字列
     * @return バイト配列
     */
    public static byte[] stringToByteArray(final String text)
    {
        byte[] textArray = new byte[0];
        try
        {
            textArray = text.getBytes(CHAR_CODE);
        }
        catch (UnsupportedEncodingException ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
        byte[] lengthArray = intToByteArray(textArray.length);

        // 文字列長と文字列を結合する
        return combineTwoByteArray(lengthArray, textArray);
    }

    /**
     * 4バイトの文字列長＋UTF8のバイト配列から文字列を作成します。<br />
     *
     * @param buffer バイト配列
     * @return 文字列
     */
    public static String byteArrayToString(final ByteBuffer buffer)
    {
        String strResult = "";

        // 文字列長を取得する
        int intbyteArrLength = buffer.getInt();

        if (intbyteArrLength > TELEGRAM_READ_LENGTH_MAX)
        {
            intbyteArrLength = TELEGRAM_READ_LENGTH_MAX;
            LOGGER.log("WECC0101", intbyteArrLength);
        }

        try
        {
            byte[] byteSoruceArr = new byte[intbyteArrLength];
            buffer.get(byteSoruceArr);

            strResult = new String(byteSoruceArr, 0, intbyteArrLength, CHAR_CODE);
        }
        catch (UnsupportedEncodingException uee)
        // CHECKSTYLE:OFF
        {
            // Do nothing.
        }
        // CHECKSTYLE:ON
        catch (Throwable th)
        {
            LOGGER.log("WECC0102", th);
        }

        return strResult;
    }

    /**
     * ２バイト符号付整数をバイト配列に変換します。<br />
     *
     * @param value 値
     * @return バイト配列
     */
    public static byte[] shortToByteArray(final short value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(SHORT_BYTE_SWITCH_LENGTH);
        buffer.putShort(value);
        return buffer.array();
    }

    /**
     * ４バイト符号付整数をバイト配列に変換します。<br />
     *
     * @param value 値
     * @return バイト配列
     */
    public static byte[] intToByteArray(final int value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(INT_BYTE_SWITCH_LENGTH);
        buffer.putInt(value);
        return buffer.array();
    }

    /**
     * ８バイト符号付整数をバイト配列に変換します。<br />
     *
     * @param value 値
     * @return バイト配列
     */
    public static byte[] longToByteArray(final long value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(LONG_BYTE_SWITCH_LENGTH);
        buffer.putLong(value);
        return buffer.array();
    }

    /**
     * ４バイト符号付小数をバイト配列に変換します。<br />
     *
     * @param value 値
     * @return バイト配列
     */
    public static byte[] floatToByteArray(final float value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(FLOAT_BYTE_SWITCH_LENGTH);
        buffer.putFloat(value);
        return buffer.array();
    }

    /**
     * ８バイト符号付小数をバイト配列に変換します。<br />
     *
     * @param value 値
     * @return バイト配列
     */
    public static byte[] doubleToByteArray(final double value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(DOUBLE_BYTE_SWITCH_LENGTH);
        buffer.putDouble(value);
        return buffer.array();
    }

    /**
     * 電文オブジェクトをバイト配列に変換します。<br />
     *
     * @param objTelegram 電文オブジェクト
     * @return バイト配列
     */
    public static List<byte[]> createTelegram(final Telegram objTelegram)
    {
        Header header = objTelegram.getObjHeader();
        
        List<byte[]> telegramList = new ArrayList<byte[]>();

        // 本体データを作る
        ByteArrayOutputStream byteArrayOutputStream = newByteStream(header);

        if (objTelegram.getObjBody() != null)
        {
            for (Body body : objTelegram.getObjBody())
            {
                //サイズを超えた場合には、そこまでで一旦送信する。
                if (byteArrayOutputStream.size() > TELEGRAM_LENGTH_MAX)
                {
                    byte[] bytesBody = byteArrayOutputStream.toByteArray();
                    int telegramLength = bytesBody.length;
                    ByteBuffer outputBuffer = ByteBuffer.wrap(bytesBody);

                    // ヘッダを変換する
                    outputBuffer.rewind();
                    outputBuffer.putInt(telegramLength);
                    telegramList.add(outputBuffer.array());
                    
                    // 新しい電文として、ヘッダを追加する。
                    byteArrayOutputStream = newByteStream(header);
                }

                try
                {
                    byte[] bytesObjName = stringToByteArray(body.getStrObjName());
                    byte[] bytesItemName = stringToByteArray(body.getStrItemName());
                    byte[] bytesObjDispName = stringToByteArray(body.getStrObjDispName());
                    // 本体データに設定する
                    byteArrayOutputStream.write(bytesObjName);
                    byteArrayOutputStream.write(bytesItemName);
                    byteArrayOutputStream.write(bytesObjDispName);

                    Body responseBody = body;
                    ItemType itemType = responseBody.getByteItemMode();
                    int loopCount = responseBody.getIntLoopCount();
                    byte[] itemModeArray = new byte[]{ItemType.getItemTypeNumber(itemType)};
                    byte[] loopCountArray = intToByteArray(loopCount);
                    byteArrayOutputStream.write(itemModeArray);
                    byteArrayOutputStream.write(loopCountArray);

                    for (int index = 0; index < loopCount; index++)
                    {
                        Object obj = responseBody.getObjItemValueArr()[index];
                        byte[] value = null;
                        switch (itemType)
                        {
                        case ITEMTYPE_BYTE:
                            value = new byte[]{((Number)obj).byteValue()};
                            break;
                        case ITEMTYPE_SHORT:
                            value = shortToByteArray(((Number)obj).shortValue());
                            break;
                        case ITEMTYPE_INT:
                            value = intToByteArray(((Number)obj).intValue());
                            break;
                        case ITEMTYPE_LONG:
                            value = longToByteArray(((Number)obj).longValue());
                            break;
                        case ITEMTYPE_FLOAT:
                            value = floatToByteArray(((Number)obj).floatValue());
                            break;
                        case ITEMTYPE_DOUBLE:
                            value = doubleToByteArray(((Number)obj).doubleValue());
                            break;
                        case ITEMTYPE_STRING:
                            value = stringToByteArray((String)obj);
                            break;
                        case ITEMTYPE_JMX:
                            value = stringToByteArray((String)obj);
                            break;
                        default:
                            return null;
                        }
                        byteArrayOutputStream.write(value);
                    }
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    continue;
                }
            }
        }

        byte[] bytesBody = byteArrayOutputStream.toByteArray();
        int telegramLength = bytesBody.length;
        ByteBuffer outputBuffer = ByteBuffer.wrap(bytesBody);

        // ヘッダを変換する
        outputBuffer.rewind();
        outputBuffer.putInt(telegramLength);
        outputBuffer.position(INT_BYTE_SWITCH_LENGTH + LONG_BYTE_SWITCH_LENGTH);
        outputBuffer.put(FINAL_TELEGRAM);
        return Arrays.asList(new byte[][] {outputBuffer.array()});
    }

    private static ByteArrayOutputStream newByteStream(Header header)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteBuffer headerBuffer = initHeaderByteBuffer(header, HALFWAY_TELEGRAM);
        try
        {
            byteArrayOutputStream.write(headerBuffer.array());
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        return byteArrayOutputStream;
    }

    private static ByteBuffer initHeaderByteBuffer(Header header, byte finalTelegram)
    {
        ByteBuffer headerBuffer = ByteBuffer.allocate(TELEGRAM_HEADER_LENGTH);
        headerBuffer.putInt(0); // 電文長は後で入れる。
        headerBuffer.putLong(header.getId());
        headerBuffer.put(finalTelegram);
        headerBuffer.put(header.getByteTelegramKind());
        headerBuffer.put(header.getByteRequestKind());
        return headerBuffer;
    }

    /**
     * バイト配列を電文オブジェクトに変換します。<br />
     *
     * @param byteTelegramArr バイト配列
     * @return 電文オブジェクト
     */
    public static Telegram recoveryTelegram(final byte[] byteTelegramArr)
    {
        // 返却する用
        Telegram objTelegram = new Telegram();

        if (byteTelegramArr == null)
        {
            return null;
        }

        ByteBuffer telegramBuffer = ByteBuffer.wrap(byteTelegramArr);

        // まず、Header分を取得する
        Header objHeader = new Header();
        objHeader.setIntSize(telegramBuffer.getInt());
        objHeader.setId(telegramBuffer.getLong());
        objHeader.setLastTelegram(telegramBuffer.get());
        objHeader.setByteTelegramKind(telegramBuffer.get());
        objHeader.setByteRequestKind(telegramBuffer.get());
        telegramBuffer.get();
        telegramBuffer.get();
        telegramBuffer.get();

        byte kind = objHeader.getByteRequestKind();
        boolean isResponseBody =
                (kind == BYTE_REQUEST_KIND_RESPONSE || kind == BYTE_REQUEST_KIND_NOTIFY);

        List<Body> bodyList = new ArrayList<Body>();

        // 本体を取得する
        while (telegramBuffer.remaining() > 0)
        {
            Body body;
            String objectName = byteArrayToString(telegramBuffer);
            String itemName = byteArrayToString(telegramBuffer);
            String objDispName = byteArrayToString(telegramBuffer);

            if (isResponseBody)
            {
                body = new ResponseBody();
            }
            else
            {
                body = new RequestBody();
            }

            // 項目型設定
            body.setByteItemMode(ItemType.getItemType(telegramBuffer.get()));

            // 繰り返し回数設定
            body.setIntLoopCount(telegramBuffer.getInt());

            // 値設定
            Object[] values = new Object[body.getIntLoopCount()];
            for (int index = 0; index < values.length; index++)
            {
                switch (body.getByteItemMode())
                {
                case ITEMTYPE_BYTE:
                    values[index] = telegramBuffer.get();
                    break;
                case ITEMTYPE_SHORT:
                    values[index] = telegramBuffer.getShort();
                    break;
                case ITEMTYPE_INT:
                    values[index] = telegramBuffer.getInt();
                    break;
                case ITEMTYPE_LONG:
                    values[index] = telegramBuffer.getLong();
                    break;
                case ITEMTYPE_FLOAT:
                    values[index] = telegramBuffer.getFloat();
                    break;
                case ITEMTYPE_DOUBLE:
                    values[index] = telegramBuffer.getDouble();
                    break;
                case ITEMTYPE_STRING:
                    values[index] = byteArrayToString(telegramBuffer);
                    break;
                case ITEMTYPE_JMX:
                    String jsonStr = byteArrayToString(telegramBuffer);
                    try
                    {
                        values[index] = ResourceItemConverter.getInstance().decodeFromJSON(jsonStr);
                    }
                    catch (Exception e)
                    {
                        SystemLogger.getInstance().warn(e);
                    }
                    break;
                default:
                    return null;
                }
            }
            body.setObjItemValueArr(values);

            body.setStrObjName(objectName);
            body.setStrItemName(itemName);
            body.setStrObjDispName(objDispName);
            bodyList.add(body);
        }

        // 本体リストを作る
        Body[] objBodyArr;
        if (isResponseBody)
        {
            objBodyArr = bodyList.toArray(new ResponseBody[bodyList.size()]);
        }
        else
        {
            objBodyArr = bodyList.toArray(new Body[bodyList.size()]);
        }

        // 回復したヘッダと本体を電文に設定する
        objTelegram.setObjHeader(objHeader);
        objTelegram.setObjBody(objBodyArr);

        return objTelegram;
    }

    /**
     * 指定された種類の電文を作成します。<br />
     *
     * @param telegramKind 電文種別
     * @param requestKind 要求応答種別
     * @param objectName オブジェクト名
     * @param itemName 項目名
     * @param itemType 項目型
     * @param value 値
     * @return 電文
     */
    public static Telegram createSingleTelegram(final byte telegramKind, final byte requestKind,
            final String objectName, final String itemName, final ItemType itemType, final Object value)
    {
        Header header = new Header();
        header.setByteTelegramKind(telegramKind);
        header.setByteRequestKind(requestKind);

        ResponseBody responseBody = createSingleResponseBody(objectName, itemName, itemType, value);

        Telegram telegram = new Telegram();
        telegram.setObjHeader(header);
        telegram.setObjBody(new Body[]{responseBody});
        return telegram;
    }

    /**
     * ダンプ取得要求の電文本体を作成します。
     * 
     * @param objName オブジェクト名
     * @param itemName 項目名
     * @return ダンプ取得要求の電文本体
     */
    public static Body[] createEmptyRequestBody(final String objName, final String itemName)
    {
        Body[] bodies = new Body[1];
        Body objBody = new Body();
        objBody.setStrObjName(objName);
        objBody.setStrItemName(itemName);
        objBody.setIntLoopCount(0);
        objBody.setByteItemMode(ItemType.ITEMTYPE_BYTE);
        objBody.setObjItemValueArr(new Object[0]);
        bodies[0] = objBody;
        return bodies;
    }

    /**
     * 指定された種類の応答を作成します。<br />
     *
     * @param objectName オブジェクト名
     * @param itemName 項目名
     * @param itemType 項目型
     * @param value 値
     * @return 応答
     */
    public static ResponseBody createSingleResponseBody(final String objectName,
            final String itemName, final ItemType itemType, final Object value)
    {
        ResponseBody responseBody = new ResponseBody();
        responseBody.setStrObjName(objectName);
        responseBody.setStrItemName(itemName);
        responseBody.setByteItemMode(itemType);
        Object[] itemValues;
        if (value instanceof List)
        {
            List<?> valueList = (List<?>)value;
            itemValues = new Object[valueList.size()];
            for (int index = 0; index < valueList.size(); index++)
            {
                itemValues[index] = valueList.get(index);
            }
        }
        else
        {
            itemValues = new Object[]{value};
        }
        responseBody.setIntLoopCount(itemValues.length);
        responseBody.setObjItemValueArr(itemValues);
        return responseBody;
    }

    /**
     * ２つのバイト配列を結合します。<br />
     *
     * @param bytesFirst 最初のバイト配列
     * @param bytesSecond 後ろにつなげるバイト配列
     * @return ２つのバイト配列をつなげたバイトは零つ
     */
    private static byte[] combineTwoByteArray(final byte[] bytesFirst, final byte[] bytesSecond)
    {
        // 返却用
        byte[] bytesResult = null;

        int byteBeforeArrLength = 0;
        int byteAfterArrLength = 0;

        // 前分 byte[] のサイズを取得
        if (bytesFirst != null)
        {
            byteBeforeArrLength = bytesFirst.length;
        }

        // 後分 byte[] のサイズを取得
        if (bytesSecond != null)
        {
            byteAfterArrLength = bytesSecond.length;
        }

        // 返却用 byte[] を作る
        if (byteBeforeArrLength + byteAfterArrLength > 0)
        {
            bytesResult = new byte[byteBeforeArrLength + byteAfterArrLength];
        }

        // 前分 byte[] を返却用 byte[] に設定する
        if (byteBeforeArrLength > 0)
        {
            System.arraycopy(bytesFirst, 0, bytesResult, 0, byteBeforeArrLength);
        }

        // 後分 byte[] を返却用 byte[] に設定する
        if (byteAfterArrLength > 0)
        {
            System.arraycopy(bytesSecond, 0, bytesResult, byteBeforeArrLength, byteAfterArrLength);
        }

        // 返却する
        return bytesResult;
    }

    /**
     * ReponseBodyを作成します。<br />
     * 
     * @param objName オブジェクト名
     * @param itemName 項目名
     * @param itemMode 項目型
     * @param objItemValueArr 項目内の配列
     * @return ReponseBody
     */
    public static ResponseBody createResponseBody(final String objName, final String itemName,
            final ItemType itemMode, final Object[] objItemValueArr)
    {
        ResponseBody body = new ResponseBody();
        body.setStrObjName(objName);
        body.setStrItemName(itemName);
        body.setByteItemMode(itemMode);
        body.setIntLoopCount(objItemValueArr.length);
        body.setObjItemValueArr(objItemValueArr);

        return body;
    }

    /**
     * 電文内容をシステムログに出力します。<br />
     * 
     * @param telegram 出力対象電文
     * @param length 出力対象電文長
     * @return ログ出力文字列
     */
    public static String toPrintStr(final Telegram telegram, final int length)
    {
        StringBuffer receivedBuffer = new StringBuffer();

        Header header = telegram.getObjHeader();
        byte telegramKind = header.getByteTelegramKind();
        byte requestKind = header.getByteRequestKind();

        receivedBuffer.append(NEW_LINE);
        receivedBuffer.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        receivedBuffer.append(NEW_LINE);

        receivedBuffer.append("電文種別      :[" + telegramKind + "]");
        receivedBuffer.append(NEW_LINE);
        receivedBuffer.append("要求応答種別  :[" + requestKind + "]");
        receivedBuffer.append(NEW_LINE);
        receivedBuffer.append("電文長        :[" + length + "]");
        receivedBuffer.append(NEW_LINE);

        Body[] objBody = telegram.getObjBody();

        receivedBuffer.append("オブジェクト名\t項目名\t項目型\t繰り返し回数\t項目値");
        receivedBuffer.append(NEW_LINE);
        for (Body body : objBody)
        {
            String objName = body.getStrObjName();
            String itemName = body.getStrItemName();
            String itemMode = "";
            String loopCount = "";
            StringBuffer itemValue = new StringBuffer();

            if (body instanceof ResponseBody)
            {
                ResponseBody responseBody = (ResponseBody)body;
                itemMode = "[" + responseBody.getByteItemMode() + "]";
                loopCount = "[" + responseBody.getIntLoopCount() + "]";

                Object[] objArr = responseBody.getObjItemValueArr();
                for (Object obj : objArr)
                {
                    itemValue.append("[" + obj + "]");
                }
            }
            receivedBuffer.append(objName);
            receivedBuffer.append("\t");
            receivedBuffer.append(itemName);
            receivedBuffer.append("\t");
            receivedBuffer.append(itemMode);
            receivedBuffer.append("\t");
            receivedBuffer.append(loopCount);
            receivedBuffer.append("\t");
            receivedBuffer.append(itemValue);
            receivedBuffer.append(NEW_LINE);
        }

        receivedBuffer.append("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        receivedBuffer.append(NEW_LINE);

        String receivedStr = receivedBuffer.toString();

        return receivedStr;
    }
}
