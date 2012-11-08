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
package jp.co.acroquest.endosnipe.communicator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.entity.ItemType;
import jp.co.acroquest.endosnipe.common.util.IOUtil;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.ResponseBody;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;

/**
 * 電文を生成するためのユーティリティクラスです。<br />
 * 
 * @author y-komori
 */
public final class TelegramCreator implements TelegramConstants
{
    private static JavelinConfig javelinConfig__ = new JavelinConfig();
    
    private TelegramCreator()
    {
        // Do nothing.
    }

    /**
     * 空の応答電文を生成します。<br />
     *
     * @param telegramKind 電文種別
     * @param requestKind 要求応答種別
     * @return 電文
     */
    public static Telegram createEmptyTelegram(final byte telegramKind,
            final byte requestKind)
    {
        Header header = new Header();
        header.setByteTelegramKind(telegramKind);
        header.setByteRequestKind(requestKind);

        Telegram telegram = new Telegram();
        telegram.setObjHeader(header);
        telegram.setObjBody(new Body[0]);
        return telegram;
    }

    /**
     * Javelinログ通知電文を生成します。<br />
     * Javelinログそのものを電文に添付します。
     * 
     * @param jvnFileName Javelinログファイル名
     * @param javelinLogContent Javelinログ
     * @param telegramId 電文 ID
     * @return ログ通知電文
     */
    public static Telegram createJvnFileNotifyTelegram(final String jvnFileName,
            final String javelinLogContent, final long telegramId)
    {
        Telegram telegram =
                createJvnLogDownloadTelegram(BYTE_REQUEST_KIND_NOTIFY, new Object[]{jvnFileName},
                                             new Object[]{javelinLogContent}, telegramId);

        return telegram;
    }

    /**
     * JVNログ電文を生成します。<br />
     * 
     * @param requestKind 要求応答種別
     * @param jvnFileNames Javelinログファイル名
     * @return JVNログ電文
     */
    public static Telegram createJvnLogDownloadTelegram(final byte requestKind,
            final Object[] jvnFileNames)
    {
        return createJvnLogDownloadTelegram(requestKind, jvnFileNames, null, 0);
    }

    /**
     * JVNログ電文を生成します。<br />
     * ログファイルの内容がnullの場合はファイルから読み込みます。
     * 
     * @param requestKind 要求応答種別
     * @param jvnFileNames Javelinログファイル名
     * @param jvnFileContents Javelinログファイルの内容
     * @param telegramId 電文 ID
     * @return JVNログ電文
     */
    public static Telegram createJvnLogDownloadTelegram(final byte requestKind,
            final Object[] jvnFileNames, Object[] jvnFileContents, final long telegramId)
    {
        final JavelinConfig JAVELINCONFIG = new JavelinConfig();

        // 電文ヘッダを作る
        Header objHeader = new Header();
        objHeader.setId(telegramId);
        objHeader.setByteRequestKind(requestKind);
        objHeader.setByteTelegramKind(BYTE_TELEGRAM_KIND_JVN_FILE);

        // 電文本体を作る
        ResponseBody jvnFileNameBody =
                TelegramUtil.createResponseBody(OBJECTNAME_JVN_FILE, ITEMNAME_JVN_FILE_NAME,
                                                ItemType.ITEMTYPE_STRING, jvnFileNames);

        if (jvnFileContents == null)
        {
            jvnFileContents = new String[jvnFileNames.length];
        }
        
        if (jvnFileNames.length != jvnFileContents.length)
        {
            throw new IllegalArgumentException();
        }
        
        for (int index = 0; index < jvnFileNames.length; index++)
        {
            if (jvnFileContents[index] == null)
            {
                Object objJvnFileName = jvnFileNames[index];
                if (objJvnFileName instanceof String == false)
                {
                    continue;
                }
                String jvnFileName = (String)objJvnFileName;
                String jvnFileContent =
                        IOUtil.readFileToString(JAVELINCONFIG.getJavelinFileDir() + File.separator
                                + jvnFileName, JAVELINCONFIG.getJvnDownloadMax());
                jvnFileContents[index] = jvnFileContent;
            }
        }
        ResponseBody jvnFileContentBody =
                TelegramUtil.createResponseBody(OBJECTNAME_JVN_FILE, ITEMNAME_JVN_FILE_CONTENT,
                                                ItemType.ITEMTYPE_STRING, jvnFileContents);

        
        // 閾値設定を電文に追加する。
        ResponseBody thresholdBody =
                                    TelegramUtil.createResponseBody(
                                                                    OBJECTNAME_JVN_FILE,
                                                                    ITEMNAME_ALARM_THRESHOLD,
                                                                    ItemType.ITEMTYPE_LONG,
                                                                    new Long[]{javelinConfig__.getAlarmThreshold()});
        ResponseBody cpuThresholdBody =
                                    TelegramUtil.createResponseBody(
                                                                    OBJECTNAME_JVN_FILE,
                                                                    ITEMNAME_ALARM_CPU_THRESHOLD,
                                                                    ItemType.ITEMTYPE_LONG,
                                                                    new Long[]{javelinConfig__.getAlarmCpuThreashold()});
        
        // 電文オブジェクトを設定する
        Telegram objTelegram = new Telegram();
        objTelegram.setObjHeader(objHeader);
        objTelegram.setObjBody(new ResponseBody[]{jvnFileNameBody, jvnFileContentBody,
                thresholdBody, cpuThresholdBody});

        return objTelegram;
    }

