<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/common.css"
	type="text/css" media="all">
<%@ include file="../include/ext/javaScriptInclude.jsp"%>
<%@ include file="../include/MapListInclude.jsp"%>
</head>
<body id="main" oncontextmenu="return false;" onload="self.focus();">
	<div id="persArea"></div>
	<input id="treeData" type="hidden" value='${treeData}' />
	<script type="text/javascript">
		var viewArea1 = {
			width : 300,
			height : 600,
			rowspan : 2,
			colspan : 1
		};
		var viewArea2 = {
			width : 900,
			height : 550,
			rowspan : 1,
			colspan : 1
		};
		var viewArea3 = {
			width : 900,
			height : 50,
			rowspan : 1,
			colspan : 1
		};

		var table = [ [ new wgp.PerspectiveModel(viewArea1),
				new wgp.PerspectiveModel(viewArea2) ], 
				[new wgp.PerspectiveModel(viewArea3)]];
		var perspectiveView = new wgp.PerspectiveView({
			id : "persArea",
			collection : table,
			minimum : false,
			close : false
		});
		
		// TODO WGPを改修し、barを非表示にする。
		$('#persArea_bar_1_0').hide();
		
		perspectiveView.dropView("persArea_drop_0_0", "tree_area", "GraphTree");
		perspectiveView.dropView("persArea_drop_0_0", "list_area", "MapList");
		perspectiveView.dropView("persArea_drop_0_1", "contents_area", "MapView");

		var appView = new ENS.AppView();
		</script>
		
		<script src="<%=request.getContextPath()%>/resources/js/common/user.js"
	type="text/javaScript"></script>
		
	<script type="text/javascript">
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

//		var rootTreeModel = new wgp.TreeModel({
//			id : "mapRoot",
//			treeId : "mapRoot",
//			data : "root",
//			parentTreeId : "",
//		});

//		var groupTreeModel1 = new wgp.TreeModel({
//			id : "mapGroup1",
//			treeId : "mapGroup1",
//			data : "group1",
//			parentTreeId : "mapRoot"
//		});

//		var groupTreeModel2 = new wgp.TreeModel({
//			id : "mapGroup2",
//			treeId : "mapGroup2",
//			data : "group2",
//			parentTreeId : "mapRoot"
//		});

//		resourceMapListView.collection = new TreeModelList();
//		resourceMapListView.registerCollectionEvent();
//		resourceMapListView.collection.add(rootTreeModel);
//		resourceMapListView.collection.add(groupTreeModel1);
//		resourceMapListView.collection.add(groupTreeModel2);
		
		// Create Menu View
		var createMapMenuModel = new ENS.mapMenuModel({
			width : 25,
			height : 25,
			styleClass : 'map_menu_icon',
			src : '<%=request.getContextPath()%>/resources/images/map/createIcon.png',
			alt : 'save',
			onclick : (function(event){
				if(resourceMapListView.childView){
					resourceMapListView.childView.onCreate();
				}else{
					console.log("please select a map");
				}
			})
		});

		var saveMapMenuModel = new ENS.mapMenuModel({
			width : 25,
			height : 25,
			styleClass : 'map_menu_icon',
			src : '<%=request.getContextPath()%>/resources/images/map/saveIcon.png',
			alt : 'save',
			onclick : (function(event){
				if(resourceMapListView.childView){
					var selectedId = $("#" + resourceMapListView.id).find(".jstree-clicked")[0].id;
					var treeModel = resourceMapListView.collection.where({id : selectedId})[0];
					resourceMapListView.childView.onSave(treeModel);
				}else{
					console.log("please select a map");
				}
			})
		});

		var menuView = new ENS.mapMenuView({
				id : "persArea_drop_1_0",
				collection : [createMapMenuModel, saveMapMenuModel]
			},
			{}
		);

	</script>
</body>
</html>