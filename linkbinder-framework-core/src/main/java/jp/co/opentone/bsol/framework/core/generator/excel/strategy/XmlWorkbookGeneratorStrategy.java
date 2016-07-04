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
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

import jp.co.opentone.bsol.framework.core.generator.GeneratorFailedException;
import jp.co.opentone.bsol.framework.core.generator.excel.WorkbookGeneratorContext;
import jp.co.opentone.bsol.framework.core.util.PropertyGetUtil;

/**
 * ExcelワークブックにBeanリストのデータを出力するクラス. このクラスでサポートするのは、1ブック1シートのデータのみ.
 * 罫線を出力することは可能だが、線種や太さなどの指定はできない.
 * @author opentone
 */
public class XmlWorkbookGeneratorStrategy implements WorkbookGeneratorStrategy {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -286963670434760341L;
    /** logger. */
    private static final Logger LOG = LoggerFactory.getLogger(XmlWorkbookGeneratorStrategy.class);

    /**
     * 出力エンコーディング.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * 通常のセルの書式.
     */
    private static final String ID_STYLE_NORMAL = "s1";
    /**
     * 日付セルの書式.
     */
    private static final String ID_STYLE_DATE   = "s2";

    /**
     * 空のインスタンスを生成する.
     */
    public XmlWorkbookGeneratorStrategy() {
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

    /**
     * SpreadsheetMLドキュメントオブジェクトを生成する.
     * @return DOM Documentオブジェクト
     * @throws ParserConfigurationException 生成に失敗
     */
    private Document createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        Document doc = factory.newDocumentBuilder().newDocument();
        Map<String, String> param = new HashMap<String, String>();
        param.put("progid", "Excel.Sheet");
        ProcessingInstruction pi =
            doc.createProcessingInstruction("mso-application", "progid=\"Excel.Sheet\"");
        doc.appendChild(pi);

        return doc;
    }

    /**
     * ワークブック要素を生成する.
     * @param context Excelブック生成に関する情報
     * @param doc DOM Document
     * @return ワークブック
     */
    private Element createWorkbook(WorkbookGeneratorContext context, Document doc) {
        Element e = doc.createElement("Workbook");
        setAttribute(e, "xmlns",      "urn:schemas-microsoft-com:office:spreadsheet");
        setAttribute(e, "xmlns:o",    "urn:schemas-microsoft-com:office:office");
        setAttribute(e, "xmlns:x",    "urn:schemas-microsoft-com:office:excel");
        setAttribute(e, "xmlns:ss",   "urn:schemas-microsoft-com:office:spreadsheet");
        setAttribute(e, "xmlns:html", "http://www.w3.org/TR/REC-html40");

        e.appendChild(createStyles(context, doc));

        return e;
    }

    /**
     * ワークブック内で参照される全てのスタイル要素を生成する.
     * @param context Excelブックに関する情報
     * @param doc DOM Document
     * @return スタイル要素
     */
    private Element createStyles(WorkbookGeneratorContext context, Document doc) {
        Element parent = doc.createElement("Styles");

        parent.appendChild(createDefaultStyle(doc));
        parent.appendChild(createStyle(context, doc));
        parent.appendChild(createDateStyle(context, doc));

        return parent;
    }

    /**
     * ワークブック内のデフォルトスタイル要素を生成する.
     * @param doc DOM Document
     * @return スタイル要素
     */
    private Element createDefaultStyle(Document doc) {
        Element e = doc.createElement("Style");
        setAttribute(e, "ss:ID", "Default");
        setAttribute(e, "ss:Name", "Normal");

        Element align = doc.createElement("Alignment");
        setAttribute(align, "ss:Vertial", "Bottom");
        e.appendChild(align);

        Element borders = doc.createElement("Borders");
        e.appendChild(borders);

        Element font = doc.createElement("Font");
        setAttribute(font, "x:Family", "Swiss");
        e.appendChild(font);

        Element interior = doc.createElement("Interior");
        e.appendChild(interior);

        Element numFormat = doc.createElement("NumberFormat");
        e.appendChild(numFormat);

        Element protection = doc.createElement("Protection");
        e.appendChild(protection);

        return e;
    }

