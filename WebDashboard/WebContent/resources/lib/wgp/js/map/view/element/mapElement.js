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
function mapElement() {
	this.moveX_ = 0;
	this.moveY_ = 0;
}

/**
 * create method on map.
 * 
 * @param positionArray
 *            array.
 * @param paper
 *            paper.
 */
mapElement.prototype.createMapElement = function(positionArray, paper) {
	var path = this.createPathString(positionArray);
	this.object = paper.path(path);
	this.object.attr({
		fill : "white"
	});
};

/**
 * get element size.
 * 
 * @returns element size
 */
mapElement.prototype.getElementSize = function() {
	return $.extend(true, {}, {
		x : this.x,
		y : this.y,
		width : this.width,
		height : this.height
	});
};

/**
 * convert array to path string.
 * 
 * @param positionArray
 *            positions
 * @returns path
 */
mapElement.prototype.createPathString = function(positionArray) {
	if (!positionArray) {
		return null;
	}
	var size = positionArray.length;
	// パス
	var path = "m ";
	for ( var index = 0; index < size; index++) {
		var position = positionArray[index];
		path = path + " " + position.x + "," + position.y;
	}
	path = path + " z";
	return path;
};

mapElement.prototype.setEventFunction = function(mapAreaViewItem) {
	// not good object
};

/**
 * move object.
 * 
 * @param moveX
 *            moment of move x
 * @param moveY
 *            moment of move y
 */
mapElement.prototype.moveElement = function(moveX, moveY) {

	// 対象オブジェクトにフレームが存在する場合は、フレームを合成させて移動を行う
	if (this.frame) {
		$.each(this.frame, function(index, frameElement) {
			frameElement.object.translate(moveX, moveY);
			frameElement.x = frameElement.x + moveX;
			frameElement.y = frameElement.y + moveY;
		});
	}
	this._moveElementObject(moveX, moveY);
	this.x = this.x + moveX;
	this.y = this.y + moveY;

	if (this.cx) {
		this.cx = this.cx + moveX;
	}

	if (this.cy) {
		this.cy = this.cy + moveY;
	}

	// 移動対象のオブジェクトが他オブジェクトとconnectしている場合、線を再描画する
	this.refactorConnectLine();
};

/**
 * move to use transform method
 * 
 * @param moveX
 *            moment of moveX
 * @param moveY
 *            moment of moveY
 */
mapElement.prototype._moveElementObject = function(moveX, moveY) {
	this.moveX_ = this.moveX_ + moveX;
	this.moveY_ = this.moveY_ + moveY;
	var transValue = "t" + this.moveX_ + "," + this.moveY_;
	this.object.transform(transValue);
};

/**
 * resize of path.
 * 
 * @param ratioX
 *            moment of resize ratio x
 * @param ratioY
 *            moment of resize ratio y
 * @param ellipsePosition
 *            small ellipse position
 */
mapElement.prototype.resize = function(ratioX, ratioY, ellipsePosition) {
	// resize element
	var paths = this.object.attr("path");
	var originX = this.x - this.moveX_;
	var originY = this.y - this.moveY_;
	$.each(paths, function(index, path) {
		if (path.length == 3) {
			path[1] = originX + (path[1] - originX) * ratioX;
			path[2] = originY + (path[2] - originY) * ratioY;
		}
	});
	this.object.attr("path", paths);

	// move element
	var afterWidth = this.width * ratioX;
	var afterHeight = this.height * ratioY;
	var moveX;
	var moveY;
	var ellipseMoveX;
	var ellipseMoveY;
	if (ellipsePosition == raphaelMapConstants.LEFT_UPPER
			|| ellipsePosition == raphaelMapConstants.LEFT_UNDER) {
		moveX = this.width - afterWidth;
		ellipseMoveX = moveX;
	} else {
		moveX = 0;
		ellipseMoveX = afterWidth - this.width;
	}
	if (ellipsePosition == raphaelMapConstants.LEFT_UPPER
			|| ellipsePosition == raphaelMapConstants.RIGHT_UPPER) {
		moveY = this.height - afterHeight;
		ellipseMoveY = moveY;
	} else {
		moveY = 0;
		ellipseMoveY = afterHeight - this.height;
	}
	this._moveElementObject(moveX, moveY);

	this.x = this.x + moveX;
	this.y = this.y + moveY;
	this.width = afterWidth;
	this.height = afterHeight;

	this.resizeFrame(ellipseMoveX, ellipseMoveY, ellipsePosition);
	this.refactorConnectLine();
};

