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
package jp.co.acroquest.endosnipe.javelin.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeCommonPluginProvider;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.parser.JavelinConstants;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogAccessor;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogColumnNum;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogConstants;
import jp.co.acroquest.endosnipe.common.util.CSVTokenizer;
import jp.co.acroquest.endosnipe.common.util.NormalDateFormatter;
import jp.co.acroquest.endosnipe.javelin.JavelinLogUtil;

/**
 * JavelinログをJavelinLogElementにパースする。 JavelinConverterから切り出して作成した。
 * 
 * @author eriguchi
 */
public class JavelinParser
{
    private static final ENdoSnipeLogger LOGGER =
            ENdoSnipeLogger.getLogger(JavelinParser.class, ENdoSnipeCommonPluginProvider.INSTANCE);

    /** 詳細タグの接頭辞 */
    public static final String DETAIL_TAG_PREFIX = "<<javelin.";

    private static final int DETAIL_START_TAG_LENGTH = DETAIL_TAG_PREFIX.length();

    /** 詳細タグの接尾辞 */
    public static final String DETAIL_TAG_START_END_STR = "_START>>";

    /**
     * 例外であるかどうかを示すタグ。
     */
    public static final String JAVELIN_EXCEPTION = "<<javelin.Exception>>";

    /** 以下、動作ログの詳細情報を分類するためのタグ */
    public static final String TAG_TYPE_ARGS = "Args";

    /** 動作ログの詳細情報を分類するタグ(JMXInfo) */
    public static final String TAG_TYPE_JMXINFO = "JMXInfo";

    /** 動作ログの詳細情報を分類するタグ(ExtraInfo) */
    public static final String TAG_TYPE_EXTRAINFO = "ExtraInfo";

    /** 動作ログの詳細情報を分類するタグ(EventInfo) */
    public static final String TAG_TYPE_EVENTINFO = "EventInfo";

    /** 動作ログの詳細情報を分類するタグ(StackTrace) */
    public static final String TAG_TYPE_STACKTRACE = "StackTrace";

    /** 動作ログの詳細情報を分類するタグ(ReturnValue) */
    public static final String TAG_TYPE_RETURN_VAL = "ReturnValue";

    /** 動作ログの詳細情報を分類するタグ(FieldValue) */
    public static final String TAG_TYPE_FIELD_VAL = "FieldValue";

    /** オブジェクト単位のシーケンスにするかを指定するプロパティの名前 */
    public static final String PROP_JAVELINCONV_OBJECT = "javelinConv.object";

    /** PROP_JAVELINCONV_OBJECTプロパティのデフォルト値 */
    public static final String PROP_JAVELINCONV_OBJECT_DEFAULT = "false";

    private static final String MESSAGE_FORMAT_ERROR =
            Messages.getString("0000_actionLogError_actionLogFileFormatError"); //$NON-NLS-1$

    /**
     * エラー出力用ストリーム。
     */
    private final PrintStream errorStream_ = System.err;

    /** logFileName_の名前をもつファイル */
    private File logFile_;

    /** ログファイルの名前。起動時、引数に渡されたままの文字列である */
    private final String logFileName_;

    /**
     * Javelin ログ取得オブジェクト。<br />
     */
    private final JavelinLogAccessor logAccessor_;

    /** logFile_を、行数をカウントしながら読み込むためのリーダー */
    private LineNumberReader logBufferedReader_;

    /** 先読みしたログ要素の基本情報 */
    private String nextBaseInfo_ = "";

    /** 現在読み込み中の行番号 */
    private int logFileLine_;

    /**
     * 読み込みファイルがファイルの終端に到達したかどうか。
     */
    private boolean isEOF_ = false;

    /**
     * ファイル名を指定してパーサを作成します。<br />
     * 
     * @param fileName
     *            パース対象のファイル名
     */
    public JavelinParser(final String fileName)
    {
        this.logFileName_ = fileName;
        this.logAccessor_ = null;
    }

    /**
     * パーサを作成します。<br />
     * 
     * @param logAccessor
     *            Javelin ログ取得オブジェクト
     */
    public JavelinParser(final JavelinLogAccessor logAccessor)
    {
        this.logFileName_ = logAccessor.getFileName();
        this.logAccessor_ = logAccessor;
    }

