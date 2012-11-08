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
 * 全てのウィジェットの基底抽象クラス
 */
function widget(){

	// ウィジェット判別用のID
	this.widget_id = null;

	// 表示物を指定
	// キー:表示物識別名
	// 値   :表示物インスタンス
	this.view_item = {};

	// 表示物の配置指定
	this.view_item_dto = {};

	// 表示物に適用するクラスを指定
	this.view_item_class = {};

	// 標準の大きさを規定
	this.width = 200;
	this.height = 150;

	// 表示物に適用するオプションを指定
	this.view_item_option = {
		width : this.width
		,height : this.height
	};

	// ウィジェット全体を囲むDIVタグのID
	this.area_id;

	// ウィジェットヘッダーのID
	this.header_id;

	// ウィジェットタイトルのID
	this.title_id;

	// 表示物を配置するDIVタグのID
	this.view_item_area_id;
};

/**
 * ウィジェットの内容の表示を行なう
 */
widget.prototype.createView = function(div_tag_id){
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
		var viewItemDiv = "<div id='"+ viewItemDivId +"'></div>";
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
			div_tag_id,
			data[index],
			instance.view_item_option[index] );
	});
};

/**
 * ウィジェットの内容の表示に必要なデータを取得する。
 * ※継承先のメソッドにてオーバーライドする。
 */
widget.prototype.getData = function(){
};

/**
 * ウィジェットにイベントを設定する。
 * @param widgetObject ウィジェット本体
 */
widget.prototype.setEventFunction = function(){
	var widget_area_id_str = "#widget_area_" + this.widget_id;
	var cancel_div_str = "#div_widgetwrapper_" + this.widget_id;

	var startWidth;
	var startHeight;
	var instance = this;

	$(widget_area_id_str).resizable({
		minWidth : 100,
		minHeight : 50,
		containment : "document",
		helper : 'ui-resizable-helper',
		start : function(event, ui){

			// 最前面に出るようにzIndexを設定する。
			common.moveEndFront(widget_area_id_str);

			// リサイズ開始時の値を取得する。
			startWidth = $("#" + event.target.id).width();
			startHeight = $("#" + event.target.id).height();
		}
		,stop : function(event, ui){
			var diff_x = ui.size.width - ui.originalSize.width;
			var diff_y = ui.size.height - ui.originalSize.height;

			var changeWidth = $("#" + event.target.id).width() - startWidth;
			var changeHeight = $("#" + event.target.id).height() - startHeight; 

			var cancelDivWidth = $(cancel_div_str).width();
			var cancelDivHeight = $(cancel_div_str).height();

			$(cancel_div_str).width( cancelDivWidth + changeWidth );
			$(cancel_div_str).height( cancelDivHeight + changeHeight );

			$.each(instance.view_item , function(index, item){
				if(item.resize){
					item.resize( changeWidth, changeHeight );
				}
			});
		}
	}) .draggable({
		cancel : cancel_div_str,
		grid : [10, 10],
		cursor : 'move',
		containment : "parent",
		scroll : true,
		start : function (event,ui) {

			// 最前面に出るようにzIndexを設定する。
			common.moveEndFront(widget_area_id_str);
		}		
	});
	$(widget_area_id_str + ", div").disableSelection();
};

/**
 * ウィジェットのコンテキストメニューを選択した際の処理
 * (子クラスにてオーバーライドを強く推奨)
 */
widget.prototype.onSelectContextMenu = function(event, target){
	var eventName = $(event.target).attr("id");

	// 「削除」を選択した場合
	if(eventName == "delete"){
		this.deleteWithContextMenu(target);
	}
};

/**
 * コンテキストメニュー「削除」メニュー押下時の処理
 */
widget.prototype.deleteWithContextMenu = function(target){
	$("#" + target.id).remove();
	delete this;
};

/**
 * ウィジェットのプロパティ画面を開く。
 * @param target
 * @param url
 */
widget.prototype.openWidgetPropertyCommon = function(target, url, title){

	var param = {};
	var properties = this.getProperties();
	$.each(properties, function(index, property){
		param[index] = JSON.stringify(property);
	});

	var queryString = common.createQueryString(param);
	var width = "800px";
	var height = "500px";

	var properties = window.showModalDialog(
		url + queryString,
		title,
		"dialogWidth:" + width + ";" + "dialogHeight:" + height);

	this.setProperties(properties);
};

/**
 * 各表示物のプロパティを全て取得する。
 */
widget.prototype.getProperties = function(){
	var properties = {};
	// 各表示物ごとにプロパティを取得する。
	$.each(this.view_item, function(index, item){
		properties[index] = item.getProperty();
	});
	return properties;
};

/**
 * 各表示物のプロパティを設定する。
 * @param properties プロパティ
 */
widget.prototype.setProperties = function(properties){

	// 各表示物ごとにプロパティを設定する。
	$.each(this.view_item, function(index, item){
		if(properties[index]){
			item.setProperty(properties[index]);
		}
	});
};

/**
 * 各表示物の実体を取得する。
 */
widget.prototype.getViewItemEntities = function(){

	var entities = {};

	// 各表示物ごとにプロパティを設定する。
	$.each(this.view_item, function(index, item){
		entities[index] = item.getEntity();
	});

	return entities;
};

/**
 * ウィジェットを指定の位置に移動する。
 */
widget.prototype.move = function(top, left){
	var widget_area_id_str = "#widget_area_" + this.widget_id;
	$(widget_area_id_str).css("top", top + "px");
	$(widget_area_id_str).css("left", left + "px");
};