/**
 * move frame when object resized.
 * 
 * @param moveX
 *            moment of move x
 * @param moveY
 *            moment of move y
 * @param ellipsePosition
 *            position of frame ellipse.
 */
mapElement.prototype.resizeFrame = function(moveX, moveY, ellipsePosition) {
	if (this.frame == null) {
		return;
	}

	if (ellipsePosition == raphaelMapConstants.LEFT_UPPER
			|| ellipsePosition == raphaelMapConstants.LEFT_UNDER) {
		this.frame[raphaelMapConstants.LEFT_UPPER].object.translate(moveX, 0);
		this.frame[raphaelMapConstants.LEFT_UNDER].object.translate(moveX, 0);
	} else {
		this.frame[raphaelMapConstants.RIGHT_UNDER].object.translate(moveX, 0);
		this.frame[raphaelMapConstants.RIGHT_UPPER].object.translate(moveX, 0);
	}
	if (ellipsePosition == raphaelMapConstants.LEFT_UPPER
			|| ellipsePosition == raphaelMapConstants.RIGHT_UPPER) {
		this.frame[raphaelMapConstants.LEFT_UPPER].object.translate(0, moveY);
		this.frame[raphaelMapConstants.RIGHT_UPPER].object.translate(0, moveY);
	} else {
		this.frame[raphaelMapConstants.RIGHT_UNDER].object.translate(0, moveY);
		this.frame[raphaelMapConstants.LEFT_UNDER].object.translate(0, moveY);
	}
};

/**
 * resize connector line.
 */
mapElement.prototype.refactorConnectLine = function() {
	if (this.connectFromLineList) {
		$.each(this.connectFromLineList, function(index, connectFromLine) {
			connectFromLine.resize();
		});
	}

	if (this.connectToLineList) {
		$.each(this.connectToLineList, function(index, connectToLine) {
			connectToLine.resize();
		});
	}
};

/**
 * set frame small ellipse
 */
mapElement.prototype.setFrame = function() {
	if (!this.frame) {
		var paper = this.object.paper;
		var RADIUS_SIZE = raphaelMapConstants.RADIUS_SIZE;
		var frameLeftUpper = new ellipseSmall(this.x - (RADIUS_SIZE / 2),
				this.y - (RADIUS_SIZE / 2), RADIUS_SIZE, RADIUS_SIZE, paper);
		this._setFrameId(frameLeftUpper, raphaelMapConstants.LEFT_UPPER);

		var frameLeftBottom = new ellipseSmall(this.x - (RADIUS_SIZE / 2),
				this.y + this.height - (RADIUS_SIZE / 2), RADIUS_SIZE,
				RADIUS_SIZE, paper);
		this._setFrameId(frameLeftBottom, raphaelMapConstants.LEFT_UNDER);

		var frameRightUpper = new ellipseSmall(this.x + this.width
				- (RADIUS_SIZE / 2), this.y - (RADIUS_SIZE / 2), RADIUS_SIZE,
				RADIUS_SIZE, paper);
		this._setFrameId(frameRightUpper, raphaelMapConstants.RIGHT_UPPER);

		var frameRightBottom = new ellipseSmall(this.x + this.width
				- (RADIUS_SIZE / 2), this.y + this.height - (RADIUS_SIZE / 2),
				RADIUS_SIZE, RADIUS_SIZE, paper);
		this._setFrameId(frameRightBottom, raphaelMapConstants.RIGHT_UNDER);

		this.frame = {};
		this.frame[raphaelMapConstants.LEFT_UPPER] = frameLeftUpper;
		this.frame[raphaelMapConstants.LEFT_UNDER] = frameLeftBottom;
		this.frame[raphaelMapConstants.RIGHT_UPPER] = frameRightUpper;
		this.frame[raphaelMapConstants.RIGHT_UNDER] = frameRightBottom;
	} else {
		$.each(this.frame, function(index, frameObject) {
			frameObject.object.show();
		});
	}
};

