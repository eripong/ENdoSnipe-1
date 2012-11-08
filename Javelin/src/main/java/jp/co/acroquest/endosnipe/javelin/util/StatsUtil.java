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
package jp.co.acroquest.endosnipe.javelin.util;

import jp.co.acroquest.endosnipe.common.logger.SystemLogger;

/**
 * 情報表示に関するユーティリティクラスです。<br />
 * 
 * @author eriguchi
 */
public class StatsUtil
{
    private StatsUtil()
    {
    }

    /**
     * スレッドを識別するための文字列を出力する。 <br>
     * フォーマット：スレッド名@スレッドクラス名@スレッドオブジェクトのID<br>
     * 
     * @param currentThread アクセス中のスレッド
     * @return スレッドを識別するための文字列
     */
    public static String createThreadIDText(final Thread currentThread)
    {
        StringBuilder threadId = new StringBuilder();
        threadId.append(currentThread.getName());
        threadId.append("@");
        threadId.append(ThreadUtil.getThreadId());
        threadId.append("(" + currentThread.getClass().getName());
        threadId.append("@");
        threadId.append(StatsUtil.getObjectID(currentThread));
        threadId.append(")");

        return threadId.toString();
    }

    /**
     * スレッドを識別するための文字列を出力する。 
     * フォーマット：スレッド名@スレッドクラス名@スレッドオブジェクトのID
     * 
     * @return スレッドを識別するための文字列
     */
    public static String createThreadIDText()
    {
        Thread currentThread = Thread.currentThread();
        return createThreadIDText(currentThread);
    }

    /**
     * オブジェクトIDを16進形式の文字列として取得する。
     * 
     * @param object オブジェクトIDを取得オブジェクト。
     * @return オブジェクトID。
     */
    public static String getObjectID(final Object object)
    {
        // 引数がnullの場合は"null"を返す。
        if (object == null)
        {
            return "null";
        }

        return Integer.toHexString(System.identityHashCode(object));
    }

    /**
     * オブジェクトの識別子を作成する。
     * @param obj オブジェクト
     * @return オブジェクトの識別子
     */
    public static String createIdentifier(final Object obj)
    {
        return obj.getClass().getName() + "@" + StatsUtil.getObjectID(obj);
    }

    /**
     * objectをtoStringで文字列に変換する。
     * 
     * toStringで例外が発生した場合は、
     * 標準エラー出力にobjectのクラス名とスタックトレースを出力し、
     * クラス名@オブジェクトIDを返す。
     * 
     * @param object オブジェクト
     * @return toStringでobjectを文字列化したもの。
     */
    public static String toStr(final Object object)
    {
        // 引数がnullの場合は"null"を返す。
        if (object == null)
        {
            return "null";
        }

        String result;
        try
        {
            result = object.toString();
        }
        catch (Throwable th)
        {
            SystemLogger.getInstance().debug(
                                             "Javelin Exception" + object.getClass().toString()
                                                     + "#toString(): ", th);
            result = object.getClass().toString() + "@" + StatsUtil.getObjectID(object);
        }
        return result;
    }

    /**
     * objectをtoStringで文字列に変換、指定長で切る。
     * 
     * toStringで例外が発生した場合は、
     * 標準エラー出力にobjectのクラス名とスタックトレースを出力し、
     * クラス名@オブジェクトIDを返す。
     * 指定長を超えている場合は指定長で切り、"..."を付与する。
     * 
     * @param object 文字列化対象オブジェクト
     * @param length 文字列指定長(負の値が指定された場合は指定長で切る処理を行わない)
     * @return toStringで文字列に変換し、指定長で切ったもの。
     */
    public static String toStr(final Object object, final int length)
    {
        // 引数がnullの場合は"null"を返す。
        if (object == null)
        {
            return "null";
        }

        String result;
        try
        {
            result = object.toString();
            if (length == 0)
            {
                result = "";
            }
            else if (length > 0 && result.length() > length)
            {
                result = result.substring(0, length) + "...";
            }
        }
        catch (Throwable th)
        {
            SystemLogger.getInstance().debug(
                                             "Javelin Exception" + object.getClass().toString()
                                                     + "#toString(): ", th);
            result = object.getClass().toString() + "@" + StatsUtil.getObjectID(object);
        }

        return result;
    }

    /**
     * バイト列をbyte[length]:FFFF...形式に変換、指定長で切る。
     * 
     * @param binary バイナリ
     * @return バイト列をbyte[length]:FFFF...形式に変換、指定長で切ったもの。
     */
    public static String toStr(final byte binary)
    {
        String hex = Integer.toHexString((binary) & 0xFF).toUpperCase();
        String result = "byte[1]:" + "00".substring(hex.length()) + hex;
        return result;
    }

    /**
     * バイト列をbyte[length]:FFFF...形式に変換(最大で先頭8バイトを16進出力)。
     * 
     * @param binary バイナリ
     * @return バイト列をbyte[length]:FFFF...形式に変換(最大で先頭8バイトを16進出力)したもの。
     */
    public static String toStr(final byte[] binary)
    {
        // 引数がnullの場合は"null"を返す。
        if (binary == null)
        {
            return "null";
        }

        if (binary.length == 0)
        {
            return "byte[0]";
        }

        StringBuffer result = new StringBuffer("byte[");
        result.append(binary.length);
        result.append("]:");
        for (int count = 0; count < 8 && count < binary.length; count++)
        {
            String hex = Integer.toHexString((binary[count]) & 0xFF).toUpperCase();
            result.append("00".substring(hex.length()) + hex);
        }
        if (binary.length > 8)
        {
            result.append("...");
        }
        return result.toString();
    }

    /**
     * Objectの情報出力を行う
     * 出力深度にあわせ、フィールドを辿るかその場で出力するか判定する
     * 
     * @param object       出力対象オブジェクト
     * @param detailDepth  出力深度
     * @return             出力結果
     */
    public static String buildDetailString(final Object object, final int detailDepth)
    {
        return DetailStringBuilder.buildDetailString(object, detailDepth);
    }

    /**
     * ToStringの結果を返す
     * 
     * @param object 変換対象
     * @return       ToStringの結果
     */
    public static String buildString(final Object object)
    {
        //toStringは例外を発生させることがあるため、発生時は
        //"????"という文字列を返すようにする。
        return DetailStringBuilder.buildString(object);
    }

    public static String substring(String str, int maxLength)
    {
        if (str == null)
        {
            return str;
        }

        if (maxLength > str.length())
        {
            maxLength = str.length();
        }
        return str.substring(0, maxLength);
    }

}
