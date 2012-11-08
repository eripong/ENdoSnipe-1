<meta charset="UTF-8" />
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!--
 WGP  0.1  - Web Graphical Platform
 Copyright (c) 2012, WGP.LICENSES.COM
 Dual licensed under the MIT and GPL licenses
 http://www.opensource.org/licenses/mit-license.php
 http://www.gnu.org/licenses/gpl-2.0.html
 Date: 2012-04-29
-->
<%@ page import="java.io.File"%>
<%@ page import="java.lang.String"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="org.wgp.util.FileNameFilter"%>
<%@ page import="org.wgp.util.FilePathUtil"%>


<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/lib/jQuery/css/jquery-ui-1.8.19.custom.css"
	type="text/css" media="all">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/lib/jqGrid/css/ui.jqgrid.css"
	type="text/css" media="all">
<link rel="stylesheet" type="text/css" media="screen"
	href="<%=request.getContextPath()%>/resources/lib/jeegoocontext/skins/cm_default/style.css" />

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/lib/wgp/css/wgp-graph.css"
	type="text/css" media="all">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/lib/wgp/css/wgp.css"
	type="text/css" media="all">

<!-- <script type="text/javascript" src="<%=request.getContextPath()%>/resources/lib/rgbcolor/rgbcolor.js"></script> -->

<!-- ウィジェット用のjavaScript動的読みこみを行なう -->
<%
	// ウィジェットとなるjavaScriptを指定
	//TODO 残課題 ファイルパス指定を直接記入している。
	List<String> pathList = new ArrayList<String>();
	pathList.add("jQuery");
	pathList.add("underscore");
	pathList.add("backbone");
	pathList.add("dygraph");
	pathList.add("jeegoocontext");
	pathList.add("jqGrid");
	pathList.add("jquery.event.drag-2.2");
	pathList.add("jsTree");
	pathList.add("Raphael");
	pathList.add("slider");
	pathList.add("wgp");
	String jsDirectoryPath = config.getServletContext()
			.getRealPath("resources/lib");
	File jsDirectory = new File(jsDirectoryPath);
	FileNameFilter nameFilter = new FileNameFilter(null, null, "js");

	// widgetMenuの一覧を取得する。
	String widgetKindsDirectoryPath =
		config.getServletContext().getRealPath("resources/lib/wgp/widget");
	File widgetDirectory = new File(widgetKindsDirectoryPath);
	File[] widgetFileList = widgetDirectory.listFiles(nameFilter);

	for (String directoryPath : pathList) {
		File elementDirectory = new File(jsDirectory, directoryPath);
		List<File> elementFileList = FilePathUtil.getAllFilePath(
				elementDirectory, nameFilter);
		if (elementFileList != null && elementFileList.size() > 0) {
			// ウィジェットとなるjavaScriptファイルのファイルパスを基にscriptタグを生成する。
			for (File jsFile : elementFileList) {
				String[] filePaths = jsFile.getAbsolutePath().split("resources");
				String tmpfilePath = "/resources" + filePaths[filePaths.length - 1];
				String filePath = tmpfilePath.replaceAll("\\\\", "/");
				out.print("<script type=\"text/javascript\" ");
				out.print("src=\"" + request.getContextPath()
						+ filePath
						+ "\">");
				out.println("</script>");
			}
		}
	}	
%>