    /**
     * 初期化します。<br />
     * 
     * パース対象のファイルを開き、行番号を初期化します。
     * 
     * @throws ParseException
     *             ファイルが存在しない場合、ディレクトリの場合、 読み込み権限がない場合
     */
    public void init()
        throws ParseException
    {
        // ファイルの開始行番号を1に初期化する。
        this.logFileLine_ = 1;

        Reader logReader = null;
        try
        {
            if (this.logAccessor_ != null)
            {
                // //////// パース対象がログ取得オブジェクトにある場合 //////////
                InputStream input = this.logAccessor_.getInputStream();
                logReader = new InputStreamReader(input);
            }
            else
            {
                // //////// パース対象がファイルの場合 //////////

                this.logFile_ = new File(this.logFileName_);

                // ログファイルが存在しない、もしくはファイルでない場合は、
                // エラーを出力して失敗を返す。
                if (this.logFile_.exists() == false || this.logFile_.isFile() == false)
                {
                    String message =
                            MessageFormat.format(Messages.getString("0001_notExist"),
                                                 new Object[]{this.logFileName_});
                    this.printError(message);
                    throw new ParseException(message);
                }
                // ファイルの読み込み権限がない場合は、エラーを出力して失敗を返す。
                else if (this.logFile_.canRead() == false)
                {
                    String message =
                            MessageFormat.format(Messages.getString("0002_unreadable"),
                                                 new Object[]{this.logFileName_});
                    this.printError(message);
                    throw new ParseException(message);
                }
                logReader = new FileReader(this.logFile_);
            }

            // 動作ログの内容を読む込むReaderを生成
            this.logBufferedReader_ = new LineNumberReader(logReader);
        }
        catch (IOException exp)
        {
            String message =
                    MessageFormat.format(Messages.getString("0002_unreadable"),
                                         new Object[]{this.logFileName_});
            this.printError(message);
            throw new ParseException(message, exp);
        }

    }

    /**
     * パース対象のファイルをクローズする。 終了時には必ずこのメソッドを呼ぶこと。
     * 
     * @throws IOException
     *             パース対象のファイルのクローズに失敗した場合。
     */
    public void close()
        throws IOException
    {
        if (this.logBufferedReader_ != null)
        {
            this.logBufferedReader_.close();
        }

    }

    /**
     * エラーメッセージを表示する。
     * 
     * @param message
     *            エラーメッセージ
     */
    private void printError(final String message)
    {
        this.errorStream_.println(message);
    }

    /**
     * 動作ログから一要素分に対応する記述を取得する。 終了位置まで読み込み終わっている場合は、nullを返す。
     * 
     * @return 動作ログにおける一要素分のログ文字列
     * @throws IOException
     *             入出力例外発生時
     * @throws ParseException
     *             パースエラー発生時
     */
    public JavelinLogElement nextElement()
        throws IOException,
            ParseException
    {
        // ファイルの開始行を記録する
        int startLogLine = this.logFileLine_;

        // 終了位置まで読み込み終わっている場合は、nullを返す。
        if (this.nextBaseInfo_ == null)
        {
            return null;
        }

        // Javelin動作ログの要素を生成
        JavelinLogElement javelinLogElement = new JavelinLogElement();

        // ログファイル名の設定
        javelinLogElement.setLogFileName(this.logFileName_);

        // 基本情報の取り出し
        List<String> baseInfoList = this.getBaseInfoList();

        if (baseInfoList == null)
        {
            return null;
        }

        // 基本情報のセット
        javelinLogElement.setBaseInfo(baseInfoList);

        // 詳細情報の取り出し
        boolean hasDetailInfo = this.getDetailInfo(javelinLogElement);
        while (hasDetailInfo == true)
        {
            // 詳細情報の取り出し
            hasDetailInfo = this.getDetailInfo(javelinLogElement);
        }

        // 空行を読み飛ばす。そのとき、空行も詳細情報に含む。
        while ("".equals(this.nextBaseInfo_))
        {
            this.nextBaseInfo_ = this.logBufferedReader_.readLine();
        }

        this.logFileLine_ = this.logBufferedReader_.getLineNumber();

        // ファイルの終了行を記録する
        int endLogLine = this.logFileLine_ - 1;
        if (this.isEOF_)
        {
            endLogLine++;
        }

        // 開始、終了の行番号を設定する。
        javelinLogElement.setStartLogLine(startLogLine);
        javelinLogElement.setEndLogLine(endLogLine);

        return javelinLogElement;
    }

