// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HttpServletRequestWrapper.java

package javax.servlet.http;

import java.security.Principal;
import java.util.Enumeration;
import javax.servlet.ServletRequestWrapper;

// Referenced classes of package javax.servlet.http:
//            HttpServletRequest, Cookie, HttpSession

public class HttpServletRequestWrapper extends ServletRequestWrapper
    implements HttpServletRequest
{

    public HttpServletRequestWrapper(HttpServletRequest request)
    {
        super(request);
    }

    private HttpServletRequest _getHttpServletRequest()
    {
        return (HttpServletRequest)super.getRequest();
    }

    public String getAuthType()
    {
        return _getHttpServletRequest().getAuthType();
    }

    public Cookie[] getCookies()
    {
        return _getHttpServletRequest().getCookies();
    }

    public long getDateHeader(String name)
    {
        return _getHttpServletRequest().getDateHeader(name);
    }

    public String getHeader(String name)
    {
        return _getHttpServletRequest().getHeader(name);
    }

    public Enumeration getHeaders(String name)
    {
        return _getHttpServletRequest().getHeaders(name);
    }

    public Enumeration getHeaderNames()
    {
        return _getHttpServletRequest().getHeaderNames();
    }

    public int getIntHeader(String name)
    {
        return _getHttpServletRequest().getIntHeader(name);
    }

    public String getMethod()
    {
        return _getHttpServletRequest().getMethod();
    }

    public String getPathInfo()
    {
        return _getHttpServletRequest().getPathInfo();
    }

    public String getPathTranslated()
    {
        return _getHttpServletRequest().getPathTranslated();
    }

    public String getContextPath()
    {
        return _getHttpServletRequest().getContextPath();
    }

    public String getQueryString()
    {
        return _getHttpServletRequest().getQueryString();
    }

    public String getRemoteUser()
    {
        return _getHttpServletRequest().getRemoteUser();
    }

    public boolean isUserInRole(String role)
    {
        return _getHttpServletRequest().isUserInRole(role);
    }

    public Principal getUserPrincipal()
    {
        return _getHttpServletRequest().getUserPrincipal();
    }

    public String getRequestedSessionId()
    {
        return _getHttpServletRequest().getRequestedSessionId();
    }

    public String getRequestURI()
    {
        return _getHttpServletRequest().getRequestURI();
    }

    public StringBuffer getRequestURL()
    {
        return _getHttpServletRequest().getRequestURL();
    }

    public String getServletPath()
    {
        return _getHttpServletRequest().getServletPath();
    }

    public HttpSession getSession(boolean create)
    {
        return _getHttpServletRequest().getSession(create);
    }

    public HttpSession getSession()
    {
        return _getHttpServletRequest().getSession();
    }

    public boolean isRequestedSessionIdValid()
    {
        return _getHttpServletRequest().isRequestedSessionIdValid();
    }

    public boolean isRequestedSessionIdFromCookie()
    {
        return _getHttpServletRequest().isRequestedSessionIdFromCookie();
    }

    public boolean isRequestedSessionIdFromURL()
    {
        return _getHttpServletRequest().isRequestedSessionIdFromURL();
    }

    public boolean isRequestedSessionIdFromUrl()
    {
        return _getHttpServletRequest().isRequestedSessionIdFromUrl();
    }
}
