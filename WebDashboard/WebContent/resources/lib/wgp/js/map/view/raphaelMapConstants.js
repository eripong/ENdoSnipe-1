/*******************************************************************************
 * WGP 1.0B - Web Graphical Platform (https://sourceforge.net/projects/wgp/)
 * 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2012 Acroquest Technology Co.,Ltd.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
var raphaelMapConstants = function() {
};

raphaelMapConstants.LEFT_UPPER = 0;
raphaelMapConstants.LEFT_UNDER = 1;
raphaelMapConstants.RIGHT_UPPER = 2;
raphaelMapConstants.RIGHT_UNDER = 3;
raphaelMapConstants.RADIUS_SIZE = 6;

raphaelMapConstants.CLASS_MAP_ELEMENT = "mapElement";

raphaelMapConstants.OBJECT_ID_NAME = "objectid";
raphaelMapConstants.OBJECT_TYPE_FIELD_NAME = "objecttype";

raphaelMapConstants.CONNECTCURVELINE_ELEMENT_NAME = "connectCurveLine";
raphaelMapConstants.CONNECTLINE_ELEMENT_NAME = "connectLine";
raphaelMapConstants.CURVELINE_ELEMENT_NAME = "curveLine";
raphaelMapConstants.ELLIPSE_ELEMENT_NAME = "ellipse";
raphaelMapConstants.ELLIPSESMALL_ELEMENT_NAME = "ellipseSmall";
raphaelMapConstants.GROUPRECTANGLE_ELEMENT_NAME = "groupRectangle";
raphaelMapConstants.IMAGE_ELEMENT_NAME_ELEMENT_NAME = "image";
raphaelMapConstants.LINE_ELEMENT_NAME = "line";
raphaelMapConstants.RECTANGLE_ELEMENT_NAME = "rectangle";
raphaelMapConstants.ROLLINGARROW_ELEMENT_NAME = "rollingArrow";
raphaelMapConstants.TEXTAREA_ELEMENT_NAME = "textArea";
raphaelMapConstants.TRIANGLE_ELEMENT_NAME = "triangle";

raphaelMapConstants.POLYGON_TYPE_NAME = "POLYGON";
raphaelMapConstants.LINE_TYPE_NAME = "LINE";
raphaelMapConstants.CONNECT_LINE_TYPE_NAME = "CONNECT_LINE";
raphaelMapConstants.TEXTAREA_TYPE_NAME = "TEXTAREA";
raphaelMapConstants.IMAGE_TYPE_NAME = "IMAGE";
raphaelMapConstants.CURVE_LINE_TYPE_NAME = "CURVE_LINE";
raphaelMapConstants.CONNECT_CURVE_LINE_TYPE_NAME = "CONNECT_CURVE_LINE";

raphaelMapConstants.ELEMENT_KINE_MAP = {};
raphaelMapConstants.ELEMENT_KINE_MAP[raphaelMapConstants.CONNECTCURVELINE_ELEMENT_NAME] = raphaelMapConstants.CONNECT_CURVE_LINE_TYPE_NAME;
raphaelMapConstants.ELEMENT_KINE_MAP[raphaelMapConstants.CONNECTLINE_ELEMENT_NAME] = raphaelMapConstants.CURVE_LINE_TYPE_NAME;
raphaelMapConstants.ELEMENT_KINE_MAP[raphaelMapConstants.CURVELINE_ELEMENT_NAME] = raphaelMapConstants.LINE_TYPE_NAME;
raphaelMapConstants.ELEMENT_KINE_MAP[raphaelMapConstants.ELLIPSE_ELEMENT_NAME] = raphaelMapConstants.POLYGON_TYPE_NAME;
raphaelMapConstants.ELEMENT_KINE_MAP[raphaelMapConstants.ELLIPSESMALL_ELEMENT_NAME] = raphaelMapConstants.POLYGON_TYPE_NAME;
raphaelMapConstants.ELEMENT_KINE_MAP[raphaelMapConstants.GROUPRECTANGLE_ELEMENT_NAME] = raphaelMapConstants.POLYGON_TYPE_NAME;
raphaelMapConstants.ELEMENT_KINE_MAP[raphaelMapConstants.IMAGE_ELEMENT_NAME_ELEMENT_NAME] = raphaelMapConstants.IMAGE_TYPE_NAME;
raphaelMapConstants.ELEMENT_KINE_MAP[raphaelMapConstants.LINE_ELEMENT_NAME] = raphaelMapConstants.LINE_TYPE_NAME;
raphaelMapConstants.ELEMENT_KINE_MAP[raphaelMapConstants.RECTANGLE_ELEMENT_NAME] = raphaelMapConstants.POLYGON_TYPE_NAME;
raphaelMapConstants.ELEMENT_KINE_MAP[raphaelMapConstants.ROLLINGARROW_ELEMENT_NAME] = raphaelMapConstants.POLYGON_TYPE_NAME;
raphaelMapConstants.ELEMENT_KINE_MAP[raphaelMapConstants.TEXTAREA_ELEMENT_NAME] = raphaelMapConstants.TEXTAREA_TYPE_NAME;
raphaelMapConstants.ELEMENT_KINE_MAP[raphaelMapConstants.TRIANGLE_ELEMENT_NAME] = raphaelMapConstants.POLYGON_TYPE_NAME;

raphaelMapConstants.PASTE_TYPE_CONTEXT = "context";
raphaelMapConstants.PASTE_TYPE_SHORTCUT = "shortcut";

raphaelMapConstants.DEFAULT_SETTING = {};
raphaelMapConstants.DEFAULT_SETTING[raphaelMapConstants.POLYGON_TYPE_NAME] = {
	basicWidth : 100,
	basicHeight : 100
};
raphaelMapConstants.DEFAULT_SETTING[raphaelMapConstants.LINE_TYPE_NAME] = {
	basicWidth : 1,
	basicHeight : 1
};
raphaelMapConstants.DEFAULT_SETTING[raphaelMapConstants.TEXTAREA_TYPE_NAME] = {
	basicWidth : 100,
	basicHeight : 100
};
raphaelMapConstants.DEFAULT_SETTING[raphaelMapConstants.IMAGE_TYPE_NAME] = {
	basicWidth : 100,
	basicHeight : 100
};
raphaelMapConstants.DEFAULT_SETTING[raphaelMapConstants.CURVE_LINE_TYPE_NAME] = {
	basicWidth : 100,
	basicHeight : 100
};
raphaelMapConstants.DEFAULT_SETTING[raphaelMapConstants.CONNECT_CURVE_LINE_TYPE_NAME] = {
	basicWidth : 100,
	basicHeight : 100
};

raphaelMapConstants.MENU_MODE_AUTO = "auto";
raphaelMapConstants.MENU_MODE_MANUAL = "manual";

raphaelMapConstants.ADD_CLASS_TO_MENU = "iconClicked";

// event constants
raphaelMapConstants.EVENT_TYPE_SELECT = "select";
raphaelMapConstants.EVENT_TYPE_RELEASE_SELECT = "releaseSelect";
raphaelMapConstants.EVENT_TYPE_MOVE_START = "moveStart";
raphaelMapConstants.EVENT_TYPE_MOVE_END = "moveEnd";
raphaelMapConstants.EVENT_TYPE_MOVE = "move";
raphaelMapConstants.EVENT_TYPE_RESIZE = "resize";
raphaelMapConstants.EVENT_TYPE_RESIZE_START = "resizeStart";
raphaelMapConstants.EVENT_TYPE_RESIZE_END = "resizeEnd";
raphaelMapConstants.EVENT_TYPE_COPY = "copy";
raphaelMapConstants.EVENT_TYPE_CUT = "cut";
raphaelMapConstants.EVENT_TYPE_DELETE = "delete";
raphaelMapConstants.EVENT_TYPE_PASTE = "paste";

raphaelMapConstants.EVENT_EXECUTE_INTERVAL = 100;

// resize ellipse position name
raphaelMapConstants.SMALL_ELLIPSE_POSITION = "ellipseposition";

/**
 * relation toward HTML DOM id name and wgp map icon type. if you write id on
 * following and HTML automatically create map element.
 */
