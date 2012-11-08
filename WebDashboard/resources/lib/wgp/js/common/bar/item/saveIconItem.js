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
function saveIconItem(){

	// 要素のID
	this.bar_item_id = null;

	// 要素の名称
	this.bar_item_name = "save_icon_bar";

	// 要素のタイトル
	this.bar_item_title = "save";

	// 要素のスタイルクラス
	this.bar_item_class = [];

	// 要素のスタイル属性
	this.bar_item_styles = {};
	
}
saveIconItem.prototype = new barItem();

saveIconItem.prototype.getContents = function(){
	return "<image src='resources/img/icon/saveIcon.png' style='width:20px; height: 20px;' >";
};

saveIconItem.prototype.setEventFunction = function(){

	// イベントを移動させる。
	$("#" + this.bar_item_id).bind("click",
		function(event){
		var divWindowManager_ = new divWindowManager();
		// TODO マップエリアのIDを取得する。
		var windowObject = divWindowManager_.getWindow(1);
		var saveData = windowObject.getAllObjectData("mapAreaViewItem");
		var ajaxHandler = new AjaxHandler();
		var data = {
			saveData : JSON.stringify(saveData),
			dataType : "MAP",
			dataId : "cep"
		};
		var settings = {
			url : common.getContextPath() + "/edit/saveData/",
			data : data
		};
//		settings[ConnectionConstants.SUCCESS_CALL_OBJECT_KEY] = this;
//		settings[ConnectionConstants.SUCCESS_CALL_FUNCTION_KEY] = "callbackSaveData";
		ajaxHandler.requestServerAsync(settings);
		}
	);
};