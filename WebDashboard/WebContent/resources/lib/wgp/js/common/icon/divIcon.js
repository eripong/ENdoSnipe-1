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
 * divタグのアイコンを作成するための関する
 * @param div_tag_id 作成先のdivタグ
 * @param option 作成したアイコンのオプション
 * @param createViewFunction アイコンの中身の描画関数(アイコンのdivタグを引数とする。)
 * @param clickEventFunction アイコンをクリックした際に実行するイベント
 * 
 */
function divIcon(
	div_tag_id
	,option
	,createViewFunction
	,clickEventFunction){

	// アイコン名を取得する。
	var iconName = option["name"];
	var iconDivId = div_tag_id + "_" + iconName;

	// 属性が定義されている場合は取得する。
	var attributes = null;
	if(option["attribute"]){
		attributes = option["attribute"];
	}

	// スタイル属性が定義されている場合は取得する。
	var styles = null;
	if(option["style"]){
		styles = option["style"];
	}

	// スタイルクラスが定義されている場合は取得する。
	var styleClasses = null;
	if(option["styleClass"]){
		styleClasses = option["styleClass"];
	}

	var iconDivDto = new wgpDomDto(
		iconDivId
		,"div"
		,attributes
		,styleClasses
		,styles
	);

	$("#" + div_tag_id).append( wgpDomCreator.createDomStringCall(iconDivDto) );

	// 描画関数が定義されている場合
	if(createViewFunction){
		createViewFunction( iconDivId );
	}

	// クリックイベントを設定する。
	if(clickEventFunction){
		$("#" + iconDivId).click( clickEventFunction );
	}

	this.entity = $("#" + iconDivId);
	return this;
};