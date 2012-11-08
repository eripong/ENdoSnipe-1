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
package jp.co.acroquest.endosnipe.web.dashboard.service.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.web.dashboard.config.DataBaseConfig;
import jp.co.acroquest.endosnipe.web.dashboard.constants.EventConstants;
import jp.co.acroquest.endosnipe.web.dashboard.constants.LogMessageCodes;
import jp.co.acroquest.endosnipe.web.dashboard.listener.javelin.JavelinNotifyListener;
import jp.co.acroquest.endosnipe.web.dashboard.manager.DatabaseManager;
import net.arnx.jsonic.JSON;

/**
 * エージェントIDを取得する処理です。
 * @author kajiwara
 */
public class AgentNotifyProcessor implements EventProcessor, EventConstants
{
    /** ロガー */
    private static final ENdoSnipeLogger LOGGER =
                                                  ENdoSnipeLogger.getLogger(AgentNotifyProcessor.class);

    /**
     * {@inheritDoc}
     */
    public void process(HttpServletRequest request, HttpServletResponse response)
    {
        Map<Integer, String> databaseInfoMap = JavelinNotifyListener.getDatabaseNameMap();
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();

        for (Map.Entry<Integer, String> databaseInfo : databaseInfoMap.entrySet())
        {
            Map<String, String> result = new HashMap<String, String>();
            result.put("serverKind", databaseInfo.getValue());
            result.put("agentId", databaseInfo.getKey().toString());

            resultList.add(result);
        }

        // connect modeを追加
        Map<String, String> mode = new HashMap<String, String>();
        DatabaseManager manager = DatabaseManager.getInstance();
        DataBaseConfig dbConfig = manager.getDataBaseConfig();

        mode.put("connectMode", dbConfig.getConnectionMode());
        resultList.add(mode);

        PrintWriter writer = null;
        try
        {
            String result = JSON.encode(resultList);
            System.out.println(result);
            writer = response.getWriter();
            writer.write(result);
            writer.flush();
            writer.close();
        }
        catch (IOException ex)
        {
            LOGGER.log(LogMessageCodes.COMMUNICATION_ERROR);
        }
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }

    }

}
