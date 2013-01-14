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
ENS.DualSliderView = wgp.AbstractView.extend({
	initialize: function(argument){
		this.viewType = wgp.constants.VIEW_TYPE.VIEW;
		
		this.sliderComponent= null;
		this.scaleMovedEventFunc = null;
		this.scaleUnitString= ENS.common.dualslider.scaleUnitString;
		this.scaleUnit		= ENS.common.dualslider.scaleUnit;
		this.groupString	= ENS.common.dualslider.groupString;
		this.groupUnitNum	= ENS.common.dualslider.groupUnitNum;
		this.groupMaxNum	= ENS.common.dualslider.groupMaxNum;
		this.groupNum		= ENS.common.dualslider.groupDefaultNum;
		this.idFrom			= ENS.common.dualslider.idFrom;
		this.idTo			= ENS.common.dualslider.idTo;
		this.viewId			= '#' + this.$el.attr('id');
		this.fromScale		= this.groupUnitNum * this.groupNum - 1;
		this.toScale		= this.groupUnitNum * this.groupNum;
		
		// get html of slider
		var htmlString = this._getScaleHtml(this.scaleUnitString, 
										this.groupUnitNum * this.groupNum, 
										this.groupString, this.groupUnitNum);
		$(this.viewId).append(htmlString);
		
		// select scale
		this._selectSelector(this.idFrom, this.fromScale);
		this._selectSelector(this.idTo, this.toScale);
		
		// make slider
		this.sliderComponent = 
			$(this.viewId + ' select#' + this.idFrom + ',' +
			  this.viewId + ' select#' + this.idTo).selectToUISlider();
		this._setScaleCss();
		
		// hide pull down menu
		$(this.viewId + ' select#' + this.idTo).hide();
		$(this.viewId + ' select#' + this.idFrom).hide();
		
		// make group selector
		var htmlString = this._getGroupHtml(this.groupMaxNum, this.groupString);
		$(this.viewId).append(htmlString + '<hr class="clearFloat">');
		this._setGroupCss();
		this._selectSelector('groupArea', this.groupNum - 1);
				
		// group selector event
		this._setGroupSelectorMovedEvent();
	},
	render : function(){
		console.log('call render (dual slider)');
	},
	onAdd : function(element){
		console.log('call onAdd (dual slider)');
	},
	onChange : function(element){
		console.log('called changeModel (dual slider)');
	},
	onRemove : function(element){
		console.log('called removeModel (dual slider)');
	},
	_getScaleHtml : function(scaleUnitString, scaleNum, groupString, groupNum){
		var htmlStr = '';
		htmlStr += '<form id="scaleArea">\n';
		htmlStr += '<fieldset>\n';
		htmlStr += '  <select id="' + this.idFrom + '">\n';
		
		var _htmlStr = '';
		for(var scale=scaleNum; scale>0; scale--){
			if(scale % groupNum == 0){
				var _groupString = (scale / groupNum) + ' ' + 
									groupString + ' ago';
				_htmlStr += '    <optgroup label="' + _groupString + '">\n';
			};
			_htmlStr += '    <option value="' + scale + '<br>' + 
							 scaleUnitString + ' ago">' + 
							 scale + ' ' + scaleUnitString + ' ago</option>\n';
		};
		_htmlStr += '    <option value="Now">Now</option>\n';
		
		htmlStr += _htmlStr
		htmlStr += '  </select>\n';
		htmlStr += '  <select id="' + this.idTo + '">\n';
		htmlStr += _htmlStr
		htmlStr += '  </select>\n';
		htmlStr += '</fieldset>\n';
		htmlStr += '</form>\n';
		
		return htmlStr;
	},
	_setScaleCss : function(){
		// adjust slider visual
		$(this.viewId + ' form#scaleArea').css({
			width	: '600px',
			float	: 'left'
		});
		$(this.viewId + ' fieldset').css({
			height	: '50px',
			padding	: '60px 40px 0px 20px',
			border	: '0px #dcdcdc solid'
		});
		
		// adjust label on slider
		$(this.viewId + ' span.ui-slider-label-show').css({
			display		: "block",
			fontSize	: "14px",
			textAlign	: "left",
			width		: "100px",
			marginLeft	: "2px"//,
			//border: "1px black solid"
		});
		
		// adjust label of group on slider
		$(this.viewId + ' dl.ui-slider-scale dt').css({
			top			: '-75px'
		});
		$(this.viewId + ' dl.ui-slider-scale dt span').css({
			color		: 'red',
			fontSize	: '14px'
		});
	},
	_getGroupHtml : function(groupMaxNum, groupString){
		var htmlStr = '';
		htmlStr += '<form id="groupArea">\n';
		htmlStr += '  <select>\n';
		for(var groupNum=1; groupNum<=groupMaxNum; groupNum++){
			htmlStr += '    <option value="' + groupNum + '">' + 
						groupNum + ' ' + groupString + ' ago' + '</option>\n';
		};
		htmlStr += '  </select>\n';
		htmlStr += '</form>\n';
		
		return htmlStr;
	},
	_setGroupCss : function(){
		$(this.viewId + ' form#groupArea').css({
			width	: '100px',
			margin	: '60px 0px 0px 10px',
			float	: 'left'
		});
		$(this.viewId + ' .clearFloat').css({
			diplay	: 'block',
			border	: '0px transparent solid',
			clear	: 'both'
		});
	},
	_selectSelector : function(idName, value){
		$('#' + idName + ' option:eq(' + value + ')').attr(
														"selected","selected");
	},
	_setGroupSelectorMovedEvent : function(){
		var instance = this;
		$(this.viewId + ' #groupArea select').change(function(){
			// delete old scale
			$('form#scaleArea').remove();
			
			// get new html of scale
			var oldGroupNum = instance.groupNum;
			instance.groupNum = $(this).val();
			var htmlString = instance._getScaleHtml(instance.scaleUnitString, 
					instance.groupUnitNum * instance.groupNum, 
					instance.groupString, instance.groupUnitNum);
			$(instance.viewId).prepend(htmlString);
			
			// select scale
			instance.fromScale = instance.groupUnitNum * instance.groupNum - (instance.groupUnitNum * oldGroupNum - instance.fromScale)
			instance.toScale = instance.groupUnitNum * instance.groupNum - (instance.groupUnitNum * oldGroupNum - instance.toScale)
			instance._selectSelector(instance.idFrom, instance.fromScale);
			instance._selectSelector(instance.idTo, instance.toScale);
			
			// make slider
			instance.sliderComponent = 
				$(instance.viewId + ' select#' + instance.idFrom + ',' +
						instance.viewId + ' select#' + instance.idTo).selectToUISlider();
			instance._setScaleCss();
			
			// hide pull down menu
			$(instance.viewId + ' select#' + instance.idTo).hide();
			$(instance.viewId + ' select#' + instance.idFrom).hide();
			
			// set event on scale
			instance.setScaleMovedEvent(instance.scaleMovedEventFunc);
		});
	},
	_getFromToAsArray : function(values){
		// values	: .ui-slider values 
		// 	  		  Ex: [4, 6]
		// return	: [fromMillisecond, toMillisecond]
		//			  the time which means how long ago from now
		
		var fromMillisecond = (this.groupUnitNum * this.groupNum - values[0]) * 
																this.scaleUnit;
		var toMillisecond = (this.groupUnitNum * this.groupNum - values[1]) * 
																this.scaleUnit;
		return [fromMillisecond, toMillisecond];
	},
	setScaleMovedEvent : function(func){
		var instance = this;
		this.scaleMovedEventFunc = func;
		this.sliderComponent.bind("slidechange", function(event, ui) {
			instance.fromScale = ui.values[0];
			instance.toScale = ui.values[1];
			var fromtoMillisecond = instance._getFromToAsArray(ui.values);
			var fromMillisecond = fromtoMillisecond[0];
			var toMillisecond = fromtoMillisecond[1];
			instance.scaleMovedEventFunc(fromMillisecond, toMillisecond);
		});
	}
});

