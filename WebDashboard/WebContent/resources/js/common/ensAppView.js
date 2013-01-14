ENS.AppView = wgp.AppView.extend({
	// Term data methods.
	initialize : function() {
		this.viewType = wgp.constants.VIEW_TYPE.CONTROL;
		this.collections = {};
		this.syncIdList = {};
		var ins = this;
		ENS.AppView = function() {
			return ins;
		}
	},
	getTermData : function(syncIdList, startTime, endTime) {
		var instance = this;
		this.stopSyncData(syncIdList);
		var url = wgp.common.getContextPath()
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
		this.onSearch(settings);
	}
});