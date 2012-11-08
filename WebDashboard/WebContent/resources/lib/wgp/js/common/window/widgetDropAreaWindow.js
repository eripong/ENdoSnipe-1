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
var widgetDropAreaWindow = function(){

	// ウィジェットドロップ領域ウィンドウ
	this.name = "widgetDropAreaWindow";

	var dropAreaViewItemDto = new viewItemDto();
	dropAreaViewItemDto.id=1;
	dropAreaViewItemDto.height = "100%";
	dropAreaViewItemDto.width = "100%";
	dropAreaViewItemDto.position_top = 0;
	dropAreaViewItemDto.position_left = 0;


	// 表示物を指定(ドロップ領域表示物)
	this.view_item = { "dropAreaViewItem" : new dropAreaViewItem()};
	this.view_item_dto = {
		"dropAreaViewItem" : dropAreaViewItemDto
	};

	// 表示物に適用するオプションを指定
	this.view_item_option = {};

	// 表示物に適用するクラス属性を指定
	this.view_item_class = {
		"dropAreaViewItem" : ["jstree-drop"]
	};

	// 表示物に適用するスタイルを指定
	this.view_item_style = {
		"dropAreaViewItem" : {
			"overflow" : "auto",
			"position" : "relative"
		}
	};
};
widgetDropAreaWindow.prototype = new divWindow();

widgetDropAreaWindow.prototype.getData = function(){
	return {"dropAreaViewItem" : {}};
};

widgetDropAreaWindow.prototype.setEventFunction = function(){

	var divId = this.view_item["dropAreaViewItem"].id;
	$("#" + divId).droppable({
		accept : ".widget_class",
		drop : function(e, ui){
			console.log("ドロップ");
		}
	});
};