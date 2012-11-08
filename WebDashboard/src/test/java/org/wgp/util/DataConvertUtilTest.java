package org.wgp.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class DataConvertUtilTest {

	
	@Test
	public void testGetFieldNameFromMethodName(){
		String mathodName = "setNamePosition";
		String fieldName = DataConvertUtil.getFieldName(mathodName);
		assertEquals("namePosition", fieldName);
	}
	
}
