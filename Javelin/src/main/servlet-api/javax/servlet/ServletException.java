package javax.servlet;


public class ServletException extends Exception
{

    public ServletException()
    {
    }

    public ServletException(String message)
    {
        super(message);
    }

    public ServletException(String message, Throwable rootCause)
    {
        super(message);
        this.rootCause = rootCause;
    }

    public ServletException(Throwable rootCause)
    {
        super(rootCause.getLocalizedMessage());
        this.rootCause = rootCause;
    }

    public Throwable getRootCause()
    {
        return rootCause;
    }

    private Throwable rootCause;
}


