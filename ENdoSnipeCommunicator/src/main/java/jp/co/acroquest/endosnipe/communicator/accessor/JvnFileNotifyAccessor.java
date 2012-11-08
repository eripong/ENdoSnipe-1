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
import java.util.List;

import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.ResponseBody;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;

/**
 * Javelin ログ通知電文のためのアクセサクラスです。<br />
 * 
 * @author y-komori
 */
public class JvnFileNotifyAccessor implements TelegramConstants
{
    private JvnFileNotifyAccessor()
    {

    }

    /**
     * Javelin ログ通知電文から内容を取り出します。<br />
     * 電文種別がログ通知電文ではない場合や、内容が防いである場合は <code>null</code> を返します。<br />
     * 
     * @param telegram Javelin ログ通知電文
     * @return 電文内容
     */
    public static JvnFileEntry[] getJvnFileEntries(final Telegram telegram)
    {
        if (checkTelegram(telegram) == false)
        {
            return null;
        }

        Body[] bodies = telegram.getObjBody();
        List<Object> jvnFileNames = new ArrayList<Object>(bodies.length);
        List<Object> jvnFileContents = new ArrayList<Object>(bodies.length);
        long alarmThreshold = -1;
        long cpuAlarmThreshold = -1;

        for (Body body : bodies)
        {
            String objectName = body.getStrObjName();
            String itemName = body.getStrItemName();

            ResponseBody responseBody = (ResponseBody)body;
            if (OBJECTNAME_JVN_FILE.equals(objectName) == true)
            {
                Object[] objItemValueArr = responseBody.getObjItemValueArr();
                if (objItemValueArr.length == 0)
                {
                    continue;
                }

                // ファイル名の配列だった場合。
                if (ITEMNAME_JVN_FILE_NAME.equals(itemName) == true)
                {
                    jvnFileNames.clear();
                    for (Object objItem : objItemValueArr)
                    {
                        jvnFileNames.add(objItem);
                    }
                }
                // ファイル内容の配列だった場合。
                else if (TelegramConstants.ITEMNAME_JVN_FILE_CONTENT.equals(itemName) == true)
                {
                    jvnFileContents.clear();
                    for (Object objItem : objItemValueArr)
                    {
                        jvnFileContents.add(objItem);
                    }
                }
                // アラーム閾値の場合
                if (TelegramConstants.ITEMNAME_ALARM_THRESHOLD.equals(itemName) == true)
                {
                    Long alarmThresholdLong = (Long)objItemValueArr[0];
                    if (alarmThresholdLong != null)
                    {
                        alarmThreshold = alarmThresholdLong.longValue();
                    }
                }
                // CPUアラーム閾値の場合
                else if (TelegramConstants.ITEMNAME_ALARM_CPU_THRESHOLD.equals(itemName) == true)
                {
                    Long alarmThresholdLong = (Long)objItemValueArr[0];
                    if (alarmThresholdLong != null)
                    {
                        cpuAlarmThreshold = alarmThresholdLong.longValue();
                    }
                }
            }
        }

        if (jvnFileNames.size() != jvnFileContents.size())
        {
            return null;
        }

        JvnFileEntry[] entries = new JvnFileEntry[jvnFileNames.size()];
        for (int i = 0; i < jvnFileNames.size(); i++)
        {
            entries[i] = new JvnFileEntry();
            entries[i].fileName = (String)jvnFileNames.get(i);
            entries[i].contents = (String)jvnFileContents.get(i);
            entries[i].alarmThreshold = alarmThreshold;
            entries[i].cpuAlarmThreshold = cpuAlarmThreshold;
        }
        return entries;
    }

    private static boolean checkTelegram(final Telegram telegram)
    {
        Header header = telegram.getObjHeader();
        return BYTE_TELEGRAM_KIND_JVN_FILE == header.getByteTelegramKind() ? true : false;
    }

    /**
     * Javelin ログを保持するためのクラスです。<br />
     * 
     * @author y-komori
     */
    public static class JvnFileEntry
    {
        /** ファイル名 */
        public String fileName;

        /** ファイル内容 */
        public String contents;

        /** アラーム閾値 */
        public long alarmThreshold;

        /** アラームCPU閾値 */
        public long cpuAlarmThreshold;
    }
}
