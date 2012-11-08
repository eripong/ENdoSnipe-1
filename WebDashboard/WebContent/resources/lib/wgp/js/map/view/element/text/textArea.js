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
function textArea(elementProperty, paper) {

    // 設定が取得できない場合は処理を終了する。
    if (!elementProperty) {
        return this;
    }

    // 位置情報リスト
    var positionArray = new Array();

    // テキストエリアに値が設定されていない場合は初期値を設定する。
    if (!elementProperty.text) {
    	elementProperty.text = "";
    }

    // intに直す。
    elementProperty.pointX = parseFloat(elementProperty.pointX);
    elementProperty.pointY = parseFloat(elementProperty.pointY);
    elementProperty.width = parseFloat(elementProperty.width);
    elementProperty.height = parseFloat(elementProperty.height);
    elementProperty.fontSize = parseInt(elementProperty.fontSize);

    // テキストオブジェクトを挿入する。
    var textPosition = {
        x : elementProperty.pointX,
        y : elementProperty.pointY,
        initValue : elementProperty.text
    };
    positionArray.push(textPosition);
    var rect = new rectangle(elementProperty, paper);
    this.object = rect.object;
    this.parentObject_ = this;
    this.createMapElement(positionArray, paper);

    this.textObject.attr('font-size', elementProperty.fontSize);
    rect.object.attr("fill", "#FFFFFF");
    rect.object.attr("fill-opacity", "0");

	this.setAttributes(elementProperty);

    this.object.node.setAttribute('objectId', elementProperty.objectId);
    this.object.node.setAttribute('class', raphaelMapConstants.CLASS_MAP_ELEMENT);

    this.objectId = elementProperty.objectId;
    this.objectType_ = raphaelMapConstants.TEXTAREA_TYPE_NAME;

    this.x = elementProperty.pointX;
    this.y = elementProperty.pointY;
    this.width = elementProperty.width;
    this.height = elementProperty.height;
    this.setTextValue(elementProperty.text);

    this.adjustPosition(elementProperty.fontSize);
    return this;
}
textArea.prototype = new mapElement();

textArea.prototype.createPositionArray = function(elementProperty){

    // ポジションのリスト
    var positionArray = new Array();
    // 左上ポジション
    var firstPosition = new Position(elementProperty.pointX, elementProperty.pointY);
    positionArray.push(firstPosition);
    // 右上ポジション
    var secondPosition = new Position(elementProperty.width, 0);
    positionArray.push(secondPosition);
    // 左下ポジション
    var thirdPosition = new Position(0, elementProperty.height);
    positionArray.push(thirdPosition);
    // 右下ポジション
    var forthPosition = new Position(-1 * elementProperty.width, 0);
    positionArray.push(forthPosition);

	return positionArray;
};


/**
 * オブジェクトの生成。
 * 
 * @private
 * @param {Map} positionArray
 * @param {raphael} paper
 *
 */
textArea.prototype.createMapElement = function(positionArray, paper) {
    if (positionArray) {
        var position = positionArray[0];
        this.textObject = paper.text(position.x, position.y, position.initValue);
        this.parentObject_ = this;
        return this;
    }
};

textArea.prototype.setAttributes = function(elementProperty){
	var instance = this;
	var textAttributes = elementProperty.textProperty;
	if(textAttributes){
		$.each(textAttributes, function(index, attr){
			instance.textObject.attr(index, attr);
		});
	}

	var rectAttributes = elementProperty.rectProperty;
	if(rectAttributes){
		$.each(rectAttributes, function(index, attr){
			instance.object.attr(index, attr);
		});
	}
	this.textProperty =  elementProperty.textProperty;
	this.rectProperty = elementProperty.rectProperty;
};

textArea.prototype.getAttributes = function(elementProperty){
	var textAttributes = this.textObject.attr();
	var rectAttributes = this.object.attr();
	return {
		textAttribute : textAttributes
		,rectAttribute : rectAttributes
	};
};

