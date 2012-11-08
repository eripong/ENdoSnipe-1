package javax.servlet;

import java.io.IOException;

public interface Servlet
{

    public abstract void init(ServletConfig servletconfig)
        throws ServletException;

    public abstract ServletConfig getServletConfig();

    public abstract void service(ServletRequest servletrequest, ServletResponse servletresponse)
        throws ServletException,
            IOException;

    public abstract String getServletInfo();

    public abstract void destroy();
}
