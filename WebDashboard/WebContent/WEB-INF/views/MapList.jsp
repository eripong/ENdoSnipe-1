<!DOCTYPE html>
<html>
<head>
<%@ include file="../include/ext/javaScriptInclude.jsp"%>
<%@ include file="../include/MapListInclude.jsp"%>
</head>
<body id="main" oncontextmenu="return false;" onload="self.focus();">
	<div id="persArea"></div>
	<input id="treeData" type="hidden" value='${treeData}' />
	<script type="text/javascript">
		var viewArea1 = {};
		var viewArea2 = {};

		viewArea1.width = 300;
		viewArea1.height = 600;
		viewArea1.rowspan = 1;
		viewArea1.colspan = 1;

		viewArea2.width = 900;
		viewArea2.height = 600;
		viewArea2.rowspan = 1;
		viewArea2.colspan = 1;

		var table = [ [ new wgp.PerspectiveModel(viewArea1),
				new wgp.PerspectiveModel(viewArea2) ] ];
		var perspactiveView = new wgp.PerspectiveView({
			id : "persArea",
			collection : table,
			minimum : false,
			close : false
		});
		perspactiveView.dropView("persArea_drop_0_0", "tree_area", "GraphTree");
		perspactiveView.dropView("persArea_drop_0_0", "list_area", "MapList");
		perspactiveView.dropView("persArea_drop_0_1", "contents_area", "MapView");

		var appView = new ENS.AppView();
	</script>

	<script src="<%=request.getContextPath()%>/resources/js/common/user.js"
		type="text/javaScript"></script>

	<script>
		var resourceTreeView = new ENS.ResourceTreeView({
			id : "tree_area",
			targetId : "contents_area",
			mode : "arrangement",
			themeUrl : wgp.common.getContextPath()
			+ "/resources/css/jsTree/style.css"
		});

		appView.addView(resourceTreeView, wgp.constants.TREE.DATA_ID);
		websocketClient = new wgp.WebSocketClient(appView, "notifyEvent");
		websocketClient.initialize();
		appView.getTermData([ wgp.constants.TREE.DATA_ID ], new Date(),
				new Date());
		
		var resourceMapListView = new ENS.ResourceMapListView({
			id : "list_area",
			targetId : "contents_area",
			themeUrl : wgp.common.getContextPath()
			+ "/resources/css/jsTree/style.css"
		});

		var rootTreeModel = new wgp.TreeModel({
			id : "mapRoot",
			treeId : "mapRoot",
			data : "root",
			parentTreeId : "",
		});

		var groupTreeModel1 = new wgp.TreeModel({
			id : "mapGroup1",
			treeId : "mapGroup1",
			data : "group1",
			parentTreeId : "mapRoot"
		});

		var groupTreeModel2 = new wgp.TreeModel({
			id : "mapGroup2",
			treeId : "mapGroup2",
			data : "group2",
			parentTreeId : "mapRoot"
		});

		resourceMapListView.collection = new TreeModelList();
		resourceMapListView.registerCollectionEvent();
		resourceMapListView.collection.add(rootTreeModel);
		resourceMapListView.collection.add(groupTreeModel1);
		resourceMapListView.collection.add(groupTreeModel2);

	</script>
</body>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/common.css"
	type="text/css" media="all">
</html>