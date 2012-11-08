<meta charset="UTF-8" />
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/nodeinfomation/nodeStyles.css"
	type="text/css" media="all">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/lib/slider/ui.slider.extras.css"
	type="text/css" media="all">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/lib/jQuery/css/jquery-ui-1.8.19.custom.css"
	type="text/css" media="all">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/lib/jqGrid/css/ui.jqgrid.css"
	type="text/css" media="all">
<link rel="stylesheet" type="text/css" media="screen"
	href="<%=request.getContextPath()%>/resources/lib/jeegoocontext/skins/cm_default/style.css" 
	type="text/css" media="all"/>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/lib/pagination/pagination.css"
	type="text/css" media="all">

<%-- libraries --%>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/lib/pagination/jquery.pagination.js">
</script>

<%-- common static value --%>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/js/common/constants.js">
</script>

<%-- dual slider --%>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/js/common/dualSliderView.js">
</script>

<%-- nodeInfo graph --%>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/js/nodeinfomation/view/nodeInfoParentView.js">
</script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/js/nodeinfomation/view/resourceGraphView.js">
</script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/js/nodeinfomation/model/resourceGraphModel.js">	
</script>
