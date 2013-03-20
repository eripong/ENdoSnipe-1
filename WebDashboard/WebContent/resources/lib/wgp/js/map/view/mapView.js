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
wgp.MapView = wgp.AbstractView.extend({
	initialize : function(argument, treeSettings) {
		this.viewType = wgp.constants.VIEW_TYPE.VIEW;
		this.collection = new MapElementList();
		if (argument["collection"]) {
			this.collection = argument["collection"];
		}

		this.width = argument["width"];
		this.height = argument["height"];
		this.maxId = 0;

		var realTag = $("#" + this.$el.attr("id"));
		if (this.width == null) {
			this.width = realTag.width();
		}
		if (this.height == null) {
			this.height = realTag.height();
		}

		this.viewCollection = {};
		this.registerCollectionEvent();
		this.render();
	},
	render : function() {
		this.paper = new Raphael(document.getElementById(this.$el.attr("id")),
				this.width, this.height);
	},
	onAdd : function(mapElement) {
		var id = mapElement.id;
		if (id === null) {
			id = this.maxId;
			this.maxId++;

			mapElement.set({
				idAttributeName : id
			}, {
				silent : true
			});
		} else {
			if (id > this.maxId) {
				this.maxId = id + 1;
			}
		}

		// if same id exists, process as change event
		if (this.viewCollection[id]) {
			this.viewCollection[id].destory();

		}
		var objectName = "wgp." + mapElement.get("objectName");
		var view = eval("new " + objectName
				+ "({model:mapElement, paper:this.paper})");
		this.viewCollection[id] = view;

	},
	onChange : function(mapElement) {
		this.viewCollection[mapElement.id].update(mapElement);
	},
	onRemove : function(mapElement) {
		var objectId = mapElement.get("objectId");
		this.viewCollection[objectId].remove(mapElement);
		delete this.viewCollection[objectId];
	},
	getTermData : function() {
		// todo
	}
});

_.bindAll(wgp.MapView);