/**
 * set ellipse id.
 * 
 * @param frameObject
 *            target object
 * @param ellipsePosition
 *            ellipse position
 */
mapElement.prototype._setFrameId = function(frameObject, ellipsePosition) {
	frameObject.parentObject_ = this;
	frameObject.object.node.setAttribute(raphaelMapConstants.OBJECT_ID_NAME,
			this.objectId);
	frameObject.object.node.setAttribute(
			raphaelMapConstants.SMALL_ELLIPSE_POSITION, ellipsePosition);
};

mapElement.prototype.setZIndex = function(zIndex) {
	this.zIndex_ = zIndex;
};

mapElement.prototype.getZIndex = function() {
	return this.zIndex_;
};

/**
 * get front node object
 * 
 * @returns {Raphael} node object
 */
mapElement.prototype.getFrontNode = function() {
	return this.object;
};

/**
 * z index adjust method
 * 
 * @returns {Raphael} node object
 */
mapElement.prototype.getBottomNode = function() {
	return this.object;
};

/**
 * move to front on target
 * 
 * @param target
 *            {Raphael} raphael object
 */
mapElement.prototype.moveFront = function(target) {
	$(target.node).after(this.object.node);
};

/**
 * move to back on target
 * 
 * @param target
 *            {Raphael} raphael object
 */
mapElement.prototype.moveBack = function(target) {
	$(target.node).before(this.object.node);
};

/**
 * reset frame object.
 */
mapElement.prototype.resetFrame = function() {
	if (this.frame) {
		this.frame[raphaelMapConstants.LEFT_UPPER].object.remove();
		this.frame[raphaelMapConstants.LEFT_UNDER].object.remove();
		this.frame[raphaelMapConstants.RIGHT_UPPER].object.remove();
		this.frame[raphaelMapConstants.RIGHT_UNDER].object.remove();
		delete this.frame;
	}
	this.setFrame();
};

/**
 * hide object
 */
mapElement.prototype.hide = function() {
	this.object.hide();
	this.hideFrame();
};

/**
 * hide frame object
 */
mapElement.prototype.hideFrame = function() {
	if (!this.frame) {
		return;
	}
	$.each(this.frame, function(index, frameObject) {
		frameObject.object.hide();
	});
};

/**
 * set attributes data
 * 
 * @param elementProperty
 *            attributes data
 */
mapElement.prototype.setAttributes = function(elementProperty) {
	var instance = this;
	var attributes = elementProperty.attributes;
	if (attributes) {
		$.each(attributes, function(index, attr) {
			instance.object.attr(index, attr);
		});
	}
	this.attributes = elementProperty.attributes;
};

/**
 * get attributes data.
 * 
 * @returns {Object} attributes data
 */
mapElement.prototype.getAttributes = function() {
	return {
		attributes : this.attributes
	};
};

mapElement.prototype.getProperty = function() {
	return this._getBaseElement();
};

/**
 * get base data.
 */
mapElement.prototype._getBaseElement = function() {
	var data = {
		objectType : this.objectType_,
		objectId : this.objectId,
		pointX : this.x,
		pointY : this.y,
		width : this.width,
		height : this.height,
		objectName : this.elementName_
	};
	return data;
};

function Position(x, y, width, height, value) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	this.initValue = value;
	this.URL = null;
	return this;
}