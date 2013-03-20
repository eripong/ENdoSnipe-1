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
wgp.TabView = wgp.AbstractView.extend({
	initialize : function(argument, treeSettings) {
		this.viewType = wgp.constants.VIEW_TYPE.TAB;
		this.collection = new TabModelList();
		this.treeSettings = treeSettings;
		this.viewList = {};

		this.divId = this.$el.attr("id");
		this.divTabId = this.$el.attr("id") + "_tab";
		this.maxId = 0;

		this._initializeRender();
		this.registerCollectionEvent();
		this.createdFlag = false;

		var instance = this;
		var collection = argument["collection"];
		_.each(collection, function(tabElement, index) {
			var tabModel = new instance.collection.model(tabElement);
			instance.collection.add(tabModel);
		});
		if (collection) {
			this.render();
		}
	},
	_initializeRender : function() {
		$("#" + this.divId).append("<ul id='" + this.divTabId + "'></ul>");
	},
	render : function() {
		if (this.createdFlag) {
			$("#" + this.divId).tabs("destroy");
		}
		$("#" + this.divId).tabs();
		this.createdFlag = true;
	},
	onAdd : function(tabModel) {
		var tabId = tabModel.get("tabId");
		var viewClassName = tabModel.get("viewClassName");
		var tabTitle = tabModel.get("tabTitle");
		var childCollection = tabModel.get("collection");

		if ($("#" + this.divTabId + "_" + tabId).length > 0) {
			alert("Already Exists Tab");
		}

		if (tabId !== null) {
			if (tabId > this.maxId) {
				this.maxId = tabId;
			}

		} else {
			tabId = this.maxId;
			tabModel.set("tabId", tabId);
			this.maxId++;
		}

		var newDivTabId = this.divTabId + "_" + tabId;
		$("#" + this.divId).append("<div id=" + newDivTabId + "></div>");

		$("#" + newDivTabId).width("100%");
		$("#" + newDivTabId).height("100%");

		var title = $("<li></li>");
		var href = $("<a href=#" + newDivTabId + ">" + tabTitle + "</a>");
		title.append(href);
		$("#" + this.divTabId).append(title);

		var childAttribute = {
			id : newDivTabId,
			collection : childCollection
		};
		var view = eval("new " + viewClassName
				+ "(childAttribute, this.treeSettings)");
		this.viewList[view.getRegisterId()] = view;
	},
	onChange : function(tabModel) {
		// blank
	},
	onRemove : function(tabModel) {
		var view = this.viewCollection(tabModel.id);
		$("#" + this.divId).tabs("remove", tabModel.id);
	},
	destroy : function() {
		_.each(this.viewList, function(view) {
			view.destroy();
		});
	}
});