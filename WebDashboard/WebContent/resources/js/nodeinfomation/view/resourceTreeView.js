ENS.ResourceTreeView = wgp.TreeView.extend({
	defaults : {},
	initialize : function(argument){

		// 継承元の初期化メソッド実行
		this.__proto__.__proto__.initialize.apply(this, [argument]);

		this.targetId = argument["targetId"];
		this.mode = argument["mode"];

		var instance = this;

		// リソース表示モードの場合はクリック時にリソースを表示する。
		if("display" === this.mode){
			$("#" + this.$el.attr("id")).mousedown(function(event) {
				var target = event.target;
				if ("A" == target.tagName) {
					var treeId = $(target).attr("id");
					var treeModel = instance.collection.get(treeId);
					instance.clickModel(treeModel);
				}
			});

		// リソース配置モードの場合はドラッグ時にリソースを配置する。
		}else if("arrangement" === this.mode){

			// TODO パースペクティブから取得する。
			$("#" + this.$el.attr("id")).draggable({
				helper : "clone",
				handle : "A",
				start  : function(event, ui){
					ui.helper.hide();

					var selectResource = ui.helper.find("A.jstree-hovered").parent("li");
					selectResource.addClass("dragTarget");

					var selectTop = selectResource.offset()["top"];
					var selectLeft = selectResource.offset()["left"];

					ui.helper.appendTo("body");

					ui.helper.css("zIndex", 1000);
					ui.helper.find("ul[class!=dragTarget]").remove();
					ui.helper.append(selectResource);

					ui.helper.removeClass("jstree-focused");
					ui.helper.css("position", "absolute");
					ui.helper.width("auto");
					ui.helper.height("auto");
				},
				drag : function(event, ui){

					setTimeout(function(){
						ui.helper.css({
							top : event.clientY,
							left : event.clientX
						});
						ui.helper.show();
					});
				}
			});
		}

	},
	clickModel : function(treeModel) {
		if (this.childView) {
			var tmpAppView = new wgp.AppView();
			tmpAppView.removeView(this.childView);
			this.childView = null;
		}
		$("#" + this.targetId).children().remove();
	
		var viewSettingName = treeModel.get("viewSettingName");
	
		var viewSettings = null;
		$.each(wgp.constants.VIEW_SETTINGS, function(index, value) {
			if (viewSettingName === index) {
				viewSettings = value;
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
	}
});