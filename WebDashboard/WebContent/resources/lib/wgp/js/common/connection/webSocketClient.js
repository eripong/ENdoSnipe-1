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
wgp.WebSocketClient = function(handler, methodName) {
	var instance = this;
    this.handler = handler;
    this.methodName = methodName;
    this.isOpen = false;
	wgp.WebSocketClient = function() {
		return instance;
	};
};

wgp.WebSocketClient.prototype.initialize = function(url) {
	if (!url) {
		url = wgp.ConnectionConstants.DEF_WEBSOCKET_SERVELET_PROTOCOL + "://"
				+ window.location.host + wgp.common.getContextPath()
				+ wgp.ConnectionConstants.DEF_WEBSOCKET_SERVELET_NAME;
	}
	this.ws = new WebSocket(url);
	this.blobBuilder = window.BlobBuilder || window.WebKitBlobBuilder
			|| window.MozBlobBuilder;

    var instance = this;
	this.ws.onopen = function() {
		instance.isOpen = true;
	};

	this.ws.onmessage = function(message) {
		if (!message || !message.data) {
			return;
		}
		var result = $.parseJSON(message.data);
        instance.handler[instance.methodName](result);
	};
};

wgp.WebSocketClient.prototype.send = function(message) {
	var instance = this;
	if (!this.isOpen) {
		var tmpMessage = message;
		setTimeout(function(){
			instance.send(tmpMessage);
		}, 100);
		return;
	}
	this.ws.send(message);
};

wgp.WebSocketClient.prototype.sendBinary = function(message) {
	var instance = this;
	if (!this.isOpen) {
		var tmpMessage = message;
		setTimeout(function(){
			instance.sendBinary(tmpMessage);
		}, 100);
		return;
	}
	this.ws.binaryType = 'arraybuffer';
	this.ws.send(message);
};
