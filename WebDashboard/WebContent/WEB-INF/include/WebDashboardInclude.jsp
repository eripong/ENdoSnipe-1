<meta charset="UTF-8" />
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/nodeinfomation/nodeStyles.css"
	type="text/css" media="all">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/lib/slider/ui.slider.extras.css"
	type="text/css" media="all">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/lib/pagination/pagination.css"
	type="text/css" media="all">

<%-- libraries --%>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/lib/pagination/jquery.pagination.js">
</script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/lib/slider/selectToUISlider.jQuery.js">
</script>

<%-- common static value --%>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/js/common/constants.js">
</script>

<%-- dual slider --%>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/js/common/dualSliderView.js">
</script>

<%-- utility --%>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/js/common/endoSnipeUtility.js">
</script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/js/common/ensAppView.js">	
</script>

<%-- resource tree --%>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/js/nodeinfomation/view/resourceTreeView.js">	
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
<script type="text/javascript"
	src="<%=request.getContextPath()%>/resources/js/tree/ensTreeView.js">	
</script>
