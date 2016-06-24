/*
 * Copyright 2016 OPEN TONE Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.java.amateras.xlsbeans.xssfconverter;

import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jxl.Workbook;
import jxl.WorkbookSettings;
import net.java.amateras.xlsbeans.xssfconverter.impl.jxl.JxlWWorkbookImpl;
import net.java.amateras.xlsbeans.xssfconverter.impl.xssf.XssfWWorkbookImpl;

/**
 * Loading Excel workbook wapper-object, according to const-string:
 * TYPE_JXL(Java Excel API), TYPE_HSSF(HSSF Excel 2003), TYPE_XSSF(XSSF Excel 2007).
 *
 * @author opentone
 *
 */
public class WorkbookFinder {

	/**
	 * Loading xlsbeans by Java Excel API (xls)
	 */
	public static final String TYPE_JXL = "jexcel api";

	/**
	 * Loading xlsbeans by HSSF (xls)
	 */
	public static final String TYPE_HSSF = "hssf";

	/**
	 * Loading xlsbeans by XSSF (xlsx)
	 */
	public static final String TYPE_XSSF = "xssf";

	/**
	 * return WWorkbook
	 * (default implementation(Java Excel API) Wrapper).
	 *
	 * @param is Excel InputStream
	 * @return WWorkbook (JExcel API wrapper)
	 */
	public static WWorkbook find(InputStream is) throws Exception {
		return find(is, TYPE_JXL);
	}

	/**
	 * return workbook wrapper.
	 *
	 * @param is Excel InputStream
	 * @param type TYPE_JXL:JExcel API Wrapper / TYPE_HSSF:hssf Wrapper / TYPE_XSSF:xssf Wrapper
	 * @return Workbook
	 */
	public static WWorkbook find(InputStream is, String type) throws Exception {
		WWorkbook retbook = null;
		if (TYPE_JXL.equals(type)) {
			WorkbookSettings settings = new WorkbookSettings();
			settings.setGCDisabled(true);
			settings.setEncoding("ISO8859_1");
			Workbook w = Workbook.getWorkbook(is, settings);
			retbook = new JxlWWorkbookImpl(w);
		} else if (TYPE_HSSF.equals(type)) {
			org.apache.poi.ss.usermodel.Workbook ssWorkbook = new HSSFWorkbook(is);
			retbook = new XssfWWorkbookImpl(ssWorkbook);
		} else if (TYPE_XSSF.equals(type)) {
			org.apache.poi.ss.usermodel.Workbook ssWorkbook = new XSSFWorkbook(OPCPackage.open(is));
			retbook = new XssfWWorkbookImpl(ssWorkbook);
		} else {
			throw new IllegalArgumentException("type must be defined by TYPE_JXL or TYPE_HSSF or TYPE_XSSF");
		}
		return retbook;
	}
}
