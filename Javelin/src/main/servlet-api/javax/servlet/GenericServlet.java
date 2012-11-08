package javax.servlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;

public abstract class GenericServlet implements Servlet, ServletConfig, Serializable
{

    public GenericServlet()
    {
    }

    public void destroy()
    {
    }

    public String getInitParameter(String name)
    {
        return getServletConfig().getInitParameter(name);
    }

    public Enumeration getInitParameterNames()
    {
        return getServletConfig().getInitParameterNames();
    }

    public ServletConfig getServletConfig()
    {
        return config;
    }

    public ServletContext getServletContext()
    {
        return getServletConfig().getServletContext();
    }

    public String getServletInfo()
    {
        return "";
    }

    public void init(ServletConfig config)
        throws ServletException
    {
        this.config = config;
        init();
    }

    public void init()
        throws ServletException
    {
    }

    public void log(String msg)
    {
        getServletContext().log(getServletName() + ": " + msg);
    }

    public void log(String message, Throwable t)
    {
        getServletContext().log(getServletName() + ": " + message, t);
    }

    public abstract void service(ServletRequest servletrequest, ServletResponse servletresponse)
        throws ServletException,
            IOException;

    public String getServletName()
    {
        return config.getServletName();
    }

    private transient ServletConfig config;
}
