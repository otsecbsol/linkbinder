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
package jp.co.opentone.bsol.framework.core.generator.excel.strategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.generator.GeneratorFailedException;
import jp.co.opentone.bsol.framework.core.generator.excel.WorkbookGeneratorContext;
import jp.co.opentone.bsol.framework.core.util.PropertyGetUtil;

/**
 * ExcelワークブックにBeanリストのデータを出力するクラス. このクラスでサポートするのは、1ブック1シートのデータのみ.
 * 罫線を出力することは可能だが、線種や太さなどの指定はできない.
 * @author opentone
 */
public class PoiWorkbookGeneratorStrategy implements WorkbookGeneratorStrategy {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -286963670434760341L;
    /** logger. */
    private static final Logger LOG = LoggerFactory.getLogger(PoiWorkbookGeneratorStrategy.class);

    /**
     * 空のインスタンスを生成する.
     */
    public PoiWorkbookGeneratorStrategy() {
    }

    /**
     * 生成可能な状態であるかチェックする.
     */
    private void checkState(WorkbookGeneratorContext context) {
        if (StringUtils.isEmpty(context.sheetName)) {
            throw new GeneratorFailedException("sheetName is null.");
        }
        if (context.data == null) {
            throw new GeneratorFailedException("data is null.");
        }
        if (context.outputFieldNames == null) {
            throw new GeneratorFailedException("outputFieldNames is null.");
        }

        List<String> headers = context.headerNames;
        List<String> fields = context.outputFieldNames;
        if (headers != null && headers.size() > 0 && fields != null) {
            if (headers.size() != fields.size()) {
                throw new GeneratorFailedException("headerNames or outpputFieldNames incorrect.");
            }
        }
    }

    private void setBorder(WorkbookGeneratorContext context, HSSFCellStyle style) {
        // 罫線
        if (context.outputLine) {
            style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        }
    }

    /**
     * MS Excelワークブックを生成する.
     * <p>
     * このメソッドでサポートするのは、1ブック1シートのデータのみ. 罫線を出力することは可能だが、線種や太さなどの指定はできない.
     * 生成したデータのファイル出力は行わず、戻り値としてデータを返す.
     * </p>
     * @param context Excelブック生成に関する情報
     * @return MS Excelワークブックデータ
     */
    public byte[] generate(WorkbookGeneratorContext context) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("generate start");
        }
        checkState(context);

        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet sheet = workBook.createSheet(context.sheetName);
        HSSFCellStyle style = workBook.createCellStyle();
        setBorder(context, style);

        // ヘッダー作成
        int count = 0;
        if (createHeader(context, sheet, style)) {
            count++;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("  createRow start");
        }
        // データ作成
        createRow(context, workBook, sheet, count, style);
        if (LOG.isDebugEnabled()) {
            LOG.debug("  createRow end");
        }

        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("  toByte start");
            }
            // バイナリデータに変換して返す
            return toByte(workBook);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("  toByte end");
            }
        }
    }

    private byte[] toByte(HSSFWorkbook book) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            book.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new GeneratorFailedException(e);
        }
    }

    private void createRow(WorkbookGeneratorContext context,
                          HSSFWorkbook workBook,
                          HSSFSheet sheet,
                          int count,
                          HSSFCellStyle style) {
        try {
            for (int i = 0; i < context.data.size(); i++) {
                Object object = context.data.get(i);
                HSSFRow rowData = sheet.createRow(count);
                createCell(context, workBook, sheet, style, object, rowData);
                count++;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("    row[{}] created.", count);
                }
            }
        } catch (IllegalAccessException e) {
            throw new GeneratorFailedException(e);
        } catch (InvocationTargetException e) {
            throw new GeneratorFailedException(e);
        } catch (NoSuchMethodException e) {
            throw new GeneratorFailedException(e);
        }
    }

    private boolean isNumber(Object value) {
        if (value == null) {
            return false;
        }
        return Number.class.isAssignableFrom(value.getClass());
    }

    private HSSFCellStyle createCell(
                            WorkbookGeneratorContext context,
                            HSSFWorkbook workBook,
                            HSSFSheet sheet,
                            HSSFCellStyle style,
                            Object object,
                            HSSFRow rowData) throws IllegalAccessException,
        InvocationTargetException, NoSuchMethodException {
        // 列作成
        HSSFCellStyle dateStyle = null;
        for (int i = 0; i < context.outputFieldNames.size(); i++) {
            HSSFCell cell = rowData.createCell(i);
            sheet.autoSizeColumn((short) i);
            Object value = PropertyGetUtil.getValue(object, context.outputFieldNames.get(i));
            if (value instanceof Date) {
                if (dateStyle == null) {
                    dateStyle = initializeDateCellStyle(workBook, style);
                }
                setCellValue(cell, value);
                cell.setCellStyle(dateStyle);
            } else if (isNumber(value)) {
                if (value != null) {
                    cell.setCellValue(Double.valueOf(String.valueOf(value)));
                }
                setCellValue(cell, value);
                setCellStyle(cell, style);
            } else {
                if (value != null) {
                    cell.setCellValue(new HSSFRichTextString(String.valueOf(value)));
                }
                setCellValue(cell, value);
                setCellStyle(cell, style);
            }
        }
        return dateStyle;
    }

    private HSSFCellStyle initializeDateCellStyle(HSSFWorkbook workbook, HSSFCellStyle baseStyle) {
        HSSFCellStyle dateStyle = workbook.createCellStyle();
        dateStyle.cloneStyleFrom(baseStyle);
        dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy"));
        return dateStyle;
    }

    private void setCellStyle(HSSFCell cell, HSSFCellStyle style) {
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private void setCellValue(HSSFCell cell, Object value) {
        if (value == null) {
            return;
        }

        if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (NumberUtils.isNumber(String.valueOf(value))) {
            cell.setCellValue(Double.valueOf(String.valueOf(value)));
        } else {
            cell.setCellValue(new HSSFRichTextString(String.valueOf(value)));
        }
    }

    private boolean createHeader(WorkbookGeneratorContext context,
                                HSSFSheet sheet,
                                HSSFCellStyle style) {
        if (context.headerNames == null || context.headerNames.isEmpty()) {
            return false;
        }

        int headerRowIndex = 0;
        HSSFRow rowHeader = sheet.createRow(headerRowIndex);
        for (int i = 0; i < context.headerNames.size(); i++) {
            HSSFCell cell = rowHeader.createCell(i);
            sheet.autoSizeColumn((short) i);

            cell.setCellStyle(style);
            cell.setCellValue(new HSSFRichTextString(context.headerNames.get(i)));
        }
        return true;
    }
}
