/*******************************************************************************
 * WGP 1.0B - Web Graphical Platform (https://sourceforge.net/projects/wgp/)
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
wgp.ByteParserConstants = function() {
};
wgp.ByteParserConstants.BYTE_SIZE = 8;
wgp.ByteParserConstants.CHARACTER_SIZE = 16;
wgp.ByteParserConstants.SHORT_SIZE = 16;
wgp.ByteParserConstants.INTEGER_SIZE = 32;
wgp.ByteParserConstants.LONG_SIZE = 64;
wgp.ByteParserConstants.FLOAT_SIZE = 32;
wgp.ByteParserConstants.DOUBLE_SIZE = 64;

wgp.ByteParserConstants.CHARACTER_BYTE_SIZE = wgp.ByteParserConstants.CHARACTER_SIZE
		/ wgp.ByteParserConstants.BYTE_SIZE;

wgp.ByteParserConstants.SHORT_BYTE_SIZE = wgp.ByteParserConstants.SHORT_SIZE
		/ wgp.ByteParserConstants.BYTE_SIZE;

wgp.ByteParserConstants.INTEGER_BYTE_SIZE = wgp.ByteParserConstants.INTEGER_SIZE
		/ wgp.ByteParserConstants.BYTE_SIZE;

wgp.ByteParserConstants.LONG_BYTE_SIZE = wgp.ByteParserConstants.LONG_SIZE
		/ wgp.ByteParserConstants.BYTE_SIZE;

wgp.ByteParserConstants.FLOAT_BYTE_SIZE = wgp.ByteParserConstants.FLOAT_SIZE
		/ wgp.ByteParserConstants.BYTE_SIZE;

wgp.ByteParserConstants.DOUBLE_BYTE_SIZE = wgp.ByteParserConstants.DOUBLE_SIZE
		/ wgp.ByteParserConstants.BYTE_SIZE;

wgp.ByteParserConstants.CHARACTER_TYPE_BYTE = 0x00;
wgp.ByteParserConstants.SHORT_TYPE_BYTE = 0x01;
wgp.ByteParserConstants.INTEGER_TYPE_BYTE = 0x02;
wgp.ByteParserConstants.LONG_TYPE_BYTE = 0x03;
wgp.ByteParserConstants.FLOAT_TYPE_BYTE = 0x04;
wgp.ByteParserConstants.DOUBLE_TYPE_BYTE = 0x05;
wgp.ByteParserConstants.STRING_TYPE_BYTE = 0x06;
wgp.ByteParserConstants.OBJECT_TYPE_BYTE = 0x07;

wgp.ByteParserConstants.DATA_TYPE_NAME_CHARACTER = "Character";
wgp.ByteParserConstants.DATA_TYPE_NAME_SHORT = "Short";
wgp.ByteParserConstants.DATA_TYPE_NAME_INTEGER = "Integer";
wgp.ByteParserConstants.DATA_TYPE_NAME_LONG = "Long";
wgp.ByteParserConstants.DATA_TYPE_NAME_FLOAT = "Float";
wgp.ByteParserConstants.DATA_TYPE_NAME_DOUBLE = "Double";
wgp.ByteParserConstants.DATA_TYPE_NAME_STRING = "String";
wgp.ByteParserConstants.DATA_TYPE_NAME_OBJECT = "Object";

wgp.ByteParser = function(arrayBuffer) {
	this.buffer_ = arrayBuffer;
	this.dataView_ = new DataView(arrayBuffer);
	this.offset_ = 0;
};

wgp.ByteParser.prototype.getReadResult = function() {

	var returnResult = [];

	for (; this.offset_ < this.buffer_.byteLength;) {

		// Get Data type
		var typeByte = this.dataView_.getInt8(this.offset_++);
		var dataCount = this.bitsToInteger();

		var loop = 0;
		var result = null;
		if (wgp.ByteParserConstants.CHARACTER_TYPE_BYTE == typeByte) {
			for (loop = 0; loop < dataCount; loop++) {
				result = this.bitsToCharacter();
				returnResult.push(result);
			}

		} else if (wgp.ByteParserConstants.SHORT_TYPE_BYTE == typeByte) {
			for (loop = 0; loop < dataCount; loop++) {
				result = this.bitsToShort();
				returnResult.push(result);
			}

		} else if (wgp.ByteParserConstants.INTEGER_TYPE_BYTE == typeByte) {
			for (loop = 0; loop < dataCount; loop++) {
				result = this.bitsToInteger();
				returnResult.push(result);
			}

		} else if (wgp.ByteParserConstants.LONG_TYPE_BYTE == typeByte) {
			for (loop = 0; loop < dataCount; loop++) {
				result = this.bitsToLong();
				returnResult.push(result);
			}

		} else if (wgp.ByteParserConstants.FLOAT_TYPE_BYTE == typeByte) {
			for (loop = 0; loop < dataCount; loop++) {
				result = this.bitsToFloat();
				returnResult.push(result);
			}

		} else if (wgp.ByteParserConstants.DOUBLE_TYPE_BYTE == typeByte) {
			for (loop = 0; loop < dataCount; loop++) {
				result = this.bitsToDouble();
				returnResult.push(result);
			}

		} else if (wgp.ByteParserConstants.STRING_TYPE_BYTE == typeByte) {
			result = this.bitsToString(dataCount);
			returnResult.push(result);

		} else if (wgp.ByteParserConstants.OBJECT_TYPE_BYTE == typeByte) {
			result = this.bitsToObject(dataCount);
			returnResult.push(result);
		}
	}

	return returnResult;
};

wgp.ByteParser.prototype.getDataTypeName = function(typeByte) {
	if (wgp.ByteParserConstants.CHARACTER_TYPE_BYTE == typeByte) {
		return wgp.ByteParserConstants.DATA_TYPE_NAME_CHARACTER;

	} else if (wgp.ByteParserConstants.SHORT_TYPE_BYTE == typeByte) {
		return wgp.ByteParserConstants.DATA_TYPE_NAME_SHORT;

	} else if (wgp.ByteParserConstants.INTEGER_TYPE_BYTE == typeByte) {
		return wgp.ByteParserConstants.DATA_TYPE_NAME_INTEGER;

	} else if (wgp.ByteParserConstants.LONG_TYPE_BYTE == typeByte) {
		return wgp.ByteParserConstants.DATA_TYPE_NAME_LONG;

	} else if (wgp.ByteParserConstants.FLOAT_TYPE_BYTE == typeByte) {
		return wgp.ByteParserConstants.DATA_TYPE_NAME_FLOAT;

	} else if (wgp.ByteParserConstants.DOUBLE_TYPE_BYTE == typeByte) {
		return wgp.ByteParserConstants.DATA_TYPE_NAME_DOUBLE;

	} else if (wgp.ByteParserConstants.STRING_TYPE_BYTE == typeByte) {
		return wgp.ByteParserConstants.DATA_TYPE_NAME_STRING;

	} else if (wgp.ByteParserConstants.OBJECT_TYPE_BYTE == typeByte) {
		return wgp.ByteParserConstants.DATA_TYPE_NAME_OBJECT;

	} else {
		return undefined;
	}
};

wgp.ByteParser.prototype.bitsToCharacter = function() {
	var value = this.dataView_.getInt16(this.offset_);
	this.offset_ += wgp.ByteParserConstants.CHARACTER_BYTE_SIZE;
	return String.fromCharCode(value);
};

wgp.ByteParser.prototype.bitsToShort = function() {
	var result = this.dataView_.getInt8(this.offset_);
	this.offset_ += wgp.ByteParserConstants.SHORT_BYTE_SIZE;
	return result;
};

wgp.ByteParser.prototype.bitsToInteger = function() {
	var result = this.dataView_.getInt32(this.offset_);
	this.offset_ += wgp.ByteParserConstants.INTEGER_BYTE_SIZE;
	return result;
};

wgp.ByteParser.prototype.bitsToLong = function() {
	var result = this.dataView_.getFloat64(this.offset_);
	this.offset_ += wgp.ByteParserConstants.DOUBLE_BYTE_SIZE;

	if (result > 0) {
		return Math.floor(result);
	} else {
		return Math.ceil(result);
	}
};

wgp.ByteParser.prototype.bitsToFloat = function() {
	var result = this.dataView_.getFloat32(this.offset_);
	this.offset_ += wgp.ByteParserConstants.FLOAT_BYTE_SIZE;
	return result;
};

wgp.ByteParser.prototype.bitsToDouble = function() {
	var result = this.dataView_.getFloat64(this.offset_);
	this.offset_ += wgp.ByteParserConstants.DOUBLE_BYTE_SIZE;
	return result;
};

wgp.ByteParser.prototype.bitsToObject = function(dataCnt) {
	var result = this.bitsToString(dataCnt);
	return $.parseJSON(result);
};

wgp.ByteParser.prototype.bitsToString = function(dataCnt) {

	var arr = [];
	var uInt8Array = Uint8Array(this.buffer_);
	var index = this.offset_;
	var length = this.offset_ + dataCnt;

	for (; index < length; index++) {
		arr.push(uInt8Array[index]);
	}

	var result = this.parseString(arr);

	this.offset_ += dataCnt;
	return result;
};

wgp.ByteParser.prototype.parseString = function(array) {

	var result = "";
	var index;
	var char;
	while (array.length > 0) {
		index = array.shift();

		if (index <= 0x7f) {
			result += String.fromCharCode(index);
		} else if (index <= 0xdf) {
			char = ((index & 0x1f) << 6);
			char += array.shift() & 0x3f;
			result += String.fromCharCode(char);
		} else if (index <= 0xe0) {
			char = ((array.shift() & 0x1f) << 6) | 0x0800;
			char += array.shift() & 0x3f;
			result += String.fromCharCode(char);
		} else {
			char = ((index & 0x0f) << 12);
			char += (array.shift() & 0x3f) << 6;
			char += array.shift() & 0x3f;
			result += String.fromCharCode(char);
		}
	}

	return result;
};