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
wgp.AjaxHandler = function() {
	this.settings_ = null;
	this.errorCallObject_ = null;
	this.errorCallFunction_ = null;
	this.successCallObject_ = null;
	this.successCallFunction_ = null;
};

/**
 * <p>
 * [概 要] 通信に失敗した場合の処理。
 * </p>
 * <p>
 * [詳 細] 通信に失敗した場合に走る処理。 取得したHttpエラーコードをダイアログに表示する。
 * </p>
 * <p>
 * [備 考] なし
 * </p>
 * 
 * @private
 * @param {XmlHttpRequest}
 *            result
 * @param {String}
 *            status
 * @param {Exception}
 *            ex
 */
wgp.AjaxHandler.prototype.handleException = function(result, status, ex) {
	// Do nothing
};

/**
 * <p>
 * [概 要] 通信に成功した場合の処理。
 * </p>
 * <p>
 * [詳 細] 通信に成功した場合に走る処理。
 * </p>
 * <p>
 * [備 考] なし
 * </p>
 * 
 * @private
 * @param {String}
 *            data
 * @param {String}
 *            status
 * @param {XmlHttpRequest}
 *            request
 */
wgp.AjaxHandler.prototype.handleSuccess = function(data, status, request) {
	var instance = this;
	if (request.status < 200 || request.status >= 300) {
		instance.handleException(request, status, null);
		return;
	}
	// callback関数を呼び出す。
	if (this.successCallObject_) {
		if (this.successCallFunction_) {
			this.successCallObject_[this.successCallFunction_](data);
		}
	}
};

/**
 * <p>
 * [概 要] POST通信処理を行う。
 * </p>
 * <p>
 * [詳 細] POST通信処理を行う。
 * </p>
 * <p>
 * [備 考] なし
 * </p>
 * 
 * @private
 * @param {Object}
 *            settings
 */
wgp.AjaxHandler.prototype.postConnection = function(settings) {
	var instance = this;
	var settingsArray = {
		type : "POST",
		contentType : 'application/x-www-form-urlencoded'
	};
	settings["connectType"] = "ajax";

	// コールバックオブジェクトが設定されている場合はコールバックを設定する。
	if (settings[wgp.ConnectionConstants.SUCCESS_CALL_OBJECT_KEY]) {
		settingsArray["success"] = function(data, status, request) {
			instance.handleSuccess(data, status, request);
		};
	}

	if (settings[wgp.ConnectionConstants.ERROR_CALL_OBJECT_KEY]) {
		settingsArray["error"] = function(data, status, request) {
			instance.handleException(data, status, request);
		};
	}

	jQuery.each(settings, function(index, object) {
		if (index == wgp.ConnectionConstants.SUCCESS_CALL_OBJECT_KEY) {
			// 成功時の処理対象。
			instance.successCallObject_ = object;
		} else if (index == wgp.ConnectionConstants.SUCCESS_CALL_FUNCTION_KEY) {
			// 成功時の処理関数名
			instance.successCallFunction_ = object;
		} else if (index == wgp.ConnectionConstants.ERROR_CALL_OBJECT_KEY) {
			// Keyがsuccessの場合はsuccessCallObject_に設定する。
			instance.errorCallObject_ = object;
		} else if (index == wgp.ConnectionConstants.ERROR_CALL_FUNCTION_KEY) {
			instance.errorCallFunction_ = object;
		} else {
			settingsArray[index] = object;
		}
	});

	var result = jQuery.ajax(settingsArray);
	return result;
};

/**
 * <p>
 * [概 要] 非同期通信を行う。
 * </p>
 * <p>
 * [詳 細] 以下の処理を行う。<br />
 * <ol>
 * <li>電文をAjax通信で送信する。</li>
 * <li>応答電文を受信する。</li>
 * <li>通信エラーが発生した場合はエラーの処理を行う。</li>
 * </ol>
 * </p>
 * <p>
 * [備 考] settingのasyncにtrueを設定することで、非同期通信を設定する。
 * </p>
 * 
 * @public
 * @param {Object}
 *            settings Ajax通信時にJQueryのajax関数に渡す設定値<br />
 *            <table border = 1> <tbody>
 *            <tr>
 *            <th>Keyの型</th>
 *            <th>keyの値</th>
 *            <th>valueの型</th>
 *            <th>valueの値</th>
 *            </tr>
 *            <tr>
 *            <td>String</td>
 *            <td>url</td>
 *            <td>String</td>
 *            <td>電文送信先のURL</td>
 *            </tr>
 *            <tr>
 *            <td>String</td>
 *            <td>data</td>
 *            <td>String</td>
 *            <td>送信するデータ(Formのプロパティ名をKeyとしたMap)</td>
 *            </tr>
 *            <tr>
 *            <td>String</td>
 *            <td>success</td>
 *            <td>Function</td>
 *            <td>Ajax通信成功時実行関数</td>
 *            </tr>
 *            <tr>
 *            <td>String</td>
 *            <td>error</td>
 *            <td>Function</td>
 *            <td>Ajax通信失敗時実行関数（設定しない場合はデフォルト関数を呼び出す）</td>
 *            </tr>
 *            </tbody> </table>
 *            通信エラー等発生時にシステムエラー画面表示ではなく、正常性アイコンの入れ替えを行う場合は画面IDを渡す。
 */
wgp.AjaxHandler.prototype.requestServerAsync = function(settings) {
	settings["async"] = true;
	this.postConnection(settings);
};

/**
 * <p>
 * [概 要] 同期通信を行う。
 * </p>
 * <p>
 * [詳 細] 以下の処理を行う。<br />
 * <ol>
 * <li>電文を同期通信で送信する。</li>
 * <li>応答電文を受信する。</li>
 * <li>通信エラーが発生した場合はエラーの処理を行う。</li>
 * </ol>
 * </p>
 * <p>
 * [備 考] settingのasyncにfalseを設定することで、同期通信を設定する。
 * </p>
 * 
 * @public
 * @param {Object}
 *            settings 同期通信時にJQueryのajax関数に渡す設定値<br />
 *            <table border = 1> <tbody>
 *            <tr>
 *            <th>Keyの型</th>
 *            <th>keyの値</th>
 *            <th>valueの型</th>
 *            <th>valueの値</th>
 *            </tr>
 *            <tr>
 *            <td>String</td>
 *            <td>url</td>
 *            <td>String</td>
 *            <td>電文送信先のURL</td>
 *            </tr>
 *            <tr>
 *            <td>String</td>
 *            <td>data</td>
 *            <td>String</td>
 *            <td>送信するデータ(Formのプロパティ名をKeyとしたMap)</td>
 *            </tr>
 *            <tr>
 *            <td>String</td>
 *            <td>success</td>
 *            <td>Function</td>
 *            <td>Ajax通信成功時実行関数（設定しない場合は戻り値としてJSONオブジェクトが返る）</td>
 *            </tr>
 *            <tr>
 *            <td>String</td>
 *            <td>error</td>
 *            <td>Function</td>
 *            <td>Ajax通信失敗時実行関数（設定しない場合はデフォルト関数を呼び出す）</td>
 *            </tr>
 *            </tbody> </table>
 */
wgp.AjaxHandler.prototype.requestServerSync = function(settings) {
	settings["async"] = false;
	var result = this.postConnection(settings);
	return result.responseText;
};
