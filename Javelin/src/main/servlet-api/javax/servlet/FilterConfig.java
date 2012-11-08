package javax.servlet;

import java.util.Enumeration;

public interface FilterConfig
{

    public abstract String getFilterName();

    public abstract ServletContext getServletContext();

    public abstract String getInitParameter(String s);

    public abstract Enumeration getInitParameterNames();
}
