wgp.TextAreaModel = wgp.MapElement.extend({
	defaults:{
		text : null,
		fontSize : 8,
		textAnchor : "start"
	}
});

wgp.TextAreaView = wgp.MapElementView.extend({
	// 本クラスはテキストエリアを描画する。
	render : function(model){

		// 継承元のrenderメソッド実行
		this.__proto__.__proto__.render.apply(this, [argument]);

		var pointX = model.get("pointX");
		var pointY = model.get("pointY");
		var text = model.get("text");

		var textPosition = {
			x : pointX,
			y : pointY,
			initValue : text
		};

		var positionArray = createPositionArray(model);

		// テキストエリアを描画する。
		var position = positionArray[0];
		this.element = paper.text(position.x, position.y, position.initValue);
		this.element.attr("font-size", model.get("fontSize"));
		this.element.attr("text-anchor", model.get("textAnchor"))
		this.element.node.setAttribute("objectId", model.get("objectId"));
		return this;
	},
	createPositionArray : function(model){

		// ポジションのリスト
		var positionArray = [];
		// 左上ポジション
		var firstPosition = this.createPosition(model.get("pointX"),
				model.get("pointY"));
		positionArray.push(firstPosition);
		// 右上ポジション
		var secondPosition = this.createPosition(elementProperty.width, 0);
		positionArray.push(secondPosition);
		// 左下ポジション
		var thirdPosition = this.createPosition(0, elementProperty.height);
		positionArray.push(thirdPosition);
		// 右下ポジション
		var forthPosition = this.createPosition(-1 * elementProperty.width, 0);
		positionArray.push(forthPosition);

		return positionArray;
	},
	adjustPosition : function(model){
		var fontSize = model.get("fontSize");
		var textAnchor = model.get("textAnchor");
		//TODO 揃えに応じたポジション設定
	}
});