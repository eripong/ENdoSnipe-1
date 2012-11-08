/*****************************************************************
 WGP  1.0B  - Web Graphical Platform
   (https://sourceforge.net/projects/wgp/)

 The MIT License (MIT)
 
 Copyright (c) 2012 Acroquest Technology Co.,Ltd.
 
 Permission is hereby granted, free of charge, to any person obtaining 
 a copy of this software and associated documentation files
 (the "Software"), to deal in the Software without restriction, 
 including without limitation the rights to use, copy, modify, merge,
 publish, distribute, sublicense, and/or sell copies of the Software,
 and to permit persons to whom the Software is furnished to do so, 
 subject to the following conditions:
 
 The above copyright notice and this permission notice shall be 
 included in all copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
 SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*****************************************************************/
var wgpStyleClassConstants = function(){
};

wgpStyleClassConstants.STYLE_ADD_SETTING = {};

/** パースペクティブテーブルのスタイルクラス属性名定義 */

// ドロップエリア
wgpStyleClassConstants.PERSPACTIVE_DROP_AREA ="perspactive_drop_Area";

// ユーティリティバー
wgpStyleClassConstants.PERSPACTIVE_UTIL_BAR = "perspactive_util_bar";

wgpStyleClassConstants.PERSPACTIVE_ICON = "perspactive_icon";

// 非表示ボタン
wgpStyleClassConstants.PERSPACTIVE_UTIL_BAR_HIDE = "perspactive_util_bar_hide";

// 最小化ボタン
wgpStyleClassConstants.PERSPACTIVE_UTIL_BAR_MIN = "perspactive_util_bar_min";

// 元に戻すボタン
wgpStyleClassConstants.PERSPACTIVE_UTIL_BAR_RESTORE = "perspactive_util_bar_restore";

// ビューエリア
wgpStyleClassConstants.PERSPACTIVE_VIEW_AREA = "perspactive_view_Area";

// 全てのドロップエリアを囲むクラス
wgpStyleClassConstants.PERSPACTIVE_DROP_AREA_ALL = "perspactive_drop_Area_all";

// 特定のスタイルクラスの場合に追加するスタイルクラスの設定
wgpStyleClassConstants.STYLE_ADD_SETTING[wgpStyleClassConstants.PERSPACTIVE_DROP_AREA] = [ "ui-widget-content" ];
wgpStyleClassConstants.STYLE_ADD_SETTING[wgpStyleClassConstants.PERSPACTIVE_UTIL_BAR] = [ "ui-widget-header" ];
wgpStyleClassConstants.STYLE_ADD_SETTING[wgpStyleClassConstants.PERSPACTIVE_ICON] = [ "ui-icon" ];
wgpStyleClassConstants.STYLE_ADD_SETTING[wgpStyleClassConstants.PERSPACTIVE_UTIL_BAR_HIDE] = [ " ui-icon-circle-close"];
wgpStyleClassConstants.STYLE_ADD_SETTING[wgpStyleClassConstants.PERSPACTIVE_UTIL_BAR_MIN] = [ " ui-icon-circle-minus"];


/** ウィジェットのスタイルクラス属性名定義 */

// ウィジェット全体を囲む領域
wgpStyleClassConstants.WIDGET_AREA = "widget_area";

// ウィジェットヘッダー
wgpStyleClassConstants.WIDGET_HEADER = "widget_header";

// ウィジェットタイトル
wgpStyleClassConstants.WIDGET_TITLE = "widget_title";

// 表示物を配置する領域
wgpStyleClassConstants.WIDGET_VIEW_ITEM_AREA = "widget_view_item_area";

//特定のスタイルクラスの場合に追加するスタイルクラスの設定
wgpStyleClassConstants.STYLE_ADD_SETTING[wgpStyleClassConstants.WIDGET_HEADER] = [ "ui-widget-header" ];


/** コンテキストメニューのスタイルクラス属性名定義 */
wgpStyleClassConstants.CONTEXT_MENU = "context_menu";

//特定のスタイルクラスの場合に追加するスタイルクラスの設定
wgpStyleClassConstants.STYLE_ADD_SETTING[wgpStyleClassConstants.CONTEXT_MENU] = ["jeegoocontext", "cm_default"];

/** バーのスタイルクラス属性名定義 */
wgpStyleClassConstants.BAR_AREA = "bar_area";
wgpStyleClassConstants.BAR_ITEM_AREA = "bar_item_area";

// メニューバー固有設定
wgpStyleClassConstants.MENU_BAR_AREA = "menu_bar_area";
wgpStyleClassConstants.MENU_BAR_ITEM_AREA = "menu_bar_item_area";

// ツールバー固有設定
wgpStyleClassConstants.TOOL_BAR_AREA = "tool_bar_area";
wgpStyleClassConstants.TOOL_BAR_ITEM_AREA = "tool_bar_item_area";

wgpStyleClassConstants.STYLE_ADD_SETTING[wgpStyleClassConstants.MENU_BAR_AREA] = ["ui-widget-header"];