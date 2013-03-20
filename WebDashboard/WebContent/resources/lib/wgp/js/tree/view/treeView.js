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
wgp.TreeView = wgp.AbstractView
		.extend({
			defaults : {},
			initialize : function(argument) {
				var instance = this;
				this.viewType = wgp.constants.VIEW_TYPE.VIEW;
				this.treeOption = {
					themes : {
						"url" : (argument.themeUrl == null) ? wgp.common
								.getContextPath()
								+ "/resources/lib/jsTree/themes/default/style.css"
								: argument.themeUrl
					},
					plugins : [ "themes", "json_data", "ui", "crrm" ],
					core : {
						animation : 0,
						load_open : wgp.constants.TREE.INITAL_OPEN
					}
				};
			},
			render : function(renderType, treeModel) {
				var targetTag = $("#" + this.$el.attr("id"));
				var idAttribute = treeModel.idAttribute;
				if (renderType == wgp.constants.RENDER_TYPE.ALL) {
					this.renderAll();
				} else if (renderType == wgp.constants.RENDER_TYPE.ADD) {
					var parentTreeId = treeModel.get("parentTreeId");
					if (parentTreeId !== null && parentTreeId !== undefined) {
						targetTag = this.getTreeNode(parentTreeId, idAttribute);
					}

					$("#" + this.$el.attr("id")).jstree("create_node",
							$(targetTag), "inside",
							this.createTreeData(treeModel));
				} else if (renderType == wgp.constants.RENDER_TYPE.DELETE) {
					if (treeModel.id !== null && treeModel.id !== undefined) {
						targetTag = this.getTreeNode(treeModel.id, idAttribute);
					}
					$("#" + this.$el.attr("id")).jstree("delete_node",
							$(targetTag));

				} else if (renderType == wgp.constants.RENDER_TYPE.UPDATE) {
					if (treeModel.id !== null && treeModel.id !== undefined) {
						targetTag = this.getTreeNode(treeModel.id, idAttribute);
					}
					var data = treeModel.get("data");
					$("#" + this.$el.attr("id")).jstree("rename_node",
							targetTag, data);
				} else {
					console.log("[treeView] renderType setting error!");
				}
			},
			renderAll : function() {
				// View jsTree
				var settings = this.treeOption;
				settings = $.extend(true, settings, {
					json_data : {
						data : this.createJSONData()
					}
				});
				$("#" + this.$el.attr("id")).jstree(settings);
			},
			onAdd : function(treeModel) {
				if ($("#" + this.$el.attr("id")).children().length === 0) {
					this.renderAll();
				} else {
					this.render(wgp.constants.RENDER_TYPE.ADD, treeModel);
				}
			},
			onChange : function(treeModel) {
				this.render(wgp.constants.RENDER_TYPE.UPDATE, treeModel);
			},
			onRemove : function(treeModel) {
				this.render(wgp.constants.RENDER_TYPE.DELETE, treeModel);
			},
			onComplete : function() {
				this.renderAll();
			},
			createJSONData : function() {
				var jsonData = [];
				var instance = this;
				// find root tree
				var rootNodeList = this.collection.where({
					parentTreeId : ""
				});
				_.each(rootNodeList, function(treeModel, index) {
					jsonData.push(instance.createTreeData(treeModel));
				});
				return jsonData;
			},
			createTreeData : function(treeModel) {
				var instance = this;

				var attr = {};
				var treeData = treeModel.get("data");
				var children = this.collection.where({
					parentTreeId : treeModel.get("id")
				});
				var childrenData = [];
				_.each(children, function(child, index) {
					childrenData.push(instance.createTreeData(child));
				});
				attr["Id"] = treeModel.get("id");

				// icon decided
				var icon = null;
				if (children.length === 0) {
					icon = wgp.constants.TREE.LEAF_NODE_ICON;
				} else {
					icon = wgp.constants.TREE.CENTER_NODE_ICON;
				}
				var data = {
					data : {
						title : treeData,
						attr : attr,
						icon : icon
					},
					children : childrenData
				};
				return data;
			},
			getTreeNode : function(treeId, idAttribute) {
				var selector = "[" + idAttribute + "=" + "'" + treeId + "'"
						+ "]";
				return $("#" + this.$el.attr("id")).find(selector);
			}
		});