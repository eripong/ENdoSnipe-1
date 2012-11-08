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

/**
 * イベントの定数クラスです。
 * @author fujii
 * @author ochiai
 *
 */
public interface EventConstants
{
    /** イベントID:agent(javelin)の取得要求 */
    int    EVENT_GET_AGENT_INFO                   = 0;

    /** イベントID:計測項目自動通知開始要求 */
    int    EVENT_START_AUTO_MEASUREMENT           = 11;

    /** イベントID:計測項目自動通知停止要求 */
    int    EVENT_STOP_AUTO_MEASUREMENT            = 12;

    /** イベントID:計測項目自動通知終了要求 */
    int    EVENT_END_AUTO_MEASUREMENT             = 121;

    /** イベントID:計測項目更新通知 */
    int    EVENT_NOTIFY_MEASUREMENT_ITEM          = 13;

    /** イベントID:レスポンス保持要求 */
    int    EVENT_STORE_RESPONSE                   = 14;

    /** イベントID:指定期間計測項目取得要求 */
    int    EVENT_NOTIFY_TERM_MEASUREMENT_REQUEST  = 31;

    /** イベントID:指定期間計測項目取得応答 */
    int    EVENT_NOTIFY_TERM_MEASUREMENT_RESPONSE = 32;

    /** イベントID:アラーム通知開始要求 */
    int    EVENT_START_ALARM_NOTIFY               = 41;

    /** イベントID:アラーム通知終了要求 */
    int    EVENT_STOP_ALARM_NOTIFY                = 42;

    /** イベントID:アラーム更新通知 */
    int    EVENT_NOTIFY_ALARM_ITEM                = 43;

    /** イベントID:指定期間アラーム通知取得要求 */
    int    EVENT_TERM_NOTIFY_ALARM_REQUEST        = 44;

    /** イベントID:指定期間アラーム通知応答 */
    int    EVENT_TERM_NOTIFY_ALARM_RESPONSE       = 45;

    /** イベントID:閾値超過アラーム開始要求 */
    int    EVENT_START_RESOURCE_ALARM             = 46;

    /** イベントID:閾値超過アラーム終了要求 */
    int    EVENT_STOP_RESOURCE_ALARM              = 47;

    /** イベントID:閾値超過アラーム更新通知 */
    int    EVENT_NOTIFY_RESOURCE_ALARM            = 48;

    /** イベントID:全アラームレベル取得要求 */
    int    EVENT_RESOURCE_STATE_ALL_REQUEST       = 490;

    /** イベントID:全アラームレベル取得応答 */
    int    EVENT_RESOURCE_STATE_ALL_RESPONSE      = 491;

    /** イベントID:エージェント一覧取得要求 */
    int    EVENT_AGENT_LIST_REQUEST               = 51;

    /** イベントID:エージェント一覧取得応答 */
    int    EVENT_AGENT_LIST_RESPONSE              = 52;

    /** イベントID:レスポンス削除要求 */
    int    EVENT_DELETE_RESPONSE                  = 99;

    /** イベントID:レポート一覧取得要求 */
    int    EVENT_REPORT_LIST_REQUEST              = 61;

    /** イベントID:レポート一覧取得応答 */
    int    EVENT_REPORT_LIST_RESPONSE             = 62;

    /** 項目ID:イベントID */
    String EVENT_ID                               = "event_id";

    /** 項目ID:通知先グラフID */
    String GRAPH_ID                               = "graph_id";

    /** 項目ID:通知要求エージェントID */
    String AGENT_IDS                              = "agent_ids";

    /** 項目ID:通知要求計測ID */
    String MEASUREMENT_TYPES                      = "measurement_types";

    /** 項目ID:クライアントID */
    String CLIENT_ID                              = "client_id";

    /** 項目ID:データ取得要求期間 */
    String SPAN                                   = "span";

    /** 項目ID:通知要求レベル(INFO, WARN, ERROR) */
    String ALARM_LEVEL                            = "alarm_level";

    /** 項目ID:アラーム取得個数 */
    String ALARM_COUNT                            = "alarm_count";

    /** 項目ID:エージェントID */
    String AGENT_ID                               = "agent_id";

    /** 項目ID:項目名 */
    String ITEM_NAME                              = "item_name";

    /** 項目ID:ファイル名 */
    String REPORT_FILE_NAME                       = "file_name";

    /** 項目ID:エージェントID */
    String LOG_FILE_NAME                          = "log_file_name";

    /** 文字コードを識別するための文字列 */
    String CHAR_SET_CODE                          = "charset";

    /** javascript指定用の固定値 */
    String JAVASCRIPT_SETTING_CODE                = "application/javascript; ";

    /** html指定用の固定値 */
    String HTML_SETTING_CODE                      = "text/html; ";

}
