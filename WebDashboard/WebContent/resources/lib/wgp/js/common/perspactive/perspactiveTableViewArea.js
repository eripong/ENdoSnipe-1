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
/**
 * パースペクティブ内の表示領域を表すクラス
 */
function perspactiveTableViewArea(){

	// 自身に関連付けられているビューのidを保持する。
	this.view_div_id;

	// 表示領域幅
	this.width = 0;

	// 表示領域高さ
	this.height = 0;

	// 表示領域の行結合
	this.rowspan = 1;

	// 表示領域の列結合
	this.colspan = 1;

	// 表示領域内のユーティリティバーのid
	this.util_bar_id;

	// ユーティリティバー内の最小化/元に戻すボタンのid
	this.minimize_restore_id;

	// ユーティリティバー内の非表示ボタンのid
	this.hide_id;

	// 表示領域のうち、ビューをドロップ可能な領域を示すid
	this.drop_area_id;

	// 最小化されている非表示領域を表示する際に適用する幅
	this.restoreWidth;

	// 最小化されている非表示領域を表示する際に適用する高さ
	this.restoreHeight;

	// 最小化されているかどうかを示すフラグ
	this.minimize_flag = false;

	// 非表示かどうかを示すフラグ
	this.hide_flag = false;

	// 隣接するパースペクティブ領域情報
	this.left_up_view_array = [];
	this.up_view_array = [];
	this.right_up_view_array = [];

	this.left_bottom_view_array = [];
	this.bottom_view_array = [];
	this.right_up_view_array = [];

	this.left_view_array = [];
	this.right_view_array = [];
};

/**
 * パースペクティブにビューが関連付けられているかどうかを返却する。
 */
perspactiveTableViewArea.prototype.isRerationView = function(){

	if(this.view_div_id && this.view_div_id != ""){
		return true;
	}else{
		return false;
	}
};