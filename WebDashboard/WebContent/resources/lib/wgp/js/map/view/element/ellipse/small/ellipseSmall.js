/*****************************************************************
 WGP  1.0B  - Web Graphical Platform
   (https://sourceforge.net/projects/wgp/)

 The MIT License (MIT)
 
 Copyright (c) 2012 Acroquest Technology Co.,Ltd.
 
 Permission is hereby granted, free of charge, to any person obtaining 
 a copy of this software and associated documentation files
 (the "Software"), to deal in the Software without restriction, 
 including without limitation the rights to use, copy, modify, merge,
 publish, distribute, sublicense, and/or sell copies of the Software,
 and to permit persons to whom the Software is furnished to do so, 
 subject to the following conditions:
 
 The above copyright notice and this permission notice shall be 
 included in all copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
 SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*****************************************************************/
function ellipseSmall(x, y, width, height, paper) {

	// ポジションリスト
	var positionArray = [];
	// 位置情報
	var position = new Position(parseFloat(x), parseFloat(y),
			parseFloat(width), parseFloat(height));

	positionArray.push(position);
	this.createMapElement(positionArray, paper);
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	this.cx = x + (width / 2);
	this.cy = y + (height / 2);

	this.elementName_ = raphaelMapConstants.ELLIPSE_ELEMENT_NAME;
	this.object.node.setAttribute(raphaelMapConstants.OBJECT_TYPE_FIELD_NAME,
			raphaelMapConstants.ELLIPSESMALL_ELEMENT_NAME);

	return this;
}
ellipseSmall.prototype = new ellipse();