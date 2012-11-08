var graphViewElement = {
	viewClassName : "wgp.DygraphElementView",
	viewAttribute : {
		term : 1800,
		noTermData : false
	}
};

var mapTabElement = {
	viewClassName : "wgp.MapView",
	tabTitle : "Map",
};

var graphAreaTabElement = {
	viewClassName : "wgp.MultiAreaView",
	tabTitle : "Graph",
	collection : [ graphViewElement ]
};

var tabViewElement = {
	viewClassName : "wgp.TabView",
	collection : [ mapTabElement, graphAreaTabElement ]
};

var nodeInfoParentView = {
	viewClassName : "halook.NodeInfoParentView",
	viewAttribute : {
		ids : {
			dualSliderArea : "sliderArea",
			graphArea : "graphArea"
		}
	}

};
var hbaseGrowthGraphView = {
	viewClassName : "HbaseView"
};

var sliderView = {
	viewClassName : "SliderView"
};

var nodeInfoField = {
	viewClassName : "wgp.MultiAreaView",
	rootView : appView,
	collection : [ nodeInfoParentView ]
};

var hbaseGrowthGraphParentView = {
	viewClassName : "HbaseParentView"
};

var hbaseRegionMapView = {
	viewClassName : "halook.HbaseResionMapParentView",
	viewAttribute : {
		realTime : true,
		width : 800,
		startXAxis :50,
		startYAxis : 350,
		textStartY : 360,
		graphMaxValue : 280,
		colorList : [
		 　　　"#AFEEEE",
		    "#FFC0CB",
		    "#ADFF2F",
		    "#FFA500",
		    "#FFFF00"
		]
	},
	
};

var hbaseGrowthGraphField = {
	viewClassName : "wgp.MultiAreaView",
	rootView : appView,
	collection : [ hbaseGrowthGraphParentView ]
};
var parentTmpElement = {
	viewClassName : "halook.ParentTmpView",
	rootView : appView,
	viewAttribute : {},
};

var ganttChartElement = {
	viewClassName : "ganttChartView",
};

var ganttChartDetailElement = {
	viewClassName : "ganttChartDetailView",
};

var ganttChartParentElement = {
	viewClassName : "ganttChartParentView",
	rootView : appView,
};

var ganttChartViewElement = {
	viewClassName : "wgp.MultiAreaView",
	rootView : appView,
	collection : [ ganttChartParentElement ]

};

var bubbleViewElement = {
	viewClassName : "BubbleChartView",
};

var bubbleMultiElement = {
	viewClassName : "wgp.MultiAreaView",
	rootView : appView,
	collection : [ bubbleViewElement ]
};

var HDFSViewElement = {
	viewClassName : "HDFSView",
	tabTitle : "HDFSView",
};

var HDFSParentElement = {
	viewClassName : "wgp.MultiAreaView",
	collection : [ HDFSViewElement ]
};

var bubbleTabViewElement = {
	viewClassName : "halook.BubbleChartView",
	rootView : appView,
	tabTitle : "Bubble Chart",
};

var arrowTabViewElement = {
	viewClassName : "halook.ArrowParentView",
	rootView : appView,
	tabTitle : "Arrow Chart",
};

var mapReduceTabViewElement = {
	viewClassName : "halook.TabView",
	 collection : [ arrowTabViewElement, bubbleTabViewElement ],
	// collection : [ bubbleTabViewElement ],
//	collection : [ arrowTabViewElement ],
};

wgp.constants.VIEW_SETTINGS = {
	"default" : graphViewElement,
	"process" : nodeInfoField,
	"/hdfs" : HDFSParentElement,
	"/hbase/event" : hbaseGrowthGraphParentView,
	"/hbase/table" : hbaseRegionMapView,
	"/mapreduce/job" : ganttChartViewElement,
	"/mapreduce/task" : mapReduceTabViewElement,
};