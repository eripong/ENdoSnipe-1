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
wgp.MultiAreaView = wgp.AbstractView.extend({
	initialize: function(argument, treeSettings){
		this.viewType = wgp.constants.VIEW_TYPE.AREA;
		this.collection = new wgp.ViewModelList();
		this.treeSettings = treeSettings;
		this.viewList = {};

		this.divId = this.$el.attr("id");
		this.registerCollectionEvent();
		this.maxId = 0;

		var instance = this;
		var collection = argument["collection"];
		_.each(collection, function(viewElement, index){
			var viewModel = new instance.collection.model(viewElement);
			instance.collection.add(viewModel);
		});
	},
	render : function(){
	},
	onAdd : function(viewModel){
		var viewId = viewModel.get("viewId");
		var viewClassName = viewModel.get("viewClassName");
		var viewAttribute = viewModel.get("viewAttribute");

		if(viewId == null){
			viewId = this.maxId;
			this.maxId++;
		}else{
			if(viewId > this.maxId){
				this.maxId = viewId + 1;
			}
		}

		var width = viewModel.get("width");
		var height = viewModel.get("height");

		var newDivAreaId = this.divId + "_" + viewId;
		var newDivArea = $("<div id='"+ newDivAreaId +"'></div>");
		$("#" + this.divId).append(newDivArea);
		newDivArea.width(width);
		newDivArea.height(height);

		$.extend(true, viewAttribute, {id: newDivAreaId });
		var view = eval("new " + viewClassName + "(viewAttribute, this.treeSettings)");
		this.viewList[view.getRegisterId()] = view;
	},
	destroy : function(){
		_.each(this.viewList, function(view){
			view.destroy();
		});
	}
});