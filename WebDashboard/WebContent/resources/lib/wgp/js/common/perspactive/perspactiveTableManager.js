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
 * パースペクティブを管理するクラス。
 * @returns
 */
perspactiveTableManager = function(){

	// パースペクティブを作成するクラス
	this.tableCreator = new perspactiveTableCreator();
	this.table;

	var instance = this;
	perspactiveTableManager = function(){
		return instance;
	};
};

/**
 * パースペクティブテーブルの初期化処理を行う。
 */
perspactiveTableManager.prototype.initializePerspactiveTable = function(parentDivId, table){

	// パースペクティブテーブルの実体となるテーブル要素を作成する。
	this.tableCreator.createTableTags(parentDivId, table);

	// パースペクティブテーブル情報を作成し、イベントを適用する。
	this.table = new perspactiveTable(table);
	this.table.prepareTable();
	this.table.setPerspactiveEvent();
};

/**
 * パースペクティブに特定のビューを関連付ける。
 * ※指定したビューが存在しない場合は新規作成する。
 */
perspactiveTableManager.prototype.dropView = function(droppableTargetId, viewId){

	var viewDiv = $("#" + viewId);
	if(viewDiv.length == 0){
		var viewAreaDto = new wgpDomDto(
			viewId
			,"div"
			,null
			,[wgpStyleClassConstants.PERSPACTIVE_VIEW_AREA]
			,null
		);

		$("#" + droppableTargetId).append( wgpDomCreator.createDomStringCall(viewAreaDto) );
	}else{
		$("#" + droppableTargetId).append( viewDiv );
	}

	this.table.dropEventFunction(droppableTargetId, viewId);
};