textArea.prototype.moveElement = function(moveX, moveY){

	// 対象オブジェクトにフレームが存在する場合は、フレームを合成させて移動を行う
    if (this.frame) {
        $.each(this.frame, function(index, frameElement) {
            frameElement.object.translate(moveX, moveY);
            frameElement.x = frameElement.x + moveX;
            frameElement.y = frameElement.y + moveY;
        });
    }

    // テキストと四角形を移動
    var moveElementArray  = [this.object, this.textObject];
    $.each(moveElementArray, function(index, moveElement){
        this.translate(moveX, moveY);
    });
    
    this.x = this.x + moveX;
    this.y = this.y + moveY;

    if(this.cx){
    	this.cx = this.cx + moveX;
    }

	if(this.cy){
		this.cy = this.cy + moveY;
	}

    // 移動対象のオブジェクトが他オブジェクトとconnectしている場合、線を再描画する
    this.refactorConnectLine();
};

textArea.prototype.resize = function(x, y, width, height) {

	// リサイズ前の中心点
	var circlePointBefore = this.getCirclePoint(this.object);

	var elementProperty = {
		pointX : x
		,pointY : y
		,width : width
		,height : height
	};
	var positionArray = this.createPositionArray(elementProperty);
	var path = this.createPathString(positionArray);
	this.object.attr("path", path);
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;

	// リサイズ後の中心点
	var circlePointAfter = this.getCirclePoint(this.object);

	this.textObject.translate(circlePointAfter[0] - circlePointBefore[0],
			circlePointAfter[1] - circlePointBefore[1]);
	
	this.refactorConnectLine();
};

textArea.prototype.setEventFunction = function(mapAreaViewItem){
	this.setDraggableFunction(mapAreaViewItem);
	this.setResizableFunction(mapAreaViewItem);
	this.setDblClickFunction(mapAreaViewItem);
};

textArea.prototype.setDblClickFunction = function(mapAreaViewItem) {
    var instance = this;
    this.object.dblclick(function(event) {
        instance.setOptionFunction(event, instance.textObject, false, mapAreaViewItem);
    });

    this.textObject.dblclick(function(event) {
        instance.setOptionFunction(event, instance.textObject, false, mapAreaViewItem);
    });
};

textArea.prototype.setOptionFunction = function(event, object, isInit, mapAreaViewItem) {
    // キーイベントを取り除く。
    var instance = this;
//    this.keyEventHandler_.detachKeyDownEvent();

    var text = this.text;
//    var textDivArea = $('<div id = "textDivArea" title="editText" ><pre></pre></div>');
    var textArea = $('<textarea id="textArea">' + text + '</textarea>');

    // 選択状態を解除
    mapAreaViewItem.managerInstance.releaseSelectAll();
    
    // overflowとpositionで回避
    var cssValue = {
        'width' : instance.width,
        'height' : instance.height,
        'top' : instance.y,
        'left' : instance.x,
        'overflow' : 'hidden',
        'position' : 'absolute',
        'z-index' : $("#" + mapAreaViewItem.divId).zIndex + 1
    };

    $("#" + mapAreaViewItem.divId).append(textArea);
    $(textArea).css(cssValue);
    $(textArea).focus();

    //    this.keyEventHandler_.SYMBOL[this.keyEventHandler_.KEY_BACKSPACE] = object;
    // テキストに改行が入ると、移動してしまうため、移動前の位置を保持しておく。
    this.beforeRefacterPositionY = this.textObject.attr('y');

    // focusout時にbackspaceをoffにする。
    var tmpIsInit = isInit;
    $(textArea).focusout(function() {
//        instance.keyEventHandler_.SYMBOL[instance.keyEventHandler_.KEY_BACKSPACE] = null;
        if (text == "") {
            text = this.value;
        }

        if (tmpIsInit == false) {

//            // redoメモリクリア
//            instance.historyManager_.removeAllRedo();
//
//            // undo処理を登録
//            var beforEvent_ = instance.objectManager_.undoText(instance, text);
//            instance.historyManager_.setUndo(beforEvent_);
//            instance.objectManager_.dirtyFlag = true;
        }
        
        // テキストの空白を「&nbsp;」に変換する。
         var result = $(textArea).val().replace(/ /g, "&nbsp;");
         $(textArea).remove();

         // テキストボックス空文字入力時、非表示
        if ("" == result || null == result) {
            instance.object.hide();
            instance.textObject.hide();
//            // undo処理を登録
//            var beforEvent_ = instance.objectManager_.undoDeleteObject(new Array(instance));
//            instance.historyManager_.setUndo(beforEvent_);
//            instance.objectManager_.dirtyFlag = true;
        } else {
            instance.setTextValue(result);
            var fontSizeString = instance.textObject.attr('font-size');
            var fontSize = parseInt(fontSizeString);
//            instance.adjustPosition(fontSize);
        }

        // キーイベントを戻す
//        instance.keyEventHandler_.attachKeyDownFunction(constants.DISPLAY_ID_EDIT);
    });
};

