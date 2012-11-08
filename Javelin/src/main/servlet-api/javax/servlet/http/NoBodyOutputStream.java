package javax.servlet.http;

import java.io.IOException;
import java.util.ResourceBundle;
import javax.servlet.ServletOutputStream;

class NoBodyOutputStream extends ServletOutputStream
{

    NoBodyOutputStream()
    {
        contentLength = 0;
    }

    int getContentLength()
    {
        return contentLength;
    }

    public void write(int b)
    {
        contentLength++;
    }

    public void write(byte buf[], int offset, int len)
        throws IOException
    {
        if (len >= 0)
        {
            contentLength += len;
        }
        else
        {
            String msg = lStrings.getString("err.io.negativelength");
            throw new IOException("negative length");
        }
    }

    private static final String   LSTRING_FILE = "javax.servlet.http.LocalStrings";

    private static ResourceBundle lStrings     =
                                                       ResourceBundle.getBundle("javax.servlet.http.LocalStrings");

    private int                   contentLength;

}
