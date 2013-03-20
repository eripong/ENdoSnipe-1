ENS.ResourceMapListView = wgp.TreeView.extend({
	defaults : {},
	initialize : function(argument){

		// 初期化
		$("#" + this.$el.attr("id")).children().remove();
		
		// 継承元の初期化メソッド実行
		this.__proto__.__proto__.initialize.apply(this, [argument]);

		this.targetId = argument["targetId"];

		var instance = this;


		// マップ一覧の要素がクリックされた際に紐付くマップを表示する。
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

		var treeSettings = treeModel.attributes;
		this.childView = new ENS.ResourceMapView({
			id : this.targetId
		});
	}
});