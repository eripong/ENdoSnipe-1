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

import java.util.List;

import jp.co.acroquest.endosnipe.common.Constants;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.util.ResourceDataUtil;
import jp.co.acroquest.endosnipe.data.dto.MeasurementValueDto;
import jp.co.acroquest.endosnipe.web.dashboard.constants.LogMessageCodes;
import jp.co.acroquest.endosnipe.web.dashboard.service.processor.TermMeasurementDataProcessor;

/**
 * イベント処理に関するユーティリティクラスです。
 * 
 * @author fujii
 */
public class EventUtil
{
    /** ロガーオブジェクト */
    private static final ENdoSnipeLogger LOGGER            =
                                                             ENdoSnipeLogger.getLogger(EventUtil.class);

    /** 通知要求レベル:INFO */
    public static final String           ALARM_LEVEL_INFO  = "INFO";

    /** 通知要求レベル:WARN */
    public static final String           ALARM_LEVEL_WARN  = "WARN";

    /** 通知要求レベル:ERROR */
    public static final String           ALARM_LEVEL_ERROR = "ERROR";

    /** 通知要求レベル：INFO */
    public static final int              LEVEL_INFO        = 20;

    /** 通知要求レベル：WARN */
    public static final int              LEVEL_WARN        = 30;

    /** 通知要求レベル：ERROR */
    public static final int              LEVEL_ERROR       = 40;

    /** 通知要求レベル：UNKNOW(識別不能) */
    public static final int              LEVEL_UNKNOW      = -1;

    /** アラーム個数のデフォルト値 */
    public static final int              DEF_ALARM_COUNT   = 100;

    /**
     * インスタンス化を阻止するプライベートクラスです。
     */
    private EventUtil()
    {
        // Do Nothing.
    }

    /**
     * 通知要求レベルを返します。
     * 未指定の場合、存在しないレベルを指定する場合、
     * デフォルト値(INFO(20))を返します。
     * @param levelStr 通知要求レベルの文字列
     * @return 通知要求のレベル(INFO/WARN/ERROR)
     */
    public static int getAlarmLevel(String levelStr)
    {
        if (levelStr == null)
        {
            LOGGER.log(LogMessageCodes.UNKNOWN_ALARM_LEVEL, ALARM_LEVEL_INFO);
            return LEVEL_INFO;
        }
        String levelStrUp = levelStr.toUpperCase();
        if (ALARM_LEVEL_INFO.equals(levelStrUp))
        {
            return LEVEL_INFO;
        }
        else if (ALARM_LEVEL_WARN.equals(levelStrUp))
        {
            return LEVEL_WARN;
        }
        else if (ALARM_LEVEL_ERROR.equals(levelStrUp))
        {
            return LEVEL_ERROR;
        }
        LOGGER.log(LogMessageCodes.UNKNOWN_ALARM_LEVEL, ALARM_LEVEL_INFO, levelStr);
        return LEVEL_INFO;
    }

    /**
     * INFOレベルで出力するかどうかを返します。
     * @param level レベル
     * @return INFOレベルで出力する場合、true を返し、そうでない場合はfalseを返します。
     */
    public static boolean isInfo(String level)
    {
        return compareLevel(level, LEVEL_INFO);
    }

    /**
     * WARNレベルで出力するかどうかを返します。
     * @param level レベル
     * @return WARNレベルで出力する場合、true を返し、そうでない場合はfalseを返します。
     */
    public static boolean isWarn(String level)
    {
        return compareLevel(level, LEVEL_WARN);
    }

    /**
     * ERRORレベルで出力するかどうかを返します。
     * @param level レベル
     * @return ERRORレベルで出力する場合、true を返し、そうでない場合はfalseを返します。
     */
    public static boolean isError(String level)
    {
        return compareLevel(level, LEVEL_ERROR);
    }

    /**
     * 引数で指定したレベルが閾値のレベルを超えているかどうかを判定しいます。
     * @param levelStr 判定対象のレベル(文字列)
     * @param alarmLevel 閾値のレベル
     * @return 引数で指定したレベルが閾値のレベルを超えている場合、<code>true</code>。
     */
    public static boolean compareLevel(String levelStr, int alarmLevel)
    {
        int level = getAlarmLevel(levelStr);
        if (alarmLevel <= level)
        {
            return true;
        }
        return false;
    }

