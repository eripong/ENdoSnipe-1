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
wgp.styleClassConstants = {};

wgp.styleClassConstants.STYLE_ADD_SETTING = {};

/** パースペクティブテーブルのスタイルクラス属性名定義 */

// ドロップエリア
wgp.styleClassConstants.PERSPECTIVE_DROP_AREA ="perspective_drop_Area";

// ユーティリティバー
wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR = "perspective_util_bar";

wgp.styleClassConstants.PERSPECTIVE_ICON = "perspective_icon";

// 非表示ボタン
wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR_HIDE = "perspective_util_bar_hide";

// 最小化ボタン
wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR_MIN = "perspective_util_bar_min";

// 元に戻すボタン
wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR_RESTORE = "perspective_util_bar_restore";

// ビューエリア
wgp.styleClassConstants.PERSPECTIVE_VIEW_AREA = "perspective_view_Area";

// 全てのドロップエリアを囲むクラス
wgp.styleClassConstants.PERSPECTIVE_DROP_AREA_ALL = "perspective_drop_Area_all";

// 特定のスタイルクラスの場合に追加するスタイルクラスの設定
wgp.styleClassConstants.STYLE_ADD_SETTING[wgp.styleClassConstants.PERSPECTIVE_DROP_AREA] = [ "ui-widget-content" ];
wgp.styleClassConstants.STYLE_ADD_SETTING[wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR] = [ "ui-widget-header", "wgp-tabs" ];
wgp.styleClassConstants.STYLE_ADD_SETTING[wgp.styleClassConstants.PERSPECTIVE_ICON] = [ "ui-icon" ];
wgp.styleClassConstants.STYLE_ADD_SETTING[wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR_HIDE] = [ " ui-icon-circle-close"];
wgp.styleClassConstants.STYLE_ADD_SETTING[wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR_MIN] = [ " ui-icon-circle-minus"];


/** ウィジェットのスタイルクラス属性名定義 */

// ウィジェット全体を囲む領域
wgp.styleClassConstants.WIDGET_AREA = "widget_area";

// ウィジェットヘッダー
wgp.styleClassConstants.WIDGET_HEADER = "widget_header";

// ウィジェットタイトル
wgp.styleClassConstants.WIDGET_TITLE = "widget_title";

// 表示物を配置する領域
wgp.styleClassConstants.WIDGET_VIEW_ITEM_AREA = "widget_view_item_area";

//特定のスタイルクラスの場合に追加するスタイルクラスの設定
wgp.styleClassConstants.STYLE_ADD_SETTING[wgp.styleClassConstants.WIDGET_HEADER] = [ "ui-widget-header" ];


/** コンテキストメニューのスタイルクラス属性名定義 */
wgp.styleClassConstants.CONTEXT_MENU = "context_menu";

//特定のスタイルクラスの場合に追加するスタイルクラスの設定
wgp.styleClassConstants.STYLE_ADD_SETTING[wgp.styleClassConstants.CONTEXT_MENU] = ["jeegoocontext", "cm_default"];

/** バーのスタイルクラス属性名定義 */
wgp.styleClassConstants.BAR_AREA = "bar_area";
wgp.styleClassConstants.BAR_ITEM_AREA = "bar_item_area";

// メニューバー固有設定
wgp.styleClassConstants.MENU_BAR_AREA = "menu_bar_area";
wgp.styleClassConstants.MENU_BAR_ITEM_AREA = "menu_bar_item_area";

// ツールバー固有設定
wgp.styleClassConstants.TOOL_BAR_AREA = "tool_bar_area";
wgp.styleClassConstants.TOOL_BAR_ITEM_AREA = "tool_bar_item_area";

wgp.styleClassConstants.STYLE_ADD_SETTING[wgp.styleClassConstants.MENU_BAR_AREA] = ["ui-widget-header"];