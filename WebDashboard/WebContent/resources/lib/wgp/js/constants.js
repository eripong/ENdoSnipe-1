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
var wgp = {};

wgp.constants = {};

wgp.constants.BACKBONE_EVENT = {};
wgp.constants.BACKBONE_EVENT.SILENT = {silent: true};

wgp.constants.RENDER_TYPE = {};
wgp.constants.RENDER_TYPE.ALL = "all";
wgp.constants.RENDER_TYPE.ADD = "add";
wgp.constants.RENDER_TYPE.DELETE = "delete";
wgp.constants.RENDER_TYPE.UPDATE = "update";

wgp.constants.VIEW_TYPE = {};
wgp.constants.VIEW_TYPE.CONTROL = "control";
wgp.constants.VIEW_TYPE.TAB = "tab";
wgp.constants.VIEW_TYPE.AREA = "area";
wgp.constants.VIEW_TYPE.VIEW = "view";

wgp.constants.CHANGE_TYPE = {};
wgp.constants.CHANGE_TYPE.ADD = 2;
wgp.constants.CHANGE_TYPE.DELETE = 1;
wgp.constants.CHANGE_TYPE.UPDATE = 0;

wgp.constants.STATE = {};
wgp.constants.STATE.NORMAL = "normal";
wgp.constants.STATE.WARN = "warn";
wgp.constants.STATE.ERROR = "error";

wgp.constants.STATE_COLOR = {};
wgp.constants.STATE_COLOR[wgp.constants.STATE.NORMAL]="#00FF00";
wgp.constants.STATE_COLOR[wgp.constants.STATE.WARN]="#FFFF00";
wgp.constants.STATE_COLOR[wgp.constants.STATE.ERROR]="#FF0000";

wgp.constants.IS_CHANGE = {};
wgp.constants.IS_CHANGE.SIZE = {"pointX":true, "pointY":true, "width":true, "height":true};
wgp.constants.IS_CHANGE.STATE = {"state":true};

// url
wgp.constants.URL = {};
wgp.constants.URL.TERM_DATA_URL = "/termData";
wgp.constants.URL.GET_TERM_DATA = wgp.constants.URL.TERM_DATA_URL + "/get";

// tree configuration
wgp.constants.TREE = {};
wgp.constants.TREE.DATA_ID = "tree";
wgp.constants.TREE.CENTER_NODE_ICON = "center";
wgp.constants.TREE.LEAF_NODE_ICON = "leaf";
wgp.constants.TREE.INITAL_OPEN = true;

wgp.constants.syncType = {};
wgp.constants.syncType.SEARCH = "search";
wgp.constants.syncType.NOTIFY = "notify";
