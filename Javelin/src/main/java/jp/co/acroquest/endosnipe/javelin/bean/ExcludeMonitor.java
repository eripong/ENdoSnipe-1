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
package jp.co.acroquest.endosnipe.javelin.bean;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.javelin.StatsJavelinRecorder;

/**
 * 計測対象から除外するクラス、メソッドを監視するクラスです。<br />
 * @author fujii
 *
 */
public class ExcludeMonitor
{
    /** 計測対象除外かどうか判定するための回数の閾値 */
    private static int autoExcludeThresholdCount__;

    /** 計測対象除外かどうか判定するための時間の閾値 */
    private static int autoExcludeThresholdTime__;

    static
    {
        JavelinConfig config = new JavelinConfig();
        autoExcludeThresholdCount__ = config.getAutoExcludeThresholdCount();
        autoExcludeThresholdTime__  = config.getAutoExcludeThresholdTime();
    }

    /**
     * インスタンス化を防ぐ、プライベートコンストラクタです。<br />
     */
    private ExcludeMonitor()
    {
        // Do Nothing.
    }

    /**
     * 引数で与えられたクラス名、メソッド名が自動で計測対象から除外されているか判定します。<br />
     * 
     * @return 自動処理により計測対象から除外されている場合、<code>true</code>
     */
    public static boolean isExcludePreffered(final Invocation invocation)
    {
        if (invocation == null)
        {
            return false;
        }
        
        return invocation.isExcludePreffered();
    }

    /**
     * 引数で与えられたInvocationで示される呼び出しを、
     * 自動的に収集した計測対象除外リストに追加します。<br />
     * 
     * @param invocation {@link Invocation}オブジェクト
     */
    public static void addExcludePreffered(final Invocation invocation)
    {
        if (invocation == null)
        {
            return;
        }

        invocation.setExcludePreffered(true);
    }

    /**
     * 引数で与えられたInvocationで示される呼び出しを、
     * 自動的に収集した計測対象除外リストから削除します。<br />
     * 
     * @param invocation {@link Invocation}オブジェクト
     */
    public static void removeExcludePreferred(Invocation invocation)
    {
        if (invocation == null)
        {
            return;
        }

        invocation.setExcludePreffered(false);
    }

    /**
     * CallTreeMeasurementをログ出力対象から除外するかどうか判定します。<br />
     * 
     * @param invocation {@link Invocation}オブジェクト
     */
    public static void judgeExclude(final Invocation invocation)
    {
        long totalTime = invocation.getTotal();
        if (autoExcludeThresholdTime__ > 0 
                && invocation.getCount() >= autoExcludeThresholdCount__
                && totalTime < autoExcludeThresholdTime__)
        {
            addExcludePreffered(invocation);
        }
    }

    /**
     * 引数で与えられたクラス名、メソッド名が自動的に収集した計測対象であるかを判定します。<br />
     * 
     * @return ログ出力から除外されている場合、<code>true</code>
     */
    public static boolean isTargetPreferred(final Invocation invocation)
    {
        if (invocation == null)
        {
            return false;
        }
        
        return invocation.isTargetPreferred();
    }

    /**
     * 引数で与えられたクラス名、メソッド名を自動的に収集した計測対象リストに追加します。<br />
     */
    public static void addTargetPreferred(final Invocation invocation)
    {
        if (invocation == null)
        {
            return;
        }

        invocation.setTargetPreferred(true);
    }

    /**
     * 引数で与えられたクラス名、メソッド名を自動的に収集した計測対象から除外します。<br />
     */
    public static void removeTargetPreferred(final Invocation invocation)
    {
        if (invocation == null)
        {
            return;
        }

        invocation.setTargetPreferred(false);
    }

    /**
     * 引数で与えられたクラス名、メソッド名が計測対象から除外するかどうかを判定します。<br />
     * 
     * @return ログ出力から除外されている場合、<code>true</code>
     */
    public static boolean isExclude(final Invocation invocation)
    {
        if (invocation == null)
        {
            return false;
        }
        
        return invocation.isExclude();
    }

    /**
     * 引数で与えられたクラス名、メソッド名を計測対象除外リストに追加します。<br />
     */
    public static void addExclude(final Invocation invocation)
    {
        if (invocation == null)
        {
            return;
        }

        invocation.setExclude(true);
    }

    /**
     * 引数で与えられたクラス名、メソッド名を計測対象除外リストから除外します。<br />
     */
    public static void removeExclude(final Invocation invocation)
    {
        if (invocation == null)
        {
            return;
        }

        invocation.setExclude(false);
    }
    
    /**
     * 引数で与えられたInvocationで示される呼び出しが、計測対象であるかを判定します。<br />
     * 
     * @param invocation {@link Invocation}オブジェクト
     * @return ログ出力から除外されている場合、<code>true</code>
     */
    public static boolean isTarget(final Invocation invocation)
    {
        if (invocation == null)
        {
            return true;
        }

        return invocation.isTarget();
    }


    /**
     * 引数で与えられたクラス名、メソッド名を計測対象リストに追加します。<br />
     */
    public static void addTarget(final Invocation invocation)
    {
        if (invocation == null)
        {
            return;
        }

        invocation.setTarget(true);
    }

    /**
     * 引数で与えられたクラス名、メソッド名を計測対象から除外します。<br />
     */
    public static void removeTarget(final Invocation invocation)
    {
        if (invocation == null)
        {
            return;
        }
        
        invocation.setTarget(false);
    }

    /**
     * 引数で与えられたクラスのメソッドが計測対象か否かを返します。<br />
     *
     * 計測対象と指定されている、または
     *
     * @return 計測対象の場合は <code>true</code> 、計測対象でない場合は <code>false</code>
     */
    public static boolean isMeasurementTarget(final Invocation invocation)
    {
        if(invocation == null)
        {
            return true;
        }
        
        boolean isTarget = invocation.isTarget();
        if (isTarget)
        {
            return true;
        }
        
        boolean isExclude = invocation.isExclude();
        if (isExclude)
        {
            return false;
        }
        
        boolean isTargetPreferred = invocation.isTargetPreferred();
        if (isTargetPreferred)
        {
            return true;
        }
        
        boolean isExcludePreferred = invocation.isExcludePreffered();
        return (isExcludePreferred == false);
    }
}
