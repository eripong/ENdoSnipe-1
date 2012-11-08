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
var divWindowCreator = function(){

	var instance = this;
	divWindowCreator = function(){
		return instance;
	};
};

/**
 * ウィンドウを作成する。
 * @param window_class_name ウィンドウのクラス名
 * @param window_id ウィンドウID
 * @param divId 作成先のdivId
 */
divWindowCreator.prototype.createWindow = function(
	window_class_name,
	window_id,
	divId, options){
	
	// 作成対象となるウィンドウのないよう部分のクラスインスタンスを取得
	var windowObject = eval("new " + window_class_name + "(options)");

	// ウィンドウIDの設定
	windowObject.window_id = window_id;

	// ウィンドウのビュアー部分を作成する。
	this.createView(windowObject, divId);

	// ウィンドウにイベントを設定する。
	this.setEventFunction(windowObject);

	return windowObject;
};

/**
 * ウィンドウのビュアー部分を作成する。
 */
divWindowCreator.prototype.createView = function(windowObject, divId){
	windowObject.createView(divId);
};

/**
 * ウィンドウにイベントを設定する。
 * @param windowObject
 */
divWindowCreator.prototype.setEventFunction = function(windowObject){
	windowObject.setEventFunction();
};

/**
 * ウィンドウにて使用するメニューを生成する。
 */
divWindowCreator.prototype.createWindowMenu = function(){
};