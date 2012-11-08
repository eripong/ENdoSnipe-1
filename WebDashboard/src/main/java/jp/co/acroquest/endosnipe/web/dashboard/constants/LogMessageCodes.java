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
package jp.co.acroquest.endosnipe.web.dashboard.constants;

import jp.co.acroquest.endosnipe.common.logger.CommonLogMessageCodes;

/**
 * ログメッセージのための定数クラスです。<br />
 * 
 * @author fujii
 */
public interface LogMessageCodes extends CommonLogMessageCodes
{

    // -------------------------------------------------------------------------
    // 基本動作メッセージコード (00xx)
    // -------------------------------------------------------------------------
    /** 入出力例外が発生 */
    String IO_ERROR                                = "WEWD0001";

    // -------------------------------------------------------------------------
    // 電文受信メッセージコード (01xx)
    // -------------------------------------------------------------------------
    /** イベントIDが未指定、または認識できない */
    String UNKNOWN_EVENT_ID                        = "WEWD0101";

    /** 通知要求エージェントIDが未指定、または認識できない */
    String UNKNOWN_AGENT_ID                        = "WEWD0102";

    /** 通知先グラフIDが未指定、または認識できない */
    String UNKNOWN_GRAPH_ID                        = "WEWD0103";

    /** 通知要求計測IDが未指定、または認識できない */
    String UNKNOWN_MEASUREMENT_TYPE                = "WEWD0104";

    /** クライアントが指定されていない */
    String NO_CLIENT_ID                            = "WEWD0105";

    /** データ取得要求期間が未指定、または認識できない */
    String UNKNOWN_SPAN                            = "WEWD0106";

    /** 通知要求レベルが未指定、または認識できない */
    String UNKNOWN_ALARM_LEVEL                     = "WEWD0107";

    /** アラームの個数が未指定、またはフォーマットエラー */
    String UNKNOWN_ALARM_COUNT                     = "WEWD0108";

    /** 取得するファイル名が未指定、または認識できない */
    String UNKNOWN_FILE_NAME                       = "WEWD0109";

    /** Javelinログの取得に失敗 */
    String FAIL_GET_JVNLOG                         = "WEWD0110";

    /** 計測種別一覧が取得できない */
    String NO_MEASUREMENT_SETTING                  = "WEWD0121";

    /** SQL実行時に例外が発生 */
    String SQL_EXCEPTION                           = "EEWD0151";

    // -------------------------------------------------------------------------
    // 通信関連メッセージコード (02xx)
    // -------------------------------------------------------------------------
    /** 通信処理で例外が発生 */
    String COMMUNICATION_ERROR                     = "WEWD0201";

    /** キューサイズを超過 */
    String QUEUE_FULL                              = "WEWD0202";

    /** レスポンスが取得できない。 */
    String CANNOT_GET_RESPONCE                     = "WEWD0203";

    /** Clientに送付するメッセージ。 */
    String RESPONCE_MESSAGE_CODE                   = "DEWD0204";

    /** Comet処理で例外が発生 */
    String COMET_ERROR                             = "EEWD0205";

    /** コメットイベントのセッション状態表示。 */
    String SESSION_INFORMATION                     = "DEWD0206";

    // -------------------------------------------------------------------------
    // DataCollector関連メッセージコード (04xx)
    // -------------------------------------------------------------------------
    /** プロパティファイルが見つからない */
    String CANNOT_FIND_PROPERTY                    = "EEWD0401";

    /** ホスト情報が見つからない */
    String CANNOT_FIND_HOST                        = "EEWD0402";

    /** パラメータの解析に失敗 */
    String FAIL_TO_READ_PARAMETER                  = "EEWD0403";

    /** データベース情報の取得に失敗 */
    String FAIL_READ_DB_SETTING                    = "WEWD0404";

    /** データベース名の取得に失敗 */
    String FAIL_READ_DB_NAME                       = "WEWD0405";

    // -------------------------------------------------------------------------
    // PerformanceDoctor関連メッセージコード (05xx)
    // -------------------------------------------------------------------------
    /** Javelinログの解析に失敗 */
    String FAIL_PARSE_JVNLOG                       = "WEWD0501";

    /** サポート対象外の文字コードを指定 */
    String UNSUPPORTED_CHARSET                     = "EEWD0502";

    /** PerformaceDoctorのルールが見つからない */
    String CANNOT_FIND_PERFRULE                    = "WEWD0503";

    /** PerformaceDoctorのルール生成に失敗*/
    String FAIL_TO_CREATE_PERFRULE                 = "WEWD0504";

    /** 指定したjvnログが見つからない*/
    String UNKNOWN_LOG_FILE_NAME                   = "WEWD0505";

    // -------------------------------------------------------------------------
    // ENdoSnipeReportor関連メッセージコード (06xx)
    // -------------------------------------------------------------------------
    /** レポート出力対象のエージェントIDが未指定 */
    String UNKNOWN_REPORT_AGENT_ID                 = "EEWD0601";

    /** レポート出力対象のレポートファイルが存在しない */
    String UNKNOWN_REPORT_FILE_NAME                = "EEWD0602";

    /** レポート出力対象のレポート出力期間を表示する */
    String REPORT_FILE_DURATION                    = "DEWD0603";

    /** レポート出力期間のフォーマットが不正。 */
    String UNSUPPORTED_REPORT_FILE_DURATION_FORMAT = "WEWD0604";

}
