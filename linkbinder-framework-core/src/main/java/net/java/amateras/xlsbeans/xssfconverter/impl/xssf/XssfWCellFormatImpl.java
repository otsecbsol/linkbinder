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

import org.apache.poi.ss.usermodel.CellStyle;

import net.java.amateras.xlsbeans.xssfconverter.WBorder;
import net.java.amateras.xlsbeans.xssfconverter.WBorderLineStyle;
import net.java.amateras.xlsbeans.xssfconverter.WCellFormat;

/**
 * CellFormat wrapper for HSSF/XSSF.
 *
 * @author opentone
 *
 */
public class XssfWCellFormatImpl implements WCellFormat {
	private CellStyle cellStyle = null;

	public XssfWCellFormatImpl(CellStyle cellStyle) {
		this.cellStyle = cellStyle;
	}

	public WBorderLineStyle getBorder(WBorder border) {
		if (cellStyle == null) {
			return WBorderLineStyle.NONE;
		}

		WBorderLineStyle style = null;
		if (WBorder.BOTTOM.equals(border)) {
			style = XssfBorderLineStyleResolver.resolve(cellStyle.getBorderBottom());
		} else if (WBorder.LEFT.equals(border)) {
			style = XssfBorderLineStyleResolver.resolve(cellStyle.getBorderLeft());
		} else if (WBorder.RIGHT.equals(border)) {
			style = XssfBorderLineStyleResolver.resolve(cellStyle.getBorderRight());
		} else if (WBorder.TOP.equals(border)) {
			style = XssfBorderLineStyleResolver.resolve(cellStyle.getBorderTop());
		} else {
			// FIXME Correct strictly? XLSBeans don't use this style, except TOP, LEFT.
			style = WBorderLineStyle.NONE;
		}
		return style;
	}

}
