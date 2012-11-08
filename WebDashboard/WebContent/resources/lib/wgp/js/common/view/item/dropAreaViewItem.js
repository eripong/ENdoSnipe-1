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
 * ドロップ領域表示物
 */
function dropAreaViewItem(){
	// 自身の実体を保持するフィールド
	this.entity = null;
	this.id = null;
};

/**
 * ドロップ領域表示物の実体を生成する。
 */
dropAreaViewItem.prototype.createViewItem = function(div_tag_id, data, option){
	this.entity = $("#" + div_tag_id);
	this.id = div_tag_id;
};

/**
 * ドロップ領域表示物のプロパティを取得する。
 */
dropAreaViewItem.prototype.getProperty = function(){};

/**
 * ドロップ領域表示物のプロパティを設定する。
 */
dropAreaViewItem.prototype.setProperty = function(property){};

/**
 * ドロップ領域表示物の実体を取得する。
 */
dropAreaViewItem.prototype.getEntity = function(){
	return this.entity;
};