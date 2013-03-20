ENS.resourceModel = Backbone.Model.extend({
	defaults : {
		elementId : null,
		elementType : "",
		x : null,
		y : null,
		width : null,
		height : null
	},
	idAttribute : "elementId",
});

ENS.resourceList = Backbone.Collection.extend({
	model : ENS.resourceModel
})

ENS.ResourceMapView = Backbone.View.extend({
	tagName : "div",
	initialize : function() {
		_.bindAll();
		var innerDiv = $("<div></div>");
		innerDiv.css({width:'100%', height:'100%'});
		innerDiv.addClass("resourceMap");
		$("#" + this.$el.attr("id")).append(innerDiv);

		this.innerDiv_ = innerDiv;
		this.collection = new ENS.resourceList();
		this.collectionIndex_ = 0;
		var instance = this;

		// 要素が追加された際に、自身の領域に要素のタイプに応じて描画要素を追加する。
		this.collection.on("add", function(model){
			var elementType = model.get("elementType");

			if("graph" == elementType){
				instance._addGraphDivision(model);
			}
		});

		var instance = this;
		$("#" + this.$el.attr("id")).droppable({
			accept: function(element){
				console.log(element);
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

				var resourceModel = new ENS.resourceModel();
				resourceModel.set({
					elementId : resourceId,
					elementType : "graph",
					x : event.clientX,
					y : event.clientY 
				});
				instance.collection.add(resourceModel);
			}
		
		});

		this.render();
	},
	render : function(){
		return this;
	},
	destroy : function (){
		this.$el.children().remove();
	},

	_addGraphDivision : function(model) {
		var graphId = model.get("elementId");
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

		this.innerDiv_.append(newDivArea);
		newDivArea.width(300);
		newDivArea.height(300);

		$.extend(true, viewAttribute, {
			id : newDivAreaId
		});
		var view =
			new ENS.ResourceGraphElementView(viewAttribute, treeSettings);

		// 後で移動する。
		newDivArea.offset({top : model.get("y"), left : model.get("x") });
		newDivArea.draggable();
		return view;
	}
});