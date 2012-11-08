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
 * バー内の一つの要素を表すクラス
 */
function barItem(){

	// 要素のID
	this.bar_item_id = null;

	// 要素の名称
	this.bar_item_name = "barItem";

	// 要素のタイトル
	this.bar_item_title = null;

	// 要素の属性
	this.bar_item_attributes = {};

	// 要素のスタイルクラス
	this.bar_item_class = [];

	// 要素のスタイル属性
	this.bar_item_styles = {};
}

/**
 * バーに表示する要素を作成する。
 */
barItem.prototype.createViewDto = function(
	bar_id
	,bar_item_add_class){

	var bar_item_class =
		this.bar_item_class.concat(bar_item_add_class)
		.concat([wgpStyleClassConstants.BAR_ITEM_AREA]);

	this.bar_item_id = bar_id + this.bar_item_name;
	var berItemDto = new wgpDomDto(
		this.bar_item_id
		,"div"
		,this.bar_item_attributes
		,bar_item_class
		,this.bar_item_styles
	);
	berItemDto.addChildren([ this.getContents() ]);
	return wgpDomCreator.createDomStringCall(berItemDto);
};

/**
 * バーの表示内容を返却する。
 */
barItem.prototype.getContents = function(){
	return this.bar_item_title;
};

/**
 * テキストのメニュー内容に対するホバーイベント設定
 * @param barItemId　バー要素のID
 */
barItem.prototype.setTextHoverEventFunction = function(barItemId){

	$("#" + barItemId).bind('mouseover',function(e) {
		$(e.target).addClass("ui-state-hover");

	}).bind('mouseout',function(e) {
		$(e.target).removeClass("ui-state-hover");
	});
};