    /**
     * JVNログ取得応答電文を生成します。<br />
     * 
     * @param jvnFileNames JVNログファイル名のリスト
     *        (<code>null</code> の場合は <code>null</code> を返す)
     * @return 電文オブジェクト
     */
    public static Telegram createJvnLogListTelegram(final String[] jvnFileNames)
    {
        // ファイル名のリストがnullのとき、nullを返す。
        if (jvnFileNames == null)
        {
            return null;
        }
        // 電文ヘッダを作る
        Header objHeader = new Header();
        objHeader.setByteRequestKind(BYTE_REQUEST_KIND_RESPONSE);
        objHeader.setByteTelegramKind(BYTE_TELEGRAM_KIND_JVN_FILE_LIST);

        // 電文本体を作る
        ResponseBody body =
                TelegramUtil.createResponseBody("jvnFile", "jvnFileName",
                                                ItemType.ITEMTYPE_STRING, jvnFileNames);

        // 電文オブジェクトを設定する
        Telegram objTelegram = new Telegram();
        objTelegram.setObjHeader(objHeader);
        objTelegram.setObjBody(new ResponseBody[]{body});

        return objTelegram;
    }

    /**
     * クラス削除要求電文を生成します。<br />
     *
     * @param classNameList クラス名のリスト
     * @return 電文オブジェクト
     */
    public static Telegram createRemoveClassTelegram(final List<String> classNameList)
    {
        Header objHeader = new Header();
        objHeader.setByteTelegramKind(BYTE_TELEGRAM_KIND_REMOVE_CLASS);
        objHeader.setByteRequestKind(BYTE_REQUEST_KIND_REQUEST);

        Telegram objOutputTelegram = new Telegram();
        objOutputTelegram.setObjHeader(objHeader);

        List<Body> bodies = new ArrayList<Body>();
        if (classNameList != null)
        {
            for (String className : classNameList)
            {
                Body body = new Body();
                body.setStrObjName(className);
                body.setByteItemMode(ItemType.ITEMTYPE_STRING);
                body.setStrItemName(ITEMNAME_CLASSTOREMOVE);
                body.setIntLoopCount(0);
                body.setObjItemValueArr(new Object[0]);
                bodies.add(body);
            }
        }

        objOutputTelegram.setObjBody(bodies.toArray(new Body[bodies.size()]));
        return objOutputTelegram;
    }

    /**
     * Invocation を更新するための電文を生成します。<br />
     *
     * @param invocationParamArray Invocation を更新する内容
     * @return 電文オブジェクト
     */
    public static Telegram createUpdateInvocationTelegram(
            final UpdateInvocationParam[] invocationParamArray)
    {
        Header objHeader = new Header();
        objHeader.setByteTelegramKind(BYTE_TELEGRAM_KIND_UPDATE_TARGET);
        objHeader.setByteRequestKind(BYTE_REQUEST_KIND_REQUEST);

        Telegram objOutputTelegram = new Telegram();
        objOutputTelegram.setObjHeader(objHeader);

        List<Body> bodies = new ArrayList<Body>();

        // 電文本体を設定する
        if (invocationParamArray != null)
        {
            for (UpdateInvocationParam invocation : invocationParamArray)
            {
                Body body;
                Object[] itemValueArr;
                String objName =
                        invocation.getClassName() + CLASSMETHOD_SEPARATOR + invocation.getMethodName();
    
                if (invocation.isTransactionGraphOutput() != null)
                {
                    body = new Body();
                    body.setStrObjName(objName);
                    body.setByteItemMode(ItemType.ITEMTYPE_STRING);
                    body.setStrItemName(ITEMNAME_TRANSACTION_GRAPH);
                    body.setIntLoopCount(1);
                    itemValueArr = new String[1];
                    itemValueArr[0] = invocation.isTransactionGraphOutput().toString();
                    body.setObjItemValueArr(itemValueArr);
                    bodies.add(body);
                }
    
                if (invocation.isTarget() != null)
                {
                    body = new Body();
                    body.setStrObjName(objName);
                    body.setByteItemMode(ItemType.ITEMTYPE_STRING);
                    body.setStrItemName(ITEMNAME_TARGET);
                    body.setIntLoopCount(1);
                    itemValueArr = new String[1];
                    itemValueArr[0] = String.valueOf(invocation.isTarget());
                    body.setObjItemValueArr(itemValueArr);
                    bodies.add(body);
                }
    
                if (invocation.getAlarmThreshold() != null)
                {
                    body = new Body();
                    body.setStrObjName(objName);
                    body.setByteItemMode(ItemType.ITEMTYPE_LONG);
                    body.setStrItemName(ITEMNAME_ALARM_THRESHOLD);
                    body.setIntLoopCount(1);
                    itemValueArr = new Long[1];
                    itemValueArr[0] = invocation.getAlarmThreshold();
                    body.setObjItemValueArr(itemValueArr);
                    bodies.add(body);
                }
    
                if (invocation.getAlarmCpuThreshold() != null)
                {
                    body = new Body();
                    body.setStrObjName(objName);
                    body.setByteItemMode(ItemType.ITEMTYPE_LONG);
                    body.setStrItemName(ITEMNAME_ALARM_CPU_THRESHOLD);
                    body.setIntLoopCount(1);
                    itemValueArr = new Long[1];
                    itemValueArr[0] = invocation.getAlarmCpuThreshold();
                    body.setObjItemValueArr(itemValueArr);
                    bodies.add(body);
                }
            }
        }
        objOutputTelegram.setObjBody(bodies.toArray(new Body[bodies.size()]));
        return objOutputTelegram;
    }

