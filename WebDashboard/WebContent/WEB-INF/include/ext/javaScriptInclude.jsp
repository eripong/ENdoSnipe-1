<meta charset="UTF-8" />
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<%@ page import="org.wgp.file.filter.FileNameFilter"%>
<%@ page import="org.wgp.file.util.FilePathUtil"%>


<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/lib/jQuery-ui/css/jquery-ui-1.9.2.custom.min.css"
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

<!-- ウィジェット用のjavaScript動的読みこみを行なう -->
<%
	// ウィジェットとなるjavaScriptを指定
	//TODO 残課題 ファイルパス指定を直接記入している。
	List<String> libraryPathList = new ArrayList<String>();
	libraryPathList.add("jQuery");
	libraryPathList.add("jQuery-ui");
	libraryPathList.add("underscore");
	libraryPathList.add("backbone");
	libraryPathList.add("dygraph");
	libraryPathList.add("jeegoocontext");
	libraryPathList.add("jquery.event.drag-2.2");
	libraryPathList.add("jsTree");
	libraryPathList.add("Raphael");
	libraryPathList.add("wgp");
	String libraryJsDirectoryPath = config.getServletContext()
			.getRealPath("resources/lib");
	File libraryJsDirectory = new File(libraryJsDirectoryPath);
	FileNameFilter libraryNameFilter = new FileNameFilter(null, null, "js");

	for (String directoryPath : libraryPathList) {
		File elementDirectory = new File(libraryJsDirectory, directoryPath);
		List<File> elementFileList = FilePathUtil.getAllFilePath(
				elementDirectory, libraryNameFilter);
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
<script
	src="<%=request.getContextPath()%>/resources/lib/jqGrid/js/i18n/grid.locale-<%=request.getLocale().getLanguage()%>.js"
	type="text/javascript"></script>
<script
	src="<%=request.getContextPath()%>/resources/lib/jqGrid/js/jquery.jqGrid.min.js"
	type="text/javascript"></script>