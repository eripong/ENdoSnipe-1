var ResourceGraphModel = Backbone.Model.extend({
	defaults:{
		dataId : null,
		data : []
	},
	idAttribute:"dataId"
});

var ResourceGraphCollection = Backbone.Collection.extend({
	model : ResourceGraphModel
});

