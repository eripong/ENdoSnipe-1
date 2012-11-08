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
package jp.co.acroquest.endosnipe.collector;

import jp.co.acroquest.endosnipe.common.logger.CommonLogMessageCodes;

/**
 * ログメッセージのための定数クラスです。<br />
 * 
 * @author y-komori
 */
public interface LogMessageCodes extends CommonLogMessageCodes
{
    // -------------------------------------------------------------------------
    // 基本動作メッセージコード (00xx)
    // -------------------------------------------------------------------------
    String ENDOSNIPE_DATA_COLLECTOR_STARTING       = "IEDC0001";

    String ENDOSNIPE_DATA_COLLECTOR_STARTED        = "IEDC0002";

    String ENDOSNIPE_DATA_COLLECTOR_STOPPING       = "IEDC0003";

    String ENDOSNIPE_DATA_COLLECTOR_STOPPED        = "IEDC0004";

    String JAVELIN_DATA_LOGGER_STARTED             = "IEDC0005";

    String JAVELIN_DATA_LOGGER_STOPPING            = "IEDC0006";

    String JAVELIN_DATA_LOGGER_STOPPED             = "IEDC0007";

    String JAVELIN_CONNECTED                       = "IEDC0008";

    String JAVELIN_DISCONNECTED                    = "IEDC0009";

    String JAVELIN_ALREADY_CONNECTED               = "WEDC0010";

    String MAKING_DIR_FAILED                       = "EEDC0011";

    String WRITING_TEMPFILE_FAILED                 = "EEDC0012";

    String DATABASE_BASE_DIR                       = "IEDC0013";

    String DATABASE_PARAMETER                      = "IEDC0014";

    String IO_EXCEPTION_OCCURED                    = "EEDC0015";

    String DATA_COLLECTOR_SERVICE_STARTING         = "IEDC0016";

    String DATA_COLLECTOR_SERVICE_STARTED          = "IEDC0017";

    String DATA_COLLECTOR_SERVICE_STOPPING         = "IEDC0018";

    String DATA_COLLECTOR_SERVICE_STOPPED          = "IEDC0019";

    String DATA_COLLECTOR_ALREADY_STARTING         = "EEDC0020";

    String ERROR_OCCURED_ON_STARTING               = "EEDC0021";

    // -------------------------------------------------------------------------
    // 電文受信メッセージコード (01xx)
    // -------------------------------------------------------------------------
    String JVN_FILE_NOTIFY_RECEIVED                = "DEDC0101";

    String RESOURCE_NOTIFY_RECEIVED                = "DEDC0102";

    // -------------------------------------------------------------------------
    // キュー関連メッセージコード (02xx)
    // -------------------------------------------------------------------------
    /** キューにデータを追加した */
    String QUEUE_OFFERED                           = "DEDC0201";

    /** キューからデータを取り出した */
    String QUEUE_TAKEN                             = "DEDC0202";

    /** アラーム通知キューが溢れた */
    String ALARM_QUEUE_FULL                        = "WEDC0211";

    // -------------------------------------------------------------------------
    // データベース関連メッセージコード (03xx)
    // -------------------------------------------------------------------------
    /** データベースアクセス時にエラーが発生した */
    String DATABASE_ACCESS_ERROR                   = "EEDC0301";

    /** ホスト情報が見つからない */
    String CANNOT_FIND_HOST_INFO                   = "EEDC0302";

    /** Javelinログテーブルローテート実施 */
    String JAVELINLOG_ROTATE                       = "DEDC0303";

    /** Javelinログテーブルローテート失敗 */
    String JAVELINLOG_ROTATE_FAIL                  = "WEDC0304";

    /** 計測データテーブルローテート実施 */
    String MEASURELOG_ROTATE                       = "DEDC0305";

    /** 計測データテーブルローテート失敗 */
    String MEASURELOG_ROTATE_FAIL                  = "WEDC0306";

    /** すでにデータベースが使用されている */
    String DATABASE_ALREADY_USED                   = "EEDC0307";

    // -------------------------------------------------------------------------
    // DataCollector関連メッセージコード (04xx)
    // -------------------------------------------------------------------------
    /** プロパティファイルが見つからない */
    String CANNOT_FIND_PROPERTY                    = "EEDC0401";

