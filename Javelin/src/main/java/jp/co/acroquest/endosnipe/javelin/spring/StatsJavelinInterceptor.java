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
package jp.co.acroquest.endosnipe.javelin.spring;

import java.io.PrintStream;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.javelin.StatsJavelinRecorder;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Component間の呼び出し関係をMBeanとして公開するためのInterceptor。
 * 以下の情報を、MBean経由で取得することが可能。
 * <ol>
 * <li>メソッドの呼び出し回数</li>
 * <li>メソッドの平均処理時間（ミリ秒単位）</li>
 * <li>メソッドの最長処理時間（ミリ秒単位）</li>
 * <li>メソッドの最短処理時間（ミリ秒単位）</li>
 * <li>メソッドの呼び出し元</li>
 * <li>例外の発生回数</li>
 * <li>例外の発生履歴</li>
 * </ol>
 * また、以下の条件をdiconファイルでのパラメータで指定可能。
 * <ol>
 * <li>intervalMax:メソッドの処理時間を何回分記録するか。
 *     デフォルト値は1000。</li>
 * <li>throwableMax:例外の発生を何回分記録するか。
 *     デフォルト値は1000。</li>
 * <li>recordThreshold:処理時間記録用の閾値。
 *     この時間を越えたメソッド呼び出しのみ記録する。
 *     デフォルト値は0。</li>
 * <li>alarmThreshold:処理時間記録用の閾値。
 *     この時間を越えたメソッド呼び出しの発生をViwerに通知する。
 *     デフォルト値は1000。</li>
 * <li>domain:MBeanを登録する際に使用するドメイン。
 *     実際のドメイン名は、[domainパラメータ] + [Mbeanの種類]となる。
 *     MBeanの種類は以下のものがある。
 *     <ol>
 *       <li>container:全コンポーネントのObjectNameを管理する。</li>
 *       <li>component:一つのコンポーネントに関する情報を公開するMBean。</li>
 *       <li>invocation:メソッド呼び出しに関する情報を公開するMBean。</li>
 *     </ol>
 *     </li>
 * </ol>
 * 
 * @version 0.1
 * @author Masanori Yamasaki
 */
public class StatsJavelinInterceptor implements MethodInterceptor
{
    private static final long serialVersionUID = 6661781313519708185L;

    private final JavelinConfig config_ = new JavelinConfig();

    /** 設定値を標準出力に出力したらtrue */
    private boolean isPrintConfig_ = false;

    /**
     * 呼び出し情報取得用のinvokeメソッド。
     * 
     * 実際のメソッド呼び出しを実行する前後で、
     * 実行回数や実行時間をMBeanに記録する。
     * 
     * 実行時に例外が発生した場合は、
     * 例外の発生回数や発生履歴も記録する。
     * 
     * @param invocation インターセプタによって取得された、呼び出すメソッドの情報
     * @return invocationを実行したときの戻り値
     * @throws Throwable invocationを実行したときに発生した例外
     */
    public Object invoke(final MethodInvocation invocation)
        throws Throwable
    {
        // 設定値を出力していなければ出力する
        synchronized (this)
        {
            if (this.isPrintConfig_ == false)
            {
                this.isPrintConfig_ = true;
                printConfigValue();
            }
        }

        String className = null;
        String methodName = null;
        try
        {
            // 呼び出し先情報取得。
            className = invocation.getMethod().getDeclaringClass().getName();
            methodName = invocation.getMethod().getName();

            StackTraceElement[] stacktrace = null;
            if (this.config_.isLogStacktrace())
            {
                stacktrace = Thread.currentThread().getStackTrace();
            }

            StatsJavelinRecorder.preProcess(className, methodName, invocation.getArguments(),
                                              stacktrace, this.config_, false);
        }
        catch (Throwable th)
        {
            th.printStackTrace();
        }

        Object ret = null;
        try
        {
            // メソッド呼び出し。
            ret = invocation.proceed();
        }
        catch (Throwable cause)
        {
            StatsJavelinRecorder.postProcess(className, methodName, cause, this.config_, false);

            //例外をスローし、終了する。
            throw cause;
        }

        try
        {
            StatsJavelinRecorder.postProcess(className, methodName, ret, this.config_, false);
        }
        catch (Throwable th)
        {
            th.printStackTrace();
        }

        return ret;
    }

    /**
     * 
     * @param intervalMax TATの最大値
     */
    public void setIntervalMax(final int intervalMax)
    {
        if (this.config_.isSetIntervalMax() == false)
        {
            this.config_.setIntervalMax(intervalMax);
        }
    }

    /**
     * 
     * @param throwableMax 保存する例外の最大数
     */
    public void setThrowableMax(final int throwableMax)
    {
        if (this.config_.isSetThrowableMax() == false)
        {
            this.config_.setThrowableMax(throwableMax);
        }
    }

    /**
     * 
     * @param alarmThreshold アラーム出力の閾値
     */
    public void setAlarmThreshold(final int alarmThreshold)
    {
        if (this.config_.isSetAlarmThreshold() == false)
        {
            this.config_.setAlarmThreshold(alarmThreshold);
        }
    }

    /**
     * 
     * @param javelinFileDir Javelinの保存ファイル
     */
    public void setJavelinFileDir(final String javelinFileDir)
    {
        if (this.config_.isSetJavelinFileDir() == false)
        {
            this.config_.setJavelinFileDir(javelinFileDir);
        }
    }

    /**
     * 
     * @param isLogArgs 引数を出力するかどうか
     */
    public void setLogArgs(final boolean isLogArgs)
    {
        if (this.config_.isSetLogArgs() == false)
        {
            this.config_.setLogArgs(isLogArgs);
        }
    }

    /**
     * 
     * @param isLogReturn 戻り値を出力するかどうか
     */
    public void setLogReturn(final boolean isLogReturn)
    {
        if (this.config_.isSetLogReturn() == false)
        {
            this.config_.setLogReturn(isLogReturn);
        }
    }

    /**
     * 
     * @param value Javelinファイルにスタックトレースを出力するかどうか
     */
    public void setLogStacktrace(final boolean value)
    {
        if (this.config_.isSetLogStacktrace() == false)
        {
            this.config_.setLogStacktrace(value);
        }
    }

    /**
     * 
     * @param endCalleeName 呼び出し先につける名称
     */
    public void setEndCalleeName(final String endCalleeName)
    {
        if (this.config_.isSetEndCalleeName() == false)
        {
            this.config_.setEndCalleeName(endCalleeName);
        }
    }

    /**
     * 
     * @param threadModel スレッドモデル
     */
    public void setThreadModel(final int threadModel)
    {
        if (this.config_.isSetThreadModel() == false)
        {
            this.config_.setThreadModel(threadModel);
        }
    }

    /**
     * 設定値を標準出力に出力する。
     */
    private void printConfigValue()
    {
        PrintStream out = System.out;
        out.println(">>>> Properties related with SpringJavelin");
        out.println("\tjavelin.intervalMax     : " + this.config_.getIntervalMax());
        out.println("\tjavelin.throwableMax    : " + this.config_.getThrowableMax());
        out.println("\tjavelin.alarmThreshold  : " + this.config_.getAlarmThreshold());
        out.println("<<<<");
    }

}