//textArea.prototype.getProperty = function() {
//
//    var textRaphaelObject = this.textObject;
//    var childRectRaphaelObject = this.object;
//
//    // 必須項目を取得して設定する。
//    var objectId = this.objectId;
//    var objectType = constants.TEXT_TYPE_NAME;
//    var box = childRectRaphaelObject.getBBox();
//    var pointX = box.x;
//    var pointY = box.y;
//    var width = box.width;
//    var height = box.height;
//    var zIndex = this.getZIndex();
//    var groupId = this.getGroupNo();
//
//    var settings = {
//        objectId : objectId,
//        objectType : objectType,
//        pointX : pointX,
//        pointY : pointY,
//        width : width,
//        height : height,
//        zIndex : zIndex,
//        groupId : groupId
//    };
//
//    // 以下の値は存在すれば設定する。
//    var text = this.getTextValue();
//    var fontFamily = textRaphaelObject.attr('font-family');
//    var fontSize = textRaphaelObject.attr('font-size');
//    var bold = textRaphaelObject.attr('font-weight');
//    var fontColor = textRaphaelObject.attr('fill');
//    var italic = textRaphaelObject.attr('font-style');
//    var alignment = textRaphaelObject.attr('text-anchor');
//
//    if (text) {
//        settings['text'] = text;
//    }
//
//    if (fontFamily) {
//        settings['fontFamily'] = fontFamily;
//    }
//
//    if (fontSize) {
//        settings['fontSize'] = fontSize;
//    }
//
//    if (bold) {
//        settings['bold'] = true;
//    } else {
//        settings['bold'] = false;
//    }
//    if (fontColor) {
//        settings['fontColor'] = fontColor;
//    }
//
//    if (italic) {
//        settings['italic'] = true;
//    } else {
//        settings['italic'] = false;
//    }
//
//    if (alignment) {
//        if (alignment == 'end') {
//            settings['alignment'] = 'rightAlign';
//        } else if (alignment == 'start') {
//            settings['alignment'] = 'leftAlign';
//        } else if (alignment == 'middle') {
//            settings['alignment'] = 'centerAlign';
//        }
//    }
//
//    // テキストエリアを囲む四角形オブジェクトを取得する。
//    var lineDashArray = childRectRaphaelObject.attr(constants.STROKE_DASHARRAY);
//    var lineOpacity = childRectRaphaelObject.attr("stroke-opacity");
//    var lineType;
//    // 線が透明の設定ならばnoneを設定する。
//    if (lineOpacity == 0) {
//        lineType = constants.LINE_TYPE_NONE_LINE;
//    } else if (lineDashArray == "") {
//        // lineTypeは名前を変換する。
//        lineType = constants.LINE_TYPE_SOLID_LINE;
//    } else {
//        lineType = constants.LINE_TYPE_DOTTED_LINE;
//    }
//    if (lineType) {
//        settings["lineType"] = lineType;
//    }
//
//    var strokeWidth = childRectRaphaelObject.attr(constants.STROKE_WIDTH);
//    if (strokeWidth) {
//        settings["strokeWidth"] = strokeWidth;
//    }
//
//    var strokeColor = childRectRaphaelObject.attr(constants.STROKE);
//    if (strokeColor) {
//        settings["strokeColor"] = strokeColor;
//    }
//    if (this.getGroupNo()) {
//        settings["groupId"] = this.getGroupNo();
//    }
//
//    return settings;
//};
//
//textArea.prototype.setProperty = function(propertyInfo) {
//    // パラメータチェック log吐き出し
//    if (!propertyInfo) {
//        if (common.log.isDebugEnabled) {
//            var logMessage = "<setProperty>：propertiesInfo=" + propertiesInfo;
//            common.log.debug(logMessage);
//        }
//    }
//
//    var positionArray = new Array();
//    var pointX = propertyInfo.pointX;
//    var pointY = propertyInfo.pointY;
//    var width = propertyInfo.width;
//    var height = propertyInfo.height;
//
//    var fontFamily = propertyInfo.fontFamily;
//    var fontSize = propertyInfo.fontSize;
//    var bold = propertyInfo.bold;
//    var fontColor = propertyInfo.fontColor;
//    var italic = propertyInfo.italic;
//    var alignment = propertyInfo.alignment;
//    var zIndex = propertyInfo.zIndex;
//    var groupId = propertyInfo.groupId;
//    var strokeWidth = propertyInfo.strokeWidth;
//    var strokeColor = propertyInfo.strokeColor;
//    var lineType = propertyInfo.lineType;
//
//    var rectSettings = {
//        pointX : pointX,
//        pointY : pointY,
//        width : width,
//        height : height
//    };
//    // 大きさを更新するために、path定義を作成する。
//    // ポジションのリスト
//    var positionArray = new Array();
//    // 左上ポジション
//    var firstPosition = new Position(pointX, pointY);
//    positionArray.push(firstPosition);
//    // 右上ポジション
//    var secondPosition = new Position(width, 0);
//    positionArray.push(secondPosition);
//    // 左下ポジション
//    var thirdPosition = new Position(0, height);
//    positionArray.push(thirdPosition);
//    // 右下ポジション
//    var forthPosition = new Position(-1 * width, 0);
//    positionArray.push(forthPosition);
//    var path = this.createPathString(positionArray);
//    var pathObject = this.object;
//    pathObject.attr('path', path);
//
//    if (strokeWidth) {
//        pathObject.attr(constants.STROKE_WIDTH, strokeWidth);
//    }
//    if (strokeColor) {
//        pathObject.attr(constants.STROKE, strokeColor);
//    }
//
//    // 線の種別に関しては変換を行う。
//    if (lineType == constants.LINE_TYPE_SOLID_LINE) {
//        pathObject.attr('stroke-dasharray', "");
//        pathObject.attr('stroke-opacity', 1);
//    } else if (lineType == constants.LINE_TYPE_DOTTED_LINE) {
//        pathObject.attr('stroke-dasharray', "-");
//        pathObject.attr('stroke-opacity', 1);
//    } else if (lineType == constants.LINE_TYPE_NONE_LINE) {
//        pathObject.attr('stroke-opacity', 0);
//    }
//
//    var textSettings = {
//        'font-family' : fontFamily,
//        'font-size' : fontSize,
//        'fill' : fontColor
//    };
//
//    if (bold) {
//        textSettings['font-weight'] = 'bold';
//    } else {
//        textSettings['font-weight'] = '';
//    }
//    if (italic) {
//        textSettings['font-style'] = 'italic';
//    } else {
//        textSettings['font-style'] = '';
//    }
//
//    // 揃えについてはraphael用に変換する
//    if (alignment == 'leftAlign') {
//        textSettings['text-anchor'] = 'start';
//    } else if (alignment == 'rightAlign') {
//        textSettings['text-anchor'] = 'end';
//    } else if (alignment == 'centerAlign') {
//        textSettings['text-anchor'] = 'middle';
//    }
//    // 各種プロパティをセットする。
//    var textObject = this.textObject;
//    common.setRaphaelProperty(textObject, textSettings);
//
//    if (zIndex) {
//        this.setZIndex(zIndex);
//    }
//    if (groupId) {
//        this.setGroupNo(groupId);
//    }
//
//    this.adjustPosition(fontSize);
//    this.refactorConnectLine();
//};
//
/**
 * テキストの位置を調整する処理。
 * 
 * @private
 */
