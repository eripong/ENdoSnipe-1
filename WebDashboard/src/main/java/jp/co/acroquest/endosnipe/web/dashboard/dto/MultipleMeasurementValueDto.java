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
package jp.co.acroquest.endosnipe.web.dashboard.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * 複数系列のデータを扱うことができるDto
 * 
 * @author nakagawa
 *
 */
public class MultipleMeasurementValueDto {

	/** 検索文字列 */
	private String searchCondition;
	
    /** 計測時刻。 */
    private long   measurementTime;	
	
    /** 複数系列の測定結果 */
    private Map<String, String> measurementValue = new HashMap<String, String>();

	public String getSearchCondition() {
		return searchCondition;
	}

	public void setSearchCondition(String searchCondition) {
		this.searchCondition = searchCondition;
	}

	public long getMeasurementTime() {
		return measurementTime;
	}

	public void setMeasurementTime(long measurementTime) {
		this.measurementTime = measurementTime;
	}

	public Map<String, String> getMeasurementValue() {
		return measurementValue;
	}

	public void setMeasurementValue(Map<String, String> measurementValue) {
		this.measurementValue = measurementValue;
	}
    
}
