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
// パースペクティブテーブルモデル
wgp.PerspactiveModel = Backbone.Model.extend({
	defaults : {
		// 自身に関連付けられているビューのidを保持する。
		view_div_id : null,

		// 表示領域幅
		width : 0,

		// 表示領域高さ
		height : 0,

		// 表示領域の行結合
		rowspan : 0,

		// 表示領域の列結合
		colspan : 1,

		// 表示領域内のユーティリティバーのid
		util_bar_id : null,

		// ユーティリティバー内の最小化/元に戻すボタンのid
		minimize_restore_id : null,

		// ユーティリティバー内の非表示ボタンのid
		hide_id : null,

		// 表示領域のうち、ビューをドロップ可能な領域を示すid
		drop_area_id : null,

		// 最小化されている非表示領域を表示する際に適用する幅
		restoreWidth : null,

		// 最小化されている非表示領域を表示する際に適用する高さ
		restoreHeight : null,

		// 最小化されているかどうかを示すフラグ
		minimize_flag : false,

		// 非表示かどうかを示すフラグ
		hide_flag : false,

		// 隣接するパースペクティブ領域情報
		left_up_view_array : [],
		up_view_array : [],
		right_up_view_array : [],

		left_bottom_view_array : [],
		bottom_view_array : [],
		right_up_view_array : [],

		left_view_array : [],
		right_view_array : [],
	},
	isRerationView : function() {
		if (this.get("view_div_id") && this.get("view_div_id") != "") {
			return true;
		} else {
			return false;
		}
	}
});
