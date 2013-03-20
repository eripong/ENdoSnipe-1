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
 * コンテキストメニューを作成するクラス
 */
var contextMenuCreator = function() {
};
contextMenuCreator();

// デフォルトセッティング
contextMenuCreator.defaultSetting = {
	widthOverflowOffset : 0,
	heightOverflowOffset : 0,
	submenuLeftOffset : 0,
	submenuTopOffset : 0,
	fadeIn : 0,
	delay : 0,
	autoHide : true,
	autoAddSubmenuArrows : true
};

/**
 * コンテキストメニュー用のHTMLを作成する。(外部呼出用)
 * 
 * @param menuId
 *            コンテキストメニューを一意に識別するID
 * @param menuArray
 *            コンテキストメニューの内容を表す配列
 */
contextMenuCreator.initializeContextMenu = function(menuId, menuArray) {

	// 既に同一のコンテキストメニューが作成されている場合は処理を行わない。
	if ($("#" + menuId).length !== 0) {
		return;
	}

	// コンテキストメニューとなるULタグ、LIタグの構成のHTML文字列を生成する。
	var contextMenuString = contextMenuCreator.createContextMenuString(menuId,
			menuArray);
	$("body").append(contextMenuString);
};

/**
 * コンテキストメニュー用のHTMLを作成する。
 * 
 * @param menuId
 *            コンテキストメニューを一意に識別するID
 * @param menuArray
 *            コンテキストメニューの内容を表す配列
 * @param parent
 *            コンテキストメニューを登録する親要素
 */
contextMenuCreator.createContextMenuString = function(menuId, menuArray) {

	var ulDto = new wgp.wgpDomDto(menuId, "ul", null,
			[ wgp.styleClassConstants.CONTEXT_MENU ], {
				display : "none"
			});

	$.each(menuArray, function(index, contextMenu) {

		var liDto = new wgp.wgpDomDto(contextMenu.menu_id, "li", null, null,
				null);

		// サブメニューが存在する場合はさらに追加
		liDto.addChildren([ contextMenu.menu_name ]);
		if (contextMenu.children && contextMenu.children.length > 0) {
			liDto.addChildren([ contextMenuCreator.createContextMenuString(
					null, contextMenu.children) ]);
		}

		ulDto.addChildren([ liDto ]);
	});

	return wgp.wgpDomCreator.createDomStringCall(ulDto);
};

/**
 * 引数を基にコンテキストメニューを生成する。
 * 
 * @param targetId
 *            コンテキストメニューを適用するタグ
 * @param menuid
 *            コンテキストメニューを一意に識別するID
 * @param option
 *            追加設定(又は上書き)するオプション
 */
contextMenuCreator.createContextMenu = function(targetId, menuId, option) {

	// 設定内容を取得する。
	var setting = $.extend(true, {
		widthOverflowOffset : 0,
		heightOverflowOffset : 0,
		submenuLeftOffset : 0,
		submenuTopOffset : 0,
		fadeIn : 0,
		delay : 0,
		autoHide : true,
		autoAddSubmenuArrows : true
	}, option);

	// jeegoocontextの設定を適用する。
	$("#" + targetId).jeegoocontext(menuId, setting);
};

/**
 * 引数を基にコンテキストメニューを生成する。
 * 
 * @param selectorString
 *            コンテキストメニューを適用するセレクタ
 * @param menuid
 *            コンテキストメニューを一意に識別するID
 * @param option
 *            追加設定(又は上書き)するオプション
 */
contextMenuCreator.createContextMenuSelector = function(selectorString, menuId,
		option) {

	// 設定内容を取得する。
	var setting = $.extend(true, {
		widthOverflowOffset : 0,
		heightOverflowOffset : 0,
		submenuLeftOffset : 0,
		submenuTopOffset : 0,
		fadeIn : 0,
		delay : 0,
		autoHide : true,
		autoAddSubmenuArrows : true
	}, option);

	// jeegoocontextの設定を適用する。
	$(selectorString).jeegoocontext(menuId, setting);
};