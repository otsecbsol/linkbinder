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
package jp.co.opentone.bsol.framework.web.view.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.model.SelectItem;

import org.junit.Test;

import jp.co.opentone.bsol.framework.core.dto.StringCode;

/**
 * {@link ViewHelper}のテストケース.
 *
 * @author opentone
 */
public class ViewHelperTest {

    /** テスト対象. */
    @Resource
    private ViewHelper helper;

    /**
     * {@link ViewHelper#createSelectStringItem(StringCode[] codes)}のテストケース.
     *
     * [テスト対象]
     * createSelectStringItem（正常）
     *
     * [テスト目的]
     * 画面セレクトボックス用の値が設定されることを確認する.
     *
     * [合格条件]
     * 取得値が想定値と全て一致している場合合格.
     *
     */
    @Test
    public void testCreateSelectStringItemStringCodeArray1() {

        helper = new ViewHelper();
        List<SelectItem> list = helper.createSelectStringItem(StringCodeMock.values());

        assertEquals(SelectItem.class, list.get(0).getClass());
        assertEquals(2, list.size());

        assertEquals(list.get(0).getValue(), "test1");
        assertEquals(list.get(0).getLabel(), "unit test no1");

        assertEquals(SelectItem.class, list.get(1).getClass());
        assertEquals(list.get(1).getValue(), "test2");
        assertEquals(list.get(1).getLabel(), "unit test no2");

    }

    /**
     * {@link ViewHelper#createSelectStringItem(StringCode[] codes)}のテストケース.
     *
     * [テスト対象]
     * createSelectStringItem（正常）
     *
     * [テスト目的]
     * 引数がnullの場合、空のリストが返却されることを確認する.
     *
     * [合格条件]
     * 取得値のリストのサイズが0の場合合格.
     *
     */
    @Test
    public void testCreateSelectStringItemStringCodeArray2() {

        helper = new ViewHelper();
        StringCode[] codes = null;

        List<SelectItem> list = helper.createSelectStringItem(codes);

        assertEquals(0, list.size());

    }

    /**
     * {@link ViewHelper#createSelectStringItem(List<StringCode> codeList)}のテストケース.
     *
     * [テスト対象]
     * createSelectStringItem（正常）
     *
     * [テスト目的]
     * 画面セレクトボックス用の値が設定されることを確認する.
     *
     * [合格条件]
     * 取得値が想定値と全て一致している場合合格.
     *
     */
    @Test
    public void testCreateSelectStringItemListOfStringCode1() {

        helper = new ViewHelper();

        List<StringCode> codeList = new ArrayList<StringCode>();

        codeList.add(StringCodeMock.TEST1);
        codeList.add(StringCodeMock.TEST2);

        List<SelectItem> list = helper.createSelectStringItem(codeList);

        assertEquals(2, list.size());
        assertEquals(SelectItem.class, list.get(0).getClass());

        assertEquals(list.get(0).getValue(), "test1");
        assertEquals(list.get(0).getLabel(), "unit test no1");

        assertEquals(SelectItem.class, list.get(1).getClass());
        assertEquals(list.get(1).getValue(), "test2");
        assertEquals(list.get(1).getLabel(), "unit test no2");

    }

    /**
     * {@link ViewHelper#createSelectStringItem(List<StringCode> codeList)}のテストケース.
     *
     * [テスト対象]
     * createSelectStringItem（正常）
     *
     * [テスト目的]
     * 引数がnullの場合、空のリストが返却されることを確認する.
     *
     * [合格条件]
     * 取得値のリストのサイズが0の場合合格.
     *
     */
    @Test
    public void testCreateSelectStringItemListOfStringCode2() {

        helper = new ViewHelper();

        List<StringCode> codeList = null;
        List<SelectItem> list = helper.createSelectStringItem(codeList);

        assertEquals(0, list.size());

    }

    /**
     * UT用Enumクラス.
     * @author opentone
     */
    public enum StringCodeMock implements StringCode {

        /** Blanket Order. */
        TEST1("test1", "unit test no1"),
        /** Purchase Order. */
        TEST2("test2", "unit test no2");

        /** value. */
        private String value;
        /** label. */
        private String label;

        private StringCodeMock(String value, String label) {
            this.value = value;
            this.label = label;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getLabel() {
            return label;
        }
    }

}
