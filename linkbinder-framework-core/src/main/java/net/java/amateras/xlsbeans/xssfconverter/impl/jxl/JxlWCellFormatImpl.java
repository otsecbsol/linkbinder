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

import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import net.java.amateras.xlsbeans.xssfconverter.WBorder;
import net.java.amateras.xlsbeans.xssfconverter.WBorderLineStyle;
import net.java.amateras.xlsbeans.xssfconverter.WCellFormat;

/**
 * CellFormat wrapper for Java Excel API.
 *
 * @author opentone
 *
 */
public class JxlWCellFormatImpl implements WCellFormat {

	private CellFormat format = null;

	public JxlWCellFormatImpl(CellFormat format) {
		this.format = format;
	}

	public WBorderLineStyle getBorder(WBorder border) {
		BorderLineStyle borderLineStyle = null;
		if (WBorder.ALL.equals(border)) {
			borderLineStyle = format.getBorder(Border.ALL);
		} else if (WBorder.BOTTOM.equals(border)) {
			borderLineStyle = format.getBorder(Border.BOTTOM);
		} else if (WBorder.LEFT.equals(border)) {
			borderLineStyle = format.getBorder(Border.LEFT);
		} else if (WBorder.NONE.equals(border)) {
			borderLineStyle = format.getBorder(Border.NONE);
		} else if (WBorder.RIGHT.equals(border)) {
			borderLineStyle = format.getBorder(Border.RIGHT);
		} else if (WBorder.TOP.equals(border)) {
			borderLineStyle = format.getBorder(Border.TOP);
		}
		if (borderLineStyle == null) {
			throw new IllegalArgumentException("Not support border style.");
		}
		return new WBorderLineStyle(borderLineStyle.getValue(), borderLineStyle.getDescription());
	}

}
