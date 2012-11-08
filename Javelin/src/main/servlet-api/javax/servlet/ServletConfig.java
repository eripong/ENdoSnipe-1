package javax.servlet;

import java.util.Enumeration;

public interface ServletConfig
{

    public abstract String getServletName();

    public abstract ServletContext getServletContext();

    public abstract String getInitParameter(String s);

    public abstract Enumeration getInitParameterNames();
}
