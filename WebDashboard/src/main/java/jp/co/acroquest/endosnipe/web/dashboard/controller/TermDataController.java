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
package jp.co.acroquest.endosnipe.web.dashboard.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.co.acroquest.endosnipe.web.dashboard.dto.MeasurementValueDto;
import jp.co.acroquest.endosnipe.web.dashboard.dto.TreeMenuDto;
import jp.co.acroquest.endosnipe.web.dashboard.form.TermDataForm;
import jp.co.acroquest.endosnipe.web.dashboard.service.MeasurementValueService;
import jp.co.acroquest.endosnipe.web.dashboard.service.TreeMenuService;
import net.arnx.jsonic.JSON;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wgp.util.DataConvertUtil;

@Controller
@RequestMapping("/termData")
public class TermDataController
{
    private static final String TREE_DATA_ID = "tree";

    @Autowired
    protected TreeMenuService treeMenuService;

    @Autowired
    protected MeasurementValueService measurementValueService;

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, List<Map<String, String>>> getTermData(
            @RequestParam(value = "data") String data)
    {

        Map<String, List<Map<String, String>>> responceDataList =
                                                                  new HashMap<String, List<Map<String, String>>>();
        TermDataForm termDataForm = JSON.decode(data, TermDataForm.class);
        List<String> graphDataList = new ArrayList<String>();
        List<String> dataGroupIdList = termDataForm.getDataGroupIdList();
        if (dataGroupIdList == null)
        {
            // if no dataGroupId, return empty map.
            return new HashMap<String, List<Map<String, String>>>();
        }
        for (String dataId : dataGroupIdList)
        {
            if (TREE_DATA_ID.equals(dataId))
            {
                List<TreeMenuDto> treeMenuDtoList = treeMenuService.initialize();
                responceDataList.put(TREE_DATA_ID, createTreeMenuData(treeMenuDtoList));
            }
            else
            {
                graphDataList.add(dataId);
            }
        }
        if (graphDataList.size() != 0)
        {
            long startTime = Long.valueOf(termDataForm.getStartTime());
            long endTime = Long.valueOf(termDataForm.getEndTime());
            Date startDate = new Date(startTime);
            Date endDate = new Date(endTime);
            Map<String, List<MeasurementValueDto>> measurementValueMap =
                                                                         measurementValueService.getMeasurementValueList(startDate,
                                                                                                                         endDate,
                                                                                                                         graphDataList);
            for (Entry<String, List<MeasurementValueDto>> measurementValueEntry : measurementValueMap.entrySet())
            {
                String dataGroupId = measurementValueEntry.getKey();
                List<Map<String, String>> measurementValueList =
                                                                 createTreeMenuData(measurementValueEntry.getValue());
                responceDataList.put(dataGroupId, measurementValueList);
            }
        }
        return responceDataList;
    }

    private List<Map<String, String>> createTreeMenuData(List treeMenuDtoList)
    {
        List<Map<String, String>> bufferDtoList = new ArrayList<Map<String, String>>();
        for (Object treeMenuData : treeMenuDtoList)
        {
            bufferDtoList.add(DataConvertUtil.getPropertyList(treeMenuData));
        }
        return bufferDtoList;
    }

}
