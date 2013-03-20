/*******************************************************************************
 * WGP 1.0B - Web Graphical Platform (https://sourceforge.net/projects/wgp/)
 * 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2012 Acroquest Technology Co.,Ltd.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
function ellipse(elementProperty, paper) {

	// 設定が取得できない場合は処理を終了する。
	if (!elementProperty) {
		return this;
	}

	// 数値に直す。
	elementProperty.pointX = parseFloat(elementProperty.pointX);
	elementProperty.pointY = parseFloat(elementProperty.pointY);
	elementProperty.width = parseFloat(elementProperty.width);
	elementProperty.height = parseFloat(elementProperty.height);

	var positionArray = this.createPositionArray(elementProperty);
	this.createMapElement(positionArray, paper);
	if (!elementProperty.color) {
		elementProperty.color = "#FFFFFF";
	}
	if (!elementProperty.lineType) {
		elementProperty.lineType = "";
	}
	this.setAttributes(elementProperty);

	this.object.node.setAttribute(raphaelMapConstants.OBJECT_ID_NAME,
			elementProperty.objectId);
	this.object.node.setAttribute('class',
			raphaelMapConstants.CLASS_MAP_ELEMENT);

	this.objectId = elementProperty.objectId;

	this.objectType_ = raphaelMapConstants.POLYGON_TYPE_NAME;
	this.elementName_ = raphaelMapConstants.ELLIPSE_ELEMENT_NAME;

	this.x = elementProperty.pointX;
	this.y = elementProperty.pointY;
	this.width = elementProperty.width;
	this.height = elementProperty.height;
	this.cx = elementProperty.x + (elementProperty.width / 2);
	this.cy = elementProperty.y + (elementProperty.height / 2);

	return this;
}
ellipse.prototype = new mapElement();

ellipse.prototype.createPositionArray = function(elementProperty) {

	// ポジションのリスト
	var positionArray = [];
	var ellipsePosition = new Position(elementProperty.pointX,
			elementProperty.pointY, elementProperty.width,
			elementProperty.height);
	positionArray.push(ellipsePosition);

	return positionArray;
};

ellipse.prototype.getElementSize = function() {
	var cx = this.object.attr("cx") + this.object.matrix["e"];
	var cy = this.object.attr("cy") + this.object.matrix["f"];
	return $.extend(true, {}, {
		x : this.x,
		y : this.y,
		width : this.width,
		height : this.height,
		cx : cx,
		cy : cy
	});
};

ellipse.prototype.createMapElement = function(positionArray, paper) {
	// 位置情報取得
	var position = positionArray[0];

	// オブジェクト生成
	this.object = paper.ellipse(position.x + (position.width / 2), position.y
			+ (position.height / 2), position.width / 2, position.height / 2);

	this.object.attr({
		fill : "#FFFFFF"
	});
};

ellipse.prototype.resize = function(ratioX, ratioY, ellisePosition) {
	var afterWidth = this.width * ratioX;
	var afterHeight = this.height * ratioY;

	var afterX;
	var afterY;

	var moveX;
	var moveY;
	if (ellisePosition == raphaelMapConstants.LEFT_UPPER
			|| ellisePosition == raphaelMapConstants.LEFT_UNDER) {
		afterX = this.x + (this.width - afterWidth);
		moveX = (this.width - afterWidth) / 2;
	} else {
		afterX = this.x;
		moveX = (afterWidth - this.width) / 2;
	}
	if (ellisePosition == raphaelMapConstants.LEFT_UPPER
			|| ellisePosition == raphaelMapConstants.RIGHT_UPPER) {
		afterY = this.y + (this.height - afterHeight);
		moveY = (this.height - afterHeight) / 2;
	} else {
		afterY = this.y;
		moveY = (afterHeight - this.height) / 2;
	}
	this._moveElementObject(moveX, moveY);
	this.object.attr("rx", Math.abs(afterWidth) / 2);
	this.object.attr("ry", Math.abs(afterHeight) / 2);

	this.resizeFrame(moveX * 2, moveY * 2, ellisePosition);

	this.x = afterX;
	this.y = afterY;
	this.width = afterWidth;
	this.height = afterHeight;
	this.cx = this.object.attr("cx");
	this.cy = this.object.attr("cy");
};