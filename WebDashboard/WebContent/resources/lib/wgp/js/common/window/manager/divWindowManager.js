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
var divWindowManager = function(){

	// ウィンドウの作成を行なうクラスインスタンスを取得する。
	this.creator = new divWindowCreator();

	// 各ウィンドウを区別するためのID(払出用)
	this.max_id = 0;

	// 作成したウィンドウを格納する配列
	this.window_list = {};

	// シングルトンのための宣言
	var instance = this;
	divWindowManager = function(){
		return instance;
	};
};

/**
 * ウィンドウを追加する。
 * @param window_class_name ウィンドウのクラス名
 * @param divId 作成先のdivタグ
 */
divWindowManager.prototype.addWindow = function(window_class_name, divId, option){

	// ウィンドウIDのカウントアップ及び設定
	this.max_id = this.max_id + 1;
	var windowObject = this.creator.createWindow(window_class_name, this.max_id ,divId, option);

	// ウィンドウを管理対象に追加
	this.window_list[this.max_id] = windowObject;
	windowObject.manager = this;
	return this.max_id;
};

/**
 * @param int windowのID
 * @returns {divWindow} windowオブジェクト 
 */
divWindowManager.prototype.getWindow = function(id){
	return this.window_list[id];
};

/**
 * @param windowId windows id
 * @param observer {Observer} observer
 */
divWindowManager.prototype.addObserver = function(windowId, observer){
	var windowObject = this.window_list[windowId];
	windowObject.setObserver(observer);
	observer.addEvent();
};