    /**
     * ワークブック内で参照されるスタイルを生成する.
     * @param context Excelブック生成に関する情報
     * @param doc DOM Document
     * @return スタイル要素
     */
    private Element createStyle(WorkbookGeneratorContext context, Document doc) {
        Element e = doc.createElement("Style");
        setAttribute(e, "ss:ID", ID_STYLE_NORMAL);

        Element borders = doc.createElement("Borders");
        if (context.outputLine) {
            borders.appendChild(createBorder(doc, "Bottom"));
            borders.appendChild(createBorder(doc, "Left"));
            borders.appendChild(createBorder(doc, "Right"));
            borders.appendChild(createBorder(doc, "Top"));
        }

        e.appendChild(borders);

        return e;
    }

    /**
     * 日付を表すスタイル要素を生成する.
     * @param context Excelブック生成に関する情報
     * @param doc DOM Document
     * @return スタイル要素
     */
    private Element createDateStyle(WorkbookGeneratorContext context, Document doc) {
        Element e = createStyle(context, doc);
        setAttribute(e, "ss:ID", ID_STYLE_DATE);

        Element format = doc.createElement("NumberFormat");
        setAttribute(format, "ss:Format", "[ENG][$-409]d\\-mmm\\-yy;@");

        e.appendChild(format);

        return e;
    }

    /**
     * 罫線を表す要素を生成する.
     * @param doc DOM Document
     * @param position Top/Left/Bottom/Right
     * @return 罫線
     */
    private Element createBorder(Document doc, String position) {
        Element e = doc.createElement("Border");
        setAttribute(e, "ss:Position", position);
        setAttribute(e, "ss:LineStyle", "Continuous");
        setAttribute(e, "ss:Weight", "1");
        setAttribute(e, "ss:Color", "#000000");

        return e;
    }

    /**
     * ワークシートを生成する.
     * @param context Excelブック生成に関する情報
     * @param doc DOM Document
     * @return ワークブック
     */
    private Element createWorksheet(WorkbookGeneratorContext context, Document doc) {
        Element e = doc.createElement("Worksheet");
        e.setAttribute("ss:Name", context.sheetName);

        return e;
    }

    /**
     * 表を生成する.
     * @param context Excelブック生成に関する情報
     * @param doc DOM Document
     * @return 表
     */
    private Element createTable(WorkbookGeneratorContext context, Document doc) {
        Element e = doc.createElement("Table");

        setAttribute(e, "ss:ExpandedColumnCount", context.outputFieldNames.size());

        int header = context.headerNames != null ? 1 : 0;
        setAttribute(e, "ss:ExpandedRowCount", header + context.data.size()); // header + data
        setAttribute(e, "x:FullColumns", 1);
        setAttribute(e, "x:FullRows", 1);

        for (Element col : createColumns(context, doc)) {
            e.appendChild(col);
        }
        return e;
    }

    /**
     * 表内の列を表す要素を生成する.
     * @param context Excelブック生成に関する情報
     * @param doc DOM Document
     * @return 列
     */
    private List<Element> createColumns(WorkbookGeneratorContext context, Document doc) {
        List<Element> result = new ArrayList<Element>();
        for (int i = 0; i < context.outputFieldNames.size(); i++) {
            Element e = doc.createElement("Column");
            setAttribute(e, "ss:AutoFitWidth", 0);

            result.add(e);
        }
        return result;
    }

    /**
     * 表のヘッダを表す要素を生成する.
     * @param context Excelブック生成に関する情報
     * @param doc DOM Document
     * @return ヘッダ
     */
    private Element createHeader(WorkbookGeneratorContext context, Document doc) {
        if (context.headerNames == null) {
            return null;
        }

        Element e = doc.createElement("Row");
        for (String h : context.headerNames) {
            e.appendChild(createCell(doc, h));
        }
        return e;
    }

