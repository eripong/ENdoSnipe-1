ENS.ResourceMapView = wgp.MapView.extend({
	tagName : "div",
	initialize : function(argument) {
		_.bindAll();

		var width = $("#" + this.$el.attr("id")).width();
		var height = $("#" + this.$el.attr("id")).height();
		_.extend(argument, {width : width, height : height});

		var ajaxHandler = new wgp.AjaxHandler();
		this.ajaxHandler = ajaxHandler;

		this.mapId = argument["mapId"];
		
		// 継承元の初期化メソッド実行
		this.__proto__.__proto__.initialize.apply(this, [argument]);

		// グラフリソースリンクがドロップされた際にグラフを描画する。
		var instance = this;
		$("#" + this.$el.attr("id")).droppable({
			accept: function(element){
				if(element.find("A").length > 0){
					return true;
				}else{
					return false;
				}
			},
			tolerance : "fit",
			drop : function(event, ui){

				// ドロップ時の位置を特定しておく。
				var perspectiveModel =
					perspectiveView.findPerspectiveFromId(instance.$el.attr("id"));
				var drop_area_id = perspectiveModel.get("drop_area_id");
				var dropAreaOffset = $("#" + drop_area_id).offset();

				var resourceId = ui.helper.find("A").attr("id");
				var treeModel = resourceTreeView.collection.get(resourceId);

				// TODO 指定したリソースが一位に特定できるか確認する。

				var resourceModel = new wgp.MapElement();
				resourceModel.set({
					objectId : resourceId,
					objectName : "resourceGraph",
					pointX : event.clientX,
					pointY : event.clientY,
					width : 300,
					height : 300,
					zIndex : 1
				});
				instance.collection.add(resourceModel);

				var stateModel = new ENS.resourceStateElementModel();
				stateModel.set({
					objectId : resourceId + "_State",
					objectName : "resourceState",
					pointX : event.clientX - 190,
					pointY : event.clientY - 140,
					width : 50,
					height : 50,
					zIndex : 1,
					stateId : "normal",
					linkId : "group1"
				});
				instance.collection.add(stateModel);
			}
		
		});

		// 本クラスのrenderメソッド実行
		this.renderExtend();
	},
	renderExtend : function(){
		this.onLoad();
		return this;
	},
	destroy : function (){
		this.$el.children().remove();
	},
	// 要素が追加された際に、自身の領域に要素のタイプに応じて描画要素を追加する。
	onAdd : function(model){
		var objectName = model.get("objectName");

		// リソースグラフの場合はグラフを描画する。
		if("resourceGraph" == objectName){
			var graphView = this._addGraphDivision(model);
			this.viewCollection[model.id] = graphView;

		}else if("resourceState" == objectName){
			var stateView = this._addStateElement(model);
			this.viewCollection[model.id] = stateView;

		}else{

			// 継承元の追加メソッドを実行する。
			this.__proto__.__proto__.initialize.onAdd(this, [model]);
		}

	},
	_addGraphDivision : function(model) {
		var graphId = model.get("objectId");
		var tempId = graphId.split("/");
		var dataId = tempId[tempId.length - 1];
		var treeSettings = {
			data : dataId,
			id : graphId,
			measurementUnit : "",
			parentTreeId : "",
			treeId : ""
		};
		var viewAttribute = {
			id : graphId,
			rootView : this,
			graphId : graphId,
			title : dataId,
			noTermData : false,
			term : 1800 * 2,
			width : 260,
			height : 200,
			dateWindow : [ new Date() - 60 * 60 * 1000, new Date() ],
			attributes : {
				xlabel : "Time",
				ylabel : "value",
				labels : [ "time", "PC1", "PC2" ]
			}
		};

		var newDivAreaId = this.$el.attr("id") + "_" + model.cid;
		var newDivArea = $("<div id='" + newDivAreaId + "'></div>");

		$("#" + this.$el.attr("id")).append(newDivArea);
		newDivArea.width(model.get("width"));
		newDivArea.height(model.get("height"));

		$.extend(true, viewAttribute, {
			id : newDivAreaId
		});
		var view =
			new ENS.ResourceGraphElementView(viewAttribute, treeSettings);

		// 後で移動する。
		newDivArea.offset({
			top : model.get("pointY"),
			left : model.get("pointX")
		});
		newDivArea.draggable();
		return view;
	},
	_addStateElement : function(model){
		var argument = {
			paper : this.paper,
			model : model
		};

		var view =
			new ENS.ResourceStateElementView(argument);
		return view;
	},
	onCreate : function(){
		var instance = this;
		console.log("click create");

		var createMapDialog = $("<div title='Create new Map'></div>");
		createMapDialog.append("<p> Please enter new Map name</p>");

		var mapNameLabel = $("<label for='mapName'>Map Name</label>");
		var mapNameText = 
			$("<input type='text' name='mapName' id='mapName' class='text ui-widget-content ui-corner-all'>");
		createMapDialog.append(mapNameLabel);
		createMapDialog.append(mapNameText);

		createMapDialog.dialog({
			autoOpen: false,
			height: 300,
			width: 350,
			modal: true,
			buttons : {
				"OK" : function(){

					if(mapNameText.val().length == 0){
						alert("Map Name is require");
						return;
					}
					
					var setting = {
						data : {
							name : mapNameText.val(),
							data : "{}"
						},
						url : wgp.common.getContextPath() + "/map/insert"
					}
					instance.ajaxHandler.requestServerSync(setting);
					createMapDialog.dialog("close");

				},
				"CANCEL" : function(){
					createMapDialog.dialog("close");
				}
			},
			close : function(event){
				createMapDialog.remove();
			}
		});

		createMapDialog.dialog("open");
	},
	onSave : function(treeModel){
		console.log("click save");
		var resourceArray = [];
		_.each(this.collection.models, function(model, index){
			resourceArray.push(model.toJSON());
		});

		var resourceMap = {
			resources : resourceArray
		}

		var setting = {
			data : {
				mapId : treeModel.get("id"),
				name : treeModel.get("data"),
				data : JSON.stringify(resourceMap)
			},
			url : wgp.common.getContextPath() + "/map/update"
		}
		this.ajaxHandler.requestServerSync(setting);
	},
	onLoad : function(){

		// コレクションをリセット
		this.collection.reset();
		
		// マップ情報取得
		var mapId = this.mapId;
		var mapData = this.getMapData(mapId);
		var resources = mapData["resources"];
		var instance = this;
		_.each(resources, function(resource, index){
			var mapElement = new wgp.MapElement(resource);
			instance.collection.add(mapElement);
		});

	},
	getMapData : function(mapId){
		var setting = {
			data : {
				mapId : mapId
			},
			url : wgp.common.getContextPath() + "/map/getById"
		}
		var result = this.ajaxHandler.requestServerSync(setting);
		var mapInfo = $.parseJSON(result);
		return $.parseJSON(mapInfo["mapData"]);
	}
});