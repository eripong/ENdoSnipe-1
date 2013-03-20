ENS.ResourceMapView = wgp.MapView.extend({
	tagName : "div",
	initialize : function(argument) {
		_.bindAll();

		var width = $("#" + this.$el.attr("id")).width();
		var height = $("#" + this.$el.attr("id")).height();
		_.extend(argument, {width : width, height : height});

		var ajaxhandler = new wgp.AjaxHandler();		

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
				var perspactiveModel =
					perspactiveView.findPerspectiveFromId(instance.$el.attr("id"));
				var drop_area_id = perspactiveModel.get("drop_area_id");
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

		// 継承先のrenderメソッド実行
		this.__proto__.__proto__.render.apply(this);
		this.render();
	},
	render : function(){
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
			this._addGraphDivision(model);

		}else if("resourceState" == objectName){
			this._addStateElement(model);

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
	}
});