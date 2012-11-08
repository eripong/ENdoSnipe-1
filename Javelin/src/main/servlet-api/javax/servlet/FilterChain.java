package javax.servlet;

import java.io.IOException;

public interface FilterChain
{

    public abstract void doFilter(ServletRequest servletrequest, ServletResponse servletresponse)
        throws IOException, ServletException;
}
