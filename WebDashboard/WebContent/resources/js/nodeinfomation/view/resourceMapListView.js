ENS.ResourceMapListView = wgp.TreeView.extend({
	defaults : {},
	initialize : function(argument){
		_.bindAll();

		// コレクションの初期化
		this.collection = new TreeModelList();

		// コレクション用イベントの登録
		this.registerCollectionEvent();
		
		// Ajax通信機能の初期化
		var ajaxHandler = new wgp.AjaxHandler();
		this.ajaxHandler = ajaxHandler;

		// ツリーの初期化
		$("#" + this.$el.attr("id")).children().remove();
		
		// 継承元の初期化メソッド実行
		this.__proto__.__proto__.initialize.apply(this, [argument]);

		this.targetId = argument["targetId"];

		// マップ一覧の要素がクリックされた際に紐付くマップを表示する。
		var instance = this;
		$("#" + this.$el.attr("id")).mousedown(function(event) {
			var target = event.target;
			if ("A" == target.tagName) {
				var treeId = $(target).attr("id");
				var treeModel = instance.collection.get(treeId);
				instance.clickModel(treeModel);
			}
		});

		// ツリー情報をサーバから読み込む
		this.onLoad();
	},
	clickModel : function(treeModel) {
		if (this.childView) {
			var tmpAppView = new wgp.AppView();
			tmpAppView.removeView(this.childView);
			this.childView = null;
		}
		$("#" + this.targetId).children().remove();

		var treeSettings = treeModel.attributes;
		this.childView = new ENS.ResourceMapView({
			id : this.targetId,
			mapId : treeModel.get("id")
		});
	},
	onLoad : function(){
		var instance = this;

		var setting = {
			data : {},
			url : wgp.common.getContextPath() + "/map/getAll"
		}

		var result = this.ajaxHandler.requestServerSync(setting);
		var mapListTree = $.parseJSON(result)["map"];
		_.each(mapListTree, function(tree, index){

			var treeModel = new wgp.TreeModel(tree);
			instance.collection.add(treeModel);
		});
	}
});