textArea.prototype.adjustPosition = function(fontSize) {
    var alignment = this.textObject.attr("text-anchor");
    // 揃えによって実装を変える。
    if (alignment == 'start') {
        this.adjustPositionLeft(fontSize);
    } else if (alignment == 'end') {
        this.adjustPositionRight(fontSize);
    } else if (alignment == 'middle') {
        this.adjustPositionCenter(fontSize);
    } else {
        this.adjustPositionLeft(fontSize);
    }
};

/**
 * テキストのみ位置調整が必要なため、修正する。
 * 
 * @private
 *
 */
textArea.prototype.adjustPositionLeft = function(fontSize) {
	var pointX = this.x;
	var pointY = this.y + this.height / 2 - this.textObject.getBBox().height / 2;

    this.textObject.attr("y", pointY);
    this.textObject.attr("x", pointX);
};

/**
 * テキストのみ位置調整が必要なため、修正する。
 * 
 * @private
 *
 */
textArea.prototype.adjustPositionCenter = function(fontSize) {
	var pointX = this.x + this.width / 2;
	var pointY = this.y + this.height / 2 - this.textObject.getBBox().height / 2;
	
    this.textObject.attr("y", pointY);
    this.textObject.attr("x", pointX);
};

/**
 * テキストのみ位置調整が必要なため、修正する。
 * 
 * @private
 *
 */