    /**
     * 基本情報の行からCSVで切り分けたリストを返す
     * 
     * @return 基本情報のリスト
     * @throws IOException
     *             ファイルの読み込みに失敗した場合
     */
    public List<String> getBaseInfoList()
        throws IOException
    {
        String baseInfoString;

        // フィールドに基本情報が保持されている場合
        if (this.nextBaseInfo_.length() > 0)
        {
            // フィールドから基本情報の取り出し
            baseInfoString = this.nextBaseInfo_;
            this.nextBaseInfo_ = null;
        }
        else
        {
            // 動作ログから基本情報の取り出し
            String line = this.logBufferedReader_.readLine();
            if (line == null)
            {
                return null;
            }
            baseInfoString = line;
        }

        // CSVによる基本情報の切り分け
        CSVTokenizer csvTokenizer = new CSVTokenizer(baseInfoString);

        // 全ての基本情報について、CSVで切り出し、
        // 基本情報のリストに追加
        List<String> baseInfoList = new ArrayList<String>();
        boolean hasMoreBaseInfo = csvTokenizer.hasMoreTokens();
        while (hasMoreBaseInfo == true)
        {
            String baseInfo = csvTokenizer.nextToken();
            baseInfoList.add(baseInfo);

            hasMoreBaseInfo = csvTokenizer.hasMoreTokens();
        }

        return baseInfoList;
    }

    /**
     * 詳細情報の読み込みを行う
     * 
     * @param logElement
     *            Javelinログの要素
     * @return 詳細情報取得の結果。取れたときは、true。取れなかったときは、false。
     * @throws ParseException
     *             詳細ログのフォーマットが異常なためにパースに失敗した場合。
     * @throws IOException
     *             詳細ログの読み込みに失敗した場合
     */
    public boolean getDetailInfo(final JavelinLogElement logElement)
        throws ParseException,
            IOException
    {
        boolean result = false;

        String nextInfoLine = this.logBufferedReader_.readLine();

        if (nextInfoLine == null)
        {
            this.isEOF_ = true;
        }
        else if (nextInfoLine != null && nextInfoLine.startsWith(DETAIL_TAG_PREFIX) == true)
        {
            result = true;

            // 詳細情報が例外を意味する"javelin.Exception"であるかをチェック
            boolean isJavelinExceptionTag = nextInfoLine.indexOf(JAVELIN_EXCEPTION) >= 0;
            if (isJavelinExceptionTag == true)
            {
                // 詳細情報 "javelin.Exception"をセット
                String detailTagType = JAVELIN_EXCEPTION;
                String detailTagData = JAVELIN_EXCEPTION;
                logElement.setDetailInfo(detailTagType, detailTagData);
                return true;
            }

            // 開始タグの末尾が正しい形になっているかチェック
            // 正しくない場合は、エラー出力して中断。
            boolean isRightEnd = nextInfoLine.endsWith(DETAIL_TAG_START_END_STR);
            if (isRightEnd == false)
            {
                // 動作ログの現在の行番号を保持する
                this.logFileLine_ = this.logBufferedReader_.getLineNumber();

                String message =
                        this.createLogParseErrorMsg(
                                                    MESSAGE_FORMAT_ERROR,
                                                    Messages.getString("0000_actionLogError_beginningTabError"),
                                                    this.logFileLine_, null);
                this.printError(message);
                return false;
            }

            int endPos = nextInfoLine.length() - DETAIL_TAG_START_END_STR.length();
            // 詳細情報のタグタイプ名の取得
            String detailTagType = nextInfoLine.substring(DETAIL_START_TAG_LENGTH, endPos);

            // 詳細情報を保存するStringBuffer
            StringBuffer detailInfoBuffer = new StringBuffer();

            // 詳細情報の中身を一行ずつ読み込む。
            // ファイルの最後まで読んでも終了タグが現れない場合は、エラー出力して中断。
            String detailInfoLine = this.logBufferedReader_.readLine();
            while (detailInfoLine.startsWith(DETAIL_TAG_PREFIX) == false)
            {
                detailInfoBuffer.append(detailInfoLine);
                detailInfoBuffer.append(System.getProperty("line.separator"));
                detailInfoLine = this.logBufferedReader_.readLine();
                if (detailInfoLine == null)
                {
                    String message =
                            this.createLogParseErrorMsg(
                                                        MESSAGE_FORMAT_ERROR,
                                                        Messages.getString("0000_actionLogError_noFinalTab"),
                                                        this.logBufferedReader_.getLineNumber(),
                                                        null);
                    this.printError(message);
                    return false;
                }
            }

            logElement.setDetailInfo(detailTagType, detailInfoBuffer.toString());
        }
        else
        {
            // 基本情報が連続した場合に通る処理
            // 読み込んだ次の行を、次の基本情報を表すフィールドにセット
            this.nextBaseInfo_ = nextInfoLine;
        }
        return result;
    }

