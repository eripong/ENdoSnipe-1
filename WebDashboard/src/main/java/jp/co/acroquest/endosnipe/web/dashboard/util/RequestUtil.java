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
package jp.co.acroquest.endosnipe.web.dashboard.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.web.dashboard.constants.LogMessageCodes;

/**
 * クライアントから受信したリクエストを解析するUtilクラスです。
 * @author fujii
 *
 */
public class RequestUtil
{
    /** ロガーオブジェクト */
    private static final ENdoSnipeLogger LOGGER      = ENdoSnipeLogger.getLogger(RequestUtil.class);

    /** 日付のフォーマット */
    private static final String          DATE_FORMAT = "yyyy/MM/dd HH:mm";

    /**
     * インスタンス化を阻止するprivateコンストラクタです。
     */
    private RequestUtil()
    {
        // Do Nothing.
    }

    /**
     * 通知要求エージェントIDをリストとして取得します。
     * @param agentIds 通知要求エージェントID
     * @return 通知要求エージェントIDのリスト
     */
    public static List<Integer> getAgentIdList(String agentIds)
    {
        List<Integer> agentIdList = new ArrayList<Integer>();
        String[] agentIdArray = agentIds.split(",");
        for (String agentIdStr : agentIdArray)
        {
            Integer agentId = null;
            try
            {
                agentId = Integer.valueOf(agentIdStr);
                agentIdList.add(agentId);
            }
            catch (NumberFormatException ex)
            {
                LOGGER.log(LogMessageCodes.UNKNOWN_AGENT_ID, agentId);
                continue;
            }
        }
        return agentIdList;
    }

    /**
     * 通知要求計測IDをリストとして取得します。
     * @param measurementTypes 通知要求計測ID
     * @return 通知要求計測IDのリスト
     */
    public static List<Integer> getMeasurementTypeList(String measurementTypes)
    {
        List<Integer> measurementTypeList = new ArrayList<Integer>();
        String[] measurementTyepArray = measurementTypes.split(",");
        for (String measurementTypeStr : measurementTyepArray)
        {
            Integer measurementType = null;
            try
            {
                measurementType = Integer.valueOf(measurementTypeStr);
                measurementTypeList.add(measurementType);
            }
            catch (NumberFormatException ex)
            {
                LOGGER.log(LogMessageCodes.UNKNOWN_MEASUREMENT_TYPE, measurementType);
                continue;
            }
        }
        return measurementTypeList;
    }

    /**
     * 期間をDate型の配列として取得します。
     * @param spanStr 期間
     * @return 期間(開始時刻, 終了時刻)
     */
    public static Date[] getSpanList(String spanStr)
    {
        String[] spanArray = spanStr.split(",");
        if (spanArray.length != 2)
        {
            return null;
        }
        String startStr = spanArray[0];
        String endStr = spanArray[1];

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

        Date[] dateArray = new Date[2];
        try
        {
            dateArray[0] = formatter.parse(startStr);
            dateArray[1] = formatter.parse(endStr);
        }
        catch (ParseException e)
        {
            return null;
        }

        return dateArray;
    }
}
