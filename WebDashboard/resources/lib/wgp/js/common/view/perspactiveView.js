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
// パースペクティブビュー
wgp.PerspactiveView = Backbone.View.extend({
	initialize : function(arguments) {
		_.bindAll()

		// パースペクティブテーブル内のドロップ領域のidのプレフィックス
		this.drop_area_prefix = "_drop";

		// パースペクティブテーブル内のユーティリティバーのidのプレフィックス
		this.util_bar_prefix = "_bar";

		// パースペクティブテーブル内の最小化/元に戻すボタン領域のidのプレフィックス
		this.minimize_restore_prefix = "_miniRestore";

		// パースペクティブテーブル内の非表示ボタン領域のidのプレフィックス
		this.hide_prefix = "_hide";

		this.id = this.$el.attr("id");
		this.collection = arguments["collection"];
		
		// レンダリングメソッドを実行
		this.render();
	},
	render : function() {
		this.createTableTags(this.id, this.collection);
		this.tableInitial(this.collection);
		this.prepareTable();
		this.setPerspactiveEvent();
	},
	createTableTags : function(parentDivId, table) {
		// 指定された親divタグを基に各領域識別用の情報を設定する。
		var util_bar_suffix = parentDivId + this.util_bar_prefix;
		var drop_area_suffix = parentDivId + this.drop_area_prefix;
		var minimize_restore_suffix = parentDivId
				+ this.minimize_restore_prefix;
		var hide_suffix = parentDivId + this.hide_prefix;

		var tableString = "";

		// パースペクティブテーブルの内容を基にtableを構築する。
		var max_index_y = table.length;

		// 作成済みのパースペクティブ情報を保持する。
		var created_perspactive_list = [];

		// パースペクティブ全体を囲むDIVタグ生成用DTO
		var dropAreaAllDto = new wgpDomDto(null, "div", null,
				[ wgpStyleClassConstants.PERSPACTIVE_DROP_AREA_ALL ],
				null);

		for ( var index_y = 0; index_y < max_index_y; index_y++) {

			var max_index_x = table[index_y].length;
			for ( var index_x = 0; index_x < max_index_x; index_x++) {

				// パースペクティブ情報を取得する。
				var tableModel = table[index_y][index_x];

				// 各種idを生成する。
				var dropDivId = drop_area_suffix + '_' + index_y + '_'
						+ index_x;
				var utilBarId = util_bar_suffix + '_' + index_y + '_'
						+ index_x;
				var miniRestoreIconId = minimize_restore_suffix + '_'
						+ index_y + '_' + index_x;
				var hideIconId = hide_suffix + '_' + index_y + '_'
						+ index_x;

				// パースペクティブ情報に追加しておく。
				var modelSet = {
					drop_area_id : dropDivId,
					util_bar_id : utilBarId,
					minimize_restore_id : miniRestoreIconId,
					hide_id : hideIconId
				};
				tableModel.set(modelSet);

				// ドロップ領域を追加
				var dropAreaWidth = Number(tableModel.get("width"));
				var dropAreaHeight = Number(tableModel.get("height"));

				// ドロップ領域生成用DTO
				var dropAreaDto = new wgpDomDto(
						dropDivId,
						"div",
						null,
						[ wgpStyleClassConstants.PERSPACTIVE_DROP_AREA ],
						{
							width : dropAreaWidth + "px",
							height : dropAreaHeight + "px"
						});

				dropAreaAllDto.addChildren([ dropAreaDto ]);

				// ユーティリティバー生成用DTO
				var utilBarDto = new wgpDomDto(
						utilBarId,
						"div",
						null,
						[ wgpStyleClassConstants.PERSPACTIVE_UTIL_BAR ],
						null);

				dropAreaDto.addChildren([ utilBarDto ]);

				// 非表示ボタン生成用DTO
				var utilBarHideDto = new wgpDomDto(hideIconId, "div",
						null,
						[ wgpStyleClassConstants.PERSPACTIVE_ICON ],
						null);

				// 最小化ボタン生成用DTO
				var utilBarMiniDto = new wgpDomDto(miniRestoreIconId,
						"div", null,
						[ wgpStyleClassConstants.PERSPACTIVE_ICON ],
						null);

				utilBarDto
						.addChildren([ utilBarHideDto, utilBarMiniDto ]);
			}
		}

		// 指定されたdivタグにtable要素を追加
		$("#" + parentDivId).append(
				wgpDomCreator.createDomStringCall(dropAreaAllDto));
	},
	tableInitial : function(perspactiveInformation) {
		var table = this.collection;

		// パースペクティブテーブル情報を基にテーブルの段組情報を解釈しやすい形に変更する。
		var persepactiveYMax = perspactiveInformation.length;
		var persepactiveXMax = 0;
		for ( var index_y = 0; index_y < persepactiveYMax; index_y++) {
			if (persepactiveXMax < perspactiveInformation[index_y].length) {
				persepactiveXMax = perspactiveInformation[index_y].length;
			}
		}

		// あらかじめ必要な分の配列を生成しておく。
		var table = new Array(persepactiveYMax);
		for ( var index_y = 0; index_y < persepactiveYMax; index_y++) {
			table[index_y] = new Array(persepactiveXMax);
		}

		// パースペクティブテーブル情報を解釈しやすい形に再構成する。
		for ( var index_y = 0; index_y < persepactiveYMax; index_y++) {
			for ( var index_x = 0; index_x < perspactiveInformation[index_y].length; index_x++) {

				// パースペクティブ領域情報を取得する。
				var tableModel = perspactiveInformation[index_y][index_x];

				var findFlag = false;
				for ( var tableIndex_y = 0; tableIndex_y < table.length;) {
					for ( var tableIndex_x = 0; tableIndex_x < table[tableIndex_y].length;) {
						if (table[tableIndex_y][tableIndex_x] == undefined) {
							findFlag = true;
						}

						if (findFlag) {
							break;
						} else {
							tableIndex_x++;
						}
					}

					if (findFlag) {
						break;
					} else {
						tableIndex_y++;
					}
				}

				for ( var rowspanIndex = 0; rowspanIndex < tableModel.get("rowspan"); rowspanIndex++) {
					for ( var colspanIndex = 0; colspanIndex < tableModel.get("colspan"); colspanIndex++) {
						table[tableIndex_y + rowspanIndex][tableIndex_x
								+ colspanIndex] = tableModel;
					}
				}

				// パースペクティブ領域情報に最終行番号及び最終列番号を加える。
				var modelSet = {
					first_index_y : tableIndex_y,
					first_index_x : tableIndex_x,
					last_index_y : tableIndex_y + rowspanIndex - 1,
					last_index_x : tableIndex_x + colspanIndex - 1
				}
				tableModel.set(modelSet);
			}
		}

		// 隣接するパースペクティブ領域に関する情報を設定する。
		var alreadySetting = {};
		for ( var index_y = 0; index_y < table.length; index_y++) {
			for ( var index_x = 0; index_x < table[index_y].length; index_x++) {

				var targetTableModel = table[index_y][index_x];
				if (alreadySetting[targetTableModel.get("drop_area_id")] == true) {
					continue;
				}
				alreadySetting[targetTableModel.get("drop_area_id")] = true;

				// 左上方向・上方向・右上方向に隣接するパースペクティブ領域
				var left_up_view_array = [];
				var up_view_array = [];
				var right_up_view_array = [];

				// 左下方向・下方向･右下方向に隣接するパースペクティブ領域
				var left_bottom_view_array = [];
				var bottom_view_array = [];
				var right_bottom_view_array = [];

				// 左方向に隣接するパースペクティブ領域
				var left_view_array = [];

				// 右方向に隣接するパースペクティブ領域
				var right_view_array = [];

				var alreadySettingLoop = {};
				for ( var temp_index_y = 0; temp_index_y < table.length; temp_index_y++) {
					for ( var temp_index_x = 0; temp_index_x < table[temp_index_y].length; temp_index_x++) {

						var indexTableModel = table[temp_index_y][temp_index_x];
						if (indexTableModel.get("drop_area_id") == targetTableModel.get("drop_area_id")
								|| alreadySettingLoop[indexTableModel.drop_area_id] == true) {
							continue;
						}
						alreadySettingLoop[indexTableModel.get("drop_area_id")] = true;

						// 左上方向に隣接する場合
						if (targetTableModel.get("first_index_y") - 1 == indexTableModel.get("last_index_y")
								&& targetTableModel.get("first_index_x") - 1 == indexTableModel.get("last_index_x")) {
							left_up_view_array.push(indexTableModel);
							continue;
						}

						// 右上方向に隣接する場合
						if (targetTableModel.get("first_index_y") - 1 == indexTableModel.get("last_index_y")
								&& targetTableModel.get("last_index_x") + 1 == indexTableModel.get("first_index_x")) {
							right_up_view_array.push(indexTableModel);
							continue;
						}

						// 左下方向に隣接する場合
						if (targetTableModel.get("last_index_y") + 1 == indexTableModel.get("first_index_y")
								&& targetTableModel.get("first_index_x") - 1 == indexTableModel.get("last_index_x")) {
							left_bottom_view_array.push(indexTableModel);
							continue;
						}

						// 右下方向に隣接する場合
						if (targetTableModel.get("last_index_y") + 1 == indexTableModel.get("first_index_y")
								&& targetTableModel.get("last_index_x") + 1 == indexTableModel.get("first_index_x")) {
							right_bottom_view_array.push(indexTableModel);
							continue;
						}

						// 上方向に隣接する場合
						if (targetTableModel.get("first_index_y") - 1 == indexTableModel.get("last_index_y")) {
							up_view_array.push(indexTableModel);
							continue;
						}

						// 下方向に隣接する場合
						if (targetTableModel.get("last_index_y") + 1 == indexTableModel.get("first_index_y")) {
							bottom_view_array.push(indexTableModel);
							continue;
						}

						// 左方向に隣接する場合
						if (targetTableModel.get("first_index_x") - 1 == indexTableModel.get("last_index_x")) {
							left_view_array.push(indexTableModel);
							continue;
						}

						// 右方向に隣接する場合
						if (targetTableModel.get("last_index_x") + 1 == indexTableModel.get("first_index_x")) {
							right_view_array.push(indexTableModel);
							continue;
						}
					}
				}

				// 隣接するパースペクティブ領域の情報を設定
				var modelSet = {
					left_up_view_array : left_up_view_array,
					up_view_array : up_view_array,
					right_up_view_array : right_up_view_array,

					left_bottom_view_array : left_bottom_view_array,
					bottom_view_array : bottom_view_array,
					right_bottom_view_array : right_bottom_view_array,

					left_view_array : left_view_array,
					right_view_array : right_view_array
				}
				targetTableModel.set(modelSet);
			}
		}

		// 生成したパースペクティブテーブルを2次元配列としてコレクションに格納しなおす。
		this.collection = table;
		this.targetIndex_x;
		this.targetIndex_y;

		this.max_width = 0;
		this.max_height = 0;

		// パースペクティブの最大幅を取得する。
		alreadySetting = {};
		for ( var index_x = 0; index_x < this.collection[0].length; index_x++) {
			var indexTableModel = this.collection[0][index_x];

			if (alreadySetting[indexTableModel.get("drop_area_id")] != true) {
				this.max_width = this.max_width + indexTableModel.get("width");
				alreadySetting[indexTableModel.get("drop_area_id")] = true;
			}
		}

		// パースペクティブの最大高さを取得する。
		alreadySetting = {};
		for ( var index_y = 0; index_y < this.collection.length; index_y++) {
			var indexTableModel = this.collection[index_y][0];

			if (alreadySetting[indexTableModel.get("drop_area_id")] != true) {
				this.max_height = this.max_height
						+ indexTableModel.get("height");
				alreadySetting[indexTableModel.get("drop_area_id")] = true;
			}
		}

		// ドロップ可能なウィンドウの定義
		this.droppableClass = "perspactive_window";

		// 最小化時の幅
		this.minimize_width = 30;

		// 最小化時の高さ
		this.minimize_height = 20;
	},
	prepareTable : function() {
		var table = this.collection;
		var alreadySetting = {};

		var max_index_y = table.length;

		// zインデックス初期値
		var zIndex = 10;
		var zIndex_margin = 10;

		// パースペクティブエリア全体を囲むクラスのtop,leftを取得する。
		var dropAreaAllDiv = $("."
				+ wgpStyleClassConstants.PERSPACTIVE_DROP_AREA_ALL);
		dropAreaAllDiv.width(this.max_width + 10);
		dropAreaAllDiv.height(this.max_height + 10);

		for ( var index_y = 0; index_y < max_index_y; index_y++) {

			var max_index_x = table[index_y].length;
			for ( var index_x = 0; index_x < max_index_x; index_x++) {

				// パースペクティブテーブル情報を取得する。
				var targetTableModel = table[index_y][index_x];

				// 既に設定済みの場合は処理を行なわない。
				if (alreadySetting[targetTableModel.get("drop_area_id")]) {
					continue;
				} else {
					alreadySetting[targetTableModel.get("drop_area_id")] = true;
				}

				// 非表示でない場合に幅・高さの設定を行なう。
				if (!targetTableModel.get("hide_flag")) {

					var utilBarId = targetTableModel.get("util_bar_id");
					var miniRestoreIconId = targetTableModel.get("minimize_restore_id");
					var hideIconId = targetTableModel.get("hide_id");
					var dropAreaId = targetTableModel.get("drop_area_id");

					// 各ボタンについて指定を行なう。
					if (targetTableModel.get("minimize_flag")) {
						common
								.addClassWrapperJQuery(
										$("#" + miniRestoreIconId),
										wgpStyleClassConstants.PERSPACTIVE_UTIL_BAR_RESTORE);
					} else {
						common
								.addClassWrapperJQuery(
										$("#" + miniRestoreIconId),
										wgpStyleClassConstants.PERSPACTIVE_UTIL_BAR_MIN);
					}

					if (!targetTableModel.get("hide_flag")) {
						common
								.addClassWrapperJQuery(
										$("#" + hideIconId),
										wgpStyleClassConstants.PERSPACTIVE_UTIL_BAR_HIDE);
					}

					// 一つ手前のパースペクティブの次に配置されるように位置を修正する。
					var sumTop = 0;
					if (index_y > 0) {
						var beTableModelTop = table[index_y - 1][index_x];
						var tempViewDiv = $("#"
								+ beTableModelTop.get("drop_area_id"));

						var tempViewDivPosition = tempViewDiv
								.position();
						sumTop = tempViewDivPosition.top
								+ tempViewDiv.height();
					} else {
						sumTop = 3;
					}

					var sumLeft = 0;
					if (index_x > 0) {
						beforeTableModelLeft = table[index_y][index_x - 1];
						var tempViewDiv = $("#"
								+ beforeTableModelLeft.get("drop_area_id"));

						var tempViewDivPosition = tempViewDiv
								.position();
						sumLeft = tempViewDivPosition.left
								+ tempViewDiv.width();
					} else {
						sumLeft = 3;
					}

					// ドロップ領域について指定を行なう。
					var dropAreaDiv = $("#" + dropAreaId);
					dropAreaDiv.css("position", "absolute");
					dropAreaDiv.css("top", 0 + sumTop + 1);
					dropAreaDiv.css("left", 0 + sumLeft + 1);

					dropAreaDiv.width(targetTableModel.get("width"));
					dropAreaDiv.height(targetTableModel.get("height"));
					dropAreaDiv.zIndex(zIndex);

					// ユーティリティバーについて指定を行なう。
					var utilBarDiv = $("#" + utilBarId);
					utilBarDiv.css("position", "relative");
					utilBarDiv.css("top", 0);
					utilBarDiv.css("left", 0);
					utilBarDiv.width("100%");
					utilBarDiv.zIndex(zIndex);

					// ビューの位置指定を行なうメソッドを呼び出す
					this.resetViewPosition(targetTableModel);
				}

				zIndex = zIndex + zIndex_margin;
			}
		}
	},
	resetViewPosition : function(targetTableModel) {
		var margin = 10;

		// ビューが関連付いている場合のみ処理を行う。
		if (targetTableModel.isRerationView()) {

			// パースペクティブ領域の情報よりドロップ領域・バー領域を取得する。
			var dropAreaDiv = $("#" + targetTableModel.get("drop_area_id"));
			var utilBarDiv = $("#" + targetTableModel.get("util_bar_id"));

			// ビュー領域を取得する。
			var viewAreaDiv = $("#" + targetTableModel.get("view_div_id"));

			// ビュー領域の位置及び大きさを再設定する。
			viewAreaDiv.width(dropAreaDiv.width() - margin);
			viewAreaDiv.height(dropAreaDiv.height()
					- utilBarDiv.height() - margin);
			viewAreaDiv.zIndex(dropAreaDiv.zIndex() + 1);
			viewAreaDiv.css("overflow", "auto");

			$.each(viewAreaDiv.children(), function(index, viewItem) {

				if (viewItem.resizeFunction) {
					viewItem.resizeFunction(viewAreaDiv.width(),
							viewAreaDiv.height());
				}
			});
		}
	},
	setPerspactiveEvent : function(){

		// パースペクティブテーブルに関連付けられているビューについて、
		// イベントの設定を行なう。
		var perspactiveTable = this.collection;
		var max_index_y = perspactiveTable.length;
		var alreadySetting = {};
		for ( var index_y = 0; index_y < max_index_y; index_y++) {

			var max_index_x = perspactiveTable[index_y].length;
			for ( var index_x = 0; index_x < max_index_x; index_x++) {

				// パースペクティブ表示領域の情報を取得する。
				var tableModel = perspactiveTable[index_y][index_x];

				// 既に設定済みの場合は処理を行なわない。
				if(!alreadySetting[tableModel.get("drop_area_id")]){
					this.setEventFunction(tableModel);
					alreadySetting[tableModel.get("drop_area_id")] = true;
				}
			}
		}
	},
	/**
	 * ビュードロップ時の処理を行う。
	 */
	dropView : function(droppableTargetId, viewId){
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
		this.dropEventFunction(droppableTargetId, viewId);
	},
	/**
	 * パースペクティブ領域にビューがドロップされた際の処理を行う。
	 * 
	 * @param droppableTargetId
	 *            ドロップ領域
	 * @param draggableTargetId
	 *            ドロップされたビュー
	 */
	dropEventFunction : function(droppableTargetId, draggableTargetId) {

		// ドロップ領域より該当するパースペクティブ情報を取得する。
		targetTableModel = this.findPerspactiveFromId(droppableTargetId);
	
		// ドロップ領域に関連付くビューがない場合、又は関連付けが変わらない場合
		if (!targetTableModel.get("view_div_id") || targetTableModel.get("view_id_id") == draggableTargetId) {
	
			// ビューの関連付けを行なう。
			targetTableModel.set({view_div_id : draggableTargetId});
	
			var targetViewDiv = $("#" + targetTableModel.get("view_div_id"));
	
			// ドロップ領域の子要素として扱われるように移動する。
			$("#" + targetTableModel.get("drop_area_id")).append(targetViewDiv);
	
			this.resetViewPosition(targetTableModel);
	
			// draggable要素のresizableイベントを非活性にする。
			targetViewDiv.resizable("disable");
	
			// 半透明になるクラスのみ削除する。
			targetViewDiv.removeClass("ui-state-disabled");
		}
	},
	/**
	 * いずれかのIDを基にパースペクティブテーブル領域の情報を取得する。
	 */
	findPerspactiveFromId : function(argument_id) {
		var max_index_y = this.collection.length;
		for ( var index_y = 0; index_y < max_index_y; index_y++) {
	
			var max_index_x = this.collection[index_y].length;
			for ( var index_x = 0; index_x < max_index_x; index_x++) {
	
				// パースペクティブ表示領域の情報を取得する。
				// ※いずれかのid属性情報と一致するかどうか確認する。
				var targetTableModel = this.collection[index_y][index_x];
				if (targetTableModel.get("drop_area_id") == argument_id ||
					targetTableModel.get("util_bar_id") == argument_id ||
					targetTableModel.get("minimize_restore_id") == argument_id ||
					targetTableModel.get("hide_id") == argument_id
				) {
					return targetTableModel;
				}
			}
		}
	},
	/**
	 * 引数にて渡されたパースペクティブ領域にイベントを設定する。
	 * 
	 * @param tableModel
	 */
	setEventFunction : function(tableModel) {

		var instance = this;

		// パースペクティブ領域に対してリサイズイベントを設定する。
		$("#" + tableModel.get("drop_area_id")).resizable({
			start : function(e, ui) {
				instance.resizeStartFunction(e.target.id);
			},
			resize : function(e, ui) {
				common.moveEndFront("#" + e.target.id);

			},
			stop : function(e, ui) {
				instance.resizeStopFunction(e.target.id);

			}
		}).droppable({
			accept : this.droppableClass,
			drop : function(e, ui) {

				// ドロップ領域のIDを取得する。
				var droppableTargetId = e.target.id;
				var draggableTargetId = ui.draggable.attr("id");

				instance.dropEventFunction(droppableTargetId, draggableTargetId);
			},
			out : function(e, ui) {

				// ドロップ領域より該当するパースペクティブ情報を取得する。
				var targetTableModel = instance.findPerspactiveFromId(e.target.id);

				// ドロップ領域に関連付くビュー情報を削除する。
				targetTableModel.set({view_div_id : ""});

				// draggable要素のresizableイベントを活性にする。
				ui.draggable.resizable("enable");
			}
		});

		// ユーティリティバー領域に対してクリックイベントを適用する。
		$("#" + tableModel.get("minimize_restore_id")).mousedown(function(event) {
			instance.minRestoreEventFunction(this.id);
		});

		$("#" + tableModel.get("hide_id")).mousedown(function(event){
			instance.hideEventFunction(this.id);
		});

		$("#" + tableModel.get("util_bar_id")).dblclick(function(event){
			instance.maximumEventFunction(this.id);
		});
	},
	resizeStartFunction : function(){
		this.memoryTablePosition();
	},
	/**
	 * パースペクティブ領域の位置情報を記憶する。
	 */
	memoryTablePosition : function(){
		var perspactiveTable = this.collection;

		// リサイズ前のパースペクティブテーブル上の全ての要素の幅、高さを設定しておく。
		var max_index_y = perspactiveTable.length;
		for ( var index_y = 0; index_y < max_index_y; index_y++) {

			var max_index_x = perspactiveTable[index_y].length;
			for ( var index_x = 0; index_x < max_index_x; index_x++) {
				var tableModel = perspactiveTable[index_y][index_x];
				var dropAreaDiv = $("#" + tableModel.get("drop_area_id"));
				var position = dropAreaDiv.position();

				var modelSet = {
					top : position["top"],
					left : position["left"],
					width : dropAreaDiv.width(),
					height : dropAreaDiv.height()
				}
				tableModel.set(modelSet);
			}
		}	
	},
	/**
	 * パースペクティブ領域のリサイズ終了時の処理を行う。
	 */
	resizeStopFunction : function(targetId) {

		var targetDiv = $("#" + targetId);
		var perspactiveTable = this.collection;

		// パースペクティブテーブル上の位置を取得する。
		var targetTableModel = this.findPerspactiveFromId(targetId);

		// リサイズ後の幅を取得する。
		var afterWidth = targetDiv.width();
		var afterHeight = targetDiv.height();
		var afterPosition = targetDiv.position();
		var afterTop = afterPosition.top;
		var afterLeft = afterPosition.left;

		// 変化量を計算する。
		var changeWidth = afterWidth - targetTableModel.get("width");
		var changeHeight = afterHeight - targetTableModel.get("height");
		var changeTop = afterTop - targetTableModel.get("top");
		var changeLeft = afterLeft - targetTableModel.get("left");

		var alreadyProcessed = {};

		var left_up_view_array = targetTableModel.get("left_up_view_array");
		var up_view_array = targetTableModel.get("up_view_array");
		var right_up_view_array = targetTableModel.get("right_up_view_array");

		var left_bottom_view_array = targetTableModel.get("left_bottom_view_array");
		var bottom_view_array = targetTableModel.get("bottom_view_array");
		var right_bottom_view_array = targetTableModel.get("right_bottom_view_array");

		var left_view_array = targetTableModel.get("left_view_array");
		var right_view_array = targetTableModel.get("right_view_array");

		// 左上方向に隣接するパースペクティブ領域に対するリサイズ処理
		$.each(left_up_view_array, function(index, indexTableModel){
			var indexDiv = $("#" + indexTableModel.drop_area_id );
			indexDiv.width( indexDiv.width() - changeLeft );
			indexDiv.height( indexDiv.height() + changeTop );			
		});

		// 上方向に隣接するパースペクティブ領域に対するリサイズ処理
		$.each(up_view_array, function(index, indexTableModel){
			var indexDiv = $("#" + indexTableModel.drop_area_id );
			indexDiv.height( indexDiv.height() + changeTop );			
		});

		// 右上方向に隣接するパースペクティブ領域に対するリサイズ処理
		$.each(right_up_view_array, function(index, indexTableModel){
			var indexDiv = $("#" + indexTableModel.drop_area_id );
			var indexPosition = indexDiv.position();

			indexDiv.css("left", indexPosition["left"] + changeWidth );
			indexDiv.width( indexDiv.width() - changeWidth );
			indexDiv.height( indexDiv.height() + changeTop );
		});

		// 左方向に隣接するパースペクティブ領域に対するリサイズ処理
		$.each(left_view_array, function(index, indexTableModel){
			var indexDiv = $("#" + indexTableModel.drop_area_id );

			indexDiv.width( indexDiv.width() + changeLeft );

			if(targetTableModel.first_index_y == indexTableModel.first_index_y){
				indexDiv.css("top", afterTop );
			}

			if(targetTableModel.last_index_y == indexTableModel.last_index_y){
				indexDiv.height( afterTop + afterHeight );
			}
		});

		// 右方向に隣接するパースペクティブ領域に対するリサイズ処理
		$.each(right_view_array, function(index, indexTableModel){
			var indexDiv = $("#" + indexTableModel.get("drop_area_id") );
			var indexPosition = indexDiv.position();

			indexDiv.css("left", indexPosition["left"] + changeWidth );
			indexDiv.width( indexDiv.width() - changeWidth );

			if(targetTableModel.get("first_index_y") == indexTableModel.get("first_index_y")){
				indexDiv.css("top", afterTop );
			}

			if(targetTableModel.last_index_y == indexTableModel.last_index_y){
				indexDiv.height( indexDiv.height() + changeHeight );
			}
		});

		// 左下方向に隣接するパースペクティブ領域に対するリサイズ処理
		$.each(left_bottom_view_array, function(index, indexTableModel){
			var indexDiv = $("#" + indexTableModel.get("drop_area_id"));
			var indexPosition = indexDiv.position();

			indexDiv.css("top", indexPosition["top"] + changeHeight);
			indexDiv.width( indexDiv.width() + changeLeft );
		});

		// 下方向に隣接するパースペクティブ領域に対するリサイズ処理
		$.each(bottom_view_array, function(index, indexTableModel){
			var indexDiv = $("#" + indexTableModel.get("drop_area_id"));
			var indexPosition = indexDiv.position();

			indexDiv.css("top", indexPosition["top"] + changeHeight);
			indexDiv.height( indexDiv.height() - changeHeight );
		});

		// 右下方向に隣接するパースペクティブ領域に対するリサイズ処理
		$.each(right_bottom_view_array, function(index, indexTableModel){
			var indexDiv = $("#" + indexTableModel.get("drop_area_id"));
			var indexPosition = indexDiv.position();

			indexDiv.css("top", indexPosition["top"] + changeHeight);
			indexDiv.css("left", indexPosition["left"] + changeWidth );
			indexDiv.width( indexDiv.width() - changeWidth );
		});

		// 関連付くビューを再配置する。
		alreadyProcessed = {};
		for(var index_y = 0; index_y < perspactiveTable.length; index_y++){
			for(var index_x = 0; index_x < perspactiveTable[index_y].length; index_x++){

				var indexTableModel = perspactiveTable[index_y][index_x];

				// 処理済みの場合は除く
				if(alreadyProcessed[indexTableModel.get("view_div_id")] == true){
					continue;
				}
				alreadyProcessed[indexTableModel.get("view_div_id")] = true;

				this.resetViewPosition(indexTableModel);
			}
		}
	}
});