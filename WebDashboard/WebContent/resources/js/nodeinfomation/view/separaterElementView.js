var SeparaterElementView = wgp.AbstractView.extend({
	initialize : function() {
		console.log('called initialize parent view');
		this.viewType = wgp.constants.VIEW_TYPE.VIEW;
		this.attributes = {};

		// div Tagの作成を行う。
		$("#" + this.$el.attr("id")).css({
			clear : "left"
		});

		this.maxId = 0;
	},
	render : function() {
		console.log('call render');
	},
	onAdd : function(element) {
		console.log('call onAdd');
	},
	onChange : function(element) {
		console.log('called changeModel');
	},
	onRemove : function(element) {
		console.log('called removeModel');
	}

});