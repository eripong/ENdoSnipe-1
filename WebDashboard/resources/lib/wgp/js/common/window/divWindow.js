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
 * 全てのウィンドウの基底抽象クラス
 */
function divWindow(){

	// 表示物を指定
	// キー:表示物識別名
	// 値   :表示物インスタンス
	this.view_item = {};

	// 表示物の配置指定
	this.view_item_dto = {};

	// 表示物に適用するオプションを指定
	this.view_item_option = {};

	// 表示物に適用するクラス属性を指定
	this.view_item_class = {};
	
	// outline window
	this.outlineWindow_ = null;

	// 標準の大きさを規定
	this.width = "100%";
	this.height = "100%";
	
	// observer格納場所
	this.observerList_ = [];
}

/**
 * ウィンドウの内容の表示を行なう
 */
divWindow.prototype.createView = function(div_tag_id){
	var data = this.getData();

	var instance = this;

	// ビュアーの数だけ表示処理を行う。
	$.each(this.view_item, function(index, item){

		var viewItemDto = instance.view_item_dto[index];
		var viewItemClass = instance.view_item_class[index];
		var viewItemStyle = instance.view_item_style[index];

		// 適用対象のdivタグを取得する。
		var divTag = $("#" + div_tag_id);

		// divタグを新たに作成して挿入する。
		var viewItemDivId = div_tag_id +"_" + index + "_"+ viewItemDto.id;
		var viewItemDiv = "<div id='"+ viewItemDivId +"' style='width:100%; height:100%;'></div>";
		divTag.append(viewItemDiv);

		var viewItemDivTag = $("#" + viewItemDivId);

		// 幅が定義されている場合は適用する。
		if(viewItemDto.width){
			viewItemDivTag.width( viewItemDto.width );
		}

		// 高さが定義されている場合は適用する。
		if(viewItemDto.height){
			viewItemDivTag.height( viewItemDto.height );
		}

		// topが定義されている場合は適用する。
		if(viewItemDto.position_top){
			viewItemDivTag.css("top", viewItemDto.position_top + "px");
		}

		// leftが定義されている場合は適用する。
		if(viewItemDto.position_left){
			viewItemDivTag.css("left", viewItemDto.position_left + "px");
		}

		// クラス属性が適用されている場合は適用する。
		$.each(viewItemClass, function(index, itemClass){

			// 要素が配列等で定義されている場合はさらにループする。
			if(typeof(itemClass) == 'object'){
				$.each(itemClass, function(index, clazz){
					viewItemDivTag.addClass(clazz);
				});

			}else{
				viewItemDivTag.addClass(itemClass);
			}
		});		

		// スタイル属性が適用されている場合は適用する
		$.each(viewItemStyle, function(index, styleClass){
			viewItemDivTag.css(index, styleClass);
		});

		item.createViewItem(
			viewItemDivId,
			data[index],
			instance.view_item_option[index] );
	});
};

/**
 * ウィンドウの内容の表示に必要なデータを取得する。
 * ※継承先のメソッドにてオーバーライドする。
 */
divWindow.prototype.getData = function(){
};

/**
 * ウィンドウにイベントを設定する。
 */
divWindow.prototype.setEventFunction = function(){
};

/**
 * 各表示物の実体を取得する。
 */
divWindow.prototype.getViewItemEntities = function(){

	var entities = {};

	// 各表示物ごとにプロパティを設定する。
	$.each(this.view_item, function(index, item){
		entities[index] = item.getEntity();
	});

	return entities;
};

divWindow.prototype.getWindowId = function(){
	return this.window_id;
};

/**
 * add observer to window.
 * @param observer {Observer} observer object
 */
divWindow.prototype.setObserver = function(observer) {
	this.observerList_.push(observer);
	observer.setSubject(this);
};

/**
 * set outline window.
 * @param outlineWindow outlineWindow
 */
divWindow.prototype.setOutlineWindow = function(outlineWindow) {
	this.outlineWindow_ = outlineWindow;
};

/**
 * send notification to outline window.
 * @param data {Object} data
 */
divWindow.prototype.sendNotificationToOutline = function(type, data){
	if (this.outlineWindow_) {
		this.outlineWindow_.executeUpdate(this.name, type, data);
	}
};