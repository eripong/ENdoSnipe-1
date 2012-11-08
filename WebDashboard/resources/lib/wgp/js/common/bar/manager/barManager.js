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
var barManager = function(){

	// バーの作成を行なうクラスインスタンスを取得する。
	this.barCreator = new barCreator();

	// 各バーを区別するためのID(払出用)
	this.max_bar_id = 0;

	// 作成したバーを格納する配列
	this.bar_list = {};

	// シングルトンのための宣言
	var instance = this;
	barManager = function(){
		return instance;
	};
};

/**
 * バーを追加する
 * @param bar_class_name 追加するバーのクラス名
 * @param divId 追加するDIVタグのID
 */
barManager.prototype.addBar = function(bar_class_name, divId){

	// バーIDのカウントアップ及び設定
	this.max_bar_id = this.max_bar_id + 1;
	var barObject = this.barCreator.createBar(bar_class_name, this.max_bar_id, divId);
};