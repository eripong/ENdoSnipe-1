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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.web.dashboard.constants.LogMessageCodes;
import net.arnx.jsonic.JSON;

/**
 * WebDashboardに通知するための共通処理を持つクラス。
 * @author s-nakagawa
 *
 */
public class ResponseUtil
{
    /** ロガーオブジェクト */
    private static final ENdoSnipeLogger LOGGER = ENdoSnipeLogger.getLogger(ResponseUtil.class);

    /**
     * インスタンス化を阻止するprivateコンストラクタです。
     */
    private ResponseUtil()
    {
        // Do Nothing.
    }

    /**
     * リアルタイム更新時のメッセージ送付処理を行う。
     * 
     * @param response レスポンス
     * @param pendingMessagesMap 送るべきメッセージ群
     * @param clientId クライアントId
     */
    public static void sendMessageToClient(HttpServletResponse response,
            Map<String, String> pendingMessagesMap, String clientId)
    {
        PrintWriter writer = null;
        try
        {
            writer = response.getWriter();
            String sendMessage = pendingMessagesMap.get(clientId);
            if (sendMessage == null)
            {
                return;
            }
            writer.print(sendMessage);
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.log(LogMessageCodes.RESPONCE_MESSAGE_CODE, clientId, sendMessage);
            }
            writer.flush();
            writer.close();
            writer = null;
        }
        catch (IOException ex)
        {
            LOGGER.log(LogMessageCodes.COMMUNICATION_ERROR, ex);
        }
        finally
        {
            if (writer != null)
            {
                writer.close();
            }

        }
    }

    public static void sendMessageOfJSONCode(HttpServletResponse response, Object entity,
            String clientId)
    {
        PrintWriter writer = null;
        try
        {
            String result = JSON.encode(entity);
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.log(LogMessageCodes.RESPONCE_MESSAGE_CODE, clientId, result);
            }
            writer = response.getWriter();
            writer.write(result);
            writer.flush();
            writer.close();
        }
        catch (IOException ex)
        {
            LOGGER.log(LogMessageCodes.COMMUNICATION_ERROR, ex);
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
