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
 * パースペクティブを作成するクラス
 * @returns
 */
var perspactiveTableCreator = function(){

	// パースペクティブテーブル内のドロップ領域のidのプレフィックス
	this.drop_area_prefix = "_drop";

	// パースペクティブテーブル内のユーティリティバーのidのプレフィックス
	this.util_bar_prefix = "_bar";

	// パースペクティブテーブル内の最小化/元に戻すボタン領域のidのプレフィックス
	this.minimize_restore_prefix = "_miniRestore";

	// パースペクティブテーブル内の非表示ボタン領域のidのプレフィックス
	this.hide_prefix = "_hide";

	var instance = this;
	perspactiveTableCreator = function(){
		return instance;
	};

};

/**
 * 引数に渡された情報を基に、パースペクティブを表すtable要素を作成する。
 * @param parentDiv
 */
perspactiveTableCreator.prototype.createTableTags = function(parentDivId, table){

	// 指定された親divタグを基に各領域識別用の情報を設定する。
	var util_bar_suffix = parentDivId + this.util_bar_prefix;
	var drop_area_suffix = parentDivId + this.drop_area_prefix;
	var minimize_restore_suffix = parentDivId + this.minimize_restore_prefix;
	var hide_suffix = parentDivId + this.hide_prefix;

	var tableString = "";

	// パースペクティブテーブルの内容を基にtableを構築する。
	var max_index_y = table.length;

	// 作成済みのパースペクティブ情報を保持する。
	var created_perspactive_list = [];

	// パースペクティブ全体を囲むDIVタグ生成用DTO
	var dropAreaAllDto = new wgpDomDto(
		null
		,"div"
		,null
		,[wgpStyleClassConstants.PERSPACTIVE_DROP_AREA_ALL]
		,null
	);

	for(var index_y = 0; index_y < max_index_y;  index_y++){

		var max_index_x = table[index_y].length;
		for(var index_x = 0; index_x < max_index_x; index_x++){

			// パースペクティブ情報を取得する。
			var tableViewArea = table[index_y][index_x];

			// 各種idを生成する。
			var dropDivId = drop_area_suffix + '_' + index_y + '_' + index_x;
			var utilBarId = util_bar_suffix + '_' + index_y + '_' + index_x;
			var miniRestoreIconId = minimize_restore_suffix + '_' + index_y + '_' + index_x;
			var hideIconId = hide_suffix + '_' + index_y + '_' + index_x;

			// パースペクティブ情報に追加しておく。
			tableViewArea.drop_area_id = dropDivId;
			tableViewArea.util_bar_id = utilBarId;
			tableViewArea.minimize_restore_id = miniRestoreIconId;
			tableViewArea.hide_id = hideIconId;

			// ドロップ領域を追加
			var dropAreaWidth = Number(tableViewArea.width);
			var dropAreaHeight = Number(tableViewArea.height);

			// ドロップ領域生成用DTO
			var dropAreaDto = new wgpDomDto(
				dropDivId
				,"div"
				,null
				,[wgpStyleClassConstants.PERSPACTIVE_DROP_AREA]
				,{
					width : dropAreaWidth + "px"
					,height : dropAreaHeight + "px"
				}
			);

			dropAreaAllDto.addChildren( [dropAreaDto] );

			// ユーティリティバー生成用DTO
			var utilBarDto = new wgpDomDto(
				utilBarId
				,"div"
				,null
				,[wgpStyleClassConstants.PERSPACTIVE_UTIL_BAR]
				,null
			);

			dropAreaDto.addChildren( [utilBarDto] );

			// 非表示ボタン生成用DTO
			var utilBarHideDto = new wgpDomDto(
				hideIconId
				,"div"
				,null
				,[wgpStyleClassConstants.PERSPACTIVE_ICON]
				,null
			);

			// 最小化ボタン生成用DTO
			var utilBarMiniDto = new wgpDomDto(
				miniRestoreIconId
				,"div"
				,null
				,[wgpStyleClassConstants.PERSPACTIVE_ICON]
				,null
			);

			utilBarDto.addChildren( [utilBarHideDto, utilBarMiniDto] );
		}
	}

	// 指定されたdivタグにtable要素を追加
	$("#" + parentDivId).append( wgpDomCreator.createDomStringCall(dropAreaAllDto) );
};

/**
 * デモ用に表示領域を作成するメソッド
 * TODO 本メソッドは実際の実装には不要となる予定
 */
perspactiveTableCreator.prototype.createDemo = function(){

	var viewArea1 = new perspactiveTableViewArea();
	var viewArea2 = new perspactiveTableViewArea();
	var viewArea3 = new perspactiveTableViewArea();
	var viewArea4 = new perspactiveTableViewArea();
	
	// 表示領域1
	viewArea1.width = 250;
	viewArea1.height = 800;
	viewArea1.rowspan = 2;
	viewArea1.colspan = 1;

	// 表示領域2
	viewArea2.width = 600;
	viewArea2.height = 600;
	viewArea2.rowspan = 1;
	viewArea2.colspan = 1;

	// 表示領域3
	viewArea3.width = 250;
	viewArea3.height = 600;
	viewArea3.rowspan = 1;
	viewArea3.colspan = 1;

	// 表示領域4
	viewArea4.width = 750;
	viewArea4.height = 200;
	viewArea4.rowspan = 1;
	viewArea4.colspan = 2;

	var table = [
 		[viewArea1, viewArea2, viewArea3]
 		,[viewArea4]
 	];
 	return table;
};