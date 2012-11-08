halook.ResourceGraphAttribute = [ "colors", "labels", "valueRange", "xlabel",
		"ylabel", "strokeWidth", "legend", "labelsDiv", "width", "height" ];
halook.nodeinfo.GRAPH_HEIGHT_MARGIN = 50;
halook.ResourceGraphElementView = wgp.AbstractView.extend({
	initialize : function(argument, treeSettings) {
		this._initData(argument, treeSettings);

		var appView = new wgp.AppView();
		appView.addView(this, argument.graphId);

		if (!this.noTermData) {
			var startTime = new Date(new Date().getTime() - this.term * 1000);
			var endTime = new Date();
			appView.getTermData([ this.graphId ], startTime, endTime);
		}
		this.dataArray = {};
		this.dataArray = this._makeRandomData();
		this._addCollection(this.dataArray);
		this.render();
	},
	_makeRandomData : function() {
		var dataArray = [];
		var today = new Date();
		var agoTime = new Date();
		agoTime.setTime(today.getTime() - 24 * 60 * 60 * 1000);
		while (today.getTime() > agoTime.getTime()) {
			var time = agoTime.getTime() + 60 * 15 * 1000;
			agoTime.setTime(time);
			var setTime = new Date(time);
			dataArray.push([ setTime, parseInt(Math.random() * 100),
					parseInt(Math.random() * 100) ]);
		}
		return dataArray;
	},
	_addCollection : function(dataArray) {
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

	_initData : function(argument, treeSettings) {
		// initialize data;
		var defauldSettings = {
			term : 1800,
			graphMaxNumber : 50,
			maxValue : 100
		};
		var argument = $.extend(true, defauldSettings, argument);

		this.viewType = wgp.constants.VIEW_TYPE.VIEW;
		this.parentId = argument.parentId;
		this.term = argument.term;
		this.graphId = argument.id;
		this.width = argument.width;
		this.height = argument.height;
		this.title = argument.id;
		this.labelX = "time";
		this.labelY = argument.id;
		this.noTermData = argument.noTermData;
		this.attributes = argument.attributes;
		this.maxId = 0;
		this.graphMaxNumber = argument.graphMaxNumber;
		this.maxValue = argument.maxValue;

		var realTag = $("#" + this.$el.attr("id"));
		if (this.width == null) {
			this.width = realTag.width();
		}
		if (this.height == null) {
			this.height = realTag.height();
		}
		$("#" + this.$el.attr("id")).attr("class", "graphbox");
	},
	render : function() {
		var data = this.getData();
		var optionSettings = {
			title : this.title,
			xlabel : this.labelX,
			ylabel : this.labelY
		};
		optionSettings = $.extend(true, optionSettings, this
				.getAttributes(halook.ResourceGraphAttribute))
		this.entity = new Dygraph(document.getElementById(this.$el.attr("id")),
				data, optionSettings);
		this.entity.resize(this.width, this.height);
	},
	onAdd : function(graphModel) {
		if (this.collection.length > this.graphMaxNumber) {
			this.collection.shift(wgp.constants.BACKBONE_EVENT.SILENT);
		}
		this.data = this.getData();
		var updateOption = {
			'file' : this.data,
			valueRange : [ 0, this.maxValue ]
		};
		if (this.data.length != 0) {
			updateOption['dateWindow'] = [ this.data[1][0],
					this.data[this.data.length - 1][0] ];
		}
		this.entity.updateOptions(updateOption);
	},

	getTermData : function() {
		this.data = this.getData();
		var updateOption = {
			'file' : this.data,
			valueRange : [ 0, this.maxValue ]
		};
		if (this.data.length != 0) {
			if (this.data.length != 1) {
				updateOption['dateWindow'] = [ this.data[1][0],
						this.data[this.data.length - 1][0] ];
			}
		}
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
	getLabels : function() {

	},
	getRegisterId : function() {
		return this.graphId;
	},
	destroy : function() {
		this.stopRegisterCollectionEvent();
		var tmpAppView = new wgp.AppView();
		tmpAppView.stopSyncData([ this.graphId ]);
		this.entity = null;
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