    /** ホスト情報が見つからない */
    String CANNOT_FIND_HOST                        = "EEDC0402";

    /** パラメータの解析に失敗 */
    String FAIL_TO_READ_PARAMETER                  = "EEDC0403";

    /** 閾値判定設定ファイルの取得に失敗 */
    String CANNOT_FIND_RESOURCE_MONITORING         = "WEDC0404";

    /** 閾値判定設定ファイルのパラメータ数が不足 */
    String FAIL_MONITORING_PARAM_NUM               = "WEDC0405";

    /** 閾値判定設定ファイルのパラメータが不正 */
    String FAIL_MONITORING_PARAM_VALUE             = "WEDC0406";

    /** パラメータの解析に失敗(デフォルト値を使用) */
    String FAIL_READ_PARAMETER_USE_DEFAULT         = "WEDC0407";

    /** イベント通知クラスの初期化に失敗 */
    String FAIL_CREATE_EVENT_SENDER                = "WEDC0411";

    /** 閾値判定処理クラスの初期化に失敗 */
    String FAIL_CREATE_ALARM_PROCESSOR             = "WEDC0412";

    /** Javelinログデータの解析に失敗 */
    String FAIL_PARSE_JVN_DATA                     = "WEDC0413";

    /** リソース状態管理の設定値出力 */
    String OUTPUT_RESOURCE_MONITORING              = "IEDC0414";

    /** イベント通知スレッド開始 */
    String EVENT_NOTIFICATION_THREAD_STARTED       = "IEDC0415";

    /** イベント通知スレッド終了 */
    String EVENT_NOTIFICATION_THREAD_STOPPING      = "IEDC0416";

    /** メールテンプレートが未指定 */
    String MAIL_TEMPLATE_NOT_SPECIFYED             = "WEDC0417";

    /** メールテンプレート設定の取得に失敗 */
    String FAIL_READ_MAIL_TEMPLATE_CONFIG          = "WEDC0418";

    /** Javelin 通信用オブジェクトが取得できない */
    String CANNOT_GET_JAVELIN_COMMUNICATION_CLIENT = "EEDC0431";

    /** 設定：メール送信なし */
    String NO_SENDMAIL_CONFIG_MESSAGE              = "WEDC0451";

    /** 送信するメッセージが空 */
    String NO_SEND_INFORMATION_MESSAGE             = "WEDC0452";

    /** メール送信中に予期せぬエラーが発生 */
    String SENDING_MAIL_ERROR_MESSAGE              = "WEDC0453";

    /** 設定:SMTPサーバ未設定 */
    String SMTP_SERVER_NOT_SPECIFIED               = "WEDC0454";

    /** メールテンプレートの読み込みに失敗 */
    String FAIL_READ_MAIL_TEMPLATE                 = "WEDC0455";

    /** SMTP設定の設定値出力 */
    String OUTPUT_SMTP_SETTINGS                    = "IEDC0456";

    /** 設定：SNMPのバージョンが対応外 */
    String INVALID_SNMP_VERSION                    = "WEDC0461";

    /** SNMPトラップ送信中に予期せぬ例外が発生 */
    String SENDING_SNMP_ERROR_MESSAGE              = "WEDC0462";

    /** 設定：SNMPのバージョンが対応外 */
    String OUTPUT_SNMP_SETTINGS                    = "IEDC0463";

    /** ファイル処理において、予期しない例外が発生した場合 */
    String ERROR_FILE_HANDLER                      = "EEDC0499";

    // -------------------------------------------------------------------------
    // アラーム発生時情報収集関連メッセージコード (05xx)
    // -------------------------------------------------------------------------
    /** アラーム発生時の情報収集を開始 */
    String ALARM_DATA_COLLECT_STARTED              = "IEDC0501";

    /** アラーム発生時の情報取得を完了 */
    String ALARM_DATA_COLLECT_COMPLETED            = "IEDC0502";

    /** アラーム発生時の情報収集をすべて完了 */
    String ALARM_DATA_COLLECT_ALL_COMPLETED        = "IEDC0503";

    /** アラームIDに対応する情報取得定義が存在しない */
    String ALARM_DATA_COLLECT_NOT_DEFINED          = "DEDC0504";
}
