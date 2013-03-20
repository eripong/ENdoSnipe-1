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
/**
 * 生成するDOMのDTOクラス
 * 
 * @param id
 *            id
 * @param domKind
 *            DOMの種別(DIV、input type="text"等・・・)
 * @param attributes
 *            属性(オブジェクト配列)
 * @param styleClass
 *            スタイルクラス(配列)
 * @returns
 */
wgp.wgpDomDto = function(id, domKind, attributes, styleClasses, styles) {
	this.id = id;
	this.domKind = domKind;
	this.attributes = attributes;
	this.styleClasses = styleClasses;
	this.styles = styles;
	this.children = [];
};

wgp.wgpDomDto.prototype.addChildren = function(children) {
	this.children = this.children.concat(children);
};