halook.ResourceGraphAttribute = [ "colors", "labels", "valueRange", "xlabel",
		"ylabel", "strokeWidth", "legend", "labelsDiv", "width", "height" ];
halook.nodeinfo.GRAPH_HEIGHT_MARGIN = 2;
halook.ResourceGraphElementView = wgp.DygraphElementView.extend({
	initialize : function(argument, treeSettings) {
		this.isRealTime = true;
		this._initData(argument, treeSettings);

		var appView = new wgp.AppView();
		appView.addView(this, argument.graphId);
		this.render();
		this.registerCollectionEvent();

		if (!this.noTermData) {
			var startTime = new Date(new Date().getTime() - this.term * 1000);
			var endTime = new Date();
			appView.getTermData([ this.graphId ], startTime, endTime);
		}

		var realTag = $("#" + this.$el.attr("id"));
		if (this.width == null) {
			this.width = realTag.width();
		} else {
			realTag.width(this.width);
		}
		if (this.height == null) {
			this.height = realTag.height();
		} else {
			realTag.height(this.height);
		}

		$("#" + this.$el.attr("id")).attr("class", "graphbox");
		$("#" + this.$el.attr("id")).css({
			margin : "10px",
			float : "left"
		});
	},
	_initData : function(argument, treeSettings) {
		var defauldSettings = {
			term : 60 * 30,
			graphMaxNumber : 50,
			maxValue : 100
		};
		this.viewType = wgp.constants.VIEW_TYPE.VIEW;
		this.collection = new ResourceGraphCollection();
		this.parentId = argument["parentId"];
		this.term = argument.term;
		this.noTermData = argument.noTermData;
		this.graphId = argument["graphId"];
		this.width = argument["width"];
		this.height = argument["height"];
		this.title = argument["title"];
		this.labelX = "time";
		this.labelY = "value";
		this.rootView = argument["rootView"];
		this.graphHeight = this.height - halook.nodeinfo.GRAPH_HEIGHT_MARGIN;
		this.dateWindow = argument["dateWindow"];
		this.maxId = 0;

		 this.graphMaxNumber = 50;//argument.graphMaxNumber;
		 this.maxValue = 100;//argument.maxValue;
	},
	render : function() {
		var data = this.getData();
		var optionSettings = {
			title : this.title,
			xlabel : this.labelX,
			ylabel : this.labelY,
			axisLabelColor : "#FFFFFF",
			labelsDivStyles : {
				background: "none repeat scroll 0 0 #000000"
			}
		};

		this.attributes = undefined;
		var attributes = this.getAttributes(halook.ResourceGraphAttribute);

		optionSettings = $.extend(true, optionSettings, attributes);

		var element = document.getElementById(this.$el.attr("id"));
		this.entity = new Dygraph(element, data, optionSettings);
		this.entity.resize(this.width, this.graphHeight);
		$("#" + this.$el.attr("id")).height(this.height);
		this.getGraphObject().updateOptions({
			dateWindow : this.dateWindow,
			axisLabelFontSize : 10,
			titleHeight : 22
		});
	},
	onAdd : function(graphModel) {
		
		if (this.isRealTime) {
			if (this.collection.length > this.graphMaxNumber) {
				this.collection.shift(wgp.constants.BACKBONE_EVENT.SILENT);
			}
			this.data = this.getData();
			var updateOption = {
				'file' : this.data
			};
			if (this.data.length != 0) {
				updateOption['dateWindow'] = [ this.data[1][0],
						this.data[this.data.length - 1][0] ];
			}
			this.entity.updateOptions(updateOption);
		}
	},
	addCollection : function(dataArray) {
		if (dataArray != null) {
			var instance = this;
			_.each(dataArray, function(data, index) {
				var model = new instance.collection.model({
					dataId : instance.maxId,
					data : data
				});
				instance.collection.add(model,
						wgp.constants.BACKBONE_EVENT.SILENT);
				instance.maxId++;
			});
		}
	},
	getTermData : function() {
		this.data = this.getData();
		var updateOption = {
			'file' : this.data,
		};
		this.entity.updateOptions(updateOption);
		
		var tmpAppView = new wgp.AppView();
		tmpAppView.syncData([ this.graphId ]);
	},
	getData : function() {

		var data = [];
		var instance = this;
		data.push([ new Date(0), null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null ]);
		_.each(this.collection.models, function(model, index) {
			data.push(instance._parseModel(model));
		});
		return data;
	},
	getRegisterId : function() {
		return this.graphId;
	},
	getGraphObject : function() {
		return this.entity;
	},
	updateDisplaySpan : function(from, to) {
		var startDate = new Date().getTime() - from;
		var endDate = new Date().getTime() - to;
		this.getGraphObject().updateOptions({
			dateWindow : [ startDate, endDate ]
		});

	},
	updateGraphData : function(graphId, from, to) {
		if (to == 0) {
			this.isRealTime = true;
		} else {
			this.isRealTime = false;
		}
		
		var startTime = new Date(new Date().getTime() - from);
		var endTime = new Date(new Date().getTime() - to);
		appView.getTermData([ graphId ], startTime, endTime);
	},
	_parseModel : function(model) {
		var timeString = model.get("measurementTime");
		var time = parseInt(timeString);
		var date = new Date(time);
		var valueString = model.get("measurementValue");
		var value = parseFloat(valueString);
		if (this.maxValue < value) {
			this.maxValue = value;
		}
		return [ date, value ];
	}

});