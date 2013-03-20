/*******************************************************************************
 * WGP 1.0B - Web Graphical Platform (https://sourceforge.net/projects/wgp/)
 * 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2012 Acroquest Technology Co.,Ltd.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
// パースペクティブビュー
wgp.PerspectiveView = Backbone.View
		.extend({
			initialize : function(argument) {
				_.bindAll();

				// パースペクティブテーブル内のドロップ領域のidのプレフィックス
				this.drop_area_prefix = "_drop";

				// パースペクティブテーブル内のユーティリティバーのidのプレフィックス
				this.util_bar_prefix = "_bar";

				// パースペクティブテーブル内の最小化/元に戻すボタン領域のidのプレフィックス
				this.minimize_restore_prefix = "_miniRestore";

				// パースペクティブテーブル内の非表示ボタン領域のidのプレフィックス
				this.hide_prefix = "_hide";

				this.id = argument["id"];
				this.collection = argument["collection"];
				this.buttonOption = {
					minimum : (argument.minimum != null) ? argument.minimum
							: true,
					close : (argument.close != null) ? argument.close : true
				};

				// レンダリングメソッドを実行
				this.render();
			},
			render : function() {
				this.createTableTags(this.id, this.collection);
				this.tableInitial(this.collection);
				this.prepareTable();
				this.setPerspectiveEvent();
			},
			createTableTags : function(parentDivId, table) {
				// 指定された親divタグを基に各領域識別用の情報を設定する。
				var util_bar_suffix = parentDivId + this.util_bar_prefix;
				var drop_area_suffix = parentDivId + this.drop_area_prefix;
				var minimize_restore_suffix = parentDivId
						+ this.minimize_restore_prefix;
				var hide_suffix = parentDivId + this.hide_prefix;

				// パースペクティブテーブルの内容を基にtableを構築する。
				var max_index_y = table.length;

				// パースペクティブ全体を囲むDIVタグ生成用DTO
				var dropAreaAllDto = new wgp.wgpDomDto(null, "div", null,
						[ wgp.styleClassConstants.PERSPECTIVE_DROP_AREA_ALL ],
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
						var dropAreaDto = new wgp.wgpDomDto(
								dropDivId,
								"div",
								null,
								[ wgp.styleClassConstants.PERSPECTIVE_DROP_AREA ],
								{
									width : dropAreaWidth + "px",
									height : dropAreaHeight + "px"
								});

						dropAreaAllDto.addChildren([ dropAreaDto ]);

						// ユーティリティバー生成用DTO
						var utilBarDto = new wgp.wgpDomDto(
								utilBarId,
								"div",
								null,
								[ wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR ],
								null);

						dropAreaDto.addChildren([ utilBarDto ]);

						var barButtonDtoList = [];
						if (this.buttonOption.close) {
							// 非表示ボタン生成用DTO
							var utilBarHideDto = new wgp.wgpDomDto(
									hideIconId,
									"div",
									null,
									[ wgp.styleClassConstants.PERSPECTIVE_ICON ],
									null);
							barButtonDtoList.push(utilBarHideDto);
						}

						if (this.buttonOption.minimum) {
							// 最小化ボタン生成用DTO
							var utilBarMiniDto = new wgp.wgpDomDto(
									miniRestoreIconId,
									"div",
									null,
									[ wgp.styleClassConstants.PERSPECTIVE_ICON ],
									null);
							barButtonDtoList.push(utilBarMiniDto);
						}

						utilBarDto.addChildren(barButtonDtoList);
					}
				}

				// 指定されたdivタグにtable要素を追加
				$("#" + parentDivId).append(
						wgp.wgpDomCreator.createDomStringCall(dropAreaAllDto));
			},
			tableInitial : function(perspectiveInformation) {

				// パースペクティブテーブル情報を基にテーブルの段組情報を解釈しやすい形に変更する。
				var persepactiveYMax = perspectiveInformation.length;
				var persepactiveXMax = 0;
				var index_y;
				for (index_y = 0; index_y < persepactiveYMax; index_y++) {
					if (persepactiveXMax < perspectiveInformation[index_y].length) {
						persepactiveXMax = perspectiveInformation[index_y].length;
					}
				}

				// あらかじめ必要な分の配列を生成しておく。
				var table = new Array(persepactiveYMax);
				for (index_y = 0; index_y < persepactiveYMax; index_y++) {
					table[index_y] = new Array(persepactiveXMax);
				}

				// パースペクティブテーブル情報を解釈しやすい形に再構成する。
				var modelSet;
				var index_x;
				for (index_y = 0; index_y < persepactiveYMax; index_y++) {
					for (index_x = 0; index_x < perspectiveInformation[index_y].length; index_x++) {

						// パースペクティブ領域情報を取得する。
						var tableModel = perspectiveInformation[index_y][index_x];

						var findFlag = false;
						var tableIndex_y = 0;
						var tableIndex_x = 0;

						for ( tableIndex_y = 0; tableIndex_y < table.length;) {
							for ( tableIndex_x = 0; tableIndex_x < table[tableIndex_y].length;) {
								if (table[tableIndex_y][tableIndex_x] === undefined) {
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

						var rowspanIndex;
						var colspanIndex;
						for ( rowspanIndex = 0; rowspanIndex < tableModel.get("rowspan"); rowspanIndex++) {
							for ( colspanIndex = 0; colspanIndex < tableModel.get("colspan"); colspanIndex++) {
								table[tableIndex_y + rowspanIndex][tableIndex_x + colspanIndex] = tableModel;
							}
						}

						// パースペクティブ領域情報に最終行番号及び最終列番号を加える。
						modelSet = {
							first_index_y : tableIndex_y,
							first_index_x : tableIndex_x,
							last_index_y : tableIndex_y + rowspanIndex - 1,
							last_index_x : tableIndex_x + colspanIndex - 1
						};
						tableModel.set(modelSet);
					}
				}

				// 隣接するパースペクティブ領域に関する情報を設定する。
				var alreadySetting = {};
				var indexTableModel;
				for (index_y = 0; index_y < table.length; index_y++) {
					for (index_x = 0; index_x < table[index_y].length; index_x++) {

						var targetTableModel = table[index_y][index_x];
						if (alreadySetting[targetTableModel.get("drop_area_id")] === true) {
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

								indexTableModel = table[temp_index_y][temp_index_x];
								if (indexTableModel.get("drop_area_id") == targetTableModel
										.get("drop_area_id")
										|| alreadySettingLoop[indexTableModel.drop_area_id] === true) {
									continue;
								}
								alreadySettingLoop[indexTableModel
										.get("drop_area_id")] = true;

								// 左上方向に隣接する場合
								if (targetTableModel.get("first_index_y") - 1 == indexTableModel
										.get("last_index_y")
										&& targetTableModel
												.get("first_index_x") - 1 == indexTableModel
												.get("last_index_x")) {
									left_up_view_array.push(indexTableModel);
									continue;
								}

								// 右上方向に隣接する場合
								if (targetTableModel.get("first_index_y") - 1 == indexTableModel
										.get("last_index_y")
										&& targetTableModel.get("last_index_x") + 1 == indexTableModel
												.get("first_index_x")) {
									right_up_view_array.push(indexTableModel);
									continue;
								}

								// 左下方向に隣接する場合
								if (targetTableModel.get("last_index_y") + 1 == indexTableModel
										.get("first_index_y")
										&& targetTableModel
												.get("first_index_x") - 1 == indexTableModel
												.get("last_index_x")) {
									left_bottom_view_array
											.push(indexTableModel);
									continue;
								}

								// 右下方向に隣接する場合
								if (targetTableModel.get("last_index_y") + 1 == indexTableModel
										.get("first_index_y")
										&& targetTableModel.get("last_index_x") + 1 == indexTableModel
												.get("first_index_x")) {
									right_bottom_view_array
											.push(indexTableModel);
									continue;
								}

								// 上方向に隣接する場合
								if (targetTableModel.get("first_index_y") - 1 == indexTableModel
										.get("last_index_y")) {
									up_view_array.push(indexTableModel);
									continue;
								}

								// 下方向に隣接する場合
								if (targetTableModel.get("last_index_y") + 1 == indexTableModel
										.get("first_index_y")) {
									bottom_view_array.push(indexTableModel);
									continue;
								}

								// 左方向に隣接する場合
								if (targetTableModel.get("first_index_x") - 1 == indexTableModel
										.get("last_index_x")) {
									left_view_array.push(indexTableModel);
									continue;
								}

								// 右方向に隣接する場合
								if (targetTableModel.get("last_index_x") + 1 == indexTableModel
										.get("first_index_x")) {
									right_view_array.push(indexTableModel);
									continue;
								}
							}
						}

						// 隣接するパースペクティブ領域の情報を設定
						modelSet = {
							left_up_view_array : left_up_view_array,
							up_view_array : up_view_array,
							right_up_view_array : right_up_view_array,

							left_bottom_view_array : left_bottom_view_array,
							bottom_view_array : bottom_view_array,
							right_bottom_view_array : right_bottom_view_array,

							left_view_array : left_view_array,
							right_view_array : right_view_array
						};
						targetTableModel.set(modelSet);
					}
				}

				// 生成したパースペクティブテーブルを2次元配列としてコレクションに格納しなおす。
				this.collection = table;

				this.max_width = 0;
				this.max_height = 0;

				// パースペクティブの最大幅を取得する。
				alreadySetting = {};
				for (index_x = 0; index_x < this.collection[0].length; index_x++) {
					indexTableModel = this.collection[0][index_x];

					if (alreadySetting[indexTableModel.get("drop_area_id")] !== true) {
						this.max_width = this.max_width
								+ indexTableModel.get("width");
						alreadySetting[indexTableModel.get("drop_area_id")] = true;
					}
				}

				// パースペクティブの最大高さを取得する。
				alreadySetting = {};
				for (index_y = 0; index_y < this.collection.length; index_y++) {
					indexTableModel = this.collection[index_y][0];

					if (alreadySetting[indexTableModel.get("drop_area_id")] !== true) {
						this.max_height = this.max_height
								+ indexTableModel.get("height");
						alreadySetting[indexTableModel.get("drop_area_id")] = true;
					}
				}

				// ドロップ可能なウィンドウの定義
				this.droppableClass = "perspective_window";

				// 最小化時の幅
				this.minimize_width = 30;

				// 最小化時の高さ
				this.minimize_height = 20;

				alreadySetting = {};
				for (index_y = 0; index_y < table.length; index_y++) {
					for (index_x = 0; index_x < table[index_y].length; index_x++) {

						var targetTableModel = table[index_y][index_x];
						if (alreadySetting[targetTableModel.get("drop_area_id")] === true) {
							continue;
						}
						alreadySetting[targetTableModel.get("drop_area_id")] = true;

						var utilBarId = targetTableModel.get("util_bar_id");
						var utilBarView = new wgp.UtilBarView({id: utilBarId});
						targetTableModel.set({utilBarView: utilBarView});
					}
				}
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
						+ wgp.styleClassConstants.PERSPECTIVE_DROP_AREA_ALL);
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
							var miniRestoreIconId = targetTableModel
									.get("minimize_restore_id");
							var hideIconId = targetTableModel.get("hide_id");
							var dropAreaId = targetTableModel
									.get("drop_area_id");

							// 各ボタンについて指定を行なう。
							if (targetTableModel.get("minimize_flag")) {
								wgp.common
										.addClassWrapperJQuery(
												$("#" + miniRestoreIconId),
												wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR_RESTORE);
							} else {
								wgp.common
										.addClassWrapperJQuery(
												$("#" + miniRestoreIconId),
												wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR_MIN);
							}

							if (!targetTableModel.get("hide_flag")) {
								wgp.common
										.addClassWrapperJQuery(
												$("#" + hideIconId),
												wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR_HIDE);
							}

							// 一つ手前のパースペクティブの次に配置されるように位置を修正する。
							var sumTop = 0;
							var tempViewDiv;
							var tempViewDivPosition;
							if (index_y > 0) {
								var beTableModelTop = table[index_y - 1][index_x];
								tempViewDiv = $("#"
										+ beTableModelTop.get("drop_area_id"));

								tempViewDivPosition = tempViewDiv.position();
								sumTop = tempViewDivPosition.top
										+ tempViewDiv.height();
							} else {
								sumTop = 3;
							}

							var sumLeft = 0;
							if (index_x > 0) {
								var beforeTableModelLeft = table[index_y][index_x - 1];
								tempViewDiv = $("#"
										+ beforeTableModelLeft
												.get("drop_area_id"));

								tempViewDivPosition = tempViewDiv.position();
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
					var dropAreaDiv = $("#"
							+ targetTableModel.get("drop_area_id"));
					var utilBarDiv = $("#"
							+ targetTableModel.get("util_bar_id"));

					// ビュー領域を取得する。
					var viewDivIdArray = targetTableModel.get("view_div_id");
					$.each(viewDivIdArray, function(index, view_div_id){
						var viewAreaDiv = $("#" + view_div_id);

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
					});
				}
			},
			setPerspectiveEvent : function() {

				// パースペクティブテーブルに関連付けられているビューについて、
				// イベントの設定を行なう。
				var perspectiveTable = this.collection;
				var max_index_y = perspectiveTable.length;
				var alreadySetting = {};
				for ( var index_y = 0; index_y < max_index_y; index_y++) {

					var max_index_x = perspectiveTable[index_y].length;
					for ( var index_x = 0; index_x < max_index_x; index_x++) {

						// パースペクティブ表示領域の情報を取得する。
						var tableModel = perspectiveTable[index_y][index_x];

						// 既に設定済みの場合は処理を行なわない。
						if (!alreadySetting[tableModel.get("drop_area_id")]) {
							this.setEventFunction(tableModel);
							alreadySetting[tableModel.get("drop_area_id")] = true;
						}
					}
				}
			},
			/**
			 * ビュードロップ時の処理を行う。
			 */
			dropView : function(droppableTargetId, viewId, viewName) {
				var viewDiv = $("#" + viewId);
				if (viewDiv.length === 0) {
					var viewAreaDto = new wgp.wgpDomDto(viewId, "div", null,
							[ wgp.styleClassConstants.PERSPECTIVE_VIEW_AREA ],
							null);

					$("#" + droppableTargetId).append(
							wgp.wgpDomCreator.createDomStringCall(viewAreaDto));
				} else {
					$("#" + droppableTargetId).append(viewDiv);
				}
				this.dropEventFunction(droppableTargetId, viewId, viewName);
			},
			/**
			 * パースペクティブ領域にビューがドロップされた際の処理を行う。
			 * 
			 * @param droppableTargetId
			 *            ドロップ領域
			 * @param draggableTargetId
			 *            ドロップされたビュー
			 */
			dropEventFunction : function(droppableTargetId, draggableTargetId, viewName) {

				// ドロップ領域より該当するパースペクティブ情報を取得する。
				var targetTableModel = this
						.findPerspectiveFromId(droppableTargetId);

				// ドロップ領域にドロップしたビューが関連付いていない場合は、ビューの関連付けを行う。
				if (!targetTableModel.isContainsView(draggableTargetId)) {

					targetTableModel.addView(draggableTargetId, viewName);

					// ビューの大きさ等をドロップ領域に合わせる。
					this.resetViewPosition(targetTableModel);
				}
			},
			/**
			 * いずれかのIDを基にパースペクティブテーブル領域の情報を取得する。
			 */
			findPerspectiveFromId : function(argument_id) {
				var max_index_y = this.collection.length;
				for ( var index_y = 0; index_y < max_index_y; index_y++) {

					var max_index_x = this.collection[index_y].length;
					for ( var index_x = 0; index_x < max_index_x; index_x++) {

						// パースペクティブ表示領域の情報を取得する。
						// ※いずれかのid属性情報と一致するかどうか確認する。
						var targetTableModel = this.collection[index_y][index_x];
						if (targetTableModel.get("drop_area_id") == argument_id
								|| targetTableModel.get("util_bar_id") == argument_id
								|| targetTableModel.get("minimize_restore_id") == argument_id
								|| targetTableModel.get("hide_id") == argument_id
								|| _.contains(targetTableModel.get("view_div_id"), argument_id) ) {
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
				$("#" + tableModel.get("drop_area_id"))
						.resizable({
							start : function(e, ui) {
								instance.resizeStartFunction(e.target.id);
							},
							resize : function(e, ui) {
								wgp.common.moveEndFront("#" + e.target.id);

							},
							stop : function(e, ui) {
								instance.resizeStopFunction(e.target.id);

							}
						})
						.droppable(
								{
									accept : this.droppableClass,
									drop : function(e, ui) {

										// ドロップ領域のIDを取得する。
										var droppableTargetId = e.target.id;
										var draggableTargetId = ui.draggable
												.attr("id");

										instance.dropEventFunction(
												droppableTargetId,
												draggableTargetId);
									},
									out : function(e, ui) {

										// ドロップ領域のIDを取得する。
										var droppableTargetId = e.target.id;
										var draggableTargetId = ui.draggable
												.attr("id");

										// ドロップ領域より該当するパースペクティブ情報を取得する。
										var targetTableModel = instance
												.findPerspectiveFromId(droppableTargetId);

										// ビューの関連付けを削除する。
										targetTableModel.removeView(draggableTargetId);
									}
								});

				// ユーティリティバー領域に対してクリックイベントを適用する。
				$("#" + tableModel.get("minimize_restore_id")).mousedown(
						function(event) {
							if (instance.minRestoreEventFunction) {
								instance.minRestoreEventFunction(this.id);
							}
						});

				$("#" + tableModel.get("hide_id")).mousedown(function(event) {
					if (instance.hideEventFunction) {
						instance.hideEventFunction(this.id);
					}
				});

				$("#" + tableModel.get("util_bar_id")).dblclick(
						function(event) {
							if (instance.maximumEventFunction) {
								instance.maximumEventFunction(this.id);
							}
						});
			},
			resizeStartFunction : function() {
				this.memoryTablePosition();
			},
			/**
			 * パースペクティブ領域の位置情報を記憶する。
			 */
			memoryTablePosition : function() {
				var perspectiveTable = this.collection;

				// リサイズ前のパースペクティブテーブル上の全ての要素の幅、高さを設定しておく。
				var max_index_y = perspectiveTable.length;
				for ( var index_y = 0; index_y < max_index_y; index_y++) {

					var max_index_x = perspectiveTable[index_y].length;
					for ( var index_x = 0; index_x < max_index_x; index_x++) {
						var tableModel = perspectiveTable[index_y][index_x];
						var dropAreaDiv = $("#"
								+ tableModel.get("drop_area_id"));
						var position = dropAreaDiv.position();

						var modelSet = {
							top : position["top"],
							left : position["left"],
							width : dropAreaDiv.width(),
							height : dropAreaDiv.height()
						};
						tableModel.set(modelSet);
					}
				}
			},
			/**
			 * パースペクティブ領域のリサイズ終了時の処理を行う。
			 */
			resizeStopFunction : function(targetId) {

				var targetDiv = $("#" + targetId);
				var perspectiveTable = this.collection;

				// パースペクティブテーブル上の位置を取得する。
				var targetTableModel = this.findPerspectiveFromId(targetId);

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

				var left_up_view_array = targetTableModel
						.get("left_up_view_array");
				var up_view_array = targetTableModel.get("up_view_array");
				var right_up_view_array = targetTableModel
						.get("right_up_view_array");

				var left_bottom_view_array = targetTableModel
						.get("left_bottom_view_array");
				var bottom_view_array = targetTableModel
						.get("bottom_view_array");
				var right_bottom_view_array = targetTableModel
						.get("right_bottom_view_array");

				var left_view_array = targetTableModel.get("left_view_array");
				var right_view_array = targetTableModel.get("right_view_array");

				// 左上方向に隣接するパースペクティブ領域に対するリサイズ処理
				$.each(left_up_view_array, function(index, indexTableModel) {
					var indexDiv = $("#" + indexTableModel.drop_area_id);
					indexDiv.width(indexDiv.width() - changeLeft);
					indexDiv.height(indexDiv.height() + changeTop);
				});

				// 上方向に隣接するパースペクティブ領域に対するリサイズ処理
				$.each(up_view_array, function(index, indexTableModel) {
					var indexDiv = $("#" + indexTableModel.drop_area_id);
					indexDiv.height(indexDiv.height() + changeTop);
				});

				// 右上方向に隣接するパースペクティブ領域に対するリサイズ処理
				$.each(right_up_view_array, function(index, indexTableModel) {
					var indexDiv = $("#" + indexTableModel.drop_area_id);
					var indexPosition = indexDiv.position();

					indexDiv.css("left", indexPosition["left"] + changeWidth);
					indexDiv.width(indexDiv.width() - changeWidth);
					indexDiv.height(indexDiv.height() + changeTop);
				});

				// 左方向に隣接するパースペクティブ領域に対するリサイズ処理
				$
						.each(
								left_view_array,
								function(index, indexTableModel) {
									var indexDiv = $("#"
											+ indexTableModel.drop_area_id);

									indexDiv.width(indexDiv.width()
											+ changeLeft);

									if (targetTableModel.first_index_y == indexTableModel.first_index_y) {
										indexDiv.css("top", afterTop);
									}

									if (targetTableModel.last_index_y == indexTableModel.last_index_y) {
										indexDiv.height(afterTop + afterHeight);
									}
								});

				// 右方向に隣接するパースペクティブ領域に対するリサイズ処理
				$
						.each(
								right_view_array,
								function(index, indexTableModel) {
									var indexDiv = $("#"
											+ indexTableModel
													.get("drop_area_id"));
									var indexPosition = indexDiv.position();

									indexDiv.css("left", indexPosition["left"]
											+ changeWidth);
									indexDiv.width(indexDiv.width()
											- changeWidth);

									if (targetTableModel.get("first_index_y") == indexTableModel
											.get("first_index_y")) {
										indexDiv.css("top", afterTop);
									}

									if (targetTableModel.last_index_y == indexTableModel.last_index_y) {
										indexDiv.height(indexDiv.height()
												+ changeHeight);
									}
								});

				// 左下方向に隣接するパースペクティブ領域に対するリサイズ処理
				$.each(left_bottom_view_array,
						function(index, indexTableModel) {
							var indexDiv = $("#"
									+ indexTableModel.get("drop_area_id"));
							var indexPosition = indexDiv.position();

							indexDiv.css("top", indexPosition["top"]
									+ changeHeight);
							indexDiv.width(indexDiv.width() + changeLeft);
						});

				// 下方向に隣接するパースペクティブ領域に対するリサイズ処理
				$.each(bottom_view_array,
						function(index, indexTableModel) {
							var indexDiv = $("#"
									+ indexTableModel.get("drop_area_id"));
							var indexPosition = indexDiv.position();

							indexDiv.css("top", indexPosition["top"]
									+ changeHeight);
							indexDiv.height(indexDiv.height() - changeHeight);
						});

				// 右下方向に隣接するパースペクティブ領域に対するリサイズ処理
				$.each(right_bottom_view_array,
						function(index, indexTableModel) {
							var indexDiv = $("#"
									+ indexTableModel.get("drop_area_id"));
							var indexPosition = indexDiv.position();

							indexDiv.css("top", indexPosition["top"]
									+ changeHeight);
							indexDiv.css("left", indexPosition["left"]
									+ changeWidth);
							indexDiv.width(indexDiv.width() - changeWidth);
						});

				// 関連付くビューを再配置する。
//				alreadyProcessed = {};
				for ( var index_y = 0; index_y < perspectiveTable.length; index_y++) {
					for ( var index_x = 0; index_x < perspectiveTable[index_y].length; index_x++) {

						var indexTableModel = perspectiveTable[index_y][index_x];

//						// 処理済みの場合は除く
//						if (alreadyProcessed[indexTableModel.get("view_div_id")] === true) {
//							continue;
//						}
//						alreadyProcessed[indexTableModel.get("view_div_id")] = true;

						this.resetViewPosition(indexTableModel);
					}
				}
			},
			minRestoreEventFunction : function(targetId) {

				// パースペクティブ情報を取得する。
				var targetTableModel = this.findPerspectiveFromId(targetId);

				var minRestoreDiv = $("#"
						+ targetTableModel.get("minimize_restore_id"));
				var dropAreaDiv;
				// 元に戻す処理を行う。
				if (minRestoreDiv
						.hasClass(wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR_RESTORE)) {

					// ドロップ領域を取得する。
					dropAreaDiv = $("#" + targetTableModel.get("drop_area_id"));

					// 最小化前の内容を取得する。
					var restoreWidth = targetTableModel.restoreWidth;
					var restoreHeight = targetTableModel.restoreHeight;
					var restoreTop = targetTableModel.restoreTop;
					var restoreLeft = targetTableModel.restoreLeft;

					// リサイズ開始処理を行う。
					this.resizeStartFunction();

					dropAreaDiv.width(restoreWidth);
					dropAreaDiv.height(restoreHeight);
					dropAreaDiv.css("top", restoreTop + "px");
					dropAreaDiv.css("left", restoreLeft + "px");

					// リサイズ終了処理を行う。
					this.resizeStopFunction(targetTableModel
							.get("drop_area_id"));

					// ビューが関連付いている場合は表示する。
					if (targetTableModel.isRerationView()) {
						$("#" + targetTableModel.get("view_div_id")).show();
					}

					// 最小化/元に戻すのボタン表示クラスを入れ替える。
					wgp.common
							.removeClassWrapperJQuery(
									minRestoreDiv,
									wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR_RESTORE);
					wgp.common.addClassWrapperJQuery(minRestoreDiv,
							wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR_MIN);

					// 最小化処理を行う。
				} else if (minRestoreDiv
						.hasClass(wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR_MIN)) {

					// ドロップ領域を取得する。
					dropAreaDiv = $("#" + targetTableModel.get("drop_area_id"));
					var dropAreaPosition = dropAreaDiv.position();

					// 最小化前の内容を登録する。
					targetTableModel.restoreWidth = dropAreaDiv.width();
					targetTableModel.restoreHeight = dropAreaDiv.height();
					targetTableModel.restoreTop = dropAreaPosition["top"];
					targetTableModel.restoreLeft = dropAreaPosition["left"];

					// リサイズ開始処理を行う。
					this.resizeStartFunction();

					var returnObject = this.decideMinHideWay(targetTableModel,
							this.minimize_width, this.minimize_height);

					if (returnObject["width"]) {
						dropAreaDiv.width(returnObject["width"]);
					}
					if (returnObject["height"]) {
						dropAreaDiv.height(returnObject["height"]);
					}
					if (returnObject["top"]) {
						dropAreaDiv.css("top", returnObject["top"] + "px");
					}
					if (returnObject["left"]) {
						dropAreaDiv.css("left", returnObject["left"] + "px");
					}

					// リサイズ終了処理を行う。
					this.resizeStopFunction(targetTableModel
							.get("drop_area_id"));

					// ビューが関連付いている場合は非表示にする。
					if (targetTableModel.isRerationView()) {
						$("#" + targetTableModel.get("view_div_id")).hide();
					}

					// 最小化/元に戻すのボタン表示クラスを入れ替える。
					wgp.common
							.addClassWrapperJQuery(
									minRestoreDiv,
									wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR_RESTORE);
					wgp.common.removeClassWrapperJQuery(minRestoreDiv,
							wgp.styleClassConstants.PERSPECTIVE_UTIL_BAR_MIN);

				}
			},
			hideEventFunction : function(targetId) {
				// パースペクティブ情報を取得する。
				var targetTableModel = this.findPerspectiveFromId(targetId);

				// ドロップ領域を取得する。
				var dropAreaDiv = $("#" + targetTableModel.get("drop_area_id"));

				// リサイズ開始処理を行う。
				this.resizeStartFunction();

				var returnObject = this
						.decideMinHideWay(targetTableModel, 0, 0);
				if (returnObject["width"] || returnObject["width"] === 0) {
					dropAreaDiv.width(returnObject["width"]);
				}
				if (returnObject["height"] || returnObject["height"] === 0) {
					dropAreaDiv.height(returnObject["height"]);
				}
				if (returnObject["top"] || returnObject["top"] === 0) {
					dropAreaDiv.css("top", returnObject["top"] + "px");
				}
				if (returnObject["left"] || returnObject["left"] === 0) {
					dropAreaDiv.css("left", returnObject["left"] + "px");
				}

				// リサイズ終了処理を行う。
				this.resizeStopFunction(targetTableModel.get("drop_area_id"));

				// パースペクティブ領域を非表示にする。
				$("#" + targetTableModel.get("drop_area_id")).hide();

				// ビューが関連付いている場合はビューを非表示にする。
				if (targetTableModel.isRerationView()) {
					$("#" + targetTableModel.get("view_div_id")).hide();
				}
			},
			maximumEventFunction : function(targetId) {

				// 最大化時のクラス定義
				var perspectiveMaxClass = "perspective-maximum";

				// パースペクティブ情報を取得する。
				var targetTableModel = this.findPerspectiveFromId(targetId);
				var perspectiveTable = this.collection;

				var targetUtilBarDiv = $("#"
						+ targetTableModel.get("util_bar_id"));

				var targetDropAreaId = targetTableModel.get("drop_area_id");
				var dropAreaDiv;
				// 最大化処理を行う。
				if (!targetUtilBarDiv.hasClass(perspectiveMaxClass)) {

					dropAreaDiv = $("#" + targetDropAreaId);
					var dropAreaPosition = dropAreaDiv.position();

					targetTableModel.restoreWidth = dropAreaDiv.width();
					targetTableModel.restoreHeight = dropAreaDiv.height();
					targetTableModel.restoreTop = dropAreaPosition["top"];
					targetTableModel.restoreLeft = dropAreaPosition["left"];
					targetTableModel.restoreZIndex = dropAreaDiv.zIndex();

					// zIndexを一番手前に更新する。
					var maxZIndex = this.getMaxZIndex(targetTableModel);

					// 左上要素の座標を取得する。
					var topLeftViewDiv = $("#"
							+ perspectiveTable[0][0].get("drop_area_id"));
					var topLeftPosition = topLeftViewDiv.position();

					dropAreaDiv.width(this.max_width + 5);
					dropAreaDiv.height(this.max_height + 5);
					dropAreaDiv.css("top", topLeftPosition["top"] + "px");
					dropAreaDiv.css("left", topLeftPosition["left"] + "px");
					dropAreaDiv.css("zIndex", maxZIndex + 1);

					// ビューの位置を再設定する。
					this.resetViewPosition(targetTableModel);

					// 最大化されていることを示すクラスを設定
					targetUtilBarDiv.addClass(perspectiveMaxClass);

					// リサイズをできなくする。
					dropAreaDiv.resizable({
						"disable" : true
					});

					// 半透明になるクラスのみ削除する。
					dropAreaDiv.removeClass("ui-state-disabled");

					// 元に戻す処理を行う。
				} else {

					// ドロップ領域を取得する。
					dropAreaDiv = $("#" + targetDropAreaId);

					// 最小化前の内容を取得する。
					var restoreWidth = targetTableModel.restoreWidth;
					var restoreHeight = targetTableModel.restoreHeight;
					var restoreTop = targetTableModel.restoreTop;
					var restoreLeft = targetTableModel.restoreLeft;
					var restoreZIndex = targetTableModel.restoreZIndex;

					dropAreaDiv.width(restoreWidth);
					dropAreaDiv.height(restoreHeight);
					dropAreaDiv.css("top", restoreTop + "px");
					dropAreaDiv.css("left", restoreLeft + "px");
					dropAreaDiv.zIndex(restoreZIndex);

					// リサイズをできなくする。
					dropAreaDiv.resizable({
						"disable" : false
					});

					// ビューの位置を再設定する。
					this.resetViewPosition(targetTableModel);

					// 最大化のクラスを削除
					targetUtilBarDiv.removeClass(perspectiveMaxClass);
				}
			},
			decideMinHideWay : function(tagetTableModel, minimizeWidth,
					minimizeHeight) {

				var perspectiveTable = this.collection;
				var returnObject = {};

				var targetDiv = $("#" + tagetTableModel.get("drop_area_id"));
				var targetWidth = targetDiv.width();
				var targetHeight = targetDiv.height();
				var targetOffset = targetDiv.offset();
				var targetTop = targetOffset.top;
				var targetLeft = targetOffset.left;

				var lastIndexY = tagetTableModel.get("last_index_y");

				var left_view_array = tagetTableModel.get("left_view_array");
				var right_view_array = tagetTableModel.get("right_view_array");
				var bottom_view_array = tagetTableModel
						.get("bottom_view_array");

				// 一番下のパースペクティブかつ行結合がない場合
				if (lastIndexY == perspectiveTable.length - 1
						&& bottom_view_array.length > 0) {
					returnObject["height"] = minimizeHeight;
					returnObject["top"] = targetTop + targetHeight
							- minimizeHeight;
				}

				// 一番左のパースペクティブの場合
				if (left_view_array.length === 0 && right_view_array.length > 0) {

					// 左方向にリサイズする。
					returnObject["width"] = minimizeWidth;

					// 一番右のパースペクティブの場合
				} else if (right_view_array.length === 0
						&& left_view_array.length > 0) {

					// 右方向にリサイズする。
					returnObject["width"] = minimizeWidth;
					returnObject["left"] = targetLeft + targetWidth
							- minimizeWidth;

				} else {

					var findFlag = false;

					// 右に隣接する行を確認
					var index;
					for (index = 0; index < right_view_array.length
							&& !findFlag; index++) {

						// 自身と同じ開始行、終了行か確認
						if (tagetTableModel.get("first_index_y") == right_view_array[index].get("first_index_y")
								&& tagetTableModel.get("last_index_y") == right_view_array[index].get("last_index_y")) {
							returnObject["width"] = minimizeWidth;
							returnObject["left"] = targetLeft;
							findFlag = true;
						}
					}

					// 左に隣接する行を確認
					for (index = 0; index < left_view_array.length && !findFlag; index++) {

						// 自身と同じ開始行、終了行か確認
						if (tagetTableModel.get("first_index_y") == left_view_array[index].get("first_index_y")
								&& tagetTableModel.get("last_index_y") == left_view_array[index].get("last_index_y")) {
							returnObject["width"] = minimizeWidth;
							returnObject["left"] = targetLeft + targetWidth;
							findFlag = true;
						}
					}
				}

				return returnObject;
			},
			getMaxZIndex : function(tagetTableModel) {

				var perspectiveTable = this.collection;

				var targetDiv = $("#" + tagetTableModel.get("drop_area_id"));
				var maxZIndex = targetDiv.zIndex();

				for ( var index_y = 0; index_y < perspectiveTable.length; index_y++) {

					for ( var index_x = 0; index_x < perspectiveTable[index_y].length; index_x++) {

						var indexViewModel = perspectiveTable[index_y][index_x];
						var dropAreaDiv = $("#"
								+ indexViewModel.get("drop_area_id"));

						// ドロップ領域及びビュー領域それぞれのzインデックスについて確認
						if (dropAreaDiv.zIndex() >= maxZIndex) {
							maxZIndex = dropAreaDiv.zIndex();
						}

						if (indexViewModel.isRerationView()) {
							var viewDivIdArray = indexViewModel.get("view_div_id");
							$.each(viewDivIdArray, function(index, view_div_id){
								var viewAreaDiv = $("#" + view_div_id);

								if (viewAreaDiv.zIndex() > maxZIndex) {
									maxZIndex = viewAreaDiv.zIndex();
								}
							})
						}
					}
				}

				return maxZIndex;
			}
		});