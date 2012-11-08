package javax.servlet.http;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.ResourceBundle;
import javax.servlet.*;

// Referenced classes of package javax.servlet.http:
//            NoBodyResponse, HttpServletRequest, HttpServletResponse

public abstract class HttpServlet extends GenericServlet implements Serializable
{

    public HttpServlet()
    {
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException,
            IOException
    {
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_get_not_supported");
        if (protocol.endsWith("1.1"))
            resp.sendError(405, msg);
        else
            resp.sendError(400, msg);
    }

    protected long getLastModified(HttpServletRequest req)
    {
        return -1L;
    }

    protected void doHead(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException,
            IOException
    {
        NoBodyResponse response = new NoBodyResponse(resp);
        doGet(req, response);
        response.setContentLength();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException,
            IOException
    {
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_post_not_supported");
        if (protocol.endsWith("1.1"))
            resp.sendError(405, msg);
        else
            resp.sendError(400, msg);
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException,
            IOException
    {
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_put_not_supported");
        if (protocol.endsWith("1.1"))
            resp.sendError(405, msg);
        else
            resp.sendError(400, msg);
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException,
            IOException
    {
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_delete_not_supported");
        if (protocol.endsWith("1.1"))
            resp.sendError(405, msg);
        else
            resp.sendError(400, msg);
    }

    private Method[] getAllDeclaredMethods(Class c)
    {
        if (c.getName().equals("javax.servlet.http.HttpServlet"))
            return null;
        int j = 0;
        Method parentMethods[] = getAllDeclaredMethods(c.getSuperclass());
        Method thisMethods[] = c.getDeclaredMethods();
        if (parentMethods != null)
        {
            Method allMethods[] = new Method[parentMethods.length + thisMethods.length];
            for (int i = 0; i < parentMethods.length; i++)
            {
                allMethods[i] = parentMethods[i];
                j = i;
            }

            for (int i = ++j; i < thisMethods.length + j; i++)
                allMethods[i] = thisMethods[i - j];

            return allMethods;
        }
        else
        {
            return thisMethods;
        }
    }

    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException,
            IOException
    {
        Method methods[] = getAllDeclaredMethods(getClass());
        boolean ALLOW_GET = false;
        boolean ALLOW_HEAD = false;
        boolean ALLOW_POST = false;
        boolean ALLOW_PUT = false;
        boolean ALLOW_DELETE = false;
        boolean ALLOW_TRACE = true;
        boolean ALLOW_OPTIONS = true;
        for (int i = 0; i < methods.length; i++)
        {
            Method m = methods[i];
            if (m.getName().equals("doGet"))
            {
                ALLOW_GET = true;
                ALLOW_HEAD = true;
            }
            if (m.getName().equals("doPost"))
                ALLOW_POST = true;
            if (m.getName().equals("doPut"))
                ALLOW_PUT = true;
            if (m.getName().equals("doDelete"))
                ALLOW_DELETE = true;
        }

        String allow = null;
        if (ALLOW_GET && allow == null)
            allow = "GET";
        if (ALLOW_HEAD)
            if (allow == null)
                allow = "HEAD";
            else
                allow = allow + ", HEAD";
        if (ALLOW_POST)
            if (allow == null)
                allow = "POST";
            else
                allow = allow + ", POST";
        if (ALLOW_PUT)
            if (allow == null)
                allow = "PUT";
            else
                allow = allow + ", PUT";
        if (ALLOW_DELETE)
            if (allow == null)
                allow = "DELETE";
            else
                allow = allow + ", DELETE";
        if (ALLOW_TRACE)
            if (allow == null)
                allow = "TRACE";
            else
                allow = allow + ", TRACE";
        if (ALLOW_OPTIONS)
            if (allow == null)
                allow = "OPTIONS";
            else
                allow = allow + ", OPTIONS";
        resp.setHeader("Allow", allow);
    }

    protected void doTrace(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException,
            IOException
    {
        String CRLF = "\r\n";
        String responseString = "TRACE " + req.getRequestURI() + " " + req.getProtocol();
        for (Enumeration reqHeaderEnum = req.getHeaderNames(); reqHeaderEnum.hasMoreElements();)
        {
            String headerName = (String)reqHeaderEnum.nextElement();
            responseString = responseString + CRLF + headerName + ": " + req.getHeader(headerName);
        }

        responseString = responseString + CRLF;
        int responseLength = responseString.length();
        resp.setContentType("message/http");
        resp.setContentLength(responseLength);
        ServletOutputStream out = resp.getOutputStream();
        out.print(responseString);
        out.close();
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException,
            IOException
    {
        String method = req.getMethod();
        if (method.equals("GET"))
        {
            long lastModified = getLastModified(req);
            if (lastModified == -1L)
            {
                doGet(req, resp);
            }
            else
            {
                long ifModifiedSince = req.getDateHeader("If-Modified-Since");
                if (ifModifiedSince < (lastModified / 1000L) * 1000L)
                {
                    maybeSetLastModified(resp, lastModified);
                    doGet(req, resp);
                }
                else
                {
                    resp.setStatus(304);
                }
            }
        }
        else if (method.equals("HEAD"))
        {
            long lastModified = getLastModified(req);
            maybeSetLastModified(resp, lastModified);
            doHead(req, resp);
        }
        else if (method.equals("POST"))
            doPost(req, resp);
        else if (method.equals("PUT"))
            doPut(req, resp);
        else if (method.equals("DELETE"))
            doDelete(req, resp);
        else if (method.equals("OPTIONS"))
            doOptions(req, resp);
        else if (method.equals("TRACE"))
        {
            doTrace(req, resp);
        }
        else
        {
            String errMsg = lStrings.getString("http.method_not_implemented");
            Object errArgs[] = new Object[1];
            errArgs[0] = method;
            errMsg = MessageFormat.format(errMsg, errArgs);
            resp.sendError(501, errMsg);
        }
    }

    private void maybeSetLastModified(HttpServletResponse resp, long lastModified)
    {
        if (resp.containsHeader("Last-Modified"))
            return;
        if (lastModified >= 0L)
            resp.setDateHeader("Last-Modified", lastModified);
    }

    public void service(ServletRequest req, ServletResponse res)
        throws ServletException,
            IOException
    {
        HttpServletRequest request;
        HttpServletResponse response;
        try
        {
            request = (HttpServletRequest)req;
            response = (HttpServletResponse)res;
        }
        catch (ClassCastException e)
        {
            throw new ServletException("non-HTTP request or response");
        }
        service(request, response);
    }

    private static final String   METHOD_DELETE     = "DELETE";

    private static final String   METHOD_HEAD       = "HEAD";

    private static final String   METHOD_GET        = "GET";

    private static final String   METHOD_OPTIONS    = "OPTIONS";

    private static final String   METHOD_POST       = "POST";

    private static final String   METHOD_PUT        = "PUT";

    private static final String   METHOD_TRACE      = "TRACE";

    private static final String   HEADER_IFMODSINCE = "If-Modified-Since";

    private static final String   HEADER_LASTMOD    = "Last-Modified";

    private static final String   LSTRING_FILE      = "javax.servlet.http.LocalStrings";

    private static ResourceBundle lStrings          =
                                                            ResourceBundle.getBundle("javax.servlet.http.LocalStrings");

}