    /**
     * 表内の一行を表す要素を生成する.
     * @param context Excelブック生成に関する情報
     * @param doc DOM Document
     * @return 一行
     */
    private List<Element> createRows(WorkbookGeneratorContext context, Document doc) {
        List<Element> result = new ArrayList<Element>();
        try {
            for (Object row : context.data) {
                Element e = doc.createElement("Row");
                for (String name : context.outputFieldNames) {
                    e.appendChild(createCell(doc, PropertyGetUtil.getNestedProperty(row, name)));
                }
                result.add(e);
            }
            return result;
        } catch (RuntimeException e) {
            throw new GeneratorFailedException(e);
        }
    }

    /**
     * 表内の一セルを表す要素を生成する.
     * @param doc DOM Document
     * @param value セルの値
     * @return セル
     */
    private Element createCell(Document doc, Object value) {
        Element cell = doc.createElement("Cell");
        setAttribute(cell, "ss:StyleID", isDate(value) ? ID_STYLE_DATE : ID_STYLE_NORMAL);

        Element v = doc.createElement("Data");
        setAttribute(v, "ss:Type", detectDataType(value));
        Node text = doc.createTextNode(formatValue(value));
        v.appendChild(text);

        cell.appendChild(v);

        return cell;
    }

    /**
     * 要素に属性を設定する.
     * @param e 要素
     * @param name 属性名
     * @param value 属性値
     */
    private static void setAttribute(Element e, String name, Object value) {
        e.setAttribute(name, String.valueOf(value));
    }

    private boolean isDate(Object value) {
        return value instanceof Date;
    }

    private boolean isNumber(Object value) {
        if (value == null) {
            return false;
        }
        return Number.class.isAssignableFrom(value.getClass());
    }

    private boolean isBigDecimal(Object value) {
        if (value == null) {
            return false;
        }
        return BigDecimal.class.isAssignableFrom(value.getClass());
    }

    private String detectDataType(Object value) {
        if (value instanceof Date) {
            return "DateTime";
        } else if (isNumber(value)) {
            return "Number";
        } else {
            return "String";
        }
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "";
        }
        if (isDate(value)) {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            return f.format((Date) value);
        } else if (isBigDecimal(value)) {
            return new BigDecimal(value.toString()).toString();
        } else if (isNumber(value)) {
            return Long.valueOf(value.toString()).toString();
        } else {
            return value.toString();
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.framework.generator.excel.strategy
     *     .WorkbookGeneratorStrategy#generate(
     *         jp.co.opentone.bsol.framework.generator.excel.WorkbookGeneratorContext)
     */
    public byte[] generate(WorkbookGeneratorContext context) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("generate start");
        }
        checkState(context);

        Document doc;
        try {
            doc = createDocument();
        } catch (ParserConfigurationException e) {
            throw new GeneratorFailedException(e);
        }

        Element book = createWorkbook(context, doc);
        doc.appendChild(book);
        Element sheet = createWorksheet(context, doc);
        book.appendChild(sheet);

        Element table = createTable(context, doc);
        sheet.appendChild(table);

        Element header = createHeader(context, doc);
        if (header != null) {
            table.appendChild(header);
        }

        List<Element> rows = createRows(context, doc);
        for (Element row : rows) {
            table.appendChild(row);
        }
        return toByte(doc);
    }

    private byte[] toByte(Document doc) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer t;
        try {
            t = factory.newTransformer();
            t.setOutputProperty("indent", "yes");
            t.setOutputProperty("encoding", ENCODING);

            t.transform(new DOMSource(doc), new StreamResult(out));
            return out.toByteArray();
        } catch (TransformerConfigurationException e) {
            throw new GeneratorFailedException(e);
        } catch (TransformerException e) {
            throw new GeneratorFailedException(e);
        }
    }
}
