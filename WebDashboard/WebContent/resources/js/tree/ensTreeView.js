ENS.treeView = wgp.TreeView.extend({
	setClickEvent : function(targetId){
		var instance = this;
		this.treeCollection = {};
		this.maxId = 0;
		this.targetId = targetId;
		$("#" + this.$el.attr("id")).mousedown(function(event) {
			var target = event.target;
			if ("A" == target.tagName) {
				var treeId = $(target).attr("id");
				var treeModel = instance.collection.get(treeId);
				instance.clickModel(treeModel);
			}
		});
	},
	clickModel : function(treeModel) {
		if (this.childView) {
			var tmpAppView = new wgp.AppView();
			tmpAppView.removeView(this.childView);
			this.childView = null;
		}
		$("#" + this.targetId).children().remove();

		var dataId = treeModel.get("id");

		var viewSettings = null;
		$.each(wgp.constants.VIEW_SETTINGS, function(index, value) {
			if (dataId.match(index)) {
				viewSettings = value;
				return false;
			}
		});
		if (viewSettings == null) {
			viewSettings = wgp.constants.VIEW_SETTINGS["default"];
			if (viewSettings == null) {
				return;
			}
		}
		var viewClassName = viewSettings.viewClassName;
		$.extend(true, viewSettings, {
			id : this.targetId
		});
		var treeSettings = treeModel.attributes;
		this.childView = eval("new " + viewClassName
				+ "(viewSettings, treeSettings)");
	},
	createTreeData : function(treeModel){
		var returnData = wgp.TreeView.prototype.createTreeData.call(this, treeModel);
		var titleData = returnData.data;
		if (titleData.title.indexOf("&#47;") >= 0) {
			titleData.title = titleData.title.split("&#47;").join("/");
		} 
		return returnData;
	}
});