    /**
     * Invocation を更新するためのパラメタクラス。<br />
     *
     * @author sakamoto
     */
    public static class UpdateInvocationParam
    {
        private final String className_;

        private final String methodName_;

        private final Boolean transactionGraphOutput_;

        private final Boolean target_;

        private final Long alarmThreshold_;

        private final Long alarmCpuThreshold_;

        /**
         * Invocation を更新するためのパラメタオブジェクトを生成します。<br />
         *
         * @param className クラス名
         * @param methodName メソッド名
         * @param transactionGraph トランザクショングラフを出力するか否か（ <code>null</code> なら設定しない）
         * @param target 計測対象にするか否か（ <code>null</code> なら設定しない）
         * @param alarmThreshold アラーム閾値（ <code>null</code> なら設定しない）
         * @param alarmCpuThreshold CPUアラーム閾値（ <code>null</code> なら設定しない）
         */
        public UpdateInvocationParam(String className, String methodName, Boolean transactionGraph,
                Boolean target, Long alarmThreshold, Long alarmCpuThreshold)
        {
            this.className_ = className;
            this.methodName_ = methodName;
            this.transactionGraphOutput_ = transactionGraph;
            this.target_ = target;
            this.alarmThreshold_ = alarmThreshold;
            this.alarmCpuThreshold_ = alarmCpuThreshold;
        }

        /**
         * クラス名を返します。<br />
         *
         * @return クラス名
         */
        public String getClassName()
        {
            return this.className_;
        }

        /**
         * メソッド名を返します。<br />
         *
         * @return メソッド名
         */
        public String getMethodName()
        {
            return this.methodName_;
        }

        /**
         * トランザクショングラフを表示するか否かを返します。<br />
         *
         * 設定しない場合は <code>null</code> を返します。<br />
         *
         * @return トランザクショングラフを表示する場合は <code>true</code>
         */
        public Boolean isTransactionGraphOutput()
        {
            return this.transactionGraphOutput_;
        }

        /**
         * 計測対象にするか否かを返します。<br />
         *
         * 設定しない場合は <code>null</code> を返します。<br />
         *
         * @return 計測対象にする場合は <code>true</code>
         */
        public Boolean isTarget()
        {
            return this.target_;
        }

        /**
         * アラーム閾値を返します。<br />
         *
         * 設定しない場合は <code>null</code> を返します。<br />
         *
         * @return アラーム閾値（ミリ秒）
         */
        public Long getAlarmThreshold()
        {
            return this.alarmThreshold_;
        }

        /**
         * CPUアラーム閾値を返します。<br />
         *
         * 設定しない場合は <code>null</code> を返します。<br />
         *
         * @return CPUアラーム閾値
         */
        public Long getAlarmCpuThreshold()
        {
            return this.alarmCpuThreshold_;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("{className=");
            builder.append(getClassName());
            builder.append(",methodName=");
            builder.append(getMethodName());
            builder.append("}");
            return builder.toString();
        }
    }

    /**
     *  JVNログ取得電文を作成する。
     * 
     * @param requestKind 電文要求応答種別。
     * @param jvnFileNames JVNログファイル名。
     * @return JVNログ取得電文。
     */
    public static Telegram createJvnLogTelegram(final byte requestKind,
            final List<String> jvnFileNames)
    {
        Telegram telegram =
                TelegramUtil.createSingleTelegram(BYTE_TELEGRAM_KIND_JVN_FILE, requestKind,
                                                  OBJECTNAME_JVN_FILE, ITEMNAME_JVN_FILE_NAME,
                                                  ItemType.ITEMTYPE_STRING, jvnFileNames);
    
        return telegram;
    }

    /**
     * スレッドダンプ取得要求電文を作成します。
     *
     * @return スレッドダンプ取得要求電文
     */
    public static Telegram createThreadDumpRequestTelegram()
    {
        Header objHeader = new Header();
        objHeader.setId(TelegramUtil.generateTelegramId());
        objHeader.setByteTelegramKind(BYTE_TELEGRAM_KIND_GET_DUMP);
        objHeader.setByteRequestKind(BYTE_REQUEST_KIND_REQUEST);
        Body[] bodies = TelegramUtil.createEmptyRequestBody(OBJECTNAME_DUMP, ITEMNAME_THREADDUMP);

        Telegram requestTelegram = new Telegram();
        requestTelegram.setObjHeader(objHeader);
        requestTelegram.setObjBody(bodies);

        return requestTelegram;
    }

    /**
     * ヒープダンプ取得要求電文を作成します。
     *
     * @return ヒープダンプ取得要求電文
     */
    public static Telegram createHeapDumpRequestTelegram()
    {
        Header objHeader = new Header();
        objHeader.setId(TelegramUtil.generateTelegramId());
        objHeader.setByteTelegramKind(BYTE_TELEGRAM_KIND_GET_DUMP);
        objHeader.setByteRequestKind(BYTE_REQUEST_KIND_REQUEST);
        Body[] bodies = 
            TelegramUtil.createEmptyRequestBody(OBJECTNAME_DUMP, ITEMNAME_HEAPDUMP);
    
        Telegram objOutputTelegram = new Telegram();
        objOutputTelegram.setObjHeader(objHeader);
        objOutputTelegram.setObjBody(bodies);
        return objOutputTelegram;
    }

    
    /**
     * ヒープダンプ取得応答電文を作成します。
     *
     * @param telegramId 電文 ID
     * @return ヒープダンプ取得応答電文
     */
    public static Telegram createHeapDumpResponseTelegram(final long telegramId)
    {
        Header objHeader = new Header();
        objHeader.setId(telegramId);
        objHeader.setByteTelegramKind(BYTE_TELEGRAM_KIND_GET_DUMP);
        objHeader.setByteRequestKind(BYTE_REQUEST_KIND_RESPONSE);
        Body[] bodies = 
            TelegramUtil.createEmptyRequestBody(OBJECTNAME_DUMP, ITEMNAME_HEAPDUMP);
    
        Telegram objOutputTelegram = new Telegram();
        objOutputTelegram.setObjHeader(objHeader);
        objOutputTelegram.setObjBody(bodies);
        return objOutputTelegram;
    }

    /**
     * クラスヒストグラム取得要求電文を作成します。
     *
     * @return クラスヒストグラム取得要求電文
     */
    public static Telegram createClassHistogramRequestTelegram()
    {
        Header objHeader = new Header();
        objHeader.setId(TelegramUtil.generateTelegramId());
        objHeader.setByteTelegramKind(BYTE_TELEGRAM_KIND_GET_DUMP);
        objHeader.setByteRequestKind(BYTE_REQUEST_KIND_REQUEST);
        Body[] bodies = 
            TelegramUtil.createEmptyRequestBody(OBJECTNAME_DUMP, ITEMNAME_CLASSHISTOGRAM);
    
        Telegram objOutputTelegram = new Telegram();
        objOutputTelegram.setObjHeader(objHeader);
        objOutputTelegram.setObjBody(bodies);
        return objOutputTelegram;
    }

    /**
     * クラスヒストグラム取得応答電文を作成します。
     *
     * @param telegramId 電文 ID
     * @return クラスヒストグラム取得応答電文
     */
    public static Telegram createClassHistogramResponseTelegram(final long telegramId)
    {
        Header objHeader = new Header();
        objHeader.setId(TelegramUtil.generateTelegramId());
        objHeader.setByteTelegramKind(BYTE_TELEGRAM_KIND_GET_DUMP);
        objHeader.setByteRequestKind(BYTE_REQUEST_KIND_RESPONSE);
        Body[] bodies = 
            TelegramUtil.createEmptyRequestBody(OBJECTNAME_DUMP, ITEMNAME_CLASSHISTOGRAM);
    
        Telegram objOutputTelegram = new Telegram();
        objOutputTelegram.setObjHeader(objHeader);
        objOutputTelegram.setObjBody(bodies);
        return objOutputTelegram;
    }
}
