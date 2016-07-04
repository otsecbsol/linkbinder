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
import jxl.format.CellFormat;
import net.java.amateras.xlsbeans.xssfconverter.WCell;
import net.java.amateras.xlsbeans.xssfconverter.WCellFormat;

/**
 * Cell wrapper for Java Excel API.
 * @author opentone
 *
 */
public class JxlWCellImpl implements WCell {
	private Cell cell = null;

	public JxlWCellImpl(Cell cell) {
		this.cell = cell;
	}

	public WCellFormat getCellFormat() {
		CellFormat org = cell.getCellFormat();
		return new JxlWCellFormatImpl(org);
	}

	public int getColumn() {
		return cell.getColumn();
	}

	public String getContents() {
		return cell.getContents();
	}

	public int getRow() {
		return cell.getRow();
	}

}
