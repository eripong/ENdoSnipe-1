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
package jp.co.acroquest.endosnipe.web.dashboard.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.data.dao.MeasurementValueDao;
import jp.co.acroquest.endosnipe.web.dashboard.constants.LogMessageCodes;
import jp.co.acroquest.endosnipe.web.dashboard.dto.MeasurementValueDto;
import jp.co.acroquest.endosnipe.web.dashboard.manager.DatabaseManager;

import org.springframework.stereotype.Service;

/**
 * MeasurementValueの取得用のインタフェースを提供する。
 * 
 * @author akiba
 */
@Service
public class MeasurementValueService
{
    /** ロガー */
    private static final ENdoSnipeLogger LOGGER =
        ENdoSnipeLogger.getLogger(MeasurementValueService.class);

    /**
     * 条件を指定してMeasurementValueのリストを取得する。
     * 
     * @param starttime  範囲開始時刻
     * @param endtime    範囲終了時刻
     * @param itemNameList 
     * @return MeasurementValueのリスト。
     */
    public Map<String, List<MeasurementValueDto>> getMeasurementValueList(Date starttime,
                                                                          Date endtime,
                                                                          List<String> measItemNameList)
    {
        // TODO データベース名が固定
        // →以下のコードは、collector.propertiesからデータベース名を取得するもの(clientモード想定)
        // →DataCollectorをserverモードで動かす場合は、Database名はあらかじめAgentから
        //   通知されているものを使用する。
        DatabaseManager dbMmanager = DatabaseManager.getInstance();
        String dbName = dbMmanager.getDataBaseName(1);
        
        Map<String, List<MeasurementValueDto>> valueMap =
            new HashMap<String, List<MeasurementValueDto>>();
        
        for (String itemName : measItemNameList)
        {
            List<MeasurementValueDto> valueList = new ArrayList<MeasurementValueDto>();
            valueMap.put(itemName, valueList);
            
            try
            {
                List<jp.co.acroquest.endosnipe.data.dto.MeasurementValueDto> queryResultList =
                    MeasurementValueDao.selectByTermAndMeasurementItemName(dbName,
                                                                           starttime, endtime,
                                                                           itemName);
                exchangeToDashboardDto(queryResultList, valueList);
            }
            catch (SQLException ex)
            {
                LOGGER.log(LogMessageCodes.SQL_EXCEPTION);
            }
        }
        
        return valueMap;
    }

    /**
     * DataAccesesorを使ってDBから取得した計測値の情報を、Dashboard用のDTOに詰め替える。
     * 
     * @param dashboardDtoList Dashboard用のDTOのリスト。
     * @param queryResultList DBから取得した計測値のリスト。
     */
    private void exchangeToDashboardDto(
            List<jp.co.acroquest.endosnipe.data.dto.MeasurementValueDto> queryResultList,
            List<MeasurementValueDto> dashboardDtoList)
    {
        if (dashboardDtoList == null || queryResultList == null)
        {
            return;
        }
        
        for (jp.co.acroquest.endosnipe.data.dto.MeasurementValueDto queryDto : queryResultList)
        {
            MeasurementValueDto dashboardDto = new MeasurementValueDto();
            dashboardDto.setMeasurementItemId(queryDto.measurementItemId);
            dashboardDto.setMeasurementItemName(queryDto.measurementItemName);
            dashboardDto.setMeasurementTime(queryDto.measurementTime.getTime());
            dashboardDto.setMeasurementValue(queryDto.value);
            dashboardDtoList.add(dashboardDto);
        }
    }
}
