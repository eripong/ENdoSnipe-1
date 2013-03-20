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
wgp.AppView = Backbone.View
		.extend({
			initialize : function() {
				this.viewType = wgp.constants.VIEW_TYPE.CONTROL;
				this.collections = {};
				this.syncIdList = {};
				var ins = this;
				wgp.AppView = function() {
					return ins;
				};
			},
			addView : function(view, syncId) {
				var ins = this;
				// if not exist collection, new WgpCollection
				var collection = this.collections[syncId];
				if (!collection) {
					collection = new wgp.WgpCollection();
					ins.collections[syncId] = collection;
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
				var ins = this;
				var startSyncIdList = [];
				_.each(syncIdList, function(id) {
					if (!ins.syncIdList[id]) {
						ins.syncIdList[id] = true;
						// start synchronize
						startSyncIdList.push(id);
					}
				});
				if (startSyncIdList.length === 0) {
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
				var ins = this;
				var stopSyncIdList = [];
				_.each(syncIdList, function(id) {
					if (ins.syncIdList[id]) {
						ins.syncIdList[id] = false;
						// start synchronize
						stopSyncIdList.push(id);
					}
				});
				if (stopSyncIdList.length === 0) {
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
				var ins = this;
				_.each(notificationList, function(notification, dataGroupId) {
					var updateCollection = ins.collections[dataGroupId];
					if (updateCollection) {
						ins._updateCollectionData(updateCollection,
								notification);
						// データ取得処理が完了したことを通知する
						updateCollection.trigger("complete", wgp.constants.syncType.NOTIFY);
					}
				});
			},
			_updateCollectionData : function(updateCollection, dataList) {
				var ins = this;
				_.each(dataList, function(updateData, modelId) {
					// create Model
					updateData.id = modelId;
					var type = updateData.type;

					// Execute Collection
					// Add Event
					if (type == wgp.constants.CHANGE_TYPE.ADD) {
						ins._add(updateCollection, updateData);
					} else if (type == wgp.constants.CHANGE_TYPE.UPDATE) {
						ins._update(updateCollection, updateData);
					} else if (type == wgp.constants.CHANGE_TYPE.DELETE) {
						ins._remove(updateCollection, updateData);
					}
				});
			},
			_add : function(addCollection, addData) {
				if (addCollection.get(addData.id) != null) {
					console.log('Collection already Exists');
				} else {
					var model = new addCollection.model(addData.updateData);
					addCollection.add(model);
				}
			},
			_update : function(updateCollection, updateData) {
				var targetModel = updateCollection.models[updateData.id];
				if (targetModel === null || targetModel === undefined) {
					console.log('Model is not exists');
				} else {
					targetModel.set(updateData.updateData);
					updateCollection.trigger("change", targetModel);
				}
			},
			_remove : function(updateCollection, removeData) {
				updateCollection.remove(removeData.id);
			},
			// Search methods.
			onSearch : function(settings) {

				var dataGroupIdList = settings["dataGroupIdList"];
				if (dataGroupIdList) {
					this.stopSyncData(dataGroupIdList);
				}

				var ajaxHandler = new wgp.AjaxHandler();
				settings[wgp.ConnectionConstants.SUCCESS_CALL_OBJECT_KEY] = this;
				settings[wgp.ConnectionConstants.SUCCESS_CALL_FUNCTION_KEY] = "onComplete";
				ajaxHandler.requestServerAsync(settings);
			},
			onComplete : function(data) {
				var ins = this;
				var idList = [];
				_.each(data, function(dataList, dataGroupId) {
					idList.push(dataGroupId);
					var collection = ins.collections[dataGroupId];
					var silent = ins.addAfterComplete ? false : true;
					collection.reset(dataList, {
						silent : silent
					});
				});
				_.each(idList, function(dataGroupId) {
					var collection = ins.collections[dataGroupId];
					collection.trigger("complete", wgp.constants.syncType.SEARCH);
				});
			}
		});