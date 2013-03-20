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
wgp.AbstractView = Backbone.View.extend({
	initialize : function(argument, treeSettings) {
		this.viewType = wgp.constants.VIEW_TYPE.VIEW;
		this.collection = new wgp.AbstractCollection();
		this.attributes = {};
		console.log('called initialize');
	},
	render : function() {
		console.log('call render');
	},
	onAdd : function(element) {
		console.log('call onAdd');
	},
	onChange : function(element) {
		console.log('called changeModel');
	},
	onRemove : function(element) {
		console.log('called removeModel');
	},
	onComplete : function(element) {
		console.log('called completeModel');
	},
	registerCollectionEvent : function() {

		// When Collection Add Model
		this.collection.on('add', this.onAdd, this);

		// When Collection Change Model
		this.collection.on('change', this.onChange, this);

		// When Collection Remove Model
		this.collection.on('remove', this.onRemove, this);
		
		// When Collection Complete Model
		this.collection.on('complete', this.onComplete, this);
	},
	stopRegisterCollectionEvent : function() {
		if (this.collection) {
			// When Collection Add Model
			this.collection.off('add', this.onAdd, this);

			// When Collection Change Model
			this.collection.off('change', this.onChange, this);

			// When Collection Remove Model
			this.collection.off('remove', this.onRemove, this);
			
			// When Collection Complete Model
			this.collection.off('complete', this.onComplete, this);
		}
	},
	getAttributes : function(attributesKey) {
		var attributes = this.attributes;
		var attributeValues = {};

		if (attributes === null || attributes === undefined) {
			return attributeValues;
		}

		_.each(attributesKey, function(attribute, index) {
			var value = attributes[attribute];
			if (value !== null && value !== undefined) {
				attributeValues[attribute] = value;
			}
		});
		return attributeValues;
	},
	getRegisterViews : function() {
		return [ this ];
	},
	getRegisterId : function() {
		return this.$el.attr("id");
	},
	destroy : function() {
		this.stopRegisterCollectionEvent();
	}
});