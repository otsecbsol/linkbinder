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

package net.java.amateras.xlsbeans.xssfconverter.impl.xssf;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import net.java.amateras.xlsbeans.xssfconverter.WSheet;
import net.java.amateras.xlsbeans.xssfconverter.WWorkbook;

/**
 * Workbook wrapper for HSSF/XSSF.
 *
 * @author opentone
 *
 */
public class XssfWWorkbookImpl implements WWorkbook {

	private Workbook workbook = null;

	public XssfWWorkbookImpl(Workbook workbook) {
		this.workbook = workbook;
	}

	public WSheet getSheet(int i) {
		Sheet sheet = workbook.getSheetAt(i);
		return new XssfWSheetImpl(sheet);
	}

	public WSheet getSheet(String name) {
		Sheet sheet = workbook.getSheet(name);
		return new XssfWSheetImpl(sheet);
	}

	public WSheet[] getSheets() {
		int sheetNum = workbook.getNumberOfSheets();
		WSheet[] retSheets = new WSheet[sheetNum];
		for (int i = 0; i < sheetNum; i++) {
			retSheets[i] = new XssfWSheetImpl(workbook.getSheetAt(i));
		}
		return retSheets;
	}

}
