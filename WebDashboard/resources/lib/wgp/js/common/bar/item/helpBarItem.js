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
function helpBarItem(){

	// 要素のID
	this.bar_item_id = null;

	// 要素の名称
	this.bar_item_name = "help_bar";

	// 要素のタイトル
	this.bar_item_title = "Help";

	// 要素の属性
	this.bar_item_attributes = {};

	// 要素のスタイルクラス
	this.bar_item_class = [];

	// 要素のスタイル属性
	this.bar_item_styles = {};
}
helpBarItem.prototype = new barItem();

/**
 * バー要素にイベントを設定する。
 */
helpBarItem.prototype.setEventFunction = function(){

	// クリックされた際に表示するコンテキストメニューを設定する。
	var contextMenu0 = new contextMenu("homePage", "Home Page");
	var contextMenu1 = new contextMenu("aboutWgp", "About WGP");

	var contextMenuArray = [ contextMenu0, contextMenu1 ];


	// コンテキストメニューを初期化
	contextMenuCreator.initializeContextMenu(this.bar_item_name, contextMenuArray);

	// クリック時に表示するコンテキストメニューを生成
	var instance = this;
	var option = {
		event : "click"
		,openBelowContext : true
		,onSelect : function(event, target){
			if(event.currentTarget.id == "aboutWgp"){
				$("<div><span style='text-align:middle'>Welcom WGP Project...<br/>Version 0.1</span></div>").dialog({"title" : "About WGP"});

			}else if(event.currentTarget.id == "homePage"){
				document.location = "http://wgp.sourceforge.net";
			}
		}
	};
	contextMenuCreator.createContextMenu(this.bar_item_id, this.bar_item_name, option);

	// ホバーイベントを設定
	this.setTextHoverEventFunction(this.bar_item_id);
};