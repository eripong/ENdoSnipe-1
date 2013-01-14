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
 * 引数にて指定された内容を基にDOM要素の生成を行うクラス
 * ※主にDIVタグの生成を担当
 */
wgp.wgpDomCreator = function(){
};

wgp.wgpDomCreator.createDomStringCall = function(wgpDomDto){

	var returnArray = [];
	this.createDomString(returnArray, wgpDomDto);
	return returnArray.join("");
};

wgp.wgpDomCreator.createDomString = function(returnArray, wgpDomDto){

	returnArray.push("<");
	returnArray.push(wgpDomDto.domKind + " ");

	if(wgpDomDto.id && wgpDomDto.id.length > 0){
		returnArray.push(" id='" + wgpDomDto.id +"'");
	}

	// 属性の設定
	if(wgpDomDto.attributes ){
		var attributeArray = [];
		$.each(wgpDomDto.attributes, function(index, attribute){
			attributeArray.push(index + "=" + "'" + attribute + "'");
		});
		returnArray.push( attributeArray.join(" ") );
	}

	// スタイルクラスの設定
	if(wgpDomDto.styleClasses ){
		var styleClassArray = [];
		styleClassArray.push("class=\"");
		$.each(wgpDomDto.styleClasses, function(index, styleClass){
			styleClassArray.push(styleClass);

			// 追加するスタイルクラスがある場合
			if(wgp.styleClassConstants.STYLE_ADD_SETTING[styleClass]){
				var addStyleClassArray = wgp.styleClassConstants.STYLE_ADD_SETTING[styleClass];
				styleClassArray = styleClassArray.concat(addStyleClassArray);
			}
		});
		styleClassArray.push("\"");

		returnArray.push( styleClassArray.join(" ") );
	}

	// スタイルの設定
	if(wgpDomDto.styles ){
		var styleArray = [];
		styleArray.push("style=\"");
		$.each(wgpDomDto.styles, function(index, style){
			styleArray.push(index + ":" + style + ";");
		});
		styleArray.push("\"");

		returnArray.push( styleArray.join(" ") );
	}

	returnArray.push(">");

	// 子要素が存在する場合は子要素も配列に入れる
	if(wgpDomDto.children && wgpDomDto.children.length > 0){

		$.each(wgpDomDto.children, function(index, child){

			// 子要素がwgpDOmDtoの場合
			if(typeof(child) == "object"){
				wgp.wgpDomCreator.createDomString(returnArray, child);

			// 上記以外はそのまま配列に加える。
			}else{
				returnArray.push( child.toString() );
			}
		});
	}
	returnArray.push("</" + wgpDomDto.domKind + ">");

	return returnArray.join("");
};

