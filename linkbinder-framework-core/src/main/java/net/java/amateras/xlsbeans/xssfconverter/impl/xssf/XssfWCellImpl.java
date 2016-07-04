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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import net.java.amateras.xlsbeans.xssfconverter.WCell;
import net.java.amateras.xlsbeans.xssfconverter.WCellFormat;

/**
 * Cell wrapper for HSSF/XSSF.
 *
 * @author opentone
 *
 */
public class XssfWCellImpl implements WCell {

	private Cell cell = null;

	public XssfWCellImpl(Cell cell) {
		this.cell = cell;
	}

	public WCellFormat getCellFormat() {
		return new XssfWCellFormatImpl(cell.getCellStyle());
	}

	public int getColumn() {
		return cell.getColumnIndex();
	}

	public String getContents() {
		String contents = null;
		// IllegalStateException occurs , if illegal type defined...
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			contents = "";
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			contents = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_ERROR:
			contents = String.valueOf(cell.getErrorCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA:
			contents = String.valueOf(cell.getNumericCellValue());
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				Date date = cell.getDateCellValue();
				// FIXME format string...in JExcel API standard.
				SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd");
				contents = formatter.format(date);
			} else {
				contents = String.valueOf(convertDoubleValue(cell
						.getNumericCellValue()));
			}
			break;
		case Cell.CELL_TYPE_STRING:
			contents = String.valueOf(cell.getStringCellValue());
			break;
		default:
			contents = "";
			break;
		}
		return contents;
	}

	public int getRow() {
		return cell.getRowIndex();
	}

	private String convertDoubleValue(double d) {
		BigDecimal bd = new BigDecimal(d);
		String convertValue = null;
		try {
			convertValue = String.valueOf(bd.intValueExact());
		} catch (ArithmeticException e) {
			convertValue = String.valueOf(d);
		}
		return convertValue;
	}

}
