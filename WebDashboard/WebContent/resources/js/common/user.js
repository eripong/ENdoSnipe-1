var nodeInfoParentView = {
	viewClassName : "halook.NodeInfoParentView",
	viewAttribute : {
		ids : {
			dualSliderArea : "sliderArea",
			graphArea : "graphArea"
		}
	}

};
var nodeInfoField = {
	viewClassName : "wgp.MultiAreaView",
	rootView : appView,
	collection : [ nodeInfoParentView ]
};

wgp.constants.VIEW_SETTINGS = {
	"default" : nodeInfoField
};