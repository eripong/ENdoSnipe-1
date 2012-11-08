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
wgp.AppView = Backbone.View
		.extend({
			initialize : function() {
				this.viewType = wgp.constants.VIEW_TYPE.CONTROL;
				this.collections = {};
				this.syncIdList = {};
				var instance = this;
				wgp.AppView = function() {
					return instance;
				}
			},
			addView : function(view, syncId) {
				var instance = this;
				// if not exist collection, new WgpCollection
				var collection = this.collections[syncId];
				if (!collection) {
					collection = new wgp.WgpCollection();
					instance.collections[syncId] = collection;
				}
				view.collection = collection;
				view.registerCollectionEvent();
			},
			removeView : function(view) {
				// remove collection
				view.destroy();
				view.collection = null;
			},
			syncData : function(syncIdList) {
				var instance = this;
				var startSyncIdList = [];
				_.each(syncIdList, function(id) {
					if (!instance.syncIdList[id]) {
						instance.syncIdList[id] = true;
						// start synchronize
						startSyncIdList.push(id);
					}
				});
				if (startSyncIdList.length == 0) {
					return;
				}
				var syncData = {
					eventType : "add",
					groupId : startSyncIdList
				};
				var message = JSON.stringify(syncData);
				var webSocket = new wgp.WebSocketClient();
				webSocket.send(message);
			},
			stopSyncData : function(syncIdList) {
				// stop real time synchronization
				var instance = this;
				var stopSyncIdList = [];
				_.each(syncIdList, function(id) {
					if (instance.syncIdList[id]) {
						instance.syncIdList[id] = false;
						// start synchronize
						stopSyncIdList.push(id);
					}
				});
				if (stopSyncIdList.length == 0) {
					return;
				}
				var syncData = {
					eventType : "remove",
					groupId : stopSyncIdList
				};
				var message = JSON.stringify(syncData);
				var webSocket = new wgp.WebSocketClient();
				webSocket.send(message);
			},
			notifyEvent : function(notificationList) {
				var instance = this;
				_
						.each(
								notificationList,
								function(notification, dataGroupId) {
									var updateCollection = instance.collections[dataGroupId];
									_
											.each(
													notification,
													function(updateData,
															modelId) {
														// create Model
														updateData.id = modelId;
														var type = updateData.type;

														// Execute Collection
														// Add Event
														if (type == wgp.constants.CHANGE_TYPE.ADD) {
															if (updateCollection
																	.get(modelId) != null) {
																console
																		.log('Collection already Exists');
															} else {
																var model = new updateCollection.model(
																		updateData.updateData);
																updateCollection
																		.add(model);
															}
														} else if (type == wgp.constants.CHANGE_TYPE.UPDATE) {
															var targetModel = updateCollection.models[modelId];
															if (targetModel == null
																	|| targetModel == undefined) {
																console
																		.log('Model is not exists');
															} else {
																targetModel
																		.set(updateData.updateData);
																updateCollection
																		.trigger(
																				"change",
																				targetModel);
															}
														} else if (type == wgp.constants.CHANGE_TYPE.DELETE) {
															updateCollection
																	.remove(modelId);
														}
													});
								});
			},
			// Term data methods.
			getTermData : function(syncIdList, startTime, endTime) {
				var instance = this;
				this.stopSyncData(syncIdList);

				var ajaxHandler = new wgp.AjaxHandler();
				var url = common.getContextPath()
						+ wgp.constants.URL.GET_TERM_DATA;
				var dataMap = {
					startTime : startTime.getTime(),
					endTime : endTime.getTime(),
					dataGroupIdList : syncIdList
				};
				var settings = {
					url : url,
					data : {
						data : JSON.stringify(dataMap)
					}
				}
				settings[ConnectionConstants.SUCCESS_CALL_OBJECT_KEY] = this;
				settings[ConnectionConstants.SUCCESS_CALL_FUNCTION_KEY] = "completeGetTermData";
				ajaxHandler.requestServerAsync(settings);
			},
			completeGetTermData : function(data) {
				var instance = this;
				var idList = [];
				_.each(data, function(dataList, dataGroupId) {
					idList.push(dataGroupId);
					var collection = instance.collections[dataGroupId];
					collection.reset(dataList, {
						silent : true
					});
				});
				_.each(idList, function(dataGroupId) {
					var collection = instance.collections[dataGroupId];
					collection.trigger("getTermData");
				});
			}
		});