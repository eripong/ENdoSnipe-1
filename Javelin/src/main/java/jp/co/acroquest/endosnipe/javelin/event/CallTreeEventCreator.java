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
package jp.co.acroquest.endosnipe.javelin.event;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.event.EventConstants;
import jp.co.acroquest.endosnipe.javelin.util.ThreadUtil;

/**
 * CallTreeに関するイベントを出力するクラスです。<br />
 * @author fujii
 *
 */
public class CallTreeEventCreator
{

    /** Javelinの設定値 */
    private static JavelinConfig config__ = new JavelinConfig();

    /**
     *CallTreeEventCreatorのインスタンス化を防止します。<br />
     */
    private CallTreeEventCreator()
    {
        // Do Nothing.
    }

    /**
     * CallTreeのサイズが最大値に達した時のイベントを出力します。<br />
     * 
     * @param className クラス名
     * @param methodName メソッド名
     * @param callTreeMax CallTreeの最大値
     * @return CallTreeのサイズが最大値に達した時のイベント
     */
    public static CommonEvent createTreeFullEvent(String className, String methodName,
            int callTreeMax)
    {
        CallTreeFullEvent event = new CallTreeFullEvent();
        event.setName(EventConstants.NAME_CALLTREE_FULL);
        event.addParam(EventConstants.PARAM_CALLTREE, String.valueOf(callTreeMax));
        String classMethod = className + "#" + methodName;
        event.addParam(EventConstants.PARAM_CALLTREE_METHOD, classMethod);
        String stackTrace = getStackTrace();
        event.addParam(EventConstants.PARAM_CALLTREE_STACKTRACE, stackTrace);

        return event;
    }

    /**
     * 現在のスタックトレースを取得します。<br />
     * 
     * @return スタックトレース
     */
    private static String getStackTrace()
    {
        StackTraceElement[] stacktraces = ThreadUtil.getCurrentStackTrace();
        String stackTrace = ThreadUtil.getStackTrace(stacktraces, config__.getTraceDepth());
        return stackTrace;
    }

}
