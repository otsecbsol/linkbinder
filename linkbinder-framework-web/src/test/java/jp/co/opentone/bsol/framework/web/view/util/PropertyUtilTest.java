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

import org.junit.Test;

import junit.framework.Assert;

/**
 * PropertyUtilクラスのテストケース.
 * @author opentone
 */
public class PropertyUtilTest {

    /**
     * [正常系] 通常呼び出されるような引数を指定した場合.
     */
    @Test
    public void testCreatePropertyKeyForViewId01() {
        String prefix = "keyprefix";
        String viewId = "/admin/adminHome.xhtml";
        String exp = "keyprefix.admin.adminHome";
        Assert.assertEquals(exp, PropertyUtil.createPropertyKeyForViewId(prefix, viewId));
    }

    /**
     * [異常系] prefixがnullまたは空文字列.
     */
    @Test
    public void testCreatePropertyKeyForViewId02() {
        boolean result = false;
        try {
            PropertyUtil.createPropertyKeyForViewId(null, "/admin/admin.xhtml");
        } catch (IllegalArgumentException e) {
            result = true;
        }
        Assert.assertTrue(result);

        result = false;
        try {
            PropertyUtil.createPropertyKeyForViewId("", "/admin/admin.xhtml");
        } catch (IllegalArgumentException e) {
            result = true;
        }
        Assert.assertTrue(result);
    }

    /**
     * [異常系] viewIdがnullまたは空文字列.
     */
    @Test
    public void testCreatePropertyKeyForViewId03() {
        boolean result = false;
        try {
            PropertyUtil.createPropertyKeyForViewId("prefix", null);
        } catch (IllegalArgumentException e) {
            result = true;
        }
        Assert.assertTrue(result);

        result = false;
        try {
            PropertyUtil.createPropertyKeyForViewId("prefix", "");
        } catch (IllegalArgumentException e) {
            result = true;
        }
        Assert.assertTrue(result);
    }
}
