wgp.UtilBarTabModel = Backbone.Model.extend({
	defaults : {
		tabName : "",
		viewId : null
	},
	idAttribute : "tabId"
});

wgp.UtilBarTabList = Backbone.Collection.extend({
	model : wgp.UtilBarTabModel
});

wgp.UtilBarTabView = Backbone.View.extend({
	tagName : "ul",
	className : "tab_parent ui-helper-clearfix",
	events:{
		"click .tab_menu" : "clickTab",
		"mouseover .tab_menu" : "overTab",
		"mouseout .tab_menu" : "outTab"
	},
	initialize : function(){
	},
	render : function(){

		// タブコレクション追加時のタブ追加処理を行う。
		var instance = this;
		this.collection = new wgp.UtilBarTabList();
		this.collection.on("add", function(model){
			var tabName = model.get("tabName");
			var viewId = model.get("viewId");

			var liTag = $("<li></li>").addClass("tab_menu ui-state-default");
			var aTag = $("<a></a>").attr("href", "#" + viewId).text(tabName);
			liTag.append(aTag);
			instance.$el.append(liTag);

			// 1個以上ある場合は隠す。
			if(this.length > 1){
				$("#" + viewId).hide();
			}

			instance.updateTabState();
		});

		return this;
	},
	clickTab : function(event){

		// イベントから表示対象のビューIDを取得する。
		var showViewId = $(event.currentTarget).find("a").attr("href").substr(1);

		// イベント発信元から表示/非表示を切り替えるビューを全て取得する。
		$.each(this.$el.find("a"), function(index, aElement){
			var aTag = $(aElement);
			var viewId = aTag.attr("href").substr(1);
			if(viewId == showViewId){
				$("#" + viewId).show();
			}else{
				$("#" + viewId).hide();
			}
			
		});

		// タブの状態を更新する。
		this.updateTabState();
	},
	overTab : function(event) {
		$(event.currentTarget).addClass("ui-state-hover");
	},
	outTab : function(event) {
		$(event.currentTarget).removeClass("ui-state-hover");
	},
	updateTabState : function() {

		$.each(this.$el.find("a"), function(index, aElement){
			var aTag = $(aElement);
			var viewId = aTag.attr("href").substr(1);

			if($("#" + viewId).css("display") == "none"){
				aTag.parent("li").removeClass("ui-state-active");
			}else{
				aTag.parent("li").addClass("ui-state-active");
			}
			
		});
	}
});

wgp.UtilButtonModel = Backbone.Model.extend({
	defaults : {
		buttonId : null,
		buttonName : "",
		clickFunction : null
	},
	idAttribute : "buttonId"
});

wgp.UtilButtonList = Backbone.Collection.extend({
	model : wgp.UtilButtonModel
});

wgp.UtilBarView = Backbone.View
.extend({
	initialize : function(argument) {
		_.bindAll();
		this.setElement($("#" + argument["id"])[0]);
		this.render();
	},
	render : function() {
		this.tabView = new wgp.UtilBarTabView();
		var tabViewTag = this.tabView.render().$el;
		this.$el.append(tabViewTag);
	},
	addTab : function(utilTabModel) {
		this.tabView.collection.add(utilTabModel);
	}
});