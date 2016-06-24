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

import net.java.amateras.xlsbeans.xssfconverter.WBorderLineStyle;

/**
 * border line resolver.
 * (Java Excel API - HSSF/XSSF)
 *
 * @author opentone
 *
 */
public class XssfBorderLineStyleResolver {

	public static WBorderLineStyle resolve(short cellStyle) {
		WBorderLineStyle style = null;
		switch(cellStyle) {
			case CellStyle.BORDER_NONE: style = WBorderLineStyle.NONE; break;
			case CellStyle.BORDER_THIN: style = WBorderLineStyle.THIN; break;
			case CellStyle.BORDER_MEDIUM: style = WBorderLineStyle.MEDIUM; break;
			case CellStyle.BORDER_DASHED: style = WBorderLineStyle.DASHED; break;
			case CellStyle.BORDER_DOTTED: style = WBorderLineStyle.DOTTED; break;
			case CellStyle.BORDER_THICK: style = WBorderLineStyle.THICK; break;
			case CellStyle.BORDER_DOUBLE: style = WBorderLineStyle.DOUBLE; break;
			case CellStyle.BORDER_HAIR: style = WBorderLineStyle.HAIR; break;
			case CellStyle.BORDER_MEDIUM_DASHED: style = WBorderLineStyle.MEDIUM_DASHED; break;
			case CellStyle.BORDER_DASH_DOT: style = WBorderLineStyle.DASH_DOT; break;
			case CellStyle.BORDER_MEDIUM_DASH_DOT: style = WBorderLineStyle.MEDIUM_DASH_DOT; break;
			case CellStyle.BORDER_MEDIUM_DASH_DOT_DOT: style = WBorderLineStyle.MEDIUM_DASH_DOT_DOT; break;
			case CellStyle.BORDER_SLANTED_DASH_DOT: style = WBorderLineStyle.SLANTED_DASH_DOT; break;
			default:  style = WBorderLineStyle.NONE; break;
		}
		return style;
	}
}
