/*******************************************************************************
 * ENdoSnipe 5.0 - (https://github.com/endosnipe)
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
var ENS = {};

ENS.ID = {}
ENS.ID.MEASUREMENT_TIME = "measurementTime";
ENS.ID.MEASUREMENT_VALUE = "measurementValue";
ENS.ID.MEASUREMENT_ITEM_NAME = "measurementItemName";

ENS.DATE_FORMAT_DETAIL = 'yyyy/MM/dd HH:mm:ss.fff';
ENS.DATE_FORMAT_DAY = 'yyyy/MM/dd';
ENS.DATE_FORMAT_HOUR = 'yyyy/MM/dd HH:mm';

ENS.common = {};
ENS.common.dualslider = {};
ENS.common.dualslider.scaleUnitString = 'hours';
ENS.common.dualslider.scaleUnit = 60 * 60 * 1000; // millisecond
ENS.common.dualslider.groupString = 'days';
ENS.common.dualslider.groupUnitNum = 24;
ENS.common.dualslider.groupMaxNum = 7;
ENS.common.dualslider.groupDefaultNum = 3;
ENS.common.dualslider.idFrom = 'dualSliderFromValue';
ENS.common.dualslider.idTo = 'dualSliderToValue';

ENS.singleslider = {};
ENS.singleslider.scaleUnitString = 'hours';
ENS.singleslider.scaleUnit = 60 * 60 * 1000; // millisecond
ENS.singleslider.groupString = 'days';
ENS.singleslider.groupUnitNum = 24;
ENS.singleslider.groupMaxNum = 7;
ENS.singleslider.groupDefaultNum = 3;
ENS.singleslider.idTime = 'singlesliderTimeValue';

ENS.nodeinfo = {};
ENS.nodeinfo.parent = {};
ENS.nodeinfo.parent.css = {};
ENS.nodeinfo.viewList = [];


ENS.nodeinfo.parent.css.informationArea = {
	fontSize : "14px",
	float : "right",
	width : "180px",
	height : "350px",
	border : "1px #dcdcdc solid",
	margin : "190px 20px 0px 0px"
};
ENS.nodeinfo.parent.css.legendArea = {
	height : "40px",
	margin : "5px 5px 5px 5px"
};
ENS.nodeinfo.parent.css.annotationLegendArea = {
	margin : "0px 0px 0px 0px",
	padding : "5px 5px 5px 5px"
};
ENS.nodeinfo.parent.css.dualSliderArea = {
	width : "800px",
	margin : "0px 0px 20px 60px",
};
ENS.nodeinfo.parent.css.graphArea = {
	float : "left",
	width : "650px",
	margin : "30px 0px 0px 10px"
};
ENS.nodeinfo.ONEDAY_MILLISEC = 86400000;

ENS.ResourceGraphAttribute = [ "colors", "labels", "valueRange", "xlabel",
		"ylabel", "strokeWidth", "legend", "labelsDiv", "width", "height" ];
ENS.nodeinfo.GRAPH_HEIGHT_MARGIN = 2;
