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

package net.java.amateras.xlsbeans.xssfconverter.impl.jxl;

import jxl.Sheet;
import jxl.Workbook;
import net.java.amateras.xlsbeans.xssfconverter.WSheet;
import net.java.amateras.xlsbeans.xssfconverter.WWorkbook;

/**
 * Workbook wrapper for Java Excel API.
 *
 * @author opentone
 *
 */
public class JxlWWorkbookImpl implements WWorkbook {

	private Workbook workbook = null;

	public JxlWWorkbookImpl(Workbook workbook) {
		this.workbook = workbook;
	}

	public WSheet getSheet(int i) {
		return new JxlWSheetImpl(workbook.getSheet(i));
	}

	public WSheet getSheet(String name) {
		return new JxlWSheetImpl(workbook.getSheet(name));
	}

	public WSheet[] getSheets() {
		Sheet[] sheets = workbook.getSheets();
		if (sheets == null) {
			return null;
		}
		WSheet[] retSheets = new WSheet[sheets.length];
		for (int i = 0; i < sheets.length; i++) {
			retSheets[i] = new JxlWSheetImpl(sheets[i]);
		}
		return retSheets;
	}
}
