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

import jxl.Cell;
import jxl.Sheet;
import net.java.amateras.xlsbeans.xssfconverter.WCell;
import net.java.amateras.xlsbeans.xssfconverter.WSheet;

/**
 * Sheet wrapper for Java Excel API.
 *
 * @author opentone
 *
 */
public class JxlWSheetImpl implements WSheet {

	private Sheet sheet = null;

	public JxlWSheetImpl(Sheet sheet) {
		this.sheet = sheet;
	}

	public WCell getCell(int column, int row) {
		return new JxlWCellImpl(sheet.getCell(column, row));
	}

	public WCell[] getColumn(int i) {
		Cell[] cells = sheet.getColumn(i);
		if (cells == null) {
			return null;
		}
		WCell[] retCells = new WCell[cells.length];
		for (int j = 0 ; j < cells.length; j++) {
			retCells[j] = new JxlWCellImpl(cells[j]);
		}
		return retCells;
	}

	public int getColumns() {
		return sheet.getColumns();
	}

	public String getName() {
		return sheet.getName();
	}

	public int getRows() {
		return sheet.getRows();
	}
}