    /**
     * アラーム個数を返します。
     * 未指定の場合、もしくはフォーマットエラーの場合、
     * デフォルト値(100)を返します。
     * @param alarmCntStr アラーム個数の文字列
     * @return アラームの個数
     */
    public static int getAlarmCount(String alarmCntStr)
    {
        int alarmCount = DEF_ALARM_COUNT;
        if (alarmCntStr == null)
        {
            LOGGER.log(LogMessageCodes.UNKNOWN_ALARM_LEVEL, DEF_ALARM_COUNT);
            return alarmCount;
        }

        try
        {
            alarmCount = Integer.parseInt(alarmCntStr);
        }
        catch (NumberFormatException ex)
        {
            LOGGER.log(LogMessageCodes.UNKNOWN_ALARM_LEVEL, DEF_ALARM_COUNT);
            return alarmCount;
        }
        return alarmCount;
    }

    /**
     * 値の期間取得時において物理メモリ使用量を空きメモリ量から計算する処理。
     * 
     * @param trueMeasurementItemName 測定項目
     * @param measurementValueList 最大メモリ量
     * @param sysPhysicMemFreeDtoList 空きメモリ使用量
     * @return 物理メモリ使用量
     */
    public static List<MeasurementValueDto> confirmMeasurementValueList(
            String trueMeasurementItemName, List<MeasurementValueDto> measurementValueList,
            List<MeasurementValueDto> sysPhysicMemFreeDtoList)
    {
        for (int index = 0; index < measurementValueList.size(); index++)
        {
            MeasurementValueDto tmpMeasurementValueDto = measurementValueList.get(index);
            //使用物理メモリを計算。
            if (trueMeasurementItemName.equals(Constants.ITEMNAME_SYSTEM_MEMORY_PHYSICAL_USED))
            {
                MeasurementValueDto sysPhysicMemUsedDto = sysPhysicMemFreeDtoList.get(index);
                tmpMeasurementValueDto.value = String.valueOf(
                    EventUtil.makeUsedPhysicalMemoryData(
                        Long.valueOf(tmpMeasurementValueDto.value).longValue(),
                        Long.valueOf(sysPhysicMemUsedDto.value).longValue()));
            }

            if (TermMeasurementDataProcessor.MEASUREMENT_TYPE_DISPLAY_NAME.containsKey(trueMeasurementItemName))
            {
                tmpMeasurementValueDto.measurementTypeDisplayName =
                    TermMeasurementDataProcessor.MEASUREMENT_TYPE_DISPLAY_NAME.get(trueMeasurementItemName);
            }

            if (TermMeasurementDataProcessor.MEASUREMENT_TYPE_ITEM_NAME.containsKey(trueMeasurementItemName))
            {
                tmpMeasurementValueDto.measurementTypeItemName =
                    TermMeasurementDataProcessor.MEASUREMENT_TYPE_ITEM_NAME.get(trueMeasurementItemName);
            }
        }
        return measurementValueList;
    }

    /**
     * 小数点以下はDBに入らないため、整数に置換してある値を元に戻す。
     * 
     * @param dbData dbに格納されている値
     * @return 測定値
     */
    public static double makeDoubleMeasurementData(double dbData)
    {
        return (dbData / ResourceDataUtil.PERCENTAGE_DATA_MAGNIFICATION);
    }

    /**
     * 物理メモリの最大値と空きメモリ量から、メモリ使用量を計算する。
     * 
     * @param systemPhysicalMaxValue 物理メモリの最大値
     * @param systemPhysicalFreeValue 物理メモリ空き容量
     * @return 物理メモリ使用量
     */
    public static long makeUsedPhysicalMemoryData(long systemPhysicalMaxValue,
            long systemPhysicalFreeValue)
    {
        long systemPhysicalUsedValue = systemPhysicalMaxValue - systemPhysicalFreeValue;
        return systemPhysicalUsedValue;
    }
}
