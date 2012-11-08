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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.web.dashboard.config.AgentSetting;
import jp.co.acroquest.endosnipe.web.dashboard.config.DataBaseConfig;
import jp.co.acroquest.endosnipe.web.dashboard.constants.EventConstants;
import jp.co.acroquest.endosnipe.web.dashboard.constants.LogMessageCodes;
import jp.co.acroquest.endosnipe.web.dashboard.entity.AgentInformationEntity;
import jp.co.acroquest.endosnipe.web.dashboard.manager.DatabaseManager;
import jp.co.acroquest.endosnipe.web.dashboard.util.ResponseUtil;

/**
 * エージェント一覧取得要求を処理するクラスです。
 * @author fujii
 *
 */
public class AgentInformationProcessor implements EventProcessor
{
    /** ロガー */
    private static final ENdoSnipeLogger LOGGER =
                                                  ENdoSnipeLogger.getLogger(AgentInformationProcessor.class);

    /**
     * サーバ側で監視対象となっているagentのid一覧を取得する。
     * {@inheritDoc}
     */
    public void process(HttpServletRequest request, HttpServletResponse response)
    {
        String clientId = request.getParameter(EventConstants.CLIENT_ID);
        if (clientId == null)
        {
            LOGGER.log(LogMessageCodes.NO_CLIENT_ID);
            return;
        }

        DatabaseManager manager = DatabaseManager.getInstance();
        DataBaseConfig dbConfig = manager.getDataBaseConfig();

        List<AgentSetting> agentSettingList = dbConfig.getAgentSettingList();
        int[] agentIds = new int[agentSettingList.size()];
        String[] agentNames = new String[agentSettingList.size()];
        for (int cnt = 0; cnt < agentSettingList.size(); cnt++)
        {
            AgentSetting agentSetting = agentSettingList.get(cnt);
            agentIds[cnt] = agentSetting.agentId;
            agentNames[cnt] = agentSetting.databaseName;
        }

        AgentInformationEntity agentEntity = new AgentInformationEntity();
        agentEntity.event_id = EventConstants.EVENT_AGENT_LIST_RESPONSE;
        agentEntity.agent_ids = agentIds;
        agentEntity.agent_names = agentNames;

        ResponseUtil.sendMessageOfJSONCode(response, agentEntity, clientId);
    }
}