    /**
     * ログのパースエラーメッセージを作成する。
     * 
     * @param message
     * @param cause
     * @param lineNum
     * @param log
     */
    private String createLogParseErrorMsg(String message, final String cause, final int lineNum,
            String log)
    {
        if (log == null)
        {
            log = "";
        }

        message =
                MessageFormat.format(Messages.getString("0000_actionLogError"), new Object[]{
                        message, cause, this.logFile_.getName(), lineNum, log});
        return message;
    }

    /**
     * Javelinログの詳細情報を初期化する。
     * 
     * 具体的には、Callログに対して、そのメソッドの純粋値 （ElapsedTimeとPure CPU
     * Time）を計算し、extraInfoマップに登録する。
     * 
     * @param logList
     *            Javelinログ
     */
    public static void initDetailInfo(final List<JavelinLogElement> logList)
    {
        // 純粋値を計算する値のキーを登録する
        Map<String, String> pureKeyMap = register();
        // メソッド呼び出しスタック
        Stack<MethodParam> methodCallStack = new Stack<MethodParam>();

        // それぞれのJavelin Callログに対して、まずはログに記述された値をマップに登録し、
        // マップから子メソッドの値を引いていく
        for (JavelinLogElement targetMethod : logList)
        {
            if (targetMethod == null)
            {
                continue;
            }
            // Callログ以外は無視する
            String id = targetMethod.getLogIDType();
            if (JavelinConstants.MSG_CALL.equals(id) == false)
            {
                continue;
            }

            MethodParam methodParam = new MethodParam();
            methodParam.setJavelinLogElement(targetMethod);

            // 現在パース中のメソッドの開始時刻を取得する
            List<String> baseInfo = targetMethod.getBaseInfo();
            if (baseInfo == null)
            {
                continue;
            }
            String callTimeString = baseInfo.get(JavelinLogColumnNum.CALL_TIME);
            if (callTimeString == null)
            {
                continue;
            }
            try
            {
                methodParam.setStartTime(NormalDateFormatter.parse(callTimeString).getTime());
            }
            catch (java.text.ParseException ex)
            {
                methodParam.setStartTime(0);
            }

            // 現在パース中のメソッドのDuration Timeを取得する
            Map<String, String> extraInfoMap =
                    JavelinLogUtil.parseDetailInfo(targetMethod, JavelinParser.TAG_TYPE_EXTRAINFO);
            String durationString = extraInfoMap.get(JavelinLogConstants.EXTRAPARAM_DURATION);
            if (durationString != null)
            {
                try
                {
                    methodParam.setDuration(Long.parseLong(durationString));
                }
                catch (NumberFormatException ex)
                {
                    LOGGER.error(ex.getMessage(), ex);
                    continue;
                }
            }
            else
            {
                methodParam.setDuration(0);
            }

            // 開始時刻とDuration Timeから、メソッドの終了時刻を計算する
            methodParam.setEndTime(methodParam.getStartTime() + methodParam.getDuration());
            methodParam.setOriginalDataMap(new HashMap<String, Double>());
            methodParam.setPureDataMap(new HashMap<String, Double>());

            // 純粋値を求めるすべての項目に対して、現在値（ログファイルに記述されている値）をマップに登録する
            for (Map.Entry<String, String> entrySet : pureKeyMap.entrySet())
            {
                // 純粋値を計算する元となるキーと値
                String detailInformationKey = entrySet.getKey();
                String originalString =
                        JavelinParser.getValueFromExtraInfoOrJmxInfo(targetMethod,
                                                                     detailInformationKey);
                double originalValue;
                if (originalString != null)
                {
                    try
                    {
                        originalValue = Double.parseDouble(originalString);
                    }
                    catch (NumberFormatException ex)
                    {
                        LOGGER.error(ex.getMessage(), ex);
                        continue;
                    }
                }
                else
                {
                    originalValue = 0;
                }
                String javelinLogFileParam = entrySet.getValue();
                methodParam.getOriginalDataMap().put(javelinLogFileParam, originalValue);
                methodParam.getPureDataMap().put(javelinLogFileParam, originalValue);
            }

            // メソッド呼び出しスタックに格納されているメソッドの中で、
            // メソッド終了時刻がこのメソッド開始時刻よりも前のものの純粋値を計算する
            calcPureValue(methodCallStack, methodParam);

            methodCallStack.push(methodParam);
        }

        // 最後まで残ったメソッドの純粋値を計算する
        while (methodCallStack.size() > 0)
        {
            MethodParam methodParam = methodCallStack.pop();
            if (methodCallStack.size() > 0)
            {
                MethodParam parentMethodParam = methodCallStack.get(methodCallStack.size() - 1);
                parentMethodParam.subtractData(methodParam);
            }
            registerPureDataToJavelinLogElement(methodParam);
        }
    }

