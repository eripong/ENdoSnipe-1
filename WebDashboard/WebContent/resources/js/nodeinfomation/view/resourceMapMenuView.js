ENS.mapMenuModel = Backbone.Model.extend({
	defaults : {
		width : null,
		height : null,
		text : null,
		id : null,
		src : null,
		alt : null,
		onclick : null
	},
	idAttribute : "elementId",
});

ENS.mapMenuModelList = Backbone.Collection.extend({
	model :ENS.mapMenuModel
});

ENS.mapMenuView = wgp.AbstractView.extend({
	initialize : function(argument, settings) {
		this.collection = new ENS.mapMenuModelList();
		this.divId = this.$el.attr("id");
		this.registerCollectionEvent();
		
		var instance = this;
		var collection = argument["collection"];
		_.each(collection, function(element, index) {
			var mapMenuModel = new instance.collection.model(element);
			instance.collection.add(element);
		});
		this.render();
	},
	render : function(){
		return this;
	},
	onAdd : function(element) {
		var width = element.get("width");
		var height = element.get("height");
		var src = element.get("src");
		var alt = element.get("alt");
		var styleClass = element.get("styleClass");
		var onclick = element.get("onclick");
		
		// create Image Tag
		if (src != null) {
			var imgTag = $("<img></img>");
			imgTag.attr("width", width);
			imgTag.attr("height", height);
			imgTag.attr("src", src);
			imgTag.attr("alt", alt);
			imgTag.attr("class", styleClass);
			if (onclick != null && typeof onclick === "function") {
				imgTag.click(function(){
					onclick();
				});
			}
			$("#" + this.divId).append(imgTag);
		}
	},
	destroy : function (){
		this.$el.children().remove();
	}
});