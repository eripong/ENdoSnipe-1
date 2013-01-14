/*****************************************************************
 WGP  1.0B  - Web Graphical Platform
   (https://sourceforge.net/projects/wgp/)

 The MIT License (MIT)
 
 Copyright (c) 2012 Acroquest Technology Co.,Ltd.
 
 Permission is hereby granted, free of charge, to any person obtaining 
 a copy of this software and associated documentation files
 (the "Software"), to deal in the Software without restriction, 
 including without limitation the rights to use, copy, modify, merge,
 publish, distribute, sublicense, and/or sell copies of the Software,
 and to permit persons to whom the Software is furnished to do so, 
 subject to the following conditions:
 
 The above copyright notice and this permission notice shall be 
 included in all copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
 SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*****************************************************************/
wgp.ConnectionConstants = {};

// 定数定義
/**
 * 処理成功時に呼び出すオブジェクト格納key
 */
wgp.ConnectionConstants.SUCCESS_CALL_OBJECT_KEY = "successCallObject";

/**
 * 処理成功時に呼び出す関数名格納key
 */
wgp.ConnectionConstants.SUCCESS_CALL_FUNCTION_KEY = "successCallFunction";

/**
 * 処理失敗時に呼び出すオブジェクト格納key
 */
wgp.ConnectionConstants.ERROR_CALL_OBJECT_KEY = "errorCallObject";

/**
 * 処理失敗時に呼び出す関数名格納key
 */
wgp.ConnectionConstants.ERROR_CALL_FUNCTION_KEY = "errorCallFunction";

/** Setting for Default WebSocketServlet PROTOCOL */
wgp.ConnectionConstants.DEF_WEBSOCKET_SERVELET_PROTOCOL = "ws";

/** Setting for Default WebSocketServlet PATH */
wgp.ConnectionConstants.DEF_WEBSOCKET_SERVELET_NAME = "/webSocketServlet";