    /**
     * 指定されたメソッドの開始時刻よりも前に実行が完了しているメソッドの純粋値を計算し、 その値をメソッドのマップに登録する。
     * 
     * @param methodCallStack
     *            この中に登録されているメソッドの純粋値を計算する
     * @param methodParam
     *            このメソッドの開始時刻より前に実行が完了しているメソッドの純粋値を計算する
     */
    private static void calcPureValue(final Stack<MethodParam> methodCallStack,
            final MethodParam methodParam)
    {
        if (methodCallStack.size() <= 0)
        {
            return;
        }

        // Stackの中で、methodParamで示されるメソッドの開始時刻よりも前に
        // 実行が終了しているメソッドの純粋値を計算する
        long callStartTime = methodParam.getStartTime();
        MethodParam parentMethodParam = methodCallStack.get(methodCallStack.size() - 1);
        while (parentMethodParam.getEndTime() <= callStartTime)
        {
            methodCallStack.pop();
            if (methodCallStack.size() == 0)
            {
                // メソッドのマップに純粋値を登録する
                registerPureDataToJavelinLogElement(parentMethodParam);
                break;
            }

            // 　現在着目しているメソッド（parentMethodParam）の親メソッドの値から、
            // 着目しているメソッドの値を引くことで、純粋値を計算する
            MethodParam grandparentMethodParam = methodCallStack.get(methodCallStack.size() - 1);
            grandparentMethodParam.subtractData(parentMethodParam);

            // メソッドのマップに純粋値を登録する
            registerPureDataToJavelinLogElement(parentMethodParam);

            parentMethodParam = grandparentMethodParam;
        }
    }

    /**
     * 純粋値を計算するキーの対応を生成する。
     * 
     * @return 純粋値を計算する元となる値のキーと、純粋値を格納するキーのマップ
     */
    private static Map<String, String> register()
    {
        Map<String, String> pureKeyMap = new HashMap<String, String>();
        pureKeyMap.put(JavelinLogConstants.EXTRAPARAM_DURATION,
                       JavelinLogConstants.EXTRAPARAM_ELAPSEDTIME);
        pureKeyMap.put(JavelinLogConstants.JMXPARAM_THREAD_CURRENT_THREAD_CPU_TIME_DELTA,
                       JavelinLogConstants.EXTRAPARAM_PURECPUTIME);
        pureKeyMap.put(JavelinLogConstants.JMXPARAM_THREAD_CURRENT_THREAD_USER_TIME_DELTA,
                       JavelinLogConstants.EXTRAPARAM_PUREUSERTIME);
        pureKeyMap.put(JavelinLogConstants.JMXPARAM_THREAD_THREADINFO_WAITED_TIME_DELTA,
                       JavelinLogConstants.EXTRAPARAM_PUREWAITEDTIME);
        return pureKeyMap;
    }

    /**
     * JavelinLogElementのExtraInfo中に、計算した純粋値を追記する。
     * 
     * @param methodParam
     *            メソッド
     */
    private static void registerPureDataToJavelinLogElement(final MethodParam methodParam)
    {
        JavelinLogElement parentMethod = methodParam.getJavelinLogElement();
        for (Map.Entry<String, Double> entrySet : methodParam.getPureDataMap().entrySet())
        {
            double value = entrySet.getValue();
            if (value < 0)
            {
                value = 0;
            }

            String extraInfoStr = parentMethod.getDetailInfo(JavelinParser.TAG_TYPE_EXTRAINFO);
            String newExtraInfoStr = extraInfoStr + entrySet.getKey() + " = " + value + "\r\n";

            parentMethod.setDetailInfo(JavelinParser.TAG_TYPE_EXTRAINFO, newExtraInfoStr);
        }
    }

    /**
     * ExtraInfoまたはJmxInfoから値を取得する。
     * 
     * @param element
     *            メソッド
     * @param key
     *            値を取得するキー
     * @return 値。値を取得できない場合は <code>null</code>
     */
    private static String getValueFromExtraInfoOrJmxInfo(final JavelinLogElement element,
            final String key)
    {
        Map<String, String> map = JavelinLogUtil.parseDetailInfo(element, TAG_TYPE_EXTRAINFO);
        String pureValueString = map.get(key);
        if (pureValueString == null)
        {
            map = JavelinLogUtil.parseDetailInfo(element, JavelinParser.TAG_TYPE_JMXINFO);
            pureValueString = map.get(key);
        }
        return pureValueString;
    }

}
