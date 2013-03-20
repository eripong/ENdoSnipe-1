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
function curveLine(elementProperty, paper) {

	// 設定が取得できない場合は処理を終了する。
	if (!elementProperty) {
		return this;
	}

	// 数値に直す。
	elementProperty.pointX = parseFloat(elementProperty.pointX);
	elementProperty.pointY = parseFloat(elementProperty.pointY);
	elementProperty.width = parseFloat(elementProperty.width);
	elementProperty.height = parseFloat(elementProperty.height);
	if (elementProperty.controlPoint1X) {
		elementProperty.controlPoint1X = parseFloat(elementProperty.controlPoint1X);
	} else {
		elementProperty.controlPoint1X = elementProperty.pointX
				+ elementProperty.width / 2 + elementProperty.height / 2;
	}
	if (elementProperty.controlPoint1Y) {
		elementProperty.controlPoint1Y = parseFloat(elementProperty.controlPoint1Y);
	} else {
		elementProperty.controlPoint1Y = elementProperty.pointY
				- elementProperty.width / 2 + elementProperty.height / 2;
	}
	if (elementProperty.controlPoint2X) {
		elementProperty.controlPoint2X = parseFloat(elementProperty.controlPoint2X);
	} else {
		elementProperty.controlPoint2X = elementProperty.pointX
				+ elementProperty.width / 2 - elementProperty.height / 2;
	}
	if (elementProperty.controlPoint2Y) {
		elementProperty.controlPoint2Y = parseFloat(elementProperty.controlPoint2Y);
	} else {
		elementProperty.controlPoint2Y = elementProperty.pointY
				+ elementProperty.width / 2 + elementProperty.height / 2;
	}

	var positionArray = this.createPosition(elementProperty);
	this.createMapElement(positionArray, paper);
	this.setAttributes(elementProperty);

	if (!elementProperty.lineType) {
		elementProperty.lineType = "";
	}

	this.object.node.setAttribute(raphaelMapConstants.OBJECT_ID_NAME,
			elementProperty.objectId);
	this.objectId = elementProperty.objectId;

	this.objectType_ = raphaelMapConstants.CURVE_LINE_TYPE_NAME;
	this.elementName_ = raphaelMapConstants.CURVELINE_ELEMENT_NAME;

	this.x = elementProperty.pointX;
	this.y = elementProperty.pointY;
	this.width = elementProperty.width;
	this.height = elementProperty.height;
	this.controlPoint1X = elementProperty.controlPoint1X;
	this.controlPoint1Y = elementProperty.controlPoint1Y;
	this.controlPoint2X = elementProperty.controlPoint2X;
	this.controlPoint2Y = elementProperty.controlPoint2Y;

	return this;
}
curveLine.prototype = new mapElement();

curveLine.prototype.createPosition = function(elementProperty) {

	// ポジションリスト
	var position = {};
	// 始点位置情報
	var startPosition = new Position(elementProperty.pointX,
			elementProperty.pointY);
	var endPosition = new Position(elementProperty.pointX
			+ elementProperty.width, elementProperty.pointY
			+ elementProperty.height);
	var controlPosition1 = new Position(elementProperty.controlPoint1X,
			elementProperty.controlPoint1Y);
	var controlPosition2 = new Position(elementProperty.controlPoint2X,
			elementProperty.controlPoint2Y);

	position = {
		startPosition : startPosition,
		endPosition : endPosition,
		controlPosition1 : controlPosition1,
		controlPosition2 : controlPosition2
	};

	return position;
};

curveLine.prototype.createPathString = function(positionArray) {

	if (!positionArray) {
		return null;
	}

	// サイズ
	var size = positionArray.length;
	// パス
	var pathArray = [];
	var index = -1;

	pathArray[++index] = 'm ';
	pathArray[++index] = positionArray.startPosition.x;
	pathArray[++index] = ',';
	pathArray[++index] = positionArray.startPosition.y;
	pathArray[++index] = ' C';
	$.each(positionArray, function(key, position) {
		if (key.indexOf('controlPosition') >= 0) {
			pathArray[++index] = position.x;
			pathArray[++index] = ',';
			pathArray[++index] = position.y;
			pathArray[++index] = ' ';
		}
	});
	pathArray[++index] = positionArray.endPosition.x;
	pathArray[++index] = ',';
	pathArray[++index] = positionArray.endPosition.y;
	return pathArray.join('');
};

curveLine.prototype.setFrame = function(mapAreaViewItem) {
	if (!this.frame) {
		var paper = mapAreaViewItem.entity;
		var raphaelMapManagerInstance = mapAreaViewItem.managerInstance;

		// 図形の端に表示するオブジェクトの作成
		var RADIUS_SIZE = raphaelMapConstants.RADIUS_SIZE; // 枠の半径

		this.frame = {};
		var frameLeftUpper = new ellipseSmall(this.x - (RADIUS_SIZE / 2),
				this.y - (RADIUS_SIZE / 2), RADIUS_SIZE, RADIUS_SIZE, paper);
		frameLeftUpper.parentObject_ = this;
		frameLeftUpper.object.node.setAttribute(
				raphaelMapConstants.OBJECT_ID_NAME, this.objectId);

		var frameRightBottom = new ellipseSmall(this.x + this.width
				- (RADIUS_SIZE / 2), this.y + this.height - (RADIUS_SIZE / 2),
				RADIUS_SIZE, RADIUS_SIZE, paper);

		frameRightBottom.parentObject_ = this;
		frameRightBottom.object.node.setAttribute(
				raphaelMapConstants.OBJECT_ID_NAME, this.objectId);

		this.frame[raphaelMapConstants.LEFT_UPPER] = frameLeftUpper;
		this.frame[raphaelMapConstants.RIGHT_UNDER] = frameRightBottom;

		var instance = this;
		$.each(this.frame, function(index, frameElement) {
			frameElement.object.mousedown(function(event) {
				raphaelMapManagerInstance.setOnMouseButton(event.button);
			});
			frameElement.object.drag(function(dx, dy) {
				raphaelMapManagerInstance.resizeElement(instance, index, dx,
						dy, mapAreaViewItem);
			}, function(x, y, event) {
				raphaelMapManagerInstance.startResizeElement(instance, index,
						x, y, event, mapAreaViewItem);
			}, function(event) {

			});

		});

	} else {
		$.each(this.frame, function(index, frameObject) {
			frameObject.object.show();
		});
	}
};

curveLine.prototype.resize = function(x, y, width, height) {
	var elementProperty = {
		pointX : x,
		pointY : y,
		width : width,
		height : height,
		controlPoint1X : x + width / 2 + height / 2,
		controlPoint1Y : y - width / 2 + height / 2,
		controlPoint2X : x + width / 2 - height / 2,
		controlPoint2Y : y + width / 2 + height / 2
	};
	var positionArray = this.createPosition(elementProperty);
	var path = this.createPathString(positionArray);
	this.object.attr("path", path);
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	this.controlPoint1X = elementProperty.controlPoint1X;
	this.controlPoint1Y = elementProperty.controlPoint1Y;
	this.controlPoint2X = elementProperty.controlPoint2X;
	this.controlPoint2Y = elementProperty.controlPoint2Y;
};