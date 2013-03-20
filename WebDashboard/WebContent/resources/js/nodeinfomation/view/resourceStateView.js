ENS.resourceStateElementModel = wgp.MapElement.extend({
	defaults : {
		linkId : null,
		stateId : null
	}
});

// 状態ID毎の画像のURL定義
ENS.resourceStateURL = {
	"ced"    : "/resources/images/state/node_d_ced.png",
	"error"  : "/resources/images/state/node_error.png",
	"normal" : "/resources/images/state/node_normal.png",
	"warn"   : "/resources/images/state/node_warn.png"
};

ENS.ResourceStateElementView = wgp.MapElementView.extend({
	render : function(model){

		// 継承元のrenderメソッド実行
		this.__proto__.__proto__.render.apply(this, [model]);

		var elementProperty  = {
			pointX : model.get("pointX"),
			pointY : model.get("pointY"),
			width  : model.get("width"),
			height : model.get("height"),
			URL : wgp.common.getContextPath() + ENS.resourceStateURL[model.get("stateId")]
		};

		// 状態を表す画像を描画する。
		this.element = new image(elementProperty, this._paper);
	},
	registerModelEvent : function(){

		// When Model Change
		this.model.on('change', this.onChange, this);

		// WHen Model Remove
		this.model.on('remove', this.onRemove, this);
	},
	onChange : function(model) {

		var elementProperty  = {
			pointX : model.get("pointX"),
			pointY : model.get("pointY"),
			width  : model.get("width"),
			height : model.get("height"),
			URL : common.getContextPath() + ENS.resourceStateURL(model.get("stateId"))
		};

		// 再度設定する。
		this.element.setProperty(elementProperty);
	},
	onRemove : function(model){
		this.element.remove();
		this.destroy();
	}
});