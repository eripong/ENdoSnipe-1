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
package jp.co.acroquest.endosnipe.javelin.communicate;

import java.util.List;

import jp.co.acroquest.endosnipe.common.entity.ItemType;
import jp.co.acroquest.endosnipe.common.util.StringUtil;
import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.javelin.MBeanManager;
import jp.co.acroquest.endosnipe.javelin.RootInvocationManager;
import jp.co.acroquest.endosnipe.javelin.bean.Component;
import jp.co.acroquest.endosnipe.javelin.bean.ExcludeMonitor;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;
import jp.co.acroquest.endosnipe.javelin.bean.TripleState;

/**
 * メソッド計測対象のON/OFFを受信するTelegramクラスです。<br />
 * 
 * @author fujii
 *
 */
public class TargetSetTelegramListener implements TelegramListener, TelegramConstants
{
    /** クラス名の配列位置 */
    private static final int INDEX_CLASSNAME = 0;

    /** メソッド名の配列位置 */
    private static final int INDEX_METHODNAME = 1;

    /**
     * 受信した電文の内容に応じてメソッド計測対象除外のON/OFFを切り替えます。<br />
     *
     * @param telegram 電文
     * @return 応答電文(null)
     */
    public Telegram receiveTelegram(Telegram telegram)
    {
        // ヘッダが以下の条件を満たす場合、処理を行います。
        // 電文種別 : 計測対象更新
        // 要求種別 : 要求
        if (telegram.getObjHeader().getByteTelegramKind() == BYTE_TELEGRAM_KIND_UPDATE_TARGET
                && telegram.getObjHeader().getByteRequestKind() == BYTE_REQUEST_KIND_REQUEST)
        {
            Body[] bodies = telegram.getObjBody();

            // 電文本体のうち、以下の条件を満たすもののみ処理します。
            // ・オブジェクト名が<クラス名>#セパレータ#<メソッド名>の形式である。
            // ・項目名が"target"である。
            // ・項目型が String型 である。
            // ・繰り返し回数が1回である。
            // ・詳細の第一引数が"true" か "false" である。
            for (Body body : bodies)
            {
                String objName = body.getStrObjName();
                List<String> strClassMethodNameList =
                                                      StringUtil.split(objName,
                                                                       CLASSMETHOD_SEPARATOR);
                String className = null;
                String methodName = null;
                if (strClassMethodNameList.size() <= INDEX_METHODNAME)
                {
                    continue;
                }
                className = strClassMethodNameList.get(INDEX_CLASSNAME);
                methodName = strClassMethodNameList.get(INDEX_METHODNAME);

                String itemName = body.getStrItemName();
                byte itemMode = ItemType.getItemTypeNumber(body.getByteItemMode());
                int loopTime = body.getIntLoopCount();
                if (BYTE_ITEMMODE_KIND_STRING == itemMode && loopTime == 1)
                {
                    if (ITEMNAME_TARGET.equals(itemName))
                    {
                        setTargetMethod(body, className, methodName);
                    }
                    else if (ITEMNAME_TRANSACTION_GRAPH.equals(itemName))
                    {
                        setTransactionGraphOutput(body, className, methodName);
                    }
                }
                else if (BYTE_ITEMMODE_KIND_8BYTE_INT == itemMode && loopTime == 1)
                {
                    if (ITEMNAME_ALARM_THRESHOLD.equals(itemName))
                    {
                        setAlarmThreshold(body, className, methodName);
                    }
                    else if (ITEMNAME_ALARM_CPU_THRESHOLD.equals(itemName))
                    {
                        setAlarmCpuThreshold(body, className, methodName);
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param body
     * @param className
     * @param methodName
     */
    private void setTargetMethod(Body body, String className, String methodName)
    {
        Component component = MBeanManager.getComponent(className);
        Invocation invocation = null;
        if (component != null)
        {
            invocation = component.getInvocation(methodName);
        }
        
        String isTargetStr = (String)body.getObjItemValueArr()[0];
        if ("true".equals(isTargetStr))
        {
            ExcludeMonitor.addTarget(invocation);
            ExcludeMonitor.removeTargetPreferred(invocation);
            ExcludeMonitor.removeExclude(invocation);
            ExcludeMonitor.removeExcludePreferred(invocation);
            if (invocation != null)
            {
                invocation.setMeasurementTarget(TripleState.ON);
            }
            
        }
        else if ("false".equals(isTargetStr))
        {
            ExcludeMonitor.addExclude(invocation);
            ExcludeMonitor.removeTarget(invocation);
            ExcludeMonitor.removeTargetPreferred(invocation);
            ExcludeMonitor.removeExcludePreferred(invocation);
            if (invocation != null)
            {
                invocation.setMeasurementTarget(TripleState.OFF);
            }
        }
    }

    /**
     * @param body
     * @param className
     * @param methodName
     */
    private void setTransactionGraphOutput(Body body, String className, String methodName)
    {
        String isTargetStr = (String)body.getObjItemValueArr()[0];
        Component component = MBeanManager.getComponent(className);
        if (component != null)
        {
            Invocation invocation = component.getInvocation(methodName);
            if (invocation != null)
            {
                if ("true".equals(isTargetStr))
                {
                    invocation.setResponseGraphOutput(TripleState.ON);
                    RootInvocationManager.addInvocation(invocation);
                }
                else if ("false".equals(isTargetStr))
                {
                    invocation.setResponseGraphOutput(TripleState.OFF);
                    RootInvocationManager.removeInvocation(invocation);
                }
            }
        }
    }

    /**
     * @param body
     * @param className
     * @param methodName
     */
    private void setAlarmThreshold(Body body, String className, String methodName)
    {
        Component component = MBeanManager.getComponent(className);
        if (component != null)
        {
            Invocation invocation = component.getInvocation(methodName);
            if (invocation != null)
            {
                long threshold = (Long)body.getObjItemValueArr()[0];
                invocation.setAlarmThreshold(threshold);
            }
        }
    }

    /**
     * @param body
     * @param className
     * @param methodName
     */
    private void setAlarmCpuThreshold(Body body, String className, String methodName)
    {
        Component component = MBeanManager.getComponent(className);
        if (component != null)
        {
            Invocation invocation = component.getInvocation(methodName);
            if (invocation != null)
            {
                long threshold = (Long)body.getObjItemValueArr()[0];
                invocation.setAlarmCpuThreshold(threshold);
            }
        }
    }
}