textArea.prototype.adjustPositionRight = function(fontSize) {
	var text = this.text;
	var textArray = text.split(common.getLineFeedCode());
	var pointX = this.x + this.width;
	var pointY = this.y + this.height / 2 - (textArray.length * fontSize) / 2;

    this.textObject.attr("y", pointY);
    this.textObject.attr("x", pointX);
};


//
///**
// * <p>
// * [概 要] テキストオブジェクトの移動を行う。
// * </p>
// * <p>
// * [詳 細] オブジェクトの移動及びコネクタ線のリファクタを行う。
// * </p>
// * <p>
// * [備 考] なし
// * </p>
// * 
// * @public　
// * @param {Number} moveX X軸方向の移動量
// * @param {Number} moveY Y軸方向の移動量
// *
// */
//textArea.prototype.moveElement = function(moveX, moveY) {
//    // 対象オブジェクトにフレームが存在する場合は、フレームを合成させて移動を行う
//    if (this.frame) {
//        $.each(this.frame, function(index, frameObject) {
//            frameObject.object.translate(moveX, moveY);
//        });
//    }
//
//    this.object.translate(moveX, moveY);
//
//    // 移動対象のオブジェクトが他オブジェクトとconnectしている場合、線を再描画する
//    this.refactorConnectLine();
//
//    // テキストを調整する。
//    var fontSize = this.textObject.attr('font-size');
//    this.adjustPosition(fontSize);
//};
//
//textArea.prototype.resize = function(scaleX, scaleY, fulcrumX, fulcrumY, clickEllipsePosition) {
//    var instance = this;
//    // パスを使用してリサイズを行う。
//    var path = this.object.attr("path");
//    // pathをコピーする。
//    var tmpPath = new Array();
//    $.each(path, function(idx, point) {
//        /** zに関しては計算を飛ばす。 */
//        if (point.length == 1) {
//            return true;
//        }
//        var tmpPoint = new Array();
//        $.each(point, function(pointIndex, value) {
//            tmpPoint.push(value);
//        });
//        tmpPoint.push(idx);
//        tmpPath.push(tmpPoint);
//    });
//
//    // resizeを行うためのpath定義を組み込む。
//    var resultPath = new Array();
//    $.each(tmpPath, function(idx, point) {
//        var deltaX = point[1] - fulcrumX;
//        var deltaY = point[2] - fulcrumY;
//        var afterX = fulcrumX + deltaX * scaleX;
//        var afterY = fulcrumY + deltaY * scaleY;
//        resultPath[point[3]] = [ point[0], afterX, afterY ];
//    });
//
//    // 作成した点に同じ座標が含まれている場合は更新しない。
//    var isScale = true;
//    if (object.isSamePoint(resultPath[0], resultPath[1])) {
//        isScale = false;
//    } else if (object.isSamePoint(resultPath[0], resultPath[2])) {
//        isScale = false;
//    } else if (object.isSamePoint(resultPath[1], resultPath[2])) {
//        isScale = false;
//    }
//    // falseなら変更しない。
//    if (!isScale) {
//        return;
//    }
//
//    // 多角形にするために、線を閉じる設定をいれる。
//    resultPath.push( [ "z" ]);
//    this.object.attr("path", resultPath);
//    var fontSize = this.textObject.attr(constants.FONT_SIZE_KEY);
//    this.adjustPosition(fontSize, clickEllipsePosition);
//    this.refactorConnectLine();
//};
//
///**
// * <p>
// * [概 要] リサイズイベント処理を設定する。
// * </p>
// * <p>
// * [詳 細] リサイズイベント処理を設定する。
// * </p>
// * <p>
// * [備 考] なし
// * </p>
// * 
// * @public
// *
// */
//textArea.prototype.setResizableFunction = function() {
//    var objectManagerInstance = new objectManager();
//
//    // クリックイベントを実装する。
//    // ※テキスト及び枠線の両方に設定する。
//    this.object.mousedown(function(event) {
//        /** objectManagerに選択されたオブジェクトを渡す。* */
//        objectManagerInstance.selectObjectRange(event);
//    });
//
//    this.textObject.mousedown(function(event) {
//        /** objectManagerに選択されたオブジェクトを渡す。* */
//        objectManagerInstance.selectObjectRange(event);
//    });
//}
//
///**
// * <p>
// * [概 要] dragイベントに処理を設定する。
// * </p>
// * <p>
// * [詳 細] dragイベントに対して独自の処理を設定する。
// * </p>
// * <p>
// * [備 考] なし
// * </p>
// * 
// * @public
// *
// */
//textArea.prototype.setDraggableFunction = function() {
//    // インスタンス
//    var instance = this;
//
//    var objectManagerInstance = new objectManager();
//    this.object.drag(function(dx, dy) {
//        objectManagerInstance.moveObject(dx, dy, this);
//    }, function(x, y, event) {
//        objectManagerInstance.startMoveObject(x, y, this, event);
//    }, function(event) {
//        objectManagerInstance.endMoveObject(event, this);
//    });
//
//    this.textObject.drag(function(dx, dy) {
//        objectManagerInstance.moveObject(dx, dy, this);
//    }, function(x, y, event) {
//        objectManagerInstance.startMoveObject(x, y, this, event);
//    }, function(event) {
//        objectManagerInstance.endMoveObject(event, this);
//    });
//};
//
//
///**
// * <p>
// * [概 要] フィールド一覧を出力する。
// * </p>
// * <p>
// * [詳 細] クラス内のフィールド一覧をカンマ区切りで出力する。
// * </p>
// * <p>
// * [備 考] なし
// * </p>
// * 
// *
// * @public
// * @returns {String} フィールド一覧をカンマ区切りにで出力した値
// */
//textArea.prototype.toString = function() {
//    var toStringObject = this.textObject;
//    var property = "textArea [";
//    if (toStringObject.attrs.x) {
//        property = property + "x=" + toStringObject.attrs.x + ", ";
//    }
//    if (toStringObject.attrs.y) {
//        property = property + "y=" + toStringObject.attrs.y + ", ";
//    }
//    if (toStringObject.attrs.height) {
//        property = property + "height=" + toStringObject.attrs.height + ", ";
//    }
//    if (toStringObject.attrs.width) {
//        property = property + "width=" + toStringObject.attrs.width + ", ";
//    }
//    if (toStringObject.attrs.scale) {
//        property = property + "scale=" + toStringObject.attrs.scale + ", ";
//    }
//    if (toStringObject.attrs.text) {
//        property = property + "text=" + toStringObject.attrs.text;
//    }
//    property = property + "]";
//    return property;
//};
//
///**
// * 初期化時にスケールサイズをセットする。
// * 
// * @private
// *
// */
//textArea.prototype.setScaleSize = function() {
//    var objectManagerInstance = new objectManager();
//    var scaleSize = objectManagerInstance.getMapScaleSize();
//    // 自身のオブジェクトにスケールをセットする。
//    this.object.scale(scaleSize, scaleSize);
//    var font = this.textObject.attr(constants.FONT_SIZE_KEY);
//    var afterFont = font * scaleSize;
//    this.object.attr(constants.FONT_SIZE_KEY, afterFont);
//};
//
///**
// * <p>
// * [概 要] 引数に指定したオブジェクトの手前に自身のオブジェクトを移動する。
// * </p>
// * <p>
// * [詳 細] 引数に指定したオブジェクトの手前に自身のオブジェクトを移動する。
// * </p>
// * <p>
// * [備 考] なし
// * </p>
// * 
// * @param {object} target jQueryオブジェクト
// *
// * @public
// */
//textArea.prototype.insertAfter = function(target) {
//    $(target).after(this.textObject.node);
//    $(target).after(this.object.node);
//};
//
///**
// * <p>
// * [概 要] 引数に指定したオブジェクトの奥に自身のオブジェクトを移動する。
// * </p>
// * <p>
// * [詳 細] 引数に指定したオブジェクトの奥に自身のオブジェクトを移動する。
// * </p>
// * <p>
// * [備 考] なし
// * </p>
// * 
// * @param {object} target jQueryオブジェクト
// *
// * @public
// */
//textArea.prototype.insertBefore = function(target) {
//    $(target).before(this.object.node);
//    $(target).before(this.textObject.node);
//};
//
///**
// * <p>
// * [概 要] 自身を構成するタグのうち、画面上手前にあるものを返す。
// * </p>
// * <p>
// * [詳 細] 自身を構成するタグのうち、画面上手前にあるものを返す。
// * </p>
// * <p>
// * [備 考] なし
// * </p>
// * 
// * @public
// * @return {Object} 画面上手前にあるJQueryオブジェクト
// *
// */
//textArea.prototype.getFrontTag = function() {
//    return this.textObject.node;
//};
//
///**
// * <p>
// * [概 要] 自身を構成するタグのうち、画面上奥にあるものを返す。
// * </p>
// * <p>
// * [詳 細] 自身を構成するタグのうち、画面上奥にあるものを返す。
// * </p>
// * <p>
// * [備 考] なし
// * </p>
// * 
// * @public
// * @return {Object} 画面上奥にあるJQueryオブジェクト
// *
// */
//textArea.prototype.getBackEndTag = function() {
//    return this.object.node;
//};
//
/**
 * 入力したテキストを取得する。
 * @returns {String} 入力したテキスト
 *
 * @private
 */
textArea.prototype.getTextValue = function() {
    return this.text;
};

/**
 * テキストオブジェクトにテキストを設定する。
 * @param {String} text テキストオブジェクトに設定する文字列
 *
 * @private
 */
textArea.prototype.setTextValue = function(text) {
    this.text = text;
    return ;
    var lineFeedCode = common.getLineFeedCode();
    
    var arrayText = String.split(text, lineFeedCode);
    var replaceText = new Array();
    var tempText;
    $.each(arrayText, function(index, textValue) {
        if (textValue == "" || textValue == "&nbsp;") {
            if (tempText != "&nbsp;") {
                replaceText.push("&nbsp;" + lineFeedCode);
            } else {
                replaceText.push(lineFeedCode);
            }
            tempText = "";
            return true;
        }
        var arraySpace = textValue.replace(/ /g, "");
        if (arraySpace == "") {
            tempText = "";
        } else {
            tempText = textValue;
        }
        replaceText.push(textValue + lineFeedCode);
    });

    var resultText = replaceText.join("");
    resultText = resultText.replace(/ /g, "&nbsp;");

    this.textObject.attr("text", resultText);
};