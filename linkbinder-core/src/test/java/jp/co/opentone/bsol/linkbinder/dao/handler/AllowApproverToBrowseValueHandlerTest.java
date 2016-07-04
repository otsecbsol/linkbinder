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
package jp.co.opentone.bsol.linkbinder.dao.handler;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;

/**
 * {@link AllowApproverToBrowseValueHandler}のテストケース.
 * @author opentone
 */
public class AllowApproverToBrowseValueHandlerTest {

    /**
     * テスト対象.
     */
    private AllowApproverToBrowseValueHandler handler;

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        handler = new AllowApproverToBrowseValueHandler();
    }

    /**
     * 正しい値が返されるか検証する.
     */
    @Test
    public void testGetValue() {
        assertArrayEquals(AllowApproverToBrowse.values(), handler.getValues());
    }

}
