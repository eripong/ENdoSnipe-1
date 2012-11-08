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
 * メニューバーを表すクラス
 */
var menuBar = function(){

	// バー判別用のID
	this.bar_id = null;

	// バーの名称
	this.bar_name = "menuBar";

	// バーに対して適用するスタイルクラス
	this.bar_class = [ wgpStyleClassConstants.BAR_AREA, wgpStyleClassConstants.MENU_BAR_AREA];

	// バー内に表示する内容
	this.bar_item = {
		"windowBarItem" : new windowBarItem()
		,"helpBarItem" : new helpBarItem()
	};

	// バー内に表示する内容に対して適用するスタイルクラス
	this.bar_item_class =
		[ wgpStyleClassConstants.BAR_ITEM_AREA, wgpStyleClassConstants.MENU_BAR_ITEM_AREA ];
};
menuBar.prototype = new bar();