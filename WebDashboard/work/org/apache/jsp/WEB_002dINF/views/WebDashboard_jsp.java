/*******************************************************************************
 * ENdoSnipe 5.0 - (https://github.com/endosnipe)
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
package org.apache.jsp.WEB_002dINF.views;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.io.File;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import org.wgp.util.FileNameFilter;
import org.wgp.util.FilePathUtil;

public final class WebDashboard_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final javax.servlet.jsp.JspFactory _jspxFactory =
          javax.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  static {
    _jspx_dependants = new java.util.HashMap<java.lang.String,java.lang.Long>(2);
    _jspx_dependants.put("/WEB-INF/views/../include/ext/javaScriptInclude.jsp", Long.valueOf(1352034772899L));
    _jspx_dependants.put("/WEB-INF/views/../include/halookInclude.jsp", Long.valueOf(1352035341173L));
  }

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
  }

  public void _jspDestroy() {
  }

  public void _jspService(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)
        throws java.io.IOException, javax.servlet.ServletException {

    final javax.servlet.jsp.PageContext pageContext;
    javax.servlet.http.HttpSession session = null;
    final javax.servlet.ServletContext application;
    final javax.servlet.ServletConfig config;
    javax.servlet.jsp.JspWriter out = null;
    final java.lang.Object page = this;
    javax.servlet.jsp.JspWriter _jspx_out = null;
    javax.servlet.jsp.PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html; charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("<!DOCTYPE html>\r\n");
      out.write("<html>\r\n");
      out.write("<head>\r\n");
      out.write("<meta charset=\"UTF-8\" />\r\n");
      out.write("\r\n");
      out.write("<!--\r\n");
      out.write(" WGP  0.1  - Web Graphical Platform\r\n");
      out.write(" Copyright (c) 2012, WGP.LICENSES.COM\r\n");
      out.write(" Dual licensed under the MIT and GPL licenses\r\n");
      out.write(" http://www.opensource.org/licenses/mit-license.php\r\n");
      out.write(" http://www.gnu.org/licenses/gpl-2.0.html\r\n");
      out.write(" Date: 2012-04-29\r\n");
      out.write("-->\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<link rel=\"stylesheet\"\r\n");
      out.write("\thref=\"");
      out.print(request.getContextPath());
      out.write("/resources/lib/jQuery/css/jquery-ui-1.8.19.custom.css\"\r\n");
      out.write("\ttype=\"text/css\" media=\"all\">\r\n");
      out.write("<link rel=\"stylesheet\"\r\n");
      out.write("\thref=\"");
      out.print(request.getContextPath());
      out.write("/resources/lib/jqGrid/css/ui.jqgrid.css\"\r\n");
      out.write("\ttype=\"text/css\" media=\"all\">\r\n");
      out.write("<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\"\r\n");
      out.write("\thref=\"");
      out.print(request.getContextPath());
      out.write("/resources/lib/jeegoocontext/skins/cm_default/style.css\" />\r\n");
      out.write("\r\n");
      out.write("<link rel=\"stylesheet\"\r\n");
      out.write("\thref=\"");
      out.print(request.getContextPath());
      out.write("/resources/lib/wgp/css/wgp-graph.css\"\r\n");
      out.write("\ttype=\"text/css\" media=\"all\">\r\n");
      out.write("<link rel=\"stylesheet\"\r\n");
      out.write("\thref=\"");
      out.print(request.getContextPath());
      out.write("/resources/lib/wgp/css/wgp.css\"\r\n");
      out.write("\ttype=\"text/css\" media=\"all\">\r\n");
      out.write("\r\n");
      out.write("<!-- <script type=\"text/javascript\" src=\"");
      out.print(request.getContextPath());
      out.write("/resources/lib/rgbcolor/rgbcolor.js\"></script> -->\r\n");
      out.write("\r\n");
      out.write("<!-- ウィジェット用のjavaScript動的読みこみを行なう -->\r\n");

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

      out.write('\r');
      out.write('\n');
      out.write("<meta charset=\"UTF-8\" />\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<link rel=\"stylesheet\"\r\n");
      out.write("\thref=\"");
      out.print(request.getContextPath());
      out.write("/resources/css/nodeinfomation/nodeStyles.css\"\r\n");
      out.write("\ttype=\"text/css\" media=\"all\">\r\n");
      out.write("<link rel=\"stylesheet\"\r\n");
      out.write("\thref=\"");
      out.print(request.getContextPath());
      out.write("/resources/lib/slider/ui.slider.extras.css\"\r\n");
      out.write("\ttype=\"text/css\" media=\"all\">\r\n");
      out.write("<link rel=\"stylesheet\"\r\n");
      out.write("\thref=\"");
      out.print(request.getContextPath());
      out.write("/resources/lib/jQuery/css/jquery-ui-1.8.19.custom.css\"\r\n");
      out.write("\ttype=\"text/css\" media=\"all\">\r\n");
      out.write("<link rel=\"stylesheet\"\r\n");
      out.write("\thref=\"");
      out.print(request.getContextPath());
      out.write("/resources/lib/jqGrid/css/ui.jqgrid.css\"\r\n");
      out.write("\ttype=\"text/css\" media=\"all\">\r\n");
      out.write("<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\"\r\n");
      out.write("\thref=\"");
      out.print(request.getContextPath());
      out.write("/resources/lib/jeegoocontext/skins/cm_default/style.css\" \r\n");
      out.write("\ttype=\"text/css\" media=\"all\"/>\r\n");
      out.write("<link rel=\"stylesheet\"\r\n");
      out.write("\thref=\"");
      out.print(request.getContextPath());
      out.write("/resources/lib/pagination/pagination.css\"\r\n");
      out.write("\ttype=\"text/css\" media=\"all\">\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<script type=\"text/javascript\"\r\n");
      out.write("\tsrc=\"");
      out.print(request.getContextPath());
      out.write("/resources/lib/pagination/jquery.pagination.js\">\r\n");
      out.write("</script>\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<script type=\"text/javascript\"\r\n");
      out.write("\tsrc=\"");
      out.print(request.getContextPath());
      out.write("/resources/js/common/constants.js\">\r\n");
      out.write("</script>\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<script type=\"text/javascript\"\r\n");
      out.write("\tsrc=\"");
      out.print(request.getContextPath());
      out.write("/resources/js/common/dualSliderView.js\">\r\n");
      out.write("</script>\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<script type=\"text/javascript\"\r\n");
      out.write("\tsrc=\"");
      out.print(request.getContextPath());
      out.write("/resources/js/nodeinfomation/view/nodeInfoParentView.js\">\r\n");
      out.write("</script>\r\n");
      out.write("<script type=\"text/javascript\"\r\n");
      out.write("\tsrc=\"");
      out.print(request.getContextPath());
      out.write("/resources/js/nodeinfomation/view/resourceGraphView.js\">\r\n");
      out.write("</script>\r\n");
      out.write("<script type=\"text/javascript\"\r\n");
      out.write("\tsrc=\"");
      out.print(request.getContextPath());
      out.write("/resources/js/nodeinfomation/model/resourceGraphModel.js\">\t\r\n");
      out.write("</script>\r\n");
      out.write("\r\n");
      out.write("</head>\r\n");
      out.write("<body id=\"main\" oncontextmenu=\"return false;\" onload=\"self.focus();\">\r\n");
      out.write("\t<div id=\"persArea\"></div>\r\n");
      out.write("\t<input id=\"treeData\" type=\"hidden\" value='");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${treeData}", java.lang.String.class, (javax.servlet.jsp.PageContext)_jspx_page_context, null, false));
      out.write("' />\r\n");
      out.write("\t<script type=\"text/javascript\">\r\n");
      out.write("\t\tvar viewArea1 = {};\r\n");
      out.write("\t\tvar viewArea2 = {};\r\n");
      out.write("\r\n");
      out.write("\t\tviewArea1.width = 300;\r\n");
      out.write("\t\tviewArea1.height = 800;\r\n");
      out.write("\t\tviewArea1.rowspan = 1;\r\n");
      out.write("\t\tviewArea1.colspan = 1;\r\n");
      out.write("\r\n");
      out.write("\t\tviewArea2.width = 900;\r\n");
      out.write("\t\tviewArea2.height = 800;\r\n");
      out.write("\t\tviewArea2.rowspan = 1;\r\n");
      out.write("\t\tviewArea2.colspan = 1;\r\n");
      out.write("\r\n");
      out.write("\t\tvar table = [ [ new wgp.PerspactiveModel(viewArea1),\r\n");
      out.write("\t\t\t\tnew wgp.PerspactiveModel(viewArea2) ] ];\r\n");
      out.write("\t\tvar perspactiveView = new wgp.PerspactiveView({\r\n");
      out.write("\t\t\tid : \"persArea\",\r\n");
      out.write("\t\t\tcollection : table\r\n");
      out.write("\t\t});\r\n");
      out.write("\t\tperspactiveView.dropView(\"persArea_drop_0_0\", \"tree_area\");\r\n");
      out.write("\t\tperspactiveView.dropView(\"persArea_drop_0_1\", \"contents_area\");\r\n");
      out.write("\r\n");
      out.write("\t\tvar appView = new wgp.AppView();\r\n");
      out.write("\t</script>\r\n");
      out.write("\r\n");
      out.write("\t<script src=\"/WebDashboard/resources/js/common/user.js\"\r\n");
      out.write("\t\ttype=\"text/javaScript\"></script>\r\n");
      out.write("\r\n");
      out.write("\t<script>\r\n");
      out.write("\t\tvar treeView = new wgp.TreeView({\r\n");
      out.write("\t\t\tid : \"tree_area\",\r\n");
      out.write("\t\t\ttargetId : \"contents_area\"\r\n");
      out.write("\t\t});\r\n");
      out.write("\t\tappView.addView(treeView, wgp.constants.TREE.DATA_ID);\r\n");
      out.write("\t\twebsocketClient = new wgp.WebSocketClient(appView, \"notifyEvent\");\r\n");
      out.write("\t\twebsocketClient.initialize();\r\n");
      out.write("\t\tappView.getTermData([ wgp.constants.TREE.DATA_ID ], new Date(),\r\n");
      out.write("\t\t\t\tnew Date());\r\n");
      out.write("\r\n");
      out.write("\t\t$(\"#tree_area\")\r\n");
      out.write("\t\t\t\t.click(\r\n");
      out.write("\t\t\t\t\t\tfunction() {\r\n");
      out.write("\t\t\t\t\t\t\tif (document.getElementById(\"/mapreduce/task\") != undefined) {\r\n");
      out.write("\r\n");
      out.write("\t\t\t\t\t\t\t\tvar parentElem = document\r\n");
      out.write("\t\t\t\t\t\t\t\t\t\t.getElementById(\"/mapreduce/task\").parentNode;\r\n");
      out.write("\t\t\t\t\t\t\t\t$(\"#tree_area\").jstree(\"delete_node\",\r\n");
      out.write("\t\t\t\t\t\t\t\t\t\tparentElem);\r\n");
      out.write("\t\t\t\t\t\t\t}\r\n");
      out.write("\t\t\t\t\t\t});\r\n");
      out.write("\t</script>\r\n");
      out.write("</body>\r\n");
      out.write("</html>");
    } catch (java.lang.Throwable t) {
      if (!(t instanceof javax.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
