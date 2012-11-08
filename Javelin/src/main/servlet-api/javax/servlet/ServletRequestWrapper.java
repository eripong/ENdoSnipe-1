package javax.servlet;

import java.io.*;
import java.util.*;

// Referenced classes of package javax.servlet:
//            ServletRequest, ServletInputStream, RequestDispatcher

public class ServletRequestWrapper implements ServletRequest
{

    public ServletRequestWrapper(ServletRequest request)
    {
        if (request == null)
        {
            throw new IllegalArgumentException("Request cannot be null");
        }
        else
        {
            this.request = request;
            return;
        }
    }

    public ServletRequest getRequest()
    {
        return request;
    }

    public void setRequest(ServletRequest request)
    {
        if (request == null)
        {
            throw new IllegalArgumentException("Request cannot be null");
        }
        else
        {
            this.request = request;
            return;
        }
    }

    public Object getAttribute(String name)
    {
        return request.getAttribute(name);
    }

    public Enumeration getAttributeNames()
    {
        return request.getAttributeNames();
    }

    public String getCharacterEncoding()
    {
        return request.getCharacterEncoding();
    }

    public void setCharacterEncoding(String enc)
        throws UnsupportedEncodingException
    {
        request.setCharacterEncoding(enc);
    }

    public int getContentLength()
    {
        return request.getContentLength();
    }

    public String getContentType()
    {
        return request.getContentType();
    }

    public ServletInputStream getInputStream()
        throws IOException
    {
        return request.getInputStream();
    }

    public String getParameter(String name)
    {
        return request.getParameter(name);
    }

    public Map getParameterMap()
    {
        return request.getParameterMap();
    }

    public Enumeration getParameterNames()
    {
        return request.getParameterNames();
    }

    public String[] getParameterValues(String name)
    {
        return request.getParameterValues(name);
    }

    public String getProtocol()
    {
        return request.getProtocol();
    }

    public String getScheme()
    {
        return request.getScheme();
    }

    public String getServerName()
    {
        return request.getServerName();
    }

    public int getServerPort()
    {
        return request.getServerPort();
    }

    public BufferedReader getReader()
        throws IOException
    {
        return request.getReader();
    }

    public String getRemoteAddr()
    {
        return request.getRemoteAddr();
    }

    public String getRemoteHost()
    {
        return request.getRemoteHost();
    }

    public void setAttribute(String name, Object o)
    {
        request.setAttribute(name, o);
    }

    public void removeAttribute(String name)
    {
        request.removeAttribute(name);
    }

    public Locale getLocale()
    {
        return request.getLocale();
    }

    public Enumeration getLocales()
    {
        return request.getLocales();
    }

    public boolean isSecure()
    {
        return request.isSecure();
    }

    public RequestDispatcher getRequestDispatcher(String path)
    {
        return request.getRequestDispatcher(path);
    }

    public String getRealPath(String path)
    {
        return request.getRealPath(path);
    }

    public int getRemotePort()
    {
        return request.getRemotePort();
    }

    public String getLocalName()
    {
        return request.getLocalName();
    }

    public String getLocalAddr()
    {
        return request.getLocalAddr();
    }

    public int getLocalPort()
    {
        return request.getLocalPort();
    }

    private ServletRequest request;
}
