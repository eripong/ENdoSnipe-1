package javax.servlet;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

public interface ServletContext
{

    public abstract ServletContext getContext(String s);
    
    public String getContextPath();

    public abstract int getMajorVersion();

    public abstract int getMinorVersion();

    public abstract String getMimeType(String s);

    public abstract Set getResourcePaths(String s);

    public abstract URL getResource(String s)
        throws MalformedURLException;

    public abstract InputStream getResourceAsStream(String s);

    public abstract RequestDispatcher getRequestDispatcher(String s);

    public abstract RequestDispatcher getNamedDispatcher(String s);

    /**
     * @deprecated Method getServlet is deprecated
     */

    public abstract Servlet getServlet(String s)
        throws ServletException;

    /**
     * @deprecated Method getServlets is deprecated
     */

    public abstract Enumeration getServlets();

    /**
     * @deprecated Method getServletNames is deprecated
     */

    public abstract Enumeration getServletNames();

    public abstract void log(String s);

    /**
     * @deprecated Method log is deprecated
     */

    public abstract void log(Exception exception, String s);

    public abstract void log(String s, Throwable throwable);

    public abstract String getRealPath(String s);

    public abstract String getServerInfo();

    public abstract String getInitParameter(String s);

    public abstract Enumeration getInitParameterNames();

    public abstract Object getAttribute(String s);

    public abstract Enumeration getAttributeNames();

    public abstract void setAttribute(String s, Object obj);

    public abstract void removeAttribute(String s);

    public abstract String getServletContextName();
}
