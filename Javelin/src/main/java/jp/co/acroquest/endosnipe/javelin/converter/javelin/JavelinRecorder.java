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
package jp.co.acroquest.endosnipe.javelin.converter.javelin;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.javelin.StatsJavelinRecorder;
import jp.co.acroquest.endosnipe.javelin.converter.util.ConverterUtil;

/**
 * Javelinログを記録する
 * 
 * @author eriguchi
 * 
 */
public class JavelinRecorder
{
    /**
     * デフォルトコンストラクタ
     */
    private JavelinRecorder()
    {
        // Do Nothing.
    }

    /** Javelinの設定ファイル */
    private static JavelinConfig config__ = new JavelinConfig();

    /** クラス名の省略化のフラグ */
    private static boolean isSimplification__ = false;

    static
    {
        isSimplification__ = config__.isClassNameSimplify();
    }

    /**
     * 前処理。
     * 
     * @param className クラス名
     * @param methodName メソッド名
     * @param args 引数
     */
    public static void preProcess(String className, String methodName, final Object[] args)
    {
        try
        {
            if (isSimplification__)
            {
                className = ConverterUtil.toSimpleName(className);
                methodName = ConverterUtil.toSimpleName(methodName);
            }

            Object[] argsToRecord = null;
            if (config__.isLogArgs())
            {
                argsToRecord = args;
            }

            StatsJavelinRecorder.preProcess(className, methodName, argsToRecord, config__, true);
        }
        catch (Throwable th)
        {
            SystemLogger.getInstance().warn(th);
        }
    }

    /**
     * 後処理（本処理成功時）。
     * 
     * @param className クラス名
     * @param methodName メソッド名
     * @param retValue 戻り値。
     */
    public static void postProcessOK(String className, String methodName, final Object retValue)
    {
        try
        {
            if (isSimplification__)
            {
                className = ConverterUtil.toSimpleName(className);
                methodName = ConverterUtil.toSimpleName(methodName);
            }

            Object retValueToRecord = null;
            if (config__.isLogReturn())
            {
                retValueToRecord = retValue;
            }

            StatsJavelinRecorder.postProcess(className, methodName, retValueToRecord, config__);
        }
        catch (Throwable th)
        {
            SystemLogger.getInstance().warn(th);
        }
    }

    /**
     * 後処理（本処理失敗時）。
     * 
     * @param className クラス名
     * @param methodName メソッド名
     * @param cause 例外の原因
     */
    public static void postProcessNG(String className, String methodName, final Throwable cause)
    {
        try
        {
            if (isSimplification__)
            {
                className = ConverterUtil.toSimpleName(className);
                methodName = ConverterUtil.toSimpleName(methodName);
            }
            StatsJavelinRecorder.postProcess(className, methodName, cause, config__, true);
        }
        catch (Throwable th)
        {
            SystemLogger.getInstance().warn(th);
        }
    }

    /**
     * 設定クラスを読み込む
     * 
     * @param config Javelinの設定ファイル
     */
    public static void setJavelinConfig(final JavelinConfig config)
    {
        JavelinRecorder.config__ = config;
    }

    /**
     * スレッドのIDを設定する
     * 
     * @param threadId スレッドID
     */
    public static void setThreadId(final String threadId)
    {
        StatsJavelinRecorder.setThreadId(threadId);
    }

}
