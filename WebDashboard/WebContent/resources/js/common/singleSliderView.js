/////////////////////////////////////////////////////////
//               Following is constant                 //
/////////////////////////////////////////////////////////

halook.singleslider = {};
halook.singleslider.scaleUnitString= 'hours';
halook.singleslider.scaleUnit		= 60 * 60 * 1000; //millisecond
halook.singleslider.groupString	= 'days';
halook.singleslider.groupUnitNum	= 24;
halook.singleslider.groupMaxNum	= 7;
halook.singleslider.groupDefaultNum= 3;
halook.singleslider.idTime 		= 'singlesliderTimeValue';

halook.SingleSliderView = wgp.AbstractView.extend({
	initialize: function(argument){
		this.viewType = wgp.constants.VIEW_TYPE.VIEW;
		
		this.sliderComponent= null;
		this.scaleMovedEventFunc = null;
		this.scaleUnitString= halook.singleslider.scaleUnitString;
		this.scaleUnit		= halook.singleslider.scaleUnit;
		this.groupString	= halook.singleslider.groupString;
		this.groupUnitNum	= halook.singleslider.groupUnitNum;
		this.groupMaxNum	= halook.singleslider.groupMaxNum;
		this.groupNum		= halook.singleslider.groupDefaultNum;
		this.idTime			= halook.singleslider.idTime;
		this.viewId			= '#' + this.$el.attr('id');
		this.timeScale		= this.groupUnitNum * this.groupNum;
		
		// get html of slider
		var htmlString = this._getScaleHtml(this.scaleUnitString, 
										this.groupUnitNum * this.groupNum, 
										this.groupString, this.groupUnitNum);
		$(this.viewId).append(htmlString);
		
		// select scale
		this._selectSelector(this.idTime, this.timeScale);
		
		// make slider
		this.sliderComponent = 
			$(this.viewId + ' select#' + this.idTime).selectToUISlider();
		this._setScaleCss();
		
		// hide pull down menu
		$(this.viewId + ' select#' + this.idTo).hide();
		$(this.viewId + ' select#' + this.idTime).hide();
		
		// make group selector
		var htmlString = this._getGroupHtml(this.groupMaxNum, this.groupString);
		$(this.viewId).append(htmlString + '<hr class="clearFloat">');
		this._setGroupCss();
		this._selectSelector('groupArea', this.groupNum);
		
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
		htmlStr += '  <select id="' + this.idTime + '">\n';
		
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
			instance.timeScale = instance.groupUnitNum * instance.groupNum - (instance.groupUnitNum * oldGroupNum - instance.timeScale)
			instance._selectSelector(instance.idTime, instance.timeScale);
			
			// make slider
			instance.sliderComponent = 
				$(instance.viewId + ' select#' + instance.idTime).selectToUISlider();
			instance._setScaleCss();
			
			// hide pull down menu
//			$(instance.viewId + ' select#' + instance.idTo).hide();
			$(instance.viewId + ' select#' + instance.idTime).hide();
			
			// set event on scale
			instance.setScaleMovedEvent(instance.scaleMovedEventFunc);
		});
	},
	_getTimeToAsArray : function(values){
		// values	: .ui-slider values 
		// 	  		  Ex: [4, 6]
		// return	: [fromMillisecond, toMillisecond]
		//			  the time which means how long ago from now
		
		var timeMillisecond = (this.groupUnitNum * this.groupNum - values[0]) * 
																this.scaleUnit;
		return [timeMillisecond];
	},
	setScaleMovedEvent : function(func){
		var instance = this;
		this.scaleMovedEventFunc = func;
		this.sliderComponent.bind("slidechange", function(event, ui) {
			instance.timeScale = ui.values[0];
			var timetoMillisecond = instance._getTimeToAsArray(ui.values);
			var timeMillisecond = timetoMillisecond[0];
			instance.scaleMovedEventFunc(timeMillisecond);
		});
	}
});

