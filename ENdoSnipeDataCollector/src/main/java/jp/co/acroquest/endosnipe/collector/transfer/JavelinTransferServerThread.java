/*******************************************************************************
 * ENdoSnipe 5.0 - (https://github.com/endosnipe)
 * 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2012 Acroquest Technology Co.,Ltd.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package jp.co.acroquest.endosnipe.collector.transfer;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.impl.CommunicationServerImpl;
import jp.co.acroquest.endosnipe.communicator.impl.JavelinClientThread;

public class JavelinTransferServerThread extends CommunicationServerImpl
{
    List<TelegramListener> listenerList_ = new ArrayList<TelegramListener>();

    public JavelinTransferServerThread()
    {
        super(false, 0, 10 * 1000, 10000, new String[]{});
    }

    @Override
    public void addTelegramListener(final TelegramListener listener)
    {
        listenerList_.add(listener);
    }

    @Override
    protected JavelinClientThread createJavelinClientThread(final Socket clientSocket)
        throws IOException
    {
        JavelinClientThread javelinClientThread =
                                                  new JavelinClientThread(clientSocket,
                                                                          isDiscard(),
                                                                          getListeners());
        for (TelegramListener listener : listenerList_)
        {
            javelinClientThread.addListener(listener);
        }
        return javelinClientThread;
    }
}