raphaelMapConstants.MENU_AREA_ID_MAP = {
	"wgp-connectCurveLine" : raphaelMapConstants.CONNECTCURVELINE_ELEMENT_NAME,
	"wgp-connectLine" : raphaelMapConstants.CONNECTLINE_ELEMENT_NAME,
	"wgp-curveLine" : raphaelMapConstants.CURVELINE_ELEMENT_NAME,
	"wgp-ellipse" : raphaelMapConstants.ELLIPSE_ELEMENT_NAME,
	"wgp-groupRectangle" : raphaelMapConstants.GROUPRECTANGLE_ELEMENT_NAME,
	"wgp-image" : raphaelMapConstants.IMAGE_ELEMENT_NAME_ELEMENT_NAME,
	"wgp-line" : raphaelMapConstants.LINE_ELEMENT_NAME,
	"wgp-rectangle" : raphaelMapConstants.RECTANGLE_ELEMENT_NAME,
	"wgp-rollingArrow" : raphaelMapConstants.ROLLINGARROW_ELEMENT_NAME,
	"wgp-textArea" : raphaelMapConstants.TEXTAREA_ELEMENT_NAME,
	"wgp-triangle" : raphaelMapConstants.TRIANGLE_ELEMENT_NAME,
	"wgp-select" : ""
};

// default select menu id.
raphaelMapConstants.DEFAULT_MENU_ID = "wgp-select";
// z index constants.
raphaelMapConstants.ZINDEX_FIELD_NAME = "zindex";
raphaelMapConstants.MAP_LAYER_NUM = 3;
raphaelMapConstants.MAP_LAYER_JOIN_OBJECT = {};
raphaelMapConstants.MAP_LAYER_JOIN_OBJECT[raphaelMapConstants.CONNECTCURVELINE_ELEMENT_NAME] = 0;
raphaelMapConstants.MAP_LAYER_JOIN_OBJECT[raphaelMapConstants.CONNECTLINE_ELEMENT_NAME] = 0;
raphaelMapConstants.MAP_LAYER_JOIN_OBJECT[raphaelMapConstants.ELLIPSE_ELEMENT_NAME] = 2;
raphaelMapConstants.MAP_LAYER_JOIN_OBJECT[raphaelMapConstants.GROUPRECTANGLE_ELEMENT_NAME] = 2;
raphaelMapConstants.MAP_LAYER_JOIN_OBJECT[raphaelMapConstants.IMAGE_ELEMENT_NAME_ELEMENT_NAME] = 2;
raphaelMapConstants.MAP_LAYER_JOIN_OBJECT[raphaelMapConstants.ROLLINGARROW_ELEMENT_NAME] = 2;
raphaelMapConstants.MAP_LAYER_JOIN_OBJECT[raphaelMapConstants.TEXTAREA_ELEMENT_NAME] = 2;
raphaelMapConstants.MAP_LAYER_JOIN_OBJECT[raphaelMapConstants.TRIANGLE_ELEMENT_NAME] = 1;
raphaelMapConstants.MAP_LAYER_JOIN_OBJECT[raphaelMapConstants.RECTANGLE_ELEMENT_NAME] = 0;
raphaelMapConstants.MAP_LAYER_JOIN_OBJECT[raphaelMapConstants.LINE_ELEMENT_NAME] = 1;
