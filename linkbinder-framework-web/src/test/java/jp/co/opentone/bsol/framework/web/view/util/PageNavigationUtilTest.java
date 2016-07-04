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

import java.util.HashMap;

import org.junit.Test;

import junit.framework.Assert;

/**
 * PageNavigationUtilのテストケース.
 * @author opentone
 */
public class PageNavigationUtilTest {

    /**
     * createRedirectUrlFromViewIdのテスト.
     * パラメータ無しの場合
     */
    @Test
    public void testCreateRedirectUrlFromViewId01() {
        String contextPath = "http://localhost/web";
        String viewId = "/admin/admin.xhtml";
        String enc = "UTF-8";
        String url = PageNavigationUtil
                        .createRedirectUrlFromViewId(contextPath, viewId, null, enc);
        Assert.assertEquals("http://localhost/web/admin/admin.jsf", url);
    }

    /**
     * createRedirectUrlFromViewIdのテスト.
     * パラメータ有りの場合
     */
    @Test
    public void testCreateRedirectUrlFromViewId02() {
        // パラメータ無し
        String contextPath = "http://localhost/web";
        String viewId = "/admin/admin.xhtml";
        String enc = "UTF-8";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key1", "value1");
        map.put("key3", "値3");
        String url = PageNavigationUtil
                        .createRedirectUrlFromViewId(contextPath, viewId, map, enc);
        Assert.assertEquals(
            "http://localhost/web/admin/admin.jsf?key1=value1&key3=%E5%80%A43", url);

        enc = "Shift_JIS";
        url = PageNavigationUtil.createRedirectUrlFromViewId(contextPath, viewId, map, enc);
        Assert.assertEquals(
                "http://localhost/web/admin/admin.jsf?key1=value1&key3=%92%6C3", url);
    }

}
