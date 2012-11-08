package javax.servlet;

import java.io.IOException;

public interface RequestDispatcher
{

    public abstract void forward(ServletRequest servletrequest, ServletResponse servletresponse)
        throws ServletException,
            IOException;

    public abstract void include(ServletRequest servletrequest, ServletResponse servletresponse)
        throws ServletException,